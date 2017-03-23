package jumpmicro.jmcloner.impl.idris

import java.io.File
import javax.script.{ScriptEngine, ScriptEngineManager}

import jumpmicro.shared.util.akkaosgi.MyBundleDelegatingClassLoader
import org.osgi.framework.BundleContext

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

// Proof that Idris code can run from Scala, refer to Main.idr
object TestIdris {


  def test(context: BundleContext) = {
    val classloader = MyBundleDelegatingClassLoader(context, Some(getClass.getClassLoader))
    import collection.JavaConverters._

    try {
      val methods = classloader.findClass("jumpmicro.jmcloner.impl.idris.JBar").getMethods.toSeq


      for (m <- methods) {
        if (m.getName == "pythag")
          println(m.invoke(null, new java.lang.Integer(5)))
      }
    } catch {
      case ex: ClassNotFoundException => { }
    }

    //println(JBar.pythag(5))
  }
}
