import java.io.File
import java.util.jar.JarFile

import net.virtualvoid.sbt.graph.{Module, ModuleGraph, ModuleId}
import org.osgi.framework.Constants
import osgifelix.{ManifestInstructions, OsgiDependency}

import scala.annotation.tailrec
import scala.collection.immutable.HashSet
import scala.xml.XML

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

import com.typesafe.sbt.osgi.OsgiKeys._
import osgifelix.OsgiFelixPlugin.autoImport._
import sbt.Keys._



// ScalaJS builds from Scala code to Javascript code so therefore it does not get involved in the OSGi process.
// Its dependencies are un-related to OSGi.

lazy val \\ = File.separator

def subPackagesOf(path: String): Seq[String] = {
  def recursiveListFiles(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }
  val file = new File("." + \\ + "src" + \\ + "main" + \\ + "scala" + \\ + path.replace('.','/'))
  if (file.exists()) {
    val allFiles = recursiveListFiles(file)
    val allNonEmptyDirectories = (for (f <- allFiles; if f.getParentFile.isDirectory) yield f.getParentFile).distinct
    val result: Seq[String] = for (f <- allNonEmptyDirectories; if f.isDirectory) yield {
      f.getPath.replace("." + \\ + "src" + \\ + "main" + \\ + "scala" + \\, "").replace("\\", ".")
    }
    Seq(path) ++ result
  } else Seq()
}

lazy val privatePackages: Seq[String] = subPackagesOf("sangria")

lazy val resourcePackages: Seq[String] = Seq("js", "static.bootstrap.css", "static.bootstrap.js",
  "static.jquery", "static.tether.dist.css", "static.tether.dist.js")

// @feature directory scalajs
// @feature start scalajs
/*
lazy val scalaJsProject = (project in file("scalajs")).settings(
  scalaVersion := "2.11.8",
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.8.1",
    "com.lihaoyi" %%% "scalatags" % "0.5.2",
    "com.lihaoyi" %%% "scalarx" % "0.2.8",
    "be.doeraene" %%% "scalajs-jquery" % "0.8.0",
    "com.lihaoyi" %%% "upickle" % "0.3.4",
    "com.lihaoyi" %%% "utest" % "0.3.0" % "test"
  )
).enablePlugins(ScalaJSPlugin)

lazy val rootProject = project.in(file(".")).aggregate(scalaJsProject)
*/
// @feature end scalajs

osgiSettings

defaultSingleProjectSettings

val projectName = "JMResourceRegistry"
name := projectName

// This OSGi bundle version
bundleVersion := "1.0.0"

lazy val bundleLicense = "https://opensource.org/licenses/MIT"
lazy val bundleName = "JumpMicro Sangria Graph QL"
lazy val bundleDescription = ""
lazy val bundleDocURL = ""
lazy val bundleCategory = "" // keywords,go,here
lazy val bundleVendor = ""
lazy val bundleContactAddress = ""
lazy val bundleCopyright = ""

additionalHeaders := Map("Bundle-License" -> bundleLicense,
  "Bundle-Name" -> bundleName,
  "Bundle-Description" -> bundleDescription,
  "Bundle-DocURL" -> bundleDocURL,
  "Bundle-Category" -> bundleCategory,
  "Bundle-Vendor" -> bundleVendor,
  "Bundle-ContactAddress" -> bundleContactAddress,
  "Bundle-Copyright" -> bundleCopyright
)

scalaVersion := "2.11.8"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
  // This bintray repo is for Neo4J OGM OSGi https://github.com/PhilAndrew/neo4j-ogm-osgi
  Resolver.bintrayIvyRepo(owner = "philandrew", repo = "org.philandrew"))

lazy val JUMPMICRO_DOT = "jumpmicro."

lazy val exportPackages = Seq(JUMPMICRO_DOT + "shared.resourceshare")

// Versions of libraries in use

