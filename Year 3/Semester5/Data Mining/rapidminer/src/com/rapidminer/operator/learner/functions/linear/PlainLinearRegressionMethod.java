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
package com.rapidminer.operator.learner.functions.linear;

import java.util.Collections;
import java.util.List;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.UndefinedParameterError;

/**
 * This method just does not perform any feature selection methods.
 * 
 * @author Sebastian Land
 */
public class PlainLinearRegressionMethod implements LinearRegressionMethod {

	@Override
	public LinearRegressionResult applyMethod(LinearRegression regression, boolean useBias, double ridge, ExampleSet exampleSet, boolean[] isUsedAttribute, int numberOfExamples, int numberOfUsedAttributes, double[] means, double labelMean, double[] standardDeviations, double labelStandardDeviation, double[] coefficientsOnFullData, double errorOnFullData) throws UndefinedParameterError {
		LinearRegressionResult result = new LinearRegressionResult();
		result.coefficients = coefficientsOnFullData;
		result.error = errorOnFullData;
		result.isUsedAttribute = isUsedAttribute;
		return result;
	}

	@Override
	public List<ParameterType> getParameterTypes() {
		return Collections.emptyList();
	}

}
