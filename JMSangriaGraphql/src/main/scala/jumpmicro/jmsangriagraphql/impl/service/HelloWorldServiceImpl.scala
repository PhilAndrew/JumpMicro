package jumpmicro.jmsangriagraphql.impl.service

import org.log4s._
import org.slf4j.LoggerFactory
import jumpmicro.jmsangriagraphql.JMSangriaGraphqlService

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

class HelloWorldServiceImpl extends JMSangriaGraphqlService {
  private[this] val logger = getLogger

  def hello(): Unit = {
    logger.info("hello world!")
  }

  def startup(): Unit = {
    logger.info("startup method in HelloWorldServiceImpl")
  }
}
