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
package com.rapidminer.gui.actions;

import java.awt.event.ActionEvent;

import com.rapidminer.gui.templates.ManageTemplatesDialog;
import com.rapidminer.gui.tools.ResourceAction;


/**
 * Start the corresponding action.
 * 
 * @author Ingo Mierswa
 */
public class ManageTemplatesAction extends ResourceAction {

	private static final long serialVersionUID = -5059748061190132388L;

	public ManageTemplatesAction() {
		super("manage_templates");
	}

	public void actionPerformed(ActionEvent e) {
		ManageTemplatesDialog dialog = new ManageTemplatesDialog();
		dialog.setVisible(true);
	}
}
