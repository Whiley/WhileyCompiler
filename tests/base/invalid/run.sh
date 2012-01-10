#!/bin/sh

files=$(ls *.whiley)

for f in $files 
do
    echo $f
    cat $f | sed -e "s/,[ ]*\[string\] args//g" > $f.new
    rm $f
    mv $f.new $f
done
