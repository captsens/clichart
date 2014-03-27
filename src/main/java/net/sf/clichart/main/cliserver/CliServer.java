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

package net.sf.clichart.main.cliserver;

import net.sf.clichart.chart.OptionsBean;
import net.sf.clichart.chart.ChartSaverException;
import net.sf.clichart.chart.Options;
import net.sf.clichart.main.ChartGenerator;
import net.sf.clichart.main.CliOptionDefinitions;
import net.sf.clichart.main.InvalidOptionsException;
import net.sf.clichart.data.InvalidDataException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.HashMap;

/**
 * A simple server that typically uses stdin/stdout or sockets to allow a client to interact with clichart.
 *
 * <p>Uses pretty much the same options (both short and long) as the CLI, and calls an ChartGenerator
 * when asked to generate a chart.
 *
 * <p>Note that options are sticky from the last chart, unless 'clear' is called.
 *
 * @author johnd
 */
public class CliServer {

    /* ========================================================================
     *
     * Class (static) variables.
     */

    /* ========================================================================
     *
     * Instance variables.
     */

    private static final boolean DEBUG = false;

    private ChartGenerator m_chartGenerator;

    private PrintWriter m_stdout;

    /** Contains options being built up by the client */
    private OptionsBean m_options = new OptionsBean();

    // contains OptionSetters keyed on option name, i.e. command
    private Map<String, OptionSetter> m_optionSetters = new HashMap<String, OptionSetter>();
    
    /** Used to terminate the JVM if no commands received within timeout period */
    private SystemExitTimer m_exitTimer = new SystemExitTimer();

    private boolean m_debugEcho = false;

    /* ========================================================================
     *
     * Constructors
     */

    public CliServer() {
        buildOptionSettersMap();
    }

    /* ========================================================================
     *
     * Static methods
     */

    /* ========================================================================
     *
     * Public methods
     */

    /**
     * Set the chart generator, for creating chart generators for generating all charts
     */
    public void setChartGenerator(ChartGenerator chartGenerator) {
        m_chartGenerator = chartGenerator;
    }

