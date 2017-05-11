package edu.pitt.dbmi.odie.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

@Deprecated
public class FileDocumentImpl extends Document {

	File file;

	public FileDocumentImpl(File file) {
		super();
		this.file = file;
	}

	private void readTextFromFile() {
		Reader in = null;
		try {
			FileReader reader = new java.io.FileReader(file);

			in = new BufferedReader(reader);

			StringBuffer buffer = new StringBuffer(512);
			char[] readBuffer = new char[512];
			int n = in.read(readBuffer);
			while (n > 0) {
				buffer.append(readBuffer, 0, n);
				n = in.read(readBuffer);
			}

			setText(buffer.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public File getFile() {
		return file;
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public long getSize() {
		return file.length();
	}

	@Override
	public String getText() {
		if (getText() == null && file != null && file.isFile()) {
			readTextFromFile();
		}
		return getText();
	}

}
