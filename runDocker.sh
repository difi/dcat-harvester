#!/bin/bash 
mvn install -DMAVEN_OPTS="-Xmx512m" -DskipTests
cd docker
docker-compose build $1 && docker-compose up -d
