package korolev

import java.io.{ByteArrayInputStream, InputStream}
import java.nio.charset.StandardCharsets
import java.util.UUID
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}

import bridge.JSAccess
import korolev.Async._
import korolev.Korolev.MutableMapFactory
import org.log4s._

import scala.collection.concurrent.TrieMap
import scala.collection.mutable
import scala.io.Source
import scala.language.higherKinds
import scala.util.{Failure, Random, Success, Try}

/**
  * @author Aleksey Fomkin <aleksey.fomkin@gmail.com>
  */
package object server {
  private[this] val logger = getLogger

  type MimeTypes = String => Option[String]

  private[server] class SessionDestroyedException(s: String) extends Exception(s)

  private[server] abstract class KorolevSession[F[+_]: Async] {
    def publish(message: String): F[Unit]
    def nextMessage: F[String]
    def destroy(): F[Unit]
    def resolveFormData(descriptor: String, formData: Try[FormData]): Unit
  }

  def korolevService[F[+_]: Async, S, M](
    mimeTypes: MimeTypes,
    config: KorolevServiceConfig[F, S, M]
  ): PartialFunction[Request, F[Response]] = {

    import misc._

    val sessions = TrieMap.empty[String, KorolevSession[F]]

    def renderStatic(request: Request): F[Response] = {
      val (_, deviceId) = deviceFromRequest(request)
      val sessionId = Random.alphanumeric.take(16).mkString
      val stateF = config.serverRouter
        .static(deviceId)
        .toState
        .lift(((), request.path))
        .getOrElse(config.stateStorage.initial(deviceId))

      val writeResultF = Async[F].flatMap(stateF)(config.stateStorage.write(deviceId, sessionId, _))

      Async[F].map(writeResultF) { state =>
        val body = config.render(state)
        val dom = 'html(
          config.head.copy(children =
            'script(bridgeJs) ::
              'script(
                s"var KorolevSessionId = '$sessionId';\n" +
                s"var KorolevServerRootPath = '${config.serverRouter.rootPath}';\n" +
                  korolevJs
              ) ::
            config.head.children),
          body
        )
        Response.Http(
          status = Response.Status.Ok,
          headers = Seq(
            "content-type" -> htmlContentType,
            "set-cookie" -> s"device=$deviceId"
          ),
          body = Some {
            val html = "<!DOCTYPE html>" + dom.html
            val bytes = html.getBytes(StandardCharsets.UTF_8)
            new ByteArrayInputStream(bytes)
          }
        )
      }
    }

    object matchStatic {

      /**
        * @return (InputStream with resource, ContentType)
        */
      def unapply(req: Request): Option[(InputStream, String)] =
        req.path match {
          case Root => None
          case path @ _ / fileName =>
            val fsPath = s"/static${path.toString}"
            val stream = getClass.getResourceAsStream(fsPath)
            Option(stream) map { stream =>
              val fileExtension = fileName.lastIndexOf('.') match {
                case -1 => binaryContentType
                case index => fileName.substring(index + 1)
              }
              (stream, fileExtension)
            }
        }
    }

    def deviceFromRequest(request: Request): (Boolean, String) = {
      request.cookie("device") match {
        case None => true -> UUID.randomUUID().toString
        case Some(deviceId) => false -> deviceId
      }
    }

    def makeSessionKey(deviceId: String, sessionId: String): String =
      s"${deviceId}_$sessionId"

    def createSession(deviceId: String, sessionId: String): F[KorolevSession[F]] = {

      val sendingQueue = new ConcurrentLinkedQueue[String]()
      val subscriber = new AtomicReference(Option.empty[String => Unit])
      val jsAccess = {
        val addToQueue: String => Unit = { message =>
          sendingQueue.add(message)
        }
        JsonQueuedJsAccess { message =>
          val fOpt = subscriber.getAndSet(None)
          fOpt.fold(addToQueue)(identity)(message)
        }
      }

      // Session storage access
      Async[F].map(config.stateStorage.read(deviceId, sessionId)) { state =>

        // Create Korolev with dynamic router
        val dux = StateManager[F, S](state)
        val router = config.serverRouter.dynamic(deviceId, sessionId)
        val env = config.envConfigurator(deviceId, sessionId, dux.apply)
        val trieMapFactory = new MutableMapFactory {
          def apply[K, V]: mutable.Map[K, V] = TrieMap.empty[K, V]
        }
        val korolev = Korolev(
          dux, jsAccess, state, config.render, router, env.onMessage, fromScratch = false,
          createMutableMap = trieMapFactory
        )
        // Subscribe on state updates an push them to storage
        korolev.stateManager.subscribe(state => config.stateStorage.write(deviceId, sessionId, state))
        korolev.stateManager.onDestroy(env.onDestroy)

        new KorolevSession[F] {

          val aliveRef = new AtomicBoolean(true)
          val currentPromise = new AtomicReference(Option.empty[Async.Promise[F, String]])

          // Put the session to registry
          val sessionKey = makeSessionKey(deviceId, sessionId)
          sessions.put(sessionKey, this)

          def publish(message: String): F[Unit] = {
            Async[F].pure(jsAccess.receive(message))
          }

          def nextMessage: F[String] = {
            if (sendingQueue.isEmpty) {
              val promise = Async[F].promise[String]
              currentPromise.set(Some(promise))
              subscriber.set(Some(m => promise.complete(Success(m))))
              promise.future
            } else {
              val message = sendingQueue.poll()
              Async[F].pure(message)
            }
          }


          def resolveFormData(descriptor: String, formData: Try[FormData]): Unit = {
            korolev.resolveFormData(descriptor, formData)
          }

          def destroy(): F[Unit] = {
            if (aliveRef.getAndSet(false)) {
              currentPromise.get() foreach { promise =>
                promise.complete(Failure(new SessionDestroyedException("Session has been closed")))
              }
              korolev.stateManager.destroy()
              sessions.remove(sessionKey)
            }
            Async[F].unit
          }
        }
      }
    }

    val formDataCodec = new FormDataCodec(config.maxFormDataEntrySize)

    val service: PartialFunction[Request, F[Response]] = {
      case matchStatic(stream, fileExtensionOpt) =>
        val headers = mimeTypes(fileExtensionOpt).fold(Seq.empty[(String, String)]) {
          fileExtension =>
            Seq("content-type" -> fileExtension)
        }
        val response = Response.Http(Response.Status.Ok, Some(stream), headers)
        Async[F].pure(response)
      case Request(Root / "bridge" / deviceId / sessionId / "form-data" / descriptor, _, _, headers, body) =>
        sessions.get(makeSessionKey(deviceId, sessionId)) match {
          case Some(session) =>
            val boundaryOpt = headers collectFirst {
              case (k, v) if k.toLowerCase == "content-type" && v.contains("multipart/form-data") => v
            } flatMap {
              _.split(';')
                .toList
                .filter(_.contains('='))
                .map(_.split('=').map(_.trim))
                .collectFirst { case Array("boundary", s) => s }
            }
            boundaryOpt match {
              case None =>
                val error = "Content-Type should be `multipart/form-data`"
                val res = Response.Http(Response.Status.BadRequest, error)
                Async[F].pure(res)
              case Some(boundary) =>
                val formData = Try(formDataCodec.decode(body, boundary))
                session.resolveFormData(descriptor, formData)
                Async[F].pure(Response.Http(Response.Status.Ok, None))
            }
          case None => Async[F].pure(Response.Http(Response.Status.BadRequest, "Session isn't exist"))
        }
      case Request(Root / "bridge" / "long-polling" / deviceId / sessionId / "publish", _, _, _, body) =>
        sessions.get(makeSessionKey(deviceId, sessionId)) match {
          case Some(session) =>
            val message = new String(body.array(), StandardCharsets.UTF_8)
            session
              .publish(message)
              .map(_ => Response.Http(Response.Status.Ok))
          case None =>
            Async[F].pure(Response.Http(Response.Status.BadRequest, "Session isn't exist"))
        }
      case Request(Root / "bridge" / "long-polling" / deviceId / sessionId / "subscribe", _, _, _, _) =>
        val sessionAsync = sessions.get(makeSessionKey(deviceId, sessionId)) match {
          case Some(x) => Async[F].pure(x)
          case None => createSession(deviceId, sessionId)
        }
        sessionAsync.flatMap(_.nextMessage.map(Response.Http(Response.Status.Ok, _))) recover {
          case _: SessionDestroyedException =>
            Response.Http(Response.Status.Gone, "Session has been destroyed")
        }
      case Request(Root / "bridge" / "web-socket" / deviceId / sessionId, _, _, _, _) =>
        val sessionAsync = sessions.get(makeSessionKey(deviceId, sessionId)) match {
          case Some(x) => Async[F].pure(x)
          case None => createSession(deviceId, sessionId)
        }
        sessionAsync map { session =>
          Response.WebSocket(
            destroyHandler = () => session.destroy() run {
              case Success(_) => // do nothing
              case Failure(e) => logger.error(e)("An error occurred during destroying the session")
            },
            publish = message => session.publish(message) run {
              case Success(_) => // do nothing
              case Failure(e) => logger.error(e)("An error occurred during publishing message to session")
            },
            subscribe = { newSubscriber =>
              def aux(): Unit = session.nextMessage run {
                case Success(message) =>
                  newSubscriber(message)
                  aux()
                case Failure(e: SessionDestroyedException) => // Do nothing
                case Failure(e) =>
                  logger.error(e)("An error occurred during polling message from session")
              }
              aux()
            }
          )
        }
      case request => renderStatic(request)
    }

    service
  }

  private[server] object misc {
    val htmlContentType = "text/html"
    val binaryContentType = "application/octet-stream"
    val korolevJs = {
      val classLoader = classOf[EventPropagation].getClassLoader
      val stream = classLoader.getResourceAsStream("korolev.js")
      Source.fromInputStream(stream).mkString
    }
    val bridgeJs = {
      import scala.concurrent.Future
      val classLoader = classOf[JSAccess[Future]].getClassLoader
      val stream = classLoader.getResourceAsStream("bridge.js")
      Source.fromInputStream(stream).mkString
    }
  }
}
