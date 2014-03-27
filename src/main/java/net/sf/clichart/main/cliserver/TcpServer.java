/* (C) Copyright 2010, by John Dickson
 *
 * Project Info:  http://clichart.sourceforge.net/
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 */
package net.sf.clichart.main.cliserver;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.clichart.main.ChartGeneratorFactory;

/**
 * A simple TCP/IP server, which spawns a CliServer to handle each connection 
 *
 */
public class TcpServer {
    
    private final SimpleDateFormat m_dateFormat = new SimpleDateFormat("HH:mm:ss,SSS");

    private final int m_listenPort;
    private ChartGeneratorFactory m_chartGeneratorFactory;
    private ServerSocket m_serverSocket;
    
    public TcpServer(int listenPort, ChartGeneratorFactory chartGeneratorFactory) {
        m_listenPort = listenPort;
        m_chartGeneratorFactory = chartGeneratorFactory;
    }

    public void serve() {
        try {
            infoLog("Starting socket server on port: " + m_listenPort);
            m_serverSocket = new ServerSocket(m_listenPort);
        } catch (IOException e) {
            errorLog("Could not listen on port: " + m_listenPort, e);
            System.exit(-1);
        }
        
        while (true) {
            try {
                Socket clientSocket = m_serverSocket.accept();
                startCliServer(clientSocket);
                
            } catch (IOException e) {
                errorLog("Accept failed on port: " + m_listenPort, e);
                System.exit(-1);
            }
        }
    }

    /**
     * Start a CliServer in a separate thread, to interact via the socket
     */
    private void startCliServer(final Socket clientSocket) {
        final CliServer cliServer = new CliServer();
        cliServer.setChartGenerator(m_chartGeneratorFactory.createChartGenerator());
        infoLog("Connection accepted from: " + clientSocket.getInetAddress());
        Thread thread = new Thread(
                new Runnable() {
                    public void run() {
                        try {
                            infoLog("Interacting with: " + clientSocket.getInetAddress());
                            cliServer.interact(clientSocket.getInputStream(), 
                                    new PrintStream(clientSocket.getOutputStream()));
                        } catch (IOException e) {
                            errorLog("Exception interacting with client", e);
                        } finally {
                            infoLog("Closing connection from: " + clientSocket.getInetAddress());
                            try {
                                clientSocket.close();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                });
        thread.start();
    }
    
    private void infoLog(String message) {
        log("INFO ", message);
    }

    private void errorLog(String message, Throwable t) {
        log("ERROR", message);
        t.printStackTrace();
    }
    
    private void log(String level, String message) {
        String timestamp = m_dateFormat.format(new Date());
        String threadId = "" + Thread.currentThread().hashCode();
        
        System.err.println(timestamp + "  " + level + "  " + threadId + "  " + message);
    }
}
