# JumpMicro Microservice [![License][licenseImg]][licenseLink] 

[licenseImg]: https://img.shields.io/github/license/PhilAndrew/JumpMicro.svg
[licenseImg2]: https://img.shields.io/:license-mit-blue.svg
[licenseLink]: LICENSE

JumpMicro is Scala Microservice **by convention**, a standard way of writing Microservices by following a set of conventions and examples which lead to the production of a microservice. Neither a library or a framework, a way of doing things.

## What do you need to setup?

JMScalaJs wants to use a Neo4J database if it is available as that is the default database for storage, but not required to run. Refer to the file jumpmicro.conf for connection settings. 

## How to get started

Go to the JMScalaJs directory and

    > sbt run

This runs it in an embedded Felix OSGi container.

When it has started the console will state 

    [ActorSystem-akka.actor.default-dispatcher-5] INFO jumpmicro.jmscalajs.impl.actor.WebServer - Server online at http://localhost:8080/
    
Then go to http://localhost:8080/test

You are looking at an Akka Http server delivering a ScalaJs page running in OSGi in Felix Embedded.

## Deploy to Karaf

The normal build steps to get it to Karaf as

1. Compile it

    > sbt clean update compile
    
2. Osgi Bundle

    > sbt osgiBundle
    
3. Build features file for Karaf and deploy it to Karaf

    > sbt karafBuild karafDeploy

## MicroService examples available

| MicroService Name        | What does it do?                                                             |
| ------------------------ |:----------------------------------------------------------------------------:|
| JMScalaJs                | Akka Http server with ScalaJS running in webpage http://localhost:8080/test/ |
| JMCloner                 | Allow creation of a new MicroService by copying one to another<br>After sbt run the visit http://localhost:8181/        |

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
* [Neo4j OGM](https://github.com/neo4j/neo4j-ogm) as the Object to Graph database mapper with a modified version for OSGi [Neo4J OGM OSGi](https://github.com/dkrizic/neo4j-ogm-osgi-demo).
* [Dropwizard Metrics](http://metrics.dropwizard.io/) for recording Metrics of the running application to ensure health of your application using [Metrics-Scala](https://github.com/erikvanoosten/metrics-scala) for  metrics recording, [documentation here](https://github.com/erikvanoosten/metrics-scala/blob/master/docs/Manual.md) with documentation for [Akka Actos](https://github.com/erikvanoosten/metrics-scala/blob/master/docs/Actors.md), [Futures](https://github.com/erikvanoosten/metrics-scala/blob/master/docs/Futures.md), [all docs](https://github.com/erikvanoosten/metrics-scala/tree/master/docs) and [3rd party libraries](http://metrics.dropwizard.io/3.1.0/manual/third-party/).
* [Korolev](https://github.com/fomkin/korolev) web framework, Single-page applications running on the server side
* Can run in a Docker container.
* [Idris language](http://www.idris-lang.org/) compiles, runs and interacts on the JVM in the MicroService using [Idris-JVM](https://github.com/mmhelloworld/idris-jvm) to compile in the sbt project. 

## Suggested libaries

* [Binding.scala](https://github.com/ThoughtWorksInc/Binding.scala) for HTML on client side
* [AutoWire](https://github.com/lihaoyi/autowire) for RPC communications between ScalaJS and a server

## SBT commands

    > sbt karafBuild

Builds the Karaf features file and dependent jar files, places them in ./target/karaf directory.

    > sbt karafDeploy
    
Deploys the Karaf features file and dependent jar files to the environment variable paths as set eg, KARAF_HOME, KARAF_DEPLOY, KARAF_JAR_DIRECTORY.

After deploying Karaf then start Karaf (or it could already be started), if the features file is deployed to Karaf then it should auto-install, else start it.

    > feature:install JMScalaJs

## Environment variables

**JUMPMICRO_CONFIG_PATH** Full path to the configuration file, default value is "jumpmicro.conf" which is the configuration file in the current running directory.

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

## How to create a new MicroService

Creation of a new MicroService is done by copying the code of another existing MicroService. In the normal case this would be the copying of a MicroService which acts as a template for a new MicroService, an example is a ScalaJS oriented MicroService whos job is to produce Javascript from ScalaJS code for a particular application. Also it is easier to copy existing code which is close to what you want rather than developing new code.

## Potentially useful libraries

* [Project Reactor](https://github.com/sinwe/reactor-core-scala)

## FAQ: Why opinionated?

I have opinions so I prefer to do things in a particular way. If you wish to diverge to a different way, you are welcome to do that and I am open to ideas and influence on how to do things, please contribute ideas and code.

## FAQ: Why OSGi?

Why OSGI? [https://www.osgi.org/developer/benefits-of-using-osgi/](https://www.osgi.org/developer/benefits-of-using-osgi/)

* OSGi is a module system which allows for code to be loaded, started and stopped easily.
* Can share resources between OSGi bundles (modules) in the same process leading to a more efficient runtime for running multiple modules on the same machine. No need to have one process per MicroService, have one process for many MicroServices.
* OSGi module system works and is reliable as a module system.
 
## FAQ: What about Java 9 Modules?
 
[Java modules](https://en.wikipedia.org/wiki/Java_Module_System) deferred to a Java 9 release in 2017 looks like a new and good idea but OSGi, although boring, works. I'm happy to move to Java Modules once I understand it. 

## FAQ: Why Neo4J vs other databases?

Graphs are portable data structures and can be easily moved from one database to another, to and from a web browser and also processed in memory. Their schema is their structure, where-as databases have schemas and it is relatively costly to move data of differing schemas around.

There is little impedience mismatch between a graph of Scala Objects in memory and a graph in a graph database when compared with the impedience mismatch of objects and databases as found by users of object-to-relational database mappers.

Data in the graph database should be seen as a source of truth from which things come from, not necessarily as a the on-going state of some system. This distinction means for example a stateful Akka actor may load its initial state from Neo4J but it can keep its ongoing events in a different event log ready for replay when the Akka actor will replay events to restore its state. 

The Neo4J data should change infrequently and represent meaningful data, it is a good idea to think of updating Neo4J data after a period no less than a few seconds for a update on some data, certainly not multiple times per second and this gives the general principle to use. Update Neo4J infrequently and represent a truthful and meaningful state from which new states can emerge. 

Akka Actors can use message passing and encapsulate state rather than using the database as the means of communicating state. Communication and communication of state should not take place by placing data into Neo4J then calling another Microservice to do some action where the other Microservice reads that data from Neo4J. You can instead use Akka message passing to communicate state. 

In summary:
 
* Represent meaningful state. 
* Change data infrequently.
* Neo4J should not be the means by which communication takes place.

## FAQ: Why not use OSGi Blueprint?

I tried OSGi Blueprints and it didn't work well with Akka, Apache Camel all mixed up together, it was too much of a struggle and now I prefer annotated classes over XML configuration. 

