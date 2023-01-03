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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.clichart.main.InvalidOptionsException;

/**
 * Parses command line (or CLI server) option for colour overrides
 */
public class ColourOverrideOptionParser {
	
	private static final Map<String, Color> NAMED_COLOURS = new HashMap<String, Color>();
	{
		NAMED_COLOURS.put("black", Color.black);
		NAMED_COLOURS.put("blue", Color.blue);
		NAMED_COLOURS.put("cyan", Color.cyan);
		NAMED_COLOURS.put("darkgray", Color.darkGray);		// US spelling
		NAMED_COLOURS.put("darkgrey", Color.darkGray);
		NAMED_COLOURS.put("gray", Color.gray);				// US spelling
		NAMED_COLOURS.put("grey", Color.gray);
		NAMED_COLOURS.put("green", Color.green);
		NAMED_COLOURS.put("lightgray", Color.lightGray);	// US spelling
		NAMED_COLOURS.put("lightgrey", Color.lightGray);
		NAMED_COLOURS.put("magenta", Color.magenta);
		NAMED_COLOURS.put("orange", Color.orange);
		NAMED_COLOURS.put("pink", Color.pink);
		NAMED_COLOURS.put("red", Color.red);
		NAMED_COLOURS.put("white", Color.white);
		NAMED_COLOURS.put("yellow", Color.yellow);
	}

	public List<ColourOverride> parseOption(String option) throws InvalidOptionsException {
		List<ColourOverride> result = new ArrayList<ColourOverride>();
		for (String optionComponent : option.split(",")) {
			result.add(parseOptionComponent(optionComponent));
		}
		return result;
	}

	private ColourOverride parseOptionComponent(String optionComponent) throws InvalidOptionsException {
		String[] indexAndColour = optionComponent.trim().split(":");
		if (indexAndColour.length != 2) {
			throw new InvalidOptionsException("Invalid colour override: [" + optionComponent + "]");
		}
		try {
			int index = Integer.parseInt(indexAndColour[0]);
			Color colour = parseColour(indexAndColour[1]);
			return new ColourOverride(index, colour);
		} catch (NumberFormatException e) {
			throw new InvalidOptionsException("Invalid index in colour override: [" + optionComponent + "]");
		}
	}

	private Color parseColour(String colourString) throws InvalidOptionsException {
		String lowerCaseColour = colourString.toLowerCase();
		Color namedColour = NAMED_COLOURS.get(lowerCaseColour);
		if (namedColour != null) {
			return namedColour;
		}
		return parseHexColour(lowerCaseColour);
	}

	private Color parseHexColour(String lowerCaseColour) throws InvalidOptionsException {
		if (lowerCaseColour.length() != 6) {
			throw new InvalidOptionsException("Invalid colour: [" + lowerCaseColour + "]");
		}
		try {
			int firstByte = Integer.parseInt(lowerCaseColour.substring(0, 2), 16);
			int secondByte = Integer.parseInt(lowerCaseColour.substring(2, 4), 16);
			int thirdByte = Integer.parseInt(lowerCaseColour.substring(4, 6), 16);
			return new Color(firstByte, secondByte, thirdByte);
		} catch (NumberFormatException e) {
			throw new InvalidOptionsException("Invalid colour: [" + lowerCaseColour + "]");
		}
	}
}
