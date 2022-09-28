#!/bin/bash

echo Drop Database
mongo 'tenure-cost-and-trade-records' --eval 'db.dropDatabase()'

echo Load Data
mongoimport --db tenure-cost-and-trade-records --collection credentials --file test-data.json --jsonArray;

echo Confirm Loaded Records
mongo 'tenure-cost-and-trade-records' --eval 'db.credentials.find()'
