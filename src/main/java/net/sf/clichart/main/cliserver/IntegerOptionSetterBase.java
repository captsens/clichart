/* (C) Copyright 2012, by John Dickson
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

import net.sf.clichart.chart.OptionsBean;
import net.sf.clichart.main.InvalidOptionsException;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * Base class for option setters for Integer and int properties.
 *
 * Like IntOptionSetter (with whom it shares, but sets type as Integer
 *
 * @author johnd
 */
public abstract class IntegerOptionSetterBase extends AbstractOptionSetter {

    /* ========================================================================
     *
     * Class (static) variables.
     */

    /* ========================================================================
     *
     * Instance variables.
     */

    /** Only set if this setter has a pre-determined arg */
    private Integer m_argument = null;

    /* ========================================================================
     *
     * Constructors
     */

    public IntegerOptionSetterBase(String setterName) {
        super(setterName);
    }

    public IntegerOptionSetterBase(String setterName, int argument) {
        super(setterName);
        m_argument = new Integer(argument);
    }

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

    protected void setValue(OptionsBean options, Method method, String command, String argument)
            throws IllegalAccessException, InvalidOptionsException, InvocationTargetException {

        if (m_argument == null && (argument == null || argument.trim().length() == 0)) {
            throw new InvalidOptionsException("Command requires an argument");
        }

        try {
            Integer intArgument = m_argument;
            if (argument != null) {
                intArgument = new Integer(argument);
            }
            method.invoke(options, new Object[]{intArgument});
        } catch (NumberFormatException e) {
            throw new InvalidOptionsException("Invalid integer argument: " + argument);
        }
    }

    /* ========================================================================
     *
     * Private methods
     */
}
