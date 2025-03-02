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
package com.rapidminer.operator.learner.tree;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.tools.Tools;

/**
 * A split condition for numerical values (less equals).
 * 
 * @author Ingo Mierswa
 */
public class LessEqualsSplitCondition extends AbstractSplitCondition {
    
    private static final long serialVersionUID = 6658964566718050949L;
    
	private final double value;
    
    public LessEqualsSplitCondition(Attribute attribute, double value) {
        super(attribute.getName());
        this.value = value;
    }
    
    public boolean test(Example example) {
        return example.getValue(example.getAttributes().get(getAttributeName())) <= value;
    }

	public String getRelation() {
		return "\u2264";
	}

	public double getValue() {
		return value;
	}
	
	public String getValueString() {
		return Tools.formatIntegerIfPossible(this.value);
	}
}
