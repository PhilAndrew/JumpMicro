package jumpmicro.jmsangriagraphql.impl.actor

import java.io.File
import java.util.UUID

import org.log4s._
import org.apache.camel.processor.aggregate.AggregationStrategy
import org.apache.camel.{CamelContext, Exchange, Processor}
import org.apache.camel.scala.dsl.builder.ScalaRouteBuilder
import scaldi.Injectable
import jumpmicro.jmsangriagraphql.impl.configuration.GlobalModule._

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

import acyclic.skipped

class StartCamel(context: CamelContext) extends ScalaRouteBuilder(context) with Injectable {
  //val logger = Logger(classOf[StartCamel])

  from("direct:start").to("direct:startWebServer")

  addRoutesToCamelContext(context)
}
