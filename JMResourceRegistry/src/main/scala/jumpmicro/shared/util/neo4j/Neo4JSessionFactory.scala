package jumpmicro.shared.util.neo4j

import org.log4s._
import org.neo4j.ogm.Neo4JOSGI
import org.neo4j.ogm.config.Configuration
import org.neo4j.ogm.drivers.bolt.driver.BoltDriver
import org.neo4j.ogm.session.Session
import org.neo4j.ogm.session.SessionFactory
import scaldi.Injectable
import jumpmicro.jmresourceregistry.impl.configuration.GlobalModule._
import org.neo4j.ogm.exception.ConnectionException

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

trait Neo4JSessionFactory

object Neo4JSessionFactory extends Injectable {
  private[this] val logger = getLogger

  def modelPackages = Seq("jumpmicro.shared.model")

  private lazy val sessionFactory = {

    Neo4JOSGI.modelPackagePath = "jumpmicro.shared.model"

    val neo4Jip = inject [String] (identified by "neo4j.server.ipaddress")
    val neo4Juser = inject [String] (identified by "neo4j.server.user")
    val neo4Jpassword = inject [String] (identified by "neo4j.server.password")

    val configuration = new Configuration()
    // Indexes http://neo4j.com/docs/ogm-manual/current/reference/#reference:indexing
    configuration.setAutoIndex("assert")
    configuration.set("username", neo4Juser)
    configuration.set("password", neo4Jpassword)

    val b = new BoltDriver()
    configuration.set("driver", "org.neo4j.ogm.drivers.bolt.driver.BoltDriver")
    configuration.set("URI", "bolt://" + neo4Juser + ":" + neo4Jpassword + "@" + neo4Jip + ":7687") // bolt port is 7687, http port is 7474
    import collection.JavaConverters._
    val result = try {
      new SessionFactory(configuration, modelPackages: _*)
    } catch {
      case ex: ConnectionException => {
        logger.error("Failed to connect to the Neo4J database, however this MicroService will still continue to run without a connection.")
        null
      }
      case _ => null
    }
    result
  }

  def getNeo4jSession(): Session = {
    if (sessionFactory!=null)
      sessionFactory.openSession()
    else null
  }
}