val slf4jVersion = "1.7.22"     // slf4j logging
val camelVersion = "2.18.2"     // http://camel.apache.org/download.html
val akkaVersion = "2.4.16"      // http://akka.io/downloads/
val akkaHttpVersion = "10.0.3"  // Akka Http library
val catsVersion = "0.9.0"       // https://github.com/typelevel/cats
val shapelessVersion = "2.3.2"  // https://github.com/milessabin/shapeless

lazy val karafDepsMustBeJarFiles = Seq(//"org.neo4j.driver/neo4j-java-driver", // org.neo4j.driver/neo4j-java-driver/1.0.5
                      "universe/neo4j-ogm-osgi_2.11", // universe/neo4j-ogm-osgi_2.11/1.4.38
                      "org.scaldi/scaldi_2.11", // org.scaldi/scaldi_2.11/0.5.8
                      "org.http4s/blaze-core_2.11",
                      "org.http4s/blaze-http_2.11",
                      "org.http4s/http4s-websocket_2.11")

// Dependencies
// All dependencies take the form of OsgiDependency due to the fact that we need to declare not only
// the SBT dependency such as "com.lihaoyi" %% "scalatags" % "0.6.1" but we also need to specify what
// bundle name we are going to import AND/OR what packages we are going to import.
// The name parameter is not used, only for documentation purposes.
// When should I use Import-Package and when should I use Require-Bundle?
// http://stackoverflow.com/questions/1865819/when-should-i-use-import-package-and-when-should-i-use-require-bundle
lazy val OsgiDependencies = Seq[OsgiDependency](

  OsgiDependency("UUID",
    Seq("io.jvm.uuid" %% "scala-uuid" % "0.2.2"),
    Seq(),
    Seq("io.jvm.uuid")),

  OsgiDependency("Sangria",
    Seq("org.parboiled" %% "parboiled" % "2.1.3",
      "org.sangria-graphql" % "sangria-marshalling-api_2.11" % "1.0.0",
      "org.sangria-graphql" % "sangria-streaming-api_2.11" % "1.0.0"),
    Seq(),
    Seq("org.parboiled2", "sangria.streaming", "sangria.marshalling")),

  OsgiDependency("Korolev",
      Seq(
        "biz.enef" %% "slogging" % "0.5.3",
        "biz.enef" %% "slogging-slf4j" % "0.5.3",
        "com.github.fomkin" %% "korolev" % "0.2.2",
        "com.github.fomkin" %% "korolev-server" % "0.2.2",
        "com.github.fomkin" %% "korolev-server-blaze" % "0.2.2",
        "org.eclipse.jetty.alpn" % "alpn-api" % "1.1.3.v20160715",
        "org.http4s" %% "blaze-http" % "0.12.4"),
    Seq(
      s"$projectName.http4s-websocket_2.11", s"$projectName.blaze-core_2.11", s"$projectName.blaze-http_2.11"),
    Seq("org.log4s", "korolev", "korolev.server", "korolev.blazeServer", "bridge", "slogging")),

  OsgiDependency("Log4s",
    Seq("org.log4s" %% "log4s" % "1.3.4"),
    Seq(),
    Seq("org.log4s")),

  // ScalaTags
  // http://www.lihaoyi.com/scalatags/
  OsgiDependency(
    "ScalaTagsDependency",
    // sbt dependencys
    Seq("com.lihaoyi" %% "scalatags" % "0.6.1"),
    // bundle requirements
    Seq(),
    // package requirements
    Seq("scalatags", "scalatags.text")
  ),

  OsgiDependency("DeclarativeServicesDependency",
    Seq(// Required for Declarative Services
      // However for DS to work you need to install and run another bundle before this one
      // http://stackoverflow.com/questions/16707784/using-an-embedded-osgi-container
      // * http://njbartlett.name/2015/08/17/osgir6-declarative-services.html
      // * http://felix.apache.org/documentation/subprojects/apache-felix-service-component-runtime.html
      "org.apache.felix" % "org.apache.felix.scr" % "2.0.6"
      //  "org.apache.felix" % "org.apache.felix.scr.annotations" % "1.12.0",
      //  "org.apache.felix" % "org.apache.felix.scr.generator" % "1.8.0", // Dependancy of annotations above line
      //  "org.apache.felix" % "org.apache.felix.scr.ds-annotations" % "1.2.8",
    ),
    Seq(),
    Seq()
  ),

  OsgiDependency("DominoOsgiDependency",
    Seq(  // Domino OSGi
      // https://www.helgoboss.org/projects/domino/user-guide
      "com.github.domino-osgi" % "domino_2.11" % "1.1.1"
    ),
    Seq(),
    Seq("domino")
  ),

  OsgiDependency("CamelCoreDependency",
    Seq("org.osgi" % "org.osgi.compendium" % "5.0.0", // Required for camel-core-osgi
      "org.apache.camel" % "camel-core-osgi" % camelVersion,
      // Scala DSL for Camel
      "org.scala-lang.modules" %% "scala-xml" % "1.0.4", // Required by camel-scala
      "org.apache.camel" % "camel-scala" % camelVersion),
    Seq("org.apache.camel.camel-core-osgi",
      "org.apache.camel.camel-scala"
    ),
    Seq()
  ),

  OsgiDependency("MonixCoreDependency",
    Seq(  // Monix https://monix.io/
      //"io.monix" %% "monix" % "2.2.1",
      "io.monix" %% "monix-cats" % "2.2.1"
    ),
    Seq(), // @todo Monix is untested to work
    Seq()
  ),

  OsgiDependency("AkkaCamelDependency",
    Seq("com.typesafe.akka" %% "akka-camel" % akkaVersion),
    Seq("com.typesafe.akka.camel"),
    Seq()
  ),

  OsgiDependency("AkkaDependency",
    Seq(  // Akka
      "com.typesafe.akka" %% "akka-osgi" % akkaVersion,
      //"com.typesafe.akka" %% "akka-actor" % akkaVersion,
      //"com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
    ),
    Seq("com.typesafe.akka.osgi"),
    Seq("akka.http", "akka.http.scaladsl.server", "akka.http.scaladsl")
  ),

  OsgiDependency("Neo4JDependency",
    Seq("universe" % "neo4j-ogm-osgi_2.11" % "1.4.39"),
    Seq(),
    Seq("org.neo4j.ogm",
      "org.neo4j.ogm.compiler",
      "org.neo4j.ogm.config",
      "org.neo4j.ogm.session",
      "org.neo4j.ogm.transaction",
      "org.neo4j.ogm.drivers.bolt.driver",
      "org.neo4j.ogm.service",
      "org.neo4j.ogm.annotation")
  ),

  OsgiDependency("ScaldiDependency",
    // Scala Dependency Injection
    // http://scaldi.org/
    Seq("org.scaldi" %% "scaldi" % "0.5.8"), Seq(), Seq("scaldi")
  ),

  OsgiDependency("CamelDependency",
    Seq(// Camel components
      //"org.apache.camel" % "camel-ssh" % camelVersion,
      "org.apache.camel" % "camel-ftp" % camelVersion,
      "org.apache.camel" % "camel-exec" % camelVersion,
      "org.apache.camel" % "camel-stream" % camelVersion),
    // If adding other camel package then add it here like the following:
    //"org.apache.camel.camel-ssh",
    //"org.apache.camel.camel-jsch",),)
    Seq("org.apache.camel.camel-exec",
      //"org.apache.camel.camel-ssh",
      "org.apache.camel.camel-ftp",
      "org.apache.camel.camel-stream"),
    Seq()
  ),

  OsgiDependency("CatsDependency",
    // Cats https://github.com/typelevel/cats
    // Un-tested to work
    Seq("org.typelevel" %% "cats-core" % catsVersion),
    Seq(),
    Seq()
  ),

  OsgiDependency("ShapelessDependency",
    // Shapeless https://github.com/milessabin/shapeless
    // Un-tested to work
    Seq("org.typelevel" % "macro-compat_2.11" % "1.1.1",
      "org.scala-lang" % "scala-reflect" % "2.11.8",
      "org.scala-lang" % "scala-compiler" % "2.11.8",
      "com.chuusai" %% "shapeless" % shapelessVersion),
    Seq(),
    Seq()
  ),

  OsgiDependency("ConfigDependency",
    Seq(  // https://github.com/kxbmap/configs
      "com.github.kxbmap" %% "configs" % "0.4.4"
    ),
    Seq(),
    Seq()
  ),

  OsgiDependency("Acylic",
    Seq("com.lihaoyi" %% "acyclic" % "0.1.7"),
    Seq(),
    Seq("acyclic"))
  // https://github.com/erikvanoosten/metrics-scala
  /*OsgiDependency("MetricsScalaDependency",
    Seq("org.mpierce.metrics.reservoir" % "hdrhistogram-metrics-reservoir" % "1.1.0", // required for metrics-scala
      "org.hdrhistogram" % "HdrHistogram" % "2.1.9",
      "nl.grons" %% "metrics-scala" % "3.5.5"), Seq(), Seq(
      "nl.grons.metrics.scala"
    )
  )*/
)

