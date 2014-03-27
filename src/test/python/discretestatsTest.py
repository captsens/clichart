#!/usr/bin/env python

"""
Unit tests for discretestats.py
"""

import unittest
import testUtils
from StringIO import StringIO

testUtils.checkPythonPath('discretestats')
from discretestats import *
import statslib

TEST_INPUT = '\n'.join(['01:24 Ham  72  The end  6.3',
        '01:24 Ham  12  some words  0.123',
        '12:32 Egg  999   zzz aa   9.99',
        '12:33 Egg  33   more words   3.21'])

# ----------------------------------------------------------------------------
def buildOptions(optionString, usageFunc = None):
    options = DiscretestatsOptions()
    options.parse(optionString.split(), usageFunc)
    return options

# ============================================================================
class DiscreteStatsTest(unittest.TestCase):
    def _checkOutput(self, outFile, valueList, requireSort = True):
        # split results into a list, sort if requested, then compare the 2 lists
        results = outFile.getvalue().splitlines()
        if requireSort:
            # omit first line from header
            results = results[0:1] + sorted(results[1:])
        self.assertEquals(results, valueList)

    def testSimpleCsv(self):
        options = buildOptions('-ck s:0:5 -v f:1')
        outFile = StringIO()
        inFile = StringIO(TEST_INPUT)
        processFile(inFile, outFile, options)
        self._checkOutput(outFile, ['Key, Egg, Ham', '01:24, 0, 2', '12:32, 1, 0', '12:33, 1, 0'])

    def testSimpleSortedText(self):
        options = buildOptions('-sk s:0:5 -v s:6:9')
        outFile = StringIO()
        inFile = StringIO(TEST_INPUT)
        processFile(inFile, outFile, options)
        self._checkOutput(outFile, ['Key      Egg       Ham', '01:24 0         2',
                '12:32 1         0', '12:33 1         0'], False)

    def testQuotedCsv(self):
        options = buildOptions('-csqk s:0:5 -v r:([A-Za-z][a-z]+[^a-z][a-z]+)')
        outFile = StringIO()
        inFile = StringIO(TEST_INPUT)
        processFile(inFile, outFile, options)
        self._checkOutput(outFile, ['Key, "The end", "more words", "some words", "zzz aa"',
                '01:24, 1, 0, 1, 0', '12:32, 0, 0, 0, 1', '12:33, 0, 1, 0, 0'])

# ============================================================================
if __name__ == '__main__':
    unittest.main()
