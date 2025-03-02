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
package com.rapidminer.tools.jdbc.connection;

import java.io.File;

import com.rapidminer.tools.jdbc.DatabaseService;


/**
 * @author Tobias Malbrecht
 */
public class AccessConnectionEntry extends ConnectionEntry {

	private File file = null;
	
	public AccessConnectionEntry() {
		this(null);
	}
	
	public AccessConnectionEntry(File file) {
		super("", DatabaseService.getJDBCProperties("ODBC Bridge (e.g. Access)"));
		user = "noUser";
		password = "noPassword".toCharArray();
		this.file = file; 
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	
	@Override
	public String getURL() {
		return "jdbc:odbc:DRIVER={Microsoft Access Driver (*.mdb)};DBQ=" + file.getAbsolutePath();
	}
}
