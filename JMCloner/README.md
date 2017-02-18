## Jump MicroService

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