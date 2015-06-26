#!/bin/bash

# getting the seed
SEED=$1

# running the synthetic population
java -jar SPGen.jar $SEED "2006_cd.csv"

