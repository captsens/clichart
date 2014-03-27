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

package net.sf.clichart.main;

import junit.framework.TestCase;
import net.sf.clichart.chart.ColourOverride;
import net.sf.clichart.chart.Options;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * CLASS_DESCRIPTION
 *
 * @author johnd
 */
public class OptionParserTest extends TestCase {

    /* ========================================================================
     *
     * Class (static) variables.
     */

    private static final String ALT_DATE_FORMAT = "dd/MM/yyyy";
    private static final String ALT_OUTPUT_PATH = "/tmp/chart.png";
    private static final String ALT_CHART_TITLE = "Chart title";
    private static final String ALT_X_TITLE = "X axis title";
    private static final String ALT_Y_TITLE = "Y axis title";
    private static final String ALT_INPUT_PATH = "/tmp/some/input.csv";
    
    private static final List<ColourOverride> COLOUR_OVERRIDES = new ArrayList<ColourOverride>();
    {
    	COLOUR_OVERRIDES.add(new ColourOverride(0, Color.red));
    	COLOUR_OVERRIDES.add(new ColourOverride(2, Color.lightGray));
    	COLOUR_OVERRIDES.add(new ColourOverride(3, new Color(240, 240, 240)));
    }

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

    /**
     * Base case - no options set, so should use default
     */
    public void testGetOptions_none() throws Exception {
        Options options = new OptionParser().getOptions(new String[]{});
        assertEquals(Options.DATA_SEP_WHITE_SPACE, options.getDataSeparatorType());
        assertEquals("HH:mm", options.getDateFormat());
        assertTrue(!options.hasHeaderRow());
        assertTrue(!options.ignoreMissingColumns());
        assertTrue(!options.ignoreEmptyColumns());
        assertTrue(Arrays.toString(options.getColumnIndexes()), Arrays.equals(new int[]{0, 1},
                options.getColumnIndexes()));
        assertEquals(Options.X_TYPE_DATE_TIME, options.getChartXType());
        assertEquals(null, options.getFileOutputPath());
        assertEquals(null, options.getChartTitle());
        assertEquals(null, options.getChartXAxisTitle());
        assertEquals(null, options.getChartYAxisTitle());
        assertEquals(null, options.getMaxYValue());
        assertEquals(null, options.getInputPath());
        assertEquals(null, options.getColourOverrides());
    }

    public void testGetOptions_all() throws Exception {
        Options options = new OptionParser().getOptions(new String[]{
                "-d", ALT_DATE_FORMAT,
                "-l", "0,1,2,3",
                "-o", ALT_OUTPUT_PATH,
                "-t", ALT_CHART_TITLE,
                "-x", ALT_X_TITLE,
                "-y", ALT_Y_TITLE,
                "-m", "400",
                "--miny", "200",
                "--forceyrange",
                "--maxy2", "999",
                "--miny2=998",
                "--forceyrange2",
                "-cfi",
                "--ignoreempty",
                "--colours", "0:red,2:lightgrey,3:f0f0f0",
                ALT_INPUT_PATH
        });
        assertEquals(Options.DATA_SEP_CSV, options.getDataSeparatorType());
        assertEquals(ALT_DATE_FORMAT, options.getDateFormat());
        assertTrue(options.hasHeaderRow());
        assertTrue(options.ignoreMissingColumns());
        assertTrue(options.ignoreEmptyColumns());
        assertTrue(Arrays.toString(options.getColumnIndexes()), Arrays.equals(new int[]{0, 1, 2, 3},
                options.getColumnIndexes()));
        assertEquals(Options.X_TYPE_DATE_TIME, options.getChartXType());
        assertEquals(ALT_OUTPUT_PATH, options.getFileOutputPath());
        assertEquals(ALT_CHART_TITLE, options.getChartTitle());
        assertEquals(ALT_X_TITLE, options.getChartXAxisTitle());
        assertEquals(ALT_Y_TITLE, options.getChartYAxisTitle());
        assertEquals(new Integer(200), options.getMinYValue());
        assertEquals(new Integer(400), options.getMaxYValue());
        assertTrue(options.forceYRange());
        assertEquals(new Integer(998), options.getSecondAxisMinYValue());
        assertEquals(new Integer(999), options.getSecondAxisMaxYValue());
        assertTrue(options.forceSecondAxisYRange());
        assertEquals(ALT_INPUT_PATH, options.getInputPath());
        for (int i = 0; i < 3; i++) {
        	assertEquals(COLOUR_OVERRIDES.get(i), options.getColourOverrides().get(i));
        }
    }

    public void testGetOptions_invalid() throws Exception {
        try {
            new OptionParser().getOptions(new String[]{"-d"});
            fail();
        } catch (InvalidOptionsException expected) {}
    }

    public void testGetOptions_usage() throws Exception {
        try {
            new OptionParser().getOptions(new String[]{"-h"});
            fail();
        } catch (ShowUsageException expected) {}
    }

    public void testShowHelp() {
        // do this with a subclass, to prevent exit from VM
        OptionParser parser = new OptionParser() {
            // package-private, so can be overridden in test
            void exit() {
            }
        };
        parser.showHelp("A message");
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
