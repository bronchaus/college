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
package com.rapidminer.gui.look.fc;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import sun.awt.AppContext;

/**
 * A helper class containing some tools.
 *
 * @author Ingo Mierswa
 */
public class Tools {

	private static final Object sharedFrameKey = JOptionPane.class;

	private static final Object sharedOwnerFrameKey = new StringBuffer("SwingUtilities.sharedOwnerFrame");

	private static int width;
	
	private static int height;

	private static BufferedImage dest, dest2;

	private static Graphics g;

	private static float[] blurMatrix = { .055f, .055f, .055f, .055f, .66f, .055f, .055f, .055f, .055f };

	private static Kernel blurKernel = new Kernel(3, 3, blurMatrix);

	private static ConvolveOp blurOperator = new ConvolveOp(blurKernel, ConvolveOp.EDGE_NO_OP, null);

	private static float[] sharpenMatrix = { -0.03f, -.03f, -0.03f, -.03f, 1.24f, -0.03f, -0.03f, -0.03f, -0.03f };

	private static Kernel sharpenKernel = new Kernel(3, 3, sharpenMatrix);

	private static ConvolveOp sharpenOperator = new ConvolveOp(sharpenKernel, ConvolveOp.EDGE_NO_OP, null);

	private static BufferedImage srcBImage;
	
	
	private static Object getAppContext(Object key) {
		return AppContext.getAppContext().get(key);
	}

	private static void putAppContext(Object key, Object value) {
		AppContext.getAppContext().put(key, value);
	}

	private static Frame getSharedOwnerFrame() throws HeadlessException {
		Frame sharedOwnerFrame = (Frame) getAppContext(sharedOwnerFrameKey);
		if (sharedOwnerFrame == null) {
			sharedOwnerFrame = new Frame() {
				
				private static final long serialVersionUID = -3949166916666457066L;
				
				@Override
				public void setVisible(boolean value) {
					// This frame can never be shown
				}
				
				@Override
				public synchronized void dispose() {
					try {
						getToolkit().getSystemEventQueue();
						super.dispose();
					} catch (Exception e) {
						// untrusted code not allowed to dispose
					}
				}
			};
			putAppContext(sharedOwnerFrameKey, sharedOwnerFrame);
		}
		return sharedOwnerFrame;
	}

	private static Frame getRootFrame() throws HeadlessException {
		Frame sharedFrame = (Frame) getAppContext(sharedFrameKey);
		if (sharedFrame == null) {
			sharedFrame = getSharedOwnerFrame();
			putAppContext(sharedFrameKey, sharedFrame);
		}
		return sharedFrame;
	}

	public static Window getWindowForComponent(Component parentComponent) throws HeadlessException {
		if (parentComponent == null) {
			return getRootFrame();
		}
		if ((parentComponent instanceof Frame) || (parentComponent instanceof Dialog)) {
			return (Window) parentComponent;
		}
		return getWindowForComponent(parentComponent.getParent());
	}

	public static ImageIcon getSmallSystemIcon(Image img) throws Exception {
		if ((img.getWidth(null) > 20) || (img.getHeight(null) > 20)) {
			if (img.getWidth(null) > img.getHeight(null)) {
				width = 18;
				height = (img.getHeight(null) * 18) / img.getWidth(null);
			} else {
				height = 18;
				width = (img.getWidth(null) * 18) / img.getHeight(null);
			}
		} else {
			return new ImageIcon(img);
		}

		dest = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		dest2 = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

		g = dest.getGraphics();
		g.drawImage(img, 1, 1, width, height, null);

		g.dispose();

		blurOperator.filter(dest, dest2);

		return new ImageIcon(dest2);
	}

	public static ImageIcon getBigSystemIcon(Image image) throws Exception {
		if ((image.getWidth(null) < 20) || (image.getHeight(null) < 20)) {
			if (image.getWidth(null) > image.getHeight(null)) {
				width = 24;
				height = (image.getHeight(null) * 24) / image.getWidth(null);
			} else {
				height = 24;
				width = (image.getWidth(null) * 24) / image.getHeight(null);
			}
		} else {
			return new ImageIcon(image);
		}

		dest = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		dest2 = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

		g = dest.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, 22, 22);
		g.drawImage(image, 0, 0, width, height, null);

		g.dispose();

		sharpenOperator.filter(dest, dest2);

		return new ImageIcon(dest);
	}

	public static Image getScaledInstance(File file) throws Exception {
		srcBImage = javax.imageio.ImageIO.read(file);

		if (srcBImage.getWidth() > srcBImage.getHeight()) {
			width = 100;
			height = (srcBImage.getHeight() * 100) / srcBImage.getWidth();
		} else {
			height = 100;
			width = (srcBImage.getWidth() * 100) / srcBImage.getHeight();
		}

		dest = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		dest2 = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

		g = dest.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, 100, 100);
		g.drawImage(srcBImage, 0, 0, width, height, null);

		g.dispose();
		srcBImage = null;
		blurOperator.filter(dest, dest2);
		
		return dest2;
	}
}
