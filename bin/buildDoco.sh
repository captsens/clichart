#!/bin/bash
# NOTE: substitution doesn't work the same using dash...

# Build the HTML doco using docutils

# Use rst2html (from the python-docutils package) to generate all HTML docs

BASE_DIR=`dirname $0`/..
DOCS_DIR=$BASE_DIR/resource/docs
RST_DIR=$DOCS_DIR/rst
OUTPUT_DIR=target/docs
HTML_OUTPUT_DIR=$OUTPUT_DIR/html
TXT_OUTPUT_DIR=$OUTPUT_DIR/txt
STYLESHEET=clichart-doc.css
IMAGE_DIR=$DOCS_DIR/images
JAVADOC_DIR=$BASE_DIR/target/java/javadoc

export JAVA_HOME=/usr

RST2HTML="/usr/bin/rst2html"

#set -xv

if [ -d $OUTPUT_DIR ]; then
    echo "Removing contents of output directory"
    rm -fR $OUTPUT_DIR/*
fi
echo "Creating output directory"
mkdir -p $HTML_OUTPUT_DIR
mkdir -p $TXT_OUTPUT_DIR

# copy the stylesheet - it's required by rst2html
cp $DOCS_DIR/$STYLESHEET $HTML_OUTPUT_DIR

# and the images
mkdir $HTML_OUTPUT_DIR/images
cp -a $IMAGE_DIR/*.png $HTML_OUTPUT_DIR/images

echo "Generating small images"
for f in $IMAGE_DIR/*.png; do
    convert $f -resize 50% -depth 8 $HTML_OUTPUT_DIR/images/`basename $f .png`-small.png
done

for f in `find $RST_DIR -name "*.txt" | grep -v .svn `; do
    relativePath=`echo $f | sed -e "s|$RST_DIR||"`
    if [ "${relativePath:0:1}" = "/" ]; then
        relativePath=${relativePath:1}
    fi

    outputPath=$HTML_OUTPUT_DIR/`dirname $relativePath`/`basename $relativePath .txt`.html

    dirName=`dirname $outputPath`
    if [ ! -d $dirName ]; then
        mkdir -p $dirName
    fi
    echo "Generating $outputPath"
    $RST2HTML --stylesheet-path=$HTML_OUTPUT_DIR/$STYLESHEET --link-stylesheet $f $outputPath
done

# add in the CHANGES page too
echo "Generating CHANGES page"
$RST2HTML --stylesheet-path=$HTML_OUTPUT_DIR/$STYLESHEET $BASE_DIR/CHANGES.txt $HTML_OUTPUT_DIR/changes.html

# copy all the text files as well
echo "Copying text files"
cp $DOCS_DIR/rst/*.txt $TXT_OUTPUT_DIR
cp $BASE_DIR/CHANGES.txt $TXT_OUTPUT_DIR

echo "Generating and copying Javadoc"
(cd `dirname $0`/..; ant javadoc)
cp -a $JAVADOC_DIR $HTML_OUTPUT_DIR/api

