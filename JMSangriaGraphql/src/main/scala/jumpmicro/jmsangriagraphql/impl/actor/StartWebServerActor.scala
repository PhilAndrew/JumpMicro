package jumpmicro.jmsangriagraphql.impl.actor

import akka.camel.{CamelMessage, Consumer}
import org.log4s._
import scaldi.Injectable
import jumpmicro.jmsangriagraphql.impl.webserver.WebServer
import jumpmicro.shared.util.osgi.OsgiGlobal
import jumpmicro.jmsangriagraphql.impl.configuration.GlobalModule._

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

class StartWebServerActor extends Consumer with Injectable {
  private[this] val logger = getLogger
  val osgi = inject[OsgiGlobal]

  override def endpointUri: String = "direct:startWebServer"

  override def receive: Receive = {
    case msg: CamelMessage => {
      implicit val system = context.system
      //webServer = new WebServer(osgi.bundleContext)
      //webServer.start()

      WebServer.startWebServer()
      logger.info("HTTP Server for korolev started at http://localhost:8181/")
    }
  }
}
