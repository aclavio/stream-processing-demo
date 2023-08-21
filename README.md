# Confluent Stream Processing Demo

## Start Confluent Platform

```
docker compose -f infrastructure/docker-compose.yml up -d
```

## Initialize the Confluent environment
Allow the platform time to start up.  Wait for the [Control Center](http://localhost:9021) to show the cluster is healthy.

```
infrastructure/initialize-environment.sh
```

## Start the postgresql database

```
docker compose -f applicants-db/docker-compose.yml up -d 
```

## Populate the database

```
applicants-db/initialize-database.sh
```

## Start the JDBC connector

```
applicants-db/create-jdbc-connector.sh
```

## Start the java producer

```
TODO
```

## Start the java consumer

```
TODO
```

## Create the kSQLdb application

```
TODO
```

## Start the Kafka Streams application

```
TODO
```

## Publish some data

TODO

## Cleanup

```
docker compose -f applicants-db/docker-compose.yml  down
docker compose -f infastructure/docker-compose.yml  down
```