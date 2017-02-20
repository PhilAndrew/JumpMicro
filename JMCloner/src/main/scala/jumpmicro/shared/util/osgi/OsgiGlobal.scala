package jumpmicro.shared.util.osgi

//import com.typesafe.scalalogging.Logger
import org.apache.camel.CamelContext
import org.osgi.framework.BundleContext
import org.slf4j.LoggerFactory

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

class OsgiGlobal {
  //val logger = Logger(classOf[OsgiGlobal])

  var bundleContext: BundleContext = null
  var camelContext: CamelContext = null
}
