#! /usr/bin/env python

"""
(C) Copyright 2004-2010, by John Dickson

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


Like a combination of head and tail - output one or more ranges from anywhere within a file (or stdout).

Complicated by a desire not to keep the whole file in memory when negative indexes are used in ranges.
Strategy:
- If any negative indexes and we're passed a file path, do a first pass through the file to see how many
  lines it has, then change the negative indexes to positive
- If negative indexes and reading from stdin, work out how much we have to buffer to satisfy all the
  ranges (based both on maximum -ve index, and on +ve start index with -ve end index), and maintain
  a buffer of that size only.

See usage()
"""

import sys, os
from .statslib import *

# ============================================================================
class ArgException(Exception):
    pass

# ============================================================================
class RangeSpec:
    def __init__(self, arg):
        """Arg is of form <start>:<end>"""
        cpts = arg.split(':')
        if len(cpts) != 2:
            raise ArgException('Invalid range argument: %s' % arg)
        try:
            self.start = int(cpts[0])
            self.end = int(cpts[1])
        except:
            raise ArgException('Invalid range argument: %s' % arg)
        if self.start == 0 or self.end == 0:
            raise ArgException('Invalid range argument: %s' % arg)

    def hasNegativeIndex(self):
        return self.start < 0 or self.end < 0

    def convertToPositive(self, fileLength):
        """Since negative indexes can cause big memory problems, convert all negative to positive)"""
        if self.start < 0:
            self.start = fileLength + self.start + 1
        if self.end < 0:
            self.end = fileLength + self.end + 1

    def shouldPrint(self, lineNumber):
        return not self.hasNegativeIndex() and lineNumber >= self.start and lineNumber <= self.end


# ============================================================================
class LineParser:
    """Responsible for accepting lines read, outputting those that are required, and also buffering
    lines required for ranges including a -ve index (since these can't be determined until the size
    of the file is required)"""
    def __init__(self, rangeSpecs):
        self.rangeSpecs = rangeSpecs
        self.lineNumber = 0
        # contains (line number, line)
        self.buffer = []
        self._calculateBufferStart()

    def _calculateBufferStart(self):
        """work out if we need to buffer file, and if so, what line to start on and how many lines to keep"""
        self.maxBufferSize = 0
        self.bufferStartLine = 0
        for rangeSpec in self.rangeSpecs:
            if rangeSpec.hasNegativeIndex():
                # if the start index is negative, need to buffer that many lines
                if rangeSpec.start < 0:
                    self.maxBufferSize = max(self.maxBufferSize, -rangeSpec.start)
                else:
                    # otherwise, start index is positive, so need to buffer at least for end, and start from start
                    self.maxBufferSize = max(self.maxBufferSize, -rangeSpec.end)
                    self.bufferStartLine = min(self.bufferStartLine, rangeSpec.start)

    def _printLine(self, line):
        print(line, end='')

    def _adviseLine(self, lineNumber, line):
        for rangeSpec in self.rangeSpecs:
            if rangeSpec.shouldPrint(lineNumber):
                self._printLine(line)
                return

    def lineRead(self, line):
        if type(line) == type(b''):
            line = line.decode('utf-8')
        #print 'line:', line
        self.lineNumber += 1
        adviseLine = True
        if self.maxBufferSize > 0:
            # buffer, rather than advising to rangeSpecs
            adviseLine = False
            self.buffer.append((self.lineNumber, line))
            # now prune and advise the first buffered line if required
            if len(self.buffer) > self.maxBufferSize:
                bufferedLineNumber, bufferedLine = self.buffer[0]
                if self.bufferStartLine == 0 and bufferedLineNumber < self.bufferStartLine:
                    del self.buffer[0]
                    self._adviseLine(bufferedLineNumber, bufferedLine)
        if adviseLine:
            self._adviseLine(self.lineNumber, line)

    def readingFinished(self):
        numLines = self.lineNumber
        for rangeSpec in self.rangeSpecs:
            rangeSpec.convertToPositive(numLines)
        for lineNumber, line in self.buffer:
            self._adviseLine(lineNumber, line)

# ----------------------------------------------------------------------------
def setExplicitRangeIndexes(rangeSpecs, filePath):
    """Find the number of lines in the file, and set all range indexes positive using the size"""
    hasNegativeIndex = False
    for rangeSpec in rangeSpecs:
        hasNegativeIndex = hasNegativeIndex or rangeSpec.hasNegativeIndex()
    if not hasNegativeIndex:
        return

    input = openInput(filePath)
    lineCount = getLineCount(input)
    closeInput(input)

    for rangeSpec in rangeSpecs:
        rangeSpec.convertToPositive(lineCount)

# ----------------------------------------------------------------------------
def usage(msg = None):
    if msg:
        print()
        print(msg)

    print("""
Output one or more ranges of lines from within a file (or stdout).  Works like
a combination of head and tail.

Usage:
========
  mid [options] range [range *] [inputFile]

If no input file is specified, reads from stdin.  Output is always to stdout.

Options:
========
 -h           Show help (this screen) and exit

Ranges:
=======
At least one line range is required.  Ranges take the form
  <startLine>:<endLine>

Line numbers are 1-based, i.e. the first line in the file is line 1, and both
the start and end line numbers are **inclusive**, i.e. both the start and end
lines are included in the output.  Negative line numbers count backwards from
the end of the file, with -1 being the last line.

Examples:
---------

 10:20          All lines from 10 to 20
 2:-1           All but the first line of the file
 -10:-1         The last 10 lines of the file
 1:1 -1:-1      Just the first and last line of the file
""")
    sys.exit(1)


# ----------------------------------------------------------------------------
def openInput(filePath):
    if filePath is not None:
        return open(filePath, 'rb')
    else:
        return sys.stdin

# ----------------------------------------------------------------------------
def closeInput(input):
    if input is not sys.stdin:
        input.close()

# ----------------------------------------------------------------------------
def getLineCount(input):
    lineCount = 0
    for line in input:
        lineCount += 1
    return lineCount

# ----------------------------------------------------------------------------
def main(args=sys.argv[1:]):
    if not args or args[0] == '-h':
        usage()

    # TODO: add --nojit option
    enablePsyco(True)

    rangeSpecs = []
    filePath = None
    try:
        for arg in args:
            rangeSpecs.append(RangeSpec(arg))
    except ArgException as msg:
        filePath = arg
        if not os.path.exists(filePath):
            usage('Cannot find file - invalid path (or range): %s' % filePath)

    if filePath is not None:
        setExplicitRangeIndexes(rangeSpecs, filePath)

    with openInput(filePath) as input:
        parser = LineParser(rangeSpecs)
        for lineBytes in input:
            parser.lineRead(lineBytes)
        parser.readingFinished()

# ----------------------------------------------------------------------------
if __name__ == '__main__':
    main()
