=============
discretestats
=============

Introduction
============

Discretestats is a Python script for generating summary statistics from
lines of textual data containing a field with discrete values, such as a system log file.
It is intended to by used to extract summary data from input, which is then piped to clichart for
graphical display.

Discretestats is based on the idea of extracting a 'key' field and a 'value' field from each line
of data.  The most common key field is a timestamp, while the value field can be anything that
has discrete values.  The output can be thought of as a table - one row for each key field value, and
one column for each value field value, with each cell containing the count that value and key.  A
typical example is generating statistics on the number of error and warning messages in a system
log per minute.

The differences between discretestats and linestats are:

 * Any value fields in linestats must be numbers (since you're interested in averages, minima etc).
   The field in discretestats can be anything (but it's usually a string)
 * Discretestats tells you how often each **distinct value** occurs for each key, whereas using a
   count in linestats just shows how many times the key occurs
 * In linestats, you specify exactly the columns to be output.  In discretestats you don't
   always know how many columns there will be, since there's a column for each discrete value.

Discretestats can:

 * Accept data from stdin or from a file
 * Identify the key field based on whitespace-separated fields, a substring, or a regular
   expression
 * Accumulate counts for each discrete value in a single value field
 * Identify the value field based on whitespace-separated fields, a substring, or a regular
   expression
 * Output counts for discrete value in thevalue field, for each value
   in the key field, e.g. for every unique timestamp
 * Ignore input lines that do not contain a supplied regular expression
 * Sort the output by the key field
 * Output as comma-separated or whitespace-separated
 * Quote column headings and keys containing spaces and/or commas.


Usage
=====

You use the tool like this::

    discretestats.py [inputOptions] [outputOptions] [inputFile]

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
 -v <valuespec> Required.  Specifies a value for which to accumulate statistics
              from each line.  valuespec may be substring, field or regex
              as for -k option.

Output Options:
---------------
::

 -c           Output as CSV (default is whitespace-separated)
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
 * Each is index is 0-based (i.e. numbers from 0)
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
`the Python regular expression documentation <http://docs.python.org/library/re.html#re-syntax>`_
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
 * Discretestats requires Python - see `the installation page <installation.html>`_


Examples
========

See the `quick start guide <quickstart.html>`_ for examples of using this tool.


