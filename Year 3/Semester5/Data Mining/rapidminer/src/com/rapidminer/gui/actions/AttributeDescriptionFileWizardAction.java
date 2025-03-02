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

import com.rapidminer.Process;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.wizards.ConfigurationListener;
import com.rapidminer.gui.wizards.ExampleSourceConfigurationWizard;
import com.rapidminer.parameter.Parameters;


/**
 * Start the corresponding action.
 * 
 * @author Ingo Mierswa
 */
public class AttributeDescriptionFileWizardAction extends ResourceAction implements ConfigurationListener {

	private static final long serialVersionUID = 5591885109312707090L;
	
	public AttributeDescriptionFileWizardAction() {
		super("attribute_wizard");
	}

	public void actionPerformed(ActionEvent e) {
		ExampleSourceConfigurationWizard wizard = new ExampleSourceConfigurationWizard(this);
		wizard.setVisible(true);
	}

	/** Returns an empty parameters object. */
	public Parameters getParameters() {
		return new Parameters();
	}

	/** Does nothing. */
	public void setParameters(Parameters parameters) {}
	
	/** Returns null. */
	public Process getProcess() {
		return null;
	}
}
