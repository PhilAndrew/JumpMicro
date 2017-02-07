
// @todo Should not need to depend on my branch of this, can I change this back to the origional?
lazy val sbtOsgiFelixPlugin = uri("https://github.com/PhilAndrew/sbt-osgi-felix.git#487d21e1fdeca3115fe8eb765f958d4a349aa1dd")

addSbtPlugin("org.doolse" % "sbt-osgi-felix" % "1.0.8-PHILIP")

// ScalaJS https://www.scala-js.org/
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.14")

//addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % sys.props.getOrElse("plugin.version", sys.error("'plugin.version' environment variable is not set")))

// Copy paste detector https://github.com/sbt/cpd4sbt
addSbtPlugin("de.johoop" % "cpd4sbt" % "1.2.0")

// Clippy helps to show better Scala error messages https://scala-clippy.org/
addSbtPlugin("com.softwaremill.clippy" % "plugin-sbt" % "0.5.0")

// sbt-docker-compose https://github.com/Tapad/sbt-docker-compose
addSbtPlugin("com.tapad" % "sbt-docker-compose" % "1.0.17")
addSbtPlugin("com.typesafe.sbt" %% "sbt-native-packager" % "1.1.0")

lazy val sbtKaraf = uri("https://github.com/PhilAndrew/osgi-tooling.git#2dfa5b654ca070575024657d1aa2766cbb29e867")

addSbtPlugin("wav.devtools" % "sbt-karaf" % "0.1.1.PHILIP")

lazy val root = project.in(file(".")).dependsOn(sbtOsgiFelixPlugin).dependsOn(sbtKaraf)





//lazy val sbtOsgiFelixPlugin = uri("https://github.com/PhilAndrew/sbt-osgi-felix.git#487d21e1fdeca3115fe8eb765f958d4a349aa1dd")


//lazy val root = project.in(file(".")).dependsOn(sbtOsgiFelixPlugin).dependsOn(sbtKaraf)

//addSbtPlugin("org.doolse" % "sbt-osgi-felix" % "1.0.8-PHILIP")

//addSbtPlugin("wav.devtools" % "sbt-karaf" % "0.1.1.PHILIP")


/*
lazy val plugins = (project in file("."))
  .dependsOn(sbtKarafPackaging)

def sbtKarafPackaging = ProjectRef(
  uri("git://github.com/wav/osgi-tooling.git"),
  "sbt-karaf-packaging")*/

// git@github.com:PhilAndrew/sbt-osgi-felix-akka-blueprint-camel.git
/*def sbtOsgiFelix = ProjectRef(
  uri("git://github.com/PhilAndrew/sbt-osgi-felix.git"),
  "sbt-osgi-felix")*/
