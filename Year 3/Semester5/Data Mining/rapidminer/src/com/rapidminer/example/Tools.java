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
package com.rapidminer.example;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.rapidminer.example.set.AttributeWeightedExampleSet;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.example.table.DoubleArrayDataRow;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.example.table.NominalMapping;
import com.rapidminer.generator.FeatureGenerator;
import com.rapidminer.operator.OperatorCreationException;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.ports.metadata.AttributeMetaData;
import com.rapidminer.operator.ports.metadata.ExampleSetMetaData;
import com.rapidminer.operator.preprocessing.IdTagging;
import com.rapidminer.tools.Ontology;
import com.rapidminer.tools.OperatorService;
import com.rapidminer.tools.RandomGenerator;
import com.rapidminer.tools.math.sampling.OrderedSamplingWithoutReplacement;

/**
 * Provides some tools for calculation of certain measures and feature
 * generation.
 * 
 * @author Simon Fischer, Ingo Mierswa
 */
public class Tools {

	// ================================================================================
	// -------------------- Tools --------------------
	// ================================================================================

	public static String[] getAllAttributeNames(ExampleSet exampleSet) {
		String[] attributeNames = new String[exampleSet.getAttributes().allSize()];
		int counter = 0;
		Iterator<Attribute> a = exampleSet.getAttributes().allAttributes();
		while (a.hasNext()) {
			Attribute attribute = a.next();
			attributeNames[counter++] = attribute.getName();
		}
		return attributeNames;
	}

	public static String[] getRegularAttributeNames(ExampleSet exampleSet) {
		String[] attributeNames = new String[exampleSet.getAttributes().size()];
		int counter = 0;
		for (Attribute attribute : exampleSet.getAttributes())
			attributeNames[counter++] = attribute.getName();
		return attributeNames;
	}

	public static String[] getRegularAttributeConstructions(ExampleSet exampleSet) {
		String[] attributeNames = new String[exampleSet.getAttributes().size()];
		int counter = 0;
		for (Attribute attribute : exampleSet.getAttributes()) {
			if ((!attribute.getConstruction().equals(attribute.getName())) && (!attribute.getConstruction().startsWith("gens"))) { // is generated
				attributeNames[counter++] = attribute.getConstruction();
			} else {
				attributeNames[counter++] = attribute.getName();
			}
		}
		return attributeNames;
	}

	// ================================================================================
	// -------------------- GENERATION --------------------
	// ================================================================================

	public static Attribute[] createRegularAttributeArray(ExampleSet exampleSet) {
		Attribute[] attributes = new Attribute[exampleSet.getAttributes().size()];
		int counter = 0;
		for (Attribute attribute : exampleSet.getAttributes())
			attributes[counter++] = attribute;
		return attributes;
	}

	public static Attribute[] getRandomCompatibleAttributes(ExampleSet exampleSet, FeatureGenerator generator, String[] functions, Random random) {
		List inputAttributes = generator.getInputCandidates(exampleSet, functions);
		if (inputAttributes.size() > 0)
			return (Attribute[]) inputAttributes.get(random.nextInt(inputAttributes.size()));
		else
			return null;
	}

	public static Attribute[] getWeightedCompatibleAttributes(AttributeWeightedExampleSet exampleSet, FeatureGenerator generator, String[] functions, RandomGenerator random) {
		List inputAttributes = generator.getInputCandidates(exampleSet, functions);
		double[] probs = new double[inputAttributes.size()];
		double probSum = 0.0d;
		Iterator i = inputAttributes.iterator();
		int k = 0;
		while (i.hasNext()) {
			Attribute[] candidate = (Attribute[]) i.next();
			for (int j = 0; j < candidate.length; j++) {
				double weight = exampleSet.getWeight(candidate[j]);
				probSum += weight;
				probs[k] = weight;
			}
		}
		for (int j = 0; j < probs.length; j++)
			probs[j] /= probSum;
		return (Attribute[]) inputAttributes.get(random.randomIndex(probs));
	}

