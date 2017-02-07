name := "jump_micro"

version := "1.0"

scalaVersion := "2.11.8"

import wav.devtools.sbt.karaf.packaging.SbtKarafPackaging
import SbtKarafPackaging.autoImport._
import KarafPackagingKeys._
import wav.devtools.karaf.packaging.FeaturesXml
import wav.devtools.karaf.packaging.FeaturesXml.{Bundle, Feature, FeatureOption, FeaturesOption}

enablePlugins(SbtKarafPackaging)

osgiSettings

//lazy val core = project.in(file("../"))

lazy val root = project.in(file("."))//.dependsOn(core)
  .settings(featuresXml := {


    /*
        <!-- Scala -->
        <bundle>mvn:org.scala-lang/scala-library/2.11.8</bundle>
        <bundle>mvn:org.scala-lang.modules/scala-java8-compat_2.11/0.7.0</bundle>
        <bundle>mvn:com.typesafe.scala-logging/scala-logging_2.11/3.4.0</bundle>
        <bundle>mvn:org.scala-lang/scala-reflect/2.11.8</bundle>
        <bundle>mvn:org.scala-lang.modules/scala-xml_2.11/1.0.3</bundle>
     */
    def scalaBundles = Seq("mvn:org.scala-lang/scala-library/2.11.8",
      "mvn:org.scala-lang.modules/scala-java8-compat_2.11/0.7.0",
      "mvn:com.typesafe.scala-logging/scala-logging_2.11/3.4.0",
      "mvn:org.scala-lang/scala-reflect/2.11.8",
      "mvn:org.scala-lang.modules/scala-xml_2.11/1.0.3")

    /*
        <!-- Akka -->
        <bundle>mvn:com.typesafe/config/1.3.0</bundle>
        <bundle>mvn:com.typesafe.akka/akka-actor_2.11/2.4.11</bundle>
        <bundle>mvn:com.typesafe.akka/akka-protobuf_2.11/2.4.11</bundle>
        <bundle>mvn:io.netty/netty/3.10.6.Final</bundle>

        <bundle>mvn:com.typesafe.akka/akka-remote_2.11/2.4.11</bundle>
        <bundle>mvn:com.typesafe.akka/akka-stream_2.11/2.4.11</bundle> <!-- required for akka remote -->
        <bundle>mvn:com.typesafe/ssl-config-akka_2.11/0.2.1</bundle> <!-- required for akka stream -->
        <bundle>mvn:com.typesafe/ssl-config-core_2.11/0.2.1</bundle> <!-- required for ssl config akka -->
        <bundle>mvn:org.scala-lang.modules/scala-parser-combinators_2.11/1.0.4</bundle> <!-- for akka remote above -->
        <bundle>mvn:org.reactivestreams/reactive-streams/1.0.0</bundle> <!-- for akka remote above -->
        <bundle>wrap:mvn:io.aeron/aeron-client/1.0.2</bundle> <!-- for akka remote above -->
        <bundle>wrap:mvn:io.aeron/aeron-driver/1.0.2</bundle> <!-- for akka remote above -->
        <bundle>mvn:com.typesafe.akka/akka-osgi_2.11/2.4.11</bundle>
        <bundle>mvn:com.typesafe.akka/akka-camel_2.11/2.4.11</bundle>
        <bundle>mvn:com.typesafe.akka/akka-slf4j_2.11/2.4.11</bundle>

        <bundle>wrap:mvn:org.agrona/Agrona/0.5.5</bundle> <!-- unknown why? -->

        <bundle>mvn:com.fasterxml.jackson.core/jackson-annotations/2.8.4</bundle>
        <bundle>mvn:org.apache.camel/camel-core-osgi/2.17.3</bundle>
        <bundle>mvn:com.fasterxml.jackson.core/jackson-core/2.8.4</bundle>
        <bundle>mvn:com.fasterxml.jackson.core/jackson-databind/2.8.4</bundle>
        <bundle>wrap:mvn:org.uncommons.maths/uncommons-maths/1.2.2a</bundle>
        <bundle>mvn:org.apache.camel/camel-scala/2.17.3</bundle>
        <bundle>mvn:commons-codec/commons-codec/1.10</bundle>
        <bundle>mvn:org.apache.commons/commons-collections4/4.1</bundle>

        <bundle>mvn:org.apache.commons/commons-lang3/3.4</bundle>
     */
    def akkaBundles = Seq("mvn:com.typesafe/config/1.3.0",
      "mvn:com.typesafe.akka/akka-actor_2.11/2.4.11",
      "mvn:com.typesafe.akka/akka-protobuf_2.11/2.4.11",
      "mvn:io.netty/netty/3.10.6.Final",
      "mvn:com.typesafe.akka/akka-remote_2.11/2.4.11",
      "mvn:com.typesafe.akka/akka-stream_2.11/2.4.11",
      "mvn:com.typesafe/ssl-config-akka_2.11/0.2.1",
      "mvn:com.typesafe/ssl-config-core_2.11/0.2.1",
      "mvn:org.scala-lang.modules/scala-parser-combinators_2.11/1.0.4",
      "mvn:org.reactivestreams/reactive-streams/1.0.0",
      "wrap:mvn:io.aeron/aeron-client/1.0.2",
      "wrap:mvn:io.aeron/aeron-driver/1.0.2",
      "mvn:com.typesafe.akka/akka-osgi_2.11/2.4.11",
      "mvn:com.typesafe.akka/akka-camel_2.11/2.4.11",
      "mvn:com.typesafe.akka/akka-slf4j_2.11/2.4.11",
      "wrap:mvn:org.agrona/Agrona/0.5.5",
      "mvn:com.fasterxml.jackson.core/jackson-annotations/2.8.4",
      "mvn:org.apache.camel/camel-core-osgi/2.17.3",
      "mvn:com.fasterxml.jackson.core/jackson-core/2.8.4",
      "mvn:com.fasterxml.jackson.core/jackson-databind/2.8.4",
      "wrap:mvn:org.uncommons.maths/uncommons-maths/1.2.2a",
      "mvn:org.apache.camel/camel-scala/2.17.3",
      "mvn:commons-codec/commons-codec/1.10",
      "mvn:org.apache.commons/commons-collections4/4.1",
      "mvn:org.apache.commons/commons-lang3/3.4")

    def fileBased = Seq(
      "file:/F:/browser/application/apps/UserInterfaceMicro/core/target/bundles/scaldi_2.11-0.5.7.jar",
      "file:/F:/browser/application/apps/UserInterfaceMicro/core/target/bundles/neo4j-java-driver-1.0.5.jar",
      "file:/F:/browser/application/apps/UserInterfaceMicro/core/target/scala-2.11/user_interface_micro_core_2.11-0.1-SNAPSHOT.jar",
      "file:/F:/browser/application/apps/UserInterfaceMicro/core/target/bundles/neo4j-ogm-osgi_2.11.jar"
    )


    def ignoreRepos = Seq(
    "mvn:com.typesafe/config/1.3.1",
    "wrap:mvn:io.aeron/aeron-driver/1.0.1",
    "mvn:org.osgi/org.osgi.compendium/4.3.1",
    "mvn:com.thoughtworks.paranamer/paranamer/2.8",
    "wrap:mvn:org.neo4j.driver/neo4j-java-driver/1.0.5",
    "wrap:mvn:org.apache.lucene/lucene-sandbox/6.2.0",
    "mvn:com.fasterxml.jackson.core/jackson-annotations/2.8.0",
    "wrap:mvn:org.scaldi/scaldi_2.11/0.5.7",
    "wrap:mvn:org.json/json/20140107$Bundle-SymbolicName=json&Bundle-Version=20140107",
    "mvn:com.fasterxml.jackson.core/jackson-core/2.8.3",
    "mvn:commons-lang/commons-lang/2.6",
    "mvn:com.typesafe.akka/akka-http-experimental_2.11/2.4.11",
    "mvn:com.fasterxml.jackson.core/jackson-databind/2.8.3",
    "mvn:org.slf4j/slf4j-api/1.7.21",
    "mvn:ch.qos.logback/logback-classic/1.1.7",
    "mvn:com.typesafe.akka/akka-http-core_2.11/2.4.11",
    "wrap:mvn:com.jcraft/jzlib/1.1.3",
    "mvn:org.slf4j/jcl-over-slf4j/1.7.21",
    "mvn:com.typesafe.akka/akka-parsing_2.11/2.4.11",
    "mvn:com.sun.xml.bind/jaxb-impl/2.2.11",
    "wrap:mvn:org.agrona/Agrona/0.5.4",
    "wrap:mvn:org.apache.lucene/lucene-queries/6.2.0",
    "file:/F:/browser/application/apps/UserInterfaceMicro/target/scala-2.11/user_interface_micro_2.11-1.0.jar",
    "wrap:mvn:org.apache.lucene/lucene-core/6.2.0",
    "mvn:ch.qos.logback/logback-core/1.1.7",
    "mvn:org.slf4j/log4j-over-slf4j/1.7.21",
    "wrap:mvn:commons-httpclient/commons-httpclient/3.1",
    "mvn:commons-logging/commons-logging/1.2",
    "mvn:org.osgi/osgi.core/6.0.0",
    "mvn:com.sun.xml.bind/jaxb-core/2.2.11",
    "mvn:org.slf4j/slf4j-simple/1.7.21",
    "wrap:mvn:universe/neo4j-ogm-osgi_2.11/1.4.22",
    "mvn:org.osgi/org.osgi.core/4.3.1",
    "wrap:mvn:io.aeron/aeron-client/1.0.1",
    "wrap:mvn:com.github.kxbmap/configs_2.11/0.4.4"
    )

    def ignoreReposFixed = ignoreRepos.toSet.diff( (scalaBundles ++ akkaBundles).toSet )
    val f: FeaturesXml = featuresXml.value
    val elems = featuresXml.value.elems

    val dependencys: Set[FeaturesXml.FeatureOption] = f.features.toList.head.deps.asInstanceOf[Set[FeaturesXml.FeatureOption]]
    val bundlesAsOptions: Set[FeaturesXml.FeatureOption] = f.features.toList.head.deps.asInstanceOf[Set[FeaturesXml.FeatureOption]]
    val elemsRepoSet: Set[FeaturesXml.Bundle] = bundlesAsOptions.filter(_.isInstanceOf[FeaturesXml.Bundle]).asInstanceOf[Set[FeaturesXml.Bundle]]
    //val arrayBuffer: scala.collection.mutable.ArrayBuffer[FeaturesXml.FeaturesOption] = f.elems.asInstanceOf[scala.collection.mutable.ArrayBuffer[FeaturesXml.FeaturesOption]]
    //val toList: List[FeaturesXml.FeaturesOption] = arrayBuffer.toList
    def workingBundleRepositorys = fileBased.map((s: String) => FeaturesXml.Bundle(url = s)) ++ scalaBundles.map((s: String) => FeaturesXml.Bundle(url = s)) ++ akkaBundles.map((s: String) => FeaturesXml.Bundle(url = s))
    def workingBundleRepositorysSet = workingBundleRepositorys.toSet

    def replaceRepo(elems: Seq[FeaturesXml.FeaturesOption], repoUrl: String): Seq[FeaturesXml.FeaturesOption] = {
      val removed = elems.filterNot{
        case e: FeaturesXml.Repository => {
          e.url == repoUrl
        }
        case e: FeaturesXml.Feature => {
          false
        }
        case _ => false
      }
      removed
    }

    def addRepo(elems: Seq[FeaturesXml.FeaturesOption], repoUrl: String): Seq[FeaturesXml.FeaturesOption] = {
      elems.asInstanceOf[Seq[FeaturesXml.FeaturesOption]] ++ Seq(FeaturesXml.Repository(url = repoUrl)).asInstanceOf[Seq[FeaturesXml.FeaturesOption]]
    }
    // Seq(FeaturesXml.Repository(url = "mvn:org.apache.camel.karaf/apache-camel/2.17.3/xml/features"))

    // elemsRepoSet is the ones it thinks it should generate
    // workingBundleRepositorysSet is the ones we want so therefore
    println("...................................................")

    // mvn:com.fasterxml.jackson.core/jackson-annotations/2.8.4

    val magic = "mvn:com.fasterxml.jackson.core/jackson-annotations/2.8.4"

    val aaa = workingBundleRepositorysSet.map(_.url).toSet // true
    val bbb = elemsRepoSet.map(_.url).toSet // false

    if (aaa.contains(magic)) println("aaa contains")
    if (bbb.contains(magic)) println("bbb contains")

    val wha = bbb.diff(aaa)

    val aaaBbb = aaa ++ bbb
    val aaaBbbWithoutIgnores = aaaBbb.diff(ignoreRepos.toSet)
    //val withIgnoresRemoved = elemsRepositoriesItWishesToAddSet.filter((z: Bundle) => ! ignoreReposFixed.toSet.contains(z.url))
    //val currentMinusWorking = elemsRepositoriesItWishesToAddSet.diff(workingBundleRepositorysSet)
    //println("workingBundleRepositorysSet size: " + workingBundleRepositorysSet.size)
    //println("elemsRepoSet size: " + elemsRepositoriesItWishesToAddSet.size)
    for (z <- aaaBbbWithoutIgnores) {
      println(z)
    }
    println("...........................................................")
    println("...")

    println("Akka bundles size " + akkaBundles.size)
    println("Scala bundles size " + scalaBundles.size)
    println("aaaBbbWithoutIgnores size " + aaaBbbWithoutIgnores.size)

    //val elemsReplaced = replaceRepo(elems, "mvn:org.apache.camel.karaf/apache-camel/2.16.0/xml/features")
    //val added = addRepo(elemsReplaced, "mvn:org.apache.camel.karaf/apache-camel/2.17.3/xml/features")

    // Seq(FeaturesXml.Repository(url = "mvn:org.apache.camel.karaf/apache-camel/2.17.3/xml/features"))

    val bundlesToOutput: Seq[FeaturesXml.FeatureOption] = aaaBbbWithoutIgnores.map((z:String) => Bundle(z)).toSeq.asInstanceOf[Seq[FeaturesXml.FeatureOption]]

    // user_interface_micro,1.0.0,Set(Bundle(
    println(featuresXml.value)

    val result = featuresXml.value.copy(elems = {
      val what = for (e <- featuresXml.value.elems) yield {
        if (e.isInstanceOf[FeaturesXml.Feature]) {
          e.asInstanceOf[FeaturesXml.Feature].copy(deps = bundlesToOutput.toSet)
        } else e
        //val f = featuresXml.value.elems.head.asInstanceOf[FeaturesXml.Feature]
      }
      // @todo The repos need to change

      what
    }
    )

    //val result2 = featuresXml.value

    result
  }
  )

featuresRequired := Set(
  feature("wrap", /* enable provisioning of wrapped bundles */
    dependency = true,
    prerequisite = true),
  feature("log") /* implements slf4j */,
  //feature("camel"),
  feature("camel-exec"),
  feature("camel-core"))

libraryDependencies ++= Seq(
  "org.json" % "json" % "20140107" toWrappedBundle(Map(
    "Bundle-SymbolicName" -> "json",
    "Bundle-Version" -> "20140107"
  )),
  "org.slf4j" % "slf4j-api" % "1.7.12" % "provided",
  "org.osgi" % "org.osgi.core" % "6.0.0" % "provided",
  FeatureID("org.apache.karaf.features", "standard", "4.0.2"),
  FeatureID("org.apache.camel.karaf", "apache-camel", "2.16.0"))

//FeatureID("org.apache.camel.karaf", "apache-camel", "2.16.0"), // I want 2.17.3, was 2.16.0
//FeatureID("org.apache.karaf.features", "standard", "4.0.2")) // I want 4.0.7, was 4.0.2
