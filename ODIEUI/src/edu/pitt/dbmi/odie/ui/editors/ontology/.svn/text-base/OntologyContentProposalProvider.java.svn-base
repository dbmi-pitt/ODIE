package edu.pitt.dbmi.odie.ui.editors.ontology;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IOntology;

public class OntologyContentProposalProvider implements
		IContentProposalProvider {

	private IOntology ontology;

	public OntologyContentProposalProvider(IOntology ontology) {
		super();
		this.ontology = ontology;
	}

	public IOntology getOntology() {
		return ontology;
	}

	public void setOntology(IOntology ontology) {
		this.ontology = ontology;
	}

	@Override
	public IContentProposal[] getProposals(String contents, int position) {
		List<IContentProposal> proposals = new ArrayList<IContentProposal>();

		List<IClass> results = Activator.getDefault().getMiddleTier()
				.searchOntology(ontology, contents);

		for (IClass c : results) {
			proposals.add(new ClassContentProposal(c));
		}

		return proposals.toArray(new IContentProposal[] {});
	}
}
