package jumpmicro.jmcloner.impl.actor

import java.nio.file.Paths
import java.util.concurrent.{Executors, TimeUnit}

import akka.actor.{ActorRef, ActorSystem}
import jumpmicro.jmcloner.impl.configuration.GlobalModule._
import jumpmicro.shared.util.global.CommonGlobalModule._
import akka.camel.{CamelMessage, Consumer}
import org.log4s._
import scaldi.Injectable
import org.neo4j.ogm.session.Neo4jSession
import org.neo4j.ogm.transaction.Transaction
import org.osgi.framework.ServiceReference
import org.slf4j.LoggerFactory
import jumpmicro.jmcloner.impl.configuration.GlobalModule
import jumpmicro.jmcloner.impl.idris.TestIdris.getClass
import jumpmicro.jmcloner.impl.webserver.WebServer
import jumpmicro.shared.util.akkaosgi.MyBundleDelegatingClassLoader
import jumpmicro.shared.util.osgi.{OsgiGlobal}
import org.http4s.blaze.channel.ServerChannel

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent._
import scala.concurrent.duration.Duration
import scala.reflect.io.File
//import universe.microservice.shared.service.BrowserService
import jumpmicro.jmcloner.impl.configuration.GlobalModule._

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------
import java.nio.file.StandardWatchEventKinds._

import acyclic.skipped

class StartWebServerActor extends Consumer with Injectable {
  private[this] val logger = getLogger
  val osgi = inject[OsgiGlobal]
  val osgiGlobal: OsgiGlobal = inject[OsgiGlobal]

  var webServer: WebServer = null

  override def endpointUri: String = "direct:startWebServer"

  override def receive: Receive = {
    case msg: CamelMessage => {
      implicit val system = context.system

      webServer = new WebServer()
      webServer.start()

      logger.info("HTTP Server for korolev started at http://localhost:8181/")
    }
 }

}

