package edu.pitt.dbmi.odie.ui.editors.ontology;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import edu.pitt.dbmi.odie.utils.FormUtils;
import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IOntology;

public class OntologyTreeSection extends SectionPart {

	private FormEditor editor;
	private IOntology ontology;
	private OntologyClassPickerPanel classPicker;

	public OntologyTreeSection(FormEditor editor, Composite parent, int style) {
		super(parent, editor.getToolkit(), style);
		this.editor = editor;
	}

	public void initialize(final IManagedForm form) {
		super.initialize(form);
		ontology = ((OntologyEditorInput) editor.getEditorInput())
				.getOntology();

		FormToolkit toolkit = form.getToolkit();
		Section section = getSection();
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
		section.addExpansionListener(new ExpansionAdapter() {

			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
			}
		});
		section.setText("All Concepts");

		Composite sectionClient = FormUtils.newNColumnComposite(toolkit,
				section, 1, true, true);
		FormUtils.setMargins(sectionClient, 5, 0);

		classPicker = new OntologyClassPickerPanel(sectionClient,
				new IOntology[] { ontology });
		editor.getSite().setSelectionProvider(classPicker);
	}

	@Override
	public void refresh() {
		super.refresh();
	}

	public void select(IClass c) {
		classPicker.setSelection(new StructuredSelection(c));

	}
}
