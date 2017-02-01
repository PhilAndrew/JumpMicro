import java.io.File

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

import com.typesafe.sbt.osgi.OsgiKeys._
import osgifelix.OsgiFelixPlugin.autoImport._
import sbt.Keys._

defaultSingleProjectSettings

name := "JMScalaJS"

// This OSGi bundle version
bundleVersion := "1.0.0"

scalaVersion := "2.11.8"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"))

// Versions of libraries in use

val slf4jVersion = "1.7.22"     // ?
val camelVersion = "2.18.2"     // http://camel.apache.org/download.html
val akkaVersion = "2.4.16"      // http://akka.io/downloads/
val akkaHttpVersion = "10.0.3"  // ?
val catsVersion = "0.9.0"       // https://github.com/typelevel/cats
val shapelessVersion = "2.3.2"  // https://github.com/milessabin/shapeless

// Dependencys are the SBT dependency followed by the package import statements for OSGi


// ScalaTags
// http://www.lihaoyi.com/scalatags/
lazy val ScalaTagsDependency: Seq[Seq[_]] = Seq(
  // sbt dependencys
  Seq[ModuleID]("com.lihaoyi" %% "scalatags" % "0.6.1"),
  // bundle requirements
  Seq(),
  // package requirements
  Seq[String]("scalatags", "scalatags.text")
)

lazy val Slf4jDependency = Seq(
  Seq("org.slf4j" % "slf4j-api" % slf4jVersion,
    "org.slf4j" % "slf4j-simple" % slf4jVersion,
    "org.slf4j" % "jcl-over-slf4j" % slf4jVersion,
    "org.slf4j" % "log4j-over-slf4j" % slf4jVersion),
  Seq(),
  Seq()
)

lazy val DeclarativeServicesDependency = Seq(
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
)

lazy val DominoOsgiDependency = Seq(
  Seq(  // Domino OSGi
    // https://www.helgoboss.org/projects/domino/user-guide
    "com.github.domino-osgi" % "domino_2.11" % "1.1.1"
  ),
  Seq(),
  Seq("domino")
)

lazy val CamelCoreDependency = Seq(
  Seq("org.osgi" % "org.osgi.compendium" % "5.0.0", // Required for camel-core-osgi
    "org.apache.camel" % "camel-core-osgi" % camelVersion,
    // Scala DSL for Camel
    "org.scala-lang.modules" %% "scala-xml" % "1.0.4", // Required by camel-scala
    "org.apache.camel" % "camel-scala" % camelVersion),
  Seq("org.apache.camel.camel-core-osgi",
    "org.apache.camel.camel-scala"
  ),
  Seq()
)

lazy val MonixCoreDependency = Seq(
  Seq(  // Monix https://monix.io/
    "io.monix" %% "monix" % "2.2.1",
    "io.monix" %% "monix-cats" % "2.2.1"
  ),
  Seq(), // @todo Monix is untested to work
  Seq()
)

lazy val AkkaCamelDependency = Seq(
  Seq("com.typesafe.akka" %% "akka-camel" % akkaVersion),
  Seq("com.typesafe.akka.camel"),
  Seq()
)

lazy val AkkaDependency = Seq(
  Seq(  // Akka
    "com.typesafe.akka" %% "akka-osgi" % akkaVersion,
    //"com.typesafe.akka" %% "akka-actor" % akkaVersion,
    //"com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
  ),
  Seq("com.typesafe.akka.osgi"),
  Seq("akka.http",
    "akka.http.scaladsl",
    "akka.http.scaladsl.server")
)

lazy val Neo4JDependency = Seq(
  Seq("universe" % "neo4j-ogm-osgi_2.11" % "1.4.36"),
  Seq(),
  Seq("org.neo4j.ogm",
    "org.neo4j.ogm.compiler",
    "org.neo4j.ogm.config",
    "org.neo4j.ogm.session",
    "org.neo4j.ogm.transaction",
    "org.neo4j.ogm.drivers.bolt.driver",
    "org.neo4j.ogm.service",
    "org.neo4j.ogm.annotation")
)

lazy val ScalaLoggingDependency = Seq(
  Seq(  // Logging
    // https://github.com/typesafehub/scala-logging
    "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"
  ), Seq(), Seq("com.typesafe.scalalogging")
)

lazy val ScaldiDependency = Seq(
  // Scala Dependency Injection
  // http://scaldi.org/
  Seq("org.scaldi" %% "scaldi" % "0.5.8"), Seq(), Seq("scaldi")
)

