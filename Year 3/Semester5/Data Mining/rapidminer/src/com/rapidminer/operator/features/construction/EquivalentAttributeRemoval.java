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
package com.rapidminer.operator.features.construction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.poi.xslf.model.geom.ExpressionParser;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.Statistics;
import com.rapidminer.example.set.AttributeWeightedExampleSet;
import com.rapidminer.example.set.SimpleExampleSet;
import com.rapidminer.example.table.AbstractExampleTable;
import com.rapidminer.example.table.DataRow;
import com.rapidminer.example.table.DataRowFactory;
import com.rapidminer.example.table.DataRowReader;
import com.rapidminer.example.table.ExampleTable;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.generator.GenerationException;
import com.rapidminer.tools.RandomGenerator;
import com.rapidminer.tools.expression.parser.AbstractExpressionParser;
import com.rapidminer.tools.expression.parser.ExpressionParserFactory;


/**
 * If the example set contain two equivalent attributes, the longer
 * representation is removed. The length is calculated as the number of nested
 * brackets. The equivalency probe is not done by structural comparison. The
 * attribute values of the equations in question are randomly sampled and the
 * equation results compared. If the difference is less than <i>epsilon</i> for
 * <i>k</i> trials, the equations are probably equivalent. At least they
 * produce similar values. <br/>
 * 
 * The values of the attributes are sampled in the range of the minimum and
 * maximum values of the attribute. This ensures equivalency or at least very
 * similar values for the definition range in question. Therefore a
 * {@link MemoryExampleTable} is constructed and filled with random values. Then
 * a {@link ExpressionParser} is used to construct the attributes values.
 * 
 * @author Ingo Mierswa
 *          ingomierswa Exp $
 */
public class EquivalentAttributeRemoval extends ExampleSetBasedIndividualOperator {

	/**
	 * Indicates the number of examples which should be randomly generated to
	 * check equivalency.
	 */
	private int numberOfSamples = 10;

	/**
	 * If the difference is smaller than epsilon, the attributes are considered
	 * as equivalent.
	 */
	private double epsilon = 0.00005d;

	/** Recalculates attribute statistics before sampling. */
	private boolean recalculateAttributeStatistics = false;

    /** The random generator for the example values. */
    private RandomGenerator random;
    
	/** Creates a new equivalent attribute removal population operator. */
	public EquivalentAttributeRemoval(int numberOfSamples, double epsilon, boolean recalculateAttributeStatistics, RandomGenerator random) {
		this.numberOfSamples = numberOfSamples;
		this.epsilon = epsilon;
		this.recalculateAttributeStatistics = recalculateAttributeStatistics;
        this.random = random;
	}

