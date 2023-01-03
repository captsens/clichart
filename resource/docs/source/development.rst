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

- root directory - gradle build script (``build.gradle``) and properties file 
  (``settings.gradle``
  ``setup.py`` (for setuptools install), plus some text files
- ``/bin`` - a couple of miscellaneous scripts for building and packaging
- ``/resource/docs`` - documentation root (uses Sphinx/reST)
- ``/resource/samples`` - sample files for demonstrating capabilities
- ``/src/main/java`` - Java source for clichart tool
- ``/src/main/python`` - source for all tools (including clichart wrapper)
- ``/src/test/java`` 
- ``/src/test/python`` 
- ``/build`` - build directory.

Building
========

**IMPORTANT NOTE:** 

    Clichart is set up to build on *nix systems (Linux, OSX etc.).  The build can be
    made to work on Windows under Cygwin, but you'll probably have quite a bit of work 
    to do if you try to do it on Windows directly...

The quick version: ::

    # start in the project's root directory
    gradle dist                 # builds and zips everything
    gradle installForTest
    . build/install-test/virtualenv/bin/activate
    bin/testInstall.py
    deactivate

The longer version is documented in ``/DistChecklist.txt``.  This will ensure that your development environment
is set up OK, and then walk you through all the steps to build, test, and package.

Mandatory Build Tools
---------------------

:git: Of course
:Java JDK:
:Gradle: Build system
:Sphinx: Documentation builder.  Check for ``sphinx-build``
:Python setuptools: Provides packaging and installer.  Check for ``easy-install``
:Python wheel: Works with setuptools to build a Python wheel (bundle)

Recommended Build Tools
-----------------------

The following tools are used in standard build targets, or are useful for checking results.

:Python nosetest: Test runner for unit tests
:Python coverage: Code coverage tool for Python, integrates with/called via nosetest
:Python virtualenv: Provides virtualised Python environments, used for test installs
:ImageMagick convert: Used to generate thumbnails of charts for documentation
:Linkchecker: Useful tool to check links in documentation

Documentation
=============

Documentation is set up on readthedocs.org, with a webhook from the git repo on Github to automatically
rebuild the documentation when published.

Special notes about the documentation:

- You have to update both the version and release numbers in the Sphinx conf.py file for a new
  release.  The gradle build script will fail the build if this isn't done
- The documentation builds initially to ``/resource/docs/build`` - the files are then copied from 
  there to the ``/build`` directory
- Because the documentation build uses Sphinx, there's no easy way (that I've found, at least)
  to have the thumbnail images updated automatically and copied in.  Therefore, the 
  gradle build script (re)generates thumbnails if required, in the 
  ``/resource/docs/source/_static/images`` directory.  **This means that running a build
  may require a git commit afterwards**.

