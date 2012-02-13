#!/bin/sh

files=$(ls *.whiley)

for f in $files 
do
    echo $f
    cat $f | sed -e "s/spawn/new/g" | sed -e "s/process/ref/g" > $f.new
    rm $f
    mv $f.new $f
done
