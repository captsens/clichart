/* (C) Copyright 2006, by John Dickson
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

package net.sf.clichart.main;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Collection;

/**
 * When printing a help string, the HelpFormatter class in commons cli only shows one option that has no short option.
 * This overrides the renderOptions method to change this behaviour.
 *
 * Also, the comparator used to sort the options doesn't work properly :( - see inner class
 *
 * @author johnd
 */
public class FixedHelpFormatter extends HelpFormatter {

    /* ========================================================================
     *
     * Class (static) variables.
     */

    /* ========================================================================
     *
     * Instance variables.
     */

    /* ========================================================================
     *
     * Constructors
     */

    /* ========================================================================
     *
     * Static methods
     */

    /* ========================================================================
     *
     * Public methods
     */

    /* ========================================================================
     *
     * Protected / package-private methods
     */

    protected StringBuffer renderOptions(StringBuffer sb,
            int width,
            Options options,
            int leftPad,
            int descPad) {
        final String lpad = createPadding(leftPad);
        final String dpad = createPadding(descPad);

        //first create list containing only <lpad>-a,--aaa where -a is opt and --aaa is
        //long opt; in parallel look for the longest opt string
        //this list will be then used to sort options ascending
        int max = 0;
        StringBuffer optBuf;
        List prefixList = new ArrayList();
        Option option;


        //List optList = options.helpOptions();
        Collection optionsCollection = options.getOptions();
        List optList = new ArrayList(optionsCollection);

        Collections.sort(optList, new StringBufferComparator());
        for (Iterator i = optList.iterator(); i.hasNext();) {
            option = (Option) i.next();
            optBuf = new StringBuffer(8);

            if (option.getOpt().equals(" ")) {
                optBuf.append(lpad).append("   " + defaultLongOptPrefix).append(option.getLongOpt());
            } else {
                optBuf.append(lpad).append(defaultOptPrefix).append(option.getOpt());
                if (option.hasLongOpt()) {
                    optBuf.append(',').append(defaultLongOptPrefix).append(option.getLongOpt());
                }

            }

            if (option.hasArg()) {
                if (option.hasArgName()) {
                    optBuf.append(" <").append(option.getArgName()).append('>');
                } else {
                    optBuf.append(' ');
                }
            }

            prefixList.add(optBuf);
            max = optBuf.length() > max ? optBuf.length() : max;
        }
        int x = 0;
        for (Iterator i = optList.iterator(); i.hasNext();) {
            option = (Option) i.next();
            optBuf = new StringBuffer(prefixList.get(x++).toString());

            if (optBuf.length() < max) {
                optBuf.append(createPadding(max - optBuf.length()));
            }
            optBuf.append(dpad);

            int nextLineTabStop = max + descPad;
            renderWrappedText(sb, width, nextLineTabStop,
                    optBuf.append(option.getDescription()).toString());
            if (i.hasNext()) {
                sb.append(defaultNewLine);
            }
        }

        return sb;
    }


    /* ========================================================================
     *
     * Inner classes
     */

    private static class StringBufferComparator
            implements Comparator {
        public int compare(Object o1, Object o2) {
            return (getOptionString(o1).compareTo(getOptionString(o2)));
        }

        private String getOptionString(Object o) {
            Option option = (Option)o;
            String result = option.getOpt();
            if (result == null || result.trim().length() == 0) {
                result = option.getLongOpt();
            }
            return result;
        }
    }
}

