/* (C) Copyright 2006, by John Dickson
 *
 * Project Info:  https://github.com/captsens/clichart
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
 * Option setter for string[] properties, i.e. series titles
 *
 * @author johnd
 */
public class StringArrayOptionSetter extends AbstractOptionSetter {

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

    public StringArrayOptionSetter(String setterName) {
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
        if (argument == null || argument.trim().length() == 0) {
            throw new InvalidOptionsException("Command requires an argument");
        }

        method.invoke(options, new Object[]{argument.split("\\s*?,\\s*?")});
    }

    protected Class[] getParameterType() {
        // TODO: Can't remember how to get the array type...
        return new Class[]{new String[]{}.getClass()};
    }

    /* ========================================================================
     *
     * Private methods
     */
}
