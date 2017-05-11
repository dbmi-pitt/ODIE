package edu.pitt.dbmi.odie.uima.church.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;

public class ODIE_GazetteerImpl implements ODIE_Gazetteer, SharedResourceObject {

	private HashSet<String> entries = new HashSet<String>();
	private boolean isCaseSensitive = false ;

	@Override
	public void load(DataResource aData) throws ResourceInitializationException {
		InputStream inStr = null;
		try {
			// open input stream to data
			inStr = aData.getInputStream();
			// read each line
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inStr));
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.replaceAll("^\\s", "").replaceAll("\\s*$", "");
				if (line.length() > 0) {
					if (!isCaseSensitive) {
						line = line.toLowerCase();
					}
					entries.add(line);
				}
			}
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		} finally {
			if (inStr != null) {
				try {
					inStr.close();
				} catch (IOException e) {
				}
			}
		}

	}

	@Override
	public boolean contains(String entry) {
		boolean result = (isCaseSensitive) ? entries.contains(entry) : entries.contains(entry.toLowerCase()) ;
		return result ;
	}

	@Override
	public boolean isCaseSensitive() {
		return this.isCaseSensitive ;
	}

	@Override
	public void setCaseSensitive(boolean isCaseSensitive) {
		this.isCaseSensitive = isCaseSensitive ;
	}

}
