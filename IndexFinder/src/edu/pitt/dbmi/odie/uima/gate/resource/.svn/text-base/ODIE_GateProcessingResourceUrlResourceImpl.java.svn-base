package edu.pitt.dbmi.odie.uima.gate.resource;

import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;

import edu.upmc.opi.caBIG.common.CaBIG_ReadWriteTextFile;

public class ODIE_GateProcessingResourceUrlResourceImpl implements
		ODIE_GateProcessingResourceUrlResource, SharedResourceObject {
	
	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ODIE_GateProcessingResourceUrlResourceImpl.class);

	private URL processingResourceUrl;

	public void load(DataResource data) throws ResourceInitializationException {
		this.processingResourceUrl = data.getUrl();
		logger.debug("Loaded URL wrapper for "
				+ this.processingResourceUrl.toString() + " the object id is "
				+ this);
		String contents = CaBIG_ReadWriteTextFile.readFileFromURL(this.processingResourceUrl) ;
	    logger.debug(contents) ;
	}

	public URL getUrl() {
		logger.debug("getUrl returning "
				+ this.processingResourceUrl.toString() + " the object id is "
				+ this);
		return this.processingResourceUrl;
	}

}
