/* (C) Copyright 2006-2012, by John Dickson
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

/**
 * Defines all options available via the CLI.  Used both for normal CLI operation, and interactive (CLI server) mode.
 *
 * @author johnd
 */
public class CliOptionDefinitions {

    /* ========================================================================
     *
     * Class (static) variables.
     */

    // Options argument types
    public static final String STRING = "S";
    public static final String INT = "I";
    public static final String INTEGER = "N";
    public static final String INT_ARRAY = "A";
    public static final String BOOLEAN = "B";
    public static final String STRING_ARRAY = "SA";
    public static final String COLOUR_INDEX_ARRAY = "CA";

    // metadata for options.
    // Each option element is {shortName, longName, description, OptionsBean setter name, Options argument type}
    // Setter name and type are null if it must be handled specially, while type is null if none is required
    public static final String[][] OPTIONS_WITH_ARGS = {
        {"d", "dateformat", "Format of date/time, in SimpleDateFormat notation. Defaults to 'HH:mm'", "setDateFormat",
            STRING},
        {"l", "columnlist", "List of columns, comma-separated, 0-based.  X axis value (if any) must be first.  " +
            "Defaults to '0,1'", "setColumnIndexes", INT_ARRAY},
        {"o", "outputpath", "Output chart to the given path (otherwise shows in a window), must be JPG or PNG",
            "setFileOutputPath", STRING},
        {"t", "title", "Title for the chart", "setChartTitle", STRING},
        {"x", "xtitle", "Title for the X axis", "setChartXAxisTitle", STRING},
        {"y", "ytitle", "Title for the Y axis", "setChartYAxisTitle", STRING},
        {"m", "maxy", "Maximum value for y axis", "setMaxYValue", INTEGER},
        {null, "miny", "Minimum value for y axis", "setMinYValue", INTEGER},
        {"w", "width", "Chart width in pixels (defaults to 800)", "setChartWidth", INT},
        {"g", "height", "Chart height in pixels (defaults to 600)", "setChartHeight", INT},
        {null, "columnlist2", "List of columns for second y axis (if any), comma-separated, 0-based.",
            "setSecondAxisColumnIndexes", INT_ARRAY},
        {null, "ytitle2", "Title for the second Y axis (if any)", "setSecondAxisChartYAxisTitle", STRING},
        {null, "maxy2", "Maximum value for second y axis (if any)", "setSecondAxisMaxYValue", INTEGER},
        {null, "miny2", "Minimum value for second y axis (if any)", "setSecondAxisMinYValue", INTEGER},
        {null, "lineweight", "Line weight (values are 1 - 5)", "setLineWeight", INT},
        {null, "lineweight2", "Line weight for the second Y axis (values are 1 - 5)", "setSecondAxisLineWeight", INT},
        {null, "seriestitles", "Data series titles, comma-separated. Interpreted in same order as " +
            "Y axis values in the column list", "setSeriesTitles", STRING_ARRAY},
        {null, "seriestitles2", "Second axis data series titles, comma-separated. " +
            "Interpreted in same order as second axis column list", "setSecondAxisSeriesTitles", STRING_ARRAY},
        {null, "colours", "Override default chart colours.  Consists of a comma-" +
            "separated list of 'index:colour', where 'index' is the 0-based series index, and 'colour' is " +
            "'red', 'blue', 'green' etc (see documentation).  Indexes not overridden use default colours", 
            "setColours", COLOUR_INDEX_ARRAY},
        {null, "port", "Port on which server should listen (only if TCP/IP server required)", "setListenPort", INT},
    };

    public static final String[][] OPTIONS_WITHOUT_ARGS = {
        {"b", "bar", "Show as a bar chart, not X-Y line", "setBarChart", BOOLEAN},
        {"c", "csv", "Expect input as CSV.  Default is whitespace-separated", null, null},
        {"f", "hasheader", "First row of data provides column headers for the legend (default is no header row)",
            "setHeaderRow", BOOLEAN},
        {"h", "help", "Show usage (this screen) and exit", null, null},
        {"i", "ignoremissing", "Ignore missing columns (default is to terminate)", "setIgnoreMissingColumns", BOOLEAN},
        {null, "ignoreempty", "Ignore empty column values (default is to terminate)", "setIgnoreEmptyColumns", BOOLEAN},
        {"p", "ignoredup", "Ignore duplicate X axis values (default is to terminate)", "setIgnoreDuplicateValues",
            BOOLEAN},
        {"n", "noxvalue", "Chart has no X axis values - just number the rows instead", null, null},
        {"v", "xvalue", "Chart has simple values as the X axis, not dates or times", null, null},
        {null, "bar2", "Show second axis as a bar chart, not X-Y line", "setSecondAxisBarChart", BOOLEAN},
        {null, "datapoints", "Indicate each data point", "setDataPoints", BOOLEAN},
        {null, "datapoints2", "Indicate each data point for the second Y axis", "setSecondAxisDataPoints", BOOLEAN},
        {null, "forceyrange", "Force the y axis to use the limits (minimum/maximum) provided.  " +
            "Default is to use limits only if chart values would exceed them", "forceYRange", BOOLEAN},
        {null, "forceyrange2", "Force the second y axis (if any) to use the limits (minimum/maximum) provided.  " +
            "Default is to use limits only if chart values would exceed them", "forceYRange", BOOLEAN},
        {null, "cliserver", "Run the program as a CLI server, reading all commands from standard in", null, null},
    };


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

    /* ========================================================================
     *
     * Protected / package-private methods
     */

    /* ========================================================================
     *
     * Private methods
     */
}
