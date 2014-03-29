====================
Changes to CLIChart
====================

This file documents the changes made in each release of CLIChart.

Version 0.5.9
---------------

Issued 2014-03-25

 * General

    - Moved to github, with documentation hosted by readthedocs
    - Changed documentation to use Sphinx
    - General build improvements

 * clichart

    - Changed clichart.py launcher to work better on Windows, particularly quoting of command
      line options containing spaces, and display of usage screen


Version 0.5.8
-------------

Issued 2012-11-01

 * aggregate

    - Added option to append suffix values to the output


Version 0.5.7
-------------

Issued 2012-06-23

 * histogram

    - Added option for cumulative values
    - Added option for fixed size intervals
    - Added option to show percentage for each interval (individual or cumulative)


Version 0.5.6
---------------

Issued 2012-05-27

 * clichart

    - Added options for minimum values on y axes
    - Added options to force use of provided minimum and maximum values - this allows generation of
      charts which always have the same scale, no matter what data they show


Version 0.5.6b2
---------------

Issued 2012-04-12

 * aggregate

    - Added support for negative column indexes, allowing use of files with arbitrary numbers of columns.


Version 0.5.6b1
---------------

Issued 2011-03-26

 * clichart
 
    - Fixed finding Jar file on Windows when cygpath is installed - prevented launching the Java app
    

Version 0.5.6a7
---------------

Issued 2010-12-10

 * aggregate
 
    - Initial support for keyed (grouped) aggregation - output is one row per key (group) per file.
    

Version 0.5.6a6
---------------

Issued 2010-08-31

 * General
 
    - The executable parts of CLIChart are now contained within an 'egg' file, which uses ``easy_install`` 
      for installation.  Installation is as simple as ``easy_install clichart-x.y.z.egg``.  All files are installed 
      under the Python ``site-packages`` directory
    - All scripts (``clichart``, ``linestats``, ``mid`` etc.) are now installed in the system path, so can be 
      executed without specifying their path or adding them to the PATH environment variable
    - All scripts are now executed **without** a ``.py`` suffix, i.e. you now type ``mid`` rather than ``mid.py``
    - All Python files, including ``cliserverlib``, are now automatically added to the system's PYTHON_PATH in
      the ``clichart`` package.  This makes it much easier to use CLIChart as a library

 * clichart/cliserver

    - The old ``clichart`` shell script and ``clichart.bat`` batch file have been replaced with a single Python
      script.  This is accessed as ``clichart`` on the system path


Version 0.5.6a5
---------------

Issued 2010-07-14

 * clichart/cliserver

    - Improved debug logging from TCP/IP server
    - BUG: Fixed memory leak caused by system exit timer under TCP/IP server


Version 0.5.6a4
---------------

Issued 2010-07-06

 * clichart/cliserver

    - First attempt at supporting a TCP/IP server mode.  Start clichart with --port set to an appropriate
      port to listen on, and use the 'port' constructor parameter in cliserverlib.ClichartDriver


Version 0.5.6a3
---------------

Issued 2010-06-07

 * clichart/cliserver

    - Added the 'timeout' command, which forces the server to exit if no command is received within that
      number of seconds


Version 0.5.6a2
---------------

Issued 2009-07-15

 * clichart/cliserver

    - Clichart now allows integers up to 2^63 (long), rather than the 2^31 (integer) previously accepted


Version 0.5.6a1
---------------

Issued 2009-06-30

 * clichart/cliserver

    - BUG: Clichart would previously ignore empty cells at the end of the line if --ignoreempty
      was set

 * merge

    - A new script which allows merging keyed data from 2 or more files


Version 0.5.5
-------------

Issued 2009-06-27 (and not before time :)

 * aggregate

    - Now also generates standard deviations

 * clichart/cliserver
 
    - Now allows skipping empty data columns (typically from CSV data), using ``--ignoreempty`` option


Version 0.5.5rc1
----------------

Issued 2008-09-21

 * general
 
 	- Clichart now requires Java 1.5 as a minimum
 	
 * clichart/cliserver
 
 	- Now allows overriding of default line colours, using ``--colours`` option


Version 0.5.4
-------------

Issued 2008-06-15

 * aggregate

    - BUG: Result values with 5 or more digits were sometimes incorrectly formatted

 * cliserver

    - BUG: Exception from JFreeChart (e.g. duplicate data item) no longer causes stack trace with
      no response in cliserver mode, but instead returns an error message

 * histogram

    - A new script for generating histograms to show data frequency.


Version 0.5.3
-------------

