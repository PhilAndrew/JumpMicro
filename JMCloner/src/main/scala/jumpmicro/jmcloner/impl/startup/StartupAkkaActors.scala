package jumpmicro.jmcloner.impl.startup

import akka.actor.{ActorRef, Props}
import jumpmicro.jmcloner.impl.actor.StartWebServerActor
import jumpmicro.shared.util.boilerplate.StartupAkkaActorsBoilerplate
//remove if not needed
import scala.concurrent.ExecutionContext.Implicits.global

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

import acyclic.skipped

class StartupAkkaActors extends StartupAkkaActorsBoilerplate {

  // Add your Akka Actors here and they will start when this OSGi component loads
  def akkaActors = Seq(Props[StartWebServerActor])

}
