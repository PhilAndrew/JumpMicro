package jumpmicro.jmcloner.impl.startup

//import com.typesafe.scalalogging.Logger
import org.apache.camel.core.osgi.OsgiDefaultCamelContext
import org.osgi.framework.BundleContext
import jumpmicro.jmcloner.impl.actor.StartCamel

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

import acyclic.skipped

class StartupCamelRoutes {
  //val logger = Logger(classOf[StartupCamelRoutes])

  var keep: StartCamel = null

  def addCamelRoutes(camelContext: OsgiDefaultCamelContext) = {
    // Add Camel routes
    keep = new StartCamel(camelContext)


  }
}
