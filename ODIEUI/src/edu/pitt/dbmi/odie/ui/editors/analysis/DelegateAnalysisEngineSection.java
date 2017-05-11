package edu.pitt.dbmi.odie.ui.editors.analysis;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.dbmi.odie.ui.editors.providers.AnalysisEngineDelegatesTreeContentProvider;
import edu.pitt.dbmi.odie.ui.editors.providers.UIMAAnalysisEngineMetaDataLabelProvider;
import edu.pitt.dbmi.odie.uima.utils.UIMAUtils;
import edu.pitt.dbmi.odie.utils.FormUtils;

public class DelegateAnalysisEngineSection extends SectionPart implements
		IDetailsPage {

	private FormEditor editor;
	private TreeViewer treeViewer;

	public DelegateAnalysisEngineSection(FormEditor editor, Composite parent,
			int style) {
		super(parent, editor.getToolkit(), style);

		this.editor = editor;
	}

	@Override
	public void initialize(final IManagedForm form) {
		super.initialize(form);

		FormToolkit toolkit = form.getToolkit();
		Section section = getSection();
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.verticalIndent = Aesthetics.INTERGROUP_SPACING;
		section.setLayoutData(gd);
		section.addExpansionListener(new ExpansionAdapter() {

			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
			}
		});
		section.setText("Analysis Engines");
		section
				.setDescription("Analysis Engines that are part of this pipeline");
		section.marginHeight = 10;
		section.marginWidth = 5;

		Composite sectionClient = toolkit.createComposite(section, SWT.WRAP);
		toolkit.paintBordersFor(sectionClient);
		this.getSection().setClient(sectionClient);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		sectionClient.setLayout(layout);
		FormUtils.setMargins(sectionClient, 5, 0);

		treeViewer = new TreeViewer(sectionClient, SWT.V_SCROLL | SWT.BORDER);
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				form.fireSelectionChanged(DelegateAnalysisEngineSection.this,
						event.getSelection());
			}
		});
		treeViewer
				.setContentProvider(new AnalysisEngineDelegatesTreeContentProvider());
		treeViewer
				.setLabelProvider(new UIMAAnalysisEngineMetaDataLabelProvider());

		gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;

		treeViewer.getTree().setLayoutData(gd);

	}

	@Override
	public void setFocus() {
		treeViewer.getControl().setFocus();
	}

	@Override
	public void refresh() {

		Analysis a = ((AnalysisEditorInput) editor.getEditorInput())
				.getAnalysis();

		if (a.getAnalysisEngine() != null) {
			treeViewer.setInput(a.getAnalysisEngine()
					.getAnalysisEngineMetaData());
		}

		super.refresh();
	}

	@Override
	public void createContents(Composite parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		// TODO Auto-generated method stub

	}

}
