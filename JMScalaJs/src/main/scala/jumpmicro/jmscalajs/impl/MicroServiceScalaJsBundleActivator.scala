package universe.microservice.jmscalajs.impl

import scaldi.Injectable
import universe.microservice.microservicescalajs.MicroServiceScalaJsService
import universe.microservice.microservicescalajs.impl.service.HelloWorldServiceImpl
import universe.microservice.microservicescalajs.impl.startup.StartupOsgi
import universe.microservice.shared.util.osgi.{BundleActivatorBoilerplate, OsgiGlobal}

import scala.concurrent.ExecutionContext.Implicits.global
import org.apache.camel.core.osgi.OsgiDefaultCamelContext
import com.typesafe.scalalogging.Logger
import scaldi.Injectable
import universe.microservice.microservicescalajs.impl.configuration.{GlobalModule, MicroConfiguration}
import universe.microservice.microservicescalajs.impl.configuration.GlobalModule._
import universe.microservice.microservicescalajs.impl.idris.TestIdris
import universe.microservice.microservicescalajs.impl.startup.{StartupCamelRoutes, StartupOsgi}

import scala.concurrent.ExecutionContext.Implicits.global
import domino._
import universe.microservice.jmscalajs.MicroServiceScalaJsService
import universe.microservice.jmscalajs.impl.configuration.MicroConfiguration
import universe.microservice.jmscalajs.impl.idris.TestIdris
import universe.microservice.jmscalajs.impl.service.HelloWorldServiceImpl
import universe.microservice.jmscalajs.impl.startup.StartupOsgi

//: ----------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: ----------------------------------------------------------------------------------

class MicroServiceScalaJsBundleActivator extends BundleActivatorBoilerplate with Injectable {
  val logger = Logger(classOf[MicroServiceScalaJsBundleActivator])

  // https://www.helgoboss.org/projects/domino/user-guide
  whenBundleActive {

    TestIdris.test()

    startScaldi()

    // @todo Can I use scalaDi to better store this bundleContext as a global
    val osgiGlobal: OsgiGlobal = inject[OsgiGlobal]
    osgiGlobal.bundleContext = bundleContext

    val config: MicroConfiguration = inject[MicroConfiguration]
    org.neo4j.ogm.Neo4JOGM.setBundleContext(bundleContext)

    camelContext = new OsgiDefaultCamelContext(bundleContext)
    osgiGlobal.camelContext = camelContext

    StartupOsgi.startup(config, bundleContext, camelContext)

    /*
     whenServicePresent[OtherService] { os =>
       new MyService(os).providesService[MyService]
     }
     */

    new HelloWorldServiceImpl().providesService[MicroServiceScalaJsService]

    onStop {
      system foreach (_.terminate())
      stopScaldi()
    }
  }


}
