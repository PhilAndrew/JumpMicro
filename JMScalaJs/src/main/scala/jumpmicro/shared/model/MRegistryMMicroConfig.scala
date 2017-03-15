package jumpmicro.shared.model

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

import org.neo4j.ogm.annotation.{GraphId, NodeEntity, Relationship}
import java.lang.{Long => JLong}
import java.util.{Set => JSet}
import java.util.{HashSet => JHashSet}
import java.lang.{String => JString}

import scala.beans.BeanProperty
import scala.collection.JavaConverters._

class MRegistryMMicroConfig extends MMicroConfig {
  @BeanProperty
  var storagePathOnDisk: String = "./data"
}
