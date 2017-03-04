package jumpmicro.shared.util.boilerplate

import akka.camel.CamelExtension
import com.typesafe.config.Config
import org.log4s._
import org.apache.camel.core.osgi.OsgiDefaultCamelContext
import org.osgi.framework.BundleContext
import org.slf4j.LoggerFactory
import scaldi.Injectable
import jumpmicro.jmsangriagraphql.impl.configuration.{GlobalModule, MicroConfiguration}
import jumpmicro.jmsangriagraphql.impl.configuration.GlobalModule._
import jumpmicro.jmsangriagraphql.impl.startup.{StartupAkkaActors, StartupCamelComponents, StartupCamelRoutes}
import jumpmicro.shared.util.akkaosgi.{AkkaCamelContextProvider, MyOsgiActorSystemFactory}
import jumpmicro.shared.util.neo4j.Neo4JSessionFactory
import jumpmicro.shared.util.osgi.OsgiGlobal

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

abstract class StartupOsgiBoilerplate extends Injectable {
  private[this] val logger = getLogger

  val startupAkkaActors = inject [StartupAkkaActors]
  val startupCamelComponents = inject [StartupCamelComponents]
  val startupCamelRoutes = inject [StartupCamelRoutes]
  val microConfiguration = inject [MicroConfiguration]

  def getActorSystemConfiguration(context: BundleContext): Config
  def getActorSystemName(context: BundleContext): String

  def loadNeo4JConfig() = {
    //Future {
    val session2 = Neo4JSessionFactory.getNeo4jSession()
    if (session2!=null) {
      //val tx2 = session2.beginTransaction()
      val nodeId = Try {
        inject[String](identified by "jumpmicro.nodeid")
      }
      if (nodeId.isSuccess) {
        val config = GlobalModule.loadConfigFromNeo4JBlocking(session2, nodeId.getOrElse(""))
        //tx2.commit()
        microConfiguration.setConfiguration(config)
      } else {
        logger.error("The node identifier for this MicroService has not been set, please make jumpmicro.conf writable so this service can add a value, then restart this MicroService.")
      }
    }
    //}
  }

  def startup(config: MicroConfiguration, bundleContext: BundleContext, camelContext: OsgiDefaultCamelContext) = {

    val osgiGlobal: OsgiGlobal = inject[OsgiGlobal]
    osgiGlobal.bundleContext = bundleContext
    org.neo4j.ogm.Neo4JOGM.setBundleContext(bundleContext)
    osgiGlobal.camelContext = camelContext

    loadNeo4JConfig()
    camelContext.setTracing(true)
    startupCamelComponents.startup(camelContext)
    AkkaCamelContextProvider.contextProvider = camelContext
    val sysConfig: Config = getActorSystemConfiguration(bundleContext)
    val actorFactory = MyOsgiActorSystemFactory(bundleContext, sysConfig)
    val system = Some(actorFactory.createActorSystem(Option(getActorSystemName(bundleContext))))
    //system foreach (addLogServiceListener(context, _))
    //system foreach (configure(bundleContext, _))
    val camel = CamelExtension(system.get)
    //val producerTemplate = camel.template
    // Add routes and Actors
    // @todo Camel routes must be added first before this starts
    startupAkkaActors.addActors(config, system.get, camel, camelContext)
    startupCamelRoutes.addCamelRoutes(camelContext)
    camelContext.start()
  }

}