lazy val dependencys = OsgiDependencies.map(_.sbtModules)







































// ***********************************************************************************************************************************************
// ***********************************************************************************************************************************************
// General sbt settings

// http://stackoverflow.com/questions/5137460/sbt-stop-run-without-exiting
//fork in run := true

cancelable in Global := true

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

// "-P:acyclic:force"
scalacOptions ++= Seq(
  "-target:jvm-1.8",
  "-encoding", "UTF-8",
  "-unchecked",
  "-deprecation",
  "-Xfuture",
  //"-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Ywarn-unused"
)

// https://github.com/HairyFotr/linter
//addCompilerPlugin("org.psywerx.hairyfotr" %% "linter" % "0.1-SNAPSHOT")

// https://github.com/Tapad/sbt-docker-compose
enablePlugins(JavaAppPackaging, DockerComposePlugin)

dockerImageCreationTask := (publishLocal in Docker).value

initialize := {
  val _ = initialize.value
  if (sys.props("java.specification.version") != "1.8")
    sys.error("Java 8 is required for this project.")
}

// Copy paste detector https://github.com/sbt/cpd4sbt
enablePlugins(CopyPasteDetector)

// Acyclic, prevents circular dependencies.
// https://github.com/lihaoyi/acyclic

autoCompilerPlugins := true

