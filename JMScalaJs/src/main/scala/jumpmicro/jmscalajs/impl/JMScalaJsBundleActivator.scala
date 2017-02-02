package jumpmicro.jmscalajs.impl

import java.util.concurrent.TimeUnit

import com.codahale.metrics.{ConsoleReporter, MetricRegistry}
import scaldi.Injectable

import scala.concurrent.ExecutionContext.Implicits.global
import org.apache.camel.core.osgi.OsgiDefaultCamelContext
import com.typesafe.scalalogging.Logger
import scaldi.Injectable

import scala.concurrent.ExecutionContext.Implicits.global
import domino._
import jumpmicro.jmscalajs.MicroServiceScalaJsService
import jumpmicro.jmscalajs.impl.configuration.MicroConfiguration
import jumpmicro.jmscalajs.impl.idris.TestIdris
import jumpmicro.jmscalajs.impl.service.HelloWorldServiceImpl
import jumpmicro.jmscalajs.impl.startup.StartupOsgi
import jumpmicro.shared.util.osgi.{BundleActivatorBoilerplate, OsgiGlobal}
import jumpmicro.jmscalajs.impl.configuration.GlobalModule._

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

class Example() extends nl.grons.metrics.scala.DefaultInstrumented {
  // Define a timer metric
  private[this] val loading = metrics.timer("loading")

  // Use timer metric
  def loadStuff() = loading.time {
    Thread.sleep(1000)
  }
}

class JMScalaJsBundleActivator extends BundleActivatorBoilerplate with Injectable with nl.grons.metrics.scala.DefaultInstrumented {
  val logger = Logger(classOf[JMScalaJsBundleActivator])

  def testMetrics() = {
    val reporter = ConsoleReporter.forRegistry(metricRegistry)
      .convertRatesTo(TimeUnit.SECONDS)
      .convertDurationsTo(TimeUnit.MILLISECONDS)
      .build()
    reporter.start(1, TimeUnit.SECONDS)

    val n = new Example()
    n.loadStuff()
  }

  // https://www.helgoboss.org/projects/domino/user-guide
  whenBundleActive {

    testMetrics()

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
