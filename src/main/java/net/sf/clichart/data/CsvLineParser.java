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

import com.csvreader.CsvReader;

import java.io.StringReader;
import java.io.IOException;

/**
 * A line parser for CSV data.  Should cope with: <ul>
 * <li> Ordinary CSV lines
 * <li> Quoting of values in double quotes (") where there might be a comma in the value
 * <li> Quoting of double quotes by repeating ("")
 * </ul>
 *
 * <p>Uses the CSV parsing library from javacsv on SourceForge (LGPL).  It's a bit inefficient, since that parser
 * expects to have full control of the stream (rather than doing it line-by-line, but it was very efficient in
 * development time :)
 *
 * @author johnd
 */
public class CsvLineParser implements LineParser {

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

    /* ========================================================================
     *
     * Public methods
     */

    public String[] parseLine(String line, int lineNumber) throws InvalidDataException {
        CsvReader csvParser = new CsvReader(new StringReader(line));
        try {
            csvParser.readRecord();
            return csvParser.getValues();
        } catch (IOException e) {
            throw new InvalidDataException("Failed to parse line number " + lineNumber + " as CSV: " + e.toString());
        }
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