    /**
     * Interact with the caller via the stream-based protocol, until 'quit' is called or input stream is closed
     *
     * @param stdin
     * @param stdout
     */
    public void interact(InputStream stdin, PrintStream stdout) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdin));
        m_stdout = new PrintWriter(stdout);
        printResponseMessage(null);

        try {
            String line = reader.readLine();
            while (line != null) {
                m_exitTimer.resetTimeout();
                processLine(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            System.err.println("Unexpected error reading input - terminating");
            e.printStackTrace();
        } catch (TerminateCliServerException e) {
            // asked to end, so we just return
        }
    }

    /* ========================================================================
     *
     * Protected / package-private methods
     */

    /* ========================================================================
     *
     * Private methods
     */

    private void buildOptionSettersMap() {
        buildOptionSettersFromDefinitions(CliOptionDefinitions.OPTIONS_WITH_ARGS);
        buildOptionSettersFromDefinitions(CliOptionDefinitions.OPTIONS_WITHOUT_ARGS);

        // special cases
        m_optionSetters.put("inputpath", new StringOptionSetter("setInputPath"));
        m_optionSetters.put("c", new IntOptionSetter("setDataSeparatorType", Options.DATA_SEP_CSV));
        m_optionSetters.put("csv", new IntOptionSetter("setDataSeparatorType", Options.DATA_SEP_CSV));
        m_optionSetters.put("n", new IntOptionSetter("setChartXType", Options.X_TYPE_NONE));
        m_optionSetters.put("noxvalue", new IntOptionSetter("setChartXType", Options.X_TYPE_NONE));
        m_optionSetters.put("v", new IntOptionSetter("setChartXType", Options.X_TYPE_VALUE));
        m_optionSetters.put("xvalue", new IntOptionSetter("setChartXType", Options.X_TYPE_VALUE));
        m_optionSetters.put("colours", new ColourOverrideListOptionSetter("setColourOverrides"));
    }

    private void buildOptionSettersFromDefinitions(String[][] optionDefinitions) {
        for (int i = 0; i < optionDefinitions.length; i++) {
            String[] optionDefinition = optionDefinitions[i];
            String shortCommand = optionDefinition[0];
            String longCommand = optionDefinition[1];
            String propertyName = optionDefinition[3];
            String propertyType = optionDefinition[4];

            OptionSetter optionSetter = buildOptionSetter(propertyType, propertyName);

            if (shortCommand != null) {
                m_optionSetters.put(shortCommand, optionSetter);
            }
            if (longCommand != null) {
                m_optionSetters.put(longCommand, optionSetter);
            }
        }
    }

    private OptionSetter buildOptionSetter(String propertyType, String setterName) {
        if (setterName == null) {
            return null;
        } else if (propertyType == null) {
            return new NoArgumentOptionSetter(setterName);
        } else if (CliOptionDefinitions.STRING.equals(propertyType)) {
            return new StringOptionSetter(setterName);
        } else if (CliOptionDefinitions.INT.equals(propertyType)) {
            return new IntOptionSetter(setterName);
        } else if (CliOptionDefinitions.INTEGER.equals(propertyType)) {
            return new IntegerOptionSetter(setterName);
        } else if (CliOptionDefinitions.INT_ARRAY.equals(propertyType)) {
            return new IntArrayOptionSetter(setterName);
        } else if (CliOptionDefinitions.BOOLEAN.equals(propertyType)) {
            return new BooleanOptionSetter(setterName);
        } else if (CliOptionDefinitions.STRING_ARRAY.equals(propertyType)) {
            return new StringArrayOptionSetter(setterName);
        } else if (CliOptionDefinitions.COLOUR_INDEX_ARRAY.equals(propertyType)) {
            return new ColourOverrideListOptionSetter(setterName);
        } else {
            System.err.println("Error: Can't find an option setter for property type: " + propertyType);
            return null;
        }
    }

    private void processLine(String line) throws TerminateCliServerException {
        line = line.trim();

        if (m_debugEcho) {
            System.err.println(line);
            System.err.flush();
        }
        
        if (line.length() == 0 || line.startsWith("#")) {
            // nothing to do
            return;
        }

        String[] commandAndArg = parseLine(line);
        processCommand(commandAndArg);
    }

    private String[] parseLine(String line) {
        return line.split("\\s+", 2);
    }

    private void processCommand(String[] commandAndArg) throws TerminateCliServerException {
        String command = commandAndArg[0].toLowerCase();
        String arg = null;
        if (commandAndArg.length == 2) {
            arg = commandAndArg[1];
        }

        try {
            if ("quit".equals(command)) {
                throw new TerminateCliServerException();

            } else if ("go".equals(command)) {
                generateChart();

            } else if ("clear".equals(command)) {
                m_options = new OptionsBean();
                m_chartGenerator.clear();

            } else if ("debug-echo".equals(command)) {
                m_debugEcho = true;

            } else if ("timeout".equals(command)) {
                setTimeout(arg);

            } else {
                OptionSetter optionSetter = (OptionSetter) m_optionSetters.get(command);
                if (optionSetter == null) {
                    printResponseMessage("Unrecognised command: " + command);
                    return;
                }
                optionSetter.setOption(m_options,  command, arg);
            }

            printResponseMessage(null);

        } catch (TerminateCliServerException e) {
        	throw e;
        } catch (InvalidOptionsException e) {
            printResponseMessage("Invalid argument: " + e.getMessage());
        } catch (InvalidDataException e) {
            printResponseMessage("Invalid data for generating chart: " + e.getMessage());
        } catch (ChartSaverException e) {
            printResponseMessage("Failed to save chart: " + e.getMessage());
        } catch (IOException e) {
            printResponseMessage("Error reading chart data: " + e.toString());
        } catch (Exception e) {
            printResponseMessage("General error: " + e.toString());
            if (DEBUG) {
                System.out.println(e.toString());
                e.printStackTrace();
            }
        }
    }

    private void generateChart() throws InvalidOptionsException, ChartSaverException, InvalidDataException, IOException {
        if (m_options.getInputPath() == null) {
            throw new InvalidOptionsException("Input file path is required");
        }
        if (m_options.getFileOutputPath() == null) {
            throw new InvalidOptionsException("Output file path is required");
        }

        m_chartGenerator.generateChart(m_options);
    }

    private void printResponseMessage(String message) {
        if (message == null) {
            message = "OK";
        }
        m_stdout.println(message);
        m_stdout.flush();
    }
    
    private void setTimeout(String timeout) {
        try {
            int iTimeout = Integer.parseInt(timeout);
            m_exitTimer.setTimeoutPeriod(iTimeout);
        } catch (NumberFormatException e) {
            printResponseMessage("Invalid timeout: " + timeout);
        }
    }



}
