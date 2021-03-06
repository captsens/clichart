========================================
Installing CLIChart - Quick Start Guide
========================================

This is a quick and dirty guide to installing CLIChart.  You can find the full documentation in 
``docs/txt/installation.rst`` (text) and ``docs/html/installation.html`` (HTML, surprisingly enough).

Step 0: Make sure you have Java (e.g. version 7) and Python 2.x (e.g. 2.7) installed.

Step 1: Open a command prompt, as a user who is able to install software (e.g. root on a Unix/Linux system).
Alternatively, use ``sudo`` in the following steps.

Step 2: Ensure that you have the easy_install program, by typing ``easy_install -h``.  This should show a 
usage screen.  If the program is not found, install it from `<http://pypi.python.org/pypi/setuptools>`_

Step 3: Change to the top-level CLIChart directory you just unzipped, and install CLIChart by typing 
``easy_install clichart-x.y.x.egg``, where the latter is the name of the (single) .egg file in the directory.

You should now be done.  Look at the documentation to find out how to use CLIChart.
