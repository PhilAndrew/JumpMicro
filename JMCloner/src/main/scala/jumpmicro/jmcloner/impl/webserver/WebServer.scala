package jumpmicro.jmcloner.impl.webserver

import jumpmicro.jmcloner.impl.webserver.State.JumpMicroProject
import korolev._
import korolev.blazeServer._
import korolev.execution._
import korolev.server._
import better.files._
import java.io.{File => JFile}

import scala.concurrent.Future
import scala.io.Codec
import scala.concurrent.blocking

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

  def filterSource(f: File): Boolean = {
    f.extension == Some(".idr") || f.extension == Some(".scala") || f.extension == Some(".java")
  }

  def replaceStringInFiles(inPath: File, from: String, to: String) = {
    for (file <- inPath.listRecursively.filter(filterSource)) {
      replaceStringInFile(file, from, to)
    }
  }

  def renameFiles(inPath: File, from: String, to: String) = {
    for (file <- inPath.listRecursively.filter(filterSource)) {
      if (file.name.contains(from)) {
        val newName = file.name.replace(from, to)
        file.renameTo(newName)
      }
    }
  }

  def cleanProject(toPath: File) = {
    val targetFolder = File(toPath.pathAsString + / + "target")
    targetFolder.delete(true)
    try { targetFolder.createDirectory() } catch { case ex: java.nio.file.FileAlreadyExistsException => { } }
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

    cleanProject(toPath)
  }

  def cloneMicroService(state: State, to: String): Future[_] = Future {
    state.projects.find(_.isSelected).headOption.foreach(todo => {
      val from = todo.text
      cloneMicroService2(from, to)
    })
  }
}

class WebServer extends KorolevBlazeServer {

  import State.effects._
  object MyStorage {

    def initialState = {
      val dirs = new java.io.File(".." + java.io.File.separator).listFiles().toSeq.filter(_.isDirectory).filter(_.getName.startsWith("JM")).toSeq
      State(
        projects = dirs.map(f => JumpMicroProject(f.getName, isSelected = false)).toVector
      )
    }

    def futureInitialState = Future { blocking { initialState } }

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
                if (state.inProgress) {
                  "Please wait, project being copied ..."
                } else
                (state.projects zipWithIndex) map {
                  case (todo, i) =>
                    'div(
                      'div(
                        'class /= {
                          if (!todo.isSelected) "checkbox"
                          else "checkbox checkbox__checked"
                        }
                      ),
                      // Generate transition when clicking checkboxes
                      event('click) {
                        immediateTransition { case s =>
                          val todos = s.projects
                          val updated = for (t <- todos.zipWithIndex) yield {
                            if (t._2 == i) t._1.copy(isSelected = true) else t._1.copy(isSelected = false)
                          }
                          s.copy(projects = updated)
                        }
                      },
                     'span(todo.text)
                    )
                }
              ),
              'form(
                // Generate AddTodo action when 'Add' button clicked
                eventWithAccess('submit) { access =>
                  immediateTransition { case s => {
                    // @todo Need to clear out the form, see https://github.com/fomkin/korolev/issues/98
                    // @todo Need form validation
                    s.copy(projects = Vector(), cloneButtonEnabled = false, inProgress = true)
                  }
                  }.deferredTransition {
                    access.property[String](inputId, 'value) flatMap { value =>
                      WebServer.cloneMicroService(state, value).flatMap{ _ => {
                        MyStorage.futureInitialState
                      }}.flatMap(_ => {
                        Future { transition { case s => {
                          blocking { MyStorage.initialState }
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
                'button(if (state.cloneButtonEnabled) None else Some('disabled /= "true"), "Clone MicroService")
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
            case State(_, _, _) =>
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

case class State(projects: Vector[State.JumpMicroProject] = State.JumpMicroProject(0),
                 cloneButtonEnabled: Boolean = true,
                 inProgress: Boolean = false)

object State {
  val effects = Effects[Future, State, Any]
  case class JumpMicroProject(text: String, isSelected: Boolean)
  object JumpMicroProject {
    def apply(n: Int): Vector[JumpMicroProject] = (0 to n).toVector map {
      i => JumpMicroProject(s"This is TODO #$i", isSelected = false)
    }
  }
}
