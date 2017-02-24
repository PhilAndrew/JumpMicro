package jumpmicro.jmsangriagraphql

import org.log4s._

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

/**
  * The trait which is exposed by this OSGi component.
  *
  * Normal OSGi works by having a bundle expose a Java interface, in this case a trait is a Java interface.
  * The trait exposes the interface the OSGi bundle implements. The implementation is hidden in the impl package
  * folder.
  *
  * Rename this to YourNameService where YourName is the name of
  * the service you are providing. You would also want to rename the osgidemo package to com.yourpackage
  *
  * Trait(s) should exist here in this for the services the OSGi component is exposing.
  */
trait JMSangriaGraphqlService {
  def hello(): Unit
  def startup(): Unit
}
