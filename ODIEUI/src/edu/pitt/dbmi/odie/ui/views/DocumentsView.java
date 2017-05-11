package edu.pitt.dbmi.odie.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.model.Datapoint;
import edu.pitt.dbmi.odie.model.Suggestion;
import edu.pitt.dbmi.odie.ui.editors.analysis.AnalysisEditor;
import edu.pitt.dbmi.odie.ui.editors.analysis.AnalysisEditorInput;
import edu.pitt.dbmi.odie.ui.editors.analysis.ChartEditor;
import edu.pitt.dbmi.odie.ui.editors.analysis.EnrichOntologiesPage;
import edu.pitt.dbmi.odie.ui.editors.document.AnnotatedDocumentEditor;
import edu.pitt.dbmi.odie.ui.editors.document.DocumentEditorInput;
import edu.pitt.dbmi.odie.ui.sorters.DocumentsSorter;
import edu.pitt.dbmi.odie.ui.sorters.HeaderSortListener;
import edu.pitt.dbmi.odie.ui.views.filters.DocumentsFilter;
import edu.pitt.dbmi.odie.ui.views.providers.DocumentsContentProvider;
import edu.pitt.dbmi.odie.ui.views.providers.DocumentsLabelProvider;
import edu.pitt.dbmi.odie.ui.views.providers.ItemCountProvider;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class DocumentsView extends FilteredStackedViewPart implements
		ISelectionListener, ISelectionChangedListener {

	public static final String ID = "edu.pitt.dbmi.odie.ui.views.DocumentsView";

	protected DocumentsFilter chartSelectionFilter;
	protected DocumentsFilter conceptSelectionFilter;
	protected DocumentsFilter suggestionsSelectionFilter;

	private String columnHeaders[] = { DocumentsMessages.headerName,
			DocumentsMessages.headerStatus };
	private ColumnLayoutData columnLayouts[] = { new ColumnWeightData(60),
			new ColumnWeightData(45) };
	private int[] columnOrder = new int[] { DocumentsSorter.NAME,
			DocumentsSorter.STATUS };
	// private String columnHeaders[] = { DocumentsMessages.headerName,
	// DocumentsMessages.headerAnnotations, DocumentsMessages.headerStatus };
	//
	// private ColumnLayoutData columnLayouts[] = { new ColumnWeightData(70),
	// new ColumnWeightData(60), new ColumnWeightData(45) };
	//
	// private int[] columnOrder = new int[]{DocumentsSorter.NAME,
	// DocumentsSorter.ANNOTATIONS, DocumentsSorter.STATUS};

	private DocumentsSorter comparator;

	private Action doubleClickAction;

	private Text filterText;
	Logger logger = Logger.getLogger(DocumentsView.class);

	private Action selectionChangedAction;
	private ISelectionChangedListener self = this;

	private Table table;

	private TableColumnTextFilter textFilter;

	private TableViewer tableViewer;

	public void createDelegateControl(Composite parent) {
		doParentLayoutAndAesthetics(parent);
		createFilterControl(parent);

		createTable(parent);
		createTableViewer();

		registerFilters();

		makeActions();
		hookListeners();

		getSite().setSelectionProvider(tableViewer);
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
		filterText = new Text(parent, SWT.BORDER);
		filterText.setText("type filter text");
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.TOP;
		filterText.setLayoutData(gd);
		filterText.selectAll();
	}

	/**
	 * Creates the table control.
	 */
	void createTable(Composite parent) {
		table = new Table(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI
				| SWT.FULL_SELECTION);
		table.setLinesVisible(true);

		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.verticalAlignment = SWT.FILL;
		gd.grabExcessVerticalSpace = true;

		table.setLayoutData(gd);

		createColumns();
	}

	void createColumns() {
		TableLayout layout = new TableLayout();
		table.setLayout(layout);
		table.setHeaderVisible(true);

		for (int i = 0; i < columnHeaders.length; i++) {
			TableColumn tc = new TableColumn(table, SWT.NONE, i);
			tc.setText(columnHeaders[i]);

			tc.setResizable(columnLayouts[i].resizable);
			layout.addColumnData(columnLayouts[i]);
		}
	}

	private void createTableViewer() {
		tableViewer = new TableViewer(table);
		tableViewer.setContentProvider(new DocumentsContentProvider());
		tableViewer.setLabelProvider(new DocumentsLabelProvider());
		comparator = new DocumentsSorter(columnOrder);
		tableViewer.setComparator(comparator);
	}

	public static String CONCEPT_VIEW_FILTER_NAME = "Chart selection";
	public static String CONCEPT_SELECTION_FILTER_NAME = "Concepts View selection";
	public static String SUGGESTION_SELECTION_FILTER_NAME = "Suggestions selection";

	/**
	 * @param parent
	 */
	private void registerFilters() {
		textFilter = new TableColumnTextFilter(0);
		registerFilter(textFilter, "filter text");

		chartSelectionFilter = new DocumentsFilter();
		registerFilter(chartSelectionFilter, CONCEPT_VIEW_FILTER_NAME);

		conceptSelectionFilter = new DocumentsFilter();
		registerFilter(conceptSelectionFilter, CONCEPT_SELECTION_FILTER_NAME);

		suggestionsSelectionFilter = new DocumentsFilter();
		registerFilter(suggestionsSelectionFilter,
				SUGGESTION_SELECTION_FILTER_NAME);
	}

	private void makeActions() {
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = tableViewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();

				DocumentEditorInput input = new DocumentEditorInput(
						(AnalysisDocument) obj);
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				try {

					page.openEditor(input, AnnotatedDocumentEditor.ID);

				} catch (PartInitException e) {
					logger.error(e.getMessage());
				}

			}
		};

		selectionChangedAction = new Action() {
			public void run() {
				ISelection selection = tableViewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();

				DocumentEditorInput input = new DocumentEditorInput(
						(AnalysisDocument) obj);

				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();

				if (page == null)
					return;

				IEditorPart part = page.findEditor(input);
				if (part != null)
					page.bringToTop(part);
			}
		};
	}

	private void hookListeners() {
		filterText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String t = ((Text) e.widget).getText();
				textFilter.setFilterText(t);
				filterChanged();
			}
		});
		filterText.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}

			@Override
			public void mouseDown(MouseEvent e) {
				filterText.selectAll();

			}

			@Override
			public void mouseUp(MouseEvent e) {
			}

		});
		getSite().getPage().addPostSelectionListener(new ISelectionListener() {

			@Override
			public void selectionChanged(IWorkbenchPart part,
					ISelection selection) {

				if (part instanceof ConceptsView) {
					// ConceptsView selection filters the Documents View only if
					// the current active editor is AnalysisEditor
					if (GeneralUtils.getActiveEditor() instanceof AnalysisEditor) {
						List selList = ((StructuredSelection) selection)
								.toList();
						conceptSelectionFilter.setDatapointSelection(selList);
						suggestionsSelectionFilter.clearSelection();
						chartSelectionFilter.clearSelection();
						filterChanged();
					}
				}
				// else if(part instanceof SuggestionsView){
				// suggestionsSelectionFilter.setSuggestionSelection(((StructuredSelection)selection).toList());
				// conceptSelectionFilter.clearSelection();
				// chartSelectionFilter.clearSelection();
				// filterChanged();
				// }

			}
		});

		getSite().getPage().addSelectionListener(new ISelectionListener() {

			@Override
			public void selectionChanged(IWorkbenchPart part,
					ISelection selection) {
				if (part instanceof AnalysisEditor) {
					Object page = ((AnalysisEditor) part).getActiveEditor();
					if (page == null)
						page = ((AnalysisEditor) part).getActivePageInstance();

					if (page instanceof ChartEditor) {
						chartSelectionFilter
								.setDatapointSelection(((StructuredSelection) selection)
										.toList());

						// TODO ideally at this point we should fetch the
						// selection
						// in ConceptsView to determine this. The problem is
						// that
						// we donot know if ConceptsView has been updated before
						// or after we get the call here. Current solution
						// assumes
						// that new chart selection is completely distinct from
						// the
						// prev. chart selection. That would mean that any
						// selections
						// in the concepts view would not be valid. However this
						// may not be necessary, in which case even if a concept
						// is selected in the concepts view it will not filter

						conceptSelectionFilter.clearSelection();
						suggestionsSelectionFilter.clearSelection();
						filterChanged();
					} else if (page instanceof EnrichOntologiesPage) {
						if (!((StructuredSelection) selection).isEmpty()
								&& ((StructuredSelection) selection)
										.getFirstElement() instanceof Suggestion) {
							suggestionsSelectionFilter
									.setSuggestionSelection(((StructuredSelection) selection)
											.toList());
							chartSelectionFilter.clearSelection();
							conceptSelectionFilter.clearSelection();
							filterChanged();
						}
					}
				}
			}
		});

		hookDocumentsViewSelectionChangedAction();
		hookDoubleClickAction();

		for (TableColumn tc : tableViewer.getTable().getColumns())
			tc.addSelectionListener(new HeaderSortListener(tableViewer,
					comparator));

		getSite().getPage().addPartListener(new IPartListener() {

			@Override
			public void partActivated(IWorkbenchPart part) {
				// TODO Auto-generated method stub

			}

			@Override
			public void partBroughtToTop(IWorkbenchPart part) {
				if (part instanceof AnalysisEditor) {
					Analysis a = ((AnalysisEditorInput) ((AnalysisEditor) part)
							.getEditorInput()).getAnalysis();

					if (tableViewer.getInput() != a) {
						tableViewer.setInput(a);

						if (GeneralUtils.isOther(a)) {
							unregisterFilter(chartSelectionFilter);
							unregisterFilter(conceptSelectionFilter);
							filterChanged();
						} else {
							chartSelectionFilter.clearSelection();
							conceptSelectionFilter.clearSelection();
							
							registerFilter(chartSelectionFilter,
									CONCEPT_VIEW_FILTER_NAME);
							registerFilter(conceptSelectionFilter,
									CONCEPT_SELECTION_FILTER_NAME);
							filterChanged();
						}

					}
				}
			}

			@Override
			public void partClosed(IWorkbenchPart part) {
				// TODO Auto-generated method stub

			}

			@Override
			public void partDeactivated(IWorkbenchPart part) {
				// TODO Auto-generated method stub

			}

			@Override
			public void partOpened(IWorkbenchPart part) {
				// TODO Auto-generated method stub

			}

		});
	}

	private void hookDocumentsViewSelectionChangedAction() {
		tableViewer
				.addPostSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						selectionChangedAction.run();

					}

				});

	}

	@Override
	public void dispose() {
		unhookListeners();
		super.dispose();
	}

	@Override
	public long getFilteredItemCount() {
		return tableViewer.getTable().getItemCount();
	}

	@Override
	public StructuredViewer getStructuredViewer() {
		return tableViewer;
	}

	@Override
	public long getUnfilteredItemCount() {
		assert tableViewer.getContentProvider() instanceof ItemCountProvider : "Content provider used for a "
				+ "viewer in a FilteredStackedViewPart must implement ItemCountProvider";

		return ((ItemCountProvider) tableViewer.getContentProvider())
				.getItemCount();
	}

	private void hookDoubleClickAction() {
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	public void refreshView() {
		filterChanged();
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		Analysis a = ((Analysis) ((StructuredSelection) selection)
				.getFirstElement());
		tableViewer.setInput(a);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(
	 * org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		filterChanged();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		tableViewer.getControl().setFocus();
	}

	private void unhookListeners() {
		getSite().setSelectionProvider(null);
		// getSite().getPage().removePartListener(analysisEditorPartListener);
	}

}