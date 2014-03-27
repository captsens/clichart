#!/bin/sh

# Upload all the docs to Sourceforge
#

BASE_DIR=`dirname $0`/..
DOCS_DIR=$BASE_DIR/docs
UPLOAD_DIR=$DOCS_DIR/output-sf

SF_DOC_DIR="captsens,jcodereview@web.sourceforge.net:/home/groups/c/cl/clichart/htdocs"

set -xv

$BASE_DIR/bin/buildHomepage.sh

echo "Uploading"
scp -pr $UPLOAD_DIR/* $SF_DOC_DIR



