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
package com.rapidminer.operator.preprocessing.normalization;

import java.util.LinkedList;
import java.util.List;

import com.rapidminer.parameter.ParameterHandler;
import com.rapidminer.parameter.ParameterType;

/**
 * This is an abstract class for all normalization methods. It returns
 * just an empty list of {@link ParameterType}s and does not perform any init code.
 * 
 * @author Sebastian Land
 *
 */
public abstract class AbstractNormalizationMethod implements NormalizationMethod {

	@Override
	public void init() {
	}

	@Override
	public List<ParameterType> getParameterTypes(ParameterHandler handler) {
		return new LinkedList<ParameterType>();
	}

}
