/* (C) Copyright 2009, by John Dickson
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
 * @author johnd
 *
 */
public class CsvLineParserTest extends TestCase {
    private CsvLineParser m_parser = new CsvLineParser(); 
    
    /**
     * Test method for {@link net.sf.clichart.data.CsvLineParser#parseLine(java.lang.String, int)}.
     * @throws InvalidDataException 
     */
    public void testParseLine() throws InvalidDataException {
        // basic
        assertArrayEquals(new String[] {"foo", "bar", "baz"}, m_parser.parseLine("foo,bar,baz", 1));
        // whitespace stripped
        assertArrayEquals(new String[] {"foo", "bar", "baz"}, m_parser.parseLine(" foo,bar , baz ", 1));
        // single element
        assertArrayEquals(new String[] {"foo"}, m_parser.parseLine("foo", 1));
        // empty element at start
        assertArrayEquals(new String[] {"", "bar", "baz"}, m_parser.parseLine(",bar,baz", 1));
        // empty element in middle
        assertArrayEquals(new String[] {"foo", "", "baz"}, m_parser.parseLine("foo,,baz", 1));
        // empty element at end
        assertArrayEquals(new String[] {"foo", "bar", ""}, m_parser.parseLine("foo,bar,", 1));
    }

    private void assertArrayEquals(String[] expected, String[] actual) {
        assertEquals(expected.length, actual.length);

        for (int i = 0; i < expected.length; i++) {
            assertEquals("Element " + i + ": expected " + expected[i] + ", got " + actual[i], expected[i], actual[i]);
        }
    }

}
