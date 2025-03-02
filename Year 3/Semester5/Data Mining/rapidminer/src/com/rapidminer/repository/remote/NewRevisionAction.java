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
package com.rapidminer.repository.remote;

import java.awt.event.ActionEvent;

import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.SwingTools;

/**
 * 
 * @author Simon Fischer
 *
 */
public class NewRevisionAction extends ResourceAction {

	private static final long serialVersionUID = 1L;
	
	private RemoteProcessEntry entry;

	public NewRevisionAction(RemoteProcessEntry remoteProcessEntry) {
		super("repository.new_revision");
		this.entry = remoteProcessEntry;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			entry.getRepository().getRepositoryService().startNewRevision(entry.getPath());
			entry.getContainingFolder().refresh();
		} catch (Exception e1) {
			SwingTools.showSimpleErrorMessage("cannot_store_process_in_repository", e1, entry.getPath());
		}
	}
}
