/* (C) Copyright 2006-2009, by John Dickson
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

package net.sf.clichart.main;

import net.sf.clichart.chart.Options;
import net.sf.clichart.chart.ChartSaverException;
import net.sf.clichart.chart.ChartSaver;
import net.sf.clichart.chart.AbstractChartBuilder;
import net.sf.clichart.chart.XYChartBuilder;
import net.sf.clichart.chart.TimeSeriesChartBuilder;
import net.sf.clichart.data.InvalidDataException;
import net.sf.clichart.data.AbstractDataParser;
import net.sf.clichart.data.LineParser;
import net.sf.clichart.data.CsvLineParser;
import net.sf.clichart.data.WhiteSpaceLineParser;
import net.sf.clichart.data.ValueDataParser;
import net.sf.clichart.data.TimeDataParser;

import java.io.IOException;
import java.io.File;
import java.io.Reader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import org.jfree.chart.JFreeChart;

import javax.swing.*;

/**
 * Default implementation of the ChartGenerator
 *
 * @author johnd
 */
public class DefaultChartGenerator implements ChartGenerator {

    /* ========================================================================
     *
     * Class (static) variables.
     */

    /* ========================================================================
     *
     * Instance variables.
     */

    /** Used to parse the input data */
    private AbstractDataParser m_dataParser;

    /** The main chart builder */
    private AbstractChartBuilder m_chartBuilder;

    /** Chart builder used for the second axis (if any) */
    private AbstractChartBuilder m_secondAxisBuilder = null;

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

    public void generateChart(Options options) throws IOException, InvalidDataException, InvalidOptionsException,
            ChartSaverException {
        JFreeChart chart = buildChart(options);

        if (options.getFileOutputPath() != null) {
            createChartSaver(chart, options).saveChart(new File(options.getFileOutputPath()));
        } else {
            JFrame frame = createChartFrame(chart, options);
            frame.setVisible(true);
        }
    }

    public void clear() {
        m_dataParser = null;
        m_chartBuilder = null;
        m_secondAxisBuilder = null;
    }

    /* ========================================================================
     *
     * Protected / package-private methods
     */

    /**
     * Return a ChartSaver for saving the chart to a file
     */
    protected ChartSaver createChartSaver(JFreeChart chart, Options options) {
        return new ChartSaver(chart, options.getChartWidth(), options.getChartHeight());
    }

    /**
     * Return a frame displaying the supplied chart
     */
    protected JFrame createChartFrame(JFreeChart chart, Options options) {
        ChartFrame frame = new ChartFrame();
        frame.setInitialWidth(options.getChartWidth());
        frame.setInitialHeight(options.getChartHeight());
        frame.setChart(chart);
        return frame;
    }

    /**
     * Create and return an appropriate chart builder, based on the options provided
     *
     * @param seriesTitles Explicit series titles to use (null if none)
     */
    protected AbstractChartBuilder buildChartBuilder(Options options, String[] seriesTitles) {
        if (options.getChartXType() == Options.X_TYPE_NONE || options.getChartXType() == Options.X_TYPE_VALUE) {
            return new XYChartBuilder(options, seriesTitles);
        } else {
            return new TimeSeriesChartBuilder(options, seriesTitles);
        }
    }

    /**
     * Create and return an appropriate data parser, based on the options provided.  Note that the chartBuilder must
     * be plugged into the data parser as its data sink.
     */
    protected AbstractDataParser buildDataParser(Options options, AbstractChartBuilder chartbuider) {
        LineParser lineParser = null;
        if (options.getDataSeparatorType() == Options.DATA_SEP_CSV) {
            lineParser = new CsvLineParser();
        } else {
            lineParser = new WhiteSpaceLineParser();
        }

        if (options.getChartXType() == Options.X_TYPE_NONE) {
            // no x values
            int[] dataColumnIndexes = getYColumnIndexes(options, 0);
            return new ValueDataParser(lineParser, -1, dataColumnIndexes, options.hasHeaderRow(),
                    options.ignoreMissingColumns(), options.ignoreEmptyColumns(), chartbuider);

        } else if (options.getChartXType() == Options.X_TYPE_VALUE) {
            int xColumnIndex = options.getColumnIndexes()[0];
            int[] dataColumnIndexes = getYColumnIndexes(options, 1);

            return new ValueDataParser(lineParser, xColumnIndex, dataColumnIndexes, options.hasHeaderRow(),
                    options.ignoreMissingColumns(), options.ignoreEmptyColumns(), chartbuider);

        } else {
            int xColumnIndex = options.getColumnIndexes()[0];
            int[] dataColumnIndexes = getYColumnIndexes(options, 1);

            return new TimeDataParser(lineParser, options.getDateFormat(), xColumnIndex, dataColumnIndexes,
                    options.hasHeaderRow(), options.ignoreMissingColumns(), options.ignoreEmptyColumns(), chartbuider);
        }
    }

    /* ========================================================================
     *
     * Private methods
     */

    private JFreeChart buildChart(Options options) throws IOException, InvalidDataException, InvalidOptionsException {
        m_chartBuilder = buildChartBuilder(options, options.getSeriesTitles());
        m_dataParser = buildDataParser(options, m_chartBuilder);

        if (options.getSecondAxisColumnIndexes() != null) {
            // need to use the same type as the main chartBuilder
            m_secondAxisBuilder = buildChartBuilder(options, options.getSecondAxisSeriesTitles());
            m_dataParser.addSecondAxis(options.getSecondAxisColumnIndexes(), m_secondAxisBuilder);
        }

        m_dataParser.parse(buildInputReader(options));
        JFreeChart chart = m_chartBuilder.getChart(options);

        if (m_secondAxisBuilder != null) {
            m_secondAxisBuilder.addSecondAxis(chart, options);
        }
        return chart;
    }


    private int[] getYColumnIndexes(Options options, int startIndex) {
        int[] dataColumnIndexes = new int[options.getColumnIndexes().length - startIndex];
        for (int i = 0; i < dataColumnIndexes.length; i++) {
            dataColumnIndexes[i] = options.getColumnIndexes()[i + startIndex];
        }
        return dataColumnIndexes;
    }

    /**
     * Note that the file is never closed, so this is only appropriate when parsing 1 file then exiting
     */
    private Reader buildInputReader(Options options) throws InvalidOptionsException {
        String inputPath = options.getInputPath();

        if (inputPath != null) {
            try {
                return new FileReader(inputPath);
            } catch (FileNotFoundException e) {
                throw new InvalidOptionsException("File not found: " + inputPath);
            }
        } else {
            //System.err.println("Reading from stdin");
            return new InputStreamReader(System.in);
        }
    }
}
