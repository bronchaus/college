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
package com.rapidminer.gui.renderer;

import java.awt.Component;

import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.parameter.ParameterHandler;
import com.rapidminer.parameter.Parameters;
import com.rapidminer.report.Reportable;

/**
 * This is the renderer interface. A renderer is a visualization component
 * for all types of objects. In addition, it should also deliver an object
 * of the interface {@link Reportable} in order to support automatic reporting 
 * actions.
 * 
 * @author Ingo Mierswa
 */
public interface Renderer extends ParameterHandler {

	public String getName();

	//TODO: Find a solution for non existing IOCOntainer
	public Component getVisualizationComponent(Object renderable, IOContainer ioContainer);
	
	//TODO: Find a solution for non existing IOCOntainer
	public Reportable createReportable(Object renderable, IOContainer ioContainer, int desiredWidth, int desiredHeight);
	
	public String toString();

	public Parameters getParameters(InputPort inputPort);
	
	/** This method overrides all existing parameters. It must be used to ensure, that 
	 * input Port referencing attributes are connected to the correct port, since they are only created once and might 
	 * be initialized from another operator.
	 */
	public void updateParameters(InputPort inputPort);
}
