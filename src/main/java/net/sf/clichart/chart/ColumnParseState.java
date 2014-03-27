/* (C) Copyright 2007, by John Dickson
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

package net.sf.clichart.chart;

/**
 * A simple data holder to track the state of parsing column data for charts
 *
 * @author johnd
 */
public class ColumnParseState {

    /* ========================================================================
     *
     * Class (static) variables.
     */

    /* ========================================================================
     *
     * Instance variables.
     */

    // set when we've initialised the data columns
    private boolean m_columnsInitialised = false;

    private int m_numHeaders = -1;

    private boolean m_excessColumnsAdvised = false;


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

    public boolean isColumnsInitialised() {
        return m_columnsInitialised;
    }

    public void setColumnsInitialised(boolean columnsInitialised) {
        m_columnsInitialised = columnsInitialised;
    }

    public int getNumHeaders() {
        return m_numHeaders;
    }

    public void setNumHeaders(int numHeaders) {
        m_numHeaders = numHeaders;
    }

    public boolean isExcessColumnsAdvised() {
        return m_excessColumnsAdvised;
    }

    public void setExcessColumnsAdvised(boolean excessColumnsAdvised) {
        m_excessColumnsAdvised = excessColumnsAdvised;
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
