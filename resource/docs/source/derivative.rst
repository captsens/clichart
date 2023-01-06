=============
derivative
=============

Introduction
============

Derivative is a simple Python script for calculating derivatives (rate of change with time) from
lines of tabular data (text or CSV) containing both timestamps and fields with numeric values.  A typical use is to plot the rate in change of fields of interest.

It is intended to by used to extract summary data from input, which is then piped to clichart for
graphical display.

**NOTE:**
This tool is very basic, and is currently only able to calculate *differences per second*.


Usage
=====

You use the tool like this::

    derivative [inputOptions] [inputFile]

If no input file is specified, reads from stdin.  Output is always to stdout.

Input Options:
--------------
::

 -h           Show help (this screen) and exit
 -c           Data is CSV (default is whitespace-separated)
 -d fmt       Date/time format in PYTHON format (default %H:%M)
 -f           First row of file is a header row, and should be skipped
 -h           Show help (this information) and exit
 -l <columns> A comma-separated ordered list of columns for which derivatives are required
              (0-based)
 --date-column <column>  Date/time column number (0-based, defaults to 0)

Other Options:
--------------
::

 --nojit      Disable Psyco Just-In-Time compiler


Examples
========
::

    $ cat inFile.csv
    10:23, 58.5, 47.3, 2376, 2020
    10:24, 57.8, 47.2, 2376, 2017
    10:25, 58.1, 47.4, 2376, 2018
    10:26, 58.4, 47.3, 2376, 2020
    $ derivative -c -l 1,2,3,4
    10:24, -0.0117, -0.0017, 0, -0.0500
    10:25, 0.005000, 0.003333, 0, 0.016667
    10:26, 0.005000, -0.0017, 0, 0.033333


