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
package com.rapidminer.io.process;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;

import com.rapidminer.operator.ExecutionUnit;
import com.rapidminer.operator.Operator;

/**
 * 
 * @author Simon Fischer
 *
 */
public class ProcessXMLFilterRegistry {

	private static final Object LOCK = new Object();
	
	private static final List<ProcessXMLFilter> FILTERS = new LinkedList<ProcessXMLFilter>();
	
	public static void registerFilter(ProcessXMLFilter filter) {
		synchronized (LOCK) {
			FILTERS.add(filter);
		}
	}
	
	protected static void fireOperatorExported(Operator operator, Element element) {
		for (ProcessXMLFilter filter : FILTERS) {
			filter.operatorExported(operator, element);
		}
	}
	
	protected static void fireOperatorImported(Operator operator, Element element) {
		for (ProcessXMLFilter filter : FILTERS) {
			filter.operatorImported(operator, element);
		}
	}

	protected static void fireExecutionUnitExported(ExecutionUnit unit, Element element) {
		for (ProcessXMLFilter filter : FILTERS) {
			filter.executionUnitExported(unit, element);
		}
	}
	
	protected static void fireExecutionUnitImported(ExecutionUnit unit, Element element) {
		for (ProcessXMLFilter filter : FILTERS) {
			filter.executionUnitImported(unit, element);
		}	
	}

}
