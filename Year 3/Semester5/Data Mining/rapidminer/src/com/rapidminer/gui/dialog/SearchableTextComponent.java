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
package com.rapidminer.gui.dialog;

/**
 * This interface wraps text components which provide the possibility to select segments. 
 * 
 * @author Ingo Mierswa
 */
public interface SearchableTextComponent {

	public void select(int start, int end);
	
	public void requestFocus();
	
	public void setCaretPosition(int pos);
	
	public int getCaretPosition();
	
	public String getText();
	
	public void replaceSelection(String newString);
    
    public boolean canHandleCarriageReturn();
}
