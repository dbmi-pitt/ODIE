package edu.pitt.dbmi.odie.ui.editors.analysis;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.mountainminds.eclipse.selectionsample.SelectionProviderCollator;

import edu.pitt.dbmi.odie.ui.editors.DualPanelForm;
import edu.pitt.dbmi.odie.utils.FormUtils;

public class EnrichOntologiesPage extends FormPage {

	public static final String ID = "edu.pitt.dbmi.odie.ui.editors.EnrichOntologiesPage";
	private ProposalDetailsSection proposalDetailsSection;
	private ProposalsSection proposalSection;
	SelectionProviderCollator collator = new SelectionProviderCollator();
	private SuggestionsSection suggestionsSection;

	public EnrichOntologiesPage(FormEditor editor) {
		super(editor, ID, "Enrich Ontology");
	}

	protected void createFormContent(final IManagedForm managedForm) {
		getEditor().getToolkit().decorateFormHeading(
				(Form) managedForm.getForm().getContent());

		managedForm.getForm().setText("Enrich Ontology");

		FormUtils.setupFormLayout(managedForm);

		final Composite form = FormUtils.getContainer(managedForm);
		suggestionsSection = new SuggestionsSection(getEditor(), form,
				Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE
						| Section.EXPANDED);

		DualPanelForm dualForm = FormUtils.addSashForm(form, getEditor()
				.getToolkit());

		proposalSection = new ProposalsSection(getEditor(), dualForm.left,
				Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE
						| Section.EXPANDED);

		proposalDetailsSection = new ProposalDetailsSection(getEditor(),
				dualForm.right, Section.DESCRIPTION | Section.TITLE_BAR
						| Section.TWISTIE | Section.EXPANDED);

		GridData gd = new GridData(GridData.FILL_BOTH);
		suggestionsSection.getSection().setLayoutData(gd); // needed

		managedForm.addPart(suggestionsSection);
		managedForm.addPart(proposalSection);
		managedForm.addPart(proposalDetailsSection);
		managedForm.addPart(new RepeatAnalysisSection(getEditor(),
				dualForm.right, Section.DESCRIPTION | Section.TITLE_BAR
						| Section.TWISTIE | Section.EXPANDED,
				(StructuredViewer) suggestionsSection.getSelectionProvider()));

		collator.addSelectionProviderDelegate(proposalSection
				.getSelectionProvider());
		collator.addSelectionProviderDelegate(suggestionsSection
				.getSelectionProvider());

	}

	public ISelectionProvider getSelectionProvider() {
		return collator;
	}

	public void refresh() {
		if(proposalSection!=null)
			proposalSection.refresh();
		if(suggestionsSection!=null)
			suggestionsSection.refresh();

	}

	public ProposalsSection getProposalsSection() {
		return proposalSection;
	}

}