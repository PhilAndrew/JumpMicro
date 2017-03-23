package jumpmicro.jmcloner.impl.webserver

//: ----------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: ----------------------------------------------------------------------------------

object RunWebServer extends App {
  val webServer = new WebServer()
  webServer.start()
}
