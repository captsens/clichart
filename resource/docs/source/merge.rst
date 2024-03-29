=====
merge
=====

Introduction
============

Merge is a Python script for merging tabular data from 2 or more files, based
on common key values.  Its most common use is when you have data from several sources
which you would like to merge in order to display on the same chart.

All merging is based on the values in the key column of each file - these values are
frequently dates or times for time-based data.  The output will consist of 1 row for
each distinct key value found in any of the files, with 1 column for the key plus any
columns you specified from each of the files.  Column cells for which no value is
available, i.e. where the key value was not found in all files, default to an empty
string, although this can be controlled using the ``--defaults`` option.

Note that keys are treated as text, so for instance '7' and '7.0' are treated as different.

Merge can:

 * Merge data from 2 or more files
 * Accept data in CSV or whitespace-separated formats.  Output is in the same format
   as the input
 * Treat the first row in each file as a header
 * Employ user-specified default values for any column.


Usage
=====
You use the tool like this::

  merge [options] inputFile*

Output is always to stdout.

Options:
--------
::

 -c             Data is CSV (default is whitespace-separated)
 -f             First row of file is a header row, and should be skipped
 -h             Show help (this information) and exit
 -l <columns>   A comma-separated list of fileNumber:columnNumber to be
                included in the output, for all non-key columns (the key column
                will be output at the start of each line).  Both file and
                column numbers are 0-based, e.g. "0:1,0:2,1:1,2:4"
 -k <columns>   A comma-separated list of fileNumber:columnNumber specifying
                the key column for each file.  There must be 1 entry per file,
                e.g. "0:0,1:0,2:1"
 --defaults     A comma-separated list of the default values for all non-key
                columns, in the same order as the -l option list.  If omitted,
                values default to the empty string
 --nojit        Disable Psyco Just-In-Time compiler


Notes:
======
 * Merge requires Python - see `the installation page <installation.html>`_


Example:
========

Merges two CSV files, where the key is the first column in each file.  Output the second and
third columns from each file. ::

  merge -k 0:0,1:0 -l 0:1,0:2,1:1,1:2 -c file1.csv file2.csv

