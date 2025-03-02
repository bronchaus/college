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
package com.rapidminer.gui.tools.dialogs.wizards.dataimport.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * A helper class for reading line based data formats
 * 
 * @author Tobias Malbrecht
 */
public class LineReader {
	private BufferedReader reader = null;
	
	public LineReader(File file) throws FileNotFoundException {
		reader = new BufferedReader((new InputStreamReader(new FileInputStream(file))));
	}
	
	public LineReader(File file, Charset encoding) throws FileNotFoundException {
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
	}
	
	public LineReader(InputStream stream, Charset encoding) {
		reader = new BufferedReader(new InputStreamReader(stream, encoding));
	}
	
	public String readLine() throws IOException {
		return reader.readLine();
	}
	
	public void close() throws IOException {
		reader.close();
	}
}
