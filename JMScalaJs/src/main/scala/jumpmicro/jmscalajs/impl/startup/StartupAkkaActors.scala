package universe.microservice.jmscalajs.impl.startup

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, Props}
import akka.camel.{Camel, CamelMessage}
import com.typesafe.scalalogging.Logger
import org.apache.camel.core.osgi.OsgiDefaultCamelContext
import org.slf4j.LoggerFactory
import scaldi.{Injectable, Injector}
import universe.microservice.jmscalajs.impl.actor.StartWebServerActor
import universe.microservice.microservicescalajs.impl.actor.StartWebServerActor
import universe.microservice.microservicescalajs.impl.configuration.MicroConfiguration
import universe.microservice.shared.util.boilerplate.StartupAkkaActorsBoilerplate

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success, Try}
//remove if not needed
import akka.actor.{ActorSystem}
import scala.concurrent.ExecutionContext.Implicits.global

//: ----------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: ----------------------------------------------------------------------------------

class StartupAkkaActors extends StartupAkkaActorsBoilerplate {

  // Add your Akka Actors here and they will start when this OSGi component loads
  def akkaActors = Seq(Props[StartWebServerActor])

}
