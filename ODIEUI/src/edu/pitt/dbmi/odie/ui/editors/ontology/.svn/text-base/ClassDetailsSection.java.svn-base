package edu.pitt.dbmi.odie.ui.editors.ontology;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import edu.pitt.dbmi.odie.ui.views.details.DetailsTreeTable;
import edu.pitt.dbmi.odie.ui.views.details.providers.FeatureValueLabelProvider;
import edu.pitt.dbmi.odie.ui.views.details.providers.PClassDetailsContentProvider;
import edu.pitt.dbmi.odie.utils.FormUtils;
import edu.pitt.ontology.IClass;

public class ClassDetailsSection extends SectionPart {

	private FormEditor editor;
	private DetailsTreeTable detailsTable;

	public ClassDetailsSection(FormEditor editor, Composite parent, int style) {
		super(parent, editor.getToolkit(), style);
		this.editor = editor;
	}

	FormEditor getEditor() {
		return editor;
	}

	public void initialize(final IManagedForm form) {
		super.initialize(form);

		FormToolkit toolkit = form.getToolkit();
		Section section = getSection();
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
		section.addExpansionListener(new ExpansionAdapter() {

			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
			}
		});
		section.setText("Concept Details");

		Composite sectionClient = FormUtils.newNColumnComposite(toolkit,
				section, 1, true, true);
		FormUtils.setMargins(sectionClient, 5, 0);

		detailsTable = new DetailsTreeTable(sectionClient,
				new int[] { 150, 400 });
		detailsTable.setContentProvider(new PClassDetailsContentProvider());
		detailsTable.setLabelProvider(new FeatureValueLabelProvider());
		detailsTable.getTree().setLinesVisible(false);
		detailsTable.getTree().setHeaderVisible(true);
		hookMyListeners();
	}

	private void hookMyListeners() {
		getEditor().getSite().getPage().addPostSelectionListener(
				new ISelectionListener() {
					@Override
					public void selectionChanged(IWorkbenchPart part,
							ISelection selection) {

						if (!(part instanceof OntologyEditor))
							return;

						if (!(selection instanceof StructuredSelection))
							return;

						Object element = ((StructuredSelection) selection)
								.getFirstElement();
						if (element instanceof IClass) {
							getSection().setText(((IClass)element).getName());
							getSection().layout();
							detailsTable.setInput(element);
							detailsTable.expandAll();
							
						}
					}
				});

	}

	@Override
	public void refresh() {
		super.refresh();
	}

}
