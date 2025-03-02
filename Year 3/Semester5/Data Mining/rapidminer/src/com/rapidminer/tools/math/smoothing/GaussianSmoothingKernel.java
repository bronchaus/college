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
package com.rapidminer.tools.math.smoothing;

/**
 * This class implements the gaussian smoothing kernel. On distance 0, the weight is normalized to 1, and on distance 1
 * it is nearly 0. But this kernel will never vanish completely.
 * 
 * @author Sebastian Land
 * 
 */
public class GaussianSmoothingKernel extends SmoothingKernel {

	private static final long serialVersionUID = -702562270260024025L;

	@Override
	public double getWeight(double distance) {
		double toQuad = 2.5d * distance;
		return Math.exp(-(toQuad * toQuad) / 2);
	}
	
	@Override
	public String toString() {
		return "Gaussian Smoothing Kernel";
	}

}
