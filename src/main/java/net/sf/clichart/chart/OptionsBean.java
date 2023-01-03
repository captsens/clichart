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

package net.sf.clichart.chart;

import java.util.Arrays;
import java.util.List;

/**
 * A simple options Java bean, used for tests (?)
 *
 * @author johnd
 */
public class OptionsBean implements Options {

    /* ========================================================================
     *
     * Class (static) variables.
     */

    /* ========================================================================
     *
     * Instance variables.
     */

    private static int hashCode(Object[] array) {
		final int PRIME = 31;
		if (array == null)
			return 0;
		int result = 1;
		for (int index = 0; index < array.length; index++) {
			result = PRIME * result + (array[index] == null ? 0 : array[index].hashCode());
		}
		return result;
	}

	private static int hashCode(int[] array) {
		final int PRIME = 31;
		if (array == null)
			return 0;
		int result = 1;
		for (int index = 0; index < array.length; index++) {
			result = PRIME * result + array[index];
		}
		return result;
	}

	private int m_dataSeparatorType = Options.DATA_SEP_WHITE_SPACE;

    private String m_dateFormat = DEFAULT_DATE_FORMAT;

    private boolean m_headerRow = false;

    private boolean m_ignoreMissingColumns = false;
    private boolean m_ignoreEmptyColumns = false;
    private boolean m_ignoreDuplicateValues = false;

    private int[] m_columnIndexes = DEFAULT_COLUMN_INDEXES;
    private int[] m_secondAxisColumnIndexes = null;

    private int m_chartXType = DEFAULT_X_TYPE;

    private String m_fileOutputPath = null;

    private String m_chartTitle = null;
    private String m_chartXAxisTitle = null;
    private String m_chartYAxisTitle = null;
    private String m_secondAxisChartYAxisTitle = null;

    private Integer m_maxYValue = null;
    private Integer m_minYValue;
    private boolean m_forceYRange = false;

    private Integer m_secondAxisMaxYValue = null;
    private Integer m_secondAxisMinYValue = null;
    private boolean m_forceSecondAxisYRange = false;

    private String m_inputPath = null;

    private int m_width = DEFAULT_WIDTH;
    private int m_height = DEFAULT_HEIGHT;

    private boolean m_barChart = false;
    private boolean m_secondAxisBarChart = false;

    private int m_lineWeight = -1;
    private int m_secondAxisLineWeight = -1;

    private boolean m_dataPoints = false;
    private boolean m_secondAxisDataPoints = false;

    private String[] m_seriesTitles = null;
    private String[] m_secondAxisSeriesTitles = null;

    private boolean m_cliServer = false;

    private List<ColourOverride> m_colourOverrides = null;

    private int m_listenPort = -1;

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

    public int getDataSeparatorType() {
        return m_dataSeparatorType;
    }

    public void setDataSeparatorType(int dataSeparatorType) {
        m_dataSeparatorType = dataSeparatorType;
    }

    public String getDateFormat() {
        return m_dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        m_dateFormat = dateFormat;
    }

    public boolean hasHeaderRow() {
        return m_headerRow;
    }

    public void setHeaderRow(boolean hasHeaderRow) {
        m_headerRow = hasHeaderRow;
    }

    public boolean ignoreMissingColumns() {
        return m_ignoreMissingColumns;
    }

    public void setIgnoreMissingColumns(boolean ignoreMissingColumns) {
        m_ignoreMissingColumns = ignoreMissingColumns;
    }
    
    public boolean ignoreEmptyColumns() {
        return m_ignoreEmptyColumns;
    }
    
    public void setIgnoreEmptyColumns(boolean ignoreEmptyColumns) {
        m_ignoreEmptyColumns = ignoreEmptyColumns;
    }

    public boolean ignoreDuplicateValues() {
        return m_ignoreDuplicateValues;
    }

    public void setIgnoreDuplicateValues(boolean ignoreDuplicateValues) {
        m_ignoreDuplicateValues = ignoreDuplicateValues;
    }

    public int[] getColumnIndexes() {
        return m_columnIndexes;
    }

    public void setColumnIndexes(int[] columnIndexes) {
        m_columnIndexes = columnIndexes;
    }

    public int[] getSecondAxisColumnIndexes() {
        return m_secondAxisColumnIndexes;
    }

    public void setSecondAxisColumnIndexes(int[] secondAxisColumnIndexes) {
        m_secondAxisColumnIndexes = secondAxisColumnIndexes;
    }

    public int getChartXType() {
        return m_chartXType;
    }

    public void setChartXType(int chartXType) {
        m_chartXType = chartXType;
    }

    public String getFileOutputPath() {
        return m_fileOutputPath;
    }

    public void setFileOutputPath(String fileOutputPath) {
        m_fileOutputPath = fileOutputPath;
    }

    public String getChartTitle() {
        return m_chartTitle;
    }

    public void setChartTitle(String chartTitle) {
        m_chartTitle = chartTitle;
    }

    public String getChartXAxisTitle() {
        return m_chartXAxisTitle;
    }