addCompilerPlugin("com.lihaoyi" %% "acyclic" % "0.1.7")
// END - Acyclic, prevents circular dependencies.

// @feature start scalajs

// ***********************************************************************************************************************************************
// ***********************************************************************************************************************************************
// ScalaJS compile Scala to Javascript
/*
lazy val packageScalaJsResource = taskKey[Unit]("Package ScalaJS")

packageScalaJsResource := {
  println("Package ScalaJs")

  val scalaJsPath = "scalajs" + \\ + "target" + \\ + "scala-2.11" + \\
  val destPath = "src" + \\ + "main" + \\ + "resources" + \\ + "js" + \\
  val dest = new File(destPath)
  dest.delete()
  dest.mkdir()
  Seq("scalajsproject-fastopt.js", "scalajsproject-jsdeps.js").foreach(f => IO.copyFile(new File(scalaJsPath + f), new File(destPath + f), true))
}

compile in Compile <<= (compile in Compile).dependsOn(packageScalaJsResource)

// http://stackoverflow.com/questions/30513492/sbt-in-a-multi-project-build-how-to-invoke-project-bs-task-from-project-a
compile in Compile <<= (compile in Compile).dependsOn(fastOptJS in Compile in scalaJsProject)
*/
// @feature end scalajs

// @feature idris directory src/main/idris

// @feature start idris

// ***********************************************************************************************************************************************
// ***********************************************************************************************************************************************
// Compile Idris to Java classes

lazy val compileIdris = taskKey[Unit]("Compile Idris")

