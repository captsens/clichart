"""
Useful functions for unit testing python code
"""

import os, sys

# ===================================================================================
def checkPythonPath(moduleName):
    """If an import of dbmstools fails, add in the appropriate directory to sys.path"""
    try:
        __import__(moduleName)
    except:
        testDir = os.path.dirname(__file__)
        # add the src/main/python directory to the python path
        sys.path.append('%s/../../../src/main/python' % testDir)

