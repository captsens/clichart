"""
(C) Copyright 2007-2012, by John Dickson

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


A library to drive clichart in its CLI server mode.  This allows you to have a single
instance of clichart started (by this library), and have all your charts generated by
that one instance - much more efficient than starting a new instance for each chart.

To use this library, construct a ClichartDriver object, call generateChart() on it as
many times as you need, then call close().  See CLICHART_OPTIONS for all the options
that can be passed to generateChart().

Expects to find one of the following in the same directory as this library:
    - clichart shell script
    - a symlink to the clichart shell script
    - clichart.bat batch file.

TODO: Now also supports connecting to a CLIChart TCP/IP server on localhost, by passing
a port to the ClichartDriver constructor.
"""

import os, threading, queue, time, socket, subprocess

DEBUG = False

# Allowable chart types (determines what the X axis is based on)
CHART_TYPE_DATETIME = 'd'
CHART_TYPE_VALUE = 'v'
CHART_TYPE_NONE = 'n'


# metadata for option commands to clichart.  Consists of {paramName: (commandName, methodName)}
CLICHART_OPTIONS = {
    'inputPath': ('inputPath', '_setOption'),
    'outputPath': ('outputpath', '_setOption'),
    'columnList': ('columnlist', '_setListOption'),
    'columnList2': ('columnlist2', '_setListOption'),
    'isCsv': ('csv', '_setBooleanOption'),
    'hasHeader': ('hasheader', '_setBooleanOption'),
    # NB: accepts one of the CHART_TYPE_* values above
    'chartType': (None, '_setChartTypeOption'),
    'title': ('title', '_setOption'),
    'xTitle': ('xtitle', '_setOption'),
    'yTitle': ('ytitle', '_setOption'),
    'yTitle2': ('ytitle2', '_setOption'),
    'ignoreMissingColumns': ('ignoremissing', '_setBooleanOption'),
    'ignoreDuplicateValues': ('ignoredup', '_setBooleanOption'),
    'dateFormat': ('dateformat', '_setOption'),
    'showDatapoints': ('datapoints', '_setBooleanOption'),
    'showDatapoints2': ('datapoints2', '_setBooleanOption'),
    'lineWeight': ('lineweight', '_setOption'),
    'lineWeight2': ('lineweight2', '_setOption'),
    'minY': ('miny', '_setOption'),
    'maxY': ('maxy', '_setOption'),
    'minY2': ('miny2', '_setOption'),
    'maxY2': ('maxy2', '_setOption'),
    'forceYRange': ('forceyrange', '_setBooleanOption'),
    'forceYRange2': ('forceyrange2', '_setBooleanOption'),
    'height': ('height', '_setOption'),
    'width': ('width', '_setOption'),
    'isBar': ('bar', '_setBooleanOption'),
    'isBar2': ('bar2', '_setBooleanOption'),
    'debugEcho': ('debug-echo', '_setBooleanOption'),
    'seriesTitles': ('seriestitles', '_setListOption'),
    'seriesTitles2': ('seriestitles2', '_setListOption'),
    'colours': ('colours', '_setColourListOption'),
}

# ==============================================================================
class ClichartError(Exception):
    """Thrown to indicate an error returned from clichart"""
    pass

# ==============================================================================
class StdoutReader(threading.Thread):
    """A threaded reader for reading stdout and/or stderr streams (from clichart), so that we don't
    block forever waiting for stuff that never arrives.  If a queue is provided, this makes lines
    available via the queue, otherwise it just outputs the data received."""
    def __init__(self, stdout, queue):
        threading.Thread.__init__(self)
        self.stdout = stdout
        self.queue = queue
        self.setDaemon(True)
    def run(self):
        """Override the run method in Thread"""
        while True:
            line = self.stdout.readline()
            #print '  line read', line.strip(), 'EOL'
            if not line:
                # EOF, so terminate thread
                #print 'Thread terminating'
                return
            line = line.strip().decode('utf-8')
            if self.queue:
                self.queue.put(line)
            else:
                print('stderr:', line)

# ==============================================================================
class SocketWriter(object):
    """Adaptor to make a socket look like a file"""
    def __init__(self, socket):
        self.socket = socket
    def write(self, data):
        self.socket.send(data)
    def flush(self):
        pass
    def close(self):
        try:
            self.socket.close()
        except:
            pass

# ==============================================================================
class SocketReader(object):
    """Adaptor to make a socket look like an input file"""
    def __init__(self, socket):
        self.socket = socket
    def readline(self):
        # TODO: should I try to split this into lines?
        return self.socket.recv(1024)
    def close(self):
        try:
            self.socket.close()
        except:
            pass

