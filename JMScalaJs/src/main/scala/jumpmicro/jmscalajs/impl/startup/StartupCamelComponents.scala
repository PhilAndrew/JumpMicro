package jumpmicro.jmscalajs.impl.startup

//import com.typesafe.scalalogging.Logger
import org.apache.camel.component.exec.ExecComponent
import org.apache.camel.component.file.remote.FtpComponent
import org.apache.camel.component.seda.SedaComponent
import org.apache.camel.component.stream.StreamComponent
//import org.apache.camel.component.scp.ScpComponent
//import org.apache.camel.component.ssh.SshComponent
import org.apache.camel.core.osgi.OsgiDefaultCamelContext

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

class StartupCamelComponents {
  //val logger = Logger(classOf[StartupCamelComponents])

  def startup(camelContext: OsgiDefaultCamelContext) = {
    // Camel components should be added here.
    // A list of components can be found here https://camel.apache.org/components.html

    //camelContext.addComponent("docker", new DockerComponent())
    //camelContext.addComponent("scp", new ScpComponent())
    camelContext.addComponent("seda", new SedaComponent())
    camelContext.addComponent("exec", new ExecComponent())
    //camelContext.addComponent("ssh", new SshComponent())
    camelContext.addComponent("stream", new StreamComponent())
    camelContext.addComponent("ftp", new FtpComponent())
  }

}

