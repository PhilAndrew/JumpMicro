package jumpmicro.shared.util.osgi

import akka.actor.ActorSystem
import org.apache.camel.core.osgi.OsgiDefaultCamelContext
import org.osgi.framework.{BundleActivator, BundleContext}
import scaldi.Injectable
import org.apache.camel.core.osgi.OsgiDefaultCamelContext
import org.osgi.framework._
import akka.actor.ActorSystem
import com.typesafe.scalalogging.Logger
import org.osgi.framework.BundleContext
import org.slf4j.LoggerFactory
import scaldi.Injectable

import scala.concurrent.ExecutionContext.Implicits.global
import domino._
import jumpmicro.jmscalajs.impl.configuration.GlobalModule
import jumpmicro.jmscalajs.impl.startup.StartupOsgi

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

class BundleActivatorBoilerplate extends DominoActivator {
  protected var camelContext: OsgiDefaultCamelContext = null
  protected var system: Option[ActorSystem] = None
}
