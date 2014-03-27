/* (C) Copyright 2006-2012, by John Dickson
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

import net.sf.clichart.data.DataSink;
import net.sf.clichart.data.InvalidDataException;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.ClusteredXYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYDataset;

import java.awt.*;

/**
 * Base class for objects that can build and return a chart based on a data series provided by the DataSink interface.
 *
 * Note that these objects can also be used to add a second axis to an existing chart, so when adding a second axis,
 * should create two of the same subclass (one for each axis), call getChart() on the first, and addSecondAxis() on
 * the second
 *
 * WARNING: Not threadsafe
 *
 * @author johnd
 */
public abstract class AbstractChartBuilder implements DataSink {

    /* ========================================================================
     *
     * Class (static) variables.
     */

    /* ========================================================================
     *
     * Instance variables.
     */

    private ColumnParseState m_columnParseState = new ColumnParseState();

    protected final Options m_options;

    protected final String[] m_seriesTitles;

    /* ========================================================================
     *
     * Constructors
     */

    protected AbstractChartBuilder(Options options, String[] seriesTitles) {
        m_options = options;
        m_seriesTitles = seriesTitles;
    }

    /* ========================================================================
     *
     * Static methods
     */

    /* ========================================================================
     *
     * Public methods
     */

    // ------------------------------------------------------------------------
    // Methods from DataSink interface
    // ------------------------------------------------------------------------
    public void headerParsed(String[] headers) throws InvalidDataException {
        initialiseColumns(headers);
        m_columnParseState.setColumnsInitialised(true);
        m_columnParseState.setNumHeaders(headers.length);
    }

    public void dataParsed(Object xValue, Object[] yValues, int lineNumber) throws InvalidDataException {
        if (!m_columnParseState.isColumnsInitialised()) {
            initialiseColumns(new String[yValues.length]);
            m_columnParseState.setColumnsInitialised(true);
        }

        for (int i = 0; i < yValues.length; i++) {
            if (m_columnParseState.getNumHeaders() <= 0 || i < m_columnParseState.getNumHeaders()) {
                addYValue(i, xValue, yValues[i]);

            } else if (yValues[i] == null) {
                // Indicates that data was missing - do nothing
                
            } else {
                // invalid column number
                if (!m_columnParseState.isExcessColumnsAdvised()) {
                    System.err.println("Invalid line data found on line " + lineNumber
                            + " - more columns than headers.  Ignoring excess columns.  "
                            + "No more warnings will be given");
                    m_columnParseState.setExcessColumnsAdvised(true);
                }
            }
        }
    }

    public void parsingFinished() {
        // don't really need to know?
    }

    // ------------------------------------------------------------------------
    // Method to create and return chart
    // ------------------------------------------------------------------------
    /**
     * Must be called *after* all parsing is finished
     * @return the chart ready to be embellished and displayed
     */
    public JFreeChart getChart(Options options) {
        assert m_columnParseState.isColumnsInitialised() : "Cannot get chart - no data provided yet";
        JFreeChart chart = getChartImpl(options);
        setOptions(chart, options);

        return chart;
    }

    /**
     * Add the data set as the second axis to this chart
     */
    public void addSecondAxis(JFreeChart chart, Options options) {
        XYPlot plot = chart.getXYPlot();
        plot.setDataset(1, getDataset());
        plot.mapDatasetToRangeAxis(1, 1);

        NumberAxis rangeAxis2 = new NumberAxis(options.getSecondAxisChartYAxisTitle());
        plot.setRangeAxis(1, rangeAxis2);

        // need a separate renderer for the axis, otherwise the same colours are used as for the first axis series
        setAxisRenderer(chart.getXYPlot(), 1, options.isSecondAxisBarChart(), options.hasSecondAxisDataPoints(),
                options.getSecondAxisLineWeight());

        setAxisLimits(rangeAxis2, options.getSecondAxisMinYValue(), options.getSecondAxisMaxYValue(),
                options.forceSecondAxisYRange());

        rangeAxis2.setAutoRangeIncludesZero(false);
    }


    // ------------------------------------------------------------------------
    // Setters and getters
    // ------------------------------------------------------------------------

    /* ========================================================================
     *
     * Protected / package-private methods
     */

    /**
     * Called for subclass to create and register the appropriate data series for the column with this title
     * @param title
     */
    protected abstract void initialiseSeriesColumn(String title);

