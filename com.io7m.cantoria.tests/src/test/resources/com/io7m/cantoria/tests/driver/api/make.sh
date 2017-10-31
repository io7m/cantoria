#!/usr/bin/env bash
set -e
set -x

for f in *
do
  pushd "$f" && faketime '2000-01-01T00:00:00Z' ./make.sh && popd
done
