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
package com.rapidminer.parameter.conditions;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.parameter.ParameterHandler;
import com.rapidminer.parameter.PortProvider;


/**
 * This condition checks whether a {@link InputPort} of a {@link Operator} is connected or not.
 * The condition is fulfilled if the {@link InputPort} is NOT connected.
 * 
 * @deprecated use {@link PortConnectedCondition} instead
 * 
 * @author Nils Woehler
 *
 */
@Deprecated
public class InputPortNotConnectedCondition extends PortConnectedCondition {

	public InputPortNotConnectedCondition(ParameterHandler handler, PortProvider portProvider, boolean becomeMandatory) {
		super(handler, portProvider, becomeMandatory, false);
	}

}
