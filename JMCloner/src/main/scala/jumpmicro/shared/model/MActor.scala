package jumpmicro.shared.model

//: ----------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: ----------------------------------------------------------------------------------

import java.util

import org.neo4j.ogm.annotation.{GraphId, NodeEntity, Relationship}
import java.lang.{Long => JLong}
import java.util.{Set => JSet}
import java.util.{HashSet => JHashSet}
import java.lang.{String => JString}

import scala.beans.BeanProperty
import scala.collection.JavaConversions._

@NodeEntity
class MActor {

  @GraphId
  @BeanProperty
  var id: JLong = _

  @BeanProperty
  var name: JString = _

  @Relationship(`type` = "ACTS_IN", direction = "OUTGOING")
  @BeanProperty
  var movies: JSet[MMovie] = new util.HashSet()

  def this(name: String) = {
    this()
    this.name = name
  }

  def actsIn(movie: MMovie): Unit = {
    movies.add(movie)
    movie.getActors.add(this)
  }

}