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
package com.rapidminer.gui.tools;

import javax.swing.Icon;

/** Model for a {@link ParentButtonPanel}.
 * 
 *  @author Simon Fischer
 */
public interface ParentButtonModel<T> {

	public T getRoot();
	
	public T getParent(T child);
	
	public int getNumberOfChildren(T node);
	
	public T getChild(T node, int index);
	
	public String toString(T node);
	
	public Icon getIcon(T node);
}
