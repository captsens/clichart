#!/usr/bin/env python3

"""
(C) Copyright 2007-2014, by John Dickson

Project Info:  https://github.com/captsens/clichart

This library is free software; you can redistribute it and/or modify it
under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation; either version 2.1 of the License, or
(at your option) any later version.

This library is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
USA.


Launches clichart, passing all command-line arguments to it.

Checks for JAVA_HOME, or if not set assumes that 'java' is in the path
"""

import sys, os, glob, platform
from subprocess import Popen, PIPE, call

JAVA = 'java'
JAR_PATTERN = 'clichart-*.jar'

# -----------------------------------------------------------------------------------
def main():
    javaCommand = findJava()
    clichartJar = findClichartJar()
    
    
    # if running headless, tell Java to use AWT for fonts
    args = [javaCommand]
    if shouldRunHeadless():
        args.append('-Djava.awt.headless=true')
    
    args = args + ['-jar', clichartJar] + sys.argv[1:]
    if isWindows():
        runWindows(javaCommand, args)
    else:
        runPosix(javaCommand, args)

# -----------------------------------------------------------------------------------
def runWindows(javaCommand, args):
    """execvp doesn't work properly on windows :( """
    call(args)

# -----------------------------------------------------------------------------------
def runPosix(javaCommand, args):
    # replace this process with Java
    os.execvp(javaCommand, args)

# -----------------------------------------------------------------------------------
def shouldRunHeadless():
    # TODO - allow an envvar to override, so can run headless on Mac
    if isWindows() or isMac():
        return False
    return os.environ.get('DISPLAY') is None

# -----------------------------------------------------------------------------------
def findJava():
    javaHome = os.environ.get('JAVA_HOME')
    if not javaHome:
        return JAVA
    javaExe = os.path.join(javaHome, 'bin', 'java')
    if isWindows():
        javaExe = javaExe + '.exe'
    if os.path.exists(javaExe):
        return javaExe
    else:
        print('Ignoring JAVA_HOME env var - does not exist', file=sys.stderr)
        print('  [%s]' % javaExe, file=sys.stderr)
        return JAVA

# -----------------------------------------------------------------------------------
def isWindows():
    return os.name in ['nt', 'ce']

# -----------------------------------------------------------------------------------
def isMac():
    return os.name == 'posix' and platform.system() == 'Darwin'

# -----------------------------------------------------------------------------------
def findClichartJar():
    """Get the real path to the CLIChart jar (following symlinks)"""
    realScript = os.path.realpath(__file__)
    directory = os.path.dirname(realScript)
    jarPaths = glob.glob(os.path.join(directory, JAR_PATTERN))
    if len(jarPaths) != 1:
        print('Error: did not find exactly one CLIChart jar file in %s' % directory, file=sys.stderr)
        sys.exit(1)
    return adjustCygwinPath(jarPaths[0])

# -----------------------------------------------------------------------------------
def adjustCygwinPath(jarPath):
    """If running under cygwin, have to adjust the cygwin path to a windows path (since Java is Windows)"""
    try:
        process = Popen(['cygpath', '-w', jarPath], stdout=PIPE)
        result = process.communicate()[0]
        if process.returncode != 0:
            return jarPath
        return result.strip()
    except:
        return jarPath

# -----------------------------------------------------------------------------------
if __name__ == '__main__':
    main()
