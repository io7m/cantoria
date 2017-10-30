#!/bin/sh
pushd before && ./make.sh && popd
pushd after && ./make.sh && popd
