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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.JButton;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.rapidminer.gui.properties.celleditors.value.TextValueCellEditor;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.syntax.JEditTextArea;
import com.rapidminer.operator.Operator;
import com.rapidminer.parameter.ParameterTypeText;
import com.rapidminer.tools.I18N;


/**
 * A Dialog displaying a {@link JEditTextArea}. This can be used to type some lengthy
 * text instead of the short text fields usually used for ParameterTypeStrings. This
 * dialog is used by the {@link TextValueCellEditor}.
 * 
 * @author Ingo Mierswa, Tobias Malbrecht, Sebastian Land
 */
public class TextPropertyDialog extends PropertyDialog {

    private static final long serialVersionUID = 8574310060170861505L;

    private String text = null;
    
    private boolean ok = false;

    private RSyntaxTextArea textArea = new RSyntaxTextArea();
    
    private JButton resizeButton = new JButton();
    
    public TextPropertyDialog(final ParameterTypeText type, String text, Operator operator) {
        super(type, "text");
        this.text = text;
        ResourceAction resizeAction = new ResourceAction(true, "text_dialog.enlarge") {
			
			private static final long serialVersionUID = 8857840715142145951L;

			@Override
			public void actionPerformed(ActionEvent e) {
				Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
				Dimension dim = new Dimension((int)(screenDim.width * 0.9), (int)(screenDim.height* 0.9));
				Dimension currentSize = getSize();
				if (currentSize.getHeight() != dim.getHeight() && currentSize.getWidth() != dim.getWidth()) {
					setSize(dim);
					setLocationRelativeTo(null);
					resizeButton.setText(I18N.getMessage(I18N.getGUIBundle(), "gui.action.text_dialog.shrink.label"));
					resizeButton.setToolTipText(I18N.getMessage(I18N.getGUIBundle(), "gui.action.text_dialog.shrink.tip"));
					resizeButton.setMnemonic(I18N.getMessage(I18N.getGUIBundle(), "gui.action.text_dialog.shrink.mne").charAt(0));
				} else {
					setSize(getDefaultSize(NORMAL));
					setDefaultLocation();
					resizeButton.setText(I18N.getMessage(I18N.getGUIBundle(), "gui.action.text_dialog.enlarge.label"));
					resizeButton.setToolTipText(I18N.getMessage(I18N.getGUIBundle(), "gui.action.text_dialog.enlarge.tip"));
					resizeButton.setMnemonic(I18N.getMessage(I18N.getGUIBundle(), "gui.action.text_dialog.enlarge.mne").charAt(0));
				}
			}
		};
		
        resizeButton = new JButton(resizeAction);
        
        textArea.setDocument(new RSyntaxDocument(type.getTextType().getSyntaxIdentifier()));
        textArea.setText(text);
        textArea.setAnimateBracketMatching(type.getTextType().isBracketMatching());
        textArea.setAutoIndentEnabled(type.getTextType().isAutoIntending());
        textArea.setAutoscrolls(true);
        layoutDefault(new RTextScrollPane(textArea), NORMAL, resizeButton, makeOkButton("text_property_dialog_apply"), makeCancelButton());
        
        textArea.requestFocusInWindow();
    }

    @Override
    protected void ok() {
    	this.ok = true;
        this.text = this.textArea.getText();
        dispose();
    }
    
    @Override
    protected void cancel() {
    	this.ok = false;
    	dispose();
    }
    
    @Override
	public boolean isOk() {
    	return this.ok;
    }
    
    public String getText() {
        return this.text;
    }
}
