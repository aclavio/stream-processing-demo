#!/bin/sh

PUBLISH_ENDPOINT="localhost:8080/publish"

curl -X POST $PUBLISH_ENDPOINT -H 'Content-type:application/json' -d \
'{ "id": "2", "firstName": "Jane", "lastName": "Doe", "phone": "", "portName": "BETA", "notes": "test" }'

curl -X POST $PUBLISH_ENDPOINT -H 'Content-type:application/json' -d \
'{ "id": "4", "firstName": "John", "lastName": "Doe", "phone": "", "portName": "ALPHA", "notes": "" }'

curl -X POST $PUBLISH_ENDPOINT -H 'Content-type:application/json' -d \
'{ "id": "", "firstName": "John", "lastName": "Doe", "phone": "", "portName": "ALPHA", "notes": "" }'

curl -X POST $PUBLISH_ENDPOINT -H 'Content-type:application/json' -d \
'{ "id": "1", "firstName": "Bilbo", "lastName": "Baggins", "phone": "", "portName": "ALPHA", "notes": "" }'

curl -X POST $PUBLISH_ENDPOINT -H 'Content-type:application/json' -d \
'{ "id": "2", "firstName": "Frodo", "lastName": "Baggins", "phone": "", "portName": "GAMMA", "notes": "" }'

curl -X POST $PUBLISH_ENDPOINT -H 'Content-type:application/json' -d \
'{ "id": "", "firstName": "Samwise", "lastName": "Gamgee", "phone": "", "portName": "GAMMA", "notes": "" }'

curl -X POST $PUBLISH_ENDPOINT -H 'Content-type:application/json' -d \
'{ "id": "", "firstName": "Pippin", "lastName": "Took", "phone": "", "portName": "OMEGA", "notes": "" }'