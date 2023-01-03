/* (C) Copyright 2008, by John Dickson
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import net.sf.clichart.chart.ColourOverride;
import net.sf.clichart.chart.ColourOverrideOptionParser;
import net.sf.clichart.chart.OptionsBean;
import net.sf.clichart.main.InvalidOptionsException;

/**
 * Option setter for colour overrides
 * 
 * @author johnd
 */
public class ColourOverrideListOptionSetter extends AbstractOptionSetter {

    public ColourOverrideListOptionSetter(String setterName) {
        super(setterName);
    }
    
	@Override
	protected Class[] getParameterType() {
		return new Class[] {List.class};
	}
	
	@Override
	protected void setValue(OptionsBean options, Method method, String command, String argument) 
			throws IllegalAccessException, InvalidOptionsException, InvocationTargetException {
        if (argument == null || argument.trim().length() == 0) {
            throw new InvalidOptionsException("Command requires an argument");
        }
        	
        List<ColourOverride> colourOverrides = new ColourOverrideOptionParser().parseOption(argument);
        method.invoke(options, colourOverrides);
	}

}
