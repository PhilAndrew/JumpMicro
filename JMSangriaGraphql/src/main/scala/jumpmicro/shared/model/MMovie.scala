package jumpmicro.shared.model

import org.neo4j.ogm.annotation.{GraphId, NodeEntity, Relationship}
import java.lang.{Long => JLong}
import java.util.{Set => JSet}
import java.util.{HashSet => JHashSet}
import java.lang.{String => JString}

import scala.beans.BeanProperty
import scala.collection.JavaConverters._

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

import acyclic.skipped

@NodeEntity
class MMovie {
  @GraphId
  @BeanProperty
  var id: java.lang.Long = _

  @BeanProperty
  var title: String = _

  @BeanProperty
  var released: Int = _

  @Relationship(`type` = "ACTS_IN", direction = "INCOMING")
  @BeanProperty
  var actors: JSet[MActor] = _
  def actorsAsScala: scala.collection.mutable.Set[MActor] = actors.asScala

  def this(title: String, year: Int) {
    this()
    this.title = title
    this.released = year
  }
}
