package jumpmicro.jmsangriagraphql.impl.actor

import org.log4s._
import org.apache.camel.{CamelContext}
import org.apache.camel.scala.dsl.builder.ScalaRouteBuilder
import scaldi.Injectable

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

import acyclic.skipped

class StartCamel(context: CamelContext) extends ScalaRouteBuilder(context) with Injectable {
  private[this] val logger = getLogger

  from("direct:start").to("direct:startWebServer")

  addRoutesToCamelContext(context)
}
