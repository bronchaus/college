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

import javax.swing.Icon;

import com.rapidminer.Process;
import com.rapidminer.gui.MainFrame;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.SwingTools;


/**
 * Start the corresponding action.
 * 
 * @author Ingo Mierswa
 */
public class RunAction extends ResourceAction {

	private static final long serialVersionUID = 1;
	
	private static final Icon ICON_PLAY_SMALL   = SwingTools.createIcon("16/media_play.png");
	private static final Icon ICON_PLAY_LARGE   = SwingTools.createIcon("24/media_play.png");
	private static final Icon ICON_RESUME_SMALL = SwingTools.createIcon("16/media_play_green.png");
	private static final Icon ICON_RESUME_LARGE = SwingTools.createIcon("24/media_play_green.png");
	
    private final MainFrame mainFrame;    
    
    public RunAction(MainFrame mainFrame) {
        super("run");
        this.mainFrame = mainFrame;
        setCondition(PROCESS_RUNNING, DISALLOWED);
        setCondition(EDIT_IN_PROGRESS, DISALLOWED);
    }

    public void actionPerformed(ActionEvent e) {
    	mainFrame.getProcessPanel().requestFocus();
    	mainFrame.runProcess();
    }
    
    public void setState(int processState) {
    	switch (processState) {
    	case Process.PROCESS_STATE_PAUSED:
    		putValue(LARGE_ICON_KEY, ICON_RESUME_LARGE);
			putValue(SMALL_ICON, ICON_RESUME_SMALL);
			break;
    	default:
    		putValue(LARGE_ICON_KEY, ICON_PLAY_LARGE);
			putValue(SMALL_ICON, ICON_PLAY_SMALL);
    		
    	}
    }
    
//    public void updateState() {    	
//        if (mainFrame.getProcessState() == Process.PROCESS_STATE_PAUSED) {
//        	putValue(SMALL_ICON, RESUME.getValue(SMALL_ICON));
//        	putValue(LARGE_ICON_KEY, RESUME.getValue(LARGE_ICON_KEY));          
//            setCondition(PROCESS_RUNNING, DONT_CARE);
//        } else {
//        	putValue(SMALL_ICON, RUN.getValue(SMALL_ICON));
//        	putValue(LARGE_ICON_KEY, RUN.getValue(LARGE_ICON_KEY));
//        	setCondition(PROCESS_RUNNING, DONT_CARE);            
//        }
//    }
}
