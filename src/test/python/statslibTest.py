#!/usr/bin/env python

"""
Unit tests for statslib
"""

import unittest
import testUtils
from StringIO import StringIO

testUtils.checkPythonPath('statslib')
from statslib import *

TEST_LINE = 'abcdef'
TEST_LINE_WITH_SPACES = 'abc def   ghi\tjkl'

ONE_FIELD = [12]
THREE_FIELDS = [12, 1.23, 'some text']
KEY = 'The key'

# ============================================================================
class LineMatcherTest(unittest.TestCase):
    def testValidPattern(self):
        matcher = LineMatcher(r'abc.*?xyz')
        self.assertEquals(matcher.shouldInclude(''), False)
        self.assertEquals(matcher.shouldInclude('Some line'), False)
        self.assertEquals(matcher.shouldInclude('abcxyz'), True)
        self.assertEquals(matcher.shouldInclude('abcMeaninglessTextxyz'), True)

    def testNullPattern(self):
        matcher = LineMatcher()
        self.assert_(matcher.shouldInclude(''))
        self.assert_(matcher.shouldInclude('Some line'))

    def testInvalidPattern(self):
        try:
            LineMatcher('(group with no ending paren')
            self.fail()
        except InvalidOptionException:
            pass

# ============================================================================
class CheckIndexTest(unittest.TestCase):
    def testOk(self):
        checkIndex(TEST_LINE, 0, 1)
        checkIndex(TEST_LINE, 1, 1)
        checkIndex(TEST_LINE, 5, 1)
        checkIndex(TEST_LINE, -1, 1)
        checkIndex(TEST_LINE, -6, 1)
    def testInvalidPositive(self):
        try:
            checkIndex(TEST_LINE, 6, 20)
            self.fail()
        except InvalidDataException:
            pass
    def testInvalidNegative(self):
        try:
            checkIndex(TEST_LINE, -7, 20)
            self.fail()
        except InvalidDataException:
            pass

# ============================================================================
class ValueExtractorTest(unittest.TestCase):
    def testGetValue_SubstringWithEnd(self):
        extractor = ValueExtractor('s:3:5')
        self.assertEquals('de', extractor.getValue(TEST_LINE, 1))
    def testGetValue_SubstringWithoutEnd(self):
        extractor = ValueExtractor('s:2:')
        self.assertEquals('cdef', extractor.getValue(TEST_LINE, 1))
    def testGetValue_SubstringBothNegative(self):
        extractor = ValueExtractor('s:-4:-2')
        self.assertEquals('cd', extractor.getValue(TEST_LINE, 1))
    def testGetValue_SubstringOneNegative(self):
        extractor = ValueExtractor('s:2:-2')
        self.assertEquals('cd', extractor.getValue(TEST_LINE, 1))
    def testGetValue_FieldNumberFirst(self):
        extractor = ValueExtractor('f:0')
        self.assertEquals('abc', extractor.getValue(TEST_LINE_WITH_SPACES, 1))
    def testGetValue_FieldNumberLast(self):
        extractor = ValueExtractor('f:3')
        self.assertEquals('jkl', extractor.getValue(TEST_LINE_WITH_SPACES, 1))
    def testGetValue_FieldNumberInvalid(self):
        extractor = ValueExtractor('f:4')
        try:
            extractor.getValue(TEST_LINE_WITH_SPACES, 1)
            self.fail()
        except InvalidDataException:
            pass
    def testGetValue_RegexWithGroup(self):
        extractor = ValueExtractor(r'r:b(.*?)g')
        self.assertEquals('c def   ', extractor.getValue(TEST_LINE_WITH_SPACES, 1))
    def testGetValue_RegexWithoutGroup(self):
        extractor = ValueExtractor(r'r:b.*?g')
        self.assertEquals('bc def   g', extractor.getValue(TEST_LINE_WITH_SPACES, 1))
    def testGetValue_RegexNoMatch(self):
        extractor = ValueExtractor(r'r:NO_MATCH')
        try:
            extractor.getValue(TEST_LINE_WITH_SPACES, 1)
            self.fail()
        except InvalidDataException:
            pass
    def _checkInvalidOption(self, optionText):
        try:
            ValueExtractor(optionText)
            self.fail()
        except InvalidOptionException:
            pass
    def testInvalidOptions(self):
        self._checkInvalidOption('No colon')
        self._checkInvalidOption('a:1:2')
        self._checkInvalidOption('s:x:2')
        self._checkInvalidOption('s:0:-y')
        self._checkInvalidOption('f:12.2')
        self._checkInvalidOption('f:12:2')
        self._checkInvalidOption('r:regex(with unbalanced parens')

# ============================================================================
class OutputFormatterTest(unittest.TestCase):
    def _testOutput(self, isCsv, quote, key, values, expected):
        output = StringIO()
        formatter = OutputFormatter(isCsv, quote)
        formatter.output(output, key, values)
        self.assertEquals(expected + '\n', output.getvalue())

    def testOutput(self):
        # Text, no quoting
        self._testOutput(False, False, None, ONE_FIELD, '12')
        self._testOutput(False, False, KEY, ONE_FIELD, '%s 12' % KEY)
        self._testOutput(False, False, None, THREE_FIELDS, '12        1.23      some text')
        self._testOutput(False, False, KEY, THREE_FIELDS, '%s 12        1.23      some text' % KEY)
        # CSV, no quoting
        self._testOutput(True, False, None, ONE_FIELD, '12')
        self._testOutput(True, False, KEY, ONE_FIELD, '%s, 12' % KEY)
        self._testOutput(True, False, None, THREE_FIELDS, '12, 1.23, some text')
        self._testOutput(True, False, KEY, THREE_FIELDS, '%s, 12, 1.23, some text' % KEY)
        # CSV, with quoting
        self._testOutput(True, True, None, ONE_FIELD, '12')
        self._testOutput(True, True, KEY, ONE_FIELD, '"%s", 12' % KEY)
        self._testOutput(True, True, None, THREE_FIELDS, '12, 1.23, "some text"')
        self._testOutput(True, True, KEY, THREE_FIELDS, '"%s", 12, 1.23, "some text"' % KEY)
        self._testOutput(True, True, KEY, THREE_FIELDS + ['"fld', 'field with " quote and spaces', 'nospace'],
                 '"%s", 12, 1.23, "some text", """fld", "field with "" quote and spaces", nospace' % KEY)

# ============================================================================
if __name__ == '__main__':
    unittest.main()