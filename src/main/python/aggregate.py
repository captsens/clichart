#!/usr/bin/env python

"""
(C) Copyright 2007-2012, by John Dickson

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


A simple script to aggregate one or more columns in a tabular (CSV or whitespace-separated)
data file or stream.

See usage()
"""

import sys, getopt, os, re
from .statslib import *

OUTPUT_MIN = 'min'
OUTPUT_MAX = 'max'
OUTPUT_AVERAGE = 'av'
OUTPUT_COUNT = 'cnt'
OUTPUT_TOTAL = 'tot'
OUTPUT_FIRST = 'first'
OUTPUT_LAST = 'last'
OUTPUT_SD = 'sd'
OUTPUT_KEY = 'k'

ALL_OUTPUT_TYPES = (OUTPUT_MIN, OUTPUT_MAX, OUTPUT_AVERAGE, OUTPUT_COUNT, OUTPUT_TOTAL,
        OUTPUT_FIRST, OUTPUT_LAST, OUTPUT_SD, OUTPUT_KEY)

COLUMN_EXPRESSION_RE = re.compile(r'(-?\d+):(%s)' % '|'.join(ALL_OUTPUT_TYPES))

# ===============================================================
def usage(msg = None):
    if msg:
        print(msg)
        print()
    print("""
A simple script to extract aggregate data (minimum, average, maximum, total,
count, first or last value) from files containing tabular data (CSV or
whitespace-separated). The output of the script will be 1 row of data for
each input file (or only one row if stdin is used).  Alternatively, if a key
(aka group) is specified, there will be one row per key.

Output is in the same format as the input, i.e. whitespace or comma-separated.

Usage: aggregate [options] [inputFile*]

Options:
 -c           Data is CSV (default is whitespace-separated)
 -f           First row of file is a header row, and should be skipped
 -h           Show help (this information) and exit
 -l <columns> A comma-separated ordered list of columns and aggregate types
              to include in the output (required).  See below
 -p           A prefix column value to write in the aggregate columns for each
              line.  Pass 1 or more of these, e.g. to provide a date column
              value.  Example: -p 12/03/2007
 -s           If no rows of data were found, silently output nothing
 -x           A suffix column value to write in the aggregate columns for each
              line.  Pass 1 or more of these, e.g. to provide a date column
              value.  Example: -x 12/03/2007
 --nojit      Disable Psyco Just-In-Time compiler.

Column indexes
--------------
The column index is the 0-based number of the column in the file, while the
aggregate type is 'min', 'av', 'max', 'tot', 'cnt', 'sd' (standard deviation),
'first' or 'last'.  If the type is 'k', this column is treated as a key, and
output is grouped by the values in the column. Note that the count is just the
number of lines in the file, not counting any header row.
  E.g. 1:max,2:av,2:max,0:cnt,4:tot,0:sd

Negative column indexes can be used, with -1 representing the last column etc.
  E.g. 1:max,-1:tot

Simple expressions can also be used. To indicate negation rather than a
negative column index, you must separate the minus sign and the column index
with a space.
  E.g. '1:max / 3:cnt / 60', '1:tot - 3:tot', '2:av - -1:av'
""")
    sys.exit(1)

