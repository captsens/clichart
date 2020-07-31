#!/usr/bin/env python

"""
(C) Copyright 2008-2012, by John Dickson

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


Quick hack to take lines containing numeric values, and to divide these into stats for 'n' bins (to
produce a distribution histogram)

See usage()
"""

import sys, getopt
from math import ceil
from .statslib import *

# ---------------------------------------------------------------
def usage(msg = None):
    if msg:
        print(msg)
        print()
    print("""
A simple script to extract a specified column in a file containing tabular
data (CSV or whitespace-separated), and to output a histogram of the
distribution of the data in that column, broken into a specified number of
intervals. The output is a row for each interval, containing the start and
end of the interval, and the number of values falling within that interval.

Either the number of intervals (-i) or the interval size (-s) is required.

Usage: histogram [options] [inputFile]

Options:
 -c             Data (input and output) is CSV (default is
                whitespace-separated)
 -f             First row of file is a header row, and should be skipped
 -h             Show help (this information) and exit
 -i <intervals> The number of intervals over which the histogram should be
                generated (i.e. the number of output rows)
 -l <column>    The number of the column from which to generate the histogram
                (required).  The column index is the 0-based number of the
                column in the file.
 -m             Show cumulative interval values, rather than individual
 -p             Add a column showing the percentage for the interval
                (individual or cumulative)
 -s <size>      Size of interval (an alternative to -i)
 --header       Include a header row in the output
 --nojit        Disable Psyco Just-In-Time compiler
""")
    sys.exit(1)

# ===============================================================
class Options(object):
    def __init__(self):
        self.numIntervals = None
        self.intervalSize = None
        self.isCsv = False
        self.skipFirst = False
        self.columnIndex = None
        self.includeOutputHeader = False
        self.cumulative = False
        self.showPercent = False
        self.filePath = None
    def parseOptions(self):
        enableJit = True
        opts, args = getopt.getopt(sys.argv[1:], 'hcfi:l:mps:', ['header', 'nojit'])
        for opt, arg in opts:
            if opt == '-h':
                usage()
            elif opt == '-c':
                self.isCsv = True
            elif opt == '-f':
                self.skipFirst = True
            elif opt == '-i':
                self.numIntervals = int(arg)
            elif opt == '-l':
                self.columnIndex = int(arg)
            elif opt == '-m':
                self.cumulative = True
            elif opt == '-p':
                self.showPercent = True
            elif opt == '-s':
                self.intervalSize = float(arg)
            elif opt == '--header':
                self.includeOutputHeader = True
            elif opt == '--nojit':
                enableJit = False
        if (self.numIntervals is None or self.numIntervals < 2) \
                and (self.intervalSize is None or self.intervalSize <= 0):
            usage('Either number of intervals or interval size is required')
        if self.columnIndex is None or self.columnIndex < 0:
            usage('Column number is required')
        if args:
            self.filePath = args[0]
        enablePsyco(enableJit)
    
# ===============================================================
class Interval(object):
    def __init__(self, startValue, endValue):
        self.startValue = startValue
        self.endValue = endValue
        self.count = 0
        self.percentage = None

# ---------------------------------------------------------------
def parseData(inFile, options):
    """Parse the data from inFile, and return (minValue, maxValue, allValues)"""
    values = []
    minValue = maxValue = None
    lineNum = 0
    for line in inFile:
        lineNum += 1
        if options.skipFirst and lineNum == 1:
            continue
        lineValues = splitLine(line, options.isCsv)
        if options.columnIndex >= len(lineValues):
            raise InvalidDataException('Invalid data on line %d: not enough values' % lineNum)
        try:
            value = float(lineValues[options.columnIndex])
        except ValueError:
            raise InvalidDataException('Non-numeric value [%s] on line %d' % (lineValues[options.columnIndex], lineNum))
        values.append(value)
        if minValue is None:
            minValue = maxValue = value
        else:
            minValue = min(minValue, value)
            maxValue = max(maxValue, value)
    return minValue, maxValue, values

# ---------------------------------------------------------------
def outputHistogram(outFile, values, minValue, maxValue, options):
    intervals = calculateHistogram(values, minValue, maxValue, options)
    headers = ['Interval_Start', 'Interval_End', 'Count']
    if options.showPercent:
        headers.append('Percent')
    if options.includeOutputHeader:
        print(_separator(options.isCsv).join(headers), file=outFile)
    for interval in intervals:
        print(_joinLine(interval, options), file=outFile)

# ---------------------------------------------------------------
def calculateHistogram(values, minValue, maxValue, options):
    """Returns intervals"""
    numIntervals, intervalSize, intervals = initialiseIntervals(values, minValue, maxValue, options)
    #print intervalValues
    for value in values:
        intervalIndex = int((value - minValue) / intervalSize)
        # ensure within range
        intervalIndex = min(intervalIndex, numIntervals - 1)
        intervals[intervalIndex].count += 1
    totalCount = sum([interval.count for interval in intervals])
    if options.cumulative:
        total = 0
        for interval in intervals:
            total += interval.count
            interval.count = total
    for interval in intervals:
        interval.percentage = 100.0 * interval.count / float(totalCount)
    return intervals

# ---------------------------------------------------------------
def initialiseIntervals(values, minValue, maxValue, options):
    """Returns numIntervals, intervalSize, intervals[]"""
    if options.numIntervals:
        numIntervals = options.numIntervals
        intervalSize = (maxValue - minValue) / float(numIntervals)
    else:
        intervalSize = options.intervalSize
        numIntervals = int(ceil((maxValue - minValue) / intervalSize))
    intervals = []
    for i in range(numIntervals):
        interval = Interval(minValue + i * intervalSize, minValue + (i + 1) * intervalSize)
        intervals.append(interval)
    return numIntervals, intervalSize, intervals

# ---------------------------------------------------------------
def _joinLine(interval, options):
    lineValues = [formatOutputValue(interval.startValue), formatOutputValue(interval.endValue), str(interval.count)]
    if options.showPercent:
        lineValues.append('%6.3f' % interval.percentage)
    return _separator(options.isCsv).join(lineValues)

# ---------------------------------------------------------------
def _separator(isCsv):
    if isCsv:
        return ', '
    else:
        return ' '

# ---------------------------------------------------------------
def main():
    options = Options()
    options.parseOptions()

    if options.filePath:
        inFile = open(options.filePath, 'rb')
    else:
        inFile = sys.stdin
    try:
        minValue, maxValue, values = parseData(inFile, options)
        outputHistogram(sys.stdout, values, minValue, maxValue, options)
    except InvalidDataException as e:
        print('Error:', e)

# ---------------------------------------------------------------
if __name__ == '__main__':
    main()
