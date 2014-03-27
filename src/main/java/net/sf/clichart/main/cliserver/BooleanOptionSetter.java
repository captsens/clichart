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

package net.sf.clichart.main.cliserver;

import net.sf.clichart.chart.OptionsBean;
import net.sf.clichart.main.InvalidOptionsException;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * Option setter for boolean properties.  Currently ignores argument, and assumes that call should always enable
 * the option
 *
 * @author johnd
 */
public class BooleanOptionSetter extends AbstractOptionSetter {

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

    public BooleanOptionSetter(String setterName) {
        super(setterName);
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
        method.invoke(options, new Object[]{Boolean.TRUE});
    }

    protected Class[] getParameterType() {
        return new Class[]{Boolean.TYPE};
    }

    /* ========================================================================
     *
     * Private methods
     */
}
