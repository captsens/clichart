#! /usr/bin/env python

"""
(C) Copyright 2006-2008, by John Dickson

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


Library functions and classes for generating summary statistics from text data
"""

import sys, re, traceback

DEBUG = False

SIGNIFICANT_DIGITS = 6

# ==============================================================================
class StatslibException(Exception):
    pass

# ==============================================================================
class InvalidOptionException(StatslibException):
    pass

# ==============================================================================
class InvalidDataException(StatslibException):
    pass

# ==============================================================================
class LineMatcher:
    """Given a line of text, return True if it should be included"""
    def __init__(self, pattern = None):
        self.regex = None
        if pattern:
            try:
                self.regex = re.compile(pattern)
            except:
                raise InvalidOptionException('Invalid regular expression [%s]' % pattern)

    def shouldInclude(self, line):
        if not self.regex:
            return True
        return self.regex.search(line) is not None

# ------------------------------------------------------------------------------
def checkIndex(line, index, lineNumber):
    if index > 0 and len(line) <= index:
        raise InvalidDataException('Line %d: invalid substring index %d' % (lineNumber, index))
    if index < 0 and len(line) < abs(index):
        raise InvalidDataException('Line %d: invalid substring index %d' % (lineNumber, index))

# ------------------------------------------------------------------------------
def enablePsyco(enable):
    if enable:
        try:
            import psyco
            psyco.full()
            if DEBUG: print('Psyco enabled', file=sys.stderr)
        except ImportError:
            if DEBUG: print('Psyco not present', file=sys.stderr)
    else:
        if DEBUG: print('Psyco disabled', file=sys.stderr)

# ==============================================================================
class ValueExtractor:
    """Given a line of text, can extract and return a value from the line.
    Note that all indexes are 0-based"""
    def __init__(self, optionText):
        self.charStartIndex = None
        self.charEndIndex = None
        self.fieldIndex = None
        self.regex = None

        try:
            optionType, option = optionText.split(':', 1)
        except:
            raise InvalidOptionException('Invalid value option: %s' % optionText)
        if optionType =='s':
            try:
                indexes = option.split(':')
                self.charStartIndex = int(indexes[0])
                if len(indexes) > 1 and indexes[1]:
                    self.charEndIndex = int(indexes[1])
            except:
                raise InvalidOptionException('Invalid substring specification [%s]' % option)
        elif optionType == 'f':
            try:
                self.fieldIndex = int(option)
            except:
                raise InvalidOptionException('Invalid field number [%s]' % option)
        elif optionType == 'r':
            try:
                self.regex = re.compile(option)
            except:
                raise InvalidOptionException('Invalid regular expression [%s]' % option, True)
        else:
            raise InvalidOptionException('Invalid field specification [%s]' % optionText)

    def getValue(self, line, lineNumber):
        if self.charEndIndex is not None:
            checkIndex(line, self.charStartIndex, lineNumber)
            checkIndex(line, self.charEndIndex, lineNumber)
            return line[self.charStartIndex:self.charEndIndex]
        if self.charStartIndex is not None:
            checkIndex(line, self.charStartIndex, lineNumber)
            return line[self.charStartIndex:]
        elif self.fieldIndex is not None:
            try:
                return line.split()[self.fieldIndex]
            except IndexError:
                raise InvalidDataException('Line %d: cannot extract field number %d' % (lineNumber, self.fieldIndex))
        else:
            match = self.regex.search(line)
            if not match:
                raise InvalidDataException('Line %d: failed to match regular expression [%s]' % (lineNumber,
                        self.regex.pattern))
            if match.groups():
                return match.group(1)
            else:
                return match.group(0)

# ==============================================================================
class OutputFormatter:
    def __init__(self, isCsv, quote = False):
        self.isCsv = isCsv
        self.quote = quote

    def _quote(self, value):
        """Only quote if field contains comma or space"""
        if not self.quote:
            return value
        if ' ' not in value and ',' not in value and '"' not in value:
            return value
        return '"%s"' % value.replace('"', '""')

    def _format(self, value):
        """Try to format numbers as right-justified over 8 chars"""
        try:
            # ask forgiveness
            float(value)
            return '%-8s' % value
        except:
            return '%8s' % self._quote(value)


    def output(self, outFile, key, values):
        if self.isCsv:
            if key is not None:
                outFile.write('%s, ' % self._quote(key))
            print(', '.join([self._quote(str(value)) for value in values]), file=outFile)
        else:
            if key is not None:
                outFile.write('%s ' % self._quote(key))
            print('  '.join([self._format(value) for value in values]).rstrip(), file=outFile)

