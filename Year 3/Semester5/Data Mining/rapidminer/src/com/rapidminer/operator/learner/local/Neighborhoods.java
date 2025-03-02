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
package com.rapidminer.operator.learner.local;

import java.util.LinkedList;
import java.util.List;

import com.rapidminer.operator.OperatorException;
import com.rapidminer.parameter.ParameterHandler;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.parameter.conditions.EqualTypeCondition;

/**
 * This class supports convenient methods for parameter depended creation of 
 * neighborhoods.
 * 
 * @author Sebastian Land
 */
public class Neighborhoods {

	public static final String PARAMETER_NEIGHBORHOOD_TYPE = "neighborhood_type";
	
	public static final String[] NEIGHBORHOOD_NAMES = new String[] {
		"Fixed Number",
		"Fixed Distance",
		"Relative Number",
		"Distance but at least"
	};
	
	public static final Class<?>[] NEIGHBORHOOD_CLASSES = new Class[] {
		NearestNeighborNeighborhood.class,
		DistanceNeighborhood.class,
		RelativeNeighborhood.class,
		AtLeastNeighborhood.class
	};
	
	public static final Neighborhood createNeighborhood(ParameterHandler handler) throws OperatorException {
		int chosenNeighborhood = handler.getParameterAsInt(PARAMETER_NEIGHBORHOOD_TYPE); 
		try {
			Neighborhood hood = (Neighborhood) NEIGHBORHOOD_CLASSES[chosenNeighborhood].newInstance();
			hood.init(handler);
			return hood;
		} catch (InstantiationException e) {
			throw new OperatorException("Could not instanciate distance measure " + NEIGHBORHOOD_NAMES[chosenNeighborhood]);
		} catch (IllegalAccessException e) {
			throw new OperatorException("Could not instanciate distance measure " + NEIGHBORHOOD_NAMES[chosenNeighborhood]);
		}
	}
	
	public static final List<ParameterType> getParameterTypes(ParameterHandler handler) {
		List<ParameterType> types = new LinkedList<ParameterType>();
		
		ParameterType type = new ParameterTypeCategory(PARAMETER_NEIGHBORHOOD_TYPE, "Determines which type of neighborhood should be used. Either with fixed number of neighbors, or all neighbors within a distance or mixed.", NEIGHBORHOOD_NAMES, 0);
		type.setExpert(false);
		types.add(type);
		for (int i = 0; i < NEIGHBORHOOD_CLASSES.length; i++) {
			try {
				Neighborhood hood = (Neighborhood) NEIGHBORHOOD_CLASSES[i].newInstance();
				for (ParameterType hoodType: hood.getParameterTypes(handler)) {
					hoodType.registerDependencyCondition(new EqualTypeCondition(handler, PARAMETER_NEIGHBORHOOD_TYPE, NEIGHBORHOOD_NAMES, true, i));
					types.add(hoodType);
				}
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			}
		}
		return types;
	}
}