    public void setChartXAxisTitle(String chartXAxisTitle) {
        m_chartXAxisTitle = chartXAxisTitle;
    }

    public String getChartYAxisTitle() {
        return m_chartYAxisTitle;
    }

    public void setChartYAxisTitle(String chartYAxisTitle) {
        m_chartYAxisTitle = chartYAxisTitle;
    }

    public String getSecondAxisChartYAxisTitle() {
        return m_secondAxisChartYAxisTitle;
    }

    public void setSecondAxisChartYAxisTitle(String secondAxisChartYAxisTitle) {
        m_secondAxisChartYAxisTitle = secondAxisChartYAxisTitle;
    }

    public Integer getMaxYValue() {
        return m_maxYValue;
    }

    public void setMaxYValue(Integer maxYValue) {
        m_maxYValue = maxYValue;
    }

    public Integer getMinYValue() {
        return m_minYValue;
    }

    public void setMinYValue(Integer minYValue) {
        m_minYValue = minYValue;
    }

    public Integer getSecondAxisMinYValue() {
        return m_secondAxisMinYValue;
    }

    public void setSecondAxisMinYValue(Integer secondAxisMinYValue) {
        m_secondAxisMinYValue = secondAxisMinYValue;
    }

    public boolean forceYRange() {
        return m_forceYRange;
    }

    public void forceYRange(boolean forceYRange) {
        m_forceYRange = forceYRange;
    }

    public boolean forceSecondAxisYRange() {
        return m_forceSecondAxisYRange;
    }

    public void forceSecondAxisYRange(boolean forceSecondAxisYRange) {
        m_forceSecondAxisYRange = forceSecondAxisYRange;
    }

    public Integer getSecondAxisMaxYValue() {
        return m_secondAxisMaxYValue;
    }

    public void setSecondAxisMaxYValue(Integer secondAxisMaxYValue) {
        m_secondAxisMaxYValue = secondAxisMaxYValue;
    }

    public String getInputPath() {
        return m_inputPath;
    }

    public void setInputPath(String inputPath) {
        m_inputPath = inputPath;
    }

    public int getChartWidth() {
        return m_width;
    }

    public void setChartWidth(int width) {
        m_width = width;
    }

    public int getChartHeight() {
        return m_height;
    }

    public void setChartHeight(int height) {
        m_height = height;
    }

    public boolean isBarChart() {
        return m_barChart;
    }

    public void setBarChart(boolean barChart) {
        m_barChart = barChart;
    }

    public boolean isSecondAxisBarChart() {
        return m_secondAxisBarChart;
    }

    public void setSecondAxisBarChart(boolean secondAxisBarChart) {
        m_secondAxisBarChart = secondAxisBarChart;
    }

    public int getLineWeight() {
        return m_lineWeight;
    }

    public void setLineWeight(int lineWeight) {
        m_lineWeight = lineWeight;
    }

    public int getSecondAxisLineWeight() {
        return m_secondAxisLineWeight;
    }

    public void setSecondAxisLineWeight(int secondAxisLineWeight) {
        m_secondAxisLineWeight = secondAxisLineWeight;
    }

    public boolean hasDataPoints() {
        return m_dataPoints;
    }

    public void setDataPoints(boolean dataPoints) {
        m_dataPoints = dataPoints;
    }

    public boolean hasSecondAxisDataPoints() {
        return m_secondAxisDataPoints;
    }

    public void setSecondAxisDataPoints(boolean secondAxisDataPoints) {
        m_secondAxisDataPoints = secondAxisDataPoints;
    }

    public String[] getSeriesTitles() {
        return m_seriesTitles;
    }

    public void setSeriesTitles(String[] seriesTitles) {
        m_seriesTitles = seriesTitles;
    }

    public String[] getSecondAxisSeriesTitles() {
        return m_secondAxisSeriesTitles;
    }

    public void setSecondAxisSeriesTitles(String[] secondAxisSeriesTitles) {
        m_secondAxisSeriesTitles = secondAxisSeriesTitles;
    }

    public boolean isCliServer() {
        return m_cliServer;
    }

    public void setCliServer(boolean cliServer) {
        m_cliServer = cliServer;
    }

	public List<ColourOverride> getColourOverrides() {
		// TODO Auto-generated method stub
		return m_colourOverrides;
	}
	
	public void setColourOverrides(List<ColourOverride> colourOverrides) {
		m_colourOverrides = colourOverrides;
	}

    public int getListenPort() {
        return m_listenPort;
    }
    
