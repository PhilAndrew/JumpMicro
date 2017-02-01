package jumpmicro.jmscalajs.impl.actor

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

import java.io.{File, IOException}
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file._

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.{Http, server}
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.Logger
import jumpmicro.shared.util.akkaosgi.MyBundleDelegatingClassLoader
import jumpmicro.shared.util.osgi.OsgiGlobal
import org.apache.camel.scala.dsl.builder.ScalaRouteBuilder
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.IOFileFilter
import org.osgi.framework.BundleContext
import scaldi.Injector

import scala.concurrent.Future
import scala.io.{Source, StdIn}
import scala.util.{Success, Try}
import scalatags.Text.all._
// import scalatags.JsDom.all._
import scaldi.Injectable

class WebServer(context: BundleContext)(implicit inj: Injector, system: ActorSystem) extends Injectable {
  val logger = Logger(classOf[WebServer])
  val osgi = inject[OsgiGlobal]

  private val classloader = MyBundleDelegatingClassLoader(context, Some(getClass.getClassLoader))

  def start() = {
    implicit val materializer = ActorMaterializer()

    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val route =
      path("js" / Remaining) { r => {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, {
          println(r)
          val src = "js/" + r
          val stream = classloader.getResourceAsStream(src)
          val lines = scala.io.Source.fromInputStream(stream).getLines.toList
          stream.close()

          lines.mkString("\n")
        }))
      }
      } ~
      path("test") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,
            html(
              scalatags.Text.all.head(
                script(src := "/js/scalajsproject-jsdeps.js"),
                script(src := "/js/scalajsproject-fastopt.js"),
                script("(new testclient.TutorialApp()).main();")
              ),
              body(
                h1("Use the chrome inspection and see the console")
              )
            ).toString()
          ))
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    logger.info("Server online at http://localhost:8080/")

  }
}