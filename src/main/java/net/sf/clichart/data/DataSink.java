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

/**
 * Interface for objects that can received events containing data parsed from some source
 *
 * @author johnd
 */
public interface DataSink {

    /* ========================================================================
     *
     * Interface (static) variables.
     */

    /* ========================================================================
     *
     * Public methods
     */

    /**
     * Provides a column header for tabular data that has been parsed from some source
     *
     * @param headers  String headers for each column of interest (elements may be an empty string, but not null).
     *      Note that there should be as many headers as there are yValues in dataParsed()
     */
    public void headerParsed(String[] headers) throws InvalidDataException;

    /**
     * Provides the data from a line of tabular data that has been parsed from some source.  Values are represented as
     * (hopefully) useful objects, e.g. Long, Integer, Double, Date
     *
     * @param xValue The X axis value for the data (often a date/time).
     * @param yValues An array of size (headers.length - 1)
     * @param lineNumber The 1-based line number for this line in the data source
     */
    public void dataParsed(Object xValue, Object[] yValues, int lineNumber) throws InvalidDataException;

    /**
     * Advice that parsing of the source has been completed
     */
    public void parsingFinished();

}
