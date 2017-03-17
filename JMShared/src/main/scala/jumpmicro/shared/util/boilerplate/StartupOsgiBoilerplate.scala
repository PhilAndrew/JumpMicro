package jumpmicro.shared.util.boilerplate

import akka.camel.CamelExtension
import com.typesafe.config.Config
import org.log4s._
import org.apache.camel.core.osgi.OsgiDefaultCamelContext
import org.osgi.framework.BundleContext
import scaldi.Injectable
import jumpmicro.shared.util.akkaosgi.{AkkaCamelContextProvider, MyOsgiActorSystemFactory}
import jumpmicro.shared.util.configuration.MicroConfiguration
import jumpmicro.shared.util.global.CommonGlobalModule
import jumpmicro.shared.util.neo4j.Neo4JSessionFactory
import jumpmicro.shared.util.osgi.OsgiGlobal
import jumpmicro.shared.util.global.CommonGlobalModule._

import scala.util.Try

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

abstract class StartupOsgiBoilerplate extends Injectable {
  private[this] val logger = getLogger

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
        val config = CommonGlobalModule.loadConfigFromNeo4JBlocking(session2, nodeId.getOrElse(""))
        microConfiguration.setConfiguration(Some(config))
      } else {
        logger.error("The node identifier for this MicroService has not been set, please make jumpmicro.conf writable so this service can add a value, then restart this MicroService.")
      }
    } else microConfiguration.setConfiguration(None)
    //}
  }

  def startupOverride(config: MicroConfiguration, bundleContext: BundleContext, camelContext: OsgiDefaultCamelContext)

  def startup(config: MicroConfiguration, bundleContext: BundleContext, camelContext: OsgiDefaultCamelContext) = {

    val osgiGlobal: OsgiGlobal = inject[OsgiGlobal]
    osgiGlobal.bundleContext = bundleContext
    org.neo4j.ogm.Neo4JOGM.setBundleContext(bundleContext)
    osgiGlobal.camelContext = camelContext

    loadNeo4JConfig()
    camelContext.setTracing(true)

    startupOverride(config, bundleContext, camelContext)
  }

}