# ===============================================================
class ColumnStats(object):
    def __init__(self, columnNumber):
        self.columnNumber = columnNumber
        self.count = 0
        self.total = 0.0
        self.min = self.max = None
        self.first = self.last = None
        # used for standard deviation.  See formular near bottom of page on http://en.wikipedia.org/wiki/Standard_deviation
        self.lastAverage = None
        self.lastQ = None

    def accumulate(self, stringValue, lineNumber):
        """Note that value is a string"""
        try:
            value = int(stringValue)
        except:
            try:
                value = float(stringValue)
            except:
                raise InvalidDataException('Invalid number in column %d, line %d: %s' % (self.columnNumber,
                        lineNumber, stringValue))

        self.total += value
        self.count += 1
        if self.min is None or value < self.min:
            self.min = value
        if self.max is None or value > self.max:
            self.max = value
        if self.first is None:
            self.first = value
        self.last = value
        # SD
        if self.lastAverage is None:
            self.lastQ = 0
            self.lastAverage = value
        else:
            self.lastQ = self.lastQ + (self.count - 1) * ((value - self.lastAverage) ** 2) / self.count
            self.lastAverage = self.lastAverage + (value - self.lastAverage) / self.count

    def getValue(self, outputType):
        if outputType == OUTPUT_MIN:
            return self.min or 0
        elif outputType == OUTPUT_MAX:
            return self.max or 0
        elif outputType == OUTPUT_AVERAGE:
            if self.count == 0:
                return 0.0
            return self.total / float(self.count)
        elif outputType == OUTPUT_COUNT:
            return self.count
        elif outputType == OUTPUT_TOTAL:
            return self.total
        elif outputType == OUTPUT_FIRST:
            return self.first
        elif outputType == OUTPUT_LAST:
            return self.last
        # SD
        elif outputType == OUTPUT_SD:
            if self.count == 0:
                return 0.0
            variance = self.lastQ / self.count
            return variance ** 0.5

# ===============================================================
class KeyedRowStats(object):
    """Contains ColumnStats for all columns for a particular key (or for a whole file if aggregation is not keyed)"""
    def __init__(self, keyValues, allColumnStats):
        """key is the list of key values we're grouping by, while allColumnStats is a dict containing ColumnStats keyed on columnIndex.
        Note that allColumnStats will be cloned"""
        self.keyValues = keyValues
        self.allColumnStats = {}
        for key, value in list(allColumnStats.items()):
            columnNumber = value.columnNumber
            self.allColumnStats[columnNumber] = ColumnStats(columnNumber)
    def accumulate(self, lineCpts, lineNumber):
        for columnNumber, columnStats in list(self.allColumnStats.items()):
            columnStats.accumulate(getColumnValue(lineCpts, columnNumber, lineNumber), lineNumber)

# ---------------------------------------------------------------
def getColumnNumbers(columnOutput):
    """Given all the column output expressions, return the indexes of columns we need to aggregate, plus the indexes of all keyed columns"""
    columnNumbers = []
    keyColumnNumbers = []
    for expression in columnOutput:
        for columnNumber, outputType in COLUMN_EXPRESSION_RE.findall(expression):
            columnNumber = int(columnNumber)
            if outputType != OUTPUT_KEY and columnNumber not in columnNumbers:
                columnNumbers.append(columnNumber)
            elif outputType == OUTPUT_KEY and columnNumber not in keyColumnNumbers:
                keyColumnNumbers.append(columnNumber)
    return columnNumbers, keyColumnNumbers

# ---------------------------------------------------------------
def extractKey(lineCpts, keyColumnNumbers, lineNumber):
    """Return the key values to use for this line
    NOTE: Returned as a tuple, since it must be hashable"""
    if not keyColumnNumbers:
        return None
    keyValues = [getColumnValue(lineCpts, columnNumber, lineNumber) for columnNumber in keyColumnNumbers]
    return tuple(keyValues)

# ---------------------------------------------------------------
def getColumnValue(lineCpts, columnNumber, lineNumber):
    try:
        return lineCpts[columnNumber]
    except IndexError:
        raise InvalidDataException('Cannot find column %d in line %d' % (columnNumber,
                lineNumber))    

# ---------------------------------------------------------------
def parseFile(inFile, columnOutput, isCsv, skipFirst):
    # contains ColumnStats keyed on column number
    allColumnStats = {}
    columnNumbers, keyColumnNumbers = getColumnNumbers(columnOutput)
    for columnNumber in columnNumbers:
        if columnNumber not in allColumnStats:
            allColumnStats[columnNumber] = ColumnStats(columnNumber)
    # contains KeyedRowStats, keyed on key values
    keyedRowStats = {}

    isFirstLine = True
    lineNumber = 0
    for line in inFile:
        lineNumber += 1
        #print line
        if isFirstLine and skipFirst:
            isFirstLine = False
            continue
        cpts = splitLine(line, isCsv)
        #print cpts
        keyValues = extractKey(cpts, keyColumnNumbers, lineNumber)
        keyedRow = keyedRowStats.setdefault(keyValues, KeyedRowStats(keyValues, allColumnStats))
        keyedRow.accumulate(cpts, lineNumber)

    return keyedRowStats

