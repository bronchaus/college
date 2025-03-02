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
package com.rapidminer.example.table;


/**
 * Implementation of DataRow that is backed by primitive arrays. Should always
 * be used if more than 50% of the data is sparse. As fast (or even faster than
 * map implementation) but needs considerably less memory. This implementation uses
 * float arrays instead of double arrays which might reduce the used memory even more.
 * 
 * @author Ingo Mierswa, Shevek
 */
public class FloatSparseArrayDataRow extends AbstractSparseArrayDataRow {

    private static final long serialVersionUID = -2445500346242180129L;
    
	/** Stores the used attribute values. */
    private float[] values;

    /** Creates an empty sparse array data row with size 0. */
    public FloatSparseArrayDataRow() {
        this(0);
    }

    /** Creates a sparse array data row of the given size. */
    public FloatSparseArrayDataRow(int size) {
    	super(size);
        values = new float[size];
    }

    @Override
	protected void swapValues(int a, int b) {
    	float tt = values[a];
    	values[a] = values[b];
    	values[b] = tt;
    }

    @Override
	public void resizeValues(int length) {
    	float[] d = new float[length];
    	System.arraycopy(values, 0, d, 0, Math.min(values.length, length));
    	values = d;
    }
    
    @Override
	public void removeValue(int index) {
    	System.arraycopy(values, index + 1, values, index, (values.length - (index + 1)));
    }
    	 
    /** Returns the desired data for the given attribute. */
    @Override
	public double getValue(int index) {
    	return values[index];
    }

    /** Sets the given data for the given attribute. */
    @Override
	public void setValue(int index, double v) {
    	values[index] = (float)v;
    }

    @Override
	protected double[] getAllValues() {
    	double[] result = new double[this.values.length];
    	for (int i = 0; i < result.length; i++)
    		result[i] = this.values[i];
    	return result;
    }
    
	@Override
	public int getType() {
		return DataRowFactory.TYPE_FLOAT_SPARSE_ARRAY;
	}
}
