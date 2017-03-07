package jumpmicro.jmresourceregistry.impl

import org.apache.camel.core.osgi.OsgiDefaultCamelContext
import scaldi.Injectable
import jumpmicro.jmresourceregistry.JMResourceRegistryService
import jumpmicro.jmresourceregistry.impl.configuration.MicroConfiguration
import jumpmicro.jmresourceregistry.impl.idris.TestIdris
import jumpmicro.jmresourceregistry.impl.service.HelloWorldServiceImpl
import jumpmicro.jmresourceregistry.impl.startup.StartupOsgi
import jumpmicro.shared.util.osgi.{BundleActivatorBoilerplate, OsgiCapsule}
import jumpmicro.jmresourceregistry.impl.configuration.GlobalModule._
import jumpmicro.shared.util.resourceshare._
import jumpmicro.shared.util.resourceshare.impl.ResourceShareServiceImpl

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

class JMResourceRegistryBundleActivator extends BundleActivatorBoilerplate with Injectable {
  whenBundleActive {
    addCapsule(new OsgiCapsule())

    whenServicePresent[ResourceShareService] { resourceShareService: ResourceShareService => {
    }

      addCapsule(new ResourceShareServiceImpl() )

    }
    TestIdris.test(bundleContext)
    // @todo Can I use scalaDi to better store this bundleContext as a global

    camelContext = new OsgiDefaultCamelContext(bundleContext)

    new HelloWorldServiceImpl().providesService[JMResourceRegistryService]

    val config: MicroConfiguration = inject[MicroConfiguration]
    StartupOsgi.startup(config, bundleContext, camelContext)
    onStop {
      camelContext.shutdown()
      system foreach (_.terminate())
    }
  }
}