    public void setListenPort(int port) {
        m_listenPort = port;
    }



	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + (m_barChart ? 1231 : 1237);
		result = PRIME * result + ((m_chartTitle == null) ? 0 : m_chartTitle.hashCode());
		result = PRIME * result + ((m_chartXAxisTitle == null) ? 0 : m_chartXAxisTitle.hashCode());
		result = PRIME * result + m_chartXType;
		result = PRIME * result + ((m_chartYAxisTitle == null) ? 0 : m_chartYAxisTitle.hashCode());
		result = PRIME * result + (m_cliServer ? 1231 : 1237);
		result = PRIME * result + OptionsBean.hashCode(m_columnIndexes);
		result = PRIME * result + (m_dataPoints ? 1231 : 1237);
		result = PRIME * result + m_dataSeparatorType;
		result = PRIME * result + ((m_dateFormat == null) ? 0 : m_dateFormat.hashCode());
		result = PRIME * result + ((m_fileOutputPath == null) ? 0 : m_fileOutputPath.hashCode());
		result = PRIME * result + (m_headerRow ? 1231 : 1237);
		result = PRIME * result + m_height;
		result = PRIME * result + (m_ignoreDuplicateValues ? 1231 : 1237);
		result = PRIME * result + (m_ignoreMissingColumns ? 1231 : 1237);
		result = PRIME * result + ((m_inputPath == null) ? 0 : m_inputPath.hashCode());
		result = PRIME * result + m_lineWeight;
		result = PRIME * result + m_maxYValue;
		result = PRIME * result + (m_secondAxisBarChart ? 1231 : 1237);
		result = PRIME * result + ((m_secondAxisChartYAxisTitle == null) ? 0 : m_secondAxisChartYAxisTitle.hashCode());
		result = PRIME * result + OptionsBean.hashCode(m_secondAxisColumnIndexes);
		result = PRIME * result + (m_secondAxisDataPoints ? 1231 : 1237);
		result = PRIME * result + m_secondAxisLineWeight;
		result = PRIME * result + m_secondAxisMaxYValue;
		result = PRIME * result + OptionsBean.hashCode(m_secondAxisSeriesTitles);
		result = PRIME * result + OptionsBean.hashCode(m_seriesTitles);
		result = PRIME * result + m_width;
		result = PRIME * result + ((m_colourOverrides == null) ? 0 : m_colourOverrides.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final OptionsBean other = (OptionsBean) obj;
		if (m_barChart != other.m_barChart)
			return false;
		if (m_chartTitle == null) {
			if (other.m_chartTitle != null)
				return false;
		} else if (!m_chartTitle.equals(other.m_chartTitle))
			return false;
		if (m_chartXAxisTitle == null) {
			if (other.m_chartXAxisTitle != null)
				return false;
		} else if (!m_chartXAxisTitle.equals(other.m_chartXAxisTitle))
			return false;
		if (m_chartXType != other.m_chartXType)
			return false;
		if (m_chartYAxisTitle == null) {
			if (other.m_chartYAxisTitle != null)
				return false;
		} else if (!m_chartYAxisTitle.equals(other.m_chartYAxisTitle))
			return false;
		if (m_cliServer != other.m_cliServer)
			return false;
		if (!Arrays.equals(m_columnIndexes, other.m_columnIndexes))
			return false;
		if (m_dataPoints != other.m_dataPoints)
			return false;
		if (m_dataSeparatorType != other.m_dataSeparatorType)
			return false;
		if (m_dateFormat == null) {
			if (other.m_dateFormat != null)
				return false;
		} else if (!m_dateFormat.equals(other.m_dateFormat))
			return false;
		if (m_fileOutputPath == null) {
			if (other.m_fileOutputPath != null)
				return false;
		} else if (!m_fileOutputPath.equals(other.m_fileOutputPath))
			return false;
		if (m_headerRow != other.m_headerRow)
			return false;
		if (m_height != other.m_height)
			return false;
		if (m_ignoreDuplicateValues != other.m_ignoreDuplicateValues)
			return false;
		if (m_ignoreMissingColumns != other.m_ignoreMissingColumns)
			return false;
		if (m_inputPath == null) {
			if (other.m_inputPath != null)
				return false;
		} else if (!m_inputPath.equals(other.m_inputPath))
			return false;
		if (m_lineWeight != other.m_lineWeight)
			return false;
		if (m_maxYValue != other.m_maxYValue)
			return false;
		if (m_secondAxisBarChart != other.m_secondAxisBarChart)
			return false;
		if (m_secondAxisChartYAxisTitle == null) {
			if (other.m_secondAxisChartYAxisTitle != null)
				return false;
		} else if (!m_secondAxisChartYAxisTitle.equals(other.m_secondAxisChartYAxisTitle))
			return false;
		if (!Arrays.equals(m_secondAxisColumnIndexes, other.m_secondAxisColumnIndexes))
			return false;
		if (m_secondAxisDataPoints != other.m_secondAxisDataPoints)
			return false;
		if (m_secondAxisLineWeight != other.m_secondAxisLineWeight)
			return false;
		if (m_secondAxisMaxYValue != other.m_secondAxisMaxYValue)
			return false;
		if (!Arrays.equals(m_secondAxisSeriesTitles, other.m_secondAxisSeriesTitles))
			return false;
		if (!Arrays.equals(m_seriesTitles, other.m_seriesTitles))
			return false;
		if (m_width != other.m_width)
			return false;
		if (m_colourOverrides == null) {
			if (other.m_colourOverrides != null)
				return false;
		} else if (!m_colourOverrides.equals(other.m_colourOverrides))
			return false;
		return true;
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
