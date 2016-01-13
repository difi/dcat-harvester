#!/bin/bash 
mvn install -DskipTests
cd docker
docker-compose build $1 && docker-compose up -d
