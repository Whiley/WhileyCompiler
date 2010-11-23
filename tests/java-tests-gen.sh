#!/bin/sh

cd valid/definite
echo "Static valid tests ..."
./static-tests-gen.sh > ../../../wyjc/testing/tests/DefiniteStaticValidTests.java
echo "Runtime valid tests ..."
./runtime-tests-gen.sh > ../../../wyjc/testing/tests/DefiniteRuntimeValidTests.java
cd ../../invalid/definite
echo "Static invalid tests ..."
./static-tests-gen.sh > ../../../wyjc/testing/tests/DefiniteStaticInvalidTests.java
echo "Runtime invalid tests ..."
./runtime-tests-gen.sh > ../../../wyjc/testing/tests/DefiniteRuntimeInvalidTests.java
cd ../simple
echo "Simple invalid tests ..."
./static-tests-gen.sh > ../../../wyjc/testing/tests/SimpleStaticInvalidTests.java
cd ../..
echo "done"
