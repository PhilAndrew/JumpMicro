package jumpmicro.shared.model

import java.lang.Long
import java.util.{Set => JSet}
import java.util.{HashSet => JHashSet}
import scala.collection.JavaConverters._
import org.neo4j.ogm.annotation.{GraphId, NodeEntity, Relationship}

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

@NodeEntity
class Movie {
  def getId: Long = id

  def getActors = {
    if (actors==null)
      actors = new JHashSet[ActorInMovie]()
    actors
  }

  @GraphId
  private var id: java.lang.Long = _

  private var title: String = _

  private var released: Int = _

  @Relationship(`type` = "ACTS_IN", direction = "INCOMING")
  var actors: JSet[ActorInMovie] = _
  def actorsAsScala: scala.collection.mutable.Set[ActorInMovie] = actors.asScala

  def this(title: String, year: Int) {
    this()
    this.title = title
    this.released = year
  }
}
