package edu.pitt.dbmi.odie.ui.views.details;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;

public class DetailsTreeTable extends TreeViewer {

	public static String columnHeaders[] = { "Feature", "Value" };

	private ColumnLayoutData columnLayouts[] = { new ColumnWeightData(40),
			new ColumnWeightData(20) };

	private int[] columnWidths;

	Logger logger = Logger.getLogger(DetailsTreeTable.class);

	public DetailsTreeTable(Composite parent) {
		this(parent, new int[] { 250, 150 });
	}

	public DetailsTreeTable(Composite parent, int[] columnWidths) {
		super(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI
				| SWT.FULL_SELECTION | SWT.BORDER);

		this.columnWidths = columnWidths;
		createColumns();
		applyLayout(getDefaultLayout());
		doMiscSetup();
	}

	void createColumns() {
		for (int i = 0; i < columnHeaders.length; i++) {
			TreeColumn tc = new TreeColumn(getTree(), SWT.LEFT);
			tc.setText(columnHeaders[i]);
			tc.setResizable(columnLayouts[i].resizable);
			tc.setWidth(columnWidths[i]);
		}
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

	public boolean setupProvidersFor(Object element) {
		logger.debug(element.getClass().getName());
		DetailViewFactory dvf = DetailViewFactory.getInstance();
		boolean cr = replaceIfRequired(dvf.getContentProvider(element
				.getClass()));
		boolean lr = replaceIfRequired(dvf.getLabelProvider(element.getClass()));

		return cr || lr;
	}

	private boolean replaceIfRequired(IContentProvider contentProvider) {
		if (getContentProvider() != contentProvider) {
			setContentProvider(contentProvider);
			return true;
		}
		return false;
	}

	private boolean replaceIfRequired(IBaseLabelProvider labelProvider) {
		if (getLabelProvider() != labelProvider) {
			setLabelProvider(labelProvider);
			return true;
		}
		return false;
	}

}
