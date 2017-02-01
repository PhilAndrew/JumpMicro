package jumpmicro.shared.model

import java.time.{LocalDateTime, ZoneId, ZoneOffset}

import org.neo4j.ogm.annotation.{GraphId, NodeEntity}

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

class MRegistryOsgiBundleJarFile extends MObject {
  var uuidValue: String = null
  var dateTimeCreatedMilliseconds: Long = 0L
  var dateTimeCreatedZone: String = null
  var nodeId: String = null

  def setDateTimeCreated(date: LocalDateTime) = {
    val zoneId = ZoneId.systemDefault() // or: ZoneId.of("Europe/Oslo");
    val epoch = date.atZone(zoneId).toInstant().toEpochMilli()

    dateTimeCreatedZone = zoneId.getId
    dateTimeCreatedMilliseconds = epoch
  }
}
