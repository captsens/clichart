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

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;

/**
 * Unit test for ChartSaver
 *
 * @author johnd
 */
public class ChartSaverTest extends TestCase {

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

    public void testIdentifyChartType() throws Exception {
        ChartSaver saver = new ChartSaver(null, 0, 0);
        assertEquals(ChartSaver.TYPE_PNG, saver.identifyChartType(new File("abc.png")));
        assertEquals(ChartSaver.TYPE_PNG, saver.identifyChartType(new File("abc.PNG")));

        assertEquals(ChartSaver.TYPE_JPEG, saver.identifyChartType(new File("abc.jpg")));
        assertEquals(ChartSaver.TYPE_JPEG, saver.identifyChartType(new File("abc.JPG")));
        assertEquals(ChartSaver.TYPE_JPEG, saver.identifyChartType(new File("abc.jpeg")));
        assertEquals(ChartSaver.TYPE_JPEG, saver.identifyChartType(new File("abc.JPEG")));
        assertEquals(ChartSaver.TYPE_JPEG, saver.identifyChartType(new File("abc.jPeG")));

        try {
            saver.identifyChartType(new File("abc.gif"));
            fail("Invalid extension");
        } catch (ChartSaverException expected) {}
    }

    public void testSaveChart_Ok() throws Exception {
        final File outputPath = new File("a/b/c.png");
        ChartSaver saver = new ChartSaver(null, 0, 0) {
            public void saveChart(int chartType, File outputFile) throws IOException {
                assertSame(outputPath, outputFile);
                assertEquals(ChartSaver.TYPE_PNG, chartType);
            }
        };
        saver.saveChart(outputPath);
    }

    public void testSaveChart_Exception() throws Exception {
        final File outputPath = new File("a/b/c.jpeg");
        ChartSaver saver = new ChartSaver(null, 0, 0) {
            public void saveChart(int chartType, File outputFile) throws IOException {
                assertSame(outputPath, outputFile);
                assertEquals(ChartSaver.TYPE_JPEG, chartType);
                throw new IOException("Test exception");
            }
        };

        try {
            saver.saveChart(outputPath);
            fail();
        } catch (ChartSaverException expected) {}
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
