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

import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.DrawingSupplier;

/**
 * Wraps a DefaultDrawingSupplier to allow (initially) colours of individual series painters to be overridden
 */

public class DrawingSupplierWrapper implements DrawingSupplier {
	
	/**
	 * Provides what's effectively a sparse array of overridden items of the appropriate type
	 */
	private static class OverrideList<T> {
		/** Current counter - index of the last element requested */
		private int m_currentIndex = -1;
		
		/** The list of overrides */
		private ArrayList<T> m_overrides = new ArrayList<T>();
		
		/**
		 * Add an element at the given index
		 */
		void addOverride(T element, int index) {
			for (int i = m_overrides.size(); i < index + 1; i++) {
				m_overrides.add(null);
			}
			m_overrides.set(index, element);
		}
		
		/**
		 * Return any override element for the current index, or null if none
		 */
		T getElement(T defaultElement) {
			m_currentIndex++;
			if (m_overrides.size() < m_currentIndex + 1) {
				return defaultElement;
			}
			T override = m_overrides.get(m_currentIndex);
			if (override != null) {
				return override;
			}
			return defaultElement;
		}
	}
	
	/** Delegate all calls to this supplier */
	private DrawingSupplier m_delegate = new DefaultDrawingSupplier();
	
	/** Overrides for colours used for line charts */ 
	private OverrideList<Paint> m_paintOverrides = new OverrideList<Paint>();

	/** Overrides for colours used for bar charts */ 
	private OverrideList<Paint> m_outlinePaintOverrides = new OverrideList<Paint>();

	
	/**
	 * Add an override for the specified colour.  These are used for drawing the lines and bars on charts.
	 * If no override is provided, the default JFreechart colour will be used.
	 * 
	 * Note that this applies to both line and bar charts
	 */
	public void addColourOverride(ColourOverride override) {
		m_paintOverrides.addOverride(override.getColour(), override.getIndex());
		// Add to outline overrides too, so that it applies to both line and bar charts
		m_outlinePaintOverrides.addOverride(override.getColour(), override.getIndex());
	}
	
	public void addColourOverrides(List<ColourOverride> overrides) {
		for (ColourOverride override : overrides) {
			addColourOverride(override);
		}
	}
	
	// ===============================================================================
	// Interface methods
	// ===============================================================================
	public Paint getNextOutlinePaint() {
		Paint defaultPaint = m_delegate.getNextOutlinePaint();
		return m_outlinePaintOverrides.getElement(defaultPaint);
	}

	public Stroke getNextOutlineStroke() {
		return m_delegate.getNextOutlineStroke();
	}

	public Paint getNextPaint() {
		Paint defaultPaint = m_delegate.getNextPaint();
		return m_paintOverrides.getElement(defaultPaint);
	}

	public Shape getNextShape() {
		return m_delegate.getNextShape();
	}

	public Stroke getNextStroke() {
		return m_delegate.getNextStroke();
	}

}
