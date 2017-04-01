## Docker Image

### The Dockerfile

The Dockerfile allows JMCloner to run.

docker build -t jmcloner .

docker run -p 8181:8181 jmcloner sbt run

