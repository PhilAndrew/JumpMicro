
Need to store data somewhere, it could be a normal database but I choose Neo4J.

A Neo4J server can run in Docker. Assuming you have a boot2docker or any docker server then this can join to that Docker server and run Neo4J.

https://neo4j.com/developer/docker/

To run in a simple way.

    docker run \
    --publish=7474:7474 --publish=7687:7687 \
    --volume=$HOME/neo4j/data:/data \
    neo4j:3.0
    