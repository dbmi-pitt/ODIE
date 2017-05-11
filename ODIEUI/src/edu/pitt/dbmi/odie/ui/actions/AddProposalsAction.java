package edu.pitt.dbmi.odie.ui.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import edu.pitt.dbmi.odie.model.Suggestion;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class AddProposalsAction extends Action {

	private static final String CREATE_NEW_CONCEPT_LABEL = "Propose as a new concept";
	private static final String CREATE_NEW_CONCEPT_TOOLTIP = "Create a new concept from the selected suggestion";

	List<Suggestion> suggestionsForProposals;

	public AddProposalsAction(List<Suggestion> suggestionsForProposals) {
		super(CREATE_NEW_CONCEPT_LABEL, IAction.AS_CHECK_BOX);
		setToolTipText(CREATE_NEW_CONCEPT_TOOLTIP);
		// setImageDescriptor(helpImage);
		this.suggestionsForProposals = suggestionsForProposals;
	}

	@Override
	public void run() {
		if (suggestionsForProposals.size() > 0) {
			GeneralUtils.addProposals(suggestionsForProposals
					.toArray(new Suggestion[] {}));
			GeneralUtils.refreshProposalLexicalSet(suggestionsForProposals.get(
					0).getAnalysis());
			GeneralUtils.refreshViews();
		}
	}
}
