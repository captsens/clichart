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

import java.util.List;

import org.apache.commons.cli.CommandLine;

import net.sf.clichart.chart.ColourOverride;
import net.sf.clichart.chart.ColourOverrideOptionParser;
import net.sf.clichart.chart.Options;

/**
 * Options implementation for options provided on the command-line, and parsed using commons CLI
 *
 * @author johnd
 */
public class CliOptions implements Options {

    /* ========================================================================
     *
     * Class (static) variables.
     */

    private static final int DEFAULT_INT_VALUE = -1;

    /* ========================================================================
     *
     * Instance variables.
     */

    private final CommandLine m_commandLine;

    // attributes for all options that must be specially-parsed, typically because we need to validate the values

    // indexes for columns required from data (with default).  0-based
    private int[] m_columnIndexes = null;
    private int[] m_secondAxisColumnIndexes = null;

    private Integer m_maxYValue;
    private Integer m_minYValue;
    private Integer m_secondAxisMaxYValue;
    private Integer m_secondAxisMinYValue;

    private int m_width = DEFAULT_INT_VALUE;
    private int m_height = DEFAULT_INT_VALUE;

    private int m_lineWeight = DEFAULT_INT_VALUE;
    private int m_secondAxisLineWeight = DEFAULT_INT_VALUE;

    private String[] m_seriesTitles = null;
    private String[] m_secondAxisSeriesTitles = null;
    
    private List<ColourOverride> m_colourOverrides = null;
    
    private int m_listenPort = -1;

    /* ========================================================================
     *
     * Constructors
     */

    public CliOptions(CommandLine commandLine) throws InvalidOptionsException, ShowUsageException {
        m_commandLine = commandLine;

        if (m_commandLine.hasOption("h")) {
            throw new ShowUsageException();
        }

        if (m_commandLine.getArgs().length > 1) {
            throw new InvalidOptionsException("Can only include 1 input file path");
        }

        // parse the options taking values
        parseColumnIndexes();
        m_maxYValue = parseIntegerValue("m");
        m_minYValue = parseIntegerValue("miny");
        m_secondAxisMaxYValue = parseIntegerValue("maxy2");
        m_secondAxisMinYValue = parseIntegerValue("miny2");
        m_width = parseIntValue("w", DEFAULT_WIDTH);
        m_height = parseIntValue("g", DEFAULT_HEIGHT);
        m_lineWeight = parseLineWeight("lineweight");
        m_secondAxisLineWeight = parseLineWeight("lineweight2");
        parseSeriesTitles();
        m_colourOverrides = parseColourOverrides();
        m_listenPort = parseIntValue("port", -1);
    }


    /* ========================================================================
     *
     * Static methods
     */

    /* ========================================================================
     *
     * Public methods
     */

    public int getDataSeparatorType() {
        return (m_commandLine.hasOption("c") ? DATA_SEP_CSV : DEFAULT_DATA_SEP);
    }

    public String getDateFormat() {
        return m_commandLine.getOptionValue("d", DEFAULT_DATE_FORMAT);
    }

    public boolean hasHeaderRow() {
        return m_commandLine.hasOption("f");
    }

    public boolean ignoreMissingColumns() {
        return m_commandLine.hasOption("i");
    }

    public boolean ignoreEmptyColumns() {
        return m_commandLine.hasOption("ignoreempty");
    }

    public boolean ignoreDuplicateValues() {
        return m_commandLine.hasOption("p");
    }

    public int[] getColumnIndexes() {
        return m_columnIndexes;
    }

    public int[] getSecondAxisColumnIndexes() {
        return m_secondAxisColumnIndexes;
    }

    public int getChartXType() {
        if (m_commandLine.hasOption("n")) {
            return X_TYPE_NONE;
        } else if (m_commandLine.hasOption("v")) {
            return X_TYPE_VALUE;
        } else {
            return DEFAULT_X_TYPE;
        }
    }

    public String getFileOutputPath() {
        return m_commandLine.getOptionValue("o", null);
    }

    public String getChartTitle() {
        return m_commandLine.getOptionValue("t", null);
    }

    public String getChartXAxisTitle() {
        return m_commandLine.getOptionValue("x", null);
    }

    public String getChartYAxisTitle() {
        return m_commandLine.getOptionValue("y", null);
    }

    public String getSecondAxisChartYAxisTitle() {
        return m_commandLine.getOptionValue("ytitle2", null);
    }

    public Integer getMaxYValue() {
        return m_maxYValue;
    }

