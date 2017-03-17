package jumpmicro.shared.util.resourceshare

import akka.actor.ActorSystem

//: ----------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: ----------------------------------------------------------------------------------

trait ResourceShareService {
  def registerActorSystem(system: ActorSystem)
  def getActorSystem(): ActorSystem
}
