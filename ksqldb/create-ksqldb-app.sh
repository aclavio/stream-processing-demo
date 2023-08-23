#!/bin/sh

docker compose -f infrastructure/docker-compose.yml cp ksqldb/create-applicants-stream.sql ksqldb-cli:/tmp/
docker compose -f infrastructure/docker-compose.yml exec ksqldb-cli ksql -f /tmp/create-applicants-stream.sql -- http://ksqldb-server:8088