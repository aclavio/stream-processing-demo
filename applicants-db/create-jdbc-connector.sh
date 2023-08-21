#!/bin/sh

curl -X POST http://localhost:8083/connectors -H "Content-Type: application/json" -d @jdbc-connector-config.json | jq .