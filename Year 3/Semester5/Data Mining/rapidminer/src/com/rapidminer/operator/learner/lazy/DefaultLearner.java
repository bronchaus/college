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
package com.rapidminer.operator.learner.lazy;

import java.util.Iterator;
import java.util.List;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.Statistics;
import com.rapidminer.operator.Model;
import com.rapidminer.operator.OperatorCapability;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.learner.AbstractLearner;
import com.rapidminer.operator.learner.PredictionModel;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.metadata.AttributeSetPrecondition;
import com.rapidminer.operator.ports.metadata.ParameterConditionedPrecondition;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeAttribute;
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.parameter.ParameterTypeDouble;
import com.rapidminer.parameter.UndefinedParameterError;
import com.rapidminer.parameter.conditions.EqualTypeCondition;

/**
 * This learner creates a model, that will simply predict a default value for all examples, i.e. the average or median
 * of the true labels (or the mode in case of classification) or a fixed specified value. This learner can be used to
 * compare the results of &quot;real&quot; learning schemes with guessing.
 * 
 * @see com.rapidminer.operator.learner.lazy.DefaultModel
 * @author Stefan Rueping, Ingo Mierswa
 */
public class DefaultLearner extends AbstractLearner {

	/** The parameter name for &quot;The method to compute the default.&quot; */
	public static final String PARAMETER_METHOD = "method";

	/** The parameter name for &quot;Value returned when method = constant.&quot; */
	public static final String PARAMETER_CONSTANT = "constant";

	public static final String PARAMETER_ATTRIBUTE_NAME = "attribute_name";

	private static final String[] METHODS = { "median", "average", "mode", "constant", "attribute" };

	public static final int MEDIAN = 0;

	public static final int AVERAGE = 1;

	public static final int MODE = 2;

	public static final int CONSTANT = 3;

	public static final int ATTRIBUTE = 4;

	public DefaultLearner(OperatorDescription description) {
		super(description);

		InputPort exampleIn = getExampleSetInputPort();
		exampleIn.addPrecondition(new ParameterConditionedPrecondition(exampleIn, new AttributeSetPrecondition(exampleIn, new AttributeSetPrecondition.AttributeNameProvider() {
			@Override
			public String[] getRequiredAttributeNames() {
				try {
					return new String[] { getParameterAsString(PARAMETER_ATTRIBUTE_NAME) };
				} catch (UndefinedParameterError e) {
					return new String[0];
				}
			}
		}), this, PARAMETER_METHOD, METHODS[ATTRIBUTE]));
	}

	public Model learn(ExampleSet exampleSet) throws OperatorException {
		double value = 0.0;
		double[] confidences = null;
		int method = getParameterAsInt(PARAMETER_METHOD);
		Attribute label = exampleSet.getAttributes().getLabel();
		if ((label.isNominal()) && ((method == MEDIAN) || (method == AVERAGE))) {
			logWarning("Cannot use method '" + METHODS[method] + "' for nominal labels: changing to 'mode'!");
			method = MODE;
		} else if ((!label.isNominal()) && (method == MODE)) {
			logWarning("Cannot use method '" + METHODS[method] + "' for numerical labels: changing to 'average'!");
			method = AVERAGE;
		}
		switch (method) {
		case MEDIAN:
			double[] labels = new double[exampleSet.size()];
			Iterator<Example> r = exampleSet.iterator();
			int counter = 0;
			while (r.hasNext()) {
				Example example = r.next();
				labels[counter++] = example.getValue(example.getAttributes().getLabel());
			}
			java.util.Arrays.sort(labels);
			value = labels[exampleSet.size() / 2];
			break;
		case AVERAGE:
			exampleSet.recalculateAttributeStatistics(label);
			value = exampleSet.getStatistics(label, Statistics.AVERAGE);
			break;
		case MODE:
			exampleSet.recalculateAttributeStatistics(label);
			value = exampleSet.getStatistics(label, Statistics.MODE);
			confidences = new double[label.getMapping().size()];
			for (int i = 0; i < confidences.length; i++) {
				confidences[i] = exampleSet.getStatistics(label, Statistics.COUNT, label.getMapping().mapIndex(i)) / exampleSet.size();
			}
			break;
		case CONSTANT:
			value = getParameterAsDouble(PARAMETER_CONSTANT);
			break;
		case ATTRIBUTE:
			return new AttributeDefaultModel(exampleSet, getParameterAsString(PARAMETER_ATTRIBUTE_NAME));
		default:
			// cannot happen
			throw new OperatorException("DefaultLearner: Unknown default method '" + method + "'!");
		}
		log("Default value is '" + (label.isNominal() ? label.getMapping().mapIndex((int) value) : value + "") + "'.");
		return new DefaultModel(exampleSet, value, confidences);
	}

	@Override
	public Class<? extends PredictionModel> getModelClass() {
		return DefaultModel.class;
	}
	
	public boolean supportsCapability(OperatorCapability capability) {
		switch (capability) {
		case BINOMINAL_ATTRIBUTES:
		case POLYNOMINAL_ATTRIBUTES:
		case NUMERICAL_ATTRIBUTES:
		case POLYNOMINAL_LABEL:
		case BINOMINAL_LABEL:
		case ONE_CLASS_LABEL:
		case NUMERICAL_LABEL:
		case MISSING_VALUES:
			return true;
		default:
			return false;
		}
	}

	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		ParameterType type = new ParameterTypeCategory(PARAMETER_METHOD, "The method to compute the default.", METHODS, MEDIAN);
		type.setExpert(false);
		types.add(type);
		type = new ParameterTypeDouble(PARAMETER_CONSTANT, "Value returned when method = constant.", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0.0d);
		type.registerDependencyCondition(new EqualTypeCondition(this, PARAMETER_METHOD, METHODS, true, CONSTANT));
		types.add(type);

		type = new ParameterTypeAttribute(PARAMETER_ATTRIBUTE_NAME, "The attribute to get the predicted value from.", getExampleSetInputPort(), true);
		type.registerDependencyCondition(new EqualTypeCondition(this, PARAMETER_METHOD, METHODS, false, ATTRIBUTE));
		types.add(type);

		return types;
	}


}
