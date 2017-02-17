package jumpmicro.jmplayscalajs.impl.message

import com.typesafe.scalalogging.Logger

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

trait MicroServiceMessages

object MicroServiceMessages {
  val logger = Logger(classOf[MicroServiceMessages])

  case class CreateDemo()
}