# ==============================================================================
class ClichartDriver(object):
    """The driver for interacting with clichart"""
    def __init__(self, responseTimeout=10, port=-1):
        """Start the clichart instance in CLI server mode, ready for interaction.  The responseTimeout
        is the number of seconds we'll wait for a response from clichart before throwing an exception.
        If the port is set, it's a TCP/IP server, """
        self.responseTimeout = responseTimeout
        if port > 0:
            self.__startTcpServer(port)
        else:
            self.__startCliServer()
        self.__readResponse()

    def __startCliServer(self):
        modulePath = __file__
        if modulePath.endswith('.pyc'):
            modulePath = modulePath[:-1]
        if os.path.islink(modulePath):
            modulePath = os.path.realpath(modulePath)
        clichartPath = os.path.join(os.path.dirname(modulePath), 'clichart')
        if clichartPath == 'clichart' and os.path.exists(clichartPath):
            # if in current directory, Linux/unix needs path
            clichartPath = './clichart'
        # want to allow finding via PATH
        if not os.path.exists(clichartPath) and not os.path.exists(clichartPath + '.bat'):
            clichartPath = 'clichart'
        commandLine = '%s --cliserver' % clichartPath
        if DEBUG:
            print(commandLine)

        #self.stdin, self.stdout, self.stderr = os.popen3(commandLine)
        process = subprocess.Popen(commandLine, stdin=subprocess.PIPE, stdout=subprocess.PIPE,
                stderr=subprocess.PIPE, close_fds=True, shell=True)
        self.stdin, self.stdout, self.stderr = process.stdin, process.stdout, process.stderr
        
        self.queue = queue.Queue()
        # stdout goes to queue, just print stderr
        StdoutReader(self.stdout, self.queue).start()
        StdoutReader(self.stderr, None).start()

    def __startTcpServer(self, port):
        if DEBUG:
            print('Starting connection to TCP/IP server on port', port)
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.connect(('localhost', port))
        self.stdin = SocketWriter(s)
        self.queue = queue.Queue()
        self.stdout = SocketReader(s)
        StdoutReader(self.stdout, self.queue).start()

    def generateChart(self, clearFirst=True, **kw):
        """Call clichart to generate the chart using the parameters provided (see docstring for this class
        for details of all params).  If clearFirst is set to False, these parameters will merely override
        the options set in previous calls, otherwise the clichart defaults will be used"""
        if clearFirst:
            self.__sendCommand('clear')
        self.__processOptions(kw)
        self.__sendCommand('go')

    def setServerTimeout(self, timeInSeconds):
        """Tell the server to exit if it goes for more than this number of seconds without receiving any input.
        Used to make use of the server more robust for long-running processes"""
        self.__sendCommand('timeout', timeInSeconds)

    def close(self):
        """Shut down clichart"""
        self.__sendCommand('quit', expectResponse = False)
        self.stdin.close()
        try:
            self.stdout.close()
        except IOError:
            pass

    def __processOptions(self, optionParams):
        for key, value in list(optionParams.items()):
            if key in CLICHART_OPTIONS:
                command, methodName = CLICHART_OPTIONS[key]
                exec('self.%s(command, value)' % methodName)
            else:
                raise ClichartError('Invalid argument: %s' % key)

    def _setOption(self, command, value):
        self.__sendCommand(command, value)

    def _setListOption(self, command, value):
        """Value is a list of ints, and must be turned into CSV"""
        self.__sendCommand(command, ','.join(['%s' % item for item in value]))

    def _setColourListOption(self, command, value):
        """Value is a list of (int, string) and must be turned into CSV with a colon after the int"""
        self.__sendCommand(command, ','.join(['%d:%s' % (index, colour) for index, colour in value]))

    def _setBooleanOption(self, command, value):
        """If value is True, send the command with no value. If false, do nothing"""
        if value:
            self.__sendCommand(command)

    def _setChartTypeOption(self, command, value):
        if value == CHART_TYPE_DATETIME:
            # TODO: Can't do this yet!
            pass
        elif value == CHART_TYPE_VALUE:
            self.__sendCommand('xvalue')
        elif value == CHART_TYPE_NONE:
            self.__sendCommand('noxvalue')
        else:
            raise ClichartError('Invalid chart type value: %s' % value)

    def __sendCommand(self, command, arg=None, expectResponse=True):
        if DEBUG:
            print('Command:', command, arg)
        self.stdin.write(command.encode('utf-8'))
        if arg:
            self.stdin.write((' %s' % arg).encode('utf-8'))
        self.stdin.write('\n'.encode('utf-8'))
        self.stdin.flush()
        if not expectResponse:
            return
        self.__readResponse()

    def __readResponse(self):
        try:
            # Allow up to responseTimeout seconds to get the response (chart generation can take a while)
            try:
                response = self.queue.get(True, self.responseTimeout)
            except TypeError:
                # Python 2.2 doesn't support timeout
                response = self.queue.get(True)
            if DEBUG:
                print(' Response:', response)
            if not response.startswith('OK'):
               raise ClichartError(response)
        except queue.Empty:
            raise ClichartError('No response received')
