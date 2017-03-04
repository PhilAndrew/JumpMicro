package jumpmicro.jmsangriagraphql.impl

import java.util.concurrent.TimeUnit

//import com.codahale.metrics.{ConsoleReporter, MetricRegistry}
import scaldi.Injectable

import scala.concurrent.ExecutionContext.Implicits.global
import org.apache.camel.core.osgi.OsgiDefaultCamelContext
import org.log4s._
import scaldi.Injectable

import scala.concurrent.ExecutionContext.Implicits.global
import domino._
import jumpmicro.jmsangriagraphql.JMSangriaGraphqlService
import jumpmicro.jmsangriagraphql.impl.configuration.MicroConfiguration
import jumpmicro.jmsangriagraphql.impl.idris.TestIdris
import jumpmicro.jmsangriagraphql.impl.service.HelloWorldServiceImpl
import jumpmicro.jmsangriagraphql.impl.startup.StartupOsgi
import jumpmicro.shared.util.osgi.{BundleActivatorBoilerplate, OsgiCapsule, OsgiGlobal}
import jumpmicro.jmsangriagraphql.impl.configuration.GlobalModule._
import jumpmicro.shared.util.resourceshare._
import org.osgi.framework.{BundleActivator, BundleContext}

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

class JMSangriaGraphqlBundleActivator extends BundleActivatorBoilerplate with Injectable {
  whenBundleActive {
    addCapsule(new OsgiCapsule())
    whenServicePresent[ResourceShareService] { resourceShareService: ResourceShareService => {
    }
    }
    TestIdris.test(bundleContext)
    // @todo Can I use scalaDi to better store this bundleContext as a global

    camelContext = new OsgiDefaultCamelContext(bundleContext)

    new HelloWorldServiceImpl().providesService[JMSangriaGraphqlService]

    val config: MicroConfiguration = inject[MicroConfiguration]
    StartupOsgi.startup(config, bundleContext, camelContext)
    onStop {
      system foreach (_.terminate())
    }
  }
}

