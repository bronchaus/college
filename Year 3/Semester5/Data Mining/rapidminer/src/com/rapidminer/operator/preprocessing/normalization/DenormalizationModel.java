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

import java.util.Iterator;
import java.util.Map;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.AttributeRole;
import com.rapidminer.example.Attributes;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.SimpleAttributes;
import com.rapidminer.example.table.ViewAttribute;
import com.rapidminer.operator.preprocessing.normalization.DenormalizationOperator.LinearTransformation;
import com.rapidminer.tools.Ontology;
import com.rapidminer.tools.Tools;

/**
 * This Model can invert each possible linear transformation given by a normalization model.
 * @author Sebastian Land
 */
public class DenormalizationModel extends AbstractNormalizationModel {

    private static final long serialVersionUID = 1370670246351357686L;

    private Map<String, LinearTransformation> attributeTransformations;
    private AbstractNormalizationModel invertedModel;

    protected DenormalizationModel(ExampleSet exampleSet, Map<String, LinearTransformation> attributeTransformations, AbstractNormalizationModel model) {
        super(exampleSet);
        this.attributeTransformations = attributeTransformations;
        this.invertedModel = model;
    }

    @Override
    public Attributes getTargetAttributes(ExampleSet viewParent) {
        SimpleAttributes attributes = new SimpleAttributes();
        // add special attributes to new attributes
        Iterator<AttributeRole> roleIterator = viewParent.getAttributes().allAttributeRoles();
        while (roleIterator.hasNext()) {
            AttributeRole role = roleIterator.next();
            if (role.isSpecial()) {
                attributes.add(role);
            }
        }
        // add regular attributes
        for (Attribute attribute : viewParent.getAttributes()) {
            if (!attribute.isNumerical() || !attributeTransformations.containsKey(attribute.getName())) {
                attributes.addRegular(attribute);
            } else {
                // giving new attributes old name: connection to rangesMap
                attributes.addRegular(new ViewAttribute(this, attribute, attribute.getName(), Ontology.NUMERICAL, null));
            }
        }
        return attributes;
    }

    @Override
    public double getValue(Attribute targetAttribute, double value) {
        LinearTransformation linearTransformation = attributeTransformations.get(targetAttribute.getName());
        if (linearTransformation != null) {
            return (value - linearTransformation.b) / linearTransformation.a;
        }
        return value;
    }

    @Override
    public String toResultString() {
        StringBuilder builder = new StringBuilder();

        builder.append("Denormalization Model of the following Normalization:" + Tools.getLineSeparator());
        builder.append(invertedModel.toResultString());

        return builder.toString();

    }
}
