/* (C) Copyright 2006-2009, by John Dickson
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Abstract base class for option setters, using reflection to set the property on the Options
 *
 * @author johnd
 */
public abstract class AbstractOptionSetter implements OptionSetter {

    /* ========================================================================
     *
     * Class (static) variables.
     */

    /* ========================================================================
     *
     * Instance variables.
     */

    private final String m_setterName;

    /* ========================================================================
     *
     * Constructors
     */

    public AbstractOptionSetter(String setterName) {
        m_setterName = setterName;
    }


    /* ========================================================================
     *
     * Static methods
     */

    /* ========================================================================
     *
     * Public methods
     */
    public void setOption(OptionsBean options, String command, String argument) throws InvalidOptionsException {
        try {
            Method method = OptionsBean.class.getDeclaredMethod(m_setterName, getParameterType());
            setValue(options, method, command, argument);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unexpected error setting field value", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unexpected error setting field value", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Unexpected error setting field value", e);
        }
    }

    /* ========================================================================
     *
     * Protected / package-private methods
     */

    protected abstract void setValue(OptionsBean options, Method method, String command, String argument)
            throws IllegalAccessException, InvalidOptionsException, InvocationTargetException;


    protected abstract Class[] getParameterType();

    /* ========================================================================
     *
     * Private methods
     */

}
