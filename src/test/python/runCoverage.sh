#!/bin/sh

# Run code coverage for all python tests, placing the results in the target/coverage directory

PYTHON_SCRIPT_DIR=src/main/python
PYTHON_TEST_DIR=src/test/python
OUTPUT_DIR=target/python/coverage

COVERAGE_SCRIPT="coverage"

if [ ! -d $OUTPUT_DIR ]; then
    mkdir -p $OUTPUT_DIR
fi

export COVERAGE_FILE=$OUTPUT_DIR/coverage.data

# erase previous coverage data
$COVERAGE_SCRIPT -e

for f in $PYTHON_TEST_DIR/*Test.py; do
    echo $f
    $COVERAGE_SCRIPT -x $f
done

# now report
$COVERAGE_SCRIPT report $PYTHON_SCRIPT_DIR/*.py

echo "Writing annotated files to $OUTPUT_DIR (with ,cover extension)."
$COVERAGE_SCRIPT -a -d $OUTPUT_DIR $PYTHON_SCRIPT_DIR/*.py
