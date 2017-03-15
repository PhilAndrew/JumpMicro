package jumpmicro.shared.model

import org.neo4j.ogm.annotation.{GraphId, Index, NodeEntity, Relationship}
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

@NodeEntity
class MMicroConfig {
  @GraphId
  @BeanProperty
  var id: java.lang.Long = _

  @Index
  @BeanProperty
  var nodeId: String = _

  def this(s: String) = {
    this()
    nodeId = s
  }

}
