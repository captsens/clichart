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
 * A parser that uses a numeric value for the X axis.  The x value is passed as an Integer or Double, as for y values.
 *
 * <p>Note that this parser can also be used when there is no x value.
 *
 * @author johnd
 */
public class ValueDataParser extends AbstractDataParser {

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

    public ValueDataParser(LineParser lineParser, int xColumnIndex, int[] yColumnIndexes, boolean hasHeader,
             boolean ignoreMissingValues, boolean ignoreEmptyValues, DataSink sink) {
        super(lineParser, xColumnIndex, yColumnIndexes, hasHeader,  ignoreMissingValues, ignoreEmptyValues, sink);
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
        return ParseUtils.parseValue(xValue, lineNumber);
    }

    /* ========================================================================
     *
     * Private methods
     */
}
