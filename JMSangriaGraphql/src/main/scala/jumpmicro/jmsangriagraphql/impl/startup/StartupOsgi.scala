package jumpmicro.jmsangriagraphql.impl.startup

import akka.camel.CamelExtension
import com.typesafe.config.{Config, ConfigFactory}
import jumpmicro.shared.util.boilerplate.StartupOsgiBoilerplate
import org.apache.camel.core.osgi.OsgiDefaultCamelContext
import org.log4s.getLogger
import org.osgi.framework.BundleContext

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

object StartupOsgi extends StartupOsgiBoilerplate {
  private[this] val logger = getLogger

  def getActorSystemConfiguration(context: BundleContext): Config = ConfigFactory.empty
  def getActorSystemName(context: BundleContext): String = "ActorSystem"
}
