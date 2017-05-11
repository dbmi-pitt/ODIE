package edu.pitt.dbmi.odie.ui.editors.analysis;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.Datapoint;
import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.dbmi.odie.ui.FormSection;
import edu.pitt.dbmi.odie.ui.editors.providers.OntologyLegendContentProvider;
import edu.pitt.dbmi.odie.ui.editors.providers.OntologyLegendLabelProvider;
import edu.pitt.dbmi.odie.ui.jfreechart.ChartStatistics;
import edu.pitt.dbmi.odie.ui.jfreechart.EnhancedChartComposite;
import edu.pitt.dbmi.odie.ui.sorters.HeaderSortListener;
import edu.pitt.dbmi.odie.ui.sorters.StatisticsSorter;
import edu.pitt.dbmi.odie.ui.views.ODIEMessages;
import edu.pitt.dbmi.odie.ui.views.StickyNotePanel;
import edu.pitt.dbmi.odie.ui.views.DatapointComparator.SortOrder;

public class ChartEditor extends EditorPart implements ISelectionProvider {

	/**
	 * 
	 */
	private static final String NO_DATA_MSG = "No concepts from the ontologies listed below were found in the documents";
	/**
	 * 
	 */
	private static final int CHART_HEIGHT = 600;
	/**
	 * 
	 */
	private static final int CHART_WIDTH = 600;
	public static final String ID = "edu.pitt.dbmi.odie.ui.editors.ChartEditor";
	private static final String STICKY_NOTE_CONTENT = "The chart above shows a histogram for the concepts that were found "
			+ "in the documents. \n\n"
			+ "You may select a range of concepts by clicking and dragging the mouse "
			+ "over the chart.";

	ChartStatistics chartDataset;
	private EnhancedChartComposite chartComposite;
	ListenerList selectionListeners = new ListenerList();
	TableViewer legendviewer;
	Table legendtable;

	// private String columnHeaders[] = {
	// ODIEMessages.headerOntologyName,ODIEMessages.headerCoverage,
	// ODIEMessages.headerFrequency };
	//
	// private ColumnLayoutData columnLayouts[] = { new
	// ColumnPixelData(100,true),new ColumnPixelData(105,true), new
	// ColumnPixelData(70,true) };

	private String columnHeaders[] = { ODIEMessages.headerOntologyName,
			ODIEMessages.headerUniqueConcepts, ODIEMessages.headerCoverage };

