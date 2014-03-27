/* (C) Copyright 2006-2009, by John Dickson
 *
 * Project Info:  http://clichart.sourceforge.net/
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 */

package net.sf.clichart.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;

/**
 * Abstract base class for data parsers.  Subclasses are responsible for parsing the X value.
 *
 * <p>Note: In order to add a second axis, call addSecondAxis before parsing starts.
 *
 * @author johnd
 */
public abstract class AbstractDataParser implements DataParser {

    /* ========================================================================
     *
     * Class (static) variables.
     */

    /* ========================================================================
     *
     * Instance variables.
     */

    /** Data sink for the main axis */
    private final DataSink m_sink;

    /** Data sink for the second axis (if any) */
    private DataSink m_secondAxisSink = null;

    private final LineParser m_lineParser;

    /** Index (0-based) of the column containing the date or time value.  Use -1 to indicate that there is no x value */
    private final int m_xColumnIndex;

    /** Indexes (0-based) of the columns containing the values we're interested in */
    private final int[] m_yColumnIndexes;

    /** Indexes (0-based) of the columns containing the values we're interested in for the second axis (if any) */
    private int[] m_secondAxisYColumnIndexes = null;

    // True if the next line of data is to be parsed as a header
    private boolean m_nextLineIsHeader = false;

    // if true, just skip line if not enough column values parsed
    private final boolean m_ignoreMissingValues;

    // if true, just skip line if any column values are empty strings
    private final boolean m_ignoreEmptyValues;


    /* ========================================================================
     *
     * Constructors
     */

    protected AbstractDataParser(LineParser lineParser, int xColumnIndex, int[] yColumnIndexes,
            boolean hasHeader, boolean ignoreMissingValues, boolean ignoreEmptyValues, DataSink sink) {
        assert lineParser != null;
        m_lineParser = lineParser;

        assert xColumnIndex >= -1;
        m_xColumnIndex = xColumnIndex;

        assert yColumnIndexes.length > 0;
        m_yColumnIndexes = yColumnIndexes;

        m_nextLineIsHeader = hasHeader;
        m_ignoreMissingValues = ignoreMissingValues;
        m_ignoreEmptyValues = ignoreEmptyValues;

        assert sink != null;
        m_sink = sink;
    }

    /* ========================================================================
     *
     * Static methods
     */

    /* ========================================================================
     *
     * Public methods
     */

    /**
     * Configure parsing for values to be displayed on a second axis on the chart.
     *
     * @param yColumnIndexes  Column indexes to display on the second chart
     * @param sink  Data sink for the second axis.
     */
    public void addSecondAxis(int[] yColumnIndexes, DataSink sink) {
        assert yColumnIndexes != null;
        assert yColumnIndexes.length > 0;
        m_secondAxisYColumnIndexes = yColumnIndexes;

        assert sink != null;
        m_secondAxisSink = sink;
    }

    public void parse(Reader source) throws IOException, InvalidDataException {
        assert m_sink != null : "No sink configured";

        BufferedReader lineReader = new BufferedReader(source);
        // Note: 1-based
        int lineNumber = 1;
        String line = lineReader.readLine();

        while (line != null) {
            parseLine(line, lineNumber++);
            line = lineReader.readLine();
        }

        m_sink.parsingFinished();
    }


    /* ========================================================================
     *
     * Protected / package-private methods
     */

    /**
     * Parse the provided X value, and return as an appropriate object, e.g. Date, Integer, Double
     */
    protected abstract Object parseXValueImpl(String xValue, int lineNumber) throws InvalidDataException;

    /* ========================================================================
     *
     * Private methods
     */

