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
import com.rapidminer.gui.viewer.AnovaCalculatorViewer;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.report.Reportable;
import com.rapidminer.tools.math.AnovaCalculator.AnovaSignificanceTestResult;

/**
 * 
 * @author Sebastian Land
 */
public class AnovaCalculatorViewerRenderer extends AbstractRenderer {

	@Override
	public Reportable createReportable(Object renderable, IOContainer ioContainer, int desiredWidth, int desiredHeight) {
		return null;
	}

	@Override
	public String getName() {
		return "ANOVA Calculator";
	}

	@Override
	public Component getVisualizationComponent(Object renderable, IOContainer ioContainer) {
		AnovaSignificanceTestResult result = (AnovaSignificanceTestResult) renderable;
		return new AnovaCalculatorViewer(result.getName(), result.getSumSquaresBetween(), result.getDf1(), result.getMeanSquaresBetween(), result.getFValue(), result.getProbability(), result.getSumSquaresResiduals(), result.getDf2(), result.getMeanSquaresResiduals(), result.getAlpha());
	}
}
