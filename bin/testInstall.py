#!/usr/bin/env python

"""
An attempt to run a smoke test of an installed clichart, in a way that will work
on (and test) Linux, Cygwin and base Windows.
"""

import sys, os, shutil, getopt, time

OUTPUT_DIR = 'build/install-test/output'
SAMPLES_DIR = 'resource/samples'
CLI_SERVER_SCRIPT = os.path.join(os.path.dirname(__file__), '../src/test/system/cliserverScript.txt')

# ----------------------------------------------------------------------------
def usage(msg = None):
    if msg:
        print()
        print(msg)

    print("""Tests the clichart install

Options:
 -c     Perform only CLI server script file test
 -h     Show help (this page) and exit
 -l     Perform only cliserverlib test
 -r     Perform only standard runthrough
""")
    sys.exit(1)

# ----------------------------------------------------------------------------
def getJavaHomes():
    if sys.platform.find('linux') < 0:
        print("This script doesn't yet work on systems other than Linux...")
        sys.exit(1)
    javaDirs = set()
    for directory in os.listdir('/usr/lib/jvm'):
        realPath = os.path.realpath(os.path.join('/usr/lib/jvm', directory))
        if os.path.isdir(realPath) and os.path.exists(os.path.join(realPath, 'bin/java')):
            javaDirs.add(realPath)
    # to test without setting JAVA_HOME
    javaDirs.add(None)
    return javaDirs

# ----------------------------------------------------------------------------
def setJavaHome(javaHome):
    if javaHome is None:
        # don't set JAVA_HOME
        return
    print('Setting Java Home to', javaHome)
    os.putenv('JAVA_HOME', javaHome)

# ----------------------------------------------------------------------------
def outputOption(filename, javaVersion):
    if not filename:
        return ''
    name, ext = os.path.splitext(filename)
    return '-o %s/%s-%s%s' % (OUTPUT_DIR, name, javaVersion, ext)

# ----------------------------------------------------------------------------
def chart(title, cmdLine, inputFile, outputFile, javaVersion):
    print('-------', title)

    realCmdLine = cmdLine.replace('$OUTPUT', outputOption(outputFile, javaVersion)) \
            .replace('$INPUT', _sample(inputFile)) \
            .replace('$TITLE', '"%s"' % title)
    #print '  ', realCmdLine
    os.system(realCmdLine)

# ----------------------------------------------------------------------------
def _sample(path):
    return os.path.join(SAMPLES_DIR, path)

# ----------------------------------------------------------------------------
def testChartWithEmptyValue(javaVersion):
    """Test a chart with an empty data value, and the --ignoreempty option set.  Don't (want to)
    have a special test file for this, so must be handled separately"""
    title = 'System temps with the 2 temps for 10:30 empty'
    print('-------', title)
    cmdLine = 'cat %s | sed -e "s/10:30, 67.9, 48.1/10:30, ,/" | ' % _sample('SystemTemps.csv') \
            + 'clichart -fcl 0,1,2 -t "%s" -y "Degrees C" %s --columnlist2 3,4 --ytitle2 RPM --ignoreempty' \
            % (title, outputOption('SystemTemps-empty.png', javaVersion))
    os.system(cmdLine)

