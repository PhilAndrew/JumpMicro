FROM 1science/sbt:0.13.8-oracle-jre-8
RUN apk add --update git
RUN git clone https://github.com/PhilAndrew/neo4j-ogm-osgi.git /app/neo4j-ogm-osgi
WORKDIR /app/neo4j-ogm-osgi
RUN sbt publishLocal
RUN git clone https://github.com/PhilAndrew/JumpMicro.git /app/JumpMicro
WORKDIR /app/JumpMicro/JMCloner
RUN sbt run
