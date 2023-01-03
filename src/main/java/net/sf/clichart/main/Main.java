/* (C) Copyright 2006, by John Dickson
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

import net.sf.clichart.chart.ChartSaverException;
import net.sf.clichart.chart.Options;
import net.sf.clichart.data.InvalidDataException;
import net.sf.clichart.main.cliserver.CliServer;
import net.sf.clichart.main.cliserver.TcpServer;

import java.io.IOException;

/**
 * Main class for executing clichart from the command line, and for use as a library.
 *
 * <p>Can also be used to drive clichart from Java, as a library.  Several of the methods can be overridden by
 * a subclass if required.
 *
 * <p>The incoming data is parsed by a DataParser, which has an AbstractChartBuilder linked to it as a data sink for
 * each chart axis.  Once parsing of the data is finished, the chart builder is responsible for building the chart
 * on request. 
 *
 * @author johnd
 */
public class Main {
    /* ========================================================================
     *
     * Class (static) variables.
     */

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

    public static void main(String[] args) {
        OptionParser optionParser = new OptionParser();
        try {
            Options options = optionParser.getOptions(args);

            if (options.getListenPort() > 0) {
                System.out.println("Starting in TCP/IP server mode");
                TcpServer server = new TcpServer(options.getListenPort(), createChartGeneratorFactory());
                server.serve();
            } else if (options.isCliServer()) {
                CliServer cliServer = new CliServer();
                cliServer.setChartGenerator(createChartGeneratorFactory().createChartGenerator());
                cliServer.interact(System.in, System.out);

            } else {
                createChartGeneratorFactory().createChartGenerator().generateChart(options);
            }

        } catch (InvalidOptionsException e) {
            optionParser.showHelp(e.getMessage());
        } catch (ShowUsageException e) {
            optionParser.showHelp(null);
        } catch (InvalidDataException e) {
            System.out.println("Invalid data: " + e.getMessage());
        } catch (ChartSaverException e) {
            System.out.println("Error saving chart: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("I/O error reading or writing: " + e.toString());
        }
    }


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

    private static ChartGeneratorFactory createChartGeneratorFactory() {
        return new DefaultChartGeneratorFactory();
    }

}
