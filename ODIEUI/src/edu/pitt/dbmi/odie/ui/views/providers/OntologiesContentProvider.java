package edu.pitt.dbmi.odie.ui.views.providers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.ontology.IOntology;
import edu.pitt.ontology.bioportal.BioPortalHelper;

public class OntologiesContentProvider implements ITreeContentProvider {
	
	IOntology[] ontologyArr;
	List<IOntology> proposalOntologies = new ArrayList<IOntology>();
	List<IOntology> bioportalOntologies = new ArrayList<IOntology>();
	List<IOntology> fileOntologies = new ArrayList<IOntology>();
	
	public static final String BIOPORTAL_ONTOLOGY_LABEL = "Bioportal Imports";
	public static final String FILE_ONTOLOGY_LABEL = "File Imports";
	public static final String PROPOSAL_ONTOLOGY_LABEL = "Proposal Ontologies";
	
	
	List<String> rootList = new ArrayList<String>();
	@Override
	public Object[] getElements(Object inputElement) {
		return rootList.toArray();
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput,
			Object newInput) {
		if(oldInput == newInput){
			return;
		}
		if(newInput instanceof IOntology[]){
			ontologyArr = (IOntology[])newInput;
			classifyOntologies();
		}
	}

	private void classifyOntologies() {
		proposalOntologies.clear();
		fileOntologies.clear();
		bioportalOntologies.clear();
		
		for(IOntology o:ontologyArr){
			String uri = o.getURI().toASCIIString();
			
			if(uri.startsWith(BioPortalHelper.BIOPORTAL_URL)){
				bioportalOntologies.add(o);
			}
			else if(uri.endsWith(ODIEConstants.PROPOSAL_ONTOLOGY_URI_POSTFIX)){
				proposalOntologies.add(o);
			}
			else
				fileOntologies.add(o);
		}
		
		rootList.clear();
		rootList.add(BIOPORTAL_ONTOLOGY_LABEL + "(" + bioportalOntologies.size() + ")");
		rootList.add(FILE_ONTOLOGY_LABEL + "(" + fileOntologies.size() + ")");
		rootList.add(PROPOSAL_ONTOLOGY_LABEL + "(" + proposalOntologies.size() + ")");
		
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof String){
			String s = (String)parentElement;
			
			if(s.startsWith(BIOPORTAL_ONTOLOGY_LABEL))
				return bioportalOntologies.toArray();
			else if(s.startsWith(FILE_ONTOLOGY_LABEL))
				return fileOntologies.toArray();
			else if(s.startsWith(PROPOSAL_ONTOLOGY_LABEL))
				return proposalOntologies.toArray();
		}
		return new Object[]{};
	}

	@Override
	public Object getParent(Object element) {
		if(element instanceof IOntology){
			if(bioportalOntologies.contains(element))
				return rootList.get(0);
			else if(fileOntologies.contains(element))
				return rootList.get(1);
			else if(proposalOntologies.contains(element))
				return rootList.get(2);
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof String){
			String s = (String)element;
			
			if(s.startsWith(BIOPORTAL_ONTOLOGY_LABEL))
				return bioportalOntologies.size()>0;
			else if(s.startsWith(FILE_ONTOLOGY_LABEL))
				return fileOntologies.size()>0;
			else if(s.startsWith(PROPOSAL_ONTOLOGY_LABEL))
				return proposalOntologies.size()>0;
		}
		return false;
		
	}
}
