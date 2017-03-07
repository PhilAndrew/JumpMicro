package jumpmicro.shared.util.osgi

import org.log4s._
import org.apache.camel.CamelContext
import org.osgi.framework.BundleContext

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

class OsgiGlobal {
  private[this] val logger = getLogger

  var bundleContext: BundleContext = null
  var camelContext: CamelContext = null
}
