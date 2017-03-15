package jumpmicro.shared.util.osgi

import domino.capsule.Capsule
import jumpmicro.shared.util.global.CommonGlobalModule
import jumpmicro.shared.util.resourceshare.ResourceShareService
import org.log4s.getLogger

//: ----------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: ----------------------------------------------------------------------------------

class OsgiCapsule extends Capsule {
  private[this] val logger = getLogger

  protected var resourceShare: Option[ResourceShareService] = None

  def start() {
    startScaldi()
  }

  def stop() {
    stopScaldi()
  }

  def startScaldi() = {
    CommonGlobalModule.injector = CommonGlobalModule.loadDI()
  }

  private def stopScaldi() = {
    CommonGlobalModule.injector.destroy()
  }
}
