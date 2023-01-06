#!/usr/bin/env python3

"""
Unit tests for derivative.py
"""

import unittest
from io import StringIO, BytesIO
from clichart.derivative import *

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


class ProcessFileTest(unittest.TestCase):
    def assertLineText(self, expected, actual, isCsv):
        # NOTE: expected is always textual, not CSV
        def splitLine(text, lineIsCsv):
            if lineIsCsv:
                cpts = text.split(',')
            else:
                cpts = text.split()
            return [cpts[0], float(cpts[1]), float(cpts[2]), float(cpts[3]), float(cpts[4])]
        expectedCpts = splitLine(expected, False)
        actualCpts = splitLine(actual, isCsv)
        self.assertEqual(expectedCpts[0], actualCpts[0])
        for i in range(1, 5):
            self.assertEqual(expectedCpts[i], actualCpts[i], 0.00001)

    def _executeFile(self, input, isCsv):
        output = StringIO()
        processFile(BytesIO(input.encode('utf-8')), output, [1, 2, 3, 4], 0, '%H:%M', isCsv, False)
        print(output.getvalue())
        return output.getvalue().splitlines()

    def _validateFileContents(self, lines, isCsv):
        self.assertEqual(3, len(lines))
        self.assertLineText('10:24  -0.0117   -0.0017   0  -0.05', lines[0], isCsv)
        self.assertLineText('10:25  0.005000  0.003333  0  0.016667', lines[1], isCsv)
        self.assertLineText('10:26  0.005000  -0.0017   0  0.033333', lines[2], isCsv)

    def testProcessFile_Text(self):
        lines = self._executeFile(TEXT_INPUT, False)
        # print(lines)
        self._validateFileContents(lines, False)

    def testProcessFile_Csv(self):
        lines = self._executeFile(CSV_INPUT, True)
        # print(lines)
        self._validateFileContents(lines, True)


# ============================================================================
if __name__ == '__main__':
    unittest.main()