#!/usr/bin/env python

"""
Unit tests for mid.py
"""

import unittest
from mid import *

# ============================================================================
class RangeSpecTest(unittest.TestCase):
    def _assertRange(self, rangeSpec, start, end, linesInFile = None):
        if linesInFile is not None:
            rangeSpec.convertToPositive(linesInFile)
        self.assertEqual(rangeSpec.start, start)
        self.assertEqual(rangeSpec.end, end)
        if start < 0 or end < 0:
            self.assertTrue(rangeSpec.hasNegativeIndex())
        else:
            self.assertTrue(not rangeSpec.hasNegativeIndex())

    def testArg_Positive(self):
        rangeSpec = RangeSpec('8:67')
        self._assertRange(rangeSpec, 8, 67)
        self._assertRange(rangeSpec, 8, 67, 100)

    def testArg_FirstNegative(self):
        rangeSpec = RangeSpec('-73:50')
        self._assertRange(rangeSpec, -73, 50)
        self._assertRange(rangeSpec, 28, 50, 100)

    def testArg_SecondNegative(self):
        rangeSpec = RangeSpec('17:-1')
        self._assertRange(rangeSpec, 17, -1)
        self._assertRange(rangeSpec, 17, 100, 100)

    def testArg_bothNegative(self):
        rangeSpec = RangeSpec('-11:-4')
        self._assertRange(rangeSpec, -11, -4)
        self._assertRange(rangeSpec, 90, 97, 100)

    def testArg_invalid(self):
        try:
            RangeSpec('rx:-11:-4')
            self.fail()
        except ArgException:
            pass

# ============================================================================
class LineParserTest(unittest.TestCase):

    class MockLineParser(LineParser):
        def __init__(self, args):
            LineParser.__init__(self, args)
            self.lines = []
        def _printLine(self, line):
            self.lines.append(line)

    def _readFile(self, parser, numLines):
        for i in range(numLines):
            parser.lineRead('%d\n' % (i + 1))
        parser.readingFinished()

    def testSimpleRange(self):
        """Single range, positive indexes"""
        parser = self.MockLineParser([RangeSpec('5:20')])
        self._readFile(parser, 30)
        self.assertEqual(16, len(parser.lines))
        self.assertEqual('5\n', parser.lines[0])
        self.assertEqual('20\n', parser.lines[-1])

    def testSingleLineRange(self):
        """Single range for 1 line, positive indexes"""
        parser = self.MockLineParser([RangeSpec('8:8')])
        self._readFile(parser, 30)
        self.assertEqual(1, len(parser.lines))
        self.assertEqual('8\n', parser.lines[0])

    def testOverlappingRanges(self):
        """Several ranges covering whole file, overlapping"""
        parser = self.MockLineParser([RangeSpec('5:20'), RangeSpec('1:8'), RangeSpec('-15:-1')])
        self._readFile(parser, 30)
        self.assertEqual(30, len(parser.lines))
        self.assertEqual('1\n', parser.lines[0])
        self.assertEqual('30\n', parser.lines[-1])

    def testNonOverlappingRanges(self):
        """Several ranges, not overlapping"""
        parser = self.MockLineParser([RangeSpec('2:4'), RangeSpec('7:-20'), RangeSpec('-15:18'),
                RangeSpec('-4:-1')])
        self._readFile(parser, 30)
        self.assertEqual(15, len(parser.lines))
        self.assertEqual('2\n', parser.lines[0])
        self.assertEqual('4\n', parser.lines[2])
        self.assertEqual('7\n', parser.lines[3])
        self.assertEqual('11\n', parser.lines[7])
        self.assertEqual('16\n', parser.lines[8])
        self.assertEqual('18\n', parser.lines[10])
        self.assertEqual('27\n', parser.lines[11])
        self.assertEqual('30\n', parser.lines[14])



# ============================================================================
if __name__ == '__main__':
    unittest.main()