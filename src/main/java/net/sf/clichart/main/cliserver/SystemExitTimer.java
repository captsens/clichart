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

import java.util.Timer;
import java.util.TimerTask;


/**
 * Keeps a timer with a specified timeout, and if it hasn't been told of any lines of input data being provided
 * within that time, calls System.exit().
 * 
 * This is an attempt to get around occasional very fatal problems with the appearance of a zombie process after 
 * generating lots of charts without ever exiting.
 *
 * @author johnd
 */
public class SystemExitTimer {
    
    /** The timeout in ms */
    private long m_timeout = -1;
    
    // NOTE: Created lazily, since from TcpServer we don't want these preventing garbage collection
    private Timer m_timer = null;
    
    private long m_lastActivityAt;
    
    /**
     * Reset the timeout period
     */
    public void setTimeoutPeriod(int timeoutInSeconds) {
        assert timeoutInSeconds > 1;
        checkTimer();
        m_timeout = timeoutInSeconds * 1000;
    }
    
    private void checkTimer() {
        if (m_timer == null) {
            m_timer = new Timer(true);
            TimerTask task = new TimerTask() {
                public void run() {
                    checkForTimeout();
                }
            };
            m_timer.scheduleAtFixedRate(task, 1000, 1000);
        }
    }
    
    /**
     * Indicate that activity has been seen, so reset the timeout
     *
     */
    public void resetTimeout() {
        m_lastActivityAt = System.currentTimeMillis();
    }
    
    private void checkForTimeout() {
        if (m_timeout < 0) {
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - m_lastActivityAt > m_timeout) {
            System.err.println("Exiting JVM - no activity detected for over " + m_timeout + " ms");
            System.exit(1);
        }
    }
    
}
