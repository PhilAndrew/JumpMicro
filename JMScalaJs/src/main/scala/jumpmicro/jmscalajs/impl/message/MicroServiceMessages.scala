package universe.microservice.jmscalajs.impl.message

import com.typesafe.scalalogging.Logger

//: ----------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: ----------------------------------------------------------------------------------

trait MicroServiceMessages

object MicroServiceMessages {
  val logger = Logger(classOf[MicroServiceMessages])

  case class CreateDemo()
}
