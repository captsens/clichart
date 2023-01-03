========================================
Installing CLIChart - Quick Start Guide
========================================

This is a quick and dirty guide to installing CLIChart.  You can find the full documentation in 
``docs/txt/installation.rst`` (text) and ``docs/html/installation.html`` (HTML, surprisingly enough).

Step 0: Make sure you have Java (e.g. version 11) and Python 3.x (e.g. 3.9) installed.

Step 1: Open a command prompt, as a user who is able to install software (e.g. root on a Unix/Linux system).
Alternatively, use ``sudo`` in the following steps (on a Mac there's no need to install globally - it'll
just install for the current user).

Step 2: Ensure that you have the pip3 program, by typing ``pip3 -h``.  This should show a
usage screen.  If the program is not found, install it using ``TODO``.

Step 3: Change to the top-level CLIChart directory you just unzipped, and install CLIChart by typing 
``pip3 install clichart-x.y.z-py3-none-any.whl``, where the latter is the name of the (single) .whl file in the directory.

You should now be done.  Look at the documentation to find out how to use CLIChart.