compileIdris := {
  def walkTree(file: File): Iterable[File] = {
    val children = new Iterable[File] {
      def iterator = if (file.isDirectory) file.listFiles.iterator else Iterator.empty
    }
    Seq(file) ++: children.flatMap(walkTree(_))
  }

  def latestModified(file: File): Long = {
    walkTree(file).toSeq.map(_.lastModified()).max
  }

  val idrisTimestamp = new File("target" + \\ + "idrisbuild.timestamp")

  val idrisSrc = "src" + \\ + "main" + \\ + "idris"

  // What is the most recently changed file in that directory
  val lastModifiedTimeStamp = idrisTimestamp.lastModified()
  val lastModifiedInSrc = latestModified(new File(idrisSrc))
  if (lastModifiedInSrc > lastModifiedTimeStamp) {
    println("Compile Idris")
    import sys.process._
    IO.delete(new File("target" + \\ + "idrisclass"))
    IO.createDirectory(new File("target" + \\ + "idrisclass"))

    val exec = scala.util.Properties.envOrElse("JUMPMICRO_IDRISJVM_COMPILER_PATH", "c:" + \\ + "home" + \\ + "projects" + \\ + "git" + \\ + "idris-jvm" + \\ + "bin" + \\ + "idrisjvm.bat")
    val dest = "." + \\ + "target" + \\ + "idrisclass"
    val command: String = exec + " --interface --cg-opt --interface ." + \\ +
      "src" + \\ + "main" + \\ + "idris" + \\ + "Main.idr -i \"." + \\ +
      "src" + \\ + "main" + \\ + "idris\" -o " + dest

    var result: String = null
    try {
      if (new File(exec).exists())
        result = command !!;
    } catch {
      case ex: java.io.IOException => {
        result = null
      }
    }

    if ((result == null) || (result.indexOf("FAILURE:") == 0)) {
      println("ERROR: Idris to Java (Idris JVM) compiler requires a server running, check at https://github.com/mmhelloworld/idris-jvm to find out how to install Idris JVM")
      println("ERROR: Note that this MicroService will still continue to work without Idris")
    } else {
      // Copy classes from idrisclass to target/scala-2.11/classes
      //IO.delete(new File("target" + File.separator + "idrisclass" + File.separator + "main"))
      //IO.createDirectory(new File("target" + File.separator + "idrisclass" + File.separator + "main"))
      IO.copyDirectory(new File("target" + \\ + "idrisclass"), new File("target" + \\ + "scala-2.11" + \\ + "classes"), true, true)
      IO.delete(new File("target" + \\ + "idrisclass"))
      IO.delete(new File("target" + \\ + "scala-2.11" + \\ + "classes" + \\ + projectName.toLowerCase))
    }
  }

  idrisTimestamp.delete()
  idrisTimestamp.createNewFile()
}

cleanFiles += file("target" + \\ + "idrisclass")

//unmanagedClasspath in Compile += baseDirectory.value / "target" / "idrisclass"

compile in Compile <<= (compile in Compile).dependsOn(compileIdris)

// @feature end idris

// OSGi component properties

// The Bundle activator executes when the bundle is loaded or unloaded
// When this OSGi bundle is started the start method is called on this, when stopped the stop method is called
bundleActivator := Some(JUMPMICRO_DOT + name.value.toString.toLowerCase + ".impl." + projectName + "BundleActivator")

// Public packages which are exposed by the OSGi component.
// The root of the Micro Service name should contain exposed traits universe.microservice.microservicescalajs
exportPackage := Seq(JUMPMICRO_DOT + name.value.toString.toLowerCase,
  // The models used by Neo4J OGM must be exposed as public packages to allow Neo4J OGM to read them
  JUMPMICRO_DOT + "shared.model",
  JUMPMICRO_DOT + "shared.bean") ++ exportPackages

