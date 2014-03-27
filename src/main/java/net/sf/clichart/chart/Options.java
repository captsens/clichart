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

import java.util.List;

/**
 * Interface for options container for CLI chart
 *
 * @author johnd
 */
public interface Options {

    /* ========================================================================
     *
     * Interface (static) variables.
     */

    // Defines for data separator/delimiter
    public static final int DATA_SEP_CSV = 0;
    public static final int DATA_SEP_WHITE_SPACE = 1;

    public static final int DEFAULT_DATA_SEP = DATA_SEP_WHITE_SPACE;

    // Defines for X axis type
    public static final int X_TYPE_DATE_TIME = 10;
    public static final int X_TYPE_VALUE = 11;
    public static final int X_TYPE_NONE = 12;

    public static final int DEFAULT_X_TYPE = X_TYPE_DATE_TIME;

    public static final String DEFAULT_DATE_FORMAT = "HH:mm";

    public static final int[] DEFAULT_COLUMN_INDEXES = {0, 1};

    public static final int DEFAULT_WIDTH = 800;
    public static final int DEFAULT_HEIGHT = 600;



    /* ========================================================================
     *
     * Public methods
     */

    /**
     * Returns the data separator (CSV)
     * @return one of the DATA_SEP_* defines
     */
    public int getDataSeparatorType();

    /**
     * Returns the date format string for parsing date/times
     */
    public String getDateFormat();

    /**
     * Return true if the first row of the data is a header (this will be used for column titles)
     */
    public boolean hasHeaderRow();

    /**
     * Return true if missing columns in the data should be ignored (with an appropriate message)
     */
    public boolean ignoreMissingColumns();

    /**
     * Return true if empty column values in the data should be ignored (with an appropriate message)
     */
    public boolean ignoreEmptyColumns();

    /**
     * Return true if exceptions for adding duplicate X axis values should be swallowed (with an appropriate message)
     */
    public boolean ignoreDuplicateValues();

    /**
     * Return the list of column indexes required from the data provided
     */
    public int[] getColumnIndexes();

    /**
     * Return the list of column indexes required for the second axis (if any) from the data provided, or null if none
     */
    public int[] getSecondAxisColumnIndexes();

    /**
     * Return the type of X axis - date/time, value or none
     * @return one of the X_TYPE_* defines
     */
    public int getChartXType();

    /**
     * Path to which chart should be saved - null if should show in UI
     */
    public String getFileOutputPath();

    /**
     * Return the title for the graph, or null if not set
     */
    public String getChartTitle();

    /**
     * Return the title for the X axis, or null if not set
     */
    public String getChartXAxisTitle();

    /**
     * Return the title for the y axis, or null if not set
     */
    public String getChartYAxisTitle();

    /**
     * Return the title for the second y axis, or null if not set
     */
    public String getSecondAxisChartYAxisTitle();

    /**
     * Return the max Y value for the chart, or null if not set
     */
    public Integer getMaxYValue();

    /**
     * Return the min Y value for the chart, or null if not set
     */
    public Integer getMinYValue();

    /**
     * Return the max Y value for the second axis for the chart, or null if not set
     */
    public Integer getSecondAxisMaxYValue();

    /**
     * Return the min Y value for the second axis for the chart, or null if not set
     */
    public Integer getSecondAxisMinYValue();

    /**
     * If set, force use of the minimum and maximum Y values provided
     */
    public boolean forceYRange();

    /**
     * If set, force use of the minimum and maximum Y values provided for the second axis
     */
    public boolean forceSecondAxisYRange();

    /**
     * Return the input path to read data from, or null if should read from stdin
     */
    public String getInputPath();

    /**
     * Return the width of the chart in pixels
     */
    public int getChartWidth();

    /**
     * Return the height of the chart in pixels
     */
    public int getChartHeight();

    /**
     * Return true if the main axis should be a bar chart, rather than X-Y
     */
    public boolean isBarChart();

    /**
     * Return true if the second axis should be a bar chart, rather than X-Y
     */
    public boolean isSecondAxisBarChart();

    /**
     * Return required line weight for first axis, on a scale of 1 - 5
     */
    int getLineWeight();

    /**
     * Return required line weight for second axis, on a scale of 1 - 5
     */
    int getSecondAxisLineWeight();

    /**
     * Return true if line shapes should be rendered for the first axis
     */
    boolean hasDataPoints();

    /**
     * Return true if line shapes should be rendered for the second axis
     */
    boolean hasSecondAxisDataPoints();

    /**
     * Return true if clichart should act as a CLI server, to be driven by a script
     */
    public boolean isCliServer();
    
    /**
     * Return the port number on which the CLI server should listen.  If not greater than 0, 
     * functions as a true CLI server, i.e. listens on stdin and writes to stdout
     */
    public int getListenPort();

    /**
     * Return the titles for each of the first Y axis data series, or null if not set
     */
    public String[] getSeriesTitles();

    /**
     * Return the titles for each of the second Y axis data series, or null if not set
     */
    public String[] getSecondAxisSeriesTitles();

	/**
	 * Return any colour overrides set for columns (may be null)
	 */
    public List<ColourOverride> getColourOverrides();
}
