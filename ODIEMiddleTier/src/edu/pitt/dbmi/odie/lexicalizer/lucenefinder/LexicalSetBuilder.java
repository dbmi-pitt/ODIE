package edu.pitt.dbmi.odie.lexicalizer.lucenefinder;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.model.LexicalSet;
import edu.pitt.dbmi.odie.model.LexicalSetLanguageResource;
import edu.pitt.ontology.IOntology;
import edu.pitt.ontology.IResource;

public class LexicalSetBuilder {
	private MiddleTier middleTier;

	public LexicalSetBuilder(MiddleTier middleTier) {
		this.middleTier = middleTier;
	}

	public boolean createNewLexicalSet(LexicalSet lexicalSet,
			PropertyChangeListener plistener) {

		//first save is so that we can get the ID for the object. ID is required to
		//build the location string.
		boolean success = saveLexicalSet(lexicalSet);
		if(!success)
			return false;

		StringBuffer sb = new StringBuffer(this.middleTier.getConfiguration().getLuceneIndexDirectory()) ;
		sb.append(File.separator) ;
		sb.append(edu.pitt.dbmi.odie.ODIEConstants.LUCENEFINDER_DIRECTORY) ;
		sb.append(File.separator) ;
		sb.append(lexicalSet.getId()) ;
		String lexicalSetLuceneIndexDirectoryName =  sb.toString();

		//second save is to save the location
		lexicalSet.setLocation(lexicalSetLuceneIndexDirectoryName);
		success = saveLexicalSet(lexicalSet);
		
		if(!success){
			removeLexicalSet(lexicalSet);
			return success;
		}
		
		ODIE_LuceneNerBuilder builder = new ODIE_LuceneNerBuilder(middleTier, lexicalSet.getLocation());

		if (plistener != null)
			builder.addPropertyChangeListener(plistener);
		success = builder
				.buildIndexFinderTables(lexicalSet.getLocation(), getResources(lexicalSet
						.getLexicalSetLanguageResources()));

		if(!success){
			removeLexicalSet(lexicalSet);
		}
			
		return success;
	}

	private boolean saveLexicalSet(LexicalSet lexicalSet) {
		try {
			middleTier.persist(lexicalSet);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean removeLexicalSet(LexicalSet lexicalSet) {
		try {
			middleTier.delete(lexicalSet);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private List<IResource> getResources(
			List<LexicalSetLanguageResource> resourceList) {
		List<IResource> output = new ArrayList<IResource>();

		for (LexicalSetLanguageResource resource : resourceList){
			IOntology o = (IOntology) middleTier.getResourceForURI(resource
					.getLanguageResource().getURI());
			
			if(resource.getSubsetParentURI() == null){
				output.add(o);
			}
			else{
				output.add(o.getClass(resource.getSubsetParentURI().toASCIIString()));
			}
		}

		return output;
	}

	private void dropDatabase(String schemaName) {
		middleTier.dropSchema(schemaName);
	}

	private void createNewDatabase(String schemaName) {
		middleTier.createSchema(schemaName);
	}
}
