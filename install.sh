#!/bin/bash

PLUGINS="wycc wycli wyc wyqc"

mvn -DskipTests=true -Dmaven.javadoc.skip=true package

for p in $PLUGINS
do
    echo "INSTALLING $p => $WHILEYHOME/lib"
    cp $p/target/$p-*.jar $WHILEYHOME/lib
done


