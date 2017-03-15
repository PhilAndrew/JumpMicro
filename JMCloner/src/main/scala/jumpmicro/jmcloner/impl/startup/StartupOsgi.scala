package jumpmicro.jmcloner.impl.startup


import akka.camel.CamelExtension
import com.typesafe.config.{Config, ConfigFactory}
import jumpmicro.shared.util.akkaosgi.{AkkaCamelContextProvider, MyOsgiActorSystemFactory}
import jumpmicro.shared.util.boilerplate.StartupOsgiBoilerplate
import jumpmicro.shared.util.configuration.MicroConfiguration
import org.apache.camel.core.osgi.OsgiDefaultCamelContext
import org.log4s.getLogger
import org.osgi.framework.BundleContext
import jumpmicro.shared.util.global.CommonGlobalModule._

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

class StartupOsgi extends StartupOsgiBoilerplate {
  private[this] val logger = getLogger

  val startupAkkaActors = inject [StartupAkkaActors]
  val startupCamelComponents = inject [StartupCamelComponents]
  val startupCamelRoutes = inject [StartupCamelRoutes]

  def getActorSystemConfiguration(context: BundleContext): Config = ConfigFactory.empty
  def getActorSystemName(context: BundleContext): String = "ActorSystem"

  override def startupOverride(config: MicroConfiguration, bundleContext: BundleContext, camelContext: OsgiDefaultCamelContext): Unit = {
    startupCamelComponents.startup(camelContext)
    AkkaCamelContextProvider.contextProvider = camelContext

    val sysConfig: Config = getActorSystemConfiguration(bundleContext)
    val actorFactory = MyOsgiActorSystemFactory(bundleContext, sysConfig)
    val system = Some(actorFactory.createActorSystem(Option(getActorSystemName(bundleContext))))
    //system foreach (addLogServiceListener(context, _))
    //system foreach (configure(bundleContext, _))
    val camel = CamelExtension(system.get)
    //val producerTemplate = camel.template
    if (! camelContext.isStarted) camelContext.start()
    startupCamelRoutes.addCamelRoutes(camelContext)
    startupAkkaActors.addActors(config, system.get, camel, camelContext)
  }
}
