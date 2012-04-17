#!/bin/sh

files=$(ls *.whiley)

for f in $files 
do
    echo $f
    cat $f | sed -e "s/* from whiley.lang.*/println from whiley.lang.System/g" > $f.new
    rm $f
    mv $f.new $f
done
