package jumpmicro.shared.util.osgi

import org.apache.camel.core.osgi.OsgiDefaultCamelContext
import akka.actor.ActorSystem
import domino._

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

class BundleActivatorBoilerplate extends DominoActivator {
  protected var camelContext: OsgiDefaultCamelContext = null
  protected var system: Option[ActorSystem] = None

}
