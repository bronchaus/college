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
import java.awt.Graphics;

import com.rapidminer.gui.plotter.PlotterPanel;
import com.rapidminer.report.Renderable;

/**
 * The default component renderer.
 * 
 * @author Ingo Mierswa
 */
public class DefaultComponentRenderable implements Renderable {

	private Component component;
	
	public DefaultComponentRenderable(Component component) {
		this.component = component;
	}

	public int getRenderHeight(int preferredHeight) {
		return preferredHeight;
	}

	public int getRenderWidth(int preferredWidth) {
		return preferredWidth;
	}

	public void prepareRendering() {}

    public void finishRendering() {}
    
	public void render(Graphics graphics, int width, int height) {
		component.setSize(width, height);
		if (component instanceof PlotterPanel) {
			((PlotterPanel)component).getPlotterSettings().getPlotter().render(graphics, width, height);
		} else {
			component.paint(graphics);
		}
	}
}
