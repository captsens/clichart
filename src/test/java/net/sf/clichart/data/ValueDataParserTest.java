/* (C) Copyright 2006, by John Dickson
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

import junit.framework.TestCase;

/**
 * Unit tests for ValueDataParser.
 *
 * Note that AbstractDataParser has been tested by the TimeDataParserTest, so this test just checks the logic specific
 * to the ValueDataParser directly
 *
 * @author johnd
 */
public class ValueDataParserTest extends TestCase {

    /* ========================================================================
     *
     * Class (static) variables.
     */

    /* ========================================================================
     *
     * Instance variables.
     */

    private ValueDataParser m_dataParser;

    /* ========================================================================
     *
     * Lifecycle methods
     */

    /**
     * Constructs what's virtually a dummy object - we'll only call the parseXValueImpl method on it...
     * @throws Exception
     */
    protected void setUp() throws Exception {
        m_dataParser = new ValueDataParser(
                new LineParser() {
                    public String[] parseLine(String line, int lineNumber) throws InvalidDataException {
                        return null;
                    }
                }, 0, new int[] {1, 2}, false, false, false, 
                new DataSink() {
                    public void headerParsed(String[] headers) throws InvalidDataException {
                    }
                    public void dataParsed(Object xValue, Object[] yValues, int lineNumber)
                            throws InvalidDataException {
                    }
                    public void parsingFinished() {
                    }
                }
        );
    }

    /* ========================================================================
     *
     * Static methods
     */

    /* ========================================================================
     *
     * Public methods
     */

    public void testParseXValueImpl_OK() throws InvalidDataException {
        assertEquals(new Long(42), m_dataParser.parseXValueImpl("42", 3));
        assertEquals(new Double(0.125), m_dataParser.parseXValueImpl("0.125", 10));
    }

    public void testParseXValueImpl_Invalid() {
        try {
            m_dataParser.parseXValueImpl("", 1);
            fail();
        } catch (InvalidDataException expected) {}
    }

    /* ========================================================================
     *
     * Protected / package-private methods
     */

    /* ========================================================================
     *
     * Private methods
     */
}
