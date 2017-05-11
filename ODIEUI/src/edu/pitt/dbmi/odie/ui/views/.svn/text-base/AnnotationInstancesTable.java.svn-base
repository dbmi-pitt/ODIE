package edu.pitt.dbmi.odie.ui.views;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;

import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.ui.views.providers.AnnotationInstancesContentProvider;
import edu.pitt.dbmi.odie.ui.views.providers.AnnotationLabelProvider;

public class AnnotationInstancesTable extends TableViewer {

	private AnnotationLabelProvider labelProvider;
	private AnnotationInstancesContentProvider contentProvider;

	public AnnotationInstancesTable(Composite parent, int style) {
		super(parent, style);
		attachProviders();
		getTable().setHeaderVisible(true);
	}

	protected void attachProviders() {
		contentProvider = new AnnotationInstancesContentProvider();
		setContentProvider(contentProvider);
		labelProvider = new AnnotationLabelProvider();
		setLabelProvider(labelProvider);
	}

	public void setAnalysisDocument(AnalysisDocument ad) {
		contentProvider.setAnalysisDocument(ad);
	}

	public void selectFirstRow() {
		Object o = contentProvider.getFirstInstance();

		if (o != null)
			this.setSelection(new StructuredSelection(o));

	}

	// @Override
	// public void setSelection(ISelection selection) {
	// if(selection instanceof StructuredSelection){
	// Object o = ((StructuredSelection)selection).getFirstElement();
	// if(o instanceof AnnotationFS){
	// this.setse
	// }
	// }
	// super.setSelection(selection);
	// }
	//	

}
