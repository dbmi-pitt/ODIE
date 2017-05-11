package edu.pitt.dbmi.odie.ui.workers;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.dbmi.odie.lexicalizer.lucenefinder.LexicalSetBuilder;
import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.LanguageResource;
import edu.pitt.dbmi.odie.model.LexicalSet;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.ui.wizards.NewLexicalSetWizard;
import edu.pitt.dbmi.odie.utils.GeneralUtils;
import edu.pitt.ontology.IOntology;
import edu.pitt.ontology.IRepository;
import edu.pitt.ontology.IResource;

public class AnalysisCreator implements IRunnableWithProgress {
	Analysis analysis;

	private static final String PROPOSAL_LEXSET_DESC = null;

	public AnalysisCreator(Analysis analysis) {
		this.analysis = analysis;
	}

	boolean success = false;

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {

		monitor.beginTask("Creating Analysis...", 3);

		try {
			if (GeneralUtils.isOE(analysis)) {
				// TODO The order of these tasks is very important. Apparently,
				// doing the create Analysis first somehow affects Hibernate's
				// internal state and causes it to function as expected in the
				// next
				// task or creating a proposal ontology. If it is switched,
				// and the ontology is created first, Hibernate does not seem to
				// find the newly added row to odie_lr.
				monitor.setTaskName("Creating scratch space");
				createAnalysisSpace(analysis);
				monitor.setTaskName("Creating suggestions ontology...");
				createProposalOntology(analysis);
				
				if(monitor.isCanceled()){
					success = false;
					return;
				}
			}
			monitor.worked(1);

			monitor.setTaskName("Saving Analysis...");
			Activator.getDefault().getMiddleTier().persist(analysis);
			monitor.worked(1);

			monitor.setTaskName("Configuring Analysis Engine...");
			GeneralUtils.loadAndConfigureAnalysisEngine(analysis);
			monitor.worked(1);
			monitor.done();
			success = true;
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		}

	}

	public boolean isSuccessful() {
		return success;
	}

	private void createProposalOntology(Analysis analysis) throws Exception {
		LexicalSet proposalLexicalSet = createNewProposalOntologyAndLexicalSet(analysis);
		if (proposalLexicalSet == null) {
			throw new Exception("Proposal Ontology Creation Failed");
		} else {
			analysis.setProposalLexicalSet(proposalLexicalSet);
		}
	}

	private void createAnalysisSpace(Analysis analysis) {
		Activator.getDefault().getMiddleTier().createSchema(
				analysis.getDatabaseName());
	}

	private LexicalSet createNewProposalOntologyAndLexicalSet(Analysis analysis)
			throws Exception {
		LanguageResource lr = createNewProposalOntologyLR(analysis);
		GeneralUtils.initLanguageResourceIfRequired(lr);
		String name = lr.getName();

		List<IResource> list = new ArrayList<IResource>();
		list.add(lr.getResource());

		LexicalSet proposalLS = NewLexicalSetWizard.createLexicalSetObject(
				name, PROPOSAL_LEXSET_DESC, list);
		proposalLS
				.setDescription(ODIEConstants.PROPOSALLEXICALSET_DESC
						.replaceAll(ODIEConstants.ANALYIS_NAME_VAR, analysis
								.getName()));

		LexicalSetBuilder builder = new LexicalSetBuilder(Activator
				.getDefault().getMiddleTier());
		builder.createNewLexicalSet(proposalLS, null);
		return proposalLS;
	}

	private LanguageResource createNewProposalOntologyLR(Analysis analysis)
			throws Exception {
		MiddleTier mt = Activator.getDefault().getMiddleTier();
		URI uri;
		IRepository rep = mt.getRepository();
		uri = new URI(ODIEConstants.PROPOSAL_ONTOLOGY_URI_PREFIX
				+ analysis.getName().replaceAll(" ", "_")
				+ ODIEConstants.PROPOSAL_ONTOLOGY_URI_POSTFIX);
		IOntology o = rep.createOntology(uri);
		o.setDescription(ODIEConstants.PROPOSALONTOLOGY_DESC.replaceAll(
				ODIEConstants.ANALYIS_NAME_VAR, analysis.getName()));
		rep.importOntology(o);

		LanguageResource lr = mt.getLanguageResourceForURI(uri);
		return lr;

	}
}
