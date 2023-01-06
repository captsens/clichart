#!/usr/bin/env python3

"""
(C) Copyright 2006-2010, by John Dickson

Project Info:  https://github.com/captsens/clichart

This library is free software; you can redistribute it and/or modify it
under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation; either version 2.1 of the License, or
(at your option) any later version.

This library is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
USA.


Merge 2 or more data files, based on values in the key column.  See usage() for details
"""

import sys, getopt, os, re
from .statslib import *

HEADER_ROW_KEY = ''

# ===============================================================
def usage(msg = None):
    if msg:
        print(msg)
        print()
    print("""
A simple script to merge data from 2 or more data files, based on the value in
the key column of each file.  The output (to stdout) is a single line for each
unique key value, containing the specified column values available in the files
for that key.  Any missing column value is defaulted to '', but this can be
changed with the --defaults option.

Usage:
========
  merge [options] inputFile*

Options:
========
 -c             Data is CSV (default is whitespace-separated)
 -f             First row of file is a header row, and should be skipped
 -h             Show help (this information) and exit
 -l <columns>   A comma-separated list of fileNumber:columnNumber to be
                included in the output, for all non-key columns (the key column
                will be output at the start of each line).  Both file and
                column numbers are 0-based, e.g. "0:1,0:2,1:1,2:4"
 -k <columns>   A comma-separated list of fileNumber:columnNumber specifying
                the key column for each file.  There must be 1 entry per file,
                e.g. "0:0,1:0,2:1"
 --defaults     A comma-separated list of the default values for all non-key
                columns, in the same order as the -l option list.  If omitted,
                values default to the empty string
 --nojit        Disable Psyco Just-In-Time compiler

Example:
========
Two CSV files, key column is first in each, output second and third columns from each file

  merge.py -k 0:0,1:0 -l 0:1,0:2,1:1,1:2 -c file1.csv file2.csv
""")
    sys.exit(1)

# ---------------------------------------------------------------
def processFile(fileIndex, filePath, keyColumns, valueColumns, isCsv, skipFirst):
    """Read the file, and return as a dict of [columnValue*] keyed on the key column"""
    keyIndex = [columnNumber for fileNumber, columnNumber in keyColumns if fileNumber == fileIndex][0]
    valueIndices = [columnNumber for fileNumber, columnNumber in valueColumns if fileNumber == fileIndex]
    results = {}
    for line in open(filePath):
        line = line.strip()
        lineCpts = splitLine(line, isCsv, strip=True)
        key = lineCpts[keyIndex]
        if skipFirst and len(results) == 0:
            key = HEADER_ROW_KEY
        results[key] = [lineCpts[index] for index in valueIndices]
    #print 'File', fileIndex
    #print results
    return results

# ---------------------------------------------------------------
def mergeFileData(dataFromFiles, valueColumns, defaults, isCsv, skipFirst):
    if defaults:
        defaultValues = defaults[:]
    else:
        defaultValues = [''] * len(valueColumns)
    # contains [columnValues] keyed on key
    valuesByKey = {}
    for fileNumber in range(len(dataFromFiles)):
        #outputColumnIndexes = [columnIndex for fileIndex, columnIndex in valueColumns if fileIndex == fileNumber]
        outputColumnIndexes = [i for i in range(len(valueColumns)) if valueColumns[i][0] == fileNumber]
        fileData = dataFromFiles[fileNumber]
        for key, values in list(fileData.items()):
            fullValues = valuesByKey.get(key, defaultValues[:])
            # copy in all the values for this file
            for i in range(len(outputColumnIndexes)):
                outputColumnIndex = outputColumnIndexes[i]
                fullValues[outputColumnIndex] = values[i]
            valuesByKey[key] = fullValues
            #print fileNumber, key, values, fullValues
    if isCsv:
        separator = ', '
    else:
        separator = '  '
    for key in sorted(valuesByKey.keys()):
        print(separator.join([key] + valuesByKey[key]))


# ---------------------------------------------------------------
def parseColumnList(columnList, listType, ):
    try:
        results = []
        for item in columnList.split(','):
            fileNumber, columnNumber = item.split(':')
            results.append((int(fileNumber), int(columnNumber)))
        return results
    except:
        usage('Invalid column list: %s' % listType)

# ---------------------------------------------------------------
def main():
    opts, args = getopt.getopt(sys.argv[1:], 'cfhk:l:', ['defaults=', 'nojit'])
    skipFirst = False
    isCsv = False
    # consists of (fileNumber, columnNumber) pairs
    valueColumns = []
    # consists of (fileNumber, columnNumber) pairs
    keyColumns = []
    defaults = []
    enableJit = True
    for opt, arg in opts:
        if opt == '-h':
            usage()
        elif opt == '-c':
            isCsv = True
        elif opt == '-f':
            skipFirst = True
        elif opt == '--nojit':
            enableJit = False
        elif opt == '-l':
            valueColumns = parseColumnList(arg, '-l')
        elif opt == '-k':
            keyColumns = parseColumnList(arg, '-k')
        elif opt == '--defaults':
            defaults = [item.strip() for item in arg.split(',')]
    filesWithKeys = [fileNumber for fileNumber, columnNumber in keyColumns]
    if sorted(filesWithKeys) != list(range(len(args))):
        usage('One key column per file is required, with file numbers 0 - %d' % (len(args) - 1))
    filesWithValues = [fileNumber for fileNumber, columnNumber in valueColumns]
    if set(filesWithValues) != set(range(len(args))):
        usage('At least one output column per file is required, with file numbers 0 - %d' % (len(args) - 1))
    if defaults and len(defaults) != len(valueColumns):
        usage('Length of defaults list must match the length of the output columns list')
    if len(args) < 2:
        usage('2 or more files are required')

    enablePsyco(enableJit)
    
    dataFromFiles = []
    for index in range(len(args)):
        dataFromFiles.append(processFile(index, args[index], keyColumns, valueColumns, isCsv, skipFirst))
    mergeFileData(dataFromFiles, valueColumns, defaults, isCsv, skipFirst)

# ---------------------------------------------------------------
if __name__ == '__main__':
    main()
