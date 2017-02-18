package jumpmicro.jmcloner.impl

import java.util.concurrent.TimeUnit

//import com.codahale.metrics.{ConsoleReporter, MetricRegistry}
import scaldi.Injectable

import scala.concurrent.ExecutionContext.Implicits.global
import org.apache.camel.core.osgi.OsgiDefaultCamelContext
import com.typesafe.scalalogging.Logger
import scaldi.Injectable

import scala.concurrent.ExecutionContext.Implicits.global
import domino._
import jumpmicro.jmcloner.JMClonerService
import jumpmicro.jmcloner.impl.configuration.MicroConfiguration
import jumpmicro.jmcloner.impl.idris.TestIdris
import jumpmicro.jmcloner.impl.service.HelloWorldServiceImpl
import jumpmicro.jmcloner.impl.startup.StartupOsgi
import jumpmicro.shared.util.osgi.{BundleActivatorBoilerplate, OsgiCapsule, OsgiGlobal}
import jumpmicro.jmcloner.impl.configuration.GlobalModule._
import jumpmicro.shared.util.resourceshare.ResourceShareService
import org.osgi.framework.{BundleActivator, BundleContext}

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

/*class Example() extends nl.grons.metrics.scala.DefaultInstrumented {
  // Define a timer metric
  private[this] val loading = metrics.timer("loading")

  // Use timer metric
  def loadStuff() = loading.time {
    Thread.sleep(1000)
  }
}
*/
// with nl.grons.metrics.scala.DefaultInstrumented
class JMClonerBundleActivator extends BundleActivatorBoilerplate with Injectable {
  val logger = Logger(classOf[JMClonerBundleActivator])

  /*def testMetrics() = {
    val reporter = ConsoleReporter.forRegistry(metricRegistry)
      .convertRatesTo(TimeUnit.SECONDS)
      .convertDurationsTo(TimeUnit.MILLISECONDS)
      .build()
    reporter.start(1, TimeUnit.SECONDS)

    val n = new Example()
    n.loadStuff()
  }*/

  // https://www.helgoboss.org/projects/domino/user-guide
  whenBundleActive {
    addCapsule(new OsgiCapsule())

    whenServicePresent[ResourceShareService] { resourceShareService: ResourceShareService => {
      }
    }

    //testMetrics()

    TestIdris.test(bundleContext)

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

    new HelloWorldServiceImpl().providesService[JMClonerService]

    onStop {
      system foreach (_.terminate())
    }
  }
}
