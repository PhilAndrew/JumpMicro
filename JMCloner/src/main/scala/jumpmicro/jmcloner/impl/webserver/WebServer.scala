package jumpmicro.jmcloner.impl.webserver

import jumpmicro.jmcloner.impl.webserver.State.Todo
import korolev._
import korolev.blazeServer._
import korolev.execution._
import korolev.server._
import better.files._
import java.io.{File => JFile}

import scala.concurrent.Future
import scala.io.Codec

object WebServer {

  private def copyFolder(fromPath: File, toPath: File) = {
    fromPath.copyTo(toPath)
  }

  def replaceStringInFile(file: File, find: String, replace: String) = {
    val lines: Seq[String] = (for (line <- file.lines(Codec.UTF8)) yield {
      line.replace(find, replace)
    }).toSeq
    file.delete()
    file.createIfNotExists().appendLines(lines: _*)(File.OpenOptions.append, Codec.UTF8)
  }

  def renameFolder(from: File, to: String) = {
    from.renameTo(to)
  }

  def replaceStringInFiles(inPath: File, from: String, to: String) = {
    for (file <- inPath.listRecursively.filter(f => f.extension == Some(".idr") || f.extension == Some(".scala") || f.extension == Some(".java"))) {
      replaceStringInFile(file, from, to)
    }
  }

  def renameFiles(inPath: File, from: String, to: String) = {
    for (file <- inPath.listRecursively.filter(f => f.extension == Some(".idr") || f.extension == Some(".scala") || f.extension == Some(".java"))) {
      if (file.name.contains(from)) {
        val newName = file.name.replace(from, to)
        file.renameTo(newName)
      }
    }
  }

  private def cloneMicroService2(from: String, to: String) = {
    val fromPath = File(".." + / + from)
    val toPath = File(".." + / + to)

    copyFolder(fromPath, toPath)

    val fromReplace = s"""val projectName = "$from""""
    val toReplace = s"""val projectName = "$to""""
    replaceStringInFile(File(toPath.pathAsString + / + "build.sbt"), fromReplace, toReplace)

    val fromLower = from.toLowerCase
    val toLower = to.toLowerCase

    replaceStringInFiles(toPath, "jumpmicro." + fromLower, "jumpmicro." + toLower)
    replaceStringInFiles(toPath, from, to)

    renameFiles(toPath, from, to)

    renameFolder(File(toPath.pathAsString + / + "src" + / + "main" + / + "scala" + / + "jumpmicro" + / + fromLower), toLower)
    renameFolder(File(toPath.pathAsString + / + "src" + / + "main" + / + "idris" + / + "jumpmicro" + / + fromLower), toLower)
  }

  def cloneMicroService(state: State, to: String): Future[_] = Future {
    state.todos.find(_.done).headOption.foreach(todo => {
      val from = todo.text
      cloneMicroService2(from, to)
    })
  }
}

class WebServer extends KorolevBlazeServer {

  import State.effects._
  object MyStorage {

    def futureInitialState = Future {
      val dirs = new java.io.File(".." + java.io.File.separator).listFiles().toSeq.filter(_.isDirectory).filter(_.getName.startsWith("JM")).toSeq
      State(
        todos = dirs.map(f => Todo(f.getName, done = false)).toVector
      )
    }

    def getStateByDeviceId(deviceId: StateStorage.DeviceId): Future[State] = futureInitialState
  }

  val storage = StateStorage.forDeviceId[Future, State] { deviceId =>
    MyStorage.getStateByDeviceId(deviceId)
  }
  val inputId = elementId

  val service = blazeService[Future, State, Any] from KorolevServiceConfig [Future, State, Any] (
    stateStorage = storage,
    head = 'head(
      'meta('charset /= "utf-8"),
      'meta('name /= "viewport", 'content /= "width=device-width, initial-scale=1, shrink-to-fit=no"),
      'link('rel /= "stylesheet", 'type /= "text/css", 'href /= "tether/dist/css/tether.css"),
      'link('rel /= "stylesheet", 'type /= "text/css", 'href /= "bootstrap/css/bootstrap.min.css"),
      'link('rel /= "stylesheet", 'type /= "text/css", 'href /= "style.css"),
      'script('src /= "jquery/jquery-3.1.1.js"),
      'script('src /= "tether/dist/js/tether.js"),
      'script('src /= "bootstrap/js/bootstrap.min.js")),
    render = {
      case state =>
        'body(
          'div('class /= "container",
            'div('class /= "header clearfix",
              'nav(
                'ul('class /= "nav nav-pills float-right",
                  'li('class /= "nav-item",
                    'a('class /= "nav-link active", 'href /= "#", "Home"))
                )
              ), 'h3('class /= "text-muted", "JMCloner")),
            'div('class /= "jumbotron",
              'h3('class /= "", "JumpMicro Cloner"),
              'div('class /= "todos",
                (state.todos zipWithIndex) map {
                  case (todo, i) =>
                    'div(
                      'div(
                        'class /= {
                          if (!todo.done) "checkbox"
                          else "checkbox checkbox__checked"
                        }
                      ),
                      // Generate transition when clicking checkboxes
                      event('click) {
                        immediateTransition { case s =>
                          val todos = s.todos
                          val updated = for (t <- todos.zipWithIndex) yield {
                            if (t._2 == i) t._1.copy(done = true) else t._1.copy(done = false)
                          }
                          s.copy(todos = updated)
                        }
                      },
                     'span(todo.text) //  if (!todo.done)
                      //else 'strike(todo.text)
                    )
                }
              ),
              'form(
                // Generate AddTodo action when 'Add' button clicked
                eventWithAccess('submit) { access =>
                  deferredTransition {
                    access.property[String](inputId, 'value) flatMap { value =>
                      WebServer.cloneMicroService(state, value).flatMap{ _ => {
                        MyStorage.futureInitialState
                      }}.flatMap(_ => {
                        Future { transition { case s => {
                          State(Vector())
                        }
                        } }
                      })
                    }
                  }
                },
                'input(
                  inputId,
                  'type /= "text",
                  'placeholder /= "New MicroService Name"
                ),
                'button("Clone MicroService")
              )
            )
          )
        )


    }
,
    serverRouter = {
      ServerRouter(
        dynamic = (_, _) => Router(
          fromState = {
            case State(_) =>
              Root
          },
          toState = {
            case (s, Root) =>
              //val u = s.copy(selectedTab = s.todos.keys.head)
              Future.successful(s)
            /*case (s, Root / name) =>
              val key = s.todos.keys.find(_.toLowerCase == name)
              Future.successful(key.fold(s)(k => s.copy(selectedTab = k)))*/
          }
        ),
        static = (deviceId) => Router(
          toState = {
            case (_, Root) =>
              storage.initial(deviceId)
            case (_, Root / name) =>
              storage.initial(deviceId) map { s =>
                //val key = s.todos.keys.find(_.toLowerCase == name)
                //key.fold(s)(k => s.copy(selectedTab = k))
                s
              }
          }
        )
      )
    }
  )
}

case class State(todos: Vector[State.Todo] = State.Todo(5))

object State {
  val effects = Effects[Future, State, Any]
  case class Todo(text: String, done: Boolean)
  object Todo {
    def apply(n: Int): Vector[Todo] = (0 to n).toVector map {
      i => Todo(s"This is TODO #$i", done = false)
    }
  }
}
