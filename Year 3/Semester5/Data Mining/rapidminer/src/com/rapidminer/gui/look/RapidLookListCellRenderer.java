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
package com.rapidminer.gui.look;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import com.rapidminer.gui.look.borders.Borders;

/**
 * The list cell renderer used for lists in this look and feel.
 *
 * @author Ingo Mierswa
 */
public class RapidLookListCellRenderer extends BasicComboBoxRenderer {

	private static final long serialVersionUID = 2133795635143498190L;

	private Border focusBorder = null;

	private JList parentList = null;

	public RapidLookListCellRenderer() {
		super();
		if (this.focusBorder == null) {
			this.focusBorder = Borders.getComboBoxListCellRendererFocusBorder();
		}
	}

	public static class UIResource extends RapidLookListCellRenderer implements javax.swing.plaf.UIResource {
		private static final long serialVersionUID = -8203261034086759332L;
	}

	public JList getParentList() {
		return this.parentList;
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		this.parentList = list;
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (index > -1) {
			if (isSelected) {
				setBorder(this.focusBorder);
			} else {
				setBorder(noFocusBorder);
			}
		}
		return this;
	}
}
