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
package com.rapidminer.tools.math.function.window;


/**
 * A Bartlett window function.
 * 
 * @author Tobias Malbrecht, Ingo Mierswa
 */
public class BartlettWindowFunction extends WindowFunction {

	public BartlettWindowFunction(Integer width) {
		super(width);
	}
	
	public BartlettWindowFunction(Integer width, Integer justification) {
		super(width, justification);
	}
	
	@Override
	protected double getValue(int width, int n) {
		return 1.0 - Math.abs(2 * (double) (n - width / 2) / width);
	}
}
