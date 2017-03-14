package jumpmicro.jmsangriagraphql.impl.configuration

import java.io.{File, FileOutputStream, PrintWriter}

import com.typesafe.config.{Config, ConfigFactory}
import org.log4s._
import org.neo4j.ogm.session.Session
import jumpmicro.jmsangriagraphql.impl.startup.{StartupAkkaActors, StartupCamelComponents, StartupCamelRoutes}
import jumpmicro.shared.model.MMicroConfig
import jumpmicro.shared.util.configuration.{ConfigurationUtil, MicroConfiguration}
import jumpmicro.shared.util.osgi.OsgiGlobal
import org.neo4j.ogm.exception.ConnectionException

import scala.util.{Failure, Success, Try}
import scaldi._

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import io.jvm.uuid._

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

import acyclic.skipped

class GlobalModule extends Module {
  private[this] val logger = getLogger

  bind [OsgiGlobal] to new OsgiGlobal
  bind [StartupAkkaActors] to new StartupAkkaActors
  bind [StartupCamelComponents] to new StartupCamelComponents
  bind [StartupCamelRoutes] to new StartupCamelRoutes
  bind [MicroConfiguration] to new MicroConfiguration
}

object GlobalModule {
  private[this] val logger = getLogger

  private var _config: Config = null

  // @todo Move some of this code to shared

  private def loadConfigFromFile(): Try[Config] = {
    var config: Config = null

    val configPath = scala.util.Properties.envOrElse("JUMPMICRO_CONFIG_PATH", "jumpmicro.conf")
    val f = new File(configPath)
    if (f.exists()) {
      config = ConfigFactory.parseFile(f)
      val packageName = this.getClass.getPackage.getName
      val thisPackage = packageName.substring(0, packageName.indexOf('.', packageName.indexOf('.')+1))
      val nodeIdKey = "jumpmicro.nodeid"
      if (!config.hasPath(nodeIdKey)) {
        val v = s"$thisPackage.${UUID.random}"
        val jmFile = new File("jumpmicro.conf")
        if (jmFile.canWrite) {
          val write = new PrintWriter(new FileOutputStream(jmFile,true))
          write.println("")
          write.println(s"$nodeIdKey = $v")
          write.flush(); write.close()
        } else {
          logger.error("The jumpmicro.conf file is not writable and we are trying to write the jumpmicro.nodeid configuration value. Please make the file writable to allow for this program to startup correctly.")
        }
        config = ConfigFactory.parseFile(f)
      }
      Success(config)
    } else Failure(null)
  }


  def loadConfigFromNeo4JBlocking(session: Session, nodeId: String): MMicroConfig = {
    val obj: Object = ConfigurationUtil.neo4JQueryOneResult(session, nodeId)

    val result = if (obj==null) {
      val r = new MMicroConfig(nodeId)
      session.save(r)
      r
    } else obj.asInstanceOf[MMicroConfig]

    result
  }

  def loadDI(): MutableInjectorAggregation = {
    val config = loadConfigFromFile()
    if (config.isSuccess) {
      _config = config.get
      TypesafeConfigInjector(_config) :: new GlobalModule
    } else {
      // @todo What to do in error case?
      null
    }
  }

  implicit var injector: MutableInjectorAggregation = null
}
