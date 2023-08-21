#!/bin/sh

#KSQL_SERVER_URL=http://localhost:8088

#KSQL_STATEMENTS=`cat create-applicants-stream.sql`
#echo $KSQL_STATEMENTS

#curl -v -X POST $KSQL_SERVER_URL/ksql \
#  -H "Accept: application/vnd.ksql.v1+json" \
#  -d $'{
#    "ksql": "LIST STREAMS;",
#    "streamProperties": {}
#  }'

docker compose -f infrastructure/docker-compose.yml cp ksqldb/create-applicants-stream.sql ksqldb-cli:/tmp/
docker compose -f infrastructure/docker-compose.yml exec ksqldb-cli ksql -f /tmp/create-applicants-stream.sql -- http://ksqldb-server:8088