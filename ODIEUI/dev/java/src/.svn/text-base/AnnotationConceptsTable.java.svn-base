package edu.pitt.dbmi.odie.ui.views;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;

import edu.pitt.dbmi.odie.ui.views.providers.AnnotationConceptsContentProvider;
import edu.pitt.dbmi.odie.ui.views.providers.OntologyConceptLabelProvider;

public class AnnotationConceptsTable extends TableViewer {

	private OntologyConceptLabelProvider labelProvider;
	private AnnotationConceptsContentProvider contentProvider;

	public AnnotationConceptsTable(Composite parent, int style) {
		super(parent, style);
		attachProviders();
		getTable().setHeaderVisible(true);
	}

	protected void attachProviders() {
		contentProvider = new AnnotationConceptsContentProvider();
		setContentProvider(contentProvider);
		labelProvider = new OntologyConceptLabelProvider();
		setLabelProvider(labelProvider);
	}

}
