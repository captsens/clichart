=========
linestats
=========

Introduction
============

Linestats is a Python script for generating summary statistics from
lines of textual data, such as a system log file.  It is intended to be used
to extract summary data from input, which is then piped to clichart for
graphical display.

Use linestats when you have more lines in the input than you need in the output data.
Linestats provides various ways to summarise data that share the same 'key field'.
All summary values (count, minimum, average, maximum etc.) are grouped based on the
value of that key field.

When parsing system logs, the most common use for linestats is to summarise data by time.
In this case the key field is part or all of the timestamp in each log message - in this
scenario, linestats will output one line of statistics (whichever statistics you choose)
for each unique timestamp.

Linestats can:

 * Accept data from stdin or from a file
 * Identify the key field based on whitespace-separated fields, a substring, or a regular
   expression
 * Accumulate statistics for zero or more value fields
 * Identify value fields based on whitespace-separated fields, a substring, or a regular
   expression
 * Output counts for each value in the key field, e.g. for every unique timestamp
 * Output minimum, average, maximum and/or total values for each value field, for each value
   in the key field, e.g. for every unique timestamp
 * Ignore input lines that do not contain a supplied regular expression
 * Sort the output by the key field
 * Output as comma-separated or whitespace-separated
 * Include a supplied column heading line in the output, for generating legends in clichart.


Usage
=====

You use the tool like this::

    linestats [inputOptions] [outputOptions] [inputFile]

If no input file is specified, reads from stdin.  Output is always to stdout.

Input Options:
--------------
::

 -h           Show help (this screen) and exit
 -k <keyspec> Specifies how to extract a key from each line (default is to use
              the whole line).  keyspec can be:
    s:<substring>  Extract key as a substring.  See Substrings
    f:<index>      Extract key as a field.  Fields are separated by white
                   space, 0-based
    r:<regex>      Extract key as a regular expression
 -m <regex>   Only include lines matching this regular expression
 -v <valuespec> Specifies a value for which to accumulate statistics from each
              line.  Zero or more.  valuespec must return a numeric value, and
              may be:
    s:<substring>  Extract value as a substring.  See Substrings
    f:<index>      Extract value as a field.  Fields are separated by white
                   space, 0-based
    r:<regex>      Extract value as a regular expression

Output Options:
---------------
::

 -c           Output as CSV (default is whitespace-separated)
 -f <line>    Output this line first, as the headers for the columns
 -l <columns> A comma-separated ordered list of columns to include in the
              output (default is 'k,k:cnt').  The field index is the 0-based
              number of the field in the list of -v options, i.e. field number
              0 is the field specified by the first -v option.  Columns may be:
    k         The key
    k:cnt     The count for the key
    0:av      The average of field '0'
    0:min     The minimum of field '0'
    0:max     The maximum of field '0'
    0:tot     The total of field '0'
 -s           Sort output by key column

Other Options:
--------------
::

 --nojit      Disable Psyco Just-In-Time compiler


Substrings:
-----------

Substrings are specified by one or two indexes into the line.

 * Substring indexes must be separated by a colon (:)
 * The substring is taken from the first index (inclusive) to the second index
   (exclusive)
 * Each index is 0-based (i.e. numbers from 0)
 * Negative indexes count from the end of the line, i.e. -1 refers to the last
   character in the line
 * If no second index is given, the substring is taken from the first index to
   the end of the line.

Examples (with 's:' prefix): ::

  s:0:5     Extract the first 5 characters from each line
  s:32:     Extract from the 33rd character to the end of the line
  s:10:-8   Extract from the 9th character to the 9th-last


Regular Expressions:
--------------------

Regular expressions are mainly used to extract key or value fields from lines,
although they are also used for the -m (match) option.

Regular expressions follow the Perl 5 syntax as implemented by Python (NOT grep/egrep!).  The main
difference is that the `*` and `+` operators are greedy by default - if you
want the egrep behaviour, append `?` to them, e.g. change `ab.*yz` to
`ab.*?yz`.  See
`the Python regular expression documentation <http://docs.python.org/2/library/re.html>`_
for full information.

When the regex is used to extract a value, if it contains a bracketed group the
value returned for the group is used - otherwise, the entire match is used.
E.g. ``thread count: ([0-9]+)`` will return the number matched by the bracketed
group, while ``thread count: [0-9]+`` will return the entire string that
matches.

Note that you must quote or escape special characters to prevent the shell from
interpreting them, typically with single quotes.

Examples (with 'r:' prefix): ::

  'r:^\d\d:\d\d'  Extract the first 5 characters, which must be in the form 99:99
  'r:A:(\d+)'     Find the string 'A:' followed by 1 or more digits, and return
                  the digits


Notes:
======
 * Linestats requires Python - see `the installation page <installation.html>`_


Examples
========

See the `quick start guide <quickstart.html>`_ for examples of using this tool.


