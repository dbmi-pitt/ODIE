package edu.pitt.dbmi.odie.ui.editors.ontology;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.Section;

import edu.pitt.dbmi.odie.ui.editors.DualPanelForm;
import edu.pitt.dbmi.odie.utils.FormUtils;
import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IOntology;

public class OntologyPage extends FormPage {

	public static final String ID = "edu.pitt.dbmi.odie.ui.editors.OntologyPage";
	private OntologyTreeSection otreeSection;

	public OntologyPage(FormEditor editor) {
		super(editor, ID, "Ontology");
	}

	protected void createFormContent(final IManagedForm managedForm) {
		getEditor().getToolkit().decorateFormHeading(
				(Form) managedForm.getForm().getContent());
		IOntology o = ((OntologyEditorInput) getEditorInput()).ontology;
		managedForm.getForm().setText(
				o.getName() + " (" + o.getURI().toASCIIString() + ")");
		
		
		FormUtils.setupFormLayout(managedForm);

		final Composite form = FormUtils.getContainer(managedForm);

//		DualPanelForm dualForm = FormUtils.addSashForm(form, getEditor()
//				.getToolkit());
		final DualPanelForm dualForm = FormUtils.setupDividedLayout(getEditor()
				.getToolkit(), managedForm, 50, 50, SWT.VERTICAL);
		
		otreeSection = new OntologyTreeSection(getEditor(), dualForm.left,
				Section.TITLE_BAR | Section.EXPANDED);

		ClassDetailsSection classDetailsSection = new ClassDetailsSection(
				getEditor(), dualForm.right, Section.TITLE_BAR | Section.TWISTIE
						| Section.EXPANDED);

		managedForm.addPart(otreeSection);
		managedForm.addPart(classDetailsSection);
	}

	public void select(IClass c) {
		otreeSection.select(c);

	}

}
