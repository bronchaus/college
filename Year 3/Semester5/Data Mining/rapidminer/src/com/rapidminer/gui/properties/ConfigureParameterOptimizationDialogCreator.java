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
package com.rapidminer.gui.properties;

import com.rapidminer.gui.wizards.AbstractConfigurationWizardCreator;
import com.rapidminer.gui.wizards.ConfigurationListener;
import com.rapidminer.parameter.ParameterType;

/**
 * The creator for the parameter specification dialog.
 * 
 * @author Tobias Malbrecht
 */
public class ConfigureParameterOptimizationDialogCreator extends AbstractConfigurationWizardCreator {
	
	public static final long serialVersionUID = 32891471478295L;
	
	@Override
	public String getI18NKey() {
		return "configure";
	}
	
	public void createConfigurationWizard(ParameterType type, ConfigurationListener listener) {
		(new ConfigureParameterOptimizationDialog(type, listener)).setVisible(true);
	}
}

