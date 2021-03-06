========
clichart
========

Introduction
============

This page documents the **clichart** tool, which is the main tool in the CLIChart project.

Clichart is a Java program (with shell script and batch file wrappers, for UNIX/Linux and
Windows respectively).  Its job is to generate a chart from tabular data, and either
to display the chart in a window or save it to disk.

Clichart can:

 * Accept data from stdin or from a file
 * Accept input as comma-separated or whitespace-separated
 * Produce charts plotted against time (any units down to seconds), numbers, or just as
   arbitrary values
 * Plot any number of data series, on one or two Y axes
 * Treat the first row of data as headers for the data series
 * Include titles for the chart, X axis and Y axis/axes
 * Plot first or second axis as line or bar chart (histogram)
 * Control plot line/var thickness, and whether data points display
 * Display charts in an interactive window, allowing full customisation of display
 * Save charts as JPEG or PNG, either from the command-line or in the display window
 * Operate in CLI server mode, generating 1 or more charts based on commands passed via
   standard in (either from another script, or from a command file)
 * Operate in TCP/IP server mode, generating 1 or more charts based on commands passed via
   a TCP/IP socket (e.g. from cliserverlib).


Usage
=====

You use the tool like this::

     clichart [options] [inputFile]

Options
-------

::

 -b,--bar             Show as a bar chart, not X-Y line
    --bar2            Show second axis as a bar chart, not X-Y line
 -c,--csv             Expect input as CSV.  Default is
                      whitespace-separated
    --cliserver       Run the program as a CLI server, reading all
                      commands from standard in
    --colours         Override default chart colours.  Consists of a
                      comma-separated list of 'index:colour', where 'index' is the 0-based
                      series index, and 'colour' is 'red', 'blue', 'green' etc (see
                      documentation).  Indexes not overridden use default colours
    --columnlist2     List of columns for second y axis (if any),
                      comma-separated, 0-based.
 -d,--dateformat      Format of date/time, in SimpleDateFormat notation.
                      Defaults to 'HH:mm'
    --datapoints      Indicate each data point
    --datapoints2     Indicate each data point for the second Y axis
 -f,--hasheader       First row of data provides column headers for the legend
                      (default is no header row)
    --forceyrange     Force the y axis to use the limits (minimum/maximum) provided.  Default
                      is to use limits only if chart values would exceed them
    --forceyrange2    Force the second y axis (if any) to use the limits (minimum/maximum)
                      provided.  Default is to use limits only if chart values would
                      exceed them
 -g,--height          Chart height in pixels (defaults to 600)
 -h,--help            Show usage (this screen) and exit
 -i,--ignoremissing   Ignore missing columns (default is to terminate)
    --ignoreempty     Ignore empty columns (default is to terminate)
 -l,--columnlist      List of columns, comma-separated, 0-based.  X axis
                      value (if any) must be first.  Defaults to '0,1'
    --lineweight      Line weight (values are 1 - 5)
    --lineweight2     Line weight for the second Y axis (values are 1 - 5)
 -m,--maxy            Maximum value for y axis
    --maxy2           Maximum value for second y axis (if any)
    --miny            Minimum value for y axis
    --miny2           Minimum value for second y axis (if any)
 -n,--noxvalue        Chart has no X axis values - just number the rows
                      instead
 -o,--outputpath      Output chart to the given path (otherwise shows in a
                      window), must be JPG or PNG
 -p,--ignoredup       Ignore duplicate X axis values (default is to
                      terminate)
    --port            Port on which server should listen (only if TCP/IP
                      server required)
    --seriestitles    Data series titles, comma-separated. Interpreted in
                      same order as Y axis values in the column list
    --seriestitles2   Second axis data series titles, comma-separated.
                      Interpreted in same order as second axis column list
 -t,--title           Title for the chart
 -v,--xvalue          Chart has simple values as the X axis, not dates or
                      times
 -w,--width           Chart width in pixels (defaults to 800)
 -x,--xtitle          Title for the X axis
 -y,--ytitle          Title for the Y axis
    --ytitle2         Title for the second Y axis (if any)


If no input file is provided, the tabular data is read from standard input.


Date Formats
------------

The data format (-d option) uses format strings specified by Java's SimpleDateFormat
(see `the specification table here
<http://docs.oracle.com/javase/1.5.0/docs/api/java/text/SimpleDateFormat.html>`_).