	/**
	 * Creates and adds the new attributes to the given example set
	 */
	public static Attribute createSpecialAttribute(ExampleSet exampleSet, String name, int valueType) {
		Attribute attribute = AttributeFactory.createAttribute(name, valueType);
		exampleSet.getExampleTable().addAttribute(attribute);
		exampleSet.getAttributes().setSpecialAttribute(attribute, name);
		return attribute;
	}

	public static AttributeMetaData createWeightAttributeMetaData(ExampleSetMetaData emd) {
		return new AttributeMetaData(Attributes.WEIGHT_NAME, Ontology.REAL, Attributes.WEIGHT_NAME);
	}

	/**
	 * This method adds a new Weight Attribute initialized with 1.0d for each example to
	 * the example table as well as to the given ExampleSet.
	 */
	public static Attribute createWeightAttribute(ExampleSet exampleSet) {
		Attribute weight = exampleSet.getAttributes().getWeight();
		if (weight != null) {
			exampleSet.getLog().logWarning("ExampleSet.createWeightAttribute(): Overwriting old weight attribute!");
		}

		weight = AttributeFactory.createAttribute(Attributes.WEIGHT_NAME, Ontology.REAL);
		exampleSet.getExampleTable().addAttribute(weight);
		exampleSet.getAttributes().setWeight(weight);

		for (Example example : exampleSet) {
			example.setValue(weight, 1d);
		}
		return weight;
	}

