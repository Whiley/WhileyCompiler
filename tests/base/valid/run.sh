#!/bin/sh

files=$(ls *.whiley)

for f in $files 
do
    echo $f
    cat $f | sed -e "s/args/sys.args/g" > $f.new
    rm $f
    mv $f.new $f
done
