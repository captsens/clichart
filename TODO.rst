Most recent (from 0.6.0 release)
===========
- Gradle build does not support running Java unit tests, and only runs python tests if triggered explicitly
- derivative script is very basic (and is not yet shown on the diagram in Introduction)
- Should use Python library for CSV parsing, rather than DIY

Older items
==================
Move to github
- Page on how to develop, including location of code
- Set up release
- Wiki?
- Message on SF forums pointing to new locations

- In aggregate, support percentage values (50, 90, 95, 99 etc.)
  See http://www.cs.umd.edu/~samir/498/vitter.pdf
- keyed aggregation
    - allow key columns to be in any position
- deprecation warning on use of popen3 - use subprocess
    
- Support for remote cliserver mode
    - Java
        - Main
            - If port is set on options, create a TCP/IP socket server
                - The socket server has a chart generator factory, and will create CliServers
        - Simple TCP/IP socket server
            - Option for listen port
                - doco
            - Later: Command-line options to start in this mode
                - Listen address (default to localhost)
                - Max threads
                - Read timeout (close socket)
                - Close after inactivity time?
            - Socket server class
                - Shut socket on timeout (use existing timer class?)
    - Python
        - allow cliserverlib to support this mode too
            x specify port
            - specify address
            - set read timeout
        - option to reconnect if closed
        

- BUG? 0.5.6a1 changed behaviour of --ignoreempty.  Now seems to break the line at the missing data point,
  rather than bridging it between the existing points
    - On second thoughts, maybe it's better that way?
- clichart - allow column headings to be used in lieu of column indexes

- merge.py
    - tests
    - support multiple key columns?

- Proper matcher for CliServerTest

- general
    - Better checking for and handling of errors (options or data)

- clichart
    - Add ability for x axis to consist of just strings
    - Introduce options object for DataParsers - far too many constructor args!
    - Consider logarithmic axes
    - ability to add moving averages for 1 or more columns
        - could add separate options like --maColumnList etc., and add the moving averages at
          the end, but then would be limited to MAs for columns already in the main list
        - alternative is to have the ChartBuilder build Series objects for all columns required
         (either raw or MA), then later add the appropriate versions of each to the chart
        - or, maybe easier still, add MAs using another Python script
    - stacked bar support
        - Use StackedXYBarRenderer?
    - add a --verbose flag
        - Message about ignoring duplicate data should only print if verbose output, and
          should include line number???
    - bar chart
        - BUG: bar width calcs look invalid for value charts.  See SystemTemps-value-x.png
        - BUG: If only one time point, bars don't show (issue with width calculation)
        - unit tests
    - rendering
        - Better line thickness on chart legend?
        - Unit tests for setting of line weight
        - Unit tests for enabling of shapes on xy renderer
    - Add install test for ignoredups
    - Split legend for second axis
    - CLI server mode
        - Add some way to reset default behaviour of each option (many currently have no way to
          re-establish their default without 'clear')

- cliserverlib
    - Add test for finding via PATH

- linestats
    - add support for quoting output
    - allow formulae in column specs

- discretestats

- mid
    - add option to disable psyco

- aggregate

- documentation

- Misc
    - Script to merge data points from 2 files?  E.g. so can add count on second axis
      from a second file.  Alternative is to allow 2 files to be specified on cmd line?

