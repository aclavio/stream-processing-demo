#!/bin/sh

# Configure Login
export CONFLUENT_REST_URL=http://localhost:8082
export CONFLUENT_PLATFORM_MDS_URL=http://localhost:8082
#export CONFLUENT_PLATFORM_USERNAME=
#export CONFLUENT_PLATFORM_PASSWORD=

#confluent login

# Create Topics
echo "...Creating topics..."
confluent kafka topic create port.entries.avro --if-not-exists --partitions 3 --no-authentication --url $CONFLUENT_REST_URL
confluent kafka topic create postgresql.applicants --if-not-exists --partitions 3 --no-authentication --url $CONFLUENT_REST_URL
confluent kafka topic create port.entries.enriched.kstreams.avro --if-not-exists --partitions 3 --no-authentication --url $CONFLUENT_REST_URL
confluent kafka topic create port.entries.enriched.ksql.avro --if-not-exists --partitions 3 --no-authentication --url $CONFLUENT_REST_URL

# install jdbc connector
echo "...Installing JDBC connector..."
docker compose exec connect confluent-hub install --no-prompt confluentinc/kafka-connect-jdbc:10.7.4
docker compose restart connect

echo "Initialization complete!"