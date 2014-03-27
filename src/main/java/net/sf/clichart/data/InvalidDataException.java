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

/**
 * Thrown to indicate that the data in a line is not valid
 *
 * @author johnd
 */
public class InvalidDataException extends Exception {

    /* ========================================================================
     *
     * Class (static) variables.
     */

    private static final long serialVersionUID = 1L;
    
    /* ========================================================================
     *
     * Instance variables.
     */

    /* ========================================================================
     *
     * Constructors
     */

    public InvalidDataException(String message) {
        super(message);
    }

    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
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

    /* ========================================================================
     *
     * Private methods
     */
}
