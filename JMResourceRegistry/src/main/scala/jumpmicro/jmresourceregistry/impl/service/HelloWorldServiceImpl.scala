package jumpmicro.jmresourceregistry.impl.service

import org.log4s._
import jumpmicro.jmresourceregistry.JMResourceRegistryService

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

class HelloWorldServiceImpl extends JMResourceRegistryService {
  private[this] val logger = getLogger

  def hello(): Unit = {
    logger.info("hello world!")
  }

  def startup(): Unit = {
    logger.info("startup method in HelloWorldServiceImpl")
  }
}
