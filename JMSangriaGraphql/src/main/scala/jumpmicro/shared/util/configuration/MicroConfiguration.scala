package jumpmicro.shared.util.configuration

import jumpmicro.shared.model.MMicroConfig
import org.log4s._

import scala.concurrent.Promise
import scala.util.Success

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

class MicroConfiguration {
  private[this] val logger = getLogger

  private val configurationLoaded: Promise[Option[MMicroConfig]] = Promise[Option[MMicroConfig]]()

  def configuration: Promise[Option[MMicroConfig]] = {
    configurationLoaded
  }

  def setConfiguration(config: Option[MMicroConfig]) = {
    configurationLoaded.complete(Success(config))
  }
}
