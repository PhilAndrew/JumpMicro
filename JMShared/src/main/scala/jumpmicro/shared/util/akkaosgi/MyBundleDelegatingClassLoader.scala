package jumpmicro.shared.util.akkaosgi

import language.existentials
import java.net.URL
import java.util.Enumeration
import org.log4s._
import org.osgi.framework.{Bundle, BundleContext}
import scala.util.Try
import org.osgi.framework.wiring.{BundleRevision, BundleWire, BundleWiring}
import scala.collection.JavaConverters._
import scala.util.Success
import scala.util.Failure
import scala.annotation.tailrec

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

/*
 * Companion object to create bundle delegating ClassLoader instances
 */
object MyBundleDelegatingClassLoader {
  private[this] val logger = getLogger

  /*
   * Create a bundle delegating ClassLoader for the bundle context's bundle
   */
  def apply(context: BundleContext): MyBundleDelegatingClassLoader = new MyBundleDelegatingClassLoader(context.getBundle, null)

  def apply(context: BundleContext, fallBackCLassLoader: Option[ClassLoader]): MyBundleDelegatingClassLoader =
    new MyBundleDelegatingClassLoader(context.getBundle, fallBackCLassLoader.orNull)
}

/*
 * A bundle delegating ClassLoader implementation - this will try to load classes and resources from the bundle
 * and the bundles transitive dependencies. If there's a ClassLoader specified, that will be used as a fallback.
 */
class MyBundleDelegatingClassLoader(bundle: Bundle, fallBackClassLoader: ClassLoader) extends ClassLoader(fallBackClassLoader) {

  private val bundles = findTransitiveBundles(bundle).toList

  override def findClass(name: String): Class[_] = {
    @tailrec def find(remaining: List[Bundle]): Class[_] = {
      if (remaining.isEmpty) throw new ClassNotFoundException(name)
      else Try { remaining.head.loadClass(name) } match {
        case Success(cls) ⇒
        {
          cls
        }
        case Failure(_)   ⇒ find(remaining.tail)
      }
    }
    find(bundles)
  }

  override def findResource(name: String): URL = {
    @tailrec def find(remaining: List[Bundle]): URL = {
      if (remaining.isEmpty) getParent.getResource(name)
      else Option { remaining.head.getResource(name) } match { // linter:ignore UseGetOrElseNotPatMatch
        case Some(r) ⇒ r
        case None    ⇒ find(remaining.tail)
      }
    }
    /*logger.error("result is:")
    for (d <- bundle.getBundleContext.getBundles.toList) {
      logger.error(":::" + d.getLocation)
    }
    logger.error("end#######################")*/
    find(bundle.getBundleContext.getBundles.toList)
  }

  //val logger = Logger(LoggerFactory.getLogger("name"))
  override def findResources(name: String): Enumeration[URL] = {
    // Debugging
    //val bbb = bundle.getBundleContext.getBundles.toList

    val resources = bundle.getBundleContext.getBundles.toList.flatMap {
      bundle ⇒ Option(bundle.getResources(name)).map { _.asScala.toList }.getOrElse(Nil)
    }

    val filtered = resources.filter( (p) => {
      p.getFile.indexOf("akka-cluster_2.11") < 0
    })

    /*logger.error("start#####################################")
    logger.error("Find resource by name filtered: " + name)
    for (b <- filtered) {
      logger.error("resource :::"+b.getFile)
    }
    logger.error("end#####################################")
*/
    java.util.Collections.enumeration(filtered.asJava)
  }

  def removeSystemBundle(result: Set[Bundle]): Set[Bundle] = {
    result.filterNot(_.getLocation=="System Bundle")
  }

  private def findTransitiveBundles(bundle: Bundle): Set[Bundle] = {
    @tailrec def process(processed: Set[Bundle], remaining: Set[Bundle]): Set[Bundle] = {
      if (remaining.isEmpty) {
        processed
      } else {
        val (b, rest) = (remaining.head, remaining.tail)
        if (processed contains b) {
          process(processed, rest)
        } else {
          val wiring = b.adapt(classOf[BundleWiring])
          val direct: Set[Bundle] =
            if (wiring == null) Set.empty
            else {
              val requiredWires: List[BundleWire] =
                wiring.getRequiredWires(BundleRevision.PACKAGE_NAMESPACE).asScala.toList
              requiredWires.flatMap {
                wire ⇒ Option(wire.getProviderWiring) map { _.getBundle }
              }.toSet
            }
          process(processed + b, rest ++ (direct diff processed))
        }
      }
    }
    val result = process(Set.empty, Set(bundle))
    removeSystemBundle(result)
  }
}