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

package net.sf.clichart.main;

import net.sf.clichart.chart.Options;
import net.sf.clichart.chart.ChartSaverException;
import net.sf.clichart.data.InvalidDataException;

import java.io.IOException;

/**
 * Interface for a stateful object that can generate a chart when asked
 *
 * @author johnd
 */
public interface ChartGenerator {

    /* ========================================================================
     *
     * Interface (static) variables.
     */

    /* ========================================================================
     *
     * Public methods
     */

    /**
     * Parse the provided data (from the supplied input file within the options, or from stdin if null), and either
     * display a chart in an interactive window or save it to a file (depending on the present of an output path in the
     * options).
     *
     * @param options  Options used for parsing data and generating chart
     * @throws java.io.IOException If not able to read input data or write chart
     * @throws net.sf.clichart.data.InvalidDataException Input data was invalid (or didn't match the options provided)
     * @throws InvalidOptionsException
     * @throws net.sf.clichart.chart.ChartSaverException
     */
    public void generateChart(Options options)
            throws IOException, InvalidDataException, InvalidOptionsException, ChartSaverException;

    /**
     * Clear all set options.
     */
    public void clear();
}