# ----------------------------------------------------------------------------
def standardRunthrough(javaHome):
    print('========== Java %s ===============' % javaHome)
    setJavaHome(javaHome)
    if javaHome:
        javaVersion = os.path.split(javaHome)[1]
    else:
        javaVersion = 'implicit'

    chart('Simple system temps', 'clichart -fcl 0,1,2 $OUTPUT $INPUT', 'SystemTemps.csv',
            'SystemTemps.png', javaVersion)

    chart('System temps with titles', 'clichart -f -c -l 0,1,2 -t $TITLE -y "Degrees C" $OUTPUT $INPUT',
            'SystemTemps.csv', 'SystemTemps-2.png', javaVersion)

    chart('Temps/fan speeds with second axis',
            'clichart -fcl 0,1,2 -t $TITLE -y "Degrees C" $OUTPUT --columnlist2 3,4 --ytitle2 RPM $INPUT',
            'SystemTemps.csv', 'SystemTemps-2axis.png', javaVersion)

    chart('Temps/fan speeds with second axis bar',
            'clichart -fcl 0,1,2 -t $TITLE -y "Degrees C" $OUTPUT --columnlist2 3,4 --bar2 --ytitle2 RPM $INPUT',
            'SystemTemps.csv', 'SystemTemps-2axisbar.png', javaVersion)

    chart('Temps/fan speeds with second axis bar and line embellishments',
            'clichart -fcl 0,1,2 -t $TITLE -y "Degrees C" $OUTPUT --columnlist2 3,4 --bar2 --ytitle2 RPM ' \
            + '--datapoints --lineweight 2 --lineweight2 5 $INPUT',
            'SystemTemps.csv', 'SystemTemps-2axisbarembellished.png', javaVersion)

    chart('Temps/fan speeds with value axis',
            'clichart -cfvl 2,4 -x "Case temperature" $OUTPUT --columnlist2 3,4 --bar2 --ytitle2 RPM ' \
            + '--datapoints --lineweight 2 --lineweight2 5 -t $TITLE $INPUT',
            'SystemTemps.csv', 'SystemTemps-value.png', javaVersion)

    chart('System log - memory and threads',
            'linestats -m VMStatusLogger -k s:0:5 -v f:4 -v f:7 -v f:11 -l k,0:min,1:min,2:min $INPUT ' \
            + '| clichart -l 0,1,2,3 -t $TITLE $OUTPUT', 'System.log', 'MemoryThreads.png', javaVersion)

    chart('System log - memory and threads, with mid to strip out data',
            'linestats -m VMStatusLogger -k s:0:5 -v f:4 -v f:7 -v f:11 -l k,0:min,1:min,2:min $INPUT ' \
            + '| mid 1:1 31:-1 | clichart -l 0,1,2,3 -t $TITLE $OUTPUT', 'System.log',
            'MemoryThreads-2.png', javaVersion)

    chart('System log - transactions',
            'linestats -m Transaction -k s:0:5 -l k:cnt,k $INPUT ' \
            + '| clichart -l 1,0 -t $TITLE $OUTPUT', 'System.log', 'Transactions.png', javaVersion)

    chart('System log - transaction amounts, with colour overrides',
            'linestats -m Transaction -k s:0:5 -v "r:A:(\\d+)" -c -l k,0:min,0:av,0:max ' \
            + ' -f "Timestamp, Min, Average, Max" $INPUT ' \
            + '| clichart -cl 0,1,2,3 -f -t $TITLE -y "Transaction amount (cents)" ' \
            + '--colours 0:green,1:cyan,2:ff00ff $OUTPUT',
            'System.log', 'Transactions-2.png', javaVersion)

    chart('System log - log message rates',
            'discretestats -k s:0:5 -v f:1 -c $INPUT ' \
            + '| clichart -cl 0,2,3 -f -t $TITLE -y "Messages per minute" $OUTPUT',
            'System.log', 'MessageRates.png', javaVersion)

    if sys.platform.find('linux') >= 0 or sys.platform.find('cygwin') >= 0:
        chart('Simple aggregate test',
                '(for x in 01 02 03; do ' \
                + 'aggregate -cf -p 2007-05-$x -l 3:last,1:av,1:max $INPUT; ' \
                + 'done) | clichart -cl 0,1,2,3 -d yyyy-MM-dd ' \
                + '--seriestitles "CPU fan speed end of day,Average CPU temp,Max CPU temp" $OUTPUT',
                'SystemTemps.csv', 'AggregateSystemTemps.png', javaVersion)

    testChartWithEmptyValue(javaVersion)


    chart('Repeat transactions, but with JPEG output',
            'linestats -m Transaction -k s:0:5 -l k:cnt,k $INPUT ' \
            + '| clichart -l 1,0 -t $TITLE $OUTPUT', 'System.log', 'Transactions.jpg', javaVersion)

    chart('Repeat transactions, but with larger charts (note diff option styles)',
            'linestats -m Transaction -k s:0:5 -l k:cnt,k $INPUT ' \
            + '| clichart -l 1,0 -t $TITLE -w 1024 --height 800 $OUTPUT',
            'System.log', 'Transactions-big.png', javaVersion)

    chart('Testing interactive! (just close window if it looks OK)',
            'linestats -m Transaction -k s:0:5 -l k:cnt,k $INPUT ' \
            + '| clichart -l 1,0 -t $TITLE ', 'System.log', None, javaVersion)

