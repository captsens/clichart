#!/usr/bin/env python

"""
Unit tests for aggregate.py
"""

import unittest
import testUtils
from StringIO import StringIO

testUtils.checkPythonPath('aggregate')
from aggregate import *
import statslib

TEXT_INPUT = """10:23   58.5    47.3    2376    2020
10:24   57.8    47.2    2376    2017
10:25   58.1    47.4    2376    2018
10:26   58.4    47.3    2376    2020
"""

CSV_INPUT = """10:23, 58.5, 47.3, 2376, 2020
10:24, 57.8, 47.2, 2376, 2017
10:25, 58.1, 47.4, 2376, 2018
10:26, 58.4, 47.3, 2376, 2020
"""

CSV_INPUT_WITH_ZERO = '10:23, 0.12'

SINGLE_KEY_CSV_INPUT = """10:23, 58.5, 47.3, 2376, 2020
10:23, 57.8, 47.3, 2376, 2017
10:25, 58.1, 47.4, 2376, 2018
10:26, 58.4, 47.3, 2376, 2020
10:23, 12.8, 40.3, 2399, 2011
"""

# ----------------------------------------------------------------------------
def assertRow(testCase, expectedValues, row, isCsv=True, keyColumns=[]):
    if isCsv:
        rowValues = row.split(',')
    else:
        rowValues = row.split()
    for columnIndex in range(len(rowValues)):
        if columnIndex in keyColumns:
            rowValues[columnIndex] = rowValues[columnIndex].strip()
        else:
            rowValues[columnIndex] = float(rowValues[columnIndex])
    testCase.assertAlmostEqualList(rowValues, expectedValues)

