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

package com.rapidminer.gui.new_plotter.gui.treenodes;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.tree.DefaultMutableTreeNode;

import com.rapidminer.gui.new_plotter.configuration.ValueSource;

/**
 * @author Nils Woehler
 *
 */
public class ValueSourceTreeNode extends DefaultMutableTreeNode implements Transferable {

	private static final long serialVersionUID = 1L;

	private final static String MIME_TYPE = DataFlavor.javaJVMLocalObjectMimeType + ";class=" + ValueSource.class.getName();
	public final static DataFlavor VALUE_SOURCE_FLAVOR = new DataFlavor(MIME_TYPE, "ValueSourceTreeNode");

	/**
	 * @param source
	 */
	public ValueSourceTreeNode(ValueSource source) {
		super(source, false);
	}

	public ValueSourceTreeNode(ValueSourceTreeNode node) {
		// shallow copy -- the new node has no parent or children
		super(node.getUserObject(), false);
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		DataFlavor[] dataFlavour = { VALUE_SOURCE_FLAVOR };
		return dataFlavour;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		if (flavor.match(VALUE_SOURCE_FLAVOR)) {
			return true;
		}
		return false;
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (isDataFlavorSupported(flavor)) {
			return this;
		}
		throw new UnsupportedFlavorException(flavor);
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public ValueSource getUserObject() {
		return (ValueSource) super.getUserObject();
	}
}