The most common format string elements are:

======  ==================================================
Format  Meaning
======  ==================================================
yy      Year, 2 digits
yyyy    Year, 4 digits
MM      Month (1-12), 1 or 2 digits
MMM     Month abbreviation, e.g. Jan, Feb
dd      Day of the month (1-31), 1 or 2 digits
HH      Hour (0-23), 1 or 2 digits
mm      Minute (0-60), 1 or 2 digits
ss      Second (0-60), 1 or 2 digits
SSS     Millisecond (0-999), 1-3 digits
======  ==================================================

Other characters (such as ":", ",") are used as-is.

Some common examples:

==================  ==========================================================  ===================
Format String       Description                                                 Sample Data
==================  ==========================================================  ===================
HH:mm               Hour (24-hour clock) plus minute                            09:32, 23:06
HH:mm:ss            Hour (24-hour clock) plus minute and second                 09:32:12, 23:06:01
dd/MMM/yyyy:HH:mm   Apache log format (without second)                          07/Apr/2006:09:32
dd/MM/yy HH:mm      Short date with 2-digit year, plus hour and minute.  You
                    must enclose this in double quotes (") on the command       07/04/06 09:32
                    line because of the space in the format string.
==================  ==========================================================  ===================


Colours
-------

The colour override format (--colours option) allows a list of series indexes (0-based) and colours
to be specified - any series index not overridden will use the standard clichart colours.  Note that
series indexes continue from the first axis to the second, so if you have 2 series on the first axis
and one on the second, colour indexes 0 and 1 will refer to the first axis, and index 2 to the second.

Colours can be specified in one of 2 ways:

 * As a 3-byte hexadecimal number (case-insensitive) specifying the red, green and blue components, e.g. 
   ``ff0000`` for red.  This is the same as the system used in HTML
 * By name, chosen from the following list (case-insensitive):
 
 	- black
 	- blue
 	- cyan
 	- darkgrey or darkgray
 	- grey or gray
 	- green
 	- lightgrey or lightgray
 	- magenta
 	- orange
 	- pink
 	- red
 	- white
 	- yellow

For example, to override the first and third series colours, you could use: ::

	0:blue,2:ff00ff


Notes
=====

 * Requirements to run clichart:

    - Clichart requires Java - see `the installation page <installation.html>`_
    - The clichart script requires that either:

        + You have the ``JAVA_HOME`` environment variable set correctly - the Java executable must be
          found at ``$JAVA_HOME/bin/java`` (Linux/UNIX) or ``%JAVA_HOME%\bin\java.exe`` (Windows), or
        + The Java executable (``java`` for Linux/UNIX or ``java.exe`` for Windows) is in you PATH.

 * Clichart can be used in 3 main modes

    - Interactive, where the chart is displayed in a window (the default), and

        + Right-click on the chart to get the popup context menu
        + The chart can be saved using the File | Save as menu item, pressing Control-S, or from the popup menu
        + Most aspects of the chart's display can be controlled by using the Properties menu item on the
          popup menu.  However, note that control of a second Y axis is not yet supported
        + The popup menu also allows printing and zooming
        + The window can be closed using the File | Exit as menu item, or pressing Control-Q

    - Automatic, where you provide a filename for saving the chart (using the -o option).
      Clichart will exit after the chart is generated
    - CLI server, where commands are passed via standard input, either from a script or a command
      file.  See the CLI Server Mode section below.


 * Clichart is usually executed using the ``clichart`` wrapper script.
   The examples assume that you have used easy_install to install CLIChart, in which case the wrapper
   script is in your PATH.  However, clichart can also be executed directly,
   by replacing ``clichart`` with ``java -jar clichart-0.5.0.jar`` (assuming you're using version
   0.5.0).
 * PNG and JPEG image formats are supported for saving of charts, and these are determined based
   on the file extension, which must be .png, .jpg or .jpeg (case-insensitive).  PNG is recommended,
   as the image files are smaller, and the images are clearer
 * Arguments containing spaces must be quoted, e.g. with double quotes.  This is commonly required
   when setting chart or axis titles
 * On Windows, arguments containing colons must be quoted with double quotes.  This is commonly
   required for the -d option, e.g. ``-d "HH:mm:ss"``
 * Series titles for either axis are comma-separated, so the titles themselves cannot contain commas
 * If any two data points have the same X axis value, generation of the chart will fail
   (TODO: insert error message).  Timestamps are evaluated to the second, so timestamps
   must be at least 1 second apart.  Alternatively, use the ``-p`` option to ignore duplicate values


Examples
========

See the `quick start guide <quickstart.html>`_ for examples of using this tool.


CLI Server Mode
===============

If run in CLI server mode (with the ``--cliserver`` option), clichart reads commands from standard
in, and responds to each successful command by writing a line starting with 'OK' to standard
out.  This allows another program, script or batch file to drive clichart to produce any number
of charts, without the expense of launching clichart anew for each one.  To use this mode, you
must already have the tabular data available in files.

Each command consists of a command name and an optional argument, followed by a line ending
(LF or CRLF).  The command name is generally one of the options supported by clichart, either
the short or long form, with the following exceptions noted below.  Arguments for commands follow the
same rules as for the options, except that everything after the command name to the end of the
line is treated as the argument, so you shouldn't use quotes around multi-word arguments.

Differences between clichart options and CLI server commands are as follows:

 * The path to the input file for the next chart is specified using the ``inputPath`` command,
   which takes the path to the (tabular data) input file as its argument.  This is required
 * The ``outputPath`` command is also required
 * The ``go`` command forces generation of a chart using the current options
 * Options are retained after generating a chart, so generating the next chart only requires
   changing the options that should change.  The ``clear`` command resets the options to
   their defaults. **Note:** A number of clichart options do not have any way to reset them to their
   defaults other than using ``clear``.
 * Terminate the session by using the ``quit`` command, closing the calling program (e.g. using
   Ctrl-C), or closing the standard input stream
 * For debugging purposes, use the ``debug-echo`` command, which echos all commands received to
   standard error
 * Command names are not case-sensitive
 * Blank lines and lines starting with ``#`` are ignored
 * The following options cannot be used as commands: ``clichart``, ``h`` and ``help``
 * The ``timeout`` command sets a timeout (in seconds).  If the server does not get any commands
   within this time period, it will exit.  This is intended to make the server mode more robust
   when used in long-running processes.


How to use CLI Server Mode
--------------------------

There are several different options for using CLI server mode.  These include:

Saved command file
    Probably the simplest mode of operation.  Save all the required commands to a file, then
    pipe or redirect that file to clichart, e.g. ::

        $ clichart --cliserver < someCommands.txt

Shell scripts/batch files
    Write a shell script or batch file that generates the commands to be run, and pipe the output
    to clichart.  In this case, all output from clichart will appear on standard out, e.g. ::

        $ head myscript.sh
        #!/bin/sh
        INPUT_DIR=some/dir
        echo "inputFile $INPUT_DIR/someData.csv"
        echo "outputFile someData.png"
        echo go
        ...
        # myscript.sh | clichart --cliserver
        OK
        OK
        OK
        OK

Use cliserverlib to embed in a Python script
    The `cliserverlib.py Python library <cliserverlib.html>`_ provides a library to drive clichart in CLI
    server mode from a Python script.

Embed in a script or program in another language
    It's easy to write a driver for the CLI server mode in any other language, and to use that in your
    scripts or programs.  The `cliserverlib.py Python library <cliserverlib.html>`_ provides a useful
    example of how to do so.

Interactive via console
    You can also drive clichart via the console, which is useful for testing and debugging.  Start clichart using
    ``clichart --cliserver`` and type in every command line.  After each line hit Enter, and you should
    see clichart respond with a line starting with 'OK'.


Example CLI Server Session
--------------------------

Here's a sample transcript of a CLI server session.  You could run this in any of the ways listed above, but the
example shows interacting via the console.  Note that ``$`` is the shell prompt in the example below, and all
the ``OK`` lines are output by clichart, not entered by you.  There is no prompt while interacting with the
CLI server. ::

    $ bin/clichart --cliserver
    OK
    inputpath samples/SystemTemps.csv
    OK
    outputpath /home/johnd/tmp/SystemTemps1.png
    OK
    csv
    OK
    hasheader
    OK
    title This is a chart of system temperatures
    OK
    go
    OK
    quit
    $