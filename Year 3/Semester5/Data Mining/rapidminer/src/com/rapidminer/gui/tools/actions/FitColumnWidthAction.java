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
package com.rapidminer.gui.tools.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import com.rapidminer.gui.tools.ExtendedJTable;
import com.rapidminer.gui.tools.IconSize;
import com.rapidminer.gui.tools.SwingTools;

/**
 * Start the corresponding action.
 * 
 * @author Ingo Mierswa
 */
public class FitColumnWidthAction extends AbstractAction {

	private static final long serialVersionUID = -438685397708559613L;

	private static final String ICON_NAME = "fit_to_size.png";
	
	private static final Icon[] ICONS = new Icon[IconSize.values().length];
	
	static {
		int counter = 0;
		for (IconSize size : IconSize.values()) {
			ICONS[counter++] = SwingTools.createIcon(size.getSize() + "/" + ICON_NAME);
		}
	}

	private ExtendedJTable table;
	
	public FitColumnWidthAction(ExtendedJTable table, IconSize size) {
		super("Fit Column Width", ICONS[size.ordinal()]);
		this.table = table;
		putValue(SHORT_DESCRIPTION, "Adapt the width of the column to the content");
		putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_W));
	}

	public void actionPerformed(ActionEvent e) {
		this.table.packColumn();
	}
}
