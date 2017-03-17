package jumpmicro.shared.util.akkaosgi

import akka.actor.ActorSystem
import akka.osgi.ActorSystemActivator
import com.typesafe.config.{Config, ConfigFactory}
import org.log4s._
import org.osgi.framework.BundleContext
import org.slf4j.LoggerFactory

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

/**
  * Factory class to create ActorSystem implementations in an OSGi environment.  This mainly involves dealing with
  * bundle classloaders appropriately to ensure that configuration files and classes get loaded properly
  */
class MyOsgiActorSystemFactory(val context: BundleContext, val fallbackClassLoader: Option[ClassLoader], config: Config = ConfigFactory.empty) {
  private[this] val logger = getLogger

  /*
   * Classloader that delegates to the bundle for which the factory is creating an ActorSystem
   */
  private val classloader = MyBundleDelegatingClassLoader(context, fallbackClassLoader)

  /**
    * Creates the [[akka.actor.ActorSystem]], using the name specified
    */
  def createActorSystem(name: String): ActorSystem = createActorSystem(Option(name))

  /**
    * Creates the [[akka.actor.ActorSystem]], using the name specified.
    *
    * A default name (`bundle-&lt;bundle id&gt;-ActorSystem`) is assigned when you pass along [[scala.None]] instead.
    */
  def createActorSystem(name: Option[String]): ActorSystem = {
    //val cf = ConfigFactory.load(getClass.getClassLoader)
    //logger.error("C: " + cf)
    ActorSystem(actorSystemName(name), actorSystemConfig(), classloader) // getClass.getClassLoader

    //val akkaSystem = ActorSystem("mySystem", cf, classLoader=getClass.getClassLoader)
    //akkaSystem
  }

  /**
    * Strategy method to create the Config for the ActorSystem
    * ensuring that the default/reference configuration is loaded from the akka-actor bundle.
    * Configuration files found in akka-actor bundle
    */
  def actorSystemConfig(): Config = {
    val configb = config.withFallback(ConfigFactory.load(classloader))
    val configc = ConfigFactory.load(classloader).withFallback(ConfigFactory.defaultReference(MyOsgiActorSystemFactory.akkaActorClassLoader))
    config.withFallback(ConfigFactory.load(classloader).withFallback(ConfigFactory.defaultReference(MyOsgiActorSystemFactory.akkaActorClassLoader)))
  }

  /**
    * Determine the name for the [[akka.actor.ActorSystem]]
    * Returns a default value of `bundle-&lt;bundle id&gt;-ActorSystem` is no name is being specified
    */
  def actorSystemName(name: Option[String]): String =
    name.getOrElse("bundle-%s-ActorSystem".format(context.getBundle.getBundleId))

}

object MyOsgiActorSystemFactory {
  /**
    * Class loader of akka-actor bundle.
    */
  def akkaActorClassLoader = classOf[ActorSystemActivator].getClassLoader

  /*
   * Create an [[OsgiActorSystemFactory]] instance to set up Akka in an OSGi environment
   */
  def apply(context: BundleContext, config: Config): MyOsgiActorSystemFactory = new MyOsgiActorSystemFactory(context, Some(akkaActorClassLoader), config)
}