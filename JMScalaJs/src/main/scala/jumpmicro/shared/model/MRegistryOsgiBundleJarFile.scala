package jumpmicro.shared.model

import org.neo4j.ogm.annotation.{GraphId, NodeEntity, Relationship}
import java.lang.{Long => JLong}
import java.util.{Set => JSet}
import java.util.{HashSet => JHashSet}
import java.lang.{String => JString}
import java.time.{LocalDateTime, ZoneId}

import scala.beans.BeanProperty
import scala.collection.JavaConverters._

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

class MRegistryOsgiBundleJarFile extends MObject {
  @BeanProperty
  var uuidValue: String = null
  @BeanProperty
  var dateTimeCreatedMilliseconds: Long = 0L
  @BeanProperty
  var dateTimeCreatedZone: String = null
  @BeanProperty
  var nodeId: String = null

  def setDateTimeCreated(date: LocalDateTime) = {
    val zoneId = ZoneId.systemDefault() // or: ZoneId.of("Europe/Oslo");
    val epoch = date.atZone(zoneId).toInstant().toEpochMilli()

    dateTimeCreatedZone = zoneId.getId
    dateTimeCreatedMilliseconds = epoch
  }
}
