package universe.microservice.jmscalajs.impl.startup

import akka.camel.CamelExtension
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.camel.core.osgi.OsgiDefaultCamelContext
import org.osgi.framework.BundleContext
import universe.microservice.shared.util.akkaosgi.{AkkaCamelContextProvider, MyOsgiActorSystemFactory}
import universe.microservice.shared.util.boilerplate.StartupOsgiBoilerplate

//: ----------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: ----------------------------------------------------------------------------------

object StartupOsgi extends StartupOsgiBoilerplate {
  def getActorSystemConfiguration(context: BundleContext): Config = ConfigFactory.empty
  def getActorSystemName(context: BundleContext): String = "ActorSystem"
}
