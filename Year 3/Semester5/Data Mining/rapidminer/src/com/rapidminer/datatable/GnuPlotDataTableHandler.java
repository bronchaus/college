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
package com.rapidminer.datatable;

import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * This class can be used to create GnuPlot files from data tables.
 * 
 * @author Ingo Mierswa
 */
public class GnuPlotDataTableHandler implements DataTableListener {

	private static final Comparator<double[]> ROW_COMPARATOR = new Comparator<double[]>() {

		public int compare(double[] row1, double[] row2) {
			for (int i = 0; i < row1.length; i++) {
				if (row1[i] < row2[i])
					return -1;
				else if (row1[i] > row2[i])
					return +1;
			}
			return 0;
		};
	};
	
	private DataTable table;
	
	public GnuPlotDataTableHandler(DataTable table) {
		this.table = table;
		this.table.addDataTableListener(this);
	}
	
	public void dataTableUpdated(DataTable table) {
		this.table = table;
	}
	
	public void writeGNUPlot(PrintStream out, int x, int y, int[] z, String linetype, String additionalCommands, String terminal) {
		writeGNUPlotHeader(out, table.getColumnName(x), (y != -1 ? table.getColumnName(y) : table.getColumnName(z[0])), table.getColumnName(z[0]), additionalCommands, terminal);
		if (y != -1)
			out.print("splot ");
		else
			out.print("plot ");
		for (int i = 0; i < z.length; i++) {
			if (i > 0)
				out.print(", ");
			out.print("'-' title \"" + table.getColumnName(z[i]) + "\" with " + linetype);// linespoints");
		}
		out.println();
		for (int i = 0; i < z.length; i++) {
			if (y != -1)
				write3DGNUPlotData(out, x, y, z[i]);
			else
				write2DGNUPlotData(out, x, z[i]);
			out.println("e");
		}
	}

	private void writeGNUPlotHeader(PrintStream out, String xAxis, String yAxis, String zAxis, String additionalCommands, String terminal) {
		out.println("#!gnuplot");
		out.println("# Generated by " + getClass() + " on " + DateFormat.getDateTimeInstance().format(new Date()));
		if (xAxis != null)
			out.println("set xlabel \"" + xAxis + "\"");
		if (yAxis != null)
			out.println("set ylabel \"" + yAxis + "\"");
		if (zAxis != null)
			out.println("set zlabel \"" + zAxis + "\"");
		if (additionalCommands != null)
			out.println(additionalCommands);
		if (terminal != null)
			out.println("set terminal " + terminal);
	}
	
	private void write2DGNUPlotData(PrintStream out, int x, int y) {
		Collection<double[]> plot = new TreeSet<double[]>(ROW_COMPARATOR);
		Iterator<DataTableRow> i = table.iterator();
		while (i.hasNext()) {
			DataTableRow row = i.next();
			plot.add(new double[] { row.getValue(x), row.getValue(y) });
		}

		Iterator<double[]> j = plot.iterator();
		while (j.hasNext()) {
			double[] row = j.next();
			out.println(row[0] + "\t" + row[1]);
		}
	}

	private void write3DGNUPlotData(PrintStream out, int x, int y, int z) {
		Collection<double[]> plot = new TreeSet<double[]>(ROW_COMPARATOR);
		Iterator<DataTableRow> i = table.iterator();
		while (i.hasNext()) {
			DataTableRow row = i.next();
			plot.add(new double[] { row.getValue(x), row.getValue(y), row.getValue(z) });
		}

		double oldX = Double.NaN;
		Iterator<double[]> j = plot.iterator();
		while (j.hasNext()) {
			double[] row = j.next();
			if ((!Double.isNaN(oldX)) && (row[0] != oldX)) {
				out.println();
			}
			out.println(row[0] + "\t" + row[1] + "\t" + row[2]);
			oldX = row[0];
		}
	}
}
