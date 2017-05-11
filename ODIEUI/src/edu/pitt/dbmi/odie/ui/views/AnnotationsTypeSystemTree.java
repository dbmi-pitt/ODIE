package edu.pitt.dbmi.odie.ui.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.PatternFilter;

import edu.pitt.dbmi.odie.ui.editors.analysis.TypeSystemTree;
import edu.pitt.dbmi.odie.ui.views.providers.AnnotationTypeSystemTreeContentProvider;
import edu.pitt.dbmi.odie.ui.views.providers.AnnotationTypeSystemTreeLabelProvider;

public class AnnotationsTypeSystemTree extends TypeSystemTree {

	public AnnotationsTypeSystemTree(Composite parent, PatternFilter filter) {
		super(parent, filter);
	}

	@Override
	protected void attachProviders() {
		getViewer().setContentProvider(
				new AnnotationTypeSystemTreeContentProvider(
						"uima.tcas.Annotation"));
		getViewer().setLabelProvider(
				new AnnotationTypeSystemTreeLabelProvider(this, parent
						.getDisplay()));
	}

	public AnnotationTypeSystemTreeLabelProvider getLabelProvider() {
		return (AnnotationTypeSystemTreeLabelProvider) getViewer()
				.getLabelProvider();
	}

}
