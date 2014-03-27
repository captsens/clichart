#!/usr/bin/env python

"""
Check that all necessary prerequisites for developing CLIChart have been installed and configured appropriately.
"""

import sys, os, re
from subprocess import check_output, CalledProcessError, STDOUT

COBERTURA_RE = re.compile(r'property\s+name\s*=\s*"cobertura.dir"\s+value\s*=\s*"(.*?)"')


# =====================================================================================
class Problem(object):
    def __init__(self, title, output, description):
        self.title = title
        self.output = output
        self.description = description

# -------------------------------------------------------------------------------------
def check(problems, title, checkArgs, description):
    print 'Checking for: %s' % title
    try:
        output = check_output(checkArgs, stderr=STDOUT)
        message = '  ... OK'
        if output:
            message += ' (%s)' % output.splitlines()[0]
        print message
    except CalledProcessError, e:
        print '  ... Failed'
        problems.append(Problem(title, 'Output: %s' % e.output))

# -------------------------------------------------------------------------------------
def checkCobertura(problems):
    print 'Checking for: cobertura'
    coberturaDir = None
    if os.path.exists('build.properties'):
        for line in open('build.properties', 'r'):
            line = line.strip()
            if not line or line.startswith('#'):
                continue
            property, value = line.split("=")
            if property.strip() == 'cobertura.dir':
                coberturaDir = value.strip()
                break
    if coberturaDir is None:
        for line in open('build.xml', 'r'):
            match = COBERTURA_RE.search(line)
            if match:
                coberturaDir = match.group(1)
                break
    if coberturaDir is None:
        print '  ... Failed'
        problems.append(Problem('cobertura', 'Property "cobertura.dir" not found in build.properties or build.xml',
                'Cobertura (http://cobertura.sourceforge.net/)'))
        return
    checkPath = os.path.join(coberturaDir, 'cobertura.jar')
    if not os.path.exists(checkPath):
        print '  ... Failed'
        problems.append(Problem('cobertura', '%s not found. Set the "cobertura.dir" property in build.properties' % checkPath,
                'Cobertura (http://cobertura.sourceforge.net/)'))
        return
    print '  ... OK (%s)' % coberturaDir
                
# -------------------------------------------------------------------------------------
if __name__ == '__main__':
    problems = []
    check(problems, 'git', ['git', '--version'], 'Git version control system (http://git-scm.com/)')
    check(problems, 'javac', ['javac', '-version'], 'JavaJDK (Oracle, OpenJDK etc.)')
    check(problems, 'ant', ['ant', '-version'], 'Apache Ant build system (http://ant.apache.org/)')
    check(problems, 'rst2html', ['rst2html', '--version'], 'Python docutils (http://docutils.sourceforge.net/)')
    check(problems, 'linkchecker', ['linkchecker', '--version'], 'Linkchecker (http://linkchecker.sourceforge.net/)')
    checkCobertura(problems)
    check(problems, 'setuptools', ['python', '-c', 'import setuptools'],
            'Python setuptools (http://pypi.python.org/pypi/setuptools)')
    check(problems, 'convert', ['convert', '-version'], 'ImageMagick (http://www.imagemagick.org/)')
    check(problems, 'coverage', ['coverage', '-h'], 'Python code coverage tool (http://pypi.python.org/pypi/coverage)')
    check(problems, 'virtualenv', ['virtualenv', '--version'], 'Python virtual environment tool (http://www.virtualenv.org)')

    if problems:
        print
        print 'Problems found:'
        for problem in problems:
            print '-', problem.title
            print '  ', problem.description
            print '   Output from check:'
            for line in problem.output.splitlines():
                print '    ', line.strip()