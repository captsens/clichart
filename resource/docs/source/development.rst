=====================
Developing CLIChart
=====================

**An early work in progress...**

- Source on Github
- Follow DistChecklist.txt
- Building is currently carried out using:

	* Ant (for the Java code)
	* Sphinx (for documentation)
	* A collection of shell and python scripts (in the bin directory) for everything else.

Particular items to note:

- Run bin/checkDevPrereqs.py to ensure you've got all the tools
- You may need to create a ``build.properties`` file for for Java, particularly to set a path for
  cobertura
- Clichart is a Java program, but executed by Python script
- All other tools are python
- Packaging/install using easy_install from python setuptools
- Sphinx, and automatic update in readthedocs?
