#!/bin/sh

echo "...Copying Files..."
docker compose cp sql/create-table.sql db:/tmp/
docker compose cp sql/initialize-table.sql db:/tmp/

echo "...Creating Applicants Table..."
docker compose exec db psql --username postgres -f /tmp/create-table.sql

echo "...Loading initial data..."
docker compose exec db psql --username postgres -f /tmp/initialize-table.sql

echo "...Complete..."


