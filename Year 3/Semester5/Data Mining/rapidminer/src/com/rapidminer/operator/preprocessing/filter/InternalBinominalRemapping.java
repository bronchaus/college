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
package com.rapidminer.operator.preprocessing.filter;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.annotation.ResourceConsumptionEstimator;
import com.rapidminer.operator.preprocessing.AbstractDataProcessing;
import com.rapidminer.operator.tools.AttributeSubsetSelector;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.tools.Ontology;
import com.rapidminer.tools.OperatorResourceConsumptionHandler;


/**
 * Correct internal mapping of binominal attributes according to the specified
 * positive and negative values. If the internal mapping differs from the
 * specifications, the mapping is switched. If the mapping contains other values
 * than the specified ones, the mapping is not corrected and the attribute is
 * simply skipped.
 * 
 * @author Tobias Malbrecht
 */
public class InternalBinominalRemapping extends AbstractDataProcessing {

	/** The parameter name for &quot;The attributes to which the mapping correction should be applied.&quot; */
	public static final String PARAMETER_ATTRIBUTES = "attributes";
	
	/** The parameter name for &quot;Consider also special attributes (label, id...)&quot; */
	public static final String PARAMETER_APPLY_TO_SPECIAL_FEATURES = "apply_to_special_features";

	/** The parameter name for &quot;The first/negative/false value.&quot; */
	public static final String PARAMETER_NEGATIVE_VALUE = "negative_value";
	
	/** The parameter name for &quot;The second/positive/true value.&quot; */
	public static final String PARAMETER_POSITIVE_VALUE = "positive_value";

	private AttributeSubsetSelector attributeSelector = new AttributeSubsetSelector(this, getInputPort(), Ontology.BINOMINAL);
	
	public InternalBinominalRemapping(OperatorDescription description) {
		super(description);
		getExampleSetInputPort().addPrecondition(attributeSelector.makePrecondition());
	}

	@Override
	public ExampleSet apply(ExampleSet exampleSet) throws OperatorException {
		String negativeValue = getParameterAsString(PARAMETER_NEGATIVE_VALUE);
		String positiveValue = getParameterAsString(PARAMETER_POSITIVE_VALUE);
		
		Set<Attribute> attributes = attributeSelector.getAttributeSubset(exampleSet, false);

		HashMap<Attribute, HashMap<Double, Double>> mappings = new HashMap<Attribute, HashMap<Double,Double>>();
		for (Attribute attribute : attributes) {
			if (negativeValue.equals(attribute.getMapping().getNegativeString()) && positiveValue.equals(attribute.getMapping().getPositiveString())) {
				continue;
			}
			if (negativeValue.equals(attribute.getMapping().getPositiveString()) && positiveValue.equals(attribute.getMapping().getNegativeString())) {
				// create inversed value mapping
				HashMap<Double, Double> mapping = new HashMap<Double, Double>();
				mapping.put(0d, 1d);
				mapping.put(1d, 0d);
				mappings.put(attribute, mapping);
				attribute.getMapping().clear();
				attribute.getMapping().mapString(negativeValue);
				attribute.getMapping().mapString(positiveValue);
				continue;
			}
			logWarning("specified values do not match values of attribute " + attribute.getName() + ", attribute is skipped.");
		}
		
		for (Example example : exampleSet) {
			for (Attribute attribute : attributes) {
				HashMap<Double, Double> mapping = mappings.get(attribute);
				if (mapping != null) {
					double value = example.getValue(attribute);
					if (!Double.isNaN(value)) {
						Double mappedValue = mapping.get(value);
						if (mappedValue != null) {
							example.setValue(attribute, mappedValue.doubleValue());
						}
					}
				}
			}
		}
	    
		return exampleSet;
	}
		
	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		types.addAll(attributeSelector.getParameterTypes());
		types.add(new ParameterTypeString(PARAMETER_NEGATIVE_VALUE, "The first/negative/false value.", false));
		types.add(new ParameterTypeString(PARAMETER_POSITIVE_VALUE, "The second/positive/true value.", false));
		return types;
	}
	
	@Override
	public boolean writesIntoExistingData() {
		return false;
	}
	
	@Override
	public ResourceConsumptionEstimator getResourceConsumptionEstimator() {
		return OperatorResourceConsumptionHandler.getResourceConsumptionEstimator(getInputPort(), InternalBinominalRemapping.class, attributeSelector);
	}
}
