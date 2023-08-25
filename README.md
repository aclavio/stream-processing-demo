# Confluent Stream Processing Demo

## Prerequisites
- [Docker](https://docs.docker.com/get-docker/)
- [Confluent CLI](https://docs.confluent.io/confluent-cli/current/overview.html)
- Bash compatible shell 
  - cygwin on windows should work
- Java 17+ Runtime Environment

## Start Confluent Platform

```
docker compose -f infrastructure/docker-compose.yml up -d
```
Allow the platform time to start up.  Wait for the [Control Center](http://localhost:9021) to show the cluster is healthy.

## Initialize the Confluent environment


```
cd infrastructure && ./initialize-environment.sh
```

## Start the postgresql database

```
docker compose -f applicants-db/docker-compose.yml up -d 
```
The database's Adminer interface will be available at port `8888` on your localhost.  You can login with test user `postgres` and password `example`.

## Populate the database
Next we create the Applicants table in the postgresql database, and populate it with an initial dataset.

```
cd applicants-db && ./initialize-database.sh
```

## Start the JDBC connector
Next we create and start a JDBC connector.  The connector is configured to pull modified records from the Applicants postgresql table, and publish the changes to the `postgresql.applicants` kafka topic.

```
cd applicants-db && ./create-jdbc-connector.sh
```
You may validate the connector is working by viewing the initial set of rows from the Applicants table has been published in the `posgresql.applicants` topic.

## Start the java producer

```
./gradlew producer-app:bootRun
```

## Start the java consumer

```
./gradlew consumer-app:run
```

## Create the kSQLdb application

```
ksqldb/create-ksqldb-app.sh
```

## Start the Kafka Streams application

```
./gradlew kafka-streams:run
```

## Publish some data

Open the web form at http://localhost:8080 and submit some entry data

## Cleanup

```
docker compose -f applicants-db/docker-compose.yml down
docker compose -f infastructure/docker-compose.yml down
```