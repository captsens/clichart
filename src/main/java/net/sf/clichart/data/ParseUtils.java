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

/**
 * Static helpers for parsing values etc.
 *
 * @author johnd
 */

public class ParseUtils {

    /* ========================================================================
     *
     * Class (static) variables.
     */

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

    /**
     * Return the appropriate wrapper type (Long or Double) for a value
     */
    public static Object parseValue(String valueString, int lineNumber) throws InvalidDataException {
        try {
            if (valueString.indexOf(".") >= 0) {
                return new Double(valueString);
            } else {
                return new Long(valueString);
            }

        } catch (NumberFormatException nfe) {
            throw new InvalidDataException("Invalid data value: [" + valueString + "], line " + lineNumber, nfe);
        }
    }

    /* ========================================================================
     *
     * Public methods
     */

    /* ========================================================================
     *
     * Protected / package-private methods
     */

    /* ========================================================================
     *
     * Private methods
     */
}
