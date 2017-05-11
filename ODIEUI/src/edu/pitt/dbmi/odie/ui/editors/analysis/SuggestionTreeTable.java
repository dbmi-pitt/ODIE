package edu.pitt.dbmi.odie.ui.editors.analysis;

import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;

import edu.pitt.dbmi.odie.model.Suggestion;
import edu.pitt.dbmi.odie.ui.dnd.SuggestionDragListener;
import edu.pitt.dbmi.odie.ui.dnd.SuggestionTransfer;
import edu.pitt.dbmi.odie.ui.editors.providers.SuggestionsContentProvider;
import edu.pitt.dbmi.odie.ui.editors.providers.SuggestionsLabelProvider;
import edu.pitt.dbmi.odie.ui.sorters.HeaderSortListener;
import edu.pitt.dbmi.odie.ui.sorters.MultiColumnSorter;
import edu.pitt.dbmi.odie.ui.sorters.SuggestionSorter;

public class SuggestionTreeTable extends TreeViewer {

	private int[] columnOrder = new int[] { SuggestionSorter.SUGGESTION,
			SuggestionSorter.SCORE, SuggestionSorter.METHOD,
			SuggestionSorter.INFO, SuggestionSorter.RELATEDCONCEPT };

	public static String columnHeaders[] = { "Suggestion", "Score",
			"Related Concept", "Method", "Evidence" };

	private ColumnLayoutData columnLayouts[] = { new ColumnWeightData(40),
			new ColumnWeightData(20), new ColumnWeightData(20),
			new ColumnWeightData(20), new ColumnWeightData(20) };

	private static int[] columnWidths = new int[] { 150, 50, 150, 60, 100 };

	private MultiColumnSorter<Suggestion> suggestionsComparator = new SuggestionSorter(
			columnOrder);

	public SuggestionTreeTable(Composite parent) {
		super(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI
				| SWT.FULL_SELECTION | SWT.BORDER);

		createColumns();
		addSortingSupport();
		attachProviders();
		applyLayout(getDefaultLayout());
		doMiscSetup();

		addDragSupport();

	}

	void createColumns() {
		for (int i = 0; i < columnHeaders.length; i++) {
			TreeColumn tc = new TreeColumn(getTree(), SWT.LEFT);
			tc.setText(columnHeaders[i]);
			tc.setResizable(columnLayouts[i].resizable);
			tc.setWidth(columnWidths[i]);
		}
	}

	private void addSortingSupport() {
		setComparator(suggestionsComparator);
		for (TreeColumn tc : getTree().getColumns())
			tc.addSelectionListener(new HeaderSortListener(this,
					suggestionsComparator));
	}

	protected void attachProviders() {
		setContentProvider(new SuggestionsContentProvider());
		setLabelProvider(new SuggestionsLabelProvider());
	}

	protected void applyLayout() {
		GridData gd = getDefaultLayout();
		getTree().setLayoutData(gd);
	}

	private GridData getDefaultLayout() {
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		return gd;
	}

	protected void applyLayout(GridData gd) {
		getTree().setLayoutData(gd);
	}

	private void doMiscSetup() {
		getTree().setLinesVisible(true);
		getTree().setHeaderVisible(true);

	}

	private void addDragSupport() {
		int ops = DND.DROP_COPY;
		Transfer[] transfers = new Transfer[] { SuggestionTransfer
				.getInstance() };

		// JFace DND - Does not work
		// tableViewer.addDragSupport(ops, transfers, new
		// SuggestionDragListener(tableViewer));

		// SWT DND
		DragSource source = new DragSource(getTree(), ops);
		source.setTransfer(transfers);
		source.addDragListener(new SuggestionDragListener(this));
	}

}
