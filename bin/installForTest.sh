#!/bin/bash

# Install newly-built version under target/install-test (using virtualenv), ready for testing

INSTALL_BASE=target/install-test
BUILD_DIR=target/package

function fail() {
    echo $1
    exit 1
}

if [ "$1" = "" ]; then
    fail "Need a version number to install"
fi

version=$1

[ -d $INSTALL_BASE ] || mkdir -p $INSTALL_BASE

installDir=$INSTALL_BASE/clichart-$version
if [ -d $installDir ]; then
    rm -fR $installDir/*
else
    mkdir $installDir
fi

if [ ! -f $BUILD_DIR/clichart-$version.zip ]; then
    fail "Can't find base package: $BUILD_DIR/clichart-$version.zip"
fi

unzip -d $installDir $BUILD_DIR/clichart-$version.zip

virtualEnv=$installDir/virtualenv
echo "Installing virtualenv in $virtualEnv"
virtualenv $virtualEnv
. $virtualEnv/bin/activate

cat << EOM

Now do the following:
   . $installDir/virtualenv/bin/activate
   easy_install $installDir/clichart-${version}/clichart-${version}.egg
   bin/testInstall.py
   deactivate
EOM
