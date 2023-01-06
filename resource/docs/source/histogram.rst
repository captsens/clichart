=========
histogram
=========

Introduction
============

Histogram is a Python script for generating a histogram (or frequency distribution) from
numeric columns in tabular data, such as CSV files.

A typical use for Histogram is showing the spread and frequency of server response times
for a particular function - the histogram gives a different view on the data than the
typical minimum/average/maximum data provided by Linestats.

Histogram can:

 * Accept data from stdin or from a file
 * Accept and output comma-separated or whitespace-separated data
 * Generate a histogram for any numeric column in tabular data
 * Construct the histogram with any user-specified number of intervals or interval size
 * Include percentages for each interval
 * Show histogram and percentage data as individual or cumulative
 * Ignore the first line of the file, e.g. when it contains column headings
 * Output a headings row if required.


Usage
=====

The output of the script will be 1 row of data for each of the number of intervals
specified, containing the start and end values for the interval, and the number of
data points falling within the interval.  Output is in the same format as the input,
i.e. whitespace or comma-separated.

You use the tool like this::

    histogram [options] [inputFile]

If no input file is specified, reads from stdin.  Output is always to stdout.

Either the number of intervals (-i) or the interval size (-s) is required.

Options:
--------------
::
    
 -c             Data (input and output) is CSV (default is
                whitespace-separated)
 -f             First row of file is a header row, and should be skipped
 -h             Show help (this information) and exit
 -i <intervals> The number of intervals over which the histogram should be
                generated (i.e. the number of output rows)
 -l <column>    The number of the column from which to generate the histogram
                (required).  The column index is the 0-based number of the
                column in the file.
 -m             Show cumulative interval values, rather than individual
 -p             Add a column showing the percentage for the interval
                (individual or cumulative)
 -s <size>      Size of interval (an alternative to -i)
 --header       Include a header row in the output
 --nojit        Disable Psyco Just-In-Time compiler


Notes:
======
 * Histogram requires Python - see `the installation page <installation.html>`_
 * The column specified with the ``-l`` option **must** be numeric


Examples
========

Using data from the samples directory, we can generate a histogram for case fan speed
(column 4, counting from 0), and dividing the data into 5 intervals: ::

    $ histogram -i 5 -l 4 samples/SystemTemps.txt
    2017 2108 5
    2108 2199 0
    2199 2290 2
    2290 2381 1
    2381 2472 9

The CSV version of the data also has a header row, which needs to be skipped.  We can also
add an output header.  This timie we'll do the CPU temperature (column 1), which is a floating
point number: ::

    $ histogram -i 5 -l 1 -cf --header samples/SystemTemps.csv
    Interval_Start, Interval_End, Count
    57.8000, 59.8800, 4
    59.8800, 61.9600, 1
    61.9600, 64.0400, 0
    64.0400, 66.1200, 2
    66.1200, 68.2000, 10

To chart this using clichart, we'd do something like this (note the -v flag, since the x axis is
based on values rather than timestamps): ::

    $ histogram -i 5 -l 1 -cf samples/SystemTemps.csv | clichart -l 1,2 -cbv
