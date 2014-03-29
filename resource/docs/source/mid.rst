===
mid
===

.. contents::

`Return to main CLIChart documentation page <index.html>`_.


Introduction
============

Mid is the steroidally-enhanced child of the venerable Unix head and tail utilities.
It's a Python script for extracting ranges of lines from a file (or stdin) in the most
convenient way possible.

You can use mid to strip out lines from text input prior to passing it to linestats or
discretestats, and/or to strip out lines from tabular data prior to passing it to
clichart.  The most common uses are to process only data from a selected time period
in a system log, or to remove lines of tabular data that cause problems for clichart.

Mid can:

 * Accept data from stdin or from a file
 * Extract one or more line ranges from the input
 * Accept line indexes as positive numbers (counting from the start of the file), and
   as negative numbers (counting backwards from the end of the file).


Usage
=====

You use the tool like this::

    mid.py [options] range [range *] [inputFile]

If no input file is specified, reads from stdin.  Output is always to stdout.

Options:
--------
::

 -h           Show help (this screen) and exit

Ranges:
-------

At least one line range is required.  Ranges take the form::

  <startLine>:<endLine>

Line numbers are 1-based, i.e. the first line in the file is line 1, and both
the start and end line numbers are **inclusive**, i.e. both the start and end
lines are included in the output.  Negative line numbers count backwards from
the end of the file, with -1 being the last line.

Examples:
---------
::

 10:20          All lines from 10 to 20
 2:-1           All but the first line of the file
 -10:-1         The last 10 lines of the file
 1:1 -1:-1      Just the first and last line of the file


Notes:
======
 * Mid requires Python - see `the installation page <installation.html>`_


