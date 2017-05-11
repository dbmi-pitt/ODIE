package edu.pitt.dbmi.odie.ui.views;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.OpenEvent;
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
import org.eclipse.ui.IWorkbenchPart;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.model.Datapoint;
import edu.pitt.dbmi.odie.model.LanguageResource;
import edu.pitt.dbmi.odie.ui.editors.analysis.AnalysisEditor;
import edu.pitt.dbmi.odie.ui.editors.analysis.AnalysisEditorInput;
import edu.pitt.dbmi.odie.ui.editors.analysis.ChartEditor;
import edu.pitt.dbmi.odie.ui.editors.analysis.EnrichOntologiesPage;
import edu.pitt.dbmi.odie.ui.editors.document.AnnotatedDocumentEditor;
import edu.pitt.dbmi.odie.ui.editors.document.DocumentEditorInput;
import edu.pitt.dbmi.odie.ui.sorters.DatapointSorter;
import edu.pitt.dbmi.odie.ui.sorters.HeaderSortListener;
import edu.pitt.dbmi.odie.ui.views.filters.DatapointsFilter;
import edu.pitt.dbmi.odie.ui.views.providers.ConceptsTableContentProvider;
import edu.pitt.dbmi.odie.ui.views.providers.DatapointLabelProvider;
import edu.pitt.dbmi.odie.ui.views.providers.ItemCountProvider;
import edu.pitt.dbmi.odie.utils.GeneralUtils;
import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IOntology;

public class ConceptsView extends FilteredStackedViewPart {

	public static final String ID = "edu.pitt.dbmi.odie.ui.views.ConceptsView";

	private String columnHeaders[] = { ODIEMessages.headerConceptName,
			ODIEMessages.headerSource, ODIEMessages.headerDocumentFrequency };

	private ColumnLayoutData columnLayouts[] = { new ColumnWeightData(75),
			new ColumnWeightData(60), new ColumnWeightData(30) };

	private int[] columnOrder = new int[] { DatapointSorter.NAME,
			DatapointSorter.SOURCE, DatapointSorter.FREQUENCY };

	private DatapointSorter comparator;

	private Table table;

	TableViewer tableViewer;

	private Text filterText;
	private TableColumnTextFilter textFilter;
	AnalysisEditor currentAnalysisEditor;

	DatapointsFilter chartSelectionFilter;

	@Override
	protected void createDelegateControl(Composite parent) {
		doParentLayoutAndAesthetics(parent);

		createFilterControl(parent);

		createTable(parent);
		createTableViewer();

		registerFilters();
		hookListeners();
		loadCurrentEditorConcepts();

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
		tableViewer.setContentProvider(new ConceptsTableContentProvider());
		tableViewer.setLabelProvider(new DatapointLabelProvider());
		comparator = new DatapointSorter(columnOrder);
		tableViewer.setComparator(comparator);
	}

	/**
	 * @param parent
	 */
	private void registerFilters() {
		textFilter = new TableColumnTextFilter(0);
		registerFilter(textFilter, "filter text");

		chartSelectionFilter = new DatapointsFilter();
		registerFilter(chartSelectionFilter, CHART_SELECTION_FILTER_NAME);
	}

	private void hookListeners() {

		tableViewer.addOpenListener(new IOpenListener() {

			@Override
			public void open(OpenEvent event) {
				Object o = ((StructuredSelection)tableViewer.getSelection()).getFirstElement();
				if(o==null || !(o instanceof Datapoint))
					return;
				
				Datapoint dp = (Datapoint)o;
				if(dp.getConceptClass() == null)
					try {
						dp.setConceptClass(GeneralUtils.getConceptClass(dp.getAnalysis(),dp.getConceptURIString()));
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
				
				GeneralUtils.openClassInOntologyEditor(dp.getConceptClass());

			}

		});
		
		for (TableColumn tc : tableViewer.getTable().getColumns())
			tc.addSelectionListener(new HeaderSortListener(tableViewer,
					comparator));

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
								.setSelection(((StructuredSelection) selection)
										.toList());
						filterChanged();
					} else if (page instanceof EnrichOntologiesPage) {
						chartSelectionFilter.clearSelection();
						filterChanged();
					}
				}
			}
		});

		getSite().getPage().addPartListener(new IPartListener() {

			@Override
			public void partActivated(IWorkbenchPart part) {
				// TODO Auto-generated method stub

			}

			@Override
			public void partBroughtToTop(IWorkbenchPart part) {
				if (part instanceof AnnotatedDocumentEditor) {
					unregisterFilter(chartSelectionFilter);
					filterChanged();
					AnalysisDocument ad = ((DocumentEditorInput) ((AnnotatedDocumentEditor) part)
							.getEditorInput()).getAnalysisDocument();

					if (tableViewer.getInput() != ad) {
						tableViewer.setInput(ad);
						if (ad.getDatapoints().size() > 0)
							tableViewer
									.setSelection(new StructuredSelection(
											new Object[] { ad.getDatapoints()
													.get(0) }));
					}
				} else if (part instanceof AnalysisEditor) {
					chartSelectionFilter.clearSelection();
					registerFilter(chartSelectionFilter,
							CHART_SELECTION_FILTER_NAME);
					filterChanged();
					Analysis a = ((AnalysisEditorInput) ((AnalysisEditor) part)
							.getEditorInput()).getAnalysis();

					if (tableViewer.getInput() != a) {
						tableViewer.setInput(a);
						tableViewer.setSelection(new StructuredSelection(
								new ArrayList()));
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

	public static String CHART_SELECTION_FILTER_NAME = "Chart";

	private void loadCurrentEditorConcepts() {
		IEditorPart part = getSite().getWorkbenchWindow().getActivePage()
				.getActiveEditor();
		if (part != null && part instanceof AnalysisEditor) {
			Analysis a = ((AnalysisEditorInput) ((AnalysisEditor) part)
					.getEditorInput()).getAnalysis();
			tableViewer.setInput(a);
		}
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		filterText.setFocus();
	}

	public void refreshView() {
		filterChanged();
	}

	@Override
	public void dispose() {
		unhookListeners();
		super.dispose();
	}

	private void unhookListeners() {
		getSite().setSelectionProvider(null);
	}

	@Override
	public StructuredViewer getStructuredViewer() {
		return tableViewer;
	}

	@Override
	public long getFilteredItemCount() {
		return tableViewer.getTable().getItemCount();
	}

	@Override
	public long getUnfilteredItemCount() {
		assert tableViewer.getContentProvider() instanceof ItemCountProvider : "Content provider used for a "
				+ "viewer in a FilteredStackedViewPart must implement TotalItemCountProvider";

		return ((ItemCountProvider) tableViewer.getContentProvider())
				.getItemCount();
	}
}