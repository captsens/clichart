#!/bin/sh

# Build the SF homepage

BASE_DIR=`dirname $0`/..
DOCS_DIR=$BASE_DIR/resource/docs
HOME_PAGE_DIR=$DOCS_DIR/homepage
OUTPUT_DIR=$BASE_DIR/target
DOCS_OUTPUT_DIR=$OUTPUT_DIR/docs
SF_OUTPUT_DIR=$DOCS_OUTPUT_DIR/homepage
IMAGE_DIR=$DOCS_DIR/images
IMAGE_OUTPUT_DIR=$SF_OUTPUT_DIR/images

echo "Updating documentation"
$BASE_DIR/bin/buildDoco.sh

echo "Building SF output dir"
if [ -d $SF_OUTPUT_DIR ]; then
    rm -fR $SF_OUTPUT_DIR
fi
mkdir $SF_OUTPUT_DIR
mkdir $IMAGE_OUTPUT_DIR

cp -a $HOME_PAGE_DIR/*.html $SF_OUTPUT_DIR
cp -a $HOME_PAGE_DIR/*.css $SF_OUTPUT_DIR
cp -a $DOCS_OUTPUT_DIR/html/images/overview*.png $IMAGE_OUTPUT_DIR

echo "Copying clichart doco"
mkdir $SF_OUTPUT_DIR/docs
cp -a $DOCS_OUTPUT_DIR/html/* $SF_OUTPUT_DIR/docs

find $SF_OUTPUT_DIR -name "*.sav" -o -name "*~" -exec rm {} \;
find $SF_OUTPUT_DIR -name ".svn" -exec rm -fR {} \;

echo "DONE!  See $SF_OUTPUT_DIR/index.html"