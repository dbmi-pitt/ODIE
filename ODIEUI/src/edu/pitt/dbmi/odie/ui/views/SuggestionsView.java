package edu.pitt.dbmi.odie.ui.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.model.Suggestion;
import edu.pitt.dbmi.odie.ui.editors.analysis.EnrichOntologiesPage;
import edu.pitt.dbmi.odie.ui.editors.analysis.SuggestionTreeTable;
import edu.pitt.dbmi.odie.ui.editors.document.AnnotatedDocumentEditor;
import edu.pitt.dbmi.odie.ui.editors.document.DocumentEditorInput;
import edu.pitt.dbmi.odie.ui.views.providers.ItemCountProvider;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class SuggestionsView extends FilteredStackedViewPart {

	private final class EditorPartListener implements IPartListener {

		@Override
		public void partBroughtToTop(IWorkbenchPart part) {
			if (part instanceof AnnotatedDocumentEditor) {
				loadCurrentEditorSuggestions();
			}
		}

		@Override
		public void partClosed(IWorkbenchPart part) {
		}

		@Override
		public void partOpened(IWorkbenchPart part) {
		}

		@Override
		public void partActivated(IWorkbenchPart part) {
			// TODO Auto-generated method stub

		}

		@Override
		public void partDeactivated(IWorkbenchPart part) {
			// TODO Auto-generated method stub

		}
	}

	public static final String ID = "edu.pitt.dbmi.odie.ui.views.SuggestionsView";

	private SuggestionTreeTable treeViewer;

	private Text filterTextbox;

	private EditorPartListener editorPartListener;

	@Override
	protected void createDelegateControl(Composite parent) {
		doParentLayoutAndAesthetics(parent);
		createFilterControl(parent);

		createTable(parent);

		registerFilters();

		hookListeners();

		loadCurrentEditorSuggestions();
	}

	private void doParentLayoutAndAesthetics(Composite parent) {
		GridLayout gl = new GridLayout();
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		gl.horizontalSpacing = 0;
		// gl.verticalSpacing = Aesthetics.VERTICAL_INTRAGROUP_SPACING;
		parent.setLayout(gl);

		parent.setBackground(new Color(parent.getDisplay(), 255, 255, 255));
	}

	private void createFilterControl(Composite parent) {
		filterTextbox = new Text(parent, SWT.BORDER);
		filterTextbox.setText("type filter text");
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.TOP;

		filterTextbox.setLayoutData(gd);
		filterTextbox.selectAll();
	}

	/**
	 * Creates the table control.
	 */
	private void createTable(Composite parent) {
		treeViewer = new SuggestionTreeTable(parent);
	}

	// public static String columnHeaders[] = { "Suggestion", "Score",
	// "Evidence"};
	//
	// private ColumnLayoutData columnLayouts[] = { new ColumnWeightData(40),
	// new ColumnWeightData(20), new ColumnWeightData(20)};
	// private int[] columnOrder = new int[]{SuggestionSorter.SUGGESTION,
	// SuggestionSorter.SCORE, SuggestionSorter.INFO};
	//
	// private MultiColumnSorter<Suggestion> suggestionsComparator = new
	// SuggestionSorter(columnOrder);

	private TableColumnTextFilter textFilter;

	private void registerFilters() {
		textFilter = new TableColumnTextFilter(0);
		registerFilter(textFilter, "filter text");
	}

	private void hookListeners() {

		filterTextbox.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String t = ((Text) e.widget).getText();
				textFilter.setFilterText(t);
				filterChanged();
			}
		});
		filterTextbox.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}

			@Override
			public void mouseDown(MouseEvent e) {
				filterTextbox.selectAll();

			}

			@Override
			public void mouseUp(MouseEvent e) {
			}

		});

		editorPartListener = new EditorPartListener();
		getSite().getPage().addPartListener(editorPartListener);
		getSite().setSelectionProvider(treeViewer);

		getSite().getPage().addPostSelectionListener(new ISelectionListener() {

			@Override
			public void selectionChanged(IWorkbenchPart part,
					ISelection selection) {
				if (part instanceof EnrichOntologiesPage) {
					if (selection instanceof StructuredSelection) {
						StructuredSelection ss = (StructuredSelection) selection;
						if (ss.getFirstElement() instanceof Suggestion)
							treeViewer.setSelection(selection);
					}
				}

			}

		});

	}

	private void loadCurrentEditorSuggestions() {
		IEditorPart part = getSite().getWorkbenchWindow().getActivePage()
				.getActiveEditor();
		if (part != null && part instanceof AnnotatedDocumentEditor) {

			DocumentEditorInput input = (DocumentEditorInput) ((AnnotatedDocumentEditor) part)
					.getEditorInput();
			AnalysisDocument ad = input.getAnalysisDocument();

			if (GeneralUtils.isOE(input.getAnalysisDocument().getAnalysis()))
				treeViewer.setInput(ad);
		}
	}

	public void refreshView() {
		treeViewer.refresh();

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		filterTextbox.setFocus();
	}

	@Override
	public void dispose() {
		unhookListeners();
		super.dispose();
	}

	private void unhookListeners() {
		getSite().getPage().removePartListener(editorPartListener);
		getSite().setSelectionProvider(null);

	}

	@Override
	public long getFilteredItemCount() {
		return treeViewer.getTree().getItemCount();
	}

	@Override
	public StructuredViewer getStructuredViewer() {
		return treeViewer;
	}

	@Override
	public long getUnfilteredItemCount() {
		assert treeViewer.getContentProvider() instanceof ItemCountProvider : "Content provider used for a "
				+ "viewer in a FilteredStackedViewPart must implement ItemCountProvider";

		return ((ItemCountProvider) treeViewer.getContentProvider())
				.getItemCount();
	}

}