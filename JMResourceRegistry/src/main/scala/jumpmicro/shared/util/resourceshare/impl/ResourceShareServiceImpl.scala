package jumpmicro.shared.util.resourceshare.impl

import akka.actor.ActorSystem
import domino.DominoActivator
import domino.capsule.Capsule
import jumpmicro.shared.util.resourceshare.ResourceShareService
import org.log4s.getLogger

//: ----------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: ----------------------------------------------------------------------------------

class ResourceShareServiceImpl extends Capsule with ResourceShareService {
  private[this] val logger = getLogger

  override def start(): Unit = {
  }

  override def stop(): Unit = {
  }

  override def registerActorSystem(system: ActorSystem): Unit = {
    
  }

  override def getActorSystem(): ActorSystem = {
    null
  }
}
