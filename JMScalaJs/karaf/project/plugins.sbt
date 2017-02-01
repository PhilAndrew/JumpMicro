
//addSbtPlugin("org.doolse" % "sbt-osgi-felix" % "1.0.4-PHILIP")

lazy val sbtOsgiFelixPlugin = uri("https://github.com/PhilAndrew/sbt-osgi-felix.git#b89fe950a7bc0c1356275a51e1ee62e24724b6af")

lazy val sbtKaraf = uri("https://github.com/PhilAndrew/osgi-tooling.git#2dfa5b654ca070575024657d1aa2766cbb29e867")

lazy val root = project.in(file(".")).dependsOn(sbtOsgiFelixPlugin).dependsOn(sbtKaraf)

addSbtPlugin("org.doolse" % "sbt-osgi-felix" % "1.0.4-PHILIP")

addSbtPlugin("wav.devtools" % "sbt-karaf" % "0.1.1.PHILIP")


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
