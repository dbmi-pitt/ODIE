package edu.pitt.dbmi.odie.ui.editors.ontology;

import org.eclipse.jface.fieldassist.IContentProposal;

import edu.pitt.dbmi.odie.ui.views.details.providers.FeatureValue;
import edu.pitt.dbmi.odie.ui.views.details.providers.PClassDetailsContentProvider;
import edu.pitt.ontology.IClass;
import edu.pitt.terminology.lexicon.Concept;

public class ClassContentProposal implements IContentProposal {

	private IClass c;
	private String description;

	public IClass getProposalClass() {
		return c;
	}

	public ClassContentProposal(IClass c) {
		super();
		this.c = c;
		initDescription();
	}

	private void initDescription() {
		description = "Name:" + c.getName() + "\n";
		Concept cc = c.getConcept();

		description += "Definition:" + cc.getDefinition() + "\n";
		description += "Synonyms:"
				+ PClassDetailsContentProvider.convertArrayToString(cc
						.getSynonyms()) + "\n";
	}

	@Override
	public String getContent() {
		return c.getName();
	}

	@Override
	public int getCursorPosition() {
		return c.getName().length();
	}

	@Override
	public String getDescription() {
		return null;
		// return description;
	}

	@Override
	public String getLabel() {
		return c.getName();
	}

}
