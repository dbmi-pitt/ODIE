package edu.pitt.dbmi.odie.ui.editors.analysis.summary;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.Section;

import edu.pitt.dbmi.odie.utils.FormUtils;

public class SummaryPage extends FormPage {

	public static final String ID = "edu.pitt.dbmi.odie.ui.editors.SummaryPage";

	private OverviewSection overviewSection;
	private OntologySpecificPerformanceSection opSection;
	private DocSpecificPerformanceSection dpSection;
	

	public SummaryPage(FormEditor editor) {
		super(editor, ID, "Summary");
	}

	protected void createFormContent(final IManagedForm managedForm) {
		getEditor().getToolkit().decorateFormHeading(
				(Form) managedForm.getForm().getContent());

		managedForm.getForm().setText("Summary");

		FormUtils.setupFormLayout(managedForm);

		final Composite form = FormUtils.getContainer(managedForm);
		overviewSection = new OverviewSection(getEditor(), form,
				Section.TITLE_BAR | Section.TWISTIE
						| Section.EXPANDED);
		GridData gd = new GridData(GridData.FILL_BOTH);
		overviewSection.getSection().setLayoutData(gd); // needed

	
		
		opSection = new OntologySpecificPerformanceSection(getEditor(), form,
				Section.TITLE_BAR | Section.TWISTIE
						| Section.EXPANDED);
		gd = new GridData(GridData.FILL_BOTH);
		opSection.getSection().setLayoutData(gd); // needed
		
		dpSection = new DocSpecificPerformanceSection(getEditor(), form,
				Section.TITLE_BAR | Section.TWISTIE
						| Section.EXPANDED);
		gd = new GridData(GridData.FILL_BOTH);
		dpSection.getSection().setLayoutData(gd); // needed
		
		managedForm.addPart(overviewSection);
		managedForm.addPart(opSection);
		managedForm.addPart(dpSection);
	}


	public void refresh() {
		overviewSection.refresh();
		opSection.refresh();
		dpSection.refresh();
	}


}