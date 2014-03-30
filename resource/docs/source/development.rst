=====================
Developing CLIChart
=====================

CLIChart needs your help!
=========================

I've been developing CLIChart for many years now, but for the last couple of years I've been
using it less and less.  And when I do use it, it already does most of the things I want. 
Therefore I don't have any intention to do much more (if anything) with the project.

So, if you want CLIChart to continue and/or advance, please get involved!


Getting the source
==================

Go to the project page on Github.  You could just clone the git repo directly, but then you won't be
able to submit changes, so better to fork the project and then clone your fork - that way you'll
be able to submit pull requests in future.

Contributing your changes
=========================

Once you've made some changes that you think will work (and you've run through at least most of
the checks in ``/DistChecklist.txt``), please submit a pull request on Github.

In future, if anyone's interested in becoming a long-term maintainer, I'm happy to pass on the reins.

Project structure
=================

Main parts are:

- root directory - ant build script (``build.xml``), ant ``build.properties`` (if you need one),
  ``setup.py`` (for setuptools install), plus some text files
- ``/bin`` - shell and python scripts for building and packaging
- ``/resource/docs`` - documentation root (uses Sphinx/reST)
- ``/resource/lib`` - Jar files needed for clichart
- ``/resource/samples`` - sample files for demonstrating capabilities
- ``/src/main/java`` - Java source for clichart tool
- ``/src/main/python`` - source for all tools (including clichart wrapper)
- ``/src/test/java`` 
- ``/src/test/python`` 
- ``/target`` - build directory.

Building
========

Follow all the steps in ``/DistChecklist.txt``.  This will ensure that your development environment
is set up OK, and then walk you through all the steps to build, test,  and package.

Note that you may need to create a ``/build.properties`` file for ant, particularly to set 
a path for cobertura.

Documentation
=============

Documentation is set up on readthedocs.org, with a webhook from the git repo on Github to automatically
rebuild the documentation when published.

Special notes about the documentation:

- You have to update both the version and release numbers in the Sphinx conf.py file for a new
  release
- The documentation builds initially to ``/resource/docs/build`` - the files are then copied from 
  there to the ``/target`` directory
- Because the documentation build uses Sphinx, there's no easy way (that I've found, at least)
  to have the thumbnail images updated automatically and copied in.  Therefore, the 
  ``/bin/buildDoco.sh`` script (re)generates thumbnails if required, in the 
  ``/resource/docs/source/_static/images`` directory.  **This means that running 
  ``/bin/buildDoco.sh`` may require a git commit afterwards** (but it should tell you if so)

