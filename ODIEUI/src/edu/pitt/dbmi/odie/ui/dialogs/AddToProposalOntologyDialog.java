package edu.pitt.dbmi.odie.ui.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.dbmi.odie.ui.editors.analysis.ProposalDetailsPanel;
import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IOntology;

public class AddToProposalOntologyDialog extends Dialog {

	private ProposalDetailsPanel proposalDetailsPanel;
	private IOntology[] ontologies;

	public AddToProposalOntologyDialog(Shell activeShell, IOntology[] ontologies) {
		super(activeShell);
		this.ontologies = ontologies;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("New Concept");
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);

		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, true);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite container = (Composite) super.createDialogArea(parent);

		GridLayout gl = new GridLayout(1, false);
		gl.marginWidth = Aesthetics.INTERGROUP_SPACING;
		gl.marginHeight = Aesthetics.INTERGROUP_SPACING;
		container.setLayout(gl);

		proposalDetailsPanel = new ProposalDetailsPanel(container, ontologies,
				false);

		return container;
	}

	public String getName() {
		return proposalDetailsPanel.getName();
	}

	public List<String> getSynonyms() {
		return proposalDetailsPanel.getSynonyms();
	}

	public IClass getParentClass() {
		return proposalDetailsPanel.getParentClass();
	}

}