	@Override
	public List<ExampleSetBasedIndividual> operate(ExampleSetBasedIndividual individual) {
		AttributeWeightedExampleSet exampleSet = individual.getExampleSet();
		if (recalculateAttributeStatistics)
			exampleSet.recalculateAllAttributeStatistics();
		Attribute[] allAttributes = exampleSet.getExampleTable().getAttributes();
		List<Attribute> simpleAttributesList = new ArrayList<Attribute>();
		for (int i = 0; i < allAttributes.length; i++) {
			if ((allAttributes[i] != null) && allAttributes[i].getConstruction().equals(allAttributes[i].getName()))
				simpleAttributesList.add(allAttributes[i]);
		}        

		Map<String, Attribute> removeMap = new HashMap<String, Attribute>();
		Attribute[] attributeArray = exampleSet.getAttributes().createRegularAttributeArray();
		for (int i = 0; i < attributeArray.length; i++) {
			for (int j = i + 1; j < attributeArray.length; j++) {
				Attribute att1 = attributeArray[i];
				Attribute att2 = attributeArray[j];
				if (att1.getConstruction().equals(att2.getConstruction())) {
					removeMap.put(att2.getName(), att2);
				} else {
					AbstractExampleTable exampleTable = new MemoryExampleTable(simpleAttributesList, new DataRowFactory(DataRowFactory.TYPE_DOUBLE_ARRAY, '.'), numberOfSamples);
					
					// create data set and attributes to check
					fillTableWithRandomValues(exampleTable, exampleSet, random);
					ExampleSet randomSet = new SimpleExampleSet(exampleTable, simpleAttributesList);
					try {
						// create parser
						AbstractExpressionParser parser = ExpressionParserFactory.getExpressionParser(true);
						Attribute test1 = parser.addAttribute(randomSet, "test1", att1.getConstruction());
						Attribute test2 = parser.addAttribute(randomSet, "test2", att2.getConstruction());
//						AttributeParser parser = new AttributeParser();
//						parser.generateAttribute(randomSet.getLog(), att1.getConstruction(), exampleTable);
//						parser.generateAttribute(randomSet.getLog(), att2.getConstruction(), exampleTable);
						
						// add longer attribute to remove map if equivalent
						if (equivalent(randomSet, test1, test2)) {
							int depth1 = att1.getConstruction().length();
							int depth2 = att2.getConstruction().length();
							if (depth1 > depth2) {
								removeMap.put(att1.getName(), att1);
								exampleSet.getLog().log("Removing attribute " + att1.getName()+"="+att1.getConstruction() + " which is equivalent to "+att2.getName()+"="+att2.getConstruction()+".");
							} else {
								removeMap.put(att2.getName(), att2);
								exampleSet.getLog().log("Removing attribute " + att2.getName()+"="+att2.getConstruction() + " which is equivalent to "+att1.getName()+"="+att1.getConstruction()+".");
							}
						}
					} catch (GenerationException e) {
						exampleSet.getLog().logWarning("Cannot generate test attribute: " + e.getMessage() + ". We just keep both attributes for sure...");
					}
				}
			}
		}

		Iterator i = removeMap.values().iterator();
		while (i.hasNext()) {
			Attribute attribute = (Attribute) i.next();
			//exampleSet.getLog().log("Remove equivalent attribute '" + attribute.getName() + "'.");
			exampleSet.getAttributes().remove(attribute);
		}

		List<ExampleSetBasedIndividual> l = new LinkedList<ExampleSetBasedIndividual>();
		l.add(new ExampleSetBasedIndividual(exampleSet));
		return l;
	}

	private boolean equivalent(ExampleSet exampleSet, Attribute test1, Attribute test2) {
		if (exampleSet.getAttributes().size() < 2) {
			return true;
		} else {
//			Iterator<Attribute> a = exampleSet.getAttributes().iterator();
//			Attribute a1 = a.next();
//			Attribute a2 = a.next();
//			if (a1.equals(a2))
//				return true;
			Iterator<Example> reader = exampleSet.iterator();
			while (reader.hasNext()) {
				Example example = reader.next();
				double value1 = example.getValue(test1);
				double value2 = example.getValue(test2);
				if (Math.abs(value1 - value2) > epsilon) {
					return false;
				}
			}
			return true;
		}
	}
	

	/**
	 * After creation of a new MemoryExampleTable with given size all values are
	 * Double.NaN. Use this method to fill the table with random values in the
	 * range specified by minimum and maximum values of the attributes. Please
	 * note that the attributes in the example table must already have proper
	 * minimum and maximum values. This works only for numerical attribute.
	 * Nominal attribute values will be set to 0.
	 */
	private static void fillTableWithRandomValues(ExampleTable exampleTable, ExampleSet baseSet, RandomGenerator random) {
		DataRowReader reader = exampleTable.getDataRowReader();
		Attribute[] attributes = exampleTable.getAttributes();
		while (reader.hasNext()) {
			DataRow dataRow = reader.next();
			for (int i = 0; i < attributes.length; i++) {
				if (attributes[i] != null) {
					if (!attributes[i].isNominal()) {
						double min = baseSet.getStatistics(attributes[i], Statistics.MINIMUM);
						double max = baseSet.getStatistics(attributes[i], Statistics.MAXIMUM);
						if (max > min)
							dataRow.set(attributes[i], random.nextDoubleInRange(min, max));
						else
							dataRow.set(attributes[i], random.nextDouble()*2 - 1);
					} else {
						dataRow.set(attributes[i], 0);
					}
				}
			}
		}
	}
}
