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
package com.rapidminer.gui.viewer;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;

import javax.swing.table.AbstractTableModel;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.AttributeRole;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.NumericalAttribute;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.Ontology;


/** The model for the {@link com.rapidminer.gui.viewer.MetaDataViewerTable}. 
 * 
 *  @author Ingo Mierswa
 */
public class DataViewerTableModel extends AbstractTableModel {

    private static final long serialVersionUID = -3057324874942971672L;

    private Attribute[] regularAttributes;
    
    private Attribute[] specialAttributes;
    
    private ExampleSet exampleSet;
    
    public DataViewerTableModel(ExampleSet exampleSet) {
        this.exampleSet = exampleSet;
        
        this.regularAttributes = new Attribute[exampleSet.getAttributes().size()];
        Iterator<Attribute> r = exampleSet.getAttributes().iterator();
        int counter = 0;
        while (r.hasNext()) {
        	this.regularAttributes[counter++] = r.next();;
        }
        
        this.specialAttributes = new Attribute[exampleSet.getAttributes().specialSize()];
        Iterator<AttributeRole> s = exampleSet.getAttributes().specialAttributes();
        counter = 0;
        while (s.hasNext()) {
        	this.specialAttributes[counter++] = s.next().getAttribute();
        }
    }
    
    @Override
	public Class<?> getColumnClass(int column) {
    	Class<?> type = super.getColumnClass(column);
        if (column == 0) {
        	type = Integer.class;
        } else {
            int col = column - 1;
            Attribute attribute = null;
            if (col < specialAttributes.length) {
                attribute = specialAttributes[col];
            } else {
                attribute = regularAttributes[col - specialAttributes.length];
            }
            
            if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(attribute.getValueType(), Ontology.DATE_TIME)) {
            	type = Date.class;
            } else if (!attribute.isNominal())
                type = Double.class;
            else
                type = String.class;
        }
        return type;
    }

    public int getRowCount() {
        return exampleSet.size();
    }

    /** Returns the sum of the number of special attributes, the number of regular attributes and 1 for the
     *  row no. column. */
    public int getColumnCount() {
        return this.specialAttributes.length + this.regularAttributes.length + 1;
    }

    public Object getValueAt(int row, int column) {
        if (column == 0) {
            return (row + 1);
        } else {
            int col = column - 1;
            Example example = exampleSet.getExample(row);
            if (example != null) {
                if (col < specialAttributes.length) {
                    return getValueWithCorrectClass(example, specialAttributes[col]);
                } else {
                    return getValueWithCorrectClass(example, this.regularAttributes[col - specialAttributes.length]);
                }
            } else {
                return null;
            }
        }
    }
    
    private Object getValueWithCorrectClass(Example example, Attribute attribute) {
    	// TODO consolidate value string formatting which is spread widely among this model, the DataViewerTable, and a ColoredTableCellRenderer
        try {
        	double value = example.getValue(attribute);
    		if (Double.isNaN(value)) {
    			return "?";
    		}
        	if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(attribute.getValueType(), Ontology.DATE_TIME)) {
        		// simply return date, format is evaluated directly in jtable, see todo above
        		return new Date((long) value);
        	} else if (attribute.isNumerical()) {
                return Double.valueOf(example.getValue(attribute));
        	} else {
                return example.getValueAsString(attribute, NumericalAttribute.DEFAULT_NUMBER_OF_DIGITS, false);
        	}
        } catch (Throwable e) {
            //LogService.getGlobal().logWarning("Cannot show correct value: " + e.getMessage());
            LogService.getRoot().log(Level.WARNING, "com.rapidminer.gui.viewer.DataViewerTableModel.showing_correct_value_error", e.getMessage());           
            return "Error";
        }
    }
    
    @Override
	public String getColumnName(int column) {
    	if (column < 0) return "";
        if (column == 0) {
            return "Row No.";
        } else {
            int col = column - 1;
            if (col < specialAttributes.length) {
                return specialAttributes[col].getName();
            } else {
                return this.regularAttributes[col - specialAttributes.length].getName();
            }
        }
    }
}
