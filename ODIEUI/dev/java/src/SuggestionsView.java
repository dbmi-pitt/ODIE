package edu.pitt.dbmi.odie.ui.views;

import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.AnalysisEngineMetadata;
import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.dbmi.odie.ui.editors.AnalysisEditorInput;
import edu.pitt.dbmi.odie.ui.editors.ChartEditor;
import edu.pitt.dbmi.odie.ui.views.providers.SuggestionLabelProvider;
import edu.pitt.dbmi.odie.ui.views.sorters.SuggestionSorter;

public class SuggestionsView extends ViewPart implements
		ISelectionChangedListener {

	private final class EditorPartListener implements IPartListener {
		@Override
		public void partActivated(IWorkbenchPart part) {
			if (part instanceof ChartEditor) {
				((ChartEditor) part).addSelectionChangedListener(self);
			}
		}

		@Override
		public void partBroughtToTop(IWorkbenchPart part) {
			if (part instanceof ChartEditor) {
				AnalysisEditorInput input = (AnalysisEditorInput) ((ChartEditor) part)
						.getEditorInput();
				Analysis analysis = input.getAnalysis();
//				if (analysis.getType().equals(AnalysisEngineMetadata.TYPE_OE)) {
////					tableViewer.setContentProvider(new PhraseContentProvider());
//					tableViewer.setLabelProvider(new SuggestionLabelProvider());
//					tableViewer.setComparator(suggestionSorter);
////					tableViewer.setInput(analysis.getStatistics());
//				}

			}
		}

		@Override
		public void partClosed(IWorkbenchPart part) {
		}

		@Override
		public void partDeactivated(IWorkbenchPart part) {
			if (part instanceof ChartEditor) {
				((ChartEditor) part).removeSelectionChangedListener(self);
			}
		}

		@Override
		public void partOpened(IWorkbenchPart part) {
		}
	}

	public static final String ID = "edu.pitt.dbmi.odie.ui.views.SuggestionsView";

	private ChartSelectionFilter chartSelectionFilter;
	private Table phraseTable;
	private TableViewer tableViewer;

	private SuggestionsView self = this;;
	private Text filterTextbox;
	private SuggestionSorter suggestionSorter = new SuggestionSorter();

	private EditorPartListener editorPartListener;

	
	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		doParentLayout(parent);
		doParentAesthetics(parent);
		createFilterControl(parent);
		
		createTable(parent);
		createTableViewer();
		
		addFilterSupportToTableViewer();
		
		hookListeners();
		loadCurrentEditorSuggestions();
	}
	
	private void doParentAesthetics(Composite parent) {
		parent.setBackground(Aesthetics.getEnabledBackground());
	}

	private void doParentLayout(Composite parent) {
		GridLayout gl = new GridLayout();
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		parent.setLayout(gl);
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
		phraseTable = new Table(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI
				| SWT.FULL_SELECTION);
		phraseTable.setLinesVisible(true);

		TableLayout layout = new TableLayout();
		phraseTable.setLayout(layout);
		phraseTable.setHeaderVisible(true);
		
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.verticalAlignment = SWT.FILL;
		gd.grabExcessVerticalSpace = true;

		phraseTable.setLayoutData(gd);

		createPhraseTableColumns();

	}
	
	void createPhraseTableColumns() {

		String columnHeaders[] = { ODIEMessages.headerNPCandidate,
				ODIEMessages.headerWeight, ODIEMessages.headerFrequency };

		ColumnLayoutData columnLayouts[] = { new ColumnWeightData(75),
				new ColumnWeightData(60), new ColumnWeightData(30) };

		SelectionAdapter headerListener = new TableHeaderSortListener(
				tableViewer);

		TableLayout layout = (TableLayout) phraseTable.getLayout();

		for (int i = 0; i < columnHeaders.length; i++) {
			TableColumn tc = new TableColumn(phraseTable, SWT.NONE, i);
			tc.setText(columnHeaders[i]);

			tc.setResizable(columnLayouts[i].resizable);
			layout.addColumnData(columnLayouts[i]);
			tc.addSelectionListener(headerListener);
		}
	}
	
	/**
	 * @return
	 */
	private void createTableViewer() {
		tableViewer = new TableViewer(phraseTable);
//		tableViewer.setContentProvider(new PhraseContentProvider());
		tableViewer.setLabelProvider(new SuggestionLabelProvider());
		tableViewer.setComparator(suggestionSorter);
	}
	
	/**
	 * @param parent
	 */
	private void addFilterSupportToTableViewer() {
		final TableColumnTextFilter pf = new TableColumnTextFilter(0);
		tableViewer.addFilter(pf);

		filterTextbox.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String filterText = ((Text) e.widget).getText();
				pf.setFilterText(filterText);
				tableViewer.refresh();
			}

		});

	}







	private void hookListeners() {
		editorPartListener = new EditorPartListener();
		getSite().getPage().addPartListener(editorPartListener);
		getSite().setSelectionProvider(tableViewer);
	}

	private void loadCurrentEditorSuggestions() {
		IEditorPart part = getSite().getWorkbenchWindow().getActivePage()
				.getActiveEditor();
		if (part != null && part instanceof ChartEditor) {

			AnalysisEditorInput input = (AnalysisEditorInput) ((ChartEditor) part)
					.getEditorInput();
			Analysis analysis = input.getAnalysis();

//			if (analysis.getType().equals(AnalysisEngineMetadata.TYPE_OE)) {
//				tableViewer.setInput(analysis.getStatistics());
//			}
		}
	}

	public void refresh() {
		tableViewer.refresh();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		if (chartSelectionFilter == null)
			return;
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

}