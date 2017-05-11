package edu.pitt.dbmi.odie.ui.editors.analysis.summary;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.ui.editors.analysis.AnalysisEditorInput;
import edu.pitt.dbmi.odie.ui.editors.analysiscomparison.AnalysisComparisonEditorInput;
import edu.pitt.dbmi.odie.utils.FormUtils;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class DocSpecificPerformanceSection extends SectionPart {
	private static final String DESCRIPTION = "";

	Logger logger = Logger.getLogger(this.getClass());
	private DocumentPerformanceTreeTable performanceTreeTable;

	private FormEditor editor;
	private List<Analysis> analyses;

	private Button copyToClipboardButton;

	public DocSpecificPerformanceSection(FormEditor editor, Composite parent, int style) {
		super(parent, editor.getToolkit(), style);
		this.editor = editor;
	}

	public void initialize(final IManagedForm form) {
		super.initialize(form);

		if(editor.getEditorInput() instanceof AnalysisEditorInput){
			analyses = new ArrayList<Analysis>();
			analyses.add(((AnalysisEditorInput) editor.getEditorInput()).getAnalysis());	
		}
		else if(editor.getEditorInput() instanceof AnalysisComparisonEditorInput){
			analyses = ((AnalysisComparisonEditorInput)editor.getEditorInput()).getAnalyses();
		}

		FormToolkit toolkit = form.getToolkit();
		Section section = getSection();
		section.addExpansionListener(new ExpansionAdapter() {

			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
			}
		});
		section.setText("Document Specific Performance");
//		section.setDescription(DESCRIPTION);

		Composite sectionClient = FormUtils.newNColumnComposite(toolkit,
				section, 1, true, true);
		FormUtils.setMargins(sectionClient, 5, 0);

		GridData gd = new GridData(GridData.FILL_BOTH);
		sectionClient.setLayoutData(gd);


		copyToClipboardButton = new Button(sectionClient,SWT.PUSH);
		copyToClipboardButton.setText("Copy To Clipboard");
		gd = new GridData();
		gd.grabExcessHorizontalSpace = false;
		gd.grabExcessVerticalSpace = false;
		gd.horizontalAlignment = SWT.RIGHT;
		gd.verticalAlignment = SWT.TOP;
		copyToClipboardButton.setLayoutData(gd);
		
		
		performanceTreeTable = new DocumentPerformanceTreeTable(sectionClient);
		gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.TOP;
		gd.heightHint = performanceTreeTable.getTree().getItemHeight()
				* ROWS_VISIBLE + performanceTreeTable.getTree().getHeaderHeight();
		performanceTreeTable.applyLayout(gd);
		hookMyListeners();
	}

	private static final int ROWS_VISIBLE = 10;

	private void hookMyListeners() {
		copyToClipboardButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				handleSelection();

			}

			private void handleSelection() {
				ISelection selection = performanceTreeTable.getSelection();
				TreeItem[] items = null;
				if (selection != null && selection instanceof IStructuredSelection) {
					IStructuredSelection sel = (IStructuredSelection) selection;
					if(sel.isEmpty()){
						items = performanceTreeTable.getTree().getItems();
					}
					else{
						items = performanceTreeTable.getTree().getSelection();
					}
				}
				if(items!=null){
					String s = GeneralUtils.getTablLimitedTableContents(items,performanceTreeTable.getTree());
					
					Clipboard cb = new Clipboard(Display.getDefault());
					TextTransfer textTransfer = TextTransfer.getInstance();
					cb.setContents(new Object[] { s },
							new Transfer[] { textTransfer });
				}
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				handleSelection();
			}
		});

	}

	@Override
	public void refresh() {
		if (analyses != null) {
			performanceTreeTable.setInput(analyses);
			super.refresh();
		}
	}

	public ISelectionProvider getSelectionProvider() {
		return performanceTreeTable;
	}
}
