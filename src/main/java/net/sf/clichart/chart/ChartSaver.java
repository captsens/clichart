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
package net.sf.clichart.chart;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import java.io.File;
import java.io.IOException;

/**
 * A simple object that can save a chart as either a PNG or JPEG
 *
 * @author johnd
 */
public class ChartSaver {

    /* ========================================================================
     *
     * Class (static) variables.
     */

    static final int TYPE_PNG = 0;
    static final int TYPE_JPEG = 1;

    /* ========================================================================
     *
     * Instance variables.
     */

    private final JFreeChart m_chart;

    private final int m_width;
    private final int m_height;

    /* ========================================================================
     *
     * Constructors
     */

    public ChartSaver(JFreeChart chart, int width, int height) {
        m_chart = chart;
        m_width = width;
        m_height = height;
    }

    /* ========================================================================
     *
     * Static methods
     */

    /* ========================================================================
     *
     * Public methods
     */

    public void saveChart(File outputFile) throws ChartSaverException {
        int chartType = identifyChartType(outputFile);

        try {
            saveChart(chartType, outputFile);
        } catch (IOException e) {
            throw new ChartSaverException("Failed to save chart", e);
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

    /**
     * Package private for testing
     */
    int identifyChartType(File outputFile) throws ChartSaverException {
        String upperFileName = outputFile.getName().toUpperCase();

        if (upperFileName.endsWith(".PNG")) {
            return TYPE_PNG;
        } else if (upperFileName.endsWith(".JPG") || upperFileName.endsWith(".JPEG")) {
            return TYPE_JPEG;
        } else {
            throw new
                    ChartSaverException("Invalid filename. Supported formats are JPEG (*.jpg, *.jpeg) and PNG (*.png)");
        }
    }

    /**
     * Package private for testing
     */
    void saveChart(int chartType, File outputFile) throws IOException {
        if (chartType == TYPE_PNG) {
            ChartUtilities.saveChartAsPNG(new File(outputFile.getPath()), m_chart, m_width, m_height);
        } else {
            ChartUtilities.saveChartAsJPEG(new File(outputFile.getPath()), m_chart, m_width, m_height);
        }
    }

}