# ===============================================================
class KeyColumnException(Exception):
    pass

# ---------------------------------------------------------------
def evaluateExpression(expression, allColumnStats):
    """Given an expression containing column references like 3:tot, 0:av etc., evaluate and return based
    on the stats dict"""
    def insertColumnStatsValue(match):
        columnNumber = int(match.group(1))
        outputType = match.group(2)
        if outputType == OUTPUT_KEY:
            raise KeyColumnException()
        return str(allColumnStats[columnNumber].getValue(outputType))
    updatedExpression = COLUMN_EXPRESSION_RE.sub(insertColumnStatsValue, expression)
    #print updatedExpression
    try:
        return eval(updatedExpression)
    except Exception as e:
        #raise e
        raise InvalidOptionException('Invalid expression: %s' % expression)

# ---------------------------------------------------------------
def printColumnStats(outFile, columnOutput, keyedRowStats, isCsv, prefixColumnValues=[], suffixColumnValues=[]):
    for keyValues in sorted(keyedRowStats.keys()):
        outputValues = []
        outputValues.extend(prefixColumnValues)
        if keyValues:
            outputValues.extend(keyValues)
        for expression in columnOutput:
            try:
                statsValue = evaluateExpression(expression, keyedRowStats[keyValues].allColumnStats)
                outputValues.append(formatOutputValue(statsValue))
            except KeyColumnException:
                pass
        if suffixColumnValues:
            outputValues.extend(suffixColumnValues)
        if isCsv:
            outFile.write(', '.join(outputValues))
            outFile.write('\n')
        else:
            outFile.write(''.join(['%10s' % value for value in outputValues]))
            outFile.write('\n')

# ---------------------------------------------------------------
def processFile(inFile, outFile, columnOutput, isCsv, skipFirst, silent, prefixColumnValues=[], suffixColumnValues=[]):
    # contains ColumnStats keyed on column number
    keyedRowStats = parseFile(inFile, columnOutput, isCsv, skipFirst)
    if not keyedRowStats:
        if not silent:
            raise InvalidDataException('No data found!')
        else:
            return
    printColumnStats(outFile, columnOutput, keyedRowStats, isCsv, prefixColumnValues, suffixColumnValues)

# ---------------------------------------------------------------
def main():
    opts, args = getopt.getopt(sys.argv[1:], 'cfhl:p:sx:', ['nojit'])
    skipFirst = False
    isCsv = False
    # contains column expressions, e.g. 3:tot, 1:cnt
    columnOutput = []
    prefixColumnValues = []
    suffixColumnValues = []
    silent = False
    enableJit = True
    for opt, arg in opts:
        if opt == '-h':
            usage()
        elif opt == '-c':
            isCsv = True
        elif opt == '-f':
            skipFirst = True
        elif opt == '-l':
            columnOutput = arg.split(',')
        elif opt == '-p':
            prefixColumnValues.append(arg)
        elif opt == '-s':
            silent = True
        elif opt == '-x':
            suffixColumnValues.append(arg)
        elif opt == '--nojit':
            enableJit = False
    if not columnOutput:
        usage('Output column specification is required')

    enablePsyco(enableJit)
    try:
        if len(args) == 0:
            processFile(sys.stdin, sys.stdout, columnOutput, isCsv, skipFirst, silent, prefixColumnValues, suffixColumnValues)
        else:
            for arg in args:
                if not os.path.exists(arg):
                    usage('Cannot locate input file: %s' % arg)
                inFile = open(arg)
                processFile(inFile, sys.stdout, columnOutput, isCsv, skipFirst, silent, prefixColumnValues, suffixColumnValues)
                inFile.close()
    except InvalidOptionException as e:
        usage(e.args[0])
    except InvalidDataException as e:
        print(e.args[0])
        
# ---------------------------------------------------------------
if __name__ == '__main__':
    main()
