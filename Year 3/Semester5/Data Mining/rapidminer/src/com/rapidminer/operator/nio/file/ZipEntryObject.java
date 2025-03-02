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
package com.rapidminer.operator.nio.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.rapidminer.operator.OperatorException;
import com.rapidminer.tools.Tools;

/**
 * 
 * @author Nils Woehler
 * 
 */
public class ZipEntryObject extends FileObject {

	private static final long serialVersionUID = 1L;

	private File file = null;

	private ZipEntry entry;
	private ZipFile zipFile;

	public ZipEntryObject(ZipEntry entry, ZipFile zipFile) {
		super();
		this.entry = entry;
		this.zipFile = zipFile;
	}

	@Override
	public InputStream openStream() throws OperatorException {
		try {
			return zipFile.getInputStream(entry);
		} catch (IOException e) {
			throw new OperatorException("302", e, entry.getName(),
					e.getMessage());
		}
	}

	@Override
	public File getFile() throws OperatorException {
		if (file == null) {
			try {
				file = File.createTempFile("rm_file_", ".dump");
				FileOutputStream fos = new FileOutputStream(file);
				InputStream in = zipFile.getInputStream(entry);
				Tools.copyStreamSynchronously(in, fos, true);
				file.deleteOnExit();
			} catch (IOException e) {
				throw new OperatorException("303", e, file, e.getMessage());
			}

			return file;
		} else {
			return file;
		}

	}

	@Override
	public String toString() {
		return (file != null) ? ("ZipEntry "+entry.getName()+" stored in temporary file: " + file
				.getAbsolutePath()) : ("ZipEntry "+entry.getName()+" from ZipFile " + zipFile
				.getName());
	}

	@Override
	protected void finalize() throws Throwable {
		if (file != null) {
			file.delete();
		}
		super.finalize();
	}

}
