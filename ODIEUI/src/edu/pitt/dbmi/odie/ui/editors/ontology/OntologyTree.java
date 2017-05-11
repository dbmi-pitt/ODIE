package edu.pitt.dbmi.odie.ui.editors.ontology;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import edu.pitt.dbmi.odie.ui.editors.providers.OntologyTreeContentProvider;
import edu.pitt.dbmi.odie.ui.editors.providers.OntologyTreeLabelProvider;

public class OntologyTree extends TreeViewer {
	public OntologyTree(Composite parent) {
		super(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.VIRTUAL);
		attachProviders();
		applyLayout();
	}

	protected void attachProviders() {
		setContentProvider(new OntologyTreeContentProvider());
		setLabelProvider(new OntologyTreeLabelProvider());
	}

	protected void applyLayout() {
		GridData gd = new GridData();
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalAlignment = GridData.FILL;
		gd.minimumHeight = 150;
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = true;

		getTree().setLayoutData(gd);
	}
}