    /**
     * Called for subclass to add this yValue to the appropriate series
     * @param columnNumber The 0-based index into the dataColumnIndexes array
     * @param xValue The x axis value for this row
     * @param yValue The y axis value for this row and column number
     */
    protected abstract void addYValue(int columnNumber, Object xValue, Object yValue);

    /**
     * Called for subclass to create the required chart object, containing all the data provided
     *
     * @return the chart ready to be embellished and displayed
     */
    protected abstract JFreeChart getChartImpl(Options options);

    /**
     * Return the dataset containing all the data provided - used to add the second axis
     */
    protected abstract XYDataset getDataset();

    /**
     * Return an appropriate tooltip generator for a new renderer
     */
    protected abstract XYToolTipGenerator getToolTipGenerator();


    /* ========================================================================
     *
     * Private methods
     */

    private void initialiseColumns(String[] headers) {
        for (int i = 0; i < headers.length; i++) {
            String header = "Series " + (i + 1);

            if (m_seriesTitles != null && m_seriesTitles.length > i) {
                header = m_seriesTitles[i];
            } else if (headers[i] != null) {
                header = headers[i];
            }

            initialiseSeriesColumn(header);
        }
    }

    private void setOptions(JFreeChart chart, Options options) {
        setAxisLimits(chart.getXYPlot().getRangeAxis(), options.getMinYValue(), options.getMaxYValue(),
                options.forceYRange());

        setAxisRenderer(chart.getXYPlot(), 0, options.isBarChart(), options.hasDataPoints(), options.getLineWeight());
        
        if (options.getColourOverrides() != null) {
	        Plot plot = chart.getPlot();
	        DrawingSupplierWrapper drawingSupplierWrapper = new DrawingSupplierWrapper();
	        drawingSupplierWrapper.addColourOverrides(options.getColourOverrides());
	        plot.setDrawingSupplier(drawingSupplierWrapper);
        }
    }

    private void setAxisRenderer(XYPlot plot, int axisIndex, boolean isBarChart, boolean hasDataPoints,
            int lineWeight) {
        XYItemRenderer renderer;
        if (isBarChart) {
            renderer = new ClusteredXYBarRenderer();
            XYDataset axisDataset = plot.getDataset(axisIndex);
            plot.setDataset(axisIndex, new XYBarDataset(axisDataset, calculateBarWidth(axisDataset, lineWeight)));

        } else {
            renderer = new XYLineAndShapeRenderer(true, hasDataPoints);

            if (lineWeight >= 0 && lineWeight <= 5) {
                for (int seriesIndex = 0; seriesIndex < plot.getDataset(axisIndex).getSeriesCount(); seriesIndex++) {
                    renderer.setSeriesStroke(seriesIndex, new BasicStroke((float)lineWeight));
                }
            }
        }
        renderer.setBaseToolTipGenerator(getToolTipGenerator());
        plot.setRenderer(axisIndex, renderer);
    }

    private double calculateBarWidth(XYDataset axisDataset, int lineWeight) {
        double minimumInterval = -1;
        for (int seriesIndex = 0; seriesIndex < axisDataset.getSeriesCount(); seriesIndex++) {
            for (int itemIndex = 1; itemIndex < axisDataset.getItemCount(seriesIndex); itemIndex++) {
                double interval = axisDataset.getXValue(seriesIndex, itemIndex)
                        - axisDataset.getXValue(seriesIndex, itemIndex - 1);
                if (itemIndex == 1) {
                    minimumInterval = interval;
                } else {
                    minimumInterval = Math.min(minimumInterval, interval);
                }
            }
        }

        double barWeight = 0.5;
        if (lineWeight >= 0 && lineWeight <= 5) {
            barWeight = 0.2 * lineWeight;
        }

        return minimumInterval * barWeight;
    }

    private void setAxisLimits(ValueAxis axis, Integer minimum, Integer maximum, boolean forceRange) {
        boolean requiresMaximum = maximum != null && (forceRange || maximum < axis.getUpperBound());
        if (requiresMaximum) {
            axis.setUpperBound(maximum);
        }
        boolean requiresMinimum = minimum != null && (forceRange || minimum > axis.getLowerBound());
        if (requiresMinimum) {
            axis.setLowerBound(minimum);
        }
    }

}
