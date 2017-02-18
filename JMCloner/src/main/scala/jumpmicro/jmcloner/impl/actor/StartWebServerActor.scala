package jumpmicro.jmcloner.impl.actor

import akka.camel.{CamelMessage, Consumer}
import com.typesafe.scalalogging.Logger
import scaldi.Injectable
import org.neo4j.ogm.session.Neo4jSession
import org.neo4j.ogm.transaction.Transaction
import org.osgi.framework.ServiceReference
import org.slf4j.LoggerFactory
import jumpmicro.jmcloner.impl.configuration.GlobalModule
import jumpmicro.shared.util.osgi.OsgiGlobal

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent._
//import universe.microservice.shared.service.BrowserService
import jumpmicro.jmcloner.impl.configuration.GlobalModule._

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

import acyclic.skipped

class StartWebServerActor extends Consumer with Injectable {
  val logger = Logger(classOf[StartWebServerActor])
  val osgi = inject[OsgiGlobal]

  override def endpointUri: String = "direct:startWebServer"
  var webServer: WebServer = null

  override def receive: Receive = {
    case msg: CamelMessage => {
      implicit val system = context.system
      webServer = new WebServer(osgi.bundleContext)
      webServer.start()
    }
  }
}

