# JumpMicro Microservice 

JumpMicro is Scala Microservice by convention. This means it is a standard way of writing Microservices by following a set of conventions and examples which lead to the production of a microservice and microservices which cooperate together.
  
It has the following features:

* Is opinionated by default but you can go off and do your own thing, but by doing things in a common way this makes it easier for programmers to understand different MicroServices.
* Can run in an OSGi container or standalone as a normal Java application.
* Supports Karaf and Felix OSGi containers. (others are untested but OSGi is a standard, so should work).
* [Domino](https://github.com/domino-osgi/domino) Domino is a small library for the programming language Scala designed to support developers in writing bundle activators for the Java module system OSGi.
* Can run in a Docker container.
* Supports [Akka Actors](http://akka.io/), [Akka Streams](http://akka.io/), [Monix](https://github.com/monix/monix), [Apache Camel](http://camel.apache.org/) in Akka Actors.
* [Neo4J](https://neo4j.com/) Graph database as the primary data storage using the [Cypher query language](https://neo4j.com/developer/cypher-query-language/).
* [Neo4j OGM](https://github.com/neo4j/neo4j-ogm) as the Object to Graph database mapper.
* Uses asynchronous message passing as the primary means of communication between Microservices.
* Not encouraging HTTP REST, prefer to use Akka Remoting message passing. You can use REST if you wish to.
* [Kamon.io](http://kamon.io/) for  metric recording.

Features can be added to individual MicroServices, the following features exist:

* [Idris language](http://www.idris-lang.org/) compiles, runs and interacts on the JVM in the MicroService using [Idris-JVM](https://github.com/mmhelloworld/idris-jvm) to compile in the sbt project. 

Preferences:

* Prefer Monix over Akka. 

## Principles

* [Use a bounded context](https://martinfowler.com/bliki/BoundedContext.html). A single MicroService should have a purpose which can be defined in about a paragraph of text.

## How to create a new MicroService

Creation of a new MicroService is done by copying the code of another existing JumpMicro MicroService. In the normal case this would be the copying of a MicroService which acts as a template for a new MicroService, an example is a ScalaJS oriented MicroService whos job is to produce Javascript from ScalaJS code for a particular application. Also it is easier to copy existing code which is close to what you want rather than developing new code.

## FAQ: Why opinionated?

I have opinions so I prefer to do things in a particular way. If you wish to diverge to a different way, you are welcome to do that and I am open to ideas and influence on how to do things, please contribute ideas and code.

## FAQ: Why Neo4J vs other databases?

Graphs are portable data structures and can be easily moved from one database to another, to and from a web browser and also processed in memory. Their schema is their structure, where-as databases have schemas and it is relatively costly to move data of differing schemas around.

Data in the graph database should be seen as a source of truth from which things come from, not necessarily as a the on-going state of some system. This distinction means for example a stateful Akka actor may load its initial state from Neo4J but it can keep its ongoing events in a different event log ready for replay when the Akka actor will replay events to restore its state. 

The Neo4J data should change infrequently and represent meaningful data, it is a good idea to think of updating Neo4J data after a minimum period of at least a few seconds, certainly not 100 times per second and this gives the general principle to use. Update Neo4J infrequently and represent a truthful and meaningful state from which new states can emerge. 

Remember Akka Actors can use message passing and encapsulate state rather than using the database as the means of communicating state. Communication and communication of state should not take place by placing data into Neo4J then calling another Microservice to do some action where the other Microservice reads that data from Neo4J. You can instead use Akka message passing to communicate state. 

In summary:
 
* Represent meaningful state. 
* Change data infrequently.
* Neo4J should not be the means by which communication takes place.

### ------------------------  RUBBISH NOTES AFTER THIS

Run your Scala/Java program in OSGi as either a **standalone JAR** OR deploy to **Apache Felix** OR **Apache Karaf**. In other words, run-once run-anywhere OSGi and a reasonably good way to start writing an OSGi program (if you agree with my opinionated way to do it).

If you found OSGi was difficult to get started with, this would be a good way to get started. 

### Features

* Can run standalone on command line without supplying an OSGi container by using Apache Felix OSGi in embedded mode
* Can create an OSGi bundle artifact intended to be loaded within a OSGi container such as Felix, Karaf or others which have not been tested (ServiceMix, Equinox, etc)
* Integrates Akka, Apache Camel, Neo4J in the OSGi environment
* Scala DSL for Apache Camel for writing Camel routes in concise Scala code 

### Description

Is a MicroService library joining together [Apache Camel](http://camel.apache.org/), [Akka](http://akka.io/), Declarative Services to allow for the development of small microservices which can communicate and be combined into a larger application architecture.  

### Discussion

These mentioned technologies are very power but often its difficult to get started joining them together into a base project to write code in. The _intent here_ is to provide a base project for development with some or all of these libraries, this reduces the amount of time required for getting started. It is relatively time consuming and difficult to get started using all these libraries in OSGi without this base project and that is a general barrier to entry for what is actually very good technology.

This project also provides example code so that it is easy to see how to accomplish common tasks. Overall this reduces the barrier to getting started and using the libraries involved as they are very powerful and a lot can be accomplished in a few lines of Apache Camel code which would have required hundreds of lines of normal code. 

This code base is opinionated and prefers one library over many others which could solve the same problem, if you feel the need to use different libaries then you are free to do so and contribute a pull request.

### Environment variables

Configuration can be passed in via environment variables and these will override any configuration values in jumpmicro.conf. 

JUMPMICRO_IDRISJVM_COMPILER_PATH Is the path to Idris JVM compiler eg.
/home/projects/git/idris-jvm/bin/idrisjvm.bat

### Static analysis

Copy paste detector can be run by typing `sbt cpd`

### Running from command line

This allows the program to run standalone, in fact it starts Apache Felix but for all purposes it acts as thought this is a standalone program running from dos.

Go to core directory (cd core) and type `sbt run`

### Running from Felix

Type 

```
> sbt deployLauncher:package

cd target\launcher

java -Dorg.osgi.framework.bootdelegation=sun.misc -jar lib/org.apache.felix.main-5.0.0.jar
```

### Build for Karaf

Not currently working

Go to core directory (cd core) and type 

```
> sbt osgiBundle
```

There is a features-works.xml file in the doc directory, edit it to set the location of the bundle and place this in deploy directory of Karaf.

Type Karaf

```
> feature:install jump_micro
```

### Documentation

[In the doc directory click here.](doc)

### Tasks to do

1. Resource Sharing. Akka ActorSystem and other resources should be shared between OSGi bundles running within the same OSGi container IF this is running inside an OSGi container.
2. jumpmicro.com website.
3. Idris language integration sample code by allowing Idris to compile to Javascript and having the Java code run the Javascript code.
   Add Idris Language by using Docker https://github.com/mmhelloworld/idris-jvm
4. Docker integration.
5. Program which take as input a set of files and merge them with this project to produce a microservice, rather than what is currently done which is to
   copy this microservice to another folder and rename parts inside.
6. Add Alpakka as it does Akka stream connectors for Akka like Apache Camel. https://github.com/akka/alpakka
 
 ### Tests to do
 
 1. Make sure bundle can install, start and stop within Karaf and when it stops and uninstalls that all is clean.
 
 2. 