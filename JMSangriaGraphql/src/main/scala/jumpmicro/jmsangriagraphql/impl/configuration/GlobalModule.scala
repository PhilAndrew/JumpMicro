package jumpmicro.jmsangriagraphql.impl.configuration

import java.io.{File, FileOutputStream, PrintWriter}
import java.util

import com.typesafe.config.{Config, ConfigFactory}
import org.log4s._
import org.neo4j.ogm.model.Result
import org.neo4j.ogm.session.Session
import jumpmicro.jmsangriagraphql.impl.startup.{StartupAkkaActors, StartupCamelComponents, StartupCamelRoutes}
import jumpmicro.shared.model.MicroConfig
import jumpmicro.shared.util.osgi.OsgiGlobal
import org.neo4j.ogm.exception.ConnectionException

import scala.util.{Failure, Success, Try}
//import org.neo4j.driver.v1.{AuthTokens, Driver, GraphDatabase}
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
      if (!config.hasPath("jumpmicro.nodeid")) {
        val packageName = this.getClass.getPackage.getName
        val thisPackage = packageName.substring(0, packageName.indexOf('.', packageName.indexOf('.')+1))
        val v = s"$thisPackage.${UUID.random}"
        // @todo What if we cannot write to the file, what to do?
        val write = new PrintWriter(new FileOutputStream(new File("jumpmicro.conf"),true))
        write.println("")
        write.println(s"jumpmicro.nodeid = $v")
        write.flush(); write.close()

        config = ConfigFactory.parseFile(f)
      }
      Success(config)
    } else Failure(null)
  }

  def loadConfigFromNeo4JBlocking(session: Session, nodeId: String): MicroConfig = {
    println("NODE ID " + nodeId)

    var result: MicroConfig = null
    try {
      // Based on the node id, fetch records from Neo4J (jumpmicro.nodeid).
      import collection.JavaConverters._
      val query = new java.lang.String("MATCH (n:MicroConfig {nodeId:\"" + nodeId + "\"}) RETURN n")
      val r: Result = session.query(query, new java.util.HashMap[String, Object]())
      var found = false
      val it = r.queryResults().iterator()
      while (it.hasNext) {
        val next = it.next()
        logger.error(next.toString)
        found = true
      }
      if (found) new MicroConfig(nodeId) else {
        result = new MicroConfig(nodeId)
        session.save(result)
      }

    } catch {
      case ex: ConnectionException => {
        logger.error("The Neo4J Database connection could not be established. This MicroService will continue to function without database access, however any further database access will fail.")
        result = new MicroConfig(nodeId)
      }
    }
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
