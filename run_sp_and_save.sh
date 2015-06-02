#!/bin/bash

# getting the seed
SEED=$1

# running the synthetic population
java -jar syntheticPopulation.jar $SEED "2006_cd.csv"

# copying the results into a new directory
mkdir outputs/output_$SEED
cp -r post\ processing/output\ tables/* /outputs/output_$SEED/
