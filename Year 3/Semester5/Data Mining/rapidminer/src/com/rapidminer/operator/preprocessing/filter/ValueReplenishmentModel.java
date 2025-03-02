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
import java.util.Iterator;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Attributes;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.ViewAttribute;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.preprocessing.PreprocessingModel;
import com.rapidminer.tools.Ontology;

/**
 * This class provides the preprocessing model for all value replacing operators.
 * @author Sebastian Land
 *
 */
public class ValueReplenishmentModel extends PreprocessingModel {

	private static final long serialVersionUID = -4886756106998999255L;

	private HashMap<String, Double> numericalAndDateReplacementMap;
	private HashMap<String, String> nominalReplacementMap;

	private double replaceWhat;

	private HashMap<Attribute, Double> attributeReplacementMap = new HashMap<Attribute, Double>();

	public ValueReplenishmentModel(ExampleSet exampleSet, double replacedValue, HashMap<String, Double> numericalAndDateReplacementMap,
			HashMap<String, String> nominalReplacementMap) {
		super(exampleSet);
		this.replaceWhat = replacedValue;
		this.nominalReplacementMap = nominalReplacementMap;
		this.numericalAndDateReplacementMap = numericalAndDateReplacementMap;
	}

	@Override
	public ExampleSet applyOnData(ExampleSet exampleSet) throws OperatorException {
		Attributes attributes = exampleSet.getAttributes();
		boolean[] replace = new boolean[attributes.size()];
		double[] by = new double[attributes.size()];
		int i = 0;
		for (Attribute attribute : attributes) {
			if (attribute.isNominal()) {
				String replacement = nominalReplacementMap.get(attribute.getName());
				if (replacement != null) {
					replace[i] = true;
					by[i] = attribute.getMapping().mapString(replacement);
				}
			}
			if (attribute.isNumerical() || Ontology.ATTRIBUTE_VALUE_TYPE.isA(attribute.getValueType(), Ontology.DATE_TIME)) {
				Double replacement = numericalAndDateReplacementMap.get(attribute.getName());
				if (replacement != null) {
					replace[i] = true;
					by[i] = replacement;
				}
			}
			i++;
		}

		for (Example example : exampleSet) {
			i = 0;
			for (Attribute attribute : attributes) {
				if (replace[i]) {
					double value = example.getValue(attribute);
					if (value == replaceWhat || (Double.isNaN(replaceWhat) && Double.isNaN(value))) { //special NaN treatment
						example.setValue(attribute, by[i]);
					}
				}
				i++;
			}
		}
		return exampleSet;
	}

	@Override
	public Attributes getTargetAttributes(ExampleSet viewParent) {
		Attributes attributes = viewParent.getAttributes();
		int i = 0;
		Iterator<Attribute> iterator = attributes.allAttributes();
		while (iterator.hasNext()) {
			Attribute attribute = iterator.next();
			if (attribute.isNominal()) {
				String replacement = nominalReplacementMap.get(attribute.getName());
				if (replacement != null) {
					// create view attribute and store
					Attribute viewAttribute = new ViewAttribute(this, attribute, attribute.getName(), attribute.getValueType(), attribute.getMapping());
					attributeReplacementMap.put(viewAttribute, (double) attribute.getMapping().mapString(replacement));
					// delete original attribute
					iterator.remove();
				}
			}
			if (attribute.isNumerical() || Ontology.ATTRIBUTE_VALUE_TYPE.isA(attribute.getValueType(), Ontology.DATE_TIME)) {
				Double replacement = numericalAndDateReplacementMap.get(attribute.getName());
				if (replacement != null) {
					// create view attribute and store
					Attribute viewAttribute = new ViewAttribute(this, attribute, attribute.getName(), attribute.getValueType(), null);
					attributeReplacementMap.put(viewAttribute, replacement);
					// delete original attribute
					iterator.remove();
				}
			}
			i++;
		}

		for (Attribute attribute : attributeReplacementMap.keySet())
			attributes.addRegular(attribute);

		return attributes;
	}

	@Override
	public double getValue(Attribute targetAttribute, double value) {
		if (replaceWhat == value || Double.isNaN(replaceWhat) && Double.isNaN(value))
			return attributeReplacementMap.get(targetAttribute);
		else
			return value;
	}
}
