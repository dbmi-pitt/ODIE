package edu.pitt.dbmi.odie.ui.wizards;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.dbmi.odie.middletier.BioportalNote;
import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.ui.wizards.pages.BioportalNewProposalNotePage;
import edu.pitt.dbmi.odie.ui.wizards.pages.BioportalNoteMetadataPage;
import edu.pitt.dbmi.odie.ui.wizards.pages.SelectEnrichmentOntologyPage;
import edu.pitt.dbmi.odie.utils.GeneralUtils;
import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IOntology;
import edu.pitt.ontology.IOntologyException;
import edu.pitt.ontology.IProperty;
import edu.pitt.ontology.IResource;
import edu.pitt.ontology.bioportal.BOntology;
import edu.pitt.ontology.bioportal.BioPortalRepository;
import edu.pitt.ontology.protege.PClass;

public class BioportalNewProposalNoteWizard extends Wizard {

	SelectEnrichmentOntologyPage eoPage;
	BioportalNewProposalNotePage notePage;
	BioportalNoteMetadataPage notemetaPage;
	
	private Analysis analysis;
	private IClass newConcept;
	private IOntology selectedOntology;
	private BioportalNote note;

	public BioportalNewProposalNoteWizard(Analysis a, IClass newConcept) {
		super();
		this.analysis = a;
		this.newConcept = newConcept;
		this.note = new BioportalNote();
		note.setNewConceptClass(newConcept);
		setNeedsProgressMonitor(true);
	}

	@Override
	public boolean performFinish() {
		MiddleTier mt = Activator.getDefault().getMiddleTier();
		if(mt.doBioportalNoteRequest(note)){
			MessageDialog dialog = new MessageDialog(Display.getCurrent().getActiveShell(), 
					"New Bioportal note created successfully", null, 
					"A new concept note has been successfully submitted to Bioportal.", 
					MessageDialog.INFORMATION, 
					new String[] { "View in Bioportal",IDialogConstants.OK_LABEL }, 0);
			PClass c = (PClass) note.getNewConceptClass();
			IProperty p = c.getOntology().getProperty(ODIEConstants.BIOPORTAL_NOTE_ID_PROPERTY);
			IProperty op = c.getOntology().getProperty(ODIEConstants.BIOPORTAL_ONTOLOGY_ID_PROPERTY);
			if(p == null){
				p = c.getOntology().createProperty(ODIEConstants.BIOPORTAL_NOTE_ID_PROPERTY, IProperty.ANNOTATION_DATATYPE);
				p.setRange(new String [0]);
				p.setFunctional(true);
				op = c.getOntology().createProperty(ODIEConstants.BIOPORTAL_ONTOLOGY_ID_PROPERTY, IProperty.ANNOTATION_DATATYPE);
				op.setRange(new String [0]);
				op.setFunctional(true);
			}
			c.setPropertyValue(p,note.getId());
			c.setPropertyValue(op,note.getOntologyId());
			try {
				c.getOntology().save();
			} catch (IOntologyException e) {
				e.printStackTrace();
			}
			
			int index = dialog.open();
			if(index == 0){
				String noteid = (String) note.getNewConceptClass().getPropertyValue(p);
				String ontologyid = (String) note.getNewConceptClass().getPropertyValue(op);
				String url = BioPortalRepository.DEFAULT_BIOPORTAL_URL + ontologyid + "/?noteid=" + noteid;
				try {
					GeneralUtils.openURL(new URL(url));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
			return true;
		}
		GeneralUtils.showErrorDialog("New Bioportal Note creation failed", "There was an error submitting the note to Bioportal");
		return false;
	}

	@Override
	public void addPages() {
		setWindowTitle("Submit new concept to Bioportal");

		List<IOntology> ontologies = getEnrichmentOntologies();
		selectedOntology = selectedParentOntology();
		
		if (ontologies.size() > 1 && selectedOntology==null) {
			eoPage = new SelectEnrichmentOntologyPage(ontologies);
			addPage(eoPage);
		}
		else if(selectedOntology==null){
			selectedOntology = ontologies.get(0);
		}
			
		
		note.setType("ProposalForCreateEntity");
		note.setAppliesToType("Ontology");
		note.setNewTermPreferredName(newConcept.getName());
		String syns = "";
		for(String s:newConcept.getConcept().getSynonyms())
			syns += s+",";
		syns = syns.substring(0,syns.length()-1);
		note.setNewTermSynonyms(syns);
		IClass[] parents = newConcept.getSuperClasses();
		if(parents.length>0 && !parents[0].getName().equals("Thing")){
			note.setNewTermParent(parents[0]);
		}		
		
		notePage = new BioportalNewProposalNotePage(note);
		notemetaPage = new BioportalNoteMetadataPage(note);
		
		if(selectedOntology!=null){
			MiddleTier mt = Activator.getDefault().getMiddleTier();
			IResource r = mt.getBioportalRepository().getResource(selectedOntology.getURI());
			note.setAppliesToOntology((BOntology)r);
			
			notePage.setSelectedOntology(selectedOntology);
			
			notePage.setTitle("New Concept Proposal for " + selectedOntology.getName() + " ontology");
			notemetaPage.setTitle("New Concept Proposal for " + selectedOntology.getName() + " ontology");
		}
		
		addPage(notePage);
		addPage(notemetaPage);
	}

	private IOntology selectedParentOntology() {
		MiddleTier mt = Activator.getDefault().getMiddleTier();
		IClass[] parents = newConcept.getSuperClasses();
		if(parents.length>0){
			IClass parent = parents[0];
			String bpURIStr = (String)parent.getPropertyValue(parent.getOntology().getProperty(IProperty.RDFS_IS_DEFINED_BY));
			if(bpURIStr!=null){
				IResource r;
				try {
					r = mt.getRepository().getResource(new URI(bpURIStr));
					if(r != null){
						return r.getOntology();
					}	
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page instanceof SelectEnrichmentOntologyPage) {
			MiddleTier mt = Activator.getDefault().getMiddleTier();
			
			selectedOntology = eoPage.getSelectedOntology();
			if(selectedOntology!=null){
				IResource r = mt.getBioportalRepository().getResource(selectedOntology.getURI());
				note.setAppliesToOntology((BOntology)r);
				notePage.setSelectedOntology(selectedOntology);
				
				notePage.setTitle("New Concept Proposal for " + selectedOntology.getName() + " ontology");
				notemetaPage.setTitle("New Concept Proposal for " + selectedOntology.getName() + " ontology");
			}
		}

		return super.getNextPage(page);

	}
	
	private List<IOntology> getEnrichmentOntologies() {
		MiddleTier mt = Activator.getDefault().getMiddleTier();
		List<IOntology> ontologies = mt.getOntologies(analysis);
		List<IOntology> proposalOntologies = new ArrayList<IOntology>();
		
		for(IOntology o:ontologies){
			if(o.getName().contains(ODIEConstants.PROPOSAL_ONTOLOGY_URI_POSTFIX))
				proposalOntologies.add(o);	
		}
		
		ontologies.removeAll(proposalOntologies);
		return ontologies;
	}
	
	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		pageContainer.getShell().setSize(485, 375);
	}
}
