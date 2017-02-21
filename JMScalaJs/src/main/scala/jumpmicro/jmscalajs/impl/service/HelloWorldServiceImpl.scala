package jumpmicro.jmscalajs.impl.service

//import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory
import jumpmicro.jmscalajs.JMScalaJsService
import org.log4s.getLogger

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

class HelloWorldServiceImpl extends JMScalaJsService {
  private[this] val logger = getLogger

  def hello(): Unit = {
    logger.info("hello world!")
  }

  def startup(): Unit = {
    logger.info("startup method in HelloWorldServiceImpl")
  }
}
