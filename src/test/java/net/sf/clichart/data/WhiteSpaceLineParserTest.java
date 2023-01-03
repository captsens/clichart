/* (C) Copyright 2006, by John Dickson
 *
 * Project Info:  https://github.com/captsens/clichart
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
 * CLASS_DESCRIPTION
 *
 * @author johnd
 */
public class WhiteSpaceLineParserTest extends TestCase {

    /* ========================================================================
     *
     * Class (static) variables.
     */

    /* ========================================================================
     *
     * Instance variables.
     */

    private WhiteSpaceLineParser m_parser = new WhiteSpaceLineParser();

    /* ========================================================================
     *
     * Constructors
     */

    /* ========================================================================
     *
     * Static methods
     */

    /* ========================================================================
     *
     * Public methods
     */

    public void testParseLine() {
        assertArrayEquals(new String[]{"Hello"}, m_parser.parseLine("Hello", 1));
        assertArrayEquals(new String[]{"Hello", "to", "you", "too"}, m_parser.parseLine("  Hello to you    too   ", 1));
        assertArrayEquals(new String[]{"Hello", "to", "you", "too"}, m_parser.parseLine("Hello   to \t you\ttoo", 1));
        assertArrayEquals(new String[]{}, m_parser.parseLine("", 1));
    }

    /* ========================================================================
     *
     * Protected / package-private methods
     */

    /* ========================================================================
     *
     * Private methods
     */

    private void assertArrayEquals(String[] expected, String[] actual) {
        assertEquals(expected.length, actual.length);

        for (int i = 0; i < expected.length; i++) {
            assertEquals("Element " + i + ": expected " + expected[i] + ", got " + actual[i], expected[i], actual[i]);
        }
    }
}
