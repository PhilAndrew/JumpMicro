package jumpmicro.jmresourceregistry.impl.actor

import akka.camel.{CamelMessage, Consumer}
import org.log4s._
import scaldi.Injectable
import jumpmicro.shared.util.osgi.OsgiGlobal
import jumpmicro.shared.util.global.CommonGlobalModule._

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
      logger.info("####################################################         Receive in start web server")
    }
  }
}
