package universe.microservice.jmscalajs.impl.startup

import com.typesafe.scalalogging.Logger
import org.apache.camel.core.osgi.OsgiDefaultCamelContext
import org.osgi.framework.BundleContext
import universe.microservice.jmscalajs.impl.actor.StartCamel

//: ----------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: ----------------------------------------------------------------------------------

class StartupCamelRoutes {
  val logger = Logger(classOf[StartupCamelRoutes])

  var keep: StartCamel = null

  def addCamelRoutes(camelContext: OsgiDefaultCamelContext) = {
    // Add Camel routes
    keep = new StartCamel(camelContext)


  }
}