	private ColumnLayoutData columnLayouts[] = {
			new ColumnPixelData(200, true), new ColumnPixelData(100, true),
			new ColumnPixelData(80, true) };

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite,
	 * org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);

		setPartName(input.getName());

		createDatasetFromAnalysis(((AnalysisEditorInput) getEditorInput())
				.getAnalysis());
	}

	/**
	 * Creates a CategoryDataset from the Statistic object
	 * 
	 * @return A category dataset populated with data from Statistics object
	 */
	private void createDatasetFromAnalysis(Analysis a) {
		chartDataset = new ChartStatistics(a);
		chartDataset.setMode(SortOrder.ONTOLOGY_DOCFREQUENCY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */

	public void createPartControl(Composite parent) {
		createParentLayout(parent);
		applyAesthetics(parent);

		if (chartDataset.getColumnCount() == 0)
			addNoDataPanel(parent);
		else
			addChartPanel(parent);

		addOptionsSection(parent);
		addLegendTable(parent);
		addStickyNotePanel(parent);
	}

	private void addOptionsSection(Composite parent) {
		FormSection section = new FormSection(parent, Section.TITLE_BAR
				| Section.EXPANDED);
		section.setText("Y Axis Options");
		GridData gd = new GridData(SWT.LEFT, SWT.TOP, false, false);
		gd.verticalIndent = Aesthetics.INTERGROUP_SPACING;
		section.setLayoutData(gd);

		Composite client = new Composite(section, SWT.NULL);
		client.setBackground(Aesthetics.getWhiteColor());
		client.setLayout(new RowLayout(SWT.VERTICAL));
		section.setClient(client);

		Button documentFreqButton = new Button(client, SWT.RADIO);
		documentFreqButton.setText("Document Frequency");
		documentFreqButton.setSelection(true);
		documentFreqButton.setData(SortOrder.ONTOLOGY_DOCFREQUENCY);

		Button occurencesButton = new Button(client, SWT.RADIO);
		occurencesButton.setText("Occurences");
		occurencesButton.setData(SortOrder.ONTOLOGY_OCCURENCES);

		SelectionListener listener = new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				handleSelection(e);

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				handleSelection(e);

			}

			private void handleSelection(SelectionEvent e) {
				chartDataset.setMode((SortOrder) ((Button) e.getSource())
						.getData());
				chartComposite.clearSelection(false, true);
				repaintChart();
			}

		};

		documentFreqButton.addSelectionListener(listener);
		occurencesButton.addSelectionListener(listener);
	}

	/**
	 * @param parent
	 */
	private void createParentLayout(Composite parent) {
		GridLayout gl = new GridLayout(2, false);
		gl.marginWidth = Aesthetics.LEFT_MARGIN * 2;
		gl.verticalSpacing = 0;
		parent.setLayout(gl);
	}

	/**
	 * @param parent
	 */
	private void applyAesthetics(Composite parent) {
		if (chartDataset.getColumnCount() == 0)
			parent.setBackground(Aesthetics.getDisabledBackground());
		else
			parent.setBackground(Aesthetics.getEnabledBackground());
	}

	/**
	 * @param parent
	 */
	private void addNoDataPanel(Composite parent) {

		Label noDataLabel = new Label(parent, SWT.NONE);
		noDataLabel.setText(NO_DATA_MSG);

		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		noDataLabel.setLayoutData(gd);
	}

	/**
	 * @param parent
	 */
	private void addStickyNotePanel(Composite parent) {
		StickyNotePanel stickyNotePanel = new StickyNotePanel(parent, SWT.NONE);
		stickyNotePanel.setText(STICKY_NOTE_CONTENT);
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
		gd.verticalIndent = Aesthetics.INTERGROUP_SPACING;
		stickyNotePanel.setLayoutData(gd);
	}

	ISelectionChangedListener chartSelectionListener = new ISelectionChangedListener() {
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			SelectionChangedEvent datapointSelectionEvent = convertToDatapointSelectionEvent(event);
			fireSelectionChangedEvent(datapointSelectionEvent);
		}

		private SelectionChangedEvent convertToDatapointSelectionEvent(
				SelectionChangedEvent event) {
			StructuredSelection chartEntitySelection = (StructuredSelection) event
					.getSelection();
			StructuredSelection datapointSelection = convertToDatapointSelection(chartEntitySelection);
			return new SelectionChangedEvent(ChartEditor.this,
					datapointSelection);
		}
	};

	public void addChartPanel(Composite parent) {

		final JFreeChart chart = createChart(chartDataset);

		if (chartComposite != null) {
			chartComposite
					.removeSelectionChangedListener(chartSelectionListener);
			chartComposite.dispose();
		}
		chartComposite = new EnhancedChartComposite(parent, SWT.NONE, chart,
				CHART_WIDTH, CHART_HEIGHT / 2);
		chartComposite.addSelectionChangedListener(chartSelectionListener);
		chartComposite.setDisplayToolTips(true);

		chartComposite.setSelectionBackground(Aesthetics
				.getChartSelectionColor());
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.heightHint = CHART_HEIGHT / 2 + 10;
		gd.horizontalAlignment = SWT.FILL;
		gd.horizontalSpan = 2;
		chartComposite.setLayoutData(gd);

		Label l = new Label(parent, SWT.NONE);
		l.setText("Concepts");
		l.setBackground(Aesthetics.getWhiteColor());
		gd = new GridData();
		gd.verticalAlignment = SWT.TOP;
		gd.horizontalAlignment = SWT.CENTER;
		gd.horizontalSpan = 2;
		l.setLayoutData(gd);

	}

	/**
	 * Creates a bar chart from the given dataset.
	 * 
	 * @param dataset
	 *            The category dataset to use for the chart.
	 * @return A JFreeChart object
	 * 
	 * @since 1.0
	 */
	private JFreeChart createChart(CategoryDataset dataset) {
		String yaxisLabel = ODIEMessages.headerDocumentFrequency;

		if (chartDataset.getMode() == SortOrder.ONTOLOGY_OCCURENCES)
			yaxisLabel = ODIEMessages.headerOccurences;

		JFreeChart chart = ChartFactory.createBarChart("Concept Frequencies",
				"Concepts", yaxisLabel, dataset, PlotOrientation.VERTICAL,
				false, true, false);

		// chart.removeLegend();
		chart.setBackgroundPaint(Color.white);
		chart.setTitle("");

		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.getRangeAxis().setStandardTickUnits(new ChartTickUnitSource());

		plot.getDomainAxis().setVisible(false);

		BarRenderer renderer = new ChartBarRenderer();

		plot.setRenderer(renderer);

		return chart;
	}

	/**
	 * @param parent
	 */
	private void addLegendTable(Composite parent) {
		createTable(parent);
		createColumns();
		createViewer(parent.getDisplay());

		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		gd.verticalIndent = Aesthetics.INTERGROUP_SPACING;
		legendviewer.getTable().setLayoutData(gd);
		legendviewer.setInput(chartDataset);
	}

	private void createTable(Composite parent) {
		legendtable = new Table(parent, SWT.BORDER);
		legendtable.setLinesVisible(false);

		// table.setLayout(new TableLayout());
	}

	private void createColumns() {

		TableLayout layout = new TableLayout();
		legendtable.setLayout(layout);
		legendtable.setHeaderVisible(true);

		for (int i = 0; i < columnHeaders.length; i++) {
			TableColumn tc = new TableColumn(legendtable, SWT.NONE, i);
			tc.setText(columnHeaders[i]);
			tc.setResizable(columnLayouts[i].resizable);
			tc.setAlignment(SWT.CENTER);
			layout.addColumnData(columnLayouts[i]);

		}
	}

	private int[] columnOrder = new int[] { StatisticsSorter.UNIQUECONCEPTS,
			StatisticsSorter.COVERAGE, StatisticsSorter.NAME };
	private StatisticsSorter comparator;
	
	private void createViewer(Display display) {
		legendviewer = new TableViewer(legendtable);
		legendviewer.setContentProvider(new OntologyLegendContentProvider());
		legendviewer.setLabelProvider(new OntologyLegendLabelProvider());
		comparator = new StatisticsSorter(columnOrder);
		legendviewer.setComparator(comparator);

		for (TableColumn tc : legendviewer.getTable().getColumns())
			tc.addSelectionListener(new HeaderSortListener(legendviewer,
					comparator));

	}

	public CategoryDataset getData() {
		return chartDataset;
	}

	/**
	 * @param event
	 */
	protected void fireSelectionChangedEvent(final SelectionChangedEvent event) {
		Object[] listeners = selectionListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			final ISelectionChangedListener l = (ISelectionChangedListener) listeners[i];
			SafeRunnable.run(new SafeRunnable() {
				public void run() {
					l.selectionChanged(event);
				}
			});
		}

	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionListeners.add(listener);

	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		selectionListeners.remove(listener);

	}

	@Override
	public ISelection getSelection() {
		if (chartComposite == null)
			return new StructuredSelection();
		else
			return convertToDatapointSelection((StructuredSelection) chartComposite
					.getSelection());
	}

	@Override
	public void setSelection(ISelection selection) {
		// TODO Not implemented yet
		chartComposite.setSelection(selection);
	}

	public void refresh() {
		createDatasetFromAnalysis(((AnalysisEditorInput) getEditorInput())
				.getAnalysis());
		repaintChart();
		legendviewer.setInput(chartDataset);
	}

	private void repaintChart() {
		if(chartComposite == null)
			return;
		chartComposite.setChart(createChart(chartDataset));
		chartComposite.forceRedraw();
	}

	public void setFocus() {
		legendviewer.getTable().setFocus();
	}

	/*
	 * ChartEditor is read-only. This method does not do anything.
	 * 
	 * @seeorg.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
	}

	/*
	 * ChartEditor is read-only. This method does not do anything.
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	public void doSaveAs() {
	}

	/*
	 * ChartEditor is read-only. Always returns false.
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	public boolean isDirty() {
		return false;
	}

	/*
	 * ChartEditor is read-only. Always returns false.
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return false;
	}

	private StructuredSelection convertToDatapointSelection(
			StructuredSelection chartEntitySelection) {
		List chartEntityList = chartEntitySelection.toList();
		List<Datapoint> outList = new ArrayList<Datapoint>();

		for (Object o : chartEntityList) {
			outList.add((Datapoint) ((CategoryItemEntity) o).getColumnKey());
		}

		StructuredSelection datapointSelection = new StructuredSelection(
				outList);
		return datapointSelection;
	}

}