# ----------------------------------------------------------------------------
def _output(path):
    return os.path.join(OUTPUT_DIR, path)
    
# ----------------------------------------------------------------------------
def testCliserverlib():
    from clichart import cliserverlib

    driver = cliserverlib.ClichartDriver()
    driver.generateChart(inputPath = _sample('SystemTemps.csv'), columnList = [0, 1, 2],
        isCsv = True, hasHeader = True, 
        outputPath = _output('SystemTemps-1-clilib.png'))

    # re-use most of the previous options, by setting clearFirst = False
    driver.generateChart(clearFirst = False, title = 'System temps with titles',
            outputPath = _output('SystemTemps-2-clilib.png'), yTitle = 'Degrees C')

    # Temps/fan speeds with second axis bar.  Cleared first, ignore missing
    driver.generateChart(inputPath = _sample('SystemTemps.txt'), title = 'Temps/fan speeds with second axis bar',
            outputPath = _output('SystemTemps-2axisbar-clilib.png'), columnList = [0, 1, 2, 42],
            ignoreMissingColumns = True, yTitle = 'Degrees C', isBar2 = True, columnList2 = [3, 4],
            yTitle2 = 'RPM', seriesTitles = ['First series'], seriesTitles2 = ['Third', 'Fourth'])

    # generate a value-based chart, including colour override
    driver.generateChart(inputPath = _sample('SystemTemps.txt'),
        outputPath = _output('SystemTemps-value-clilib.png'),
        chartType = cliserverlib.CHART_TYPE_VALUE, columnList = [2, 4], title = 'Temps/fan speeds with value axis',
        xTitle = 'Case temperature', columnList2 = [3, 4], isBar2 = True, yTitle2 = 'RPM',
        showDatapoints = True, lineWeight = 2, lineWeight2 = 5, debugEcho = True,
        colours = [(0, 'cyan'), (1, 'FF00FF'), (3, 'darkgrey')])

    # test invalid values
    try:
        driver.generateChart(clearFirst = False, unknownArgument = True)
        assert False
    except cliserverlib.ClichartError:
        pass
    try:
        driver.generateChart(clearFirst = False, columnList = ['a', 'b'])
        assert False
    except cliserverlib.ClichartError:
        pass
    try:
        driver.generateChart(clearFirst = False, lineWeight = 'some text')
        assert False
    except cliserverlib.ClichartError:
        pass
    try:
        driver.generateChart()
        assert False
    except cliserverlib.ClichartError:
        pass # needs input path

    # now shut the driver down
    driver.close()

    # finally, check that the timeout is used
    driver = cliserverlib.ClichartDriver()
    driver.setServerTimeout(1)
    print('    =========================================================')
    print('    Should now see exit message from cliserver!')
    print('    =========================================================')
    time.sleep(2)


# ----------------------------------------------------------------------------
if __name__ == '__main__':
    runthrough = cliScript = cliLib = True
    opts, args = getopt.getopt(sys.argv[1:], 'chlr')
    for opt, arg in opts:
        if opt == '-c':
            runthrough = cliLib = False
        elif opt == '-h':
            usage()
        elif opt == '-l':
            runthrough = cliScript = False
        elif opt == '-r':
            cliLib = cliScript = False

    if os.path.exists(OUTPUT_DIR):
        shutil.rmtree(OUTPUT_DIR)
    os.mkdir(OUTPUT_DIR)

    if runthrough:
        for javaHome in getJavaHomes():
            standardRunthrough(javaHome)

    # don't yet have 1.6 installed on Windows, but not having it causes cliserverlib to fail
    setJavaHome('1.5')

    # Now test CLI server mode by piping a script to stdin
    if cliScript:
        print('=' * 75)
        print('Running CLI server test from saved script file')
        os.system('clichart --cliserver < %s' % CLI_SERVER_SCRIPT)

    if cliLib:
        print('=' * 75)
        print('Running CLI server test using cliserverlib')
        testCliserverlib()
