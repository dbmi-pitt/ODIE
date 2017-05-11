package edu.pitt.dbmi.odie.ui.editors.ontology;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import edu.pitt.dbmi.odie.ui.editors.providers.OntologyTreeContentProvider;
import edu.pitt.dbmi.odie.ui.editors.providers.OntologyTreeLabelProvider;
import edu.pitt.ontology.IOntology;

public class OntologyFilteredTree extends FilteredTree {
	public OntologyFilteredTree(Composite parent, PatternFilter filter) {
		this(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER,
				filter);
	}

	public OntologyFilteredTree(Composite parent, int treeStyle,
			PatternFilter filter) {
		super(parent, treeStyle, filter, true);
		attachProviders();
		applyLayout();
	}

	public OntologyFilteredTree(Composite parent) {
		this(parent, new PatternFilter());
	}

	protected void attachProviders() {
		this.getViewer().setContentProvider(new OntologyTreeContentProvider());
		this.getViewer().setLabelProvider(new OntologyTreeLabelProvider());
	}

	protected void applyLayout() {
		GridData gd = new GridData();
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalAlignment = GridData.FILL;
		gd.minimumHeight = 150;
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = true;

		this.getViewer().getTree().setLayoutData(gd);
	}

	public void setInput(IOntology o) {
		if (o == null)
			this.getViewer().setInput(new Object());
		else
			this.getViewer().setInput(o);
	}

	public void refresh() {
		getViewer().refresh();

	}
}
