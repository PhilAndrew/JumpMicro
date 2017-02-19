package jumpmicro.shared.util.boilerplate

import akka.camel.CamelExtension
import com.typesafe.config.Config
import com.typesafe.scalalogging.Logger
import org.apache.camel.core.osgi.OsgiDefaultCamelContext
import org.osgi.framework.BundleContext
import org.slf4j.LoggerFactory
import scaldi.Injectable
import jumpmicro.jmcloner.impl.configuration.{GlobalModule, MicroConfiguration}
import jumpmicro.jmcloner.impl.configuration.GlobalModule._
import jumpmicro.jmcloner.impl.startup.{StartupAkkaActors, StartupCamelComponents, StartupCamelRoutes}
import jumpmicro.shared.util.akkaosgi.{AkkaCamelContextProvider, MyOsgiActorSystemFactory}
import jumpmicro.shared.util.neo4j.Neo4JSessionFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

abstract class StartupOsgiBoilerplate extends Injectable {
  val logger = Logger(classOf[StartupOsgiBoilerplate])

  val startupAkkaActors = inject [StartupAkkaActors]
  val startupCamelComponents = inject [StartupCamelComponents]
  val startupCamelRoutes = inject [StartupCamelRoutes]
  val microConfiguration = inject [MicroConfiguration]

  def getActorSystemConfiguration(context: BundleContext): Config
  def getActorSystemName(context: BundleContext): String

  def loadNeo4JConfig() = {
    //Future {
    val session2 = Neo4JSessionFactory.getNeo4jSession()
      //val tx2 = session2.beginTransaction()
    val config = GlobalModule.loadConfigFromNeo4JBlocking(session2, inject[String](identified by "jumpmicro.nodeid"))
      //tx2.commit()
    microConfiguration.setConfiguration(config)
    //}
  }

  def startup(config: MicroConfiguration, bundleContext: BundleContext, camelContext: OsgiDefaultCamelContext) = {
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
    //camelContext.start()
  }

}
