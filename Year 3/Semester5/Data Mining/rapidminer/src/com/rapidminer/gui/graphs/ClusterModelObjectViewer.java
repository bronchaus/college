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
package com.rapidminer.gui.graphs;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.rapidminer.ObjectVisualizer;
import com.rapidminer.gui.tools.ExtendedJScrollPane;
import com.rapidminer.operator.clustering.HierarchicalClusterNode;
import com.rapidminer.tools.ObjectVisualizerService;

/**
 * The graph object viewer for cluster nodes in a cluster model.
 *  
 * @author Ingo Mierswa
 */
public class ClusterModelObjectViewer implements GraphObjectViewer, ListSelectionListener {

    private static final long serialVersionUID = -1849689229737482745L;
    
    private DefaultListModel model = new DefaultListModel();
    
    private JList listComponent = new JList(this.model);

	private Object clusterModel;
    
    public ClusterModelObjectViewer(Object clusterModel) {
    	this.clusterModel = clusterModel;
    }
    
    public JComponent getViewerComponent() { 
        listComponent.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listComponent.addListSelectionListener(this);
        listComponent.setVisibleRowCount(-1);
        return new ExtendedJScrollPane(listComponent);
    }

    public void showObject(Object object) {
        this.model.removeAllElements();
        if (object != null) {
        	HierarchicalClusterNode node = (HierarchicalClusterNode)object;
            for (Object id: node.getExampleIdsInSubtree()) 
                this.model.addElement(id);
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            Object id = listComponent.getSelectedValue();
            if (id != null) {
            	ObjectVisualizer visualizer = ObjectVisualizerService.getVisualizerForObject(clusterModel);
            	visualizer.startVisualization(id);
            }
        }
    }
}
