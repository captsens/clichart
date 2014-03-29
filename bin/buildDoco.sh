#!/bin/bash
# NOTE: substitution doesn't work the same using dash...

# Build the HTML doco using sphinx, and the Javadoc, then copy all the files to target directory

DOCS_DIR=resource/docs
TEXT_DIR=$DOCS_DIR/source
HTML_BUILD_DIR=$DOCS_DIR/build/html
IMAGE_DIR=$DOCS_DIR/source/_static/images
JAVADOC_BUILD_DIR=target/java/javadoc

OUTPUT_DIR=target/docs
TEXT_OUTPUT_DIR=$OUTPUT_DIR/text
HTML_OUTPUT_DIR=$OUTPUT_DIR/html
JAVADOC_OUTPUT_DIR=$OUTPUT_DIR/javadoc

#set -xv

if [ -d $OUTPUT_DIR ]; then
    echo "Removing contents of output directory"
    rm -fR $OUTPUT_DIR/*
fi
echo "Creating output directory"
mkdir -p $HTML_OUTPUT_DIR
mkdir -p $TEXT_OUTPUT_DIR
mkdir -p $JAVADOC_OUTPUT_DIR

echo "Generating small images (if required)"
imagesUpdated="N"
for imagePath in $IMAGE_DIR/*.png; do
    echo $imagePath | grep -q -- "-small"
    isSmallImage=$?
    if [ $isSmallImage -eq 0 ]
    then
        continue
    fi
    smallImagePath=`dirname $imagePath`/`basename $imagePath .png`-small.png
    if [ ! -f $smallImagePath -o $imagePath -nt $smallImagePath ]
    then
        echo "  $smallImagePath"
        convert $imagePath -resize 50% -depth 8 $smallImagePath
        imagesUpdated="Y"
    fi
done
if [ "$imagesUpdated" = "Y" ]
then
    echo "NOTE: You must commit the updated images in git!"
fi

echo "Generating documentation in Spinx"
(cd $DOCS_DIR; make html)

echo "Copying text and html files"
cp -a $HTML_BUILD_DIR/* $HTML_OUTPUT_DIR
cp -a $TEXT_DIR/*.rst $TEXT_OUTPUT_DIR

echo "Generating and copying Javadoc"
ant javadoc
cp -a $JAVADOC_BUILD_DIR/* $JAVADOC_OUTPUT_DIR

