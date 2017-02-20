package jumpmicro.shared.util.akkaosgi

import akka.actor.ExtendedActorSystem
import akka.camel.ContextProvider
//import com.typesafe.scalalogging.Logger
import org.apache.camel.impl.DefaultCamelContext

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

class AkkaCamelContextProvider extends ContextProvider {
  //val logger = Logger(classOf[AkkaCamelContextProvider])

  override def getContext(system: ExtendedActorSystem): DefaultCamelContext = {
    AkkaCamelContextProvider.contextProvider
  }
}

object AkkaCamelContextProvider {
  var contextProvider: DefaultCamelContext = null
}