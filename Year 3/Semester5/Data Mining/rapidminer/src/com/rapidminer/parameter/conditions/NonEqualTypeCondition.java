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

import org.w3c.dom.Element;

import com.rapidminer.parameter.ParameterHandler;
import com.rapidminer.tools.XMLException;

/**
 * This condition checks if a type parameter (category) has NOT a certain value.
 * 
 * @author Ingo Mierswa, Sebastian Land
 */
public class NonEqualTypeCondition extends EqualTypeCondition {

    public NonEqualTypeCondition(Element element) throws XMLException {
        super(element);
    }

    public NonEqualTypeCondition(ParameterHandler handler, String conditionParameter, String[] options, boolean becomeMandatory, int...types) {
        super(handler, conditionParameter, options, becomeMandatory, types);
    }

    @Override
    public boolean isConditionFullfilled() {
        return !super.isConditionFullfilled();
    }
}