	public static boolean containsValueType(ExampleSet exampleSet, int valueType) {
		for (Attribute attribute : exampleSet.getAttributes()) {
			if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(attribute.getValueType(), valueType))
				return true;
		}
		return false;
	}

	/** Replaces the given real value by the new one. Please note that this method will only 
	 *  work for nominal attributes. */
	public static void replaceValue(Attribute attribute, String oldValue, String newValue) {
		if (!attribute.isNominal())
			throw new RuntimeException("Example-Tools: replaceValue is only supported for nominal attributes.");
		NominalMapping mapping = attribute.getMapping();
		int oldIndex = mapping.getIndex(oldValue);
		if (oldIndex < 0)
			throw new RuntimeException("Example-Tools: replaceValue cannot be performed since old value was not defined in the attribute.");
		mapping.setMapping(newValue, oldIndex);
	}

	/**
	 * Replaces the given value by the new one. This method will only work
	 * for nominal attributes.
	 */
	public static void replaceValue(ExampleSet exampleSet, Attribute attribute, String oldValue, String newValue) {
		if (!attribute.isNominal())
			throw new RuntimeException("Example-Tools: replaceValue is only supported for nominal attributes.");
		NominalMapping mapping = attribute.getMapping();
		if (oldValue.equals("?")) {
			for (Example example : exampleSet) {
				if (Double.isNaN(example.getValue(attribute)))
					example.setValue(attribute, mapping.mapString(newValue));
			}
		} else {
			int oldIndex = mapping.getIndex(oldValue);
			if (oldIndex < 0)
				throw new RuntimeException("Example-Tools: replaceValue cannot be performed since old value was not defined in the attribute.");
			if (newValue.equals("?")) {
				for (Example example : exampleSet) {
					int index = mapping.getIndex(example.getValueAsString(attribute));
					if (index == oldIndex) {
						example.setValue(attribute, Double.NaN);
					}
				}
				return;
			}
			int newIndex = mapping.getIndex(newValue);
			if (newIndex >= 0) {
				for (Example example : exampleSet) {
					int index = mapping.getIndex(example.getValueAsString(attribute));
					if (index == oldIndex) {
						example.setValue(attribute, newIndex);
					}
				}
			} else {
				mapping.setMapping(newValue, oldIndex);
			}
		}
	}

	/** Replaces the given real value by the new one. Please note that this method will only properly
	 *  work for numerical attributes since for nominal attributes no remapping is performed. Please
	 *  note also that this method performs a data scan. */
	public static void replaceValue(ExampleSet exampleSet, Attribute attribute, double oldValue, double newValue) {
		for (Example example : exampleSet) {
			double value = example.getValue(attribute);
			if (Double.isNaN(oldValue) && Double.isNaN(value)) {
				example.setValue(attribute, newValue);
				continue;
			}
			if (com.rapidminer.tools.Tools.isEqual(value, oldValue)) {
				example.setValue(attribute, newValue);
			}
		}
	}

	/**
	 * Returns true if value and block types of the first attribute are subtypes of
	 * value and block type of the second.
	 */
	public static boolean compatible(Attribute first, Attribute second) {
		return (Ontology.ATTRIBUTE_VALUE_TYPE.isA(first.getValueType(), second.getValueType())) && (Ontology.ATTRIBUTE_BLOCK_TYPE.isA(first.getBlockType(), second.getBlockType()));
	}

	// ================================================================================
	// P r o b a b i l t i e s
	// ================================================================================

	public static double getAverageWeight(AttributeWeightedExampleSet exampleSet) {
		int counter = 0;
		double weightSum = 0.0d;
		for (Attribute attribute : exampleSet.getAttributes()) {
			double weight = exampleSet.getWeight(attribute);
			if (!Double.isNaN(weight)) {
				weightSum += Math.abs(weight);
				counter++;
			}
		}
		return weightSum / counter;
	}

	public static double[] getProbabilitiesFromWeights(Attribute[] attributes, AttributeWeightedExampleSet exampleSet) {
		return getProbabilitiesFromWeights(attributes, exampleSet, false);
	}

	public static double[] getInverseProbabilitiesFromWeights(Attribute[] attributes, AttributeWeightedExampleSet exampleSet) {
		return getProbabilitiesFromWeights(attributes, exampleSet, true);
	}

	/**
	 * Calculates probabilities for attribute selection purposes based on the
	 * given weight. Attributes whose weight is not defined in the weight vector
	 * get a probability corresponding to the average weight. Inverse
	 * probabilities can be calculated for cases where attributes with a high
	 * weight should be selected with small probability.
	 */
	public static double[] getProbabilitiesFromWeights(Attribute[] attributes, AttributeWeightedExampleSet exampleSet, boolean inverse) {
		double weightSum = 0.0d;
		int counter = 0;
		for (int i = 0; i < attributes.length; i++) {
			double weight = exampleSet.getWeight(attributes[i]);
			if (!Double.isNaN(weight)) {
				weightSum += Math.abs(weight);
				counter++;
			}
		}
		double weightAverage = weightSum / counter;
		weightSum += (attributes.length - counter) * weightAverage;

		double[] probs = new double[attributes.length];
		for (int i = 0; i < probs.length; i++) {
			double weight = exampleSet.getWeight(attributes[i]);
			if (Double.isNaN(weight)) {
				probs[i] = weightAverage / weightSum;
			} else {
				probs[i] = inverse ? ((2 * weightAverage - Math.abs(weight)) / weightSum) : (Math.abs(weight) / weightSum);
			}
		}
		return probs;
	}

	public static Attribute selectAttribute(Attribute[] attributes, double[] probs, Random random) {
		double r = random.nextDouble();
		double sum = 0.0d;
		for (int i = 0; i < attributes.length; i++) {
			sum += probs[i];
			if (r < sum) {
				return attributes[i];
			}
		}
		return attributes[attributes.length - 1];
	}

	public static boolean isDefault(double defaultValue, double value) {
		if (Double.isNaN(defaultValue))
			return Double.isNaN(value);
		/* Don't use infinity.
		if (Double.isInfinite(defaultValue))
			return Double.isInfinite(value);
		 */
		return defaultValue == value;
	}

	/** The data set is not allowed to contain missing values. */
	public static void onlyNonMissingValues(ExampleSet exampleSet, String task) throws OperatorException {
		exampleSet.recalculateAllAttributeStatistics();
		for (Attribute attribute : exampleSet.getAttributes()) {
			double missing = exampleSet.getStatistics(attribute, Statistics.UNKNOWN);
			if (missing > 0) {
				throw new UserError(null, 139, task);
			}
		}
	}

	/**
	 * The attributes all have to be numerical.
	 * 
	 * @param es
	 *            the example set
	 * @throws OperatorException
	 */
	public static void onlyNumericalAttributes(ExampleSet es, String task) throws OperatorException {
		onlyNumericalAttributes(es.getAttributes(), task);
	}

	/**
	 * The attributes all have to be numerical.
	 * 
	 * @param es
	 *            the example set
	 * @throws OperatorException
	 */
	public static void onlyNumericalAttributes(Attributes attributes, String task) throws OperatorException {
		for (Attribute attribute : attributes) {
			if (!Ontology.ATTRIBUTE_VALUE_TYPE.isA(attribute.getValueType(), Ontology.NUMERICAL))
				throw new UserError(null, 104, task, attribute.getName());
		}
	}
	
	/**
	 * The attributes all have to be nominal or binary.
	 * 
	 * @param es
	 *            the example set
	 * @throws OperatorException
	 */
	public static void onlyNominalAttributes(ExampleSet es, String task) throws OperatorException {
		onlyNominalAttributes(es.getAttributes(), task);
	}
	
	/**
	 * The attributes all have to be nominal or binary.
	 * 
	 * @throws OperatorException
	 */
	public static void onlyNominalAttributes(Attributes attributes, String task) throws OperatorException {
		for (Attribute attribute : attributes) {
			if (!Ontology.ATTRIBUTE_VALUE_TYPE.isA(attribute.getValueType(), Ontology.NOMINAL))
				throw new UserError(null, 103, task, attribute.getName());
		}
	}

	/**
	 * The attributes all have to be nominal and have a maximum of two values or binominal. 
	 * 
	 * @param exampleSet
	 *            the example set
	 * @throws OperatorException
	 * 
	 */
	public static void maximumTwoNominalAttributes(ExampleSet exampleSet, String task) throws OperatorException {
		for (Attribute attribute : exampleSet.getAttributes()) {
			int valueType = attribute.getValueType();
			boolean throwError = false;
			if (!Ontology.ATTRIBUTE_VALUE_TYPE.isA(valueType, Ontology.NOMINAL)) {
				throwError = true;
			}
			if (valueType == Ontology.NOMINAL) {
				if (attribute.getMapping().size() > 2) {
					throwError = true;
				}
			}
			if (throwError) {
				throw new UserError(null, 114, task, attribute.getName());
			}
		}
	}

	/**
	 * The example set has to contain labels.
	 * 
	 * @param es
	 *            the example set
	 * @throws OperatorException
	 */
	public static void isLabelled(ExampleSet es) throws OperatorException {
		if (es.getAttributes().getLabel() == null) {
			throw new UserError(null, 105);
		}
	}

	/**
	 * The example set has to be tagged with ids.
	 * 
	 * @param es
	 *            the example set
	 * @throws OperatorException
	 */
	public static void isIdTagged(ExampleSet es) throws OperatorException {
		if (es.getAttributes().getId() == null) {
			throw new UserError(null, 129);
		}
	}

	/**
	 * The example set has to have ids. If no id attribute is available, it will
	 * be automatically created with help of the IDTagging operator.
	 * 
	 * @param es
	 *            the example set
	 * @throws OperatorException
	 */
	public static void checkAndCreateIds(ExampleSet es) throws OperatorException {
		if (es.getAttributes().getId() == null) {
			try {
				// create ids (and visualization)
				IdTagging idTagging = OperatorService.createOperator(IdTagging.class);
				idTagging.apply(es);
			} catch (OperatorCreationException e) {
				throw new UserError(null, 129);
			}
		}
	}

	public static void checkIds(ExampleSet exampleSet) throws UserError {
		if (exampleSet.getAttributes().getId() == null) {
			throw new UserError(null, 129);
		}
	}

	/**
	 * The example set has to have nominal labels.
	 * 
	 * @param es
	 *            the example set
	 * @throws OperatorException
	 */
	public static void hasNominalLabels(ExampleSet es) throws OperatorException {
		isLabelled(es);
		Attribute a = es.getAttributes().getLabel();
		if (!Ontology.ATTRIBUTE_VALUE_TYPE.isA(a.getValueType(), Ontology.NOMINAL))
			throw new UserError(null, 101, "clustering", a.getName());
	}

	/**
	 * The example set has to contain at least one example.
	 * 
	 * @param es
	 *            the example set
	 * @throws OperatorException
	 */
	public static void isNonEmpty(ExampleSet es) throws OperatorException {
		if (es.size() == 0)
			throw new UserError(null, 117);
	}

	/** Returns a new example set based on a fresh memory example table sampled from the
	 *  given set. */
	public static ExampleSet getLinearSubsetCopy(ExampleSet exampleSet, int size, int offset) {
		Map<Attribute, String> specialMap = new HashMap<Attribute, String>();
		List<Attribute> attributes = new LinkedList<Attribute>();
		Iterator<AttributeRole> a = exampleSet.getAttributes().allAttributeRoles();
		while (a.hasNext()) {
			AttributeRole role = a.next();
			Attribute clone = (Attribute) role.getAttribute().clone();
			attributes.add(clone);
			if (role.isSpecial()) {
				specialMap.put(clone, role.getSpecialName());
			}
		}

		MemoryExampleTable table = new MemoryExampleTable(attributes);
		int maxSize = exampleSet.size();
		for (int i = offset; (i < offset + size) && (i < maxSize); i++) {
			Example example = exampleSet.getExample(i);
			Iterator<Attribute> allI = exampleSet.getAttributes().allAttributes();
			int counter = 0;
			double[] dataRow = new double[attributes.size()];
			while (allI.hasNext()) {
				dataRow[counter++] = example.getValue(allI.next());
			}
			table.addDataRow(new DoubleArrayDataRow(dataRow));
		}

		return table.createExampleSet(specialMap);
	}

	/** Returns a new example set based on a fresh memory example table sampled from the
	 *  given set. */
	public static ExampleSet getShuffledSubsetCopy(ExampleSet exampleSet, int size, RandomGenerator randomGenerator) {
		int[] selectedIndices = OrderedSamplingWithoutReplacement.getSampledIndices(randomGenerator, exampleSet.size(), size);
		Map<Attribute, String> specialMap = new HashMap<Attribute, String>();
		List<Attribute> attributes = new LinkedList<Attribute>();
		Iterator<AttributeRole> a = exampleSet.getAttributes().allAttributeRoles();
		while (a.hasNext()) {
			AttributeRole role = a.next();
			Attribute clone = (Attribute) role.getAttribute().clone();
			attributes.add(clone);
			if (role.isSpecial()) {
				specialMap.put(clone, role.getSpecialName());
			}
		}

		MemoryExampleTable table = new MemoryExampleTable(attributes);

		for (int i = 0; i < selectedIndices.length; i++) {
			Example example = exampleSet.getExample(selectedIndices[i]);
			Iterator<Attribute> allI = exampleSet.getAttributes().allAttributes();
			int counter = 0;
			double[] dataRow = new double[attributes.size()];
			while (allI.hasNext()) {
				dataRow[counter++] = example.getValue(allI.next());
			}
			table.addDataRow(new DoubleArrayDataRow(dataRow));
		}

		return table.createExampleSet(specialMap);
	}

}
