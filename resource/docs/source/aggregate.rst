=========
aggregate
=========

Introduction
============

Aggregate is a Python script for extracting aggregate (or summary) data from
numeric columns in tabular data, such as CSV files.

A typical use for Aggregate is generating data to show long-term trends, where
you already have a number of tabular data files covering shorter periods.  For
example, we use Aggregate to extract day-by-day transaction rates and total volumes, memory
and thread use etc. from daily stats for a number of servers - this allows a
quick glance to pick up any anomaly that should be investigated further.

Aggregate can:

 * Accept data from stdin or from one or more files
 * Accept and output comma-separated or whitespace-separated data
 * Output minimum, maximum, average, total, count, standard deviation, first or last
   value for any numeric column
 * Ignore the first line of the file, e.g. when it contains column headings
 * Output one aggregate row per file, or one per key/group value.


Usage
=====

The output of the script will be 1 row of data for each input file (or only one row 
if stdin is used).  Alternatively, if a key (aka group) is specified, there will be 
one row per key.  Output is in the same format as the input, i.e. whitespace
or comma-separated.

You use the tool like this::

    aggregate [options] [inputFile*]

If no input file is specified, reads from stdin.  Output is always to stdout.

Options:
--------------
::
    
 -c           Data is CSV (default is whitespace-separated)
 -f           First row of file is a header row, and should be skipped
 -h           Show help (this information) and exit
 -l <columns> A comma-separated ordered list of columns and aggregate types
              to include in the output (required).  See below
 -p           A prefix column value to write in the aggregate columns for each
              line.  Pass 1 or more of these, e.g. to provide a date column
              value.  Example: -p 12/03/2007
 -s           If no rows of data were found, silently output nothing
 -x           A suffix column value to write in the aggregate columns for each
              line.  Pass 1 or more of these, e.g. to provide a date column
              value.  Example: -x 12/03/2007
 --nojit      Disable Psyco Just-In-Time compiler.

Column indexes
---------------

The column index is the 0-based number of the column in the file, while the
aggregate type is 'min', 'av', 'max', 'tot', 'cnt', 'sd' (standard deviation),
'first' or 'last'.  If the type is 'k', this column is treated as a key, and
output is grouped by the values in the column. Note that the count is just the
number of lines in the file, not counting any header row.  E.g.::

    1:max,2:av,2:max,0:cnt,4:tot,0:sd

Negative column indexes can be used, with -1 representing the last column etc.
E.g.::

    1:max,-1:tot

Simple expressions can also be used. To indicate negation rather than a
negative column index, you must separate the minus sign and the column index
with a space.  E.g.::

    '1:max / 3:cnt / 60', '1:tot - 3:tot', '2:av - -1:av'


Notes:
======
 * Aggregate requires Python - see `the installation page <installation.html>`_
 * Every column specified with the ``-l`` option **must** be numeric
    
    - The only exception to this is columns specified as keys - these are treated
      as strings
      
 * Key columns can be specified anywhere in the column list, but will always be
   output at the start of each row (but in the order specified)
 * When key columns are specified, output is sorted in ascending order by the key
   columns (as strings)
 * Expressions can use:

    - Any column aggregate, e.g. '3:av'
    - Integer and floating point numbers
    - Standard mathematical operators: '+', '-', '*', '/', as well as '%'
      (remainder after integer division)
    - Standard Python functions (if you know some Python).  However, you won't
      be able to use any function requiring more than one argument, since the
      comma is used to separate the column expressions.

 * Expressions must return a single numericial value.


Examples
========

Using data from the samples directory, we can extract the minimum, average and maximum
CPU temperature and fan speed: ::

    $ aggregate -cfl 1:min,1:av,1:max,3:min,3:av,3:max samples/SystemTemps.csv
    57.80, 64.73, 68.20, 2376, 2448, 2510

If we're only interested in what the values were at the start and end of the day: ::

    $ aggregate -cfl 1:first,1:last,3:first,3:last samples/SystemTemps.csv
    58.50, 65.40, 2376, 2471

And if we want the date as the first column in the output: ::

    $ aggregate -cfl 1:first,1:last,3:first,3:last -p 12/03/2007 samples/SystemTemps.csv
    12/03/2007, 58.50, 65.40, 2376, 2471
    
Alternatively to generate aggregates keyed on (grouped by) one of the columns: ::

    $ aggregate -cfl 3:k,1:min,1:av,1:max samples/SystemTemps.csv
    2376, 57.8000, 58.8600, 61.5000
    2451, 66.1000, 66.6500, 67.2000
    2471, 65.4000, 66.9429, 67.9000
    2510, 68, 68.0667, 68.2000
   
    