package jumpmicro.shared.util.configuration

import java.util

import jumpmicro.shared.model.MMicroConfig
import org.log4s.getLogger
import org.neo4j.ogm.exception.ConnectionException
import org.neo4j.ogm.model.Result
import org.neo4j.ogm.session.Session

//: ----------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: ----------------------------------------------------------------------------------

object ConfigurationUtil {
  private[this] val logger = getLogger

  def neo4JQueryOneResult(session: Session, nodeId: String): Object = {
    var result: Object = null
    try {
      // Based on the node id, fetch records from Neo4J (jumpmicro.nodeid).
      import collection.JavaConverters._
      val query = new java.lang.String("MATCH (n:MMicroConfig {nodeId:\"" + nodeId + "\"}) RETURN n")
      val r: Result = session.query(query, new java.util.HashMap[String, Object]())
      val it = r.queryResults().iterator()
      if (it.hasNext) {
        result = it.next().asInstanceOf[util.LinkedHashMap[String, Object]].values().asScala.head
      }
    } catch {
      case ex: ConnectionException => {
        logger.error("The Neo4J Database connection could not be established. This MicroService will continue to function without database access, however any further database access will fail.")
        result = new MMicroConfig(nodeId)
      }
    }
    result
  }

}
