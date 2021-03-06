package jumpmicro.shared.util.neo4j

import org.log4s._
import org.neo4j.ogm.session.Session

import scala.concurrent.{ExecutionContext, Future}

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

trait Neo4JUtil

object Neo4JUtil {
  private[this] val logger = getLogger

  def save(session: Session, obj: Any)(implicit ex: ExecutionContext): Future[_] = {
    Future {
      try {
        val tx = session.beginTransaction()
        session.save(obj)
        tx.commit()
      } catch {
        case ex: Exception => ex.printStackTrace()
      }
    }
  }
}


