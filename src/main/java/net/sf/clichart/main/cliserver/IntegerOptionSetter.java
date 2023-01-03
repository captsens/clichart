/* (C) Copyright 22012, by John Dickson
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
 * Option setter for Integer properties.
 *
 * Like IntOptionSetter (with whom it shares, but sets type as Integer
 *
 * @author johnd
 */
public class IntegerOptionSetter extends IntegerOptionSetterBase {

    public IntegerOptionSetter(String setterName) {
        super(setterName);
    }

    public IntegerOptionSetter(String setterName, int argument) {
        super(setterName, argument);
    }

    protected Class[] getParameterType() {
        return new Class[]{Integer.class};
    }
}
