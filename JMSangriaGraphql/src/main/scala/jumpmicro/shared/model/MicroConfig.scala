package jumpmicro.shared.model

import org.neo4j.ogm.annotation.{GraphId, NodeEntity}

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

@NodeEntity
class MicroConfig {
  @GraphId
  private var id: java.lang.Long = _
  private var nodeId: String = _

  def getNodeId = nodeId
  def setNodeId(n: String) = nodeId = n

  def this(s: String) = {
    this()
    nodeId = s
  }

}
