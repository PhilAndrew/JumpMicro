# JumpMicro Microservice for Scala/Java [![License][licenseImg]][licenseLink] 

[licenseImg]: https://img.shields.io/github/license/PhilAndrew/JumpMicro.svg
[licenseImg2]: https://img.shields.io/:license-mit-blue.svg
[licenseLink]: LICENSE

JumpMicro is Scala Microservice by convention, not a library but a standard way of writing Microservices by following examples which lead to the production of a microservice. JumpMicro uses [OSGi](https://en.wikipedia.org/wiki/OSGi) to allow for code to be reloaded at runtime and as such many MicroServices can run in one OSGi container efficiently.

## What do you need to setup?

You need Java, Scala, SBT build tool.

JMScalaJs wants to use a Neo4J database if it is available as that is the default database for storage, but not required to run. Refer to the file jumpmicro.conf for connection settings. 

## How can this be run?

You can also run a normal Java or Scala main within this project by doing the following. However this does not give you any OSGi features and really it not the right way to run it.

    > sbt runMain package.path.to.main.YourClass param1 param2

JumpMicro produces an OSGi JAR bundle which can be run inside an OSGi container. You can run it in three ways.
 
* [Apache Felix](http://felix.apache.org/) in [embedded mode](http://felix.apache.org/documentation/subprojects/apache-felix-framework/apache-felix-framework-launching-and-embedding.html) by running ```sbt run``` which is a simple and easy way to run the OSGi program
* In the [Apache Felix](http://felix.apache.org/) container normally
* In the [Apache Karaf](http://karaf.apache.org/) container by ```sbt karafBuild``` and ```sbt karafDeploy``` which will create a ```features.xml``` file and deploy it to Karaf
* Other OSGi containers have not yet been tested

Other OSGi containers I would like to test in the near future are

* [Eclipse Virgo](http://www.eclipse.org/virgo/)
* [Equinox OSGi](http://www.eclipse.org/equinox/) 
* [Knopflerfish OSGi](http://www.knopflerfish.org/) 

## How to build and run

You must firstly install [Neo4J OGM OSGi](https://github.com/PhilAndrew/neo4j-ogm-osgi) so that it is available for build as a dependency.

    > git clone git@github.com:PhilAndrew/neo4j-ogm-osgi.git
    
    > cd neo4j-ogm-osgi
    
    > sbt publishLocal
    
Then git clone this repository, for test purposes lets run JMScalaJS.    

Go to the JMScalaJs directory inside JumpMicro and

    > sbt run

This runs it in an embedded Felix OSGi container.

When it has started the console will state 

    [ActorSystem-akka.actor.default-dispatcher-5] INFO jumpmicro.jmscalajs.impl.actor.WebServer - Server online at http://localhost:8080/
    
Then go to http://localhost:8080/test

You are looking at an Akka Http server delivering a ScalaJs page running in OSGi in Felix Embedded.

## How to create a new MicroService

Creating a new one is done by copying one to a new folder. This is done by running JMCloner, go into the JMCloner and run ```sbt run```, then http://localhost:8181/, click on the MicroService you want to clone, type in a new name such as JMNewMicroService and press "Clone MicroService". This creates a new directory with a copy of that MicroService with a new name, all the packages, classes and names are updated to match the new name.

## Adding SBT dependencies

Dependencies in SBT are usually written as follows

    libraryDependencies += "org.log4s" %% "log4s" % "1.3.4"

However for OSGi we want to be able to express the the normal SBT module dependencies but also the packages we want to import and use ```packageRequirements```. The settings you are less likely to use are what files must be JAR files for Karaf (I'll explain that later) and what modules you want to import. You shouldn't really import modules, you should import packages in ```packageRequirements```, but module importing is a more coarse grained way to import. 

     OsgiDependency(val name : String, // name is just for documentation purposes
                val sbtModules : Seq[sbt.ModuleID], // The SBT modules imported
                val packageRequirements : Seq[scala.Predef.String], // The packages which we import in our project
                val mustBeJarFilesForKaraf : Seq[scala.Predef.String], // Less used setting
                val moduleRequirements : Seq[scala.Predef.String]) // Less used setting
     
So the equivalent OsgiDependency for Log4s is as follows.                
                
    OsgiDependency("Log4s",
      Seq("org.log4s" %% "log4s" % "1.3.4"),
      Seq("org.log4s"),
      Seq(), Seq()),                

## Deploy to Karaf (if you wish to)

The normal build steps to get it to Karaf as

1. Compile it (normally you just want to sbt compile)

    > sbt clean update compile
    
2. Osgi Bundle builds the OSGi bundle file for example for the JMCloner MicroService in ```target/scala-2.11/jmcloner-2.11-1.0.0.jar```

    > sbt osgiBundle
    
3. Build features file for Karaf and deploy it to Karaf, for the deployment you need to have set the right environment variables for this to locate Karaf, KARAF_HOME, KARAF_DEPLOY, KARAF_JAR_DIRECTORY.

    > sbt karafBuild karafDeploy

## MicroService examples available

| MicroService Name        | What does it do?                                                             |
| ------------------------ |:----------------------------------------------------------------------------:|
| JMScalaJs                | Akka Http server with ScalaJS running in webpage http://localhost:8080/test/ |
| JMCloner                 | Allow creation of a new MicroService by copying one to another<br>After sbt run the visit http://localhost:8181/        |
| JMSangriaGraphql         | NOT WORKING YET: Demonstration of Sangria GraphQL<br>After sbt run the visit http://localhost:8181/ |
| JMResourceRegistry       | NOT WORKING YET: A common resource registry OSGi service used by the other OSGi services |

## Features    
  
It has the following features:

* Can run in an OSGi container or standalone as a normal Java application using the embedded Felix OSGi container.
* Dependency Injection using [Scaldi](http://scaldi.org/).
* Logging using [Log4s](https://github.com/Log4s/log4s) which is a slf4j wrapper and works ok with Apache Karaf OSGi container.
* Supports [Apache Karaf](http://karaf.apache.org/) and [Apache Felix](http://felix.apache.org/) OSGi containers. (others are untested but OSGi is a standard, so should work).
* OSGi components using [Domino Scala library](https://github.com/domino-osgi/domino) with [user guide](https://github.com/domino-osgi/domino/blob/master/UserGuide.adoc) makes OSGi easier to understand and use from Scala, it support developers in writing bundle activators for the JVM module system OSGi.
* [Apache Camel](http://camel.apache.org/) and its [components](https://camel.apache.org/components.html).
* Supports [Akka Actors](http://akka.io/), [Akka Streams](http://akka.io/), [Monix](https://github.com/monix/monix), [Apache Camel](http://camel.apache.org/) in Akka Actors.
* For data storage the default is [Neo4J](https://neo4j.com/) Graph database using the [Cypher query language](https://neo4j.com/developer/cypher-query-language/).
* [Neo4j OGM](https://github.com/neo4j/neo4j-ogm) as the Object to Graph database mapper with a modified version for OSGi which uses Java Reflection to parse annotations [Neo4J OGM OSGi](https://github.com/PhilAndrew/neo4j-ogm-osgi).
* [Dropwizard Metrics](http://metrics.dropwizard.io/) for recording Metrics of the running application to ensure health of your application using [Metrics-Scala](https://github.com/erikvanoosten/metrics-scala) for  metrics recording, [documentation here](https://github.com/erikvanoosten/metrics-scala/blob/master/docs/Manual.md) with documentation for [Akka Actors](https://github.com/erikvanoosten/metrics-scala/blob/master/docs/Actors.md), [Futures](https://github.com/erikvanoosten/metrics-scala/blob/master/docs/Futures.md), [all docs](https://github.com/erikvanoosten/metrics-scala/tree/master/docs) and [3rd party libraries](http://metrics.dropwizard.io/3.1.0/manual/third-party/).
* [Korolev](https://github.com/fomkin/korolev) web framework, Single-page applications running on the server side
* Can run in a Docker container.
* [Idris language](http://www.idris-lang.org/) compiles, runs and interacts on the JVM in the MicroService using [Idris-JVM](https://github.com/mmhelloworld/idris-jvm) to compile in the sbt project. 

## Conventions and rules

* Use a higher level messaging solution to communicate between MicroServices such as Akka remoting messaging (in a cluster), consider use of backpressure or Camel to send and receive messages, use GraphQL, rather than REST.
* ?.

## Suggested libaries

* [Binding.scala](https://github.com/ThoughtWorksInc/Binding.scala) for HTML on client side
* [AutoWire](https://github.com/lihaoyi/autowire) for RPC communications between ScalaJS and a server

## Compile with Idris

[Idris](https://www.idris-lang.org/) is not required for JumpMicro to work, however Idris is a nice language with dependent types which, when type checks, usually produces correct code. The interoperability between Idris and JumpMicro can give you the ability to write Idris code and have it run within Akka Actors, in which case you can feel more certain the function you wrote work and you get the surrounding features of OSGi and JVM.

There is example Idris code in each MicroService in ```src/main/idris``` which can be compiled, in order for it to compile you need to install the [Idris language](https://www.idris-lang.org/) and [Idris-JVM](https://github.com/mmhelloworld/idris-jvm). Idris-JVM will start a server when you run its setup which allows it to compile Idris files to JVM classes. 

## SBT commands

    > sbt karafBuild

Builds the Karaf features file and dependent jar files, places them in ./target/karaf directory.

    > sbt karafDeploy
    
Deploys the Karaf features file and dependent jar files to the environment variable paths as set eg, KARAF_HOME, KARAF_DEPLOY, KARAF_JAR_DIRECTORY.

After deploying Karaf then start Karaf (or it could already be started), if the features file is deployed to Karaf then it should auto-install, else start it.

Inside the Karaf shell, after running Karaf, for example:

    > feature:install JMScalaJs

## Environment variables

The following is used by the MicroService when running.

**JUMPMICRO_CONFIG_PATH** Full path to the configuration file, default value is "jumpmicro.conf" which is the configuration file in the current running directory.

The following is used by the SBT build.sbt.

If Karaf is the OSGi target being used and is accessable from this SBT project via the file system the the following environment variables should be set.

**KARAF_HOME** Full path to Karaf on file system, for example "C:\\home\\software\\apache-karaf-4.0.8\\apache-karaf-4.0.8"

**KARAF_DEPLOY** Full path to a writeable Karaf deployment directory on file system, for example "C:\\home\\software\\apache-karaf-4.0.8\\apache-karaf-4.0.8\\deploy"

**KARAF_JAR_DIRECTORY** Full path to a writeable directory to allow dependency JAR file to be written to on the file system, for example "C:\\home\\software\\apache-karaf-4.0.8\\apache-karaf-4.0.8\\jars"
 
## How to run?
 
Choose one of the following:
 
* On the command line use SBT to do `sbt run`.
* Run `felix.bat` on Windows which creates an OSGi bundle and runs in a [Felix](http://felix.apache.org/) OSGi container. Refer to the folder `target/launcher` to see the [Felix](http://felix.apache.org/) bundle.
* [Apache Karaf](http://karaf.apache.org/) can be deployed to and is being tested.
* [Knopflerfish](http://www.knopflerfish.org/) is not tested yet.
* [Eclipse Virgo](http://www.eclipse.org/virgo/) is not tested yet.

## Configuration

Configuration is initially loaded from a config file at this environment variable path **JUMPMICRO_CONFIG_PATH**.

Then secondly, configuration is loaded from Neo4J if it exists in the Neo4J entity named MicroConfig with the nodeId matching the **jumpmicro.nodeid** in the configuration file.

## Sugggested development environment

Use IntelliJ IDEA with the following plugins enabled, enable them in `File / Settings / Plugins`

* Scala
* Apache Camel IDEA Plugin

## Principles

* [Use a bounded context](https://martinfowler.com/bliki/BoundedContext.html). A single MicroService should have a purpose which can be defined in about a paragraph of text.

## Potentially useful libraries

* [Project Reactor](https://github.com/sinwe/reactor-core-scala)

## FAQ: Why opinionated?

I have opinions so I prefer to do things in a particular way. If you wish to diverge to a different way, you are welcome to do that and I am open to ideas and influence on how to do things, please contribute ideas and code.

## FAQ: Why OSGi?

Why OSGI? [https://www.osgi.org/developer/benefits-of-using-osgi/](https://www.osgi.org/developer/benefits-of-using-osgi/)

* OSGi is a module system which allows for code to be loaded, started and stopped easily.
* Can share resources between OSGi bundles (modules) in the same process leading to a more efficient runtime for running multiple modules on the same machine. No need to have one process per MicroService, have one process for many MicroServices.
* OSGi module system works and is reliable as a module system.
* The container is not heavyweight, it does not provide a lot of services, it just allows services to work together as modules.
 
## FAQ: What about Java 9 Modules?
 
[Java modules](https://en.wikipedia.org/wiki/Java_Module_System) deferred to a Java 9 release in 2017 looks like a new and good idea but OSGi, although boring, works. I'm happy to move to Java Modules once I understand it. I'd like to integrate Java modules to work with this so that older OSGi JARs will work.  

## FAQ: Shouldn't MicroServices be smaller?

I am of the opinion that a MicroService should be a bounded context and that the size in code is not so important but the abstractions are more important.

Consider the video [Implementing Microservices with Scala and Akka - by Vaughn Vernon](https://www.youtube.com/watch?v=19rbbQ46LB4)

## FAQ: Why Neo4J vs other databases?

Graphs are portable data structures and can be easily moved from one database to another, to and from a web browser and also processed in memory. Their schema is their structure, where-as databases have schemas and it is relatively costly to move data of differing schemas around.

There is little impedience mismatch between a graph of Scala Objects in memory and a graph in a graph database when compared with the impedience mismatch of objects and databases as found by users of object-to-relational database mappers.

Data in the graph database should be seen as a source of truth from which things come from, not necessarily as a the on-going state of some system. This distinction means for example a stateful Akka actor may load its initial state from Neo4J but it can keep its ongoing events in a different event log ready for replay when the Akka actor will replay events to restore its state. 

The Neo4J data should change infrequently and represent meaningful data, it is a good idea to think of updating Neo4J data after a period no less than a few seconds for a update on some data, certainly not multiple times per second and this gives the general principle to use. Update Neo4J infrequently and represent a truthful and meaningful state from which new states can emerge. 

Akka Actors can use message passing and encapsulate state rather than using the database as the means of communicating state. Communication and communication of state should not take place by placing data into Neo4J then calling another Microservice to do some action where the other Microservice reads that data from Neo4J. You can instead use Akka message passing to communicate state. 

In summary:
 
* Represent meaningful state. 
* Change data infrequently.
* Neo4J should not be the means by which communication takes place, don't share data between MicroServices via the database.

## FAQ: Why not use OSGi Blueprint?

I tried OSGi Blueprints and it didn't work well with Akka, Apache Camel all mixed up together, it was too much of a struggle and now I prefer annotated classes over XML configuration. 

