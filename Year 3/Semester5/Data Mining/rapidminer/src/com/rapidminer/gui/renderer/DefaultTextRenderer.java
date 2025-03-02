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

import javax.swing.JEditorPane;
import javax.swing.JLabel;

import com.rapidminer.gui.tools.ExtendedJScrollPane;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.ResultObject;
import com.rapidminer.report.Reportable;
import com.rapidminer.tools.Tools;

/**
 * This is the abstract renderer superclass for all renderers which
 * should be a simple text based on the toString method of a given
 * renderable.
 * 
 * @author Ingo Mierswa
 */
public class DefaultTextRenderer extends NonGraphicalRenderer {

	public String getName() {
		return "Text View";
	}

	public Component getVisualizationComponent(Object renderable, IOContainer ioContainer) {
		JEditorPane resultText = new JEditorPane();
		resultText.setContentType("text/html");
		resultText.setBorder(javax.swing.BorderFactory.createEmptyBorder(11, 11, 11, 11));
		resultText.setEditable(false);
		resultText.setBackground((new JLabel()).getBackground());


		if (renderable instanceof ResultObject) {
			ResultObject result = (ResultObject)renderable;
			String str = Tools.escapeHTML(result.toResultString());
			resultText.setText("<html><h1>" + result.getName() + "</h1><pre>" + str + "</pre></html>");			
		} else {
			String str = Tools.escapeHTML(renderable.toString());
			resultText.setText("<html><h1>" + renderable.getClass().getSimpleName() + "</h1><pre>" + str + "</pre></html>");
		}
		return new ExtendedJScrollPane(resultText);
	}

	@Override
	public Reportable createReportable(Object renderable, IOContainer ioContainer) {
		return new DefaultReadable(renderable.toString());
	}
}
