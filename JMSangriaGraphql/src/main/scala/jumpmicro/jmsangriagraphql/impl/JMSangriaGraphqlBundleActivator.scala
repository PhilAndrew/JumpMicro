package jumpmicro.jmsangriagraphql.impl

import org.apache.camel.core.osgi.OsgiDefaultCamelContext
import scaldi.Injectable
import jumpmicro.jmsangriagraphql.JMSangriaGraphqlService
import jumpmicro.jmsangriagraphql.impl.configuration.GlobalModule
import jumpmicro.jmsangriagraphql.impl.idris.TestIdris
import jumpmicro.jmsangriagraphql.impl.service.HelloWorldServiceImpl
import jumpmicro.jmsangriagraphql.impl.startup.StartupOsgi
import jumpmicro.shared.util.osgi.{BundleActivatorBoilerplate, OsgiCapsule}
import jumpmicro.jmsangriagraphql.impl.configuration.GlobalModule._
import jumpmicro.shared.util.configuration.MicroConfiguration
import jumpmicro.shared.util.global.CommonGlobalModule
import jumpmicro.shared.util.global.CommonGlobalModule._

import jumpmicro.shared.util.resourceshare._

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

class JMSangriaGraphqlBundleActivator extends BundleActivatorBoilerplate with Injectable {
  whenBundleActive {
    CommonGlobalModule.injector = CommonGlobalModule.loadDI() :: new GlobalModule

    addCapsule(new OsgiCapsule() {
      override def startScaldi() = {
      }
    })

    whenServicePresent[ResourceShareService] { resourceShareService: ResourceShareService => {
    }

    }
    TestIdris.test(bundleContext)
    // @todo Can I use scalaDi to better store this bundleContext as a global

    camelContext = new OsgiDefaultCamelContext(bundleContext)

    new HelloWorldServiceImpl().providesService[JMSangriaGraphqlService]

    val config: MicroConfiguration = inject[MicroConfiguration]
    new StartupOsgi().startup(config, bundleContext, camelContext)
    onStop {
      camelContext.shutdown()
      system foreach (_.terminate())
    }
  }
}

