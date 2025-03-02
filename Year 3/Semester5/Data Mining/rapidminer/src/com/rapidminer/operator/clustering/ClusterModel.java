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
package com.rapidminer.operator.clustering;

import java.util.ArrayList;
import java.util.Collection;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Attributes;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.set.ConditionedExampleSet;
import com.rapidminer.example.set.NoMissingAttributeValueCondition;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.example.table.NominalMapping;
import com.rapidminer.operator.AbstractModel;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.tools.Ontology;
import com.rapidminer.tools.Tools;

/**
 * This class is the standard flat cluster model, using the example ids to remember which examples
 * were assigned to which cluster. This information is stored within the single clusters. Since this, 
 * the id attribute needs to be unchanged when cluster model is applied onto an example set.
 * @author Sebastian Land
 */
public class ClusterModel extends AbstractModel implements ClusterModelInterface {

	public static final int UNASSIGNABLE = -1;

	private static final long serialVersionUID = 3780908886210272852L;

	private boolean isAddingAsLabel;
	private boolean isRemovingUnknown;

	private ArrayList<Cluster> clusters;

	public ClusterModel(ExampleSet exampleSet, int k, boolean addClusterAsLabel, boolean removeUnknown) {
		super(exampleSet);
		this.clusters = new ArrayList<Cluster>(k);
		for (int i = 0; i < k; i++) {
			clusters.add(new Cluster(i));
		}
		this.isAddingAsLabel = addClusterAsLabel;
		this.isRemovingUnknown = removeUnknown;
	}


	@Override
	public ExampleSet apply(ExampleSet exampleSet) throws OperatorException {
		Attributes attributes = exampleSet.getAttributes();

		// additional checks
		this.checkCapabilities(exampleSet);

		// creating attribute
		Attribute targetAttribute;
		if (!isAddingAsLabel) {
			targetAttribute = AttributeFactory.createAttribute("cluster", Ontology.NOMINAL);
			exampleSet.getExampleTable().addAttribute(targetAttribute);
			attributes.setCluster(targetAttribute);
		} else {
			targetAttribute = AttributeFactory.createAttribute("label", Ontology.NOMINAL);
			exampleSet.getExampleTable().addAttribute(targetAttribute);
			attributes.setLabel(targetAttribute);
		}

		// setting values
		int[] clusterIndices = getClusterAssignments(exampleSet);
		int i = 0;
		for (Example example: exampleSet) {
			if (clusterIndices[i] != ClusterModel.UNASSIGNABLE) {
				example.setValue(targetAttribute, "cluster_" + clusterIndices[i]);
			} else {
				example.setValue(targetAttribute, Double.NaN);
			}
			i++;
		}
		// removing unknown examples if desired
		if (isRemovingUnknown)
			exampleSet = new ConditionedExampleSet(exampleSet, new NoMissingAttributeValueCondition(exampleSet, targetAttribute.getName())); 

		return exampleSet;

	}


	public int getNumberOfClusters() {
		return clusters.size();
	}
	
	/**
	 * This method returns whether this cluster model should add the assignment 
	 * as a label.
	 */
	public boolean isAddingLabel() {
		return isAddingAsLabel;
	}

	/**
	 * This method returns whether examples which can't be assigned should be removed from
	 * the resulting example set.
	 * @return
	 */
	public boolean isRemovingUnknownAssignments() {
		return isRemovingUnknown;
	}
	
	public void setClusterAssignments(int[] clusterId, ExampleSet exampleSet) {
		Attribute id = exampleSet.getAttributes().getId();
		if (id.isNominal()) {
			NominalMapping mapping = id.getMapping();
			int i = 0;
			for (Example example: exampleSet) {
				getCluster(clusterId[i]).assignExample(mapping.mapIndex((int)example.getValue(id)));
				i++;
			}
		} else {
			int i = 0;
			for (Example example: exampleSet) {
				getCluster(clusterId[i]).assignExample(example.getValue(id));
				i++;
			}
		}
	}

	/**
	 * This method returns an array with the indices or the cluster for all examples in the set.
	 * This will work with new examples, if centroid based clustering has been used before.
	 * Otherwise new examples cannot be assigned. 
	 */
	public int[] getClusterAssignments(ExampleSet exampleSet) {
		int[] clusterAssignments = new int[exampleSet.size()];
		Attribute idAttribute = exampleSet.getAttributes().getId();
		if (idAttribute.isNominal()) {
			int j = 0;
			for (Example example: exampleSet) {
				clusterAssignments[j] = getClusterIndexOfId(example.getValueAsString(idAttribute));
				j++;
			}
		} else {
			int j = 0;
			for (Example example: exampleSet) {
				clusterAssignments[j] = getClusterIndexOfId(example.getValue(idAttribute));
				j++;
			}
		}
		return clusterAssignments;
	}

	/**
	 * This method returns the index of the cluster, this Id's example has been assigned to.
	 * Please note, that this can only be applied to examples included in the clustering process.
	 * New examples might be assigned to clusters using the getClusterAssignments method, if and only 
	 * if the cluster model supports this. Currently only centroid based cluster models do.
	 */
	public int getClusterIndexOfId(Object id) {
		int index = 0;
		for (Cluster cluster: clusters) {
			if (cluster.containsExampleId(id))
				return index;
			index++;
		}
		return UNASSIGNABLE;
	}
	public Cluster getCluster(int i) {
		return clusters.get(i);
	}

	public Collection<Cluster> getClusters() {
		return clusters;
	}

	@Override
	public String getExtension() {
		return "cm";
	}

	@Override
	public String getFileDescription() {
		return "Cluster model";
	}

	public void checkCapabilities(ExampleSet exampleSet) throws OperatorException {
		com.rapidminer.example.Tools.isIdTagged(exampleSet);
	}

	@Override
	public String getName() {
		return "Cluster Model";
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		int sum = 0;
		for (int i = 0; i < getNumberOfClusters(); i++) {
			Cluster cl = getCluster(i);
			int numObjects = cl.getNumberOfExamples();
			result.append("Cluster " + cl.getClusterId() + ": " + numObjects + " items" + Tools.getLineSeparator());
			sum += numObjects;
		}
		result.append("Total number of items: " + sum + Tools.getLineSeparator());
		return result.toString();
	}
}
