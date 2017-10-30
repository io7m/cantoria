#!/usr/bin/env bash
set -e
set -x

rm -rfv out
cp src/x.y.z/module-info.java.input src/x.y.z/module-info.java
/usr/lib/jvm/java-9-openjdk/bin/javac -d out/x.y.z `find src -name '*.java' -type f`
/usr/lib/jvm/java-9-openjdk/bin/javac -d out/x.y.z -source 1.7 -target 1.7 src/x.y.z/x/y/z/p/X.java
pushd out
pushd x.y.z
faketime '2000-01-01T00:00:00Z' jar cf ../module.jar .
popd
popd
mv out/module.jar .
rm -rfv out
rm -f src/x.y.z/module-info.java
