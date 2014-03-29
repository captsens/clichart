============
cliserverlib
============

Introduction
============

This page documents the **cliserverlib** Python library, which provides a Python driver for
clichart in CLI server and TCP/IP server modes.


Requirements
============

Before you use cliserverlib in CLI server mode, you must ensure that cliserverlib can locate the clichart shell
script/batch file (as appropriate).  There are 3 ways of doing this:

 * The directory containing the ``cliserverlib.py`` file also contains the clichart shell
   script/batch file (as appropriate)
 * (Unix/Linux only) The directory containing the ``cliserverlib.py`` file contains a symbolic
   link to the clichart shell script, or
 * The directory containing the clichart shell script/batch file is in the PATH.

To use cliserverlib in TCP/IP server mode, the server must be listening to the appropriate port
on localhost.

You'll also need to add the directory containing the ``cliserverlib.py`` file to your PYTHONPATH,
so that it can be found by your Python script.


Class Documentation
===================

To interact with the library, you use the ``ClichartDriver`` class, which has the following public
methods:

class ClichartDriver(responseTimeout=10, port=-1)
    Creates the driver, including starting clichart and connecting to its input, output and error
    streams.  ``responseTimeout`` is the maximum time to wait for any command response before
    generating an error, in seconds.  If ``port`` is greater than 0, attempts to connect to the
    CLIChart TCP/IP server on that port

generateChart(clearFirst = True, \*\*kw)
    Generate a chart, using all the options set using key:value pair arguments.  The arguments are
    as shown in the table below.

    By default this clears all previous options first, so you must
    supply all options required. However, by passing ``clearFirst = False``, your options will be
    taken as overrides for the previously-supplied options.

    Any errors will be thrown as ``ClichartErrors``.

close()
    Shut down clichart

setServerTimeout(timeInSeconds)
    Tell the server to exit if it goes for more than this number of seconds without receiving any input.
    Used to make use of the server more robust for long-running processes


The module also defines the following values, which are used for the ``chartType`` argument for
the ``generateChart()`` method.

CHART_TYPE_DATETIME
    Define for charts with an X axis based on date and/or time (the default)

CHART_TYPE_VALUE
    Define for charts with an X axis based on numerical values

CHART_TYPE_NONE
    Define for charts with an X axis with no values (the chart is just a series of Y axis values)


Arguments for generateChart()
------------------------------

The arguments used for the generateChart() method mostly match the documented options for clichart -
see the `documentation on CLI server mode <clichart.html#cli-server-mode>`_ for documentation
on the meaning of each one.

The arguments are:

======================  ========================================  ==================  =============================================
Argument                Value                                     Equivalent          Example
                                                                  clichart option
======================  ========================================  ==================  =============================================
inputPath               Path to input file                        inputPath           inputPath = '/path/to/file.csv'
outputPath              Path to output file                       outputpath          outputPath = 'dir/chart.png'
columnList              List or tuple of                          columnlist          columnList = [0,1,4,5]
                        column indexes
columnList2             As per columnList                         columnlist2         columnList2 = [3]
isCsv                   True                                      csv                 isCsv = True
hasHeader               True                                      hasheader           hasHeader = True
chartType               CHART_TYPE_VALUE, CHART_TYPE_NONE         xvalue, noxvalue    chartType = cliserverlib.CHART_TYPE_VALUE
title                   Title                                     title               title = 'The title'
xTitle                  Title                                     xtitle              xTitle = 'Date'
yTitle                  Title                                     ytitle              yTitle = 'Temperature (deg C)'
yTitle2                 Title                                     ytitle2             yTitle2 = 'RPM'
ignoreMissingColumns    True                                      ignoremissing       ignoreMissingColumns = True
ignoreDuplicateValues   True                                      ignoredup           ignoreDuplicateValues = True
dateFormat              Format string                             dateformat          dateFormat = 'HH:MM:ss'
showDatapoints          True                                      datapoints          showDatapoints = True
showDatapoints2         True                                      datapoints2         showDatapoints2 = True
lineWeight              int, 1 - 5                                lineweight          lineWeight = 4
lineWeight2             int, 1 - 5                                lineweight2         lineWeight2= 1
maxY                    float                                     maxy                maxY = 30000
maxY2                   float                                     maxy2               maxY2 = 30000
minY                    float                                     miny                minY = 30000
minY2                   float                                     miny2               minY2 = 30000
forceYRange             True                                      forceyrange         forceYRange = True
forceYRange2            True                                      forceyrange2        forceYRange2 = True
height                  int, pixels                               height              height = 600
width                   int, pixels                               width               width = 800
isBar                   True                                      bar                 isBar = True
isBar2                  True                                      bar2                isBar2 = True
seriesTitles            List or tuple of series titles            seriestitles        seriesTitles = ['Fan speed', 'Temp']
seriesTitles2           As per seriesTitles                       seriestitles2       seriesTitles2 = ['Fan speed', 'Temp']
debugEcho               True                                      debug-echo          debugEcho = True
colours                 List of (int, string) as (index, colour)  colours             colours = [(0: 'cyan'), (1, 'FF00FF')]
======================  ========================================  ==================  =============================================


Usage and Example
=================

A sample script fragment to generate 2 charts, then exit: ::

    # import the library
    import cliserverlib

    try:

        # create the driver
        driver = cliserverlib.ClichartDriver()

        # a simple chart
        driver.generateChart(title = 'A title', inputPath = 'samples/SystemTemps.csv',
            isCsv = True, hasHeader = True, outputPath = 'samples/SystemTemps1.png')

        # re-use most of the previous options, by setting clearFirst = False
        driver.generateChart(clearFirst = False, outputPath = 'samples/SystemTemps2.png',
            columnList = [0, 1, 2])

        # generate a value-based chart
        driver.generateChart(clearFirst = False, outputPath = 'samples/SystemTemps3.png',
            chartType = cliserverlib.CHART_TYPE_VALUE, columnList = [1, 2])

        # now shut the driver down
        driver.close()

    except cliserverlib.ClichartError, message:
        print 'Chart generation failed with message', message
