// @feature start commonheader
import java.io.File
import java.security.MessageDigest
import java.util.jar.JarFile

import net.virtualvoid.sbt.graph.{Module, ModuleGraph, ModuleId}
import osgifelix.{ManifestInstructions, OsgiDependency}
import sbt.File

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

lazy val \\ = java.io.File.separator

def recursiveListFiles(f: File): Array[File] = {
  if (f.exists()) {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  } else Array[File]()
}

def subPackagesOf(path: String): Seq[String] = {
  def subPackagesOfImpl(prefix: String, path: String): Seq[String] = {
    val file = new File(prefix + \\ + path.replace('.','/'))
    if (file.exists()) {
      val allFiles = recursiveListFiles(file)
      val allNonEmptyDirectories = (for (f <- allFiles; if f.getParentFile.isDirectory) yield f.getParentFile).distinct
      val result: Seq[String] = for (f <- allNonEmptyDirectories; if f.isDirectory) yield {
        f.getPath.replace(prefix + \\, "").replace("\\", ".")
      }
      Seq(path) ++ result
    } else Seq()
  }
  val prefixs = Seq("." + \\ + "src" + \\ + "main" + \\ + "scala", "." + \\ + "src" + \\ + "main" + \\ + "java")
  prefixs.flatMap(subPackagesOfImpl(_, path))
}

lazy val JUMPMICRO_DOT = "jumpmicro."
// @feature end commonheader




















// [jumpmicro.shared, jumpmicro.shared.util, Decidable, Prelude, main]

// ScalaJS builds from Scala code to Javascript code so therefore it does not get involved in the OSGi process.
// Its dependencies are un-related to OSGi.

lazy val privatePackages: Seq[String] = subPackagesOf("bridge") ++ subPackagesOf("korolev")

val projectName = "JMCloner"
name := projectName

// This OSGi bundle version
//bundleVersion := "1.0.0"

lazy val thisVersion = "1.0.0"

version := thisVersion

val scalaMajorVersion = "2.11"
val scalaMinorVersion = "8"

scalaVersion := scalaMajorVersion + "." + scalaMinorVersion

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
  )

lazy val exportPackages = Seq()

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
  "io.jvm.uuid/scala-uuid_2.11",
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

  OsgiDependency("Korolov",
    Seq("org.eclipse.jetty.alpn" % "alpn-api" % "1.1.3.v20160715",
      "biz.enef" %% "slogging" % "0.5.2",
      // If this is included then it does not work in Karaf due to Karafs logging "biz.enef" %% "slogging-slf4j" % "0.5.2",
      "org.http4s" % "blaze-core_2.11" % "0.12.4",
      "org.http4s" % "blaze-http_2.11" % "0.12.4"),
    Seq(),
    Seq("org.log4s", "org.http4s.blaze.http", "org.http4s.blaze.http.http20", "org.http4s.blaze.http.util", "org.http4s.blaze.http.websocket",
      "org.http4s.blaze.channel", "org.http4s.blaze.channel.nio2",
      "slogging")),

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











































// @feature start commonfooter
// ***********************************************************************************************************************************************
// ***********************************************************************************************************************************************
// General sbt settings

lazy val dependencys = OsgiDependencies.map(_.sbtModules)

osgiSettings

defaultSingleProjectSettings

// http://stackoverflow.com/questions/5137460/sbt-stop-run-without-exiting
//fork in run := false

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
// @todo Commented out because slow
//enablePlugins(CopyPasteDetector)

// @todo Commented out because slow
// Acyclic, prevents circular dependencies.
// https://github.com/lihaoyi/acyclic

//autoCompilerPlugins := true

//addCompilerPlugin("com.lihaoyi" %% "acyclic" % "0.1.7")
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

// ***********************************************************************************************************************************************
// ***********************************************************************************************************************************************
// Copy shared source

lazy val copySharedSrc = taskKey[Unit]("Copy Shared Src")

copySharedSrc := {
  //val sharedDir = new File("src" + \\ + "main" + \\ + "scala" + \\ + "jumpmicro" + \\ + "shared")
  //IO.delete(sharedDir)
  //IO.copyDirectory(new File(".." + \\ + "JMShared" + \\ + "src" + \\ + "main" + \\ + "scala" + \\ + "jumpmicro" + \\ + "shared"), sharedDir, true, true)
}

// @feature start idris

// ***********************************************************************************************************************************************
// ***********************************************************************************************************************************************
// Synchronize all MicroServices

val jmSyncTask = TaskKey[Unit]("jmSync", "Synchronize all JumpMicro Microservices")