// Packages which are to be inside the OSGi component must be listed here as private packages.
// They are not exposed as public packages but are implementation packages inside of the bundle.
// The rule is simple, if a new package is created in this project, at least you must add it to the private packages.
privatePackage := privatePackages ++ resourcePackages ++ subPackagesOf(JUMPMICRO_DOT + name.value.toString.toLowerCase + ".impl") ++
  subPackagesOf(JUMPMICRO_DOT + "shared") ++ Seq(
  JUMPMICRO_DOT + name.value.toString.toLowerCase,
  "mmhelloworld.idrisjvmruntime",
  "Decidable",
  "Prelude",
  "main"
)

lazy val moduleDeps: Seq[ModuleID] = dependencys.flatten

libraryDependencies ++= moduleDeps ++ Seq(
  "com.jcraft" % "jzlib" % "1.1.3",
  "org.scalacheck" %% "scalacheck" % "1.13.4" % "test" // http://www.scalacheck.org/

  // http://www.scalactic.org/
  // ScalaTest http://www.scalatest.org/install
  //"org.scalactic" %% "scalactic" % "3.0.1",
  //"org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

//import com.softwaremill.clippy.ClippySbtPlugin._ // needed in global configuration only
clippyColorsEnabled := false

//addCompilerPlugin("com.softwaremill.clippy" %% "plugin" % "0.5.0" classifier "bundle")

lazy val bundleDepsReqs: Seq[String] = OsgiDependencies.flatMap { (d) => {
  d.moduleRequirements
}
}

lazy val packageDepsReqs: Seq[String] = OsgiDependencies.flatMap { (d) => {
  d.packageRequirements
}
}

osgiDependencies in Compile := bundleReqs(
  bundleDepsReqs ++ Seq():_*
) ++ packageReqs(
  // If the bundle name above is not known we can just specify the packages we want to import:
  packageDepsReqs ++ Seq():_*
)

// This starts the bundles with these symbolic names
osgiDependencies in run := bundleReqs(
)

// This appends these import packages to the end
// I need sun.misc, dont know about the rest
importPackage := Seq(
  //"sun.misc",
  "*"
)

lazy val DeployLauncher = config("deployLauncher")

// ***********************************************************************************************************************************************
// ***********************************************************************************************************************************************
// Karaf task builds a kar file ready for deployment to Karaf

val karafDeployTask = TaskKey[Unit]("karafDeploy", "Deploy Karaf")

karafDeployTask := {
  /*
  a) KARAF_HOME C:\home\software\apache-karaf-4.0.8\apache-karaf-4.0.8
  b) KARAF_DEPLOY C:\home\software\apache-karaf-4.0.8\apache-karaf-4.0.8\deploy
  c) KARAF_JAR_DIRECTORY C:\home\software\apache-karaf-4.0.8\apache-karaf-4.0.8\jars
   */

  val karafHome = scala.util.Properties.envOrElse("KARAF_HOME", "C:\\home\\software\\apache-karaf-4.0.8\\apache-karaf-4.0.8")
  val karafDeploy = scala.util.Properties.envOrElse("KARAF_DEPLOY", "C:\\home\\software\\apache-karaf-4.0.8\\apache-karaf-4.0.8\\deploy")
  val karafJarDirectory = scala.util.Properties.envOrElse("KARAF_JAR_DIRECTORY", "C:\\home\\software\\apache-karaf-4.0.8\\apache-karaf-4.0.8\\jars")

  val dirs = Seq(karafHome, karafDeploy, karafJarDirectory)
  dirs.foreach(d => if (! new File(d).exists()) IO.createDirectory(new File(d)))

  // Copy the jars across, kars in
  val jarFolder = new File("." + \\ + "target" + \\ + "karaf" + \\)
  IO.listFiles(jarFolder).toSeq.foreach( (source) => if (source.ext=="jar") IO.copyFile(source, new File(karafJarDirectory + \\ + source.getName)))

  val featuresXml = IO.readLines(new File("." + \\ + "target" + \\ + "karaf" + \\ + "features.xml"))
  // <bundle>file:/C:/home/projects/git/JumpMicro/JMScalaJs/target/bundles/scaldi_2.11-0.5.8.jar</bundle>
  val newFeaturesXml = for (line <- featuresXml) yield {
    if (line.indexOf("<bundle>file:") > 0) {
      val startIndex = line.indexOf("<bundle>") + "<bundle>".length
      // @todo get the file name from this
      val endIndex = line.indexOf("</bundle>")
      val filePart = line.substring(startIndex, endIndex)
      // Find last /
      val lastSlash = filePart.lastIndexOf(\\) + 1
      val jarFile = filePart.substring(lastSlash)
      "        <bundle>" + karafJarDirectory + \\ + jarFile + "</bundle>"
      line
    } else line
  }

  // Write new features xml
  val dest = new File(karafDeploy + \\ + projectName + ".xml")
  if (dest.exists()) dest.delete()
  IO.writeLines(dest, newFeaturesXml)
}

val karafBuildTask = TaskKey[Unit]("karafBuild", "Build Karaf features")

karafBuildTask <<= (moduleGraph in Compile) map { (m: ModuleGraph) =>

  val allModules: Seq[Module] = m.nodes
  val dependencyMap: Map[net.virtualvoid.sbt.graph.ModuleId, Seq[Module]] = m.dependencyMap

  import collection.JavaConverters._

  def isModuleWrap(m: Module): Boolean = {
    val opt: Option[Option[String]] = for (j <- m.jarFile) yield {
      val file: File = j
      val mf = new JarFile(file.getCanonicalPath).getManifest()
      val sym = mf.getMainAttributes.getValue("Bundle-SymbolicName")
      val symOption: Option[String] = if (sym==null) None else {
        Some(sym).filterNot(_.isEmpty)
      }
      symOption
    }
    if (opt.isDefined) opt.get.isEmpty else false
  }

  val ignoredModules = HashSet(
    "org.osgi/org.osgi.core",
    "org.osgi/org.osgi.compendium"
  )

  // Some modules do not work in Karaf
  def removeModulesWhichDoNotWorkInKaraf(modules: Seq[Module]): Seq[Module] = {
    modules.filter(m => {
      ! ignoredModules.contains(Seq(m.id.organisation, m.id.name).mkString("/"))
    })
  }
  def dependentsOf(module: Module): Set[Module] = {
    def recursiveFetch(moduleId: ModuleId): Seq[Module] = {
      // Find all deps
      val allDeps = dependencyMap(moduleId).filter(! _.isEvicted)
      val result = for (dep <- allDeps) yield recursiveFetch(dep.id)
      result.flatten ++ allDeps
    }
    recursiveFetch(module.id).toSet
  }

  def findModulesToRemoveAsTheyAreInvolvedInKarafFeatures(modules: Seq[Module]): Seq[Module] = {
    val topLevelModulesToRemove: Set[Module] = modules.map(module => {
      if (module.id.organisation == "org.apache.camel") {
        if (module.id.name == "camel-core-osgi") {
          // Ignore this case, keep the module and do not remove any
          Seq()
        } else {
          // Remove the module
          Seq(module)
        }
      } else {
        Seq()
      }}).flatten.toSet

    val keepDependents: Set[Module] = (modules.toSet.diff(topLevelModulesToRemove)).flatMap(dependentsOf)
    val removeDependents: Set[Module] = topLevelModulesToRemove.flatMap(dependentsOf)
    val modulesToRemove = topLevelModulesToRemove ++ removeDependents.diff(keepDependents)
    modulesToRemove.toSeq
  }

  def karafFeaturesOfModules(modules: Seq[Module]): Seq[String] = {
    modules.map(m => {
      if (m.id.organisation == "org.apache.camel") {
        Some(m.id.name)
      } else None
    }).flatten ++ Seq("camel")
  }

  def karafDepsMustBeJarsFilesPlusMain: Seq[String] = {
    karafDepsMustBeJarFiles ++ Seq(projectName.toLowerCase + "/" + projectName.toLowerCase + "_2.11/0.1-SNAPSHOT")
  }

  def getMustBeFileOf(module: Module): Option[Module] = {
    val startsWith = module.id.organisation + "/" + module.id.name
    if (karafDepsMustBeJarsFilesPlusMain.exists((s) => s.indexOf(startsWith) >= 0)) {
      Some(module)
    } else None
  }

  def modulesWhichMustBeOsgiBundleFilesNotFeatures(modules: Seq[Module]): Seq[Module] = {
    for (module <- modules;
         mustBeFile <- getMustBeFileOf(module)) yield module
  }

  def getJarFilesInBundles(mustBeFiles: Seq[Module]): Seq[File] = {
    for (f <- mustBeFiles;
         jarFile <- f.jarFile) yield
      new File("." + \\ + "target" + \\ + "bundles" + \\ + jarFile.getName)
  }

  val modulesWithEvictedOnesRemoved = allModules.filter(! _.isEvicted)
  val modulesToRemove = findModulesToRemoveAsTheyAreInvolvedInKarafFeatures(modulesWithEvictedOnesRemoved)
  val featuresToAdd = karafFeaturesOfModules(modulesToRemove)
  val modulesMinusTheRemovedOnes = modulesWithEvictedOnesRemoved.diff(modulesToRemove)
  val modulesNotIgnored = removeModulesWhichDoNotWorkInKaraf(modulesMinusTheRemovedOnes)
  val mustBeFiles = modulesWhichMustBeOsgiBundleFilesNotFeatures(modulesNotIgnored)
  val modulesWithoutMustBeFile = modulesNotIgnored.diff(mustBeFiles)
  val jarFilesInBundles = getJarFilesInBundles(mustBeFiles)
  val modulesWithWrap = modulesWithoutMustBeFile.map(m => (m, isModuleWrap(m)))

  val features =
    <features xmlns="http://karaf.apache.org/xmlns/features/v1.4.0" name={projectName}>
      <repository>mvn:org.apache.camel.karaf/apache-camel/2.18.2/xml/features</repository>
      <feature description={projectName} version="0.1.0" name={projectName} install="auto">
        <feature prerequisite="true" dependency="false">wrap</feature>
        { featuresToAdd.map(f => {
        <feature>{f}</feature>
      }
      ) }
        { modulesWithWrap.map( (mod) => {
        val m = mod._1
        val dep = Seq(m.id.organisation, m.id.name, m.id.version).mkString("/")
        <bundle>{ s"${if (mod._2) "wrap:" else ""}mvn:$dep" }</bundle>
      })
        }
        {
        jarFilesInBundles.map( (file) => {
          <bundle>{ "file:/" + file.getCanonicalPath }</bundle>
        })
        }
        {
        // @todo Should it be file:/ or file:
        for (m <- mustBeFiles; if m.jarFile.isEmpty) yield {
          // jmscalajs_2.11-0.1-SNAPSHOT.jar
            <bundle>{ "file:/" + new File("." + \\ + "target" + \\ + "scala-2.11" + \\ + m.id.name + "-" + m.id.version + ".jar").getCanonicalPath }</bundle>
        }

        }
      </feature>
    </features>

  val karafDir = new File("." + \\ + "target" + \\ + "karaf")
  val karDirPath = "." + \\ + "target" + \\ + "karaf"
  val karafKarDir = new File(karDirPath)
  IO.delete(karafDir)
  IO.createDirectory(karafDir)
  IO.createDirectory(karafKarDir)

  for (j <- jarFilesInBundles) IO.copyFile(j, new File(karDirPath + "/" + j.getName))
  for (m <- mustBeFiles; if m.jarFile.isEmpty) {
    val file = new File("." + \\ + "target" + \\ + "scala-2.11" + \\ + m.id.name + "-" + m.id.version + ".jar")
    IO.copyFile(file, new File(karDirPath + \\ + file.getName))
  }

  val p = new scala.xml.PrettyPrinter(1000, 4)
  val outputString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + p.format(features)
  IO.write(new File(karDirPath + \\ + "features.xml"), outputString)
}