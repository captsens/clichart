#!/usr/bin/env python3

"""
Unit tests for histogram.py

TODO:
- Improve tests for calculating histogram
- Add tests for output
"""

import unittest
from io import StringIO
from histogram import *
from statslib import InvalidDataException

CSV_INPUT = """1, 17.2, 55, 120.888
6, 4, 51, 220.888
4, 12.6, 52, 120.888
4, 13.2, 52, 320.888
3, 13.19, 55, 120.888
-1, 12.88, 58, 20.888"""

HEADER = 'column_0, column_1, column_2, column_3\n'

# contain (minValue, maxValue, allValues) for each column
COLUMN_RESULTS = (
    (-1, 6, [-1, 1, 3, 4, 4, 6]),
    (4, 17.2, [17.2, 4, 12.6, 13.2, 13.19, 12.88]),
    (51, 58, [55, 51, 52, 52, 55, 58]),
    (20.888, 320.888, [20.888, 120.888, 120.888, 120.888, 220.888, 320.888]))

CSV_OUTPUT = """51, 52, 1, 16.667
52, 53, 2, 33.333
53, 54, 0,  0.000
54, 55, 0,  0.000
55, 56, 2, 33.333
56, 57, 0,  0.000
57, 58, 1, 16.667
"""
# ============================================================================
class HistogramTest(unittest.TestCase):
    def testParseData_CsvNoHeader(self):
        self._testParseData(CSV_INPUT, True, False)

    def testParseData_CsvWithHeader(self):
        self._testParseData(self._getInputWithHeader(), True, True)

    def testParseData_TextNoHeader(self):
        self._testParseData(CSV_INPUT.replace(',', ' '), False, False)
        
    def testParseData_TextWithHeader(self):
        self._testParseData(self._getInputWithHeader().replace(',', ' '), False, True)

    def testParseData_NonNumeric(self):
        testData = CSV_INPUT.replace('12.88', 'xxx')
        try:
            self._testParseData(testData, True, False)
            self.fail()
        except InvalidDataException as e:
            pass

    def testCalculateHistogram_numIntervals(self):
        minValue, maxValue, allValues = COLUMN_RESULTS[0]
        options = self._buildOptions(numIntervals=7, showPercent=True)
        intervals = calculateHistogram(allValues, minValue, maxValue, options)
        self._validateHistogram(intervals)
        options.cumulative = True
        intervals = calculateHistogram(allValues, minValue, maxValue, options)
        self._validateHistogram_cumulative(intervals)

    def testCalculateHistogram_intervalSize(self):
        minValue, maxValue, allValues = COLUMN_RESULTS[0]
        options = self._buildOptions(intervalSize=1, showPercent=True)
        intervals = calculateHistogram(allValues, minValue, maxValue, options)
        self._validateHistogram(intervals)
        options.cumulative = True
        intervals = calculateHistogram(allValues, minValue, maxValue, options)
        self._validateHistogram_cumulative(intervals)

    def _buildOptions(self, **kw):
        options = Options()
        for property, value in list(kw.items()):
            setattr(options, property, value)
        return options

    def _validateHistogram(self, intervals):
        self._assertListsAlmostEqual([1, 0, 1, 0, 1, 2, 1], [interval.count for interval in intervals], sortLists = False)
        self._assertListsAlmostEqual([-1, 0, 1, 2, 3, 4, 5], [interval.startValue for interval in intervals], sortLists = False)
        self._assertListsAlmostEqual([0, 1, 2, 3, 4, 5, 6], [interval.endValue for interval in intervals], sortLists = False)
        self._assertListsAlmostEqual([100/6.0, 0, 100/6.0, 0, 100/6.0, 200/6.0, 100/6.0],
                [interval.percentage for interval in intervals], sortLists = False)
        
    def _validateHistogram_cumulative(self, intervals):
        self._assertListsAlmostEqual([1, 1, 2, 2, 3, 5, 6], [interval.count for interval in intervals], sortLists = False)
        self._assertListsAlmostEqual([-1, 0, 1, 2, 3, 4, 5], [interval.startValue for interval in intervals], sortLists = False)
        self._assertListsAlmostEqual([0, 1, 2, 3, 4, 5, 6], [interval.endValue for interval in intervals], sortLists = False)
        self._assertListsAlmostEqual([100/6.0, 100/6.0, 200/6.0, 200/6.0, 300/6.0, 500/6.0, 600/6.0],
                [interval.percentage for interval in intervals], sortLists = False)

    def _testParseData(self, testData, isCsv, skipFirst):
        options = self._buildOptions(isCsv=isCsv, skipFirst=skipFirst)
        for columnIndex in range(4):
            options.columnIndex = columnIndex
            minValue, maxValue, allValues = parseData(StringIO(testData), options)
            self._verifyParseDataResults(minValue, maxValue, allValues, *COLUMN_RESULTS[columnIndex])
        
    def _verifyParseDataResults(self, minValue, maxValue, allValues, expectedMinValue, expectedMaxValue, expectedAllValues):
        self.assertAlmostEqual(expectedMinValue, minValue)
        self.assertAlmostEqual(expectedMaxValue, maxValue)
        if expectedAllValues is not None:
            self._assertListsAlmostEqual(expectedAllValues, allValues)
        
    def _getInputWithHeader(self):
        return HEADER + CSV_INPUT

    def testOutputHistogram(self):
        options = self._buildOptions(numIntervals=7, isCsv=True, columnIndex=0, showPercent=True)
        outFile = StringIO()
        minValue, maxValue, values = COLUMN_RESULTS[2]
        outputHistogram(outFile, values, minValue, maxValue, options)
        self.assertEqual(CSV_OUTPUT, outFile.getvalue())
        
    def _assertListsAlmostEqual(self, listA, listB, sortLists = True):
        self.assertEqual(len(listA), len(listB))
        if sortLists:
            listA = sorted(listA)
            listB = sorted(listB)
        for valueA, valueB in zip(listA, listB):
            self.assertAlmostEqual(valueA, valueB)

# ============================================================================
if __name__ == '__main__':
    unittest.main()
