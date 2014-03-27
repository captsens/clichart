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

import java.util.regex.Pattern;

/**
 * A LineParser that splits the string based on white space
 *
 * @author johnd
 */
public class WhiteSpaceLineParser implements LineParser {

    /* ========================================================================
     *
     * Class (static) variables.
     */

    private static final Pattern SPLIT_PATTERN = Pattern.compile("\\s+");

    private static final String[] EMPTY_ARRAY = new String[]{};

    /* ========================================================================
     *
     * Instance variables.
     */

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

    public String[] parseLine(String line, int lineNumber) {
        line = line.trim();
        if (line.length() == 0) {
            return EMPTY_ARRAY;
        }

        return SPLIT_PATTERN.split(line.trim());
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
