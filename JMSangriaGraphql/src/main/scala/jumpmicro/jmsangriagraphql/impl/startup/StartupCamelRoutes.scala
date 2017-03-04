package jumpmicro.jmsangriagraphql.impl.startup

import org.log4s._
import org.apache.camel.core.osgi.OsgiDefaultCamelContext
import jumpmicro.jmsangriagraphql.impl.actor.StartCamel

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

class StartupCamelRoutes {
  private[this] val logger = getLogger

  var keep: StartCamel = null

  def addCamelRoutes(camelContext: OsgiDefaultCamelContext) = {
    // Add Camel routes
    keep = new StartCamel(camelContext)


  }
}
