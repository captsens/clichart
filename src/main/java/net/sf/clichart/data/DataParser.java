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

package net.sf.clichart.data;

import java.io.Reader;
import java.io.IOException;

/**
 * Interface for an object that can parse data from a stream, and pass it to a DataSink
 *
 * @author johnd
 */
public interface DataParser {

    /* ========================================================================
     *
     * Interface (static) variables.
     */

    /* ========================================================================
     *
     * Public methods
     */

    /**
     * Parse the data from the reader, advising the sink of all data parsed
     *
     * @param source
     */
    public void parse(Reader source) throws IOException, InvalidDataException;

}
