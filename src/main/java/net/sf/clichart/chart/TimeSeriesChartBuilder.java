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

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.Second;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.general.SeriesException;

import java.util.Date;

/**
 * Builder for charts displaying time series (dates, times etc.)
 *
 * @author johnd
 */
public class TimeSeriesChartBuilder extends AbstractChartBuilder {

    /* ========================================================================
     *
     * Class (static) variables.
     */

    /* ========================================================================
     *
     * Instance variables.
     */

    // holds the time series which in turn hold the data for each column
    private TimeSeriesCollection m_timeSeriesCollection = new TimeSeriesCollection();


    /* ========================================================================
     *
     * Constructors
     */

    public TimeSeriesChartBuilder(Options options, String[] seriesTitles) {
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
        TimeSeries timeSeries = new TimeSeries(title, Second.class);
        m_timeSeriesCollection.addSeries(timeSeries);
    }

    protected void addYValue(int columnNumber, Object xValue, Object yValue) {
        Date date = (Date)xValue;
        Number value = (Number) yValue;

        TimeSeries timeSeries = m_timeSeriesCollection.getSeries(columnNumber);
        try {
            timeSeries.add(new Second(date), value);
        } catch (SeriesException e) {
            if (!m_options.ignoreDuplicateValues()) {
                throw e;
            }
//            System.err.println("Ignoring duplicate X axis value: " + xValue);
        }
    }

    protected JFreeChart getChartImpl(Options options) {
        return ChartFactory.createTimeSeriesChart(options.getChartTitle(), options.getChartXAxisTitle(),
                options.getChartYAxisTitle(), m_timeSeriesCollection, true, true, false);
    }

    protected XYDataset getDataset() {
        return m_timeSeriesCollection;
    }

    protected XYToolTipGenerator getToolTipGenerator() {
        return StandardXYToolTipGenerator.getTimeSeriesInstance();
    }

    /* ========================================================================
     *
     * Private methods
     */
}
