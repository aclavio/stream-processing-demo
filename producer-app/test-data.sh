#!/bin/sh

PUBLISH_ENDPOINT="localhost:8080/publish"

curl -X POST $PUBLISH_ENDPOINT -H 'Content-type:application/json' -d \
'{ "firstName": "Jane", "lastName": "Doe", "portName": "BETA", "latitude": "0.0", "longitude": "0.0", "notes": "test" }'

curl -X POST $PUBLISH_ENDPOINT -H 'Content-type:application/json' -d \
'{ "firstName": "John", "lastName": "Doe", "portName": "ALPHA", "latitude": "0.0", "longitude": "0.0", "notes": "" }'

curl -X POST $PUBLISH_ENDPOINT -H 'Content-type:application/json' -d \
'{ "firstName": "John", "lastName": "Doe", "portName": "ALPHA", "latitude": "0.0", "longitude": "0.0", "notes": "" }'

curl -X POST $PUBLISH_ENDPOINT -H 'Content-type:application/json' -d \
'{ "firstName": "Bilbo", "lastName": "Baggins", "portName": "ALPHA", "latitude": "0.0", "longitude": "0.0", "notes": "" }'

curl -X POST $PUBLISH_ENDPOINT -H 'Content-type:application/json' -d \
'{ "firstName": "Frodo", "lastName": "Baggins", "portName": "GAMMA", "latitude": "0.0", "longitude": "0.0", "notes": "" }'

curl -X POST $PUBLISH_ENDPOINT -H 'Content-type:application/json' -d \
'{ "firstName": "Samwise", "lastName": "Gamgee", "portName": "GAMMA", "latitude": "0.0", "longitude": "0.0", "notes": "" }'

curl -X POST $PUBLISH_ENDPOINT -H 'Content-type:application/json' -d \
'{ "firstName": "Pippin", "lastName": "Took", "portName": "OMEGA", "latitude": "0.0", "longitude": "0.0", "notes": "" }'