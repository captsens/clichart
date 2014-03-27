/* (C) Copyright 2006-2008, by John Dickson
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

import net.sf.clichart.chart.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * Used to parse the command line into options and arguments.
 *
 * <p> Note: This class refers to the options parsed from the command-line as Options, while the Jakarta commons CLI
 * API uses the word to mean option definitions.  The commons usage is represented with a fully-qualified class name
 * in this class.
 *
 * @author johnd
 */
public class OptionParser {

    /* ========================================================================
     *
     * Class (static) variables.
     */


    /* ========================================================================
     *
     * Instance variables.
     */

    private org.apache.commons.cli.Options m_optionDefs;

    /* ========================================================================
     *
     * Constructors
     */

    public OptionParser() {
        m_optionDefs = buildOptionDefs();
    }

    /* ========================================================================
     *
     * Static methods
     */

    /* ========================================================================
     *
     * Public methods
     */

    public Options getOptions(String[] args) throws InvalidOptionsException, ShowUsageException {
        try {
            CommandLineParser clParser = new PosixParser();
            CommandLine commandLine = clParser.parse(m_optionDefs, args);
            return new CliOptions(commandLine);

        } catch (ParseException e) {
            throw new InvalidOptionsException("Invalid command line: " + e.getMessage(), e);
        }

    }

    public void showHelp(String message) {
        HelpFormatter formatter = new FixedHelpFormatter();

        String header = "Create a chart from tabular data";
        if (message != null) {
            header = message + "\n\n" + header;
        }

        formatter.printHelp("java -jar clichart.jar [options] [inputFile]", header, m_optionDefs,
                "If input file is omitted, reads data from stdin");

        exit();
    }

    /* ========================================================================
     *
     * Protected / package-private methods
     */

    /* ========================================================================
     *
     * Private methods
     */

    private org.apache.commons.cli.Options buildOptionDefs() {
        org.apache.commons.cli.Options optionDefs = new org.apache.commons.cli.Options();

        for (int i = 0; i < CliOptionDefinitions.OPTIONS_WITH_ARGS.length; i++) {
            addOption(optionDefs, CliOptionDefinitions.OPTIONS_WITH_ARGS[i], true);
        }

        for (int i = 0; i < CliOptionDefinitions.OPTIONS_WITHOUT_ARGS.length; i++) {
            addOption(optionDefs, CliOptionDefinitions.OPTIONS_WITHOUT_ARGS[i], false);
        }

        return optionDefs;
    }

    private void addOption(org.apache.commons.cli.Options optionDefs, String[] optionDetails, boolean hasArgument) {
        // OptionBuilder is a stateful class
        OptionBuilder.withLongOpt(optionDetails[1]);
        OptionBuilder.withDescription(optionDetails[2]);

        if (hasArgument) {
            OptionBuilder.hasArg(true);
            OptionBuilder.withValueSeparator();
        }

        if (optionDetails[0] != null) {
            optionDefs.addOption(OptionBuilder.create(optionDetails[0]));
        } else {
            optionDefs.addOption(OptionBuilder.create());
        }
    }

    // package-private, so can be overridden in test
    void exit() {
        System.exit(1);
    }


}
