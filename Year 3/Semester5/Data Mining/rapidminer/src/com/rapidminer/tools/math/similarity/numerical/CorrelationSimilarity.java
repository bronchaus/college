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
package com.rapidminer.tools.math.similarity.numerical;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.Tools;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.tools.math.MathFunctions;
import com.rapidminer.tools.math.similarity.SimilarityMeasure;

/**
 * Similarity based on the correlation coefficient.
 * 
 * @author Sebastian Land, Michael Wurst
 */
public class CorrelationSimilarity extends SimilarityMeasure {

	private static final long serialVersionUID = 7106870911590574668L;

	@Override
	public double calculateSimilarity(double[] value1, double[] value2) {
		return MathFunctions.correlation(value1, value2);
	}

	@Override
	public double calculateDistance(double[] value1, double[] value2) {
		return -calculateSimilarity(value1, value2);
	}

	@Override
	public void init(ExampleSet exampleSet) throws OperatorException {
	    super.init(exampleSet);
	    Tools.onlyNumericalAttributes(exampleSet, "value based similarities");
	}

	@Override
	public String toString() {
		return "Correlation based similarity";
	}
}