# ==============================================================================
class OutputColumnExtractor:
    """Note that column indexes are 0-based"""
    def __init__(self, optionString):
        fieldSpecs = [option.strip() for option in optionString.split(',')]
        # contains (field index or None, statsType or None)
        self.fields = []
        for fieldSpec in fieldSpecs:
            if fieldSpec == 'k':
                self.fields.append((None, None))
            elif fieldSpec ==  'k:cnt':
                self.fields.append((None, 'count'))
            else:
                try:
                    index, statsType = fieldSpec.split(':')
                    assert statsType in ('av', 'min', 'max', 'tot')
                    self.fields.append((int(index), statsType.strip()))
                except:
                    raise InvalidOptionException('Invalid output column specification [%s]' % fieldSpec)

    def getValues(self, key, keyCount, fieldStats):
        values = []
        for fieldIndex, statsType in self.fields:
            if statsType is None:
                values.append(key)
            elif fieldIndex is None:
                values.append(keyCount)
            else:
                values.append(fieldStats[fieldIndex].getValue(statsType))
        return values

    def checkIndexes(self, numIndexes):
        for fieldIndex, statsType in self.fields:
            if fieldIndex is not None and fieldIndex >= numIndexes:
                raise InvalidOptionException(\
                        'Invalid output field index [%d] - there are only %d fields available' \
                        % (fieldIndex, numIndexes))

# ==============================================================================
class Options:
    """Superclass for parsing and holding command-line options.  This one understands:
    -c isCsv
    -h to show help
    -k keyExtractor (default is the whole line)
    -l outputColumnExtractor
    -m lineMatcher
    -s sortOutput
    -v fieldExtractors (0 or more)
    --nojit enablePsyco
     """
    def __init__(self, defaultKeyExtractor):
        # default is to use the whole line
        self.keyExtractor = defaultKeyExtractor
        self.fieldExtractors = []
        self.lineMatcher = LineMatcher()
        self.isCsv = False
        self.outputColumnExtractor = OutputColumnExtractor('k,k:cnt')
        self.sortOutput = False
        self.enablePsyco = True

    def _parseOptions(self, options, usageFunc):
        """Return the options that weren't successfully parsed"""
        unparsedOptions = []
        for opt, arg in options:
            if opt == '-h':
                usageFunc()
            elif opt == '-c':
                self.isCsv = True
            elif opt == '-k':
                self.keyExtractor = ValueExtractor(arg)
            elif opt == '-l':
                self.outputColumnExtractor = OutputColumnExtractor(arg)
            elif opt == '-m':
                self.lineMatcher = LineMatcher(arg)
            elif opt == '-s':
                self.sortOutput = True
            elif opt == '-v':
                self.fieldExtractors.append(ValueExtractor(arg))
            elif opt == '--nojit':
                self.enablePsyco = False
            else:
                unparsedOptions.append((opt, arg))
        self.outputColumnExtractor.checkIndexes(len(self.fieldExtractors))
        return unparsedOptions

# ------------------------------------------------------------------------------
def splitLine(line, isCsv, strip=False):
    """Return the line, split on white space or comma.
    NOTE: Does not correctly ignore commas within quoted strings"""
    if isCsv:
        result = line.split(',')
    else:
        result = line.split()
    if strip:
        result = [item.strip() for item in result]
    return result

# ---------------------------------------------------------------
def formatOutputValue(value):
    """Try to output with no more than SIGNIFICANT_DIGITS"""
    quotient, remainder = divmod(value, 1.0)
    # some versions of python (2.3?) seem to make quotient a float...
    quotient = int(quotient)
    if remainder == 0 or quotient > 10 ** SIGNIFICANT_DIGITS:
        return '%d' % quotient
    # need to work out picture string to provide SIGNIFICANT_DIGITS overall
    quotientDigits = len(str(quotient))
    if quotient == 0:
        quotientDigits = 0
    pictureString = '%' + '%d.%d' % (SIGNIFICANT_DIGITS + 1, SIGNIFICANT_DIGITS - quotientDigits) + 'f'
    return (pictureString % value).strip()

