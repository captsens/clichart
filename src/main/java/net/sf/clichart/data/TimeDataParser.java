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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Implementation of a DataParser for time-based data, i.e. data where the X axis will be a date/time/datetime.
 *
 * <p>The xValue is passed to the sink as a Date.
 *
 * <p>Note that this class is not threadsafe.
 *
 * @author johnd
 */
public class TimeDataParser extends AbstractDataParser {

    /* ========================================================================
     *
     * Class (static) variables.
     */

    private static final String DEFAULT_DATE_TIME_PATTERN = "HH:mm";

    /* ========================================================================
     *
     * Instance variables.
     */

    // Used to parse the dates/times
    private final DateFormat m_dateParser;


    /* ========================================================================
     *
     * Constructors
     */

    public TimeDataParser(LineParser lineParser, String dateTimePattern, int xColumnIndex, int[] yColumnIndexes,
            boolean hasHeader, boolean ignoreMissingValues, boolean ignoreEmptyValues, DataSink sink) {
        super(lineParser, xColumnIndex, yColumnIndexes, hasHeader, ignoreMissingValues, ignoreEmptyValues, sink);

        // must have an xColumn
        assert xColumnIndex >= 0;

        if (dateTimePattern == null) {
            dateTimePattern =  DEFAULT_DATE_TIME_PATTERN;
        }
        m_dateParser = new SimpleDateFormat(dateTimePattern);
        m_dateParser.setLenient(false);
    }

    /* ========================================================================
     *
     * Static methods
     */

    /* ========================================================================
     *
     * Public methods
     */

    /* ========================================================================
     *
     * Protected / package-private methods
     */

    protected Object parseXValueImpl(String xValue, int lineNumber) throws InvalidDataException {
        try {
            return m_dateParser.parse(xValue);
        } catch (ParseException e) {
            throw new InvalidDataException("Invalid date: [" + xValue + "], line " + lineNumber);
        }
    }

    /* ========================================================================
     *
     * Private methods
     */

}