lazy val CamelDependency = Seq(
  Seq(// Camel components
    "org.apache.camel" % "camel-ssh" % camelVersion,
    "org.apache.camel" % "camel-ftp" % camelVersion,
    "org.apache.camel" % "camel-exec" % camelVersion,
    "org.apache.camel" % "camel-stream" % camelVersion),
    // If adding other camel package then add it here like the following:
    //"org.apache.camel.camel-ssh",
    //"org.apache.camel.camel-jsch",),)
  Seq("org.apache.camel.camel-exec",
    "org.apache.camel.camel-ssh",
    "org.apache.camel.camel-ftp",
    "org.apache.camel.camel-stream"),
  Seq()
)

lazy val CatsDependency = Seq(
  // Cats https://github.com/typelevel/cats
  // Un-tested to work
  Seq("org.typelevel" %% "cats-core" % catsVersion),
  Seq(),
  Seq()
)

lazy val ShapelessDependency = Seq(
  // Shapeless https://github.com/milessabin/shapeless
  // Un-tested to work
  Seq("org.typelevel" % "macro-compat_2.11" % "1.1.1",
  "org.scala-lang" % "scala-reflect" % "2.11.8",
  "org.scala-lang" % "scala-compiler" % "2.11.8",
  "com.chuusai" %% "shapeless" % shapelessVersion),
  Seq(),
  Seq()
)

lazy val ConfigDependency = Seq(
  Seq(  // https://github.com/kxbmap/configs
    "com.github.kxbmap" %% "configs" % "0.4.4"
  ),
  Seq(),
  Seq()
)

lazy val dependencys = Seq(ScalaTagsDependency, Slf4jDependency, DeclarativeServicesDependency, DominoOsgiDependency,
  CamelCoreDependency, MonixCoreDependency, AkkaCamelDependency, AkkaDependency, Neo4JDependency, ScaldiDependency,
  ScalaLoggingDependency, CamelDependency, CatsDependency, ShapelessDependency, ConfigDependency)

// ScalaJS builds from Scala code to Javascript code so therefore it does not get involved in the OSGi process.
// Its dependencies are un-related to OSGi.

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







































javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

scalacOptions += "-deprecation"

// https://github.com/HairyFotr/linter
addCompilerPlugin("org.psywerx.hairyfotr" %% "linter" % "0.1-SNAPSHOT")

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

lazy val packageScalaJsResource = taskKey[Unit]("Package ScalaJS")

packageScalaJsResource := {
  println("Package ScalaJs")

  val scalaJsPath = "scalajs" + File.separator + "target" + File.separator + "scala-2.11" + File.separator
  val destPath = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "js" + File.separator
  val dest = new File(destPath)
  dest.delete()
  dest.mkdir()
  Seq("scalajsproject-fastopt.js", "scalajsproject-jsdeps.js").foreach(f => IO.copyFile(new File(scalaJsPath + f), new File(destPath + f), true))
}

compile in Compile <<= (compile in Compile).dependsOn(packageScalaJsResource)

// http://stackoverflow.com/questions/30513492/sbt-in-a-multi-project-build-how-to-invoke-project-bs-task-from-project-a
compile in Compile <<= (compile in Compile).dependsOn(fastOptJS in Compile in scalaJsProject)

// In scalajs\target\scala-2.11\ there is scalajsproject-fastopt.js and scalajsproject-fastopt.js.map and scalajsproject-jsdeps.js

// http://stackoverflow.com/questions/30011826/intellij-not-compiling-scala-project
// https://github.com/jboner/akka-training/issues/1
//scalaVersion := Option(System.getProperty("scala.version")).getOrElse("2.12.0")

// Compile Idris to Java classes

lazy val compileIdris = taskKey[Unit]("Compile Idris")

compileIdris := {
  println("Compile Idris")

  import sys.process._
  IO.createDirectory(new File("target" + File.separator + "idrisclass"))

  val exec = scala.util.Properties.envOrElse("JUMPMICRO_IDRISJVM_COMPILER_PATH", "/home/projects/git/idris-jvm/bin/idrisjvm.bat")
  val dest = "." + File.separator + "target" + File.separator + "idrisclass"
  val command = exec + " --interface --cg-opt --interface ." + File.separator +
    "src" + File.separator + "main" + File.separator + "idris" + File.separator + "Main.idr -i \"." + File.separator +
    "src" + File.separator + "main" + File.separator + "idris\" -o " + dest
  val result = command !!;
  if (result.indexOf("FAILURE:") == 0) println("ERROR: Idris to Java (Idris JVM) compiler requires a server running, check at https://github.com/mmhelloworld/idris-jvm to find out how to install Idris JVM")

  // Copy classes from idrisclass to target/scala-2.11/classes
  IO.copyDirectory(new File("target" + File.separator + "idrisclass"), new File("target" + File.separator + "scala-2.11" + File.separator + "classes"), true, true)
}

cleanFiles += file("target" + File.separator + "idrisclass")

unmanagedClasspath in Compile += baseDirectory.value / "target" / "idrisclass"

compile in Compile <<= (compile in Compile).dependsOn(compileIdris)

