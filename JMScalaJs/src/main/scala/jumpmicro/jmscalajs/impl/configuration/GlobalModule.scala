package jumpmicro.jmscalajs.impl.configuration

import java.io.File
import java.util

import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.Logger
import org.neo4j.ogm.model.{Result}
import org.neo4j.ogm.session.Session
import jumpmicro.jmscalajs.impl.startup.{StartupAkkaActors, StartupCamelComponents, StartupCamelRoutes}
import jumpmicro.shared.model.MicroConfig
import jumpmicro.shared.util.osgi.OsgiGlobal
//import org.neo4j.driver.v1.{AuthTokens, Driver, GraphDatabase}
import scaldi._
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

import acyclic.skipped

class GlobalModule extends Module {
  val logger = Logger(classOf[Any])

  bind [OsgiGlobal] to new OsgiGlobal
  bind [StartupAkkaActors] to new StartupAkkaActors
  bind [StartupCamelComponents] to new StartupCamelComponents
  bind [StartupCamelRoutes] to new StartupCamelRoutes
  bind [MicroConfiguration] to new MicroConfiguration
  //bind [Neo4JSessionFactory] to new Neo4JSessionFactory
}

object GlobalModule {
  val logger = Logger(classOf[GlobalModule])
  private var _config: Config = null

  def loadConfig(): Config = {
    var config: Config = null

    val c = System.getProperty("jumpmicro.config.path")
    val configPath = if (c == null) "jumpmicro.conf" else c
    if (configPath != null) {
      val f = new File(configPath)
      if (f.exists()) {
        config = ConfigFactory.parseFile(f)
      }
    }

    config
  }

  def loadConfigFromNeo4JBlocking(session: Session, nodeId: String): MicroConfig = {
    // Based on the node id, fetch records from Neo4J (jumpmicro.nodeid).
    import collection.JavaConverters._

    val query = new java.lang.String("MATCH (n:MicroConfig) RETURN n")

    val r: Result = session.query(query, new java.util.HashMap[String, Object]())

    var found = false
    val it = r.queryResults().iterator()
    while (it.hasNext) {
      val next = it.next()
      //logger.error(next.toString)
      found = true
    }
    val result = if (found) new MicroConfig(nodeId) else {
      val insert = new MicroConfig(nodeId)
      session.save(insert)
      insert
    }

    result
  }

  def loadDI() = {
    _config = loadConfig()
    TypesafeConfigInjector(_config) :: new GlobalModule
  }

  implicit var injector: MutableInjectorAggregation = null
}

