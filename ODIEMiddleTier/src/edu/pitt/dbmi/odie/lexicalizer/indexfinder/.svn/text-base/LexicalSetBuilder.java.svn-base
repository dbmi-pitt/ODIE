package edu.pitt.dbmi.odie.lexicalizer.indexfinder;

import java.beans.PropertyChangeListener;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.model.LexicalSet;
import edu.pitt.dbmi.odie.model.LexicalSetLanguageResource;
import edu.pitt.ontology.IResource;

public class LexicalSetBuilder {
	private MiddleTier middleTier;

	public LexicalSetBuilder(MiddleTier middleTier) {
		this.middleTier = middleTier;
	}

	public boolean createNewLexicalSet(LexicalSet lexicalSet,
			PropertyChangeListener plistener) {
		createNewDatabase(lexicalSet.getLocation());

		IndexFinderBuilder builder = new IndexFinderBuilder(middleTier, lexicalSet.getLocation());

		if (plistener != null)
			builder.addPropertyChangeListener(plistener);

		boolean success = builder
				.buildIndexFinderTables(getResources(lexicalSet
						.getLexicalSetLanguageResources()));
		if (success)
			success = saveLexicalSet(lexicalSet);
		else
			dropDatabase(lexicalSet.getLocation());

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
			middleTier.dropSchema(ODIEConstants.LEXSET_PREFIX + lexicalSet.getName());
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
			if(resource.getSubsetParentURI() == null){
				output.add(middleTier.getResourceForURI(resource
						.getLanguageResource().getURI()));
			}
			else{
				output.add(middleTier.getResourceForURI(resource.getSubsetParentURI()));
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