// OSGi component properties

lazy val JUMPMICRO_DOT = "jumpmicro."

// The Bundle activator executes when the bundle is loaded or unloaded
// When this OSGi bundle is started the start method is called on this, when stopped the stop method is called
bundleActivator := Some(JUMPMICRO_DOT + name.value.toString.toLowerCase + ".impl.MicroServiceScalaJsBundleActivator")

// Public packages which are exposed by the OSGi component.
// The root of the Micro Service name should contain exposed traits universe.microservice.microservicescalajs
exportPackage := Seq(JUMPMICRO_DOT + name.value.toString.toLowerCase,
  // The models used by Neo4J OGM must be exposed as public packages to allow Neo4J OGM to read them
  JUMPMICRO_DOT + "shared.model",
  JUMPMICRO_DOT + "shared.bean")

def subPackagesOf(path: String): Seq[String] = {
  def recursiveListFiles(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }
  val file = new File("./src/main/scala/" + path.replace('.','/'))
  val r = recursiveListFiles(file)
  val result: Seq[String] = for (f <- r; if f.isDirectory) yield {
    f.getPath.replace(".\\src\\main\\scala\\", "").replace("\\", ".")
  }
  Seq(path) ++ result
}

// Packages which are to be inside the OSGi component must be listed here as private packages.
// They are not exposed as public packages but are implementation packages inside of the bundle.
// The rule is simple, if a new package is created in this project, at least you must add it to the private packages.
privatePackage := subPackagesOf(JUMPMICRO_DOT + name.value.toString.toLowerCase + ".impl") ++ Seq(
  JUMPMICRO_DOT + name.value.toString.toLowerCase,
  JUMPMICRO_DOT + "shared.model",
  JUMPMICRO_DOT + "shared.bean",
  JUMPMICRO_DOT + "shared.util.akkaosgi",
  JUMPMICRO_DOT + "shared.util.boilerplate",
  JUMPMICRO_DOT + "shared.util.neo4j",
  JUMPMICRO_DOT + "shared.util.osgi",
  "mmhelloworld.idrisjvmruntime",
  "microservicescalajs",
  "Decidable",
  "Prelude",
  "main"
)

lazy val moduleDeps: Seq[ModuleID] = dependencys.flatMap(_.head.asInstanceOf[Seq[ModuleID]])

libraryDependencies ++= moduleDeps ++ Seq(
  "com.jcraft" % "jzlib" % "1.1.3",
  "org.scalacheck" %% "scalacheck" % "1.13.4" % "test", // http://www.scalacheck.org/

  // http://www.scalactic.org/
  // ScalaTest http://www.scalatest.org/install
  "org.scalactic" %% "scalactic" % "3.0.1",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

//import com.softwaremill.clippy.ClippySbtPlugin._ // needed in global configuration only
clippyColorsEnabled := false

//addCompilerPlugin("com.softwaremill.clippy" %% "plugin" % "0.5.0" classifier "bundle")

lazy val bundleDepsReqs: Seq[String] = dependencys.flatMap { (d) => {
  d.apply(1).asInstanceOf[Seq[String]]
}
}

lazy val packageDepsReqs: Seq[String] = dependencys.flatMap { (d) => {
  d.apply(2).asInstanceOf[Seq[String]]
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
  "sun.misc",
  "*"
)

lazy val DeployLauncher = config("deployLauncher")

osgiRepositoryRules := Seq(
  // Required to allow Neo4J OGM OSGi to "see" the model packages exposed by this OSGi bundle.
  //rewriteCustom("neo4j-ogm-osgi_2.11", ManifestInstructions(extraProperties = Map("DynamicImport-Package" -> "*")))
  // @todo Add boot delegation here? https://github.com/doolse/sbt-osgi-felix/pull/2
  //Some(Constants.FRAMEWORK_BOOTDELEGATION -> "sun.misc")
)
















// for JSON4S "com.thoughtworks.paranamer"
// For Json4S
// https://mvnrepository.com/artifact/com.thoughtworks.paranamer/paranamer
//"com.thoughtworks.paranamer" % "paranamer" % "2.8",

//"org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4"

//"com.typesafe.akka" %% "akka-remote" % akkaVersion,

//"org.apache.camel" % "camel-jsch" % camelVersion,
//"org.apache.felix",
//"org.apache.felix.scr.annotations",
//"org.apache.felix.scrplugin.processing",
//"org.osgi.service.component"
//"org.apache.felix.scr"
//additionalHeaders := Map("Service-Component" -> "OSGI-INF/serviceComponents.xml")
// Service-Component
/*
@todo This may be a good idea?
http://stackoverflow.com/questions/28365000/no-configuration-setting-found-for-key-akka
assemblyMergeStrategy in assembly := {
  case PathList("reference.conf") => MergeStrategy.concat
}
*/
