#!/usr/bin/env python3

"""
Unit tests for linestats.py
"""

import unittest
from io import StringIO
from linestats import *
import statslib

TEST_INPUT_1 = '\n'.join(['01:24 xxx', '01:24 yyy', '12:32 aa', '12:33 b'])
TEST_INPUT_2 = '\n'.join(['01:24 Ham  72  The end  6.3',
        '01:24 Ham  12  some words  0.123',
        '12:32 Egg  999   zzz aa   9.99',
        '12:33 Egg  33   more words   3.21'])

# ----------------------------------------------------------------------------
def buildOptions(optionString, usageFunc = None):
    options = LinestatsOptions()
    options.parse(optionString.split(), usageFunc)
    return options

# ============================================================================
class OptionsTest(unittest.TestCase):
    def testShowHelp(self):
        self.usageCalled = False
        def usage(msg = None):
            self.usageCalled = True
        options = buildOptions('-h', usage)
        self.assertTrue(self.usageCalled)

    def _checkValueExtractor(self, extractor, charStartIndex, charEndIndex, fieldIndex = None, pattern = None):
        self.assertEqual(extractor.charStartIndex, charStartIndex)
        self.assertEqual(extractor.charEndIndex, charEndIndex)
        self.assertEqual(extractor.fieldIndex, fieldIndex)
        if pattern is None:
            self.assertEqual(extractor.regex, None)
        else:
            self.assertEqual(extractor.regex.pattern, pattern)

    def _checkOutputColumnExtractor(self, options, index, value_0, value_1):
        self.assertEqual(options.outputColumnExtractor.fields[index][0], value_0)
        self.assertEqual(options.outputColumnExtractor.fields[index][1], value_1)

    def testNoArgs(self):
        options = buildOptions('')
        self.assertEqual(len(options.args), 0)
        self.assertEqual(options.isCsv, False)
        self.assertEqual(options.headerLine, None)
        self.assertEqual(options.lineMatcher.regex, None)
        self.assertEqual(len(options.fieldExtractors), 0)
        self._checkValueExtractor(options.keyExtractor, 0, None)
        self.assertEqual(len(options.outputColumnExtractor.fields), 2)
        self._checkOutputColumnExtractor(options, 0, None, None)
        self._checkOutputColumnExtractor(options, 1, None, 'count')

    def testSimpleExampleOne(self):
        options = buildOptions('-ck s:0:5')
        self.assertEqual(options.isCsv, True)
        self.assertEqual(options.headerLine, None)
        self.assertEqual(options.lineMatcher.regex, None)
        self.assertEqual(len(options.fieldExtractors), 0)
        self._checkValueExtractor(options.keyExtractor, 0, 5)
        self.assertEqual(len(options.outputColumnExtractor.fields), 2)
        self._checkOutputColumnExtractor(options, 0, None, None)
        self._checkOutputColumnExtractor(options, 1, None, 'count')

    def testExampleTwo(self):
        """Example taken from quick start example"""
        argString = '-m VMStatusLogger -k s:0:5 -v f:4 -v s:7:12 -v r:aPattern ' \
                + '-l k,0:min,1:max,2:av,1:tot samples/System.log'
        options = buildOptions('-m VMStatusLogger -k s:0:5 -v f:4 -v s:7:12 -v r:aPattern ' \
                + '-l k,0:min,1:max,2:av,1:tot samples/System.log')
        self.assertEqual(options.isCsv, False)
        self.assertEqual(options.headerLine, None)
        self.assertEqual(options.lineMatcher.regex.pattern, 'VMStatusLogger')
        self.assertEqual(len(options.fieldExtractors), 3)
        self._checkValueExtractor(options.fieldExtractors[0], None, None, 4)
        self._checkValueExtractor(options.fieldExtractors[1], 7, 12)
        self._checkValueExtractor(options.fieldExtractors[2], None, None, None, 'aPattern')
        self._checkValueExtractor(options.keyExtractor, 0, 5)
        self.assertEqual(len(options.outputColumnExtractor.fields), 5)
        self._checkOutputColumnExtractor(options, 0, None, None)
        self._checkOutputColumnExtractor(options, 1, 0, 'min')
        self._checkOutputColumnExtractor(options, 2, 1, 'max')
        self._checkOutputColumnExtractor(options, 3, 2, 'av')
        self._checkOutputColumnExtractor(options, 4, 1, 'tot')

    def testTooManyOutputFields(self):
        try:
            options = buildOptions('-v f:4 -l k:0:min,0:max')
            self.fail()
        except statslib.InvalidOptionException:
            pass

    def testInvalidColumnIndex(self):
        try:
            options = buildOptions('-k s:0:5 -v s:10:14 -l k,k:cnt,1:min')
            self.fail()
        except statslib.InvalidOptionException:
            pass


# ============================================================================
class LineStatsTest(unittest.TestCase):
    def _checkOutput(self, outFile, valueList, requireSort = True):
        # split results into a list, sort if requested, then compare the 2 lists
        results = outFile.getvalue().splitlines()
        if requireSort:
            results.sort()
        self.assertEqual(results, valueList)

    def testSimpleCsv(self):
        options = buildOptions('-ck s:0:5')
        outFile = StringIO()
        inFile = StringIO(TEST_INPUT_1)
        processFile(inFile, outFile, options)
        self._checkOutput(outFile, ['01:24, 2', '12:32, 1', '12:33, 1'])

    def testSimpleSortedText(self):
        """Also has header row"""
        options = buildOptions('-sk s:0:5 -f Note_that_checkOutput_doesnt_work_with_spaces')
        outFile = StringIO()
        inFile = StringIO(TEST_INPUT_1)
        processFile(inFile, outFile, options)
        self._checkOutput(outFile, ['Note_that_checkOutput_doesnt_work_with_spaces', '   01:24  2',
                '   12:32  1', '   12:33  1'], False)

    def testSimpleValues(self):
        options = buildOptions('-ck s:0:5 -v s:10:14 -v f:5 ' \
                + '-l k,k:cnt,0:min,0:max,0:tot,0:av,1:min,1:max,1:tot,1:av')
        outFile = StringIO()
        inFile = StringIO(TEST_INPUT_2)
        processFile(inFile, outFile, options)
        self._checkOutput(outFile, ['01:24, 2, 12.0, 72.0, 84.0, 42.0, 0.123, 6.3, 6.423, 3.2115',
                '12:32, 1, 999.0, 999.0, 999.0, 999.0, 9.99, 9.99, 9.99, 9.99',
                '12:33, 1, 33.0, 33.0, 33.0, 33.0, 3.21, 3.21, 3.21, 3.21'])


# ============================================================================
if __name__ == '__main__':
    unittest.main()
