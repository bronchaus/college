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
package com.rapidminer.datatable;

import java.util.Iterator;
import java.util.List;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;


/**
 *  This iterator iterates over all examples of an example set and creates
 *  {@link com.rapidminer.datatable.Example2DataTableRowWrapper} objects.
 *  
 *   @author Ingo Mierswa
 */
public class Example2DataTableRowIterator implements Iterator<DataTableRow> {

	private Iterator<Example> reader;
	
	private List<Attribute> allAttributes;
	
	private Attribute idAttribute;
	
	/** Creates a new DataTable iterator backed up by examples. If the idAttribute is null the DataTableRows 
	 *  will not be able to deliver an Id. */
	public Example2DataTableRowIterator(Iterator<Example> reader, List<Attribute> allAttributes, Attribute idAttribute) {
		this.reader = reader;
		this.allAttributes = allAttributes;
		this.idAttribute = idAttribute;
	}
	
	public boolean hasNext() {
		return reader.hasNext();
	}
	
	public DataTableRow next() {
		return new Example2DataTableRowWrapper(reader.next(), allAttributes, idAttribute);
	}
	
	public void remove() {
		reader.remove();
	}
}
