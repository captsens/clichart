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

from statslib import *

# ==============================================================================
class Accumulator:
    def __init__(self, keyExtractor, fieldExtractor):
        self.keyExtractor = keyExtractor
        self.fieldExtractor = fieldExtractor
        # contains {fieldValue: count} keyed on line key
        self.lineStats = {}
        # contains every discrete value seen
        self.allValues = {}

    def lineRead(self, line, lineNumber):
        key = self.keyExtractor.getValue(line, lineNumber)
        value = self.fieldExtractor.getValue(line, lineNumber)
        thisLineStats = self.lineStats.setdefault(key, {})
        thisLineStats[value] = thisLineStats.get(value, 0) + 1
        self.allValues[value] = 1

    def getColumnNames(self):
        """Return all the column values (useful for headers)"""
        columnNames = self.allValues.keys()
        columnNames.sort()
        return columnNames

    def getResults(self, sortOutput):
        """Yields key, [valueCounts] for each key"""
        keys = self.lineStats.keys()
        if sortOutput:
            keys.sort()
        for key in keys:
            thisLineStats = self.lineStats[key]
            yield key, [thisLineStats.get(colName, 0) for colName in self.getColumnNames()]

# ------------------------------------------------------------------------------
def usage(msg = None, includeException = False):
    if msg:
        print
        print msg
        if includeException:
            errors = traceback.format_exception_only(sys.exc_type, sys.exc_value)
            for error in errors:
                print '  %s' % error.strip()

    print """
Given line-based text input, extract a key field and a field containing
discrete values for each line of interest, and output the count of *each
value* for each key field. For example, when the field is the priority of log
messages, shows the count of each priority for each time period.

Useful for generating summary statistics from text data, especially for
passing to clichart for charting.

Usage:
========
  discretestats [inputOptions] [outputOptions] [inputFile]

If no input file is specified, reads from stdin.  Output is always to stdout.

Input Options:
==============
 -h           Show help (this screen) and exit
 -k <keyspec> Required. Specifies how to extract a key from each line.
              keyspec can be:
    s:<substring>  Extract key as a substring.  See Substrings
    f:<index>      Extract key as a field.  Fields are separated by white
                   space, 0-based
    r:<regex>      Extract key as a regular expression
 -m <regex>   Only include lines matching this regular expression
 -v <valuespec> Required.  Specifies a value for which to accumulate statistics
              from each line.  valuespec may be substring, field or regex
              as for -k option.

Output Options:
===============
 -c           Output as CSV (default is whitespace-separated)
 -q           Quote keys and value headings (with double quotes)
 -s           Sort output by key column

Other Options:
==============
 --nojit      Disable Psyco Just-In-Time compiler

Substrings:
===========
Consist of 1 or 2 indexes, 0-based, separated by a colon.
"""
    sys.exit(1)

# ==============================================================================
class DiscretestatsOptions(Options):
    """Parses and holds command-line options"""
    def __init__(self):
        Options.__init__(self, None)
        self.args = []
    def parse(self, argList = sys.argv[1:], usageFunc = usage):
        opts, self.args = getopt.getopt(argList, 'hck:l:m:qsv:', ['nojit'])
        unparsedOptions = self._parseOptions(opts, usageFunc)
        self.outputFormatter = OutputFormatter(self.isCsv)
        for opt, arg in unparsedOptions:
            if opt == '-q':
                self.outputFormatter.quote = True
        if len(self.args) > 1:
            raise InvalidOptionException('Only one file can be parsed')
        if self.keyExtractor is None:
            usage('-k option is required')
        if len(self.fieldExtractors) != 1:
            usage('Exactly one -v option is required')

# ------------------------------------------------------------------------------
def processFile(inFile, outFile, options):
    accumulator = Accumulator(options.keyExtractor, options.fieldExtractors[0])

    lineNumber = 0
    while True:
        line = inFile.readline().strip()
        lineNumber += 1
        if not line:
            break
        if options.lineMatcher.shouldInclude(line):
            accumulator.lineRead(line, lineNumber)
    # header row
    options.outputFormatter.output(outFile, 'Key', accumulator.getColumnNames())
    for key, values in accumulator.getResults(options.sortOutput):
        options.outputFormatter.output(outFile, key, values)

# ------------------------------------------------------------------------------
def main():
    try:
        options = DiscretestatsOptions()
        options.parse()
        enablePsyco(options.enablePsyco)
        if options.args:
            inFile = open(options.args[0], 'rb')
        else:
            inFile = sys.stdin

        processFile(inFile, sys.stdout, options)

        if inFile != sys.stdin:
            inFile.close()
    except InvalidOptionException, e:
        usage(e)
    except InvalidDataException, e:
        print e

# ------------------------------------------------------------------------------
if __name__ == '__main__':
    main()