    public Integer getMinYValue() {
        return m_minYValue;
    }

    public boolean forceYRange() {
        return m_commandLine.hasOption("forceyrange");
    }

    public Integer getSecondAxisMaxYValue() {
        return m_secondAxisMaxYValue;
    }

    public Integer getSecondAxisMinYValue() {
        return m_secondAxisMinYValue;
    }

    public boolean forceSecondAxisYRange() {
        return m_commandLine.hasOption("forceyrange2");
    }

    public String getInputPath() {
        if (m_commandLine.getArgs().length == 1) {
            return m_commandLine.getArgs()[0];
        }
        return null;
    }

    public int getChartWidth() {
        return m_width;
    }

    public int getChartHeight() {
        return m_height;
    }

    public boolean isBarChart() {
        return m_commandLine.hasOption("b");
    }

    public boolean isSecondAxisBarChart() {
        return m_commandLine.hasOption("bar2");
    }

    public int getLineWeight() {
        return m_lineWeight;
    }

    public int getSecondAxisLineWeight() {
        return m_secondAxisLineWeight;
    }

    public boolean hasDataPoints() {
        return m_commandLine.hasOption("datapoints");
    }

    public boolean hasSecondAxisDataPoints() {
        return m_commandLine.hasOption("datapoints2");
    }

    public boolean isCliServer() {
        return m_commandLine.hasOption("cliserver");
    }

    public String[] getSeriesTitles() {
        return m_seriesTitles;
    }

    public String[] getSecondAxisSeriesTitles() {
        return m_secondAxisSeriesTitles;
    }

	public List<ColourOverride> getColourOverrides() {
		return m_colourOverrides;
	}
    
    public int getListenPort() {
        return m_listenPort;
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
     * NOTE: All 0-based now
     */
    private void parseColumnIndexes() throws InvalidOptionsException {
        m_columnIndexes = parseColumnIndexesImpl("l", DEFAULT_COLUMN_INDEXES);
        m_secondAxisColumnIndexes = parseColumnIndexesImpl("columnlist2", null);
    }

    private int[] parseColumnIndexesImpl(String optionName, int[] defaultIndexes) throws InvalidOptionsException {
        String indexesString = m_commandLine.getOptionValue(optionName, null);

        if (indexesString != null) {
            String[] indexArray = indexesString.split(",");
            int[] columnIndexes = new int[indexArray.length];

            for (int i = 0; i < indexArray.length; i++) {
                try {
                    columnIndexes[i] = Integer.parseInt(indexArray[i]);

                } catch (NumberFormatException e) {
                    throw new InvalidOptionsException("Invalid column index: " + indexArray[i]);
                }
            }
            return columnIndexes;
        }

        return defaultIndexes;
    }

    private int parseIntValue(String optionName, int defaultValue) throws InvalidOptionsException {
        Integer value = parseIntegerValue(optionName);

        if (value == null) {
            return defaultValue;
        }
        return value.intValue();
    }

    private Integer parseIntegerValue(String optionName) throws InvalidOptionsException {
        String value = m_commandLine.getOptionValue(optionName, null);
        if (value == null) {
            return null;
        }
        try {
            return new Integer(value);
        } catch (NumberFormatException e) {
            throw new InvalidOptionsException("Invalid " + optionName + " value: " + value);
        }
    }

    private int parseLineWeight(String optionName) throws InvalidOptionsException {
        int lineWeight = parseIntValue(optionName, DEFAULT_INT_VALUE);

        if (lineWeight != DEFAULT_INT_VALUE && (lineWeight < 1 || lineWeight > 5)) {
            throw new InvalidOptionsException("Invalid " + optionName + " value (must be 1 - 5): " + lineWeight);
        }
        return lineWeight;
    }

    private void parseSeriesTitles() {
        m_seriesTitles = parseSeriesTitlesImpl("seriestitles");
        m_secondAxisSeriesTitles = parseSeriesTitlesImpl("seriestitles2");
    }

    private String[] parseSeriesTitlesImpl(String optionName) {
        String value = m_commandLine.getOptionValue(optionName, null);
        if (value != null) {
            return value.split("\\s*?,\\s*?");
        }
        return null;
    }

	private List<ColourOverride> parseColourOverrides() throws InvalidOptionsException {
		if (m_commandLine.hasOption("colours")) {
			return new ColourOverrideOptionParser().parseOption(m_commandLine.getOptionValue("colours"));
		}
		return null;
	}



}
