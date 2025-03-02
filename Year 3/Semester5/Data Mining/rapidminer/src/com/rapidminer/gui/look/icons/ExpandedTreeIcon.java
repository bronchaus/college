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
package com.rapidminer.gui.look.icons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;

import javax.swing.Icon;

/**
 * The expanded tree icon.
 *
 * @author Ingo Mierswa
 */
public class ExpandedTreeIcon implements Icon, Serializable {

	private static final long serialVersionUID = 8948047408825915900L;

	protected static final int SIZE = 9;

	protected static final int HALF_SIZE = 4;
	
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Color backgroundColor = c.getBackground();
		g.setColor(backgroundColor == null ? Color.white : backgroundColor);
		g.fillRect(x, y, 8, 8);
		g.setColor(Color.red);
		g.drawRect(x, y, 8, 8);
		g.setColor(Color.black);
		g.drawLine(x + 2, y + 4, x + 6, y + 4);
	}

	public int getIconWidth() {
		return 9;
	}

	public int getIconHeight() {
		return 9;
	}
}
