==========================
Frequently-Asked Questions
==========================

General
=======

How can I make CLIChart run faster?
-----------------------------------

a) Install Psyco to speed up Python scripts
+++++++++++++++++++++++++++++++++++++++++++

`Psyco <http://psyco.sourceforge.net/>`_ is a Just-In-Time compiler for Python.  The CLIChart
Python-based tools (linestats, discretestats, mid and aggregate) will use psyco if it's
installed, and this should speed up their operation when larger amounts of data are involved.

b) Use a Later Version of Java
++++++++++++++++++++++++++++++

The main clichart program runs under Java.  Later versions of Java are likely to make it start and/or run
faster.  Java 1.4 is particularly slow - if you're still on 1.4, you should think very seriously
about upgrading.


Running Under Windows
=====================

Why do I get an 'invalid file descriptor' error when piping data to a tool?
---------------------------------------------------------------------------

If you get a stack trace (complaining about an 'invalid file descriptor') when piping
data into one of the Python-based tools (linestats, discretestats, mid or aggregate), this is
due to a bug present in some versions of Windows.

The bug presents when you pipe data into a script that was launched based on a file extension, so
for example it occurs in the first case below but not the second.  Note that you must have
``python.exe`` in your PATH for the second example to work: ::

    rem This may fail, depending on the Windows bug
    ... | aggregate.py

    rem This should always work (if python.exe is in your PATH)
    ... | python aggregate.py

See http://support.microsoft.com/kb/321788 for details of the bug