    /**
     * Parse the line, and advise the sink
     */
    private void parseLine(String line, int lineNumber) throws InvalidDataException {
        if (line.trim().length() == 0) {
            // skip blank lines
            return;
        }

        String[] lineCpts = m_lineParser.parseLine(line, lineNumber);
        if (m_nextLineIsHeader) {
            parseAllHeaders(lineCpts);
            m_nextLineIsHeader = false;
            return;
        }

        Object xValue = null;
        if (m_xColumnIndex >= 0) {
            xValue = parseXValue(lineCpts, lineNumber);
        } else {
            // we'll just use the line number as the X value
            xValue = new Integer(lineNumber);
        }
        m_sink.dataParsed(xValue, parseYValues(lineCpts, m_yColumnIndexes, lineNumber), lineNumber);
        if (m_secondAxisSink != null) {
            m_secondAxisSink.dataParsed(xValue, parseYValues(lineCpts, m_secondAxisYColumnIndexes, lineNumber),
                    lineNumber);
        }
    }

    private void parseAllHeaders(String[] lineCpts) throws InvalidDataException {
        String[] headers = parseHeaders(lineCpts, m_yColumnIndexes);
        m_sink.headerParsed(headers);

        if (m_secondAxisSink != null) {
            headers = parseHeaders(lineCpts, m_secondAxisYColumnIndexes);
            m_secondAxisSink.headerParsed(headers);
        }
    }

    private String[] parseHeaders(String[] lineCpts, int[] columnIndexes) throws InvalidDataException {
        String[] headers = new String[columnIndexes.length];
        for (int i = 0; i < columnIndexes.length; i++) {
            try {
                headers[i] = lineCpts[columnIndexes[i]];
            } catch (ArrayIndexOutOfBoundsException e) {
                if (!m_ignoreMissingValues) {
                    throw new InvalidDataException("Not enough header columns - cannot find column "
                            + columnIndexes[i]);
                }
            }
        }
        headers = (String[])shrinkArray(headers, String.class);
        return headers;
    }

    private Object[] parseYValues(String[] lineCpts, int[] columnIndexes, int lineNumber) throws InvalidDataException {
        Object[] values = new Object[columnIndexes.length];

        for (int i = 0; i < columnIndexes.length; i++) {
            try {
                values[i] = ParseUtils.parseValue(lineCpts[columnIndexes[i]], lineNumber);
            } catch (ArrayIndexOutOfBoundsException e) {
                if (!m_ignoreMissingValues) {
                    throw new InvalidDataException("Not enough data columns in line " + lineNumber
                            + " - cannot find column " + columnIndexes[i]);
                }
            } catch (InvalidDataException e) {
                String dataValue = lineCpts[columnIndexes[i]].trim();
                if (!m_ignoreEmptyValues || dataValue.length() > 0) {
                    throw e;
                }
            }
        }
        if (m_ignoreMissingValues) {
            values = shrinkArray(values, Object.class);
        }
        return values;
    }

    private Object parseXValue(String[] lineCpts, int lineNumber) throws InvalidDataException {
        assert m_xColumnIndex >= 0;

        try {
            return parseXValueImpl(lineCpts[m_xColumnIndex], lineNumber);

        } catch (ArrayIndexOutOfBoundsException e) {
            throw new InvalidDataException("Not enough columns in line " + lineNumber
                    + " - cannot find timestamp column " + m_xColumnIndex);
        }
    }

    /**
     * If the array has any null values at the end, shrink it to remove them.  Used when m_ignoreMissingValues is
     * specified, and the data doesn't have as many columns as expected.
     * @param array Note that this is modified in situ
     */
    Object[] shrinkArray(Object[] array, Class arrayType) {
        if (array.length == 0) {
            return array;
        }

        if (array[array.length - 1] != null) {
            // nothing to do
            return array;
        }

        // walk backwards through the array, looking for the first non-null value
        for (int i = array.length -1; i >= 0; i--) {
            if (array[i] != null) {
                Object[] target = (Object[]) Array.newInstance(arrayType, i + 1);
                System.arraycopy(array, 0, target, 0, i + 1);
                return target;
            }
        }

        // if we got here, it was an array containing only nulls
        return (Object[]) Array.newInstance(arrayType, 0);
    }

}
