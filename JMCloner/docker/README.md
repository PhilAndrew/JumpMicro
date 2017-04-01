## Docker Image

This can be run from within a docker environment.

### The Dockerfile

The Dockerfile allows JMCloner to run, in the directory where the Dockerfile exists do the following.

    docker build -t jmcloner .

    docker run -p 8181:8181 jmcloner sbt run

### Running Neo4J database

Neo4j is not required but is used if available.

    docker run --publish=7474:7474 --publish=7687:7687 --volume=$HOME/neo4j2/data:/data neo4j:3.0

### Running as a daemon process

Docker can run processes as daemon, use the ```-d``` command line option. In which case starting Neo4J with the ```-d``` option is as follows.

    docker run -d --publish=7474:7474 --publish=7687:7687 --volume=$HOME/neo4j2/data:/data neo4j:3.0