jmSyncTask := {
  val lastSyncFile = new File(".lastsync")
  def touchLastSync() = {
    IO.touch(lastSyncFile)
  }

  // http://www.codejava.net/coding/how-to-calculate-md5-and-sha-hash-values-in-java

  def convertByteArrayToHexString(arrayBytes: Array[Byte]): String = {
    val stringBuffer: StringBuffer = new StringBuffer()
    for (i <- 0 until arrayBytes.length) {
      stringBuffer.append(
        java.lang.Integer
          .toString((arrayBytes(i) & 0xff) + 0x100, 16)
          .substring(1))
    }
    stringBuffer.toString
  }

  def hashFile(file: File, algorithm: String): String = {
    // @todo Handle exceptions
    val digest: MessageDigest = MessageDigest.getInstance(algorithm)
    try {
      digest.update(IO.readBytes(file))
    } catch {
      case ex: java.io.FileNotFoundException => {  }
    }
    val hashedBytes: Array[Byte] = digest.digest()
    convertByteArrayToHexString(hashedBytes)
  }

  def md5File(f: File): String = {
    if (f.isDirectory)
      "directory"
    else
      hashFile(f, "MD5")
  }

  def directoryHash(d: File, filesHashed: Seq[(File, String)]): String = {
    // Get all files in directory
    val filesInDirectoryHash: Seq[String] = for (f <- filesHashed
                                                 if f._1.getCanonicalPath.startsWith(d.getCanonicalPath)) yield f._2
    val allStrings = filesInDirectoryHash.mkString("")

    val digest: MessageDigest = MessageDigest.getInstance("MD5")
    digest.update(allStrings.toCharArray.map(_.toByte))
    val hashedBytes: Array[Byte] = digest.digest()
    convertByteArrayToHexString(hashedBytes)
  }

  // The key is the relative path and the hash
  def toMap(a: Seq[(File, String, String, Long)]): Map[String, (File, String, String, Long)] = {
    a.map((z) => (z._2 + z._3, (z._1, z._2, z._3, z._4))).toMap
  }

  def intersection(a: Seq[(File, String, String, Long)], b: Seq[(File, String, String, Long)]): Seq[(File, String, String, Long)] = {
    val aMap = toMap(a)
    val bMap = toMap(b)
    val result = for (inA <- aMap; inB <- bMap.get(inA._1)) yield inB
    result.toSeq
  }

  // The symmetric difference is a NOT of intersection
  def symmetricDiff(intersectionOf: Seq[(File, String, String, Long)], a: Seq[(File, String, String, Long)], b: Seq[(File, String, String, Long)]): Map[String, (File, String, String, Long)] = {
    val aMap = toMap(a)
    val bMap = toMap(b)
    val interMap = toMap(intersectionOf)
    val inANotInter = for (inA <- aMap; if interMap.contains(inA._1) == false) yield inA
    val inBNotInter = for (inB <- bMap; if interMap.contains(inB._1) == false) yield inB
    inANotInter ++ inBNotInter
  }

  def updateFiles(lastModifiedDate: Option[Long],
                  firstDate: Long, secondDate: Long,
                  firstHash: String, secondHash: String,
                  firstFileSize: Long, secondFileSize: Long,
                  firstFile: File, secondFile: File) = {
    val fileContentsEqual = (firstHash == secondHash) && (firstFileSize == secondFileSize)
    val sameModifiedDate = firstDate == secondDate
    val firstNewerThanSecondDate = firstDate > secondDate
    val secondNewerThanFirstDate = secondDate > firstDate
    val firstAfterLastModifiedDate = if (lastModifiedDate.isDefined) firstDate > lastModifiedDate.get else false
    val secondAfterLastModifiedDate = if (lastModifiedDate.isDefined) secondDate > lastModifiedDate.get else false
    val hashEqual = firstHash == secondHash
    // Now actions
    if (fileContentsEqual && hashEqual) {
      // Do no action
    } else if (firstAfterLastModifiedDate && !secondAfterLastModifiedDate) {
      // Files are not equal and first was modified most recently, copy first over second
      IO.copyFile(firstFile, secondFile, true)
    } else if (!firstAfterLastModifiedDate && secondAfterLastModifiedDate) {
      // Mirror of first case
      IO.copyFile(secondFile, firstFile, true)
    } else if (firstNewerThanSecondDate) {
      IO.copyFile(firstFile, secondFile, true)
    } else if (secondNewerThanFirstDate) {
      IO.copyFile(secondFile, firstFile, true)
    } else {
    }
  }

  def syncDirs(fromDir: File, toDir: File, filesHashed: Seq[(File, String)], lastModified: Option[Long]) = {
    // Lets try to sync this directory with JMShared by comparing all the directories first
    val jmSharedDir = toDir; //new File(".." + \\ + "JMShared")
    val jmSangriaDir = fromDir; //new File(".." + \\ + "JMSangriaGraphql")
    val jmSharedDirFiles = filesHashed.filter(_._1.getCanonicalPath.startsWith(jmSharedDir.getCanonicalPath))
    val jmSangriaGraphqlDirFiles = filesHashed.filter(_._1.getCanonicalPath.startsWith(jmSangriaDir.getCanonicalPath))

    // File, relative path, MD5, last modified date/time
    val aSharedDirFiles: Seq[(File, String, String, Long)] = for (f <- jmSharedDirFiles) yield (f._1, f._1.getCanonicalPath.stripPrefix(jmSharedDir.getCanonicalPath), f._2, f._1.lastModified())
    val bSangriaGraphqlDirFiles: Seq[(File, String, String, Long)] = for (f <- jmSangriaGraphqlDirFiles) yield (f._1, f._1.getCanonicalPath.stripPrefix(jmSangriaDir.getCanonicalPath), f._2, f._1.lastModified())

    // Given a and b, apply the logic for sync
    // 1. All files and directories which are exactly the same ignore, this can narrow the set to consider
    val theSame = intersection(aSharedDirFiles, bSangriaGraphqlDirFiles)
    val aMap = toMap(aSharedDirFiles)
    val bMap = toMap(bSangriaGraphqlDirFiles)
    val notTheSame = symmetricDiff(theSame, aSharedDirFiles, bSangriaGraphqlDirFiles)

    // For those files which are different then group them together and consider their differences
    val grouped = notTheSame.groupBy(_._2._2).values.toSeq
    for (g <- grouped) {
      val listG = g.values.toSeq
      val first = listG(0)
      val second = listG(1)

      updateFiles(lastModifiedDate = lastModified,
        firstDate = first._4, secondDate = second._4,
        firstHash = first._3, secondHash = second._3,
        firstFileSize = first._1.length(), secondFileSize = second._1.length(),
        firstFile = first._1, secondFile = second._1)
    }

  }

  val lastModified: Option[Long] = if (lastSyncFile.exists()) Some(lastSyncFile.lastModified()) else None
  touchLastSync() // @todo Should this be at the start or end of sync

  // All directories in sub-directory
  val allSubFiles = new File("..").listFiles().toSeq
  val allSubDirs: Seq[File] = for (f <- allSubFiles
                                   if (f.isDirectory && (f.getName.startsWith(".") == false))) yield f
  // Keep a file called .lastsync to keep track of the last time a sync happened and all calculations are based upon
  // the last modified time of all files.
  // The process is as follows.
  // 1. Fetch all .lastsync from each sub-directory and mark the created and modified files
  // Sync means
  // 1. Modified files case: If a file or directory is modified in (a) at a newer time than the one in (b)
  //  Copy or merge the contents of file in (a) to the file in (b)
  // 2. Newly created files case: If a file or directory exists in (a) but does not exist in (b)
  // IF .lastsynctime exists
  //    IF the newly created file is after .lastsync modified time THEN
  //      Copy the newly created file to (b)
  //    ELSE
  //      This means the file was deleted in (b), so delete the file in (a)
  // ELSE
  //    The file was either created or deleted, we should assume creation so Copy the newly created file to (b)
  // 3. Deleted files case: If a file or directory does not exist in (a) but does exist in (b)
  // This is just the inverse of 2. so case 2. covers this

  // Build up the data about the file system
  val subDirsAndContainedFiles: Seq[(File, Seq[File])] = for (f <- allSubDirs) yield
    (f, recursiveListFiles(new File(f.getCanonicalPath + \\ + "src" + \\ + "main" + \\ + "scala" + \\ + "jumpmicro" + \\ + "shared")).toSeq)

  // All files in all directories located in JumpMicro, files only
  val filesHashed: Seq[(File, String)] = for (f <- subDirsAndContainedFiles; g: File <- f._2; if g.isFile) yield (g, md5File(g))
  // These are all the directories located in JumpMicro
  val dirsHashed: Seq[(File, String)] = for (d <- subDirsAndContainedFiles; if d._1.isDirectory) yield {
    val d2: File = d._1
    val z: String = directoryHash(d2, filesHashed)
    (d2, z)
  }
  // If all directories have the same contents then no action, this is the most simple case
  val test = dirsHashed.head._2
  val allDirsEqual = dirsHashed.forall(_._2 == test)
  if (allDirsEqual) {
    println("All JumpMicro MicroService directories are equal so no synchronization will take place")
  } else {
    println("Some directory is different, so some synchronization can take place")
    if (lastModified.isDefined)
      println("A last modified file exists")
    else
      println("No last modified file exists")

    // 1. Sync the changed directory(s) to JMShared
    for (fromDir <- allSubDirs.filter(_.getName != "JMShared")) {
      val toDir = new File(".." + \\ + "JMShared")
      syncDirs(fromDir, toDir, filesHashed, lastModified)
    }

    // 2. Sync JMShared back out to all to all others where different
    for (toDir <- allSubDirs.filter(_.getName != "JMShared")) {
      val fromDir = new File(".." + \\ + "JMShared")
      syncDirs(fromDir, toDir, filesHashed, lastModified)
    }
  }

  lastSyncFile.delete()
  lastSyncFile.createNewFile()
}

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
      IO.copyDirectory(new File("target" + \\ + "idrisclass"), new File("target" + \\ + "scala-" + scalaMajorVersion + \\ + "classes"), true, true)
      IO.delete(new File("target" + \\ + "idrisclass"))
      IO.delete(new File("target" + \\ + "scala-" + scalaMajorVersion + \\ + "classes" + \\ + projectName.toLowerCase))
    }
  }

  idrisTimestamp.delete()
  idrisTimestamp.createNewFile()
}

