package edu.pitt.dbmi.odie.ui.editors.analysis;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
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
import edu.pitt.dbmi.odie.ui.actions.AddProposalsAction;
import edu.pitt.dbmi.odie.ui.editors.providers.SuggestionsContentProvider;
import edu.pitt.dbmi.odie.utils.FormUtils;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class SuggestionsSection extends SectionPart {
	private static final String DESCRIPTION = "suggestions for new concepts. Right click or drag suggestions into \"Proposed New Concepts\" list below to create a new proposals.";

	Logger logger = Logger.getLogger(this.getClass());
	private SuggestionTreeTable treeTableViewer;

	private FormEditor editor;
	private Analysis analysis;

	private Button copyToClipboardButton;

	public SuggestionsSection(FormEditor editor, Composite parent, int style) {
		super(parent, editor.getToolkit(), style);
		this.editor = editor;
	}

	public void initialize(final IManagedForm form) {
		super.initialize(form);

		analysis = ((AnalysisEditorInput) editor.getEditorInput())
				.getAnalysis();

		FormToolkit toolkit = form.getToolkit();
		Section section = getSection();
		section.addExpansionListener(new ExpansionAdapter() {

			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
			}
		});
		section.setText("Suggestions");
		section.setDescription(DESCRIPTION);

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
		
		treeTableViewer = new SuggestionTreeTable(sectionClient);
		gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.TOP;
		gd.heightHint = treeTableViewer.getTree().getItemHeight()
				* ROWS_VISIBLE + treeTableViewer.getTree().getHeaderHeight();
		treeTableViewer.applyLayout(gd);
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
				ISelection selection = treeTableViewer.getSelection();
				TreeItem[] items = null;
				if (selection != null && selection instanceof IStructuredSelection) {
					IStructuredSelection sel = (IStructuredSelection) selection;
					if(sel.isEmpty()){
						items = treeTableViewer.getTree().getItems();
					}
					else{
						items = treeTableViewer.getTree().getSelection();
					}
				}
				if(items!=null){
					String s = GeneralUtils.getTablLimitedTableContents(items,treeTableViewer.getTree());
					
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
		
		final MenuManager mgr = new MenuManager();
		mgr.setRemoveAllWhenShown(true);

		mgr.addMenuListener(new IMenuListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.jface.action.IMenuListener#menuAboutToShow(org.eclipse
			 * .jface.action.IMenuManager)
			 */
			public void menuAboutToShow(IMenuManager manager) {
				IStructuredSelection selection = (IStructuredSelection) treeTableViewer
						.getSelection();
				if (!selection.isEmpty()) {

					mgr.add(new AddProposalsAction(selection.toList()));
				}
			}
		});
		treeTableViewer.getControl().setMenu(
				mgr.createContextMenu(treeTableViewer.getControl()));

	}

	@Override
	public void refresh() {
		if (analysis != null) {
			treeTableViewer.setInput(analysis);
			super.refresh();
		}
		long count = ((SuggestionsContentProvider) treeTableViewer
				.getContentProvider()).getItemCount();
		getSection().setDescription(count + " " + DESCRIPTION);
	}

	public ISelectionProvider getSelectionProvider() {
		return treeTableViewer;
	}
}
