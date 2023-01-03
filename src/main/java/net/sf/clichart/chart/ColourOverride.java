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

/**
 * Represents an override of the chart colour for a particular series in the chart.
 */
public class ColourOverride {

	/** The index of the series number this applies to (0-based) */
	private final int m_index;
	
	/** The colour the series is to be set to */
	private final Color m_colour;

	/**
	 * @param index
	 * @param color
	 */
	public ColourOverride(final int index, final Color color) {
		m_index = index;
		m_colour = color;
	}

	public Color getColour() {
		return m_colour;
	}

	public int getIndex() {
		return m_index;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((m_colour == null) ? 0 : m_colour.hashCode());
		result = PRIME * result + m_index;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ColourOverride other = (ColourOverride) obj;
		if (m_colour == null) {
			if (other.m_colour != null)
				return false;
		} else if (!m_colour.equals(other.m_colour))
			return false;
		if (m_index != other.m_index)
			return false;
		return true;
	}
	
	
	
}
