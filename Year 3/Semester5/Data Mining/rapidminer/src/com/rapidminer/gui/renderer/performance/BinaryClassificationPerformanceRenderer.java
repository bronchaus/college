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
package com.rapidminer.gui.renderer.performance;

import java.awt.Component;

import com.rapidminer.gui.renderer.AbstractRenderer;
import com.rapidminer.gui.viewer.ConfusionMatrixViewer;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.performance.BinaryClassificationPerformance;
import com.rapidminer.report.Reportable;

/**
 * 
 * @author Sebastian Land
 */
public class BinaryClassificationPerformanceRenderer extends AbstractRenderer {

	@Override
	public Reportable createReportable(Object renderable, IOContainer ioContainer, int desiredWidth, int desiredHeight) {
		BinaryClassificationPerformance performance = (BinaryClassificationPerformance) renderable;
		ConfusionMatrixViewer viewer = new ConfusionMatrixViewer(performance.getTitle(), 
				new String[] { performance.getNegativeClassName(), performance.getPositiveClassName()},
				performance.getCounter()); 
		return viewer;
	}

	@Override
	public String getName() {
		return "Binary Classification Performance";
	}

	@Override
	public Component getVisualizationComponent(Object renderable, IOContainer ioContainer) {
		BinaryClassificationPerformance performance = (BinaryClassificationPerformance) renderable;
		ConfusionMatrixViewer viewer = new ConfusionMatrixViewer(performance.getTitle(), 
				new String[] { performance.getNegativeClassName(), performance.getPositiveClassName()},
				performance.getCounter()); 
		return viewer;
	}

}