# ============================================================================
class ProcessFileTest(unittest.TestCase):
    def assertAlmostEqualList(self, expectedList, actualList):
        self.assertEqual(len(expectedList), len(actualList))
        for expected, actual in zip(expectedList, actualList):
            if type(expected) == type('abc'):
                self.assertEqual(expected, actual)
            else:
                self.assertAlmostEqual(expected, actual)
    def testProcessFile_EveryAggregateType_Text(self):
        output = StringIO()
        processFile(StringIO(TEXT_INPUT), output, ['1:min', '1:max', '1:av',
                '1:cnt', '1:tot', '1:first', '1:last', '1:sd'], False, False, False, [])
        results = [float(value) for value in output.getvalue().split()]
        self.assertAlmostEqualList([57.8, 58.5, 58.2, 4.0, 232.8, 58.5, 58.4, 0.273861], results)
    def testProcessFile_SimpleExpressions_Text(self):
        output = StringIO()
        processFile(StringIO(TEXT_INPUT), output, ['1:min - 57.8', '1:max / 1:max', '1:av / 10.0',
                '1:tot / 1:cnt'], False, False, False, [])
        results = [float(value) for value in output.getvalue().split()]
        self.assertAlmostEqualList([0.0, 1.0, 5.82, 58.2], results)
    def testProcessFile_ManyColumns_Csv(self):
        output = StringIO()
        processFile(StringIO(CSV_INPUT), output, ['1:min', '2:max', '3:av',
                '4:tot', '1:first', '2:last'], True, False, False, [])
        results = [float(value) for value in output.getvalue().split(',')]
        self.assertAlmostEqualList([57.8, 47.4, 2376, 8075, 58.5, 47.3], results)
    def testProcessFile_NegativeColumnIndex_Csv(self):
        output = StringIO()
        processFile(StringIO(CSV_INPUT), output, ['-4:min', '-3:max', '-2:av',
                '-1:tot', '-4:first', '-3:last'], True, False, False, [])
        results = [float(value) for value in output.getvalue().split(',')]
        self.assertAlmostEqualList([57.8, 47.4, 2376, 8075, 58.5, 47.3], results)
    def testProcessFile_OneColumn_CsvWithHeader(self):
        output = StringIO()
        processFile(StringIO(CSV_INPUT), output, ['1:min'], True, True, False, [])
        results = [float(value) for value in output.getvalue().split(',')]
        self.assertAlmostEqualList([57.8], results)
    def testProcessFile_WithSinglePrefix(self):
        output = StringIO()
        processFile(StringIO(CSV_INPUT), output, ['1:min', '2:max'], True, True, False, ['12/03/2007'])
        resultList = output.getvalue().split(',')
        self.assertEqual('12/03/2007', resultList[0])
        results = [float(value) for value in resultList[1:]]
        self.assertAlmostEqualList([57.8, 47.4], results)
    def testProcessFile_WithMultipleSuffix(self):
        output = StringIO()
        processFile(StringIO(CSV_INPUT), output, ['1:min', '2:max'], True, True, False,
                suffixColumnValues=['foo', 'bar'])
        resultList = output.getvalue().split(',')
        self.assertEqual('foo', resultList[2].strip())
        self.assertEqual('bar', resultList[3].strip())
        results = [float(value) for value in resultList[0:2]]
        self.assertAlmostEqualList([57.8, 47.4], results)
    def testProcessFile_EmptyAndSilent(self):
        output = StringIO()
        processFile(StringIO(''), output, ['1:min'], True, True, True, [])
        self.assertEqual('', output.getvalue())
    def testProcessFile_EmptyAndNotSilent(self):
        output = StringIO()
        try:
            processFile(StringIO(''), output, ['1:min'], True, True, False, [])
            fail()
        except statslib.InvalidDataException, expected:
            pass
    def testProcessFile_InvalidNumberInInput(self):
        output = StringIO()
        invalidInput = CSV_INPUT.replace('58.1', '58xxx1')
        try:
            processFile(StringIO(invalidInput), output, ['1:min'], True, False, False, [])
            self.fail()
        except statslib.InvalidDataException, expected:
            pass
    def testProcessFile_InvalidOutputColumnIndex(self):
        output = StringIO()
        try:
            processFile(StringIO(CSV_INPUT), output, ['5:min'], True, False, False, [])
            self.fail()
        except statslib.InvalidDataException, expected:
            pass
    def testProcessFile_InvalidOutputExpression(self):
        output = StringIO()
        try:
            processFile(StringIO(CSV_INPUT), output, ['2:min +/ 2'], True, False, False, [])
            self.fail()
        except statslib.InvalidOptionException, expected:
            pass
    def testProcessFile_WithZeroQuotient(self):
        output = StringIO()
        processFile(StringIO(CSV_INPUT_WITH_ZERO), output, ['1:min', '1:av', '1:tot'], True, False, False, [])
        assertRow(self, [0.12, 0.12, 0.12], output.getvalue())
    def testProcessFile_KeyedCsv(self):
        output = StringIO()
        processFile(StringIO(SINGLE_KEY_CSV_INPUT), output, ['0:k', '1:min', '2:av'], True, False, False, [])
        resultLines = output.getvalue().splitlines()
        self.assertEqual(len(resultLines), 3)
        assertRow(self, ['10:23', 12.8, 44.9667], resultLines[0], keyColumns=[0])
        assertRow(self, ['10:25', 58.1, 47.4], resultLines[1], keyColumns=[0])
        assertRow(self, ['10:26', 58.4, 47.3], resultLines[2], keyColumns=[0])
    def testProcessFile_MultiKeyedCsv(self):
        """This one has 2 key columns, and they're specified part way through the column list"""
        output = StringIO()
        processFile(StringIO(SINGLE_KEY_CSV_INPUT), output, ['1:min', '0:k', '3:max', '2:k'], True, False, False, [])
        resultLines = output.getvalue().splitlines()
        self.assertEqual(len(resultLines), 4)
        assertRow(self, ['10:23', '40.3', 12.8, 2399], resultLines[0], keyColumns=[0, 1])
        assertRow(self, ['10:23', '47.3', 57.8, 2376], resultLines[1], keyColumns=[0, 1])
        assertRow(self, ['10:25', '47.4', 58.1, 2376], resultLines[2], keyColumns=[0, 1])
        assertRow(self, ['10:26', '47.3', 58.4, 2376], resultLines[3], keyColumns=[0, 1])

# ============================================================================
class FunctionTest(unittest.TestCase):
    def _testFormatOutputValueEqual(self, value):
        self.assertAlmostEqual(value, float(formatOutputValue(value)))

    def _testTruncated(self, value):
        result = formatOutputValue(value)
        self.assertNotEqual(value, float(result))
        maxLength = 6
        if '.' in result:
            maxLength += 1
        self.assert_(len(result) <= maxLength)
        # at the very least, the part before the decimal point should be equal
        self.assertEqual(int(value), int(float(result)))

    def testFormatOutputValue(self):
        self._testFormatOutputValueEqual(0)
        self._testFormatOutputValueEqual(0.0)
        self._testFormatOutputValueEqual(0.12345)
        self._testFormatOutputValueEqual(12345.0)
        self._testFormatOutputValueEqual(1234567890.0)
        self._testFormatOutputValueEqual(8.98765)
        self._testTruncated(8.987654)
        self._testTruncated(8.987654)

# ============================================================================
if __name__ == '__main__':
    unittest.main()
