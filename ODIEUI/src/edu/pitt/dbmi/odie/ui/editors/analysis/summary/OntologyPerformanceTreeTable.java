package edu.pitt.dbmi.odie.ui.editors.analysis.summary;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.Statistics;

public class OntologyPerformanceTreeTable extends TreeViewer {


	public OntologyPerformanceTreeTable(Composite parent) {
		super(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI
				| SWT.FULL_SELECTION | SWT.BORDER);

		
		createColumns(new ArrayList<Analysis>());
		addSortingSupport();
		attachProviders();
		applyLayout(getDefaultLayout());
		doMiscSetup();
	}

	void createColumns(List<Analysis> analyses) {
		removeExistingColumns();
		
		//column with all labels
		TreeColumn tc = new TreeColumn(getTree(), SWT.LEFT);
		tc.setText("Performance");
		tc.setResizable(true);
		tc.setWidth(200);
		
		for(Analysis a:analyses){
			Collection<Statistics> c = a.getStatistics().ontologyStatistics;
			if(c==null)
				return;
			
			for(Statistics st:c){
				URI uri = (URI)st.context;
				String oname = uri.toASCIIString().substring(uri.toASCIIString().lastIndexOf("/") + 1);
				tc = new TreeColumn(getTree(), SWT.LEFT);
				tc.setText(a.getName() + ":" + oname);
				tc.setResizable(true);
				tc.setWidth(50);
			}
		}
	}

	private void removeExistingColumns() {
		for(TreeColumn tc:getTree().getColumns()){
			tc.dispose();
		}
	}

	private void addSortingSupport() {
//		setComparator(suggestionsComparator);
//		for (TreeColumn tc : getTree().getColumns())
//			tc.addSelectionListener(new HeaderSortListener(this,
//					suggestionsComparator));
	}

	protected void attachProviders() {
		setContentProvider(new OntologyPerformanceContentProvider());
		setLabelProvider(new ComparisonValuesDataLabelProvider());
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

	private void packColumns() {
		for(TreeColumn tc:getTree().getColumns()){
			tc.pack();
		}
	}
	public void setInput(List<Analysis> analyses){
		createColumns(analyses);
		super.setInput(analyses);
		packColumns();
		
	}
	
}
