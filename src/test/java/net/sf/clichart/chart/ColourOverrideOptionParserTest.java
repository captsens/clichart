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

package net.sf.clichart.chart;

import java.awt.Color;
import java.util.List;

import net.sf.clichart.main.InvalidOptionsException;

import junit.framework.TestCase;


/**
 * @author johnd
 *
 */
public class ColourOverrideOptionParserTest extends TestCase {
	
	private ColourOverrideOptionParser m_parser = new ColourOverrideOptionParser();
	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
	}

	public void testParseOption_NamedColour() throws InvalidOptionsException {
		List<ColourOverride> result = m_parser.parseOption("0:red");
		assertEquals(1, result.size());
		verifyOverride(result.get(0), 0, Color.red);
	}

	public void testParseOption_NamedColours() throws InvalidOptionsException {
		List<ColourOverride> result = m_parser.parseOption("0:cyan,2:blue,8:lightgrey,10:gray");
		assertEquals(4, result.size());
		verifyOverride(result.get(0), 0, Color.cyan);
		verifyOverride(result.get(1), 2, Color.blue);
		verifyOverride(result.get(2), 8, Color.lightGray);
		verifyOverride(result.get(3), 10, Color.gray);
	}

	public void testParseOption_HexColours() throws InvalidOptionsException {
		List<ColourOverride> result = m_parser.parseOption("1:ff0000,3:003a2b");
		assertEquals(2, result.size());
		verifyOverride(result.get(0), 1, new Color(255, 0, 0));
		verifyOverride(result.get(1), 3, new Color(0, 58, 43));
	}

	/**
	 * @param result
	 */
	private void verifyOverride(ColourOverride override, int index, Color colour) {
		assertEquals(index, override.getIndex());
		assertEquals(colour, override.getColour());
	}

}
