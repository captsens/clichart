#!/usr/bin/env python

"""
(C) Copyright 2014, by John Dickson

Project Info:  http://clichart.sourceforge.net/

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


A simple script to calculate derivatives (time-based rate of change) of one or more columns in a tabular 
(CSV or whitespace-separated) data file or stream.

See usage()
"""

import sys, getopt, os, re, datetime
from statslib import *

# ===============================================================
def usage(msg = None):
    if msg:
        print msg
        print
    print """
Output derivatives of columns.
Output is in the same format as the input, i.e. whitespace or comma-separated.

Usage: derivative [options] [inputFile*]

Options:
 -c           Data is CSV (default is whitespace-separated)
 -d fmt       Date/time format in PYTHON format (default %H:%M)
 -f           First row of file is a header row, and should be skipped
 -h           Show help (this information) and exit
 -l <columns> A comma-separated ordered list of columns for which derivatives are required
 --date-column <column>  Date/time column number (0-based, defaults to 0)
 --nojit      Disable Psyco Just-In-Time compiler.
 --copy-first Copy the relevant column titles of the first row as a header
"""
    sys.exit(1)

# ---------------------------------------------------------------
def main():
    opts, args = getopt.getopt(sys.argv[1:], 'cd:fhl:', ['copy-first', 'date-column=', 'nojit'])
    skipFirst = False
    dateFormat = '%H:%M'
    dateColumn = 0
    isCsv = False
    copyFirst = False
    # contains column list, e.g. 0,1,3
    columns = []
    enableJit = True
    for opt, arg in opts:
        if opt == '-h':
            usage()
        elif opt == '-c':
            isCsv = True
        elif opt == '-d':
            dateFormat = arg
        elif opt == '-f':
            skipFirst = True
        elif opt == '-l':
            columns = [int(value) for value in arg.split(',')]
        elif opt == '--copy-first':
            copyFirst = True
        elif opt == '--date-column':
            dateColumn = int(arg)
        elif opt == '--nojit':
            enableJit = False
    if not columns:
        usage('Output column specification is required')

    enablePsyco(enableJit)
    try:
        if len(args) == 0:
            processFile(sys.stdin, sys.stdout, columns, dateColumn, dateFormat, isCsv, skipFirst, copyFirst)
        else:
            for arg in args:
                if not os.path.exists(arg):
                    usage('Cannot locate input file: %s' % arg)
                inFile = open(arg)
                processFile(inFile, sys.stdout, columns, dateColumn, dateFormat, isCsv, skipFirst, copyFirst)
                inFile.close()
    except InvalidOptionException, e:
        usage(e.args[0])
    except InvalidDataException, e:
        print e.args[0]

# ---------------------------------------------------------------
def processFile(inFile, outFile, columns, dateColumn, dateFormat, isCsv, skipFirst, copyFirst):
    result = parseFile(inFile, columns, dateColumn, dateFormat, isCsv, skipFirst, copyFirst)
    writeResults(result, outFile, isCsv)

# ---------------------------------------------------------------
class Results(object):
    def __init__(self):
        self.derivatives = []
        self.headerValues = []

# ---------------------------------------------------------------
def parseFile(inFile, columns, dateColumn, dateFormat, isCsv, skipFirst, copyFirst):
    lastValues = None
    lastTimestamp = None
    result = Results()
    isFirstLine = True
    lineNumber = 0
    for line in inFile:
        lineNumber += 1
        #print line
        if isFirstLine and skipFirst:
            isFirstLine = False
            if copyFirst:
                cpts = splitLine(line, isCsv, strip=True)
                result.headerValues = [cpts[index] for index in [dateColumn] + columns]
            continue
        cpts = splitLine(line, isCsv, strip=True)
        #print cpts
        try:
            columnValues = [float(cpts[index]) for index in columns]
            timestampStr = cpts[dateColumn]
            timestamp = datetime.datetime.strptime(timestampStr, dateFormat)
        except TypeError:
            print >> sys.stderr, 'Invalid number on line %d of file - skipping line' % lineNumber
            continue
        if lastValues is None:
            lastValues = columnValues
            lastTimestamp = timestamp
            continue
        deltas = calculateDeltas(columnValues, lastValues, timestamp, lastTimestamp)
        result.derivatives.append([timestampStr, deltas])
        lastValues = columnValues
        lastTimestamp = timestamp
    return result

# ---------------------------------------------------------------
def calculateDeltas(columnValues, lastValues, timestamp, lastTimestamp):
    deltas = [value - lastValue for lastValue, value in zip(lastValues, columnValues)]
    seconds = (timestamp - lastTimestamp).total_seconds()
    derivatives = [delta / seconds for delta in deltas]
    return derivatives

# ---------------------------------------------------------------
def writeResults(result, outFile, isCsv):
    if result.headerValues:
        writeLine(outFile, result.headerValues, isCsv)
    for timestampStr, deltas in result.derivatives:
        values = [formatOutputValue(value) for value in deltas]
        allValues = [timestampStr] + values
        writeLine(outFile, allValues, isCsv)
    
# ---------------------------------------------------------------
def writeLine(outFile, values, isCsv):
    if isCsv:
        outFile.write(', '.join(values))
        outFile.write('\n')
    else:
        outFile.write(''.join(['%10s' % value for value in values]))
        outFile.write('\n')

# ---------------------------------------------------------------
if __name__ == '__main__':
    main()
