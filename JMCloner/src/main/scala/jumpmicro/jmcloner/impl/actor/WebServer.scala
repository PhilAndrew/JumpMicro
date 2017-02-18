package jumpmicro.jmcloner.impl.actor

//: ----------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: ----------------------------------------------------------------------------------

import korolev._
import korolev.server._
import korolev.blazeServer._

import scala.concurrent.Future


/**
  * @author Aleksey Fomkin <aleksey.fomkin@gmail.com>
  */
object WebServer extends KorolevBlazeServer {

  import State.effects._

  // Handler to input
  val inputId = elementId

/*
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/css/bootstrap.min.css" integrity="sha384-rwoIResjU2yc3z8GV/NPeZWAv56rSmLldC3R/AZzGRnGxQQKnKkoFVhFQhNUwEyJ" crossorigin="anonymous">
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/js/bootstrap.min.js" integrity="sha384-vBWWzlZJ8ea9aCX4pEW3rVHjgjt7zpkNpZk+02D9phzyeVkE+jo0ieGizqPLForn" crossorigin="anonymous"></script>
*/
  val service = blazeService[Future, State, Any] from KorolevServiceConfig [Future, State, Any] (
    head = 'head(
      'link('rel /= "stylesheet", 'href /= "tether-1.3.3/dist/css/tether.css"),
      'link('rel /= "stylesheet", 'href /= "bootstrap-4.0.0-alpha.6-dist/css/bootstrap.css"),
      'script('src /= "jquery/jquery-3.1.1.js"),
      'script('src /= "tether-1.3.3/dist/js/tether.js"),
      'script('src /= "bootstrap-4.0.0-alpha.6-dist/js/bootstrap.js")),
    serverRouter = ServerRouter.empty[Future, State],
    stateStorage = StateStorage.default(State()),
    render = {
      case state =>
        'body(
          'div("Super TODO tracker"),
          'div('style /= "height: 250px; overflow-y: scroll",
            (state.todos zipWithIndex) map {
              case (todo, i) =>
                'div(
                  'input(
                    'type /= "checkbox",
                    'checked when todo.done,
                    // Generate transition when clicking checkboxes
                    event('click) {
                      immediateTransition { case tState =>
                        val updated = tState.todos.updated(i, tState.todos(i).copy(done = !todo.done))
                        tState.copy(todos = updated)
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
                  transition { case tState =>
                    tState.copy(todos = tState.todos :+ todo)
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
        )
    }
  )
}

case class State(todos: Vector[State.Todo] = (0 to 2).toVector map {
  i => State.Todo(s"This is TODO #$i", done = false)
})

object State {
  val effects = Effects[Future, State, Any]
  case class Todo(text: String, done: Boolean)
}