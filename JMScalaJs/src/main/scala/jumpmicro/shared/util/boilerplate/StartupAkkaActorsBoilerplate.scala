package jumpmicro.shared.util.boilerplate

import java.util.concurrent.TimeUnit
import javax.activation.DataHandler

import akka.actor.{ActorRef, Props}
import akka.camel.{Camel, CamelMessage}
import org.log4s._
import org.apache.camel.core.osgi.OsgiDefaultCamelContext
import org.slf4j.LoggerFactory
import scaldi.Injectable
import jumpmicro.jmscalajs.impl.configuration.MicroConfiguration

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.{Deadline, Duration}
import scala.util.{Failure, Success, Try}
//remove if not needed
import akka.actor.{ActorSystem}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------
import acyclic.skipped

trait StartupAkkaActorsBoilerplate {
  //val logger = Logger(classOf[StartupAkkaActorsBoilerplate])

  def akkaActors: Seq[Props]

  def addActors(config: MicroConfiguration, system: ActorSystem, camel: Camel, camelContext: OsgiDefaultCamelContext) = {
    val actors: Seq[ActorRef] = akkaActors.map((a) => system.actorOf(a) )
    addActorsImpl(config, actors, system, camel, camelContext)
  }

  def addActorsImpl(config: MicroConfiguration, actors: Seq[ActorRef], system: ActorSystem, camel: Camel, camelContext: OsgiDefaultCamelContext) = {
    // When all Akka actors started AND configuration loaded from Neo4J THEN trigger the Camel route direct:start
    // @todo Place timeout in configuration
    val timeout = Duration.create(60, TimeUnit.SECONDS)
    val activationFutures: Seq[Future[ActorRef]] = actors.map((a) => camel.activationFutureFor(a)(timeout, system.dispatcher))

    val lifted: Seq[Future[Try[ActorRef] with Product with Serializable]] = activationFutures.map(
      _.map(Success(_)).recover { case t => Failure(t) }
    )

    // Send a camel message to start events after configuration has loaded and all actors started
    Future.sequence(config.configuration.future +: lifted).onComplete((it) => {
      val c = new CamelMessage("body", null.asInstanceOf[Map[String,Any]], null.asInstanceOf[Map[String,DataHandler]])
      camelContext.createProducerTemplate.sendBody("direct:start", c)
    })
  }
}
