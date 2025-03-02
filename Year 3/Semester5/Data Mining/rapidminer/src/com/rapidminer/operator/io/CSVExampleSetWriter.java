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
package com.rapidminer.operator.io;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.DateFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.Port;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.parameter.PortProvider;
import com.rapidminer.parameter.conditions.PortConnectedCondition;
import com.rapidminer.tools.Ontology;
import com.rapidminer.tools.io.Encoding;

/**
 * <p>
 * This operator can be used to write data into CSV files (Comma Separated
 * Values). The values and columns are separated by &quot;;&quot;. Missing data
 * values are indicated by empty cells.
 * </p>
 * 
 * @author Ingo Mierswa
 */
public class CSVExampleSetWriter extends AbstractStreamWriter {

	/** The parameter name for &quot;The CSV file which should be written.&quot; */
	public static final String PARAMETER_CSV_FILE = "csv_file";

	/** The parameter name for the column separator parameter. */
	public static final String PARAMETER_COLUMN_SEPARATOR = "column_separator";

	/** Indicates if the attribute names should be written as first row. */
	public static final String PARAMETER_WRITE_ATTRIBUTE_NAMES = "write_attribute_names";

	/**
	 * Indicates if nominal values should be quoted with double quotes. Quotes
	 * inside of nominal values will be escaped by a backslash.
	 */
	public static final String PARAMETER_QUOTE_NOMINAL_VALUES = "quote_nominal_values";

	public static final String PARAMETER_APPEND_FILE = "append_to_file";

	/**
	 * Indicates if date attributes are written as a formated string or as
	 * milliseconds past since January 1, 1970, 00:00:00 GMT
	 */
	// TODO introduce parameter which allows to determine the written format see
	// Nominal2Date operator
	public static final String PARAMETER_FORMAT_DATE = "format_date_attributes";

	public CSVExampleSetWriter(OperatorDescription description) {
		super(description);
	}

	public static void writeCSV(ExampleSet exampleSet, PrintWriter out, String colSeparator, boolean quoteNomValues, boolean writeAttribNames, boolean formatDate) {
		String columnSeparator = colSeparator;
		boolean quoteNominalValues = quoteNomValues;

		// write column names
		if (writeAttribNames) {
			Iterator<Attribute> a = exampleSet.getAttributes().allAttributes();
			boolean first = true;
			while (a.hasNext()) {
				if (!first)
					out.print(columnSeparator);
				Attribute attribute = a.next();
				String name = attribute.getName();
				if (quoteNominalValues) {
					name = name.replaceAll("\"", "'");
					name = "\"" + name + "\"";
				}
				out.print(name);
				first = false;
			}
			out.println();
		}

		// write data
		for (Example example : exampleSet) {
			Iterator<Attribute> a = exampleSet.getAttributes().allAttributes();
			boolean first = true;
			while (a.hasNext()) {
				Attribute attribute = a.next();
				if (!first)
					out.print(columnSeparator);
				if (!Double.isNaN(example.getValue(attribute))) {
					if (attribute.isNominal()) {
						String stringValue = example.getValueAsString(attribute);
						if (quoteNominalValues) {
							stringValue = stringValue.replaceAll("\"", "'");
							stringValue = "\"" + stringValue + "\"";
						}
						out.print(stringValue);
					} else {
						Double value = example.getValue(attribute);
						if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(attribute.getValueType(), Ontology.DATE_TIME)) {
							if (formatDate) {
								Date date = new Date(value.longValue());
								String s = DateFormat.getInstance().format(date);
								out.print(s);
							} else {
								out.print(value);
							}
						} else {
							out.print(value);
						}

					}
				}
				first = false;
			}
			out.println();
		}
	}

	@Override
	public void writeStream(ExampleSet exampleSet, java.io.OutputStream outputStream) throws OperatorException {

		String columnSeparator = getParameterAsString(PARAMETER_COLUMN_SEPARATOR);
		boolean quoteNominalValues = getParameterAsBoolean(PARAMETER_QUOTE_NOMINAL_VALUES);
		boolean writeAttribNames = getParameterAsBoolean(PARAMETER_WRITE_ATTRIBUTE_NAMES);
		boolean formatDate = getParameterAsBoolean(PARAMETER_FORMAT_DATE);
		PrintWriter out = null;
		try {
			out = new PrintWriter(new OutputStreamWriter(outputStream, Encoding.getEncoding(this)));
			writeCSV(exampleSet, out, columnSeparator, quoteNominalValues, writeAttribNames, formatDate);
			out.flush();
		} finally {
			if (out != null) {
				out.close();
			}
		}

	}

	@Override
	protected boolean supportsEncoding() {
		return true;
	}

	@Override
	protected boolean shouldAppend() {
		return getParameterAsBoolean(PARAMETER_APPEND_FILE);
	}

	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = new LinkedList<ParameterType>();
		types.add(makeFileParameterType());
		// types.add(new ParameterTypeFile(PARAMETER_CSV_FILE,
		// "The CSV file which should be written.", "csv", false));
		types.add(new ParameterTypeString(PARAMETER_COLUMN_SEPARATOR, "The column separator.", ";", false));
		types.add(new ParameterTypeBoolean(PARAMETER_WRITE_ATTRIBUTE_NAMES, "Indicates if the attribute names should be written as first row.", true, false));
		types.add(new ParameterTypeBoolean(PARAMETER_QUOTE_NOMINAL_VALUES, "Indicates if nominal values should be quoted with double quotes.", true, false));
		types.add(new ParameterTypeBoolean(PARAMETER_FORMAT_DATE,
				"Indicates if date attributes are written as a formated string or as milliseconds past since January 1, 1970, 00:00:00 GMT", true, true));
		ParameterType type = new ParameterTypeBoolean(PARAMETER_APPEND_FILE,
				"Indicates if new content should be appended to the file or if the pre-existing file content should be overwritten.", false, false);
		type.registerDependencyCondition(new PortConnectedCondition(this, new PortProvider() {

			@Override
			public Port getPort() {
				return fileOutputPort;
			}
		}, true, false));
		types.add(type);
		types.addAll(super.getParameterTypes());
		return types;
	}

	@Override
	String getFileParameterName() {
		return PARAMETER_CSV_FILE;
	}

	@Override
	String[] getFileExtensions() {
		return new String[] { "csv" };
	}
}
