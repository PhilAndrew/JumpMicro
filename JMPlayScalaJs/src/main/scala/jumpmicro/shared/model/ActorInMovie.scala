package jumpmicro.shared.model

import org.neo4j.ogm.annotation.{GraphId, NodeEntity, Relationship}
import java.util.{Set => JSet}
import java.util.{HashSet => JHashSet}
import scala.collection.JavaConverters._

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

import acyclic.skipped

@NodeEntity
class ActorInMovie {
  def getName: String = name

  @GraphId
  private var id: java.lang.Long = _
  private var name: String = _

  @Relationship(`type` = "ACTS_IN", direction = "OUTGOING")
  private var movies: JSet[Movie] = new JHashSet[Movie]()
  def moviesAsScala: scala.collection.mutable.Set[Movie] = movies.asScala

  def this(name: String) {
    this()
    this.name = name
  }

  def actsIn(movie: Movie) {
    moviesAsScala.add(movie)
    movie.getActors.add(this)
  }
}