cleanFiles += file("target" + \\ + "idrisclass")

//unmanagedClasspath in Compile += baseDirectory.value / "target" / "idrisclass"

compile in Compile <<= (compile in Compile).dependsOn(copySharedSrc).dependsOn(compileIdris)

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
privatePackage := privatePackages ++ subPackagesOf(JUMPMICRO_DOT + name.value.toString.toLowerCase + ".impl") ++
  subPackagesOf(JUMPMICRO_DOT + "shared") ++ Seq(
  JUMPMICRO_DOT + name.value.toString.toLowerCase,
  "mmhelloworld.idrisjvmruntime"
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
      if (mf==null) None // No manifest, then wrap it
      else {
        val sym = mf.getMainAttributes.getValue("Bundle-SymbolicName")
        val symOption: Option[String] = if (sym == null) None else {
          Some(sym).filterNot(_.isEmpty)
        }
        symOption
      }
    }
    if (opt.isDefined) opt.get.isEmpty else false
  }

  val ignoredModules = HashSet(
    "org.osgi/org.osgi.core",
    "org.osgi/org.osgi.compendium",
    "default/" + projectName.toLowerCase() + "_" + scalaMajorVersion,
    projectName.toLowerCase() + "/" + projectName.toLowerCase() + "_" + scalaMajorVersion
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
    karafDepsMustBeJarFiles ++ Seq(projectName.toLowerCase + "/" + projectName.toLowerCase + "_")
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
         jarFile <- f.jarFile) yield {
      val result = new File("." + \\ + "target" + \\ + "bundles" + \\ + jarFile.getName)
      if (result.exists()) result else {
        val result2 = new File("." + \\ + "target" + \\ + "bundles" + \\ + f.id.name + ".jar")
        if (result2.exists()==false) {
          println("Error, the jar file is not found. " + result2)
        }
        result2
      }
    }
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
      <feature description={projectName} version={thisVersion} name={projectName} install="auto">
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
        <bundle>file:/{ new File("." + \\ + "target" + \\ + "scala-" + scalaMajorVersion + \\ + projectName.toLowerCase() + "_" + scalaMajorVersion + "-" + thisVersion + ".jar").getCanonicalPath }</bundle>
        }
      </feature>
    </features>

  val karafDir = new File("." + \\ + "target" + \\ + "karaf")
  val karDirPath = "." + \\ + "target" + \\ + "karaf"
  val karafKarDir = new File(karDirPath)
  IO.delete(karafDir)
  IO.createDirectory(karafDir)
  IO.createDirectory(karafKarDir)

  def copyFile(path: File, dest: File) = {
    if (path.exists()) IO.copyFile(path, dest) else println("Error in copying file, the source file does not exist " + path.getCanonicalPath)
  }

  for (j <- jarFilesInBundles) { copyFile(j, new File(karDirPath + "/" + j.getName)) }
  for (m <- mustBeFiles; if m.jarFile.isEmpty) {
    val file = new File("." + \\ + "target" + \\ + "bundles" + \\ + m.id.name + ".jar")
    IO.copyFile(file, new File(karDirPath + \\ + file.getName))
  }

  val p = new scala.xml.PrettyPrinter(1000, 4)
  val outputString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + p.format(features)
  IO.write(new File(karDirPath + \\ + "features.xml"), outputString)
}
// @feature end commonfooter
