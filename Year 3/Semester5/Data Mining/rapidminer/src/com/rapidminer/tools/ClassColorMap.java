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
package com.rapidminer.tools;

import java.awt.Color;
import java.util.logging.Level;
/**
 * @author Simon Fischer
 */
public class ClassColorMap extends ParentResolvingMap<Class,Color>{

	@Override
	public Color getDefault() {
		return Color.BLACK;		
	}

	@Override
	public Class getParent(Class child) {
		return child.getSuperclass();
	}

	@Override
	public Class parseKey(String key, ClassLoader classLoader) {
		try {
			return Class.forName(key, true, classLoader);
		} catch (ClassNotFoundException e) {
			//LogService.getGlobal().logError("Unknwon IO class: "+key);
			LogService.getRoot().log(Level.SEVERE,"com.rapidminer.tools.ClassColorMap.unkown_io_class", key);
			return null;		
		}		
	}

	@Override
	public Color parseValue(String value, ClassLoader classLoader) {
		return Color.decode(value.trim());
	}

}