Issued 2007-06-11

 * general

    - All Python scripts now use psyco (if available), to speed up processing, with --nojit option
      to disable
    - BUG: CSV output for fields containing double quotes without spaces did not escape the quotes

 * discretestats

    - Value headings are now sorted alphabetically on output
    - BUG: removed trailing white space at end of line for text output

 * linestats

    - BUG: Default keyspec (if no -k option) missed the first character in the line
    - BUG: removed trailing white space at end of line for text output

 * documentation

    - Added FAQ
    - Added FAQ entry on Windows bug piping data to Python scripts
    - Changed documentation to distinguish between CLIChart (the project) and clichart (the tool).


Version 0.5.3-rc1
-----------------

Issued 2007-06-03

 * cliserver

    - BUG: Fixed NullPointerException if input file consists only of partial headers
    - BUG: Fixed exception if input file does not contain sufficient headers

 * cliserverlib

    - Added ability to locate clichart via the PATH (needed for Windows, since no
      symlinks are available)

 * aggregate

    - Added ability to specify simple expressions for columns, e.g. '1:tot / 60'
    - Improved messages for typical errors (and without stack traces, even :)
    - BUG: Fixed embarassing error reading data from stdin
    - Added an example of using aggregate to the quick start documentation.


Version 0.5.2
-------------

Issued 2007-04-22

 * cliserverlib

    - Previously failed to read responses under Python 2.2, due to use of a 2.3 API

 * discretestats

    - Improved error messages for invalid options and data

 * linestats

    - Improved error messages for invalid options and data

 * Documentation updates.


Version 0.5.2-rc1
-----------------

Issued 2007-04-14

 * clichart

    - Added a CLI server mode (using --cliserver), where clichart is controlled by
      commands from stdin.  This allows clichart to be used efficiently when many charts
      are to be generated, since the program need only be started once.  Input can come
      from a command file, from a script or program (see cliserverlib for Python programs),
      or (for debugging) via an interactive telnet-like session
    - Added seriestitles and seriestitles2 options, to allow setting/overriding of data
      series titles for display in the legend
    - Message about saving chart (to stderr) now only prints if saved from GUI.

 * cliserverlib

    - A new Python driver library for interacting with the new CLI server mode of clichart.
      Makes it very easy and efficient to generate charts based on tabular data files from
      Python scripts.

 * aggregate

    - A new script for extracting aggregate data from 1 or more tabular data files. This
      script can output any or all of the following data for any numeric column in the file:
      minimum, maximum, average, total, count, first value or last value.
      Mostly used to summarise data from many files, e.g. for generating long-term trend
      charts from day-by-day data files.

 * Documentation updates.


Version 0.5.1
-------------

Issued 2007-04-06

 * clichart

    - BUG: shell script failed to follow relative symlinks that were not in the current
      directory
    - Minor documentation update


Version 0.5.1-rc1
-----------------

Issued 2007-04-01 (don't read anthing into this...)

 * clichart

    - Added the ability to draw first or second Y axis as a bar chart (histogram)
    - Added the ability to control line/bar weights (widths), and draw shapes to
      indicate data points
    - Significant new functionality on interactive chart window (courtesy of JFreechart):

        - Tooltips to show data points
        - Popup context menu
        - Ability to customise most aspects of chart via GUI (but not second Y axis)
        - Zoom in/out

    - Restructured Main class to provide a better API for using clichart as a library
    - Upgrade to JFreechart 1.0.5
    - BUG: Second axis previously always started at 0
    - BUG: clichart.bat didn't lauch clichart, as the Jar filename was incorrect
    - BUG: clichart.bat didn't correctly interpret JAVA_HOME (I just *love* batch files...)


Version 0.5.0
-------------

Issued 2007-03-18

The final release of version 0.5.0.  Changes made since -rc3:

 * clichart

    - Removed output on duplicate lines ignored
    - BUG: in scripted mode, always saved as JPG even if filename was PNG
    - BUG: failed to generate chart if running headless (i.e. no X display on Linux/Unix)
    - BUG: readlink options in shell script were not valid on older versions of Linux/Cygwin

 * Documentation updates and corrections


Version 0.5.0-rc3
-----------------

Issued 2007-03-11

This is the first public release of clichart.  It's a rewrite in Java of the
original Jython version, and has the following main features:

 * Displays charts in a window (with option to save), or non-interactive saving to disk
 * Accepts data in comma- or whitespace-separated formats
 * Reads data from a file, or piped into its standard input
 * Displays XY line charts, with the X axis based on dates, times or values.  The
   Y axis must be simple values (integer or decimal).

In addition, it's packaged with:

 * linestats, which generates grouped summary statistics (count, minimum, average,
   maximum, total) from line-based textual data, and
 * discretestats, which generates grouped counts of discrete field values from
   line-based textual data.
 * mid, which extracts lines of data from files, like a combination of head and tail

