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
package com.rapidminer.operator.clustering.clusterer;

import java.util.Map;

import com.rapidminer.operator.clustering.HierarchicalClusterNode;

/**
 * This class provides the AverageLinkageMethod, also called UPGMA, for the 
 * agglomerative clustering operator.
 * @author Sebastian Land
 */
public class AverageLinkageMethod extends AbstractLinkageMethod {

	private int[] clusterIds;
	
	public AverageLinkageMethod(DistanceMatrix matrix, int[] clusterIds) {
		super(matrix, clusterIds);
		this.clusterIds = clusterIds;
	}

	@Override
	public void updateDistances(DistanceMatrix matrix, int updatedRow, int unionedRow, Map<Integer, HierarchicalClusterNode> clusterMap) {
		double weightUpdatedRow = clusterMap.get(clusterIds[updatedRow]).getNumberOfExamplesInSubtree();
		double weightUnionedRow = clusterMap.get(clusterIds[unionedRow]).getNumberOfExamplesInSubtree();
		double totalWeight = weightUnionedRow + weightUpdatedRow;
		weightUnionedRow /= totalWeight;
		weightUpdatedRow /= totalWeight;
		for (int y = 0; y < matrix.getHeight(); y++) {
			matrix.set(updatedRow, y, weightUpdatedRow * matrix.get(updatedRow, y) + weightUnionedRow * matrix.get(unionedRow, y));
		}
	}

}
