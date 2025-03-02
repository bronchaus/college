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

import java.util.Comparator;

import com.rapidminer.tools.jdbc.JDBCProperties;

/**
 * A database connection, offered by the DatabaseConnectionService. Each entry stores the needed
 * informations like data base type, it's sql properties, as well as user name and password.
 * 
 * @author Tobias Malbrecht, Sebastian Land
 */
public abstract class ConnectionEntry implements Comparable {
	public static Comparator<ConnectionEntry> COMPARATOR = new Comparator<ConnectionEntry>() {

		@Override
		public int compare(ConnectionEntry o1, ConnectionEntry o2) {
			return o1.toString().compareTo(o2.toString());
		}
	};
	
	protected String name;
	
	protected JDBCProperties properties;
	
	protected String user = null;
	
	protected char[] password = null;

	public ConnectionEntry() {
		this("", JDBCProperties.createDefaultJDBCProperties());
	}
	
	public ConnectionEntry(String name, JDBCProperties properties) {
		this.name = name;
		this.properties = properties;
	}

	@Override
	public int compareTo(Object o) {
		return this.name.compareTo(((ConnectionEntry) o).name);
	}
	
	public String getName() {
		return name;
	}
	
	public JDBCProperties getProperties() {
		return properties;
	}
	
	public String getUser() {
		return user;
	}
	
	public char[] getPassword() {
		return password;
	}
	
	public abstract String getURL();
	
	@Override
	public String toString() {
		return name;
	}
	
	
	/**
	 * This method returns if the connection entry is changeable by user.
	 */
	public boolean isReadOnly() {
		return false;
	}
	
	/**
	 * This method returns a name if this entry has been generated by a remote repository and must not 
	 * be stored in file on program termination.
	 * Otherwise null is returned.
	 */
	public String getRepository() {
		return null;
	}
}
