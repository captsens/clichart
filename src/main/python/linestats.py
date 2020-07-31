#! /usr/bin/env python

"""
(C) Copyright 2006-2010, by John Dickson

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


Given a line-based text file, print out statistics extracted from the lines.
See usage()
"""

import sys, getopt, re, traceback
from .statslib import *

# ------------------------------------------------------------------------------
def usage(msg = None, includeException = False):
    if msg:
        print()
        print(msg)
        if includeException:
            errors = traceback.format_exception_only(sys.exc_info()[0], sys.exc_info()[1])
            for error in errors:
                print('  %s' % error.strip())

    print("""
Given line-based text input, output statistics about the lines received, e.g
counts, sums, averages etc.  Useful for generating summary statistics from text
data, especially for passing to clichart for charting.

Statistics may be generated based on the entire line provided, but more
commonly is based on a key field in the line (extracted based on a substring,
a whitespace-separated field or a regular expression).

Usage:
========
  linestats [inputOptions] [outputOptions] [inputFile]

If no input file is specified, reads from stdin.  Output is always to stdout.

Input Options:
==============
 -h           Show help (this screen) and exit
 -k <keyspec> Specifies how to extract a key from each line (default is to use
              the whole line).  keyspec can be:
    s:<substring>  Extract key as a substring.  See Substrings
    f:<index>      Extract key as a field.  Fields are separated by white
                   space, 0-based
    r:<regex>      Extract key as a regular expression
 -m <regex>   Only include lines matching this regular expression
 -v <valuespec> Specifies a value for which to accumulate statistics from each
              line.  Zero or more.  valuespec may be substring, field or regex
              as for -k option, but the result MUST be numeric

Output Options:
===============
 -c           Output as CSV (default is whitespace-separated)
 -f <line>    Output this line first, as the headers for the columns
 -l <columns> A comma-separated ordered list of columns to include in the
              output (default is 'k,k:cnt').  The field index is the 0-based
              number of the field in the list of -v options, i.e. field number
              0 is the field specified by the first -v option.  Columns may be:
    k         The key
    k:cnt     The count for the key
    0:av      The average of field '0'
    1:min     The minimum of field '1'
    1:max     The maximum of field '1'
    1:tot     The total of field '1'
 -s           Sort output by key column

Other Options:
==============
 --nojit      Disable Psyco Just-In-Time compiler

Substrings:
===========
Consist of 1 or 2 indexes, 0-based, separated by a colon.

Regular Expressions:
====================
Regular expressions use Perl 5 syntax, not grep/egrep!  The main difference is
that the "*" and "+" operators are greedy, so they match as much as possible.
To duplicate grep behaviour, suffix them with "?" to make them non-greedy.

When used to extract a value, if the regex contains a bracketed group, this
will be used as the value - otherwise the whole match is used.  See HTML
documentation for full details.
""")
    sys.exit(1)

# ==============================================================================
class LinestatsOptions(Options):
    """Parses and holds command-line options"""
    def __init__(self):
        Options.__init__(self, ValueExtractor('s:0:'))
        self.args = []
        self.headerLine = None
    def parse(self, argList = sys.argv[1:], usageFunc = usage):
        opts, self.args = getopt.getopt(argList, 'hcf:k:l:m:sv:', ['nojit'])
        unparsedOptions = self._parseOptions(opts, usageFunc)
        for opt, arg in unparsedOptions:
            if opt == '-f':
                self.headerLine = arg
        if len(self.args) > 1:
            raise InvalidOptionException('Only one file can be parsed')

# ==============================================================================
class FieldStats:
    """Maintains stats for a field"""
    def __init__(self):
        self.min = None
        self.max = self.count = self.total = 0
    def addValue(self, stringValue):
        value = float(stringValue)
        self.count += 1
        self.total += value
        self.max = max(self.max, value)
        if self.min is None or value < self.min:
            self.min = value

    def getValue(self, statsType):
        if statsType == 'tot':
            return self.total
        elif statsType == 'min':
            return self.min
        elif statsType == 'max':
            return self.max
        elif statsType == 'av':
            return 1.0 * self.total / self.count

# ==============================================================================
class Accumulator:
    def __init__(self, keyExtractor, fieldExtractors):
        self.keyExtractor = keyExtractor
        self.fieldExtractors = fieldExtractors
        # contains counts keyed on line key
        self.lineCounts = {}
        # contains [FieldStats] keyed on line key
        self.lineFieldStats = {}

    def _getFieldStats(self, key, index):
        fieldStatsList = self.lineFieldStats.get(key)
        if fieldStatsList is None:
            fieldStatsList = [FieldStats() for ignored in self.fieldExtractors]
            self.lineFieldStats[key] = fieldStatsList
        return fieldStatsList[index]

    def lineRead(self, line, lineNumber):
        key = self.keyExtractor.getValue(line, lineNumber)
        self.lineCounts[key] = self.lineCounts.get(key, 0) + 1
        for i in range(len(self.fieldExtractors)):
            fieldStats = self._getFieldStats(key, i)
            fieldStats.addValue(self.fieldExtractors[i].getValue(line, lineNumber))

    def getResults(self, sortOutput):
        """Yields key, count(key), [fieldStats] for each key"""
        keys = list(self.lineCounts.keys())
        if sortOutput:
            keys.sort()
        for key in keys:
            yield key, self.lineCounts[key], self.lineFieldStats.get(key, [])

# ==============================================================================
class ColumnListOutputFormatter:
    def __init__(self, outputColumnExtractor, isCsv):
        self.outputColumnExtractor = outputColumnExtractor
        self.outputFormatter = OutputFormatter(isCsv)

    def output(self, outFile, key, keyCount, fieldStats):
        values = self.outputColumnExtractor.getValues(key, keyCount, fieldStats)
        self.outputFormatter.output(outFile, None, values)

# ------------------------------------------------------------------------------
def processFile(inFile, outFile, options):
    outputFormatter = ColumnListOutputFormatter(options.outputColumnExtractor, options.isCsv)
    accumulator = Accumulator(options.keyExtractor, options.fieldExtractors)

    lineNumber = 0
    while True:
        line = inFile.readline().strip()
        lineNumber += 1
        if not line:
            break
        if options.lineMatcher.shouldInclude(line):
            accumulator.lineRead(line, lineNumber)

    if options.headerLine:
        print(options.headerLine, file=outFile)
    for key, keyCount, fieldStats in accumulator.getResults(options.sortOutput):
        outputFormatter.output(outFile, key, keyCount, fieldStats)

# ------------------------------------------------------------------------------
def main():
    try:
        options = LinestatsOptions()
        options.parse()
        enablePsyco(options.enablePsyco)
        if options.args:
            inFile = open(options.args[0], 'rb')
        else:
            inFile = sys.stdin

        processFile(inFile, sys.stdout, options)

        if inFile != sys.stdin:
            inFile.close()
    except InvalidOptionException as e:
        usage(e)
    except InvalidDataException as e:
        print(e)
        
# ------------------------------------------------------------------------------
if __name__ == '__main__':
    main()
