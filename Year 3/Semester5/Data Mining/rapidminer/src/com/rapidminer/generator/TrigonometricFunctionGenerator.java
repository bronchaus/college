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
package com.rapidminer.generator;

import java.util.logging.Level;

import com.rapidminer.tools.LogService;

/**
 * This class has one numerical input attribute and one output attribute.
 * Depending on the mode specified in the constructor the result will be the
 * sinus, cosinus, tangens, arc sinus, arc cosinus, or arc tangens.
 * 
 * @author Ingo Mierswa
 *          ingomierswa Exp $
 */
public class TrigonometricFunctionGenerator extends SingularNumericalGenerator {

	public static final int SINUS = 0;

	public static final int COSINUS = 1;

	public static final int TANGENS = 2;

	public static final int ARC_TANGENS = 3;

	private static final String[] FUNCTION_NAMES = { "sin", "cos", "tan", "atan" };

	private int mode = SINUS;

	public TrigonometricFunctionGenerator(int mode) {
		this.mode = mode;
	}

	public TrigonometricFunctionGenerator() {}

	@Override
	public FeatureGenerator newInstance() {
		return new TrigonometricFunctionGenerator(mode);
	}

	@Override
	public double calculateValue(double value) {
		double r = 0;
		switch (mode) {
			case SINUS:
				r = Math.sin(value);
				break;
			case COSINUS:
				r = Math.cos(value);
				break;
			case TANGENS:
				r = Math.tan(value);
				break;
			case ARC_TANGENS:
				r = Math.atan(value);
				break;
		}
		return r;
	}

	@Override
	public void setFunction(String name) {
		for (int i = 0; i < FUNCTION_NAMES.length; i++) {
			if (FUNCTION_NAMES[i].equals(name)) {
				this.mode = i;
				return;
			}
		}
		//LogService.getGlobal().log("Illegal function name '" + name + "' for " + getClass().getName() + ".", LogService.ERROR);
		LogService.getRoot().log(Level.SEVERE, "com.rapidminer.generator.TrigonometricFunctionGenerator.illegal_function_name",
				new Object[] {name, getClass().getName()});
	}

	@Override
	public String getFunction() {
		return FUNCTION_NAMES[mode];
	}

	@Override
	public boolean equals(Object o) {
		return (super.equals(o) && (this.mode == ((TrigonometricFunctionGenerator) o).mode));
	}
	
	@Override
	public int hashCode() {
		return super.hashCode() ^ Integer.valueOf(mode).hashCode();
	}
}
