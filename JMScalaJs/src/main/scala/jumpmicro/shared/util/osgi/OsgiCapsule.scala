package jumpmicro.shared.util.osgi

import domino.capsule.Capsule
import jumpmicro.jmscalajs.impl.configuration.GlobalModule
import jumpmicro.shared.util.resourceshare.ResourceShare

//: ----------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: ----------------------------------------------------------------------------------

class OsgiCapsule extends Capsule {
  protected var resourceShare: Option[ResourceShare] = None

  def start() {
    startScaldi()
  }

  def stop() {
    stopScaldi()
  }

  private def startScaldi() = {
    GlobalModule.injector = GlobalModule.loadDI()
  }

  private def stopScaldi() = {
    GlobalModule.injector.destroy()
  }
}

