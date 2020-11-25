/*
 * Copyright 2015 Anna Jonsson for the research group Foundations of Language
 * Processing, Department of Computing Science, Umeå university
 *
 * This file is part of Betty.
 *
 * Betty is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Betty is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Betty.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.umu.cs.flp.aj.nbest.wta.exceptions;


public class SymbolUsageException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public SymbolUsageException() {
		super();
	}

	public SymbolUsageException(String message) {
		super(message);
	}

	public SymbolUsageException(String message, Throwable cause) {
		super(message, cause);
	}

	public SymbolUsageException(Throwable cause) {
		super(cause);
	}

}
