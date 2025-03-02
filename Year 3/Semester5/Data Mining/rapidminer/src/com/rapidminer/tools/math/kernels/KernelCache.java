/*
 *  RapidMiner
 *
 *  Copyright (C) 2001-2013 by Rapid-I and the contributors
 *
 *  Complete list of developers available at our web site:
 *
 *       http://rapid-i.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package com.rapidminer.tools.math.kernels;

/**
 * This is the interface describing the kernel cache.
 * 
 * @author Ingo Mierswa
 */
public interface KernelCache {

	/** Stores the value. Should only be invoked if the value was not known. This method might
	 *  delete kernel values so that the invocation of get might deliver NaN. */
	public void store(int i, int j, double value);
	
	/** Delivers the cached value or Double.NaN if the value is not known. */
	public double get(int i, int j);
	
}
