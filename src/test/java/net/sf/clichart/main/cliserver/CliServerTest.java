/* (C) Copyright 2008-2012, by John Dickson
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
package net.sf.clichart.main.cliserver;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import net.sf.clichart.chart.ColourOverride;
import net.sf.clichart.chart.OptionsBean;
import net.sf.clichart.main.ChartGenerator;

import org.easymock.MockControl;

import junit.framework.TestCase;

public class CliServerTest extends TestCase {
	
    private MockControl m_chartGeneratorControl;
    private ChartGenerator m_chartGeneratorMock;
    
    private CliServer m_cliServer;
    

    protected void setUp() throws IOException {
        m_chartGeneratorControl = MockControl.createStrictControl(ChartGenerator.class);
        m_chartGeneratorMock = (ChartGenerator) m_chartGeneratorControl.getMock();

    	m_cliServer = new CliServer();
    	m_cliServer.setChartGenerator(m_chartGeneratorMock);
    }

	public void testQuitCommand() throws IOException {
		assertEquals("OK\n", interactWithCliServer(""));
	}
	
	public void testInvalidCommand() throws IOException {
		assertEquals("OK\nUnrecognised command: unknown\n", interactWithCliServer("unknown\n"));
	}

	public void testClearCommand() throws IOException {
		m_chartGeneratorMock.clear();
		m_chartGeneratorControl.replay();
		assertEquals("OK\nOK\n", interactWithCliServer("clear\n"));
		m_chartGeneratorControl.verify();
	}

	public void testDebugEchoCommand() throws IOException {
		assertEquals("OK\nOK\n", interactWithCliServer("debug-echo\n"));
	}

	public void testInvalidOptionCommand() throws IOException {
		assertEquals("OK\nInvalid argument: Command requires an argument\n", interactWithCliServer("t\n"));
	}

	public void testCommandWithException() throws IOException {
		m_chartGeneratorMock.clear();
		m_chartGeneratorControl.setThrowable(new RuntimeException("XXX"));
		m_chartGeneratorControl.replay();
		assertEquals("OK\nGeneral error: java.lang.RuntimeException: XXX\n", interactWithCliServer("clear\n"));
		m_chartGeneratorControl.verify();
	}

	public void testChartGenerate() throws Exception {
		m_chartGeneratorMock.clear();
		OptionsBean options = new OptionsBean();
		options.setInputPath("samples/SystemTemps.csv");
		options.setFileOutputPath("samples/SystemTemps.csv");
		options.setDataSeparatorType(OptionsBean.DATA_SEP_CSV);
		options.setHeaderRow(true);
		options.setColumnIndexes(new int[] {0, 1, 2});
		options.setChartTitle("Temps/fan speeds with second axis bar");
		options.setChartYAxisTitle("Degrees C");
		options.setSecondAxisColumnIndexes(new int[] {2, 3, 4});
		options.setSecondAxisBarChart(true);
		options.setSecondAxisChartYAxisTitle("secondAxisChartYAxisTitle");
		List<ColourOverride> colourOverrides = Arrays.asList(new ColourOverride[]{new ColourOverride(0, Color.red), 
				new ColourOverride(2, new Color(0, 240, 255))});
		options.setColourOverrides(colourOverrides);
        options.setMinYValue(40);
        options.setMaxYValue(70);
        options.forceYRange(true);
        options.setSecondAxisMinYValue(2000);
        options.setSecondAxisMaxYValue(2600);
        options.forceSecondAxisYRange(true);
		m_chartGeneratorMock.generateChart(options);
		// TODO: work out what's missing from above options bean...
		// TODO: Looks like this doesn't actually check all the values against the mock anyway...
		m_chartGeneratorControl.setMatcher(MockControl.ALWAYS_MATCHER);
		m_chartGeneratorControl.replay();
		String command = "clear\n"
				+ "inputpath samples/SystemTemps.csv\n"
				+ "o ../output/SystemTemps-2axisbar-cli.png\n"
				+ "c\n"
				+ "f\n"
				+ "l 0,1,2\n"
				+ "\n"
				+ "# a comment that we ignore \n"
				+ "t Temps/fan speeds with second axis bar\n"
				+ "y Degrees C\n"
				+ "columnlist2 3,4\n"
				+ "bar2\n"
				+ "ytitle2 RPM\n"
				+ "colours 0:red,2:00f0ff\n"
                + "miny 40\n"
                + "maxy 70\n"
                + "forceyrange\n"
                + "miny2 2000\n"
                + "maxy2 2600\n"
                + "forceyrange2\n"
                + "go\n";
		assertEquals("OK\nOK\nOK\nOK\nOK\nOK\nOK\nOK\nOK\nOK\nOK\nOK\nOK\nOK\nOK\nOK\nOK\nOK\nOK\nOK\n",
                interactWithCliServer(command));
		m_chartGeneratorControl.verify();
	}

	/**
	 * Call the CliServer's interact method, passing the testInput string as its input, and returning
	 * its output.
	 * 
	 * NOTE:
	 * The command "quit\n" will be appended to your input, otherwise the call to interact() will never return!
	 */
	private String interactWithCliServer(String testInput) {
		String fullInput = testInput + "quit\n";
		ByteArrayInputStream input = new ByteArrayInputStream(fullInput.getBytes());
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		m_cliServer.interact(input, new PrintStream(output));
		return output.toString();
	}
}
