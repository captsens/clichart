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

package net.sf.clichart.chart;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.general.SeriesException;

/**
 * Builder for charts with a numeric X axis
 *
 * @author johnd
 */
public class XYChartBuilder extends AbstractChartBuilder {

    /* ========================================================================
     *
     * Class (static) variables.
     */

    /* ========================================================================
     *
     * Instance variables.
     */

    // holds all series which in turn hold the data for each column
    private XYSeriesCollection m_seriesCollection = new XYSeriesCollection();


    /* ========================================================================
     *
     * Constructors
     */

    public XYChartBuilder(Options options, String[] seriesTitles) {
        super(options, seriesTitles);
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

    protected void initialiseSeriesColumn(String title) {
        XYSeries series = new XYSeries(title);
        m_seriesCollection.addSeries(series);
    }

    protected void addYValue(int columnNumber, Object xValue, Object yValue) {
        XYSeries series = m_seriesCollection.getSeries(columnNumber);

        try {
            series.add((Number)xValue, (Number) yValue);
        } catch (SeriesException e) {
            if (!m_options.ignoreDuplicateValues()) {
                throw e;
            }
//            System.err.println("Ignoring duplicate X axis value: " + xValue);
        }
    }

    protected JFreeChart getChartImpl(Options options) {
        return ChartFactory.createXYLineChart(options.getChartTitle(), options.getChartXAxisTitle(),
                options.getChartYAxisTitle(), m_seriesCollection, PlotOrientation.VERTICAL, true, true, false);
    }

    protected XYDataset getDataset() {
        return m_seriesCollection;
    }

    protected XYToolTipGenerator getToolTipGenerator() {
        return new StandardXYToolTipGenerator();
    }

    /* ========================================================================
     *
     * Private methods
     */
}
