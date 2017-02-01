package universe.microservice.jmscalajs.impl.actor

import akka.camel.{CamelMessage, Consumer}
import com.typesafe.scalalogging.Logger
import scaldi.Injectable
import universe.microservice.microservicescalajs.impl.message.MicroServiceMessages.CreateDemo
import universe.microservice.shared.util.osgi.OsgiGlobal
import universe.microservice.microservicescalajs.impl.configuration.GlobalModule._
import universe.microservice.shared.bean.UIRectangle
import org.neo4j.ogm.session.Neo4jSession
import org.neo4j.ogm.transaction.Transaction
import org.osgi.framework.ServiceReference
import org.slf4j.LoggerFactory
import universe.microservice.jmscalajs.impl.configuration.GlobalModule

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent._
import universe.microservice.shared.util.neo4j.Neo4JSessionFactory
import universe.microservice.shared.model.{ActorInMovie, Movie}
//import universe.microservice.shared.service.BrowserService

//: ----------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: ----------------------------------------------------------------------------------

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

