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

import java.util.Arrays;

/**
 * CLASS_DESCRIPTION
 *
 * @author johnd
 */
public class AbstractDataParserTest extends TestCase {

    /* ========================================================================
     *
     * Class (static) variables.
     */

    /* ========================================================================
     *
     * Instance variables.
     */

    private AbstractDataParser m_parser;

    /* ========================================================================
     *
     * Lifecycle methods
     */

    protected void setUp() throws Exception {
        DataSink sink = new DataSink() {
            public void headerParsed(String[] headers) throws InvalidDataException {
            }
            public void dataParsed(Object xValue, Object[] yValues, int lineNumber) throws InvalidDataException {
            }
            public void parsingFinished() {
            }
        };
        m_parser = new AbstractDataParser(new WhiteSpaceLineParser(), 0, new int[]{1,2,3}, false, true, false, sink) {
            protected Object parseXValueImpl(String xValue, int lineNumber) throws InvalidDataException {
                return null;
            }
        };
    }

    /* ========================================================================
     *
     * Static methods
     */

    /* ========================================================================
     *
     * Public methods
     */

    public void testShrinkArray_noChange() {
        String[] testArray = new String[]{"a", "b", "c"};
        assertSame(testArray, m_parser.shrinkArray(testArray, String.class));
    }

    public void testShrinkArray_oneNull() {
        String[] expected = new String[]{"a", "b", "c"};
        String[] testArray = new String[]{"a", "b", "c", null};
        assertArrayEquals(expected, m_parser.shrinkArray(testArray, String.class));
    }

    public void testShrinkArray_nothingButOneNull() {
        String[] expected = new String[0];
        String[] testArray = new String[]{null};
        assertArrayEquals(expected, m_parser.shrinkArray(testArray, String.class));
    }

    public void testShrinkArray_empty() {
        String[] expected = new String[0];
        String[] testArray = new String[0];
        assertArrayEquals(expected, m_parser.shrinkArray(testArray, String.class));
    }

    /* ========================================================================
     *
     * Protected / package-private methods
     */

    /* ========================================================================
     *
     * Private methods
     */

    private void assertArrayEquals(Object[] expected, Object[] actual) {
        if (!Arrays.deepEquals(expected, actual)) {
            assertTrue("Expected: " + Arrays.toString(expected) + ", actual: " + Arrays.toString(actual), false);
        }
        assertEquals(expected.getClass(), actual.getClass());
    }
}
