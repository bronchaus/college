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
package com.rapidminer.operator.learner.functions.kernel.hyperhyper;

import java.util.Iterator;

import com.rapidminer.datatable.DataTable;
import com.rapidminer.datatable.SimpleDataTable;
import com.rapidminer.datatable.SimpleDataTableRow;
import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.learner.PredictionModel;
import com.rapidminer.tools.Tools;

/**
 * The model for the HyperHyper implementation.
 * 
 * @author Regina Fritsch
 */
public class HyperModel extends PredictionModel {

	private static final long serialVersionUID = -453402008180607969L;

	private String[] coefficientNames;

	private double[] x1;

	private double[] x2;

	private double bias;

	private  double[] w;


	public HyperModel(ExampleSet trainingExampleSet, double bias, double[] w, double[] x1, double[] x2) {
		super(trainingExampleSet);
		this.coefficientNames = com.rapidminer.example.Tools.getRegularAttributeNames(trainingExampleSet);
		this.bias = bias;
		this.w = w;
		this.x1 = x1;
		this.x2 = x2;
	}

	public int getNumberOfAttributes() {
		return x1.length;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		result.append("Support Vector 1:" + Tools.getLineSeparator());
		for (int i = 0; i < this.coefficientNames.length; i++) {
			result.append(coefficientNames[i]).append(" = ").append(Tools.formatNumber(this.x1[i])).append(Tools.getLineSeparator());
		}
		result.append(Tools.getLineSeparator()).append("Support Vector 2:").append(Tools.getLineSeparator());
		for (int i = 0; i < this.coefficientNames.length; i++) {
			result.append(this.coefficientNames[i] + " = " + Tools.formatNumber(this.x2[i]) + Tools.getLineSeparator());
		}

		result.append(Tools.getLineSeparator()).append("Bias (offset): ").append(Tools.formatNumber(this.bias)).append(Tools.getLineSeparators(2));

		result.append("Coefficients:").append(Tools.getLineSeparator());
		for (int j = 0; j < w.length; j++) {
			result.append("w(").append(this.coefficientNames[j]).append(") = ").append(Tools.formatNumber(this.w[j])).append(Tools.getLineSeparator());
		}
		return result.toString();
	}

	@Override
	public String getName() {
		return ("HyperHyper Model");
	}

	@Override
	public ExampleSet performPrediction(ExampleSet exampleSet, Attribute predictedLabel) throws OperatorException {
		if (exampleSet.getAttributes().size() != getNumberOfAttributes())
			throw new UserError(null, 133, getNumberOfAttributes(), exampleSet.getAttributes().size());

		Iterator<Example> reader = exampleSet.iterator();
		while (reader.hasNext()) {
			Example activeExample = reader.next();
			double sum = 0;
			int i = 0;
			for (Attribute attribute : exampleSet.getAttributes()) {
				sum += activeExample.getValue(attribute)  * this.w[i];
				i++;
			}
			double result = sum + this.bias; // <w * x> + b

			int prediction = 0;
			if (result > 0) {
				prediction = getLabel().getMapping().getPositiveIndex();
			} else {
				prediction = getLabel().getMapping().getNegativeIndex();
			}
			activeExample.setValue(predictedLabel, prediction);
			activeExample.setConfidence(predictedLabel.getMapping().getPositiveString(), 1.0d / (1.0d + java.lang.Math.exp(-result)));
			activeExample.setConfidence(predictedLabel.getMapping().getNegativeString(), 1.0d / (1.0d + java.lang.Math.exp(result)));
		}

		return exampleSet;
	}

	private DataTable createWeightsTable() {
		SimpleDataTable weightTable = new SimpleDataTable("Hyper Weights", new String[] { "Attribute", "Weight" } );
		for (int j = 0; j < w.length; j++) {
			int nameIndex = weightTable.mapString(0, this.coefficientNames[j]);
			weightTable.add(new SimpleDataTableRow(new double[] { nameIndex, w[j]}));
		}
		return weightTable;
	}

	public DataTable getWeightTable() {
		return createWeightsTable();
	}
}
