/* (C) Copyright 2006-2012, by John Dickson
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
 * Option setter for int properties.
 *
 * Works both for options taking an int argument, and non-arg options that are mapped onto a call with an int arg
 *
 * @author johnd
 */
public class IntOptionSetter extends IntegerOptionSetterBase {

    public IntOptionSetter(String setterName) {
        super(setterName);
    }

    public IntOptionSetter(String setterName, int argument) {
        super(setterName, argument);
    }

    protected Class[] getParameterType() {
        return new Class[]{Integer.TYPE};
    }

}
