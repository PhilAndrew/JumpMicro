package jumpmicro.jmcloner.impl.actor

import korolev._
import korolev.server._
import korolev.blazeServer._

import scala.concurrent.Future

/**
  * @author Aleksey Fomkin <aleksey.fomkin@gmail.com>
  */
object WebServer extends KorolevBlazeServer {

  import State.effects._

  val storage = StateStorage.default[Future, State](State())
  val inputId = elementId

  val service = blazeService[Future, State, Any] from KorolevServiceConfig [Future, State, Any] (
    stateStorage = storage,
    head = 'head(
      'meta('charset /= "utf-8"),
      'meta('name /= "viewport", 'content /= "width=device-width, initial-scale=1, shrink-to-fit=no"),
      'link('rel /= "stylesheet", 'type /= "text/css", 'href /= "tether-1.3.3/dist/css/tether.css"),
      'link('rel /= "stylesheet", 'type /= "text/css", 'href /= "bootstrap-4.0.0-alpha.6-dist/css/bootstrap.css"),
      'script('src /= "jquery/jquery-3.1.1.js"),
      'script('src /= "tether-1.3.3/dist/js/tether.js"),
      'script('src /= "bootstrap-4.0.0-alpha.6-dist/js/bootstrap.js")),
    render = {
      case state =>

        /*
          <div class="container">
          <div class="header clearfix">
            <nav>
              <ul class="nav nav-pills float-right">
                <li class="nav-item">
                  <a class="nav-link active" href="#">Home <span class="sr-only">(current)</span></a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" href="#">About</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" href="#">Contact</a>
                </li>
              </ul>
            </nav>
            <h3 class="text-muted">Project name</h3>
          </div>
         */
        'body(
          'div('class /= "container",
            'div('class /= "header clearfix",
            'nav(), 'h3('class /= "text-muted", "Project name")),

            /*
                      <div class="jumbotron">
                        <h1 class="display-3">Jumbotron heading</h1>
                        <p class="lead">Cras justo odio, dapibus ac facilisis in, egestas eget quam. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus.</p>
                        <p><a class="btn btn-lg btn-success" href="#" role="button">Sign up today</a></p>
                      </div>
             */
            'div('class /= "jumbotron",
              'h1('class /= "display-3", "Jumbotron heading"),
              'p('class /= "lead", "Cras justo odio, dapibus ac facilisis in, egestas eget quam. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus."),
              'p('a('class /= "btn btn-lg btn-success", 'href /= "#", 'role /= "button", "Sign up today"))),

            /*
            <div class="row marketing">
              <div class="col-lg-6">
                <h4>Subheading</h4>
                <p>Donec id elit non mi porta gravida at eget metus. Maecenas faucibus mollis interdum.</p>

                <h4>Subheading</h4>
                <p>Morbi leo risus, porta ac consectetur ac, vestibulum at eros. Cras mattis consectetur purus sit amet fermentum.</p>

                <h4>Subheading</h4>
                <p>Maecenas sed diam eget risus varius blandit sit amet non magna.</p>
              </div>

              <div class="col-lg-6">
                <h4>Subheading</h4>
                <p>Donec id elit non mi porta gravida at eget metus. Maecenas faucibus mollis interdum.</p>

                <h4>Subheading</h4>
                <p>Morbi leo risus, porta ac consectetur ac, vestibulum at eros. Cras mattis consectetur purus sit amet fermentum.</p>

                <h4>Subheading</h4>
                <p>Maecenas sed diam eget risus varius blandit sit amet non magna.</p>
              </div>
            </div>
             */

            'div('class /= "row marketing",
              'div('class /= "col-lg-6",
                'h4("Sub heading"),
                'p("Hello")),
              'div('class /= "col-lg-6",
                'h4("Sub heading"),
                'p("Hello")))
          )
        )


/*          <footer class="footer">
            <p>&copy; Company 2017</p>
          </footer>*/

/*
        'body(
          'div("Super TODO tracker"),
          'div(
            state.todos.keys map { name =>
              'span(
                event('click) {
                  immediateTransition { case s =>
                    s.copy(selectedTab = name)
                  }
                },
                'style /= "margin-left: 10px",
                if (name == state.selectedTab) 'strong(name)
                else name
              )
            }
          ),
          'div('class /= "todos",
            (state.todos(state.selectedTab) zipWithIndex) map {
              case (todo, i) =>
                'div(
                  'div(
                    'class /= {
                      if (!todo.done) "checkbox"
                      else "checkbox checkbox__checked"
                    },
                    // Generate transition when clicking checkboxes
                    event('click) {
                      immediateTransition { case s =>
                        val todos = s.todos(s.selectedTab)
                        val updated = todos.updated(i, todos(i).copy(done = !todo.done))
                        s.copy(todos = s.todos + (s.selectedTab -> updated))
                      }
                    }
                  ),
                  if (!todo.done) 'span(todo.text)
                  else 'strike(todo.text)
                )
            }
          ),
          'form(
            // Generate AddTodo action when 'Add' button clicked
            eventWithAccess('submit) { access =>
              deferredTransition {
                access.property[String](inputId, 'value) map { value =>
                  val todo = State.Todo(value, done = false)
                  transition { case s =>
                    s.copy(todos = s.todos + (s.selectedTab -> (s.todos(s.selectedTab) :+ todo)))
                  }
                }
              }
            },
            'input(
              inputId,
              'type /= "text",
              'placeholder /= "What should be done?"
            ),
            'button("Add todo")
          )
        )*/
    },
    serverRouter = {
      ServerRouter(
        dynamic = (_, _) => Router(
          fromState = {
            case State(tab, _) =>
              Root / tab.toLowerCase
          },
          toState = {
            case (s, Root) =>
              val u = s.copy(selectedTab = s.todos.keys.head)
              Future.successful(u)
            case (s, Root / name) =>
              val key = s.todos.keys.find(_.toLowerCase == name)
              Future.successful(key.fold(s)(k => s.copy(selectedTab = k)))
          }
        ),
        static = (deviceId) => Router(
          toState = {
            case (_, Root) =>
              storage.initial(deviceId)
            case (_, Root / name) =>
              storage.initial(deviceId) map { s =>
                val key = s.todos.keys.find(_.toLowerCase == name)
                key.fold(s)(k => s.copy(selectedTab = k))
              }
          }
        )
      )
    }
  )
}

case class State(
                  selectedTab: String = "Tab1",
                  todos: Map[String, Vector[State.Todo]] = Map(
                    "Tab1" -> State.Todo(5),
                    "Tab2" -> State.Todo(7),
                    "Tab3" -> State.Todo(2)
                  )
                )

object State {
  val effects = Effects[Future, State, Any]
  case class Todo(text: String, done: Boolean)
  object Todo {
    def apply(n: Int): Vector[Todo] = (0 to n).toVector map {
      i => Todo(s"This is TODO #$i", done = false)
    }
  }
}
