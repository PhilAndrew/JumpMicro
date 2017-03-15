package jumpmicro.jmresourceregistry.impl.configuration

import java.io.{File, FileOutputStream, PrintWriter}

import com.typesafe.config.{Config, ConfigFactory}
import org.log4s._
import org.neo4j.ogm.session.Session
import jumpmicro.shared.model.MMicroConfig
import jumpmicro.shared.util.configuration.{ConfigurationUtil, MicroConfiguration}
import jumpmicro.shared.util.osgi.OsgiGlobal
import org.neo4j.ogm.exception.ConnectionException

import scala.util.{Failure, Success, Try}
import scaldi._

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import io.jvm.uuid._
import jumpmicro.jmresourceregistry.impl.startup.{StartupAkkaActors, StartupCamelComponents, StartupCamelRoutes}
import jumpmicro.shared.util.global.CommonGlobalModule

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

import acyclic.skipped

class GlobalModule extends Module {
  private[this] val logger = getLogger

  bind [StartupAkkaActors] to new StartupAkkaActors
  bind [StartupCamelComponents] to new StartupCamelComponents
  bind [StartupCamelRoutes] to new StartupCamelRoutes
  bind [MicroConfiguration] to new MicroConfiguration
}

object GlobalModule {

}
