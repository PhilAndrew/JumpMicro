
// @todo Should not need to depend on my branch of this, can I change this back to the origional?

//lazy val root = project.in(file(".")).dependsOn(sbtOsgiFelixPlugin).dependsOn(sbtKaraf)

//lazy val concatPlugin = uri("https://github.com/ground5hark/sbt-concat.git#342acc34195438799b8a278fda94b126238aae17")

///*

resolvers += Resolver.bintrayRepo("philandrew", "sbt-plugins")

// Chose one of the following

//addSbtPlugin("org.philandrew" % "sbt-osgi-felix-p" % "1.0.13")

lazy val sbtOsgiFelixPlugin = uri("https://github.com/PhilAndrew/sbt-osgi-felix-p.git#020e456787d3c88583be074f31c37b50b1d6a8c3")

lazy val root = project.in(file(".")).dependsOn(sbtOsgiFelixPlugin)

// ScalaJS https://www.scala-js.org/
//addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.14")

// Copy paste detector https://github.com/sbt/cpd4sbt
addSbtPlugin("de.johoop" % "cpd4sbt" % "1.2.0")

// Clippy helps to show better Scala error messages https://scala-clippy.org/
addSbtPlugin("com.softwaremill.clippy" % "plugin-sbt" % "0.5.0")

// sbt-docker-compose https://github.com/Tapad/sbt-docker-compose
addSbtPlugin("com.tapad" % "sbt-docker-compose" % "1.0.17")
addSbtPlugin("com.typesafe.sbt" %% "sbt-native-packager" % "1.1.0")

// https://github.com/jrudolph/sbt-dependency-graph/
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")
