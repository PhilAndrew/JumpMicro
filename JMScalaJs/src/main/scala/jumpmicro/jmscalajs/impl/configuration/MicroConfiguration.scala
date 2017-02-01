package universe.microservice.jmscalajs.impl.configuration

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory
import universe.microservice.shared.model.MicroConfig

import scala.concurrent.{Future, Promise}
import scala.util.{Success, Try}

//: ----------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: ----------------------------------------------------------------------------------

class MicroConfiguration {
  val logger = Logger(classOf[MicroConfiguration])

  private val configurationLoaded: Promise[MicroConfig] = Promise[MicroConfig]()

  def configuration: Promise[MicroConfig] = {
    configurationLoaded
  }

  def setConfiguration(config: MicroConfig) = {
    configurationLoaded.complete(Success(config))
  }
}
