package edu.pitt.dbmi.odie.ui.editors.analysis;

import org.apache.uima.cas.TypeSystem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import edu.pitt.dbmi.odie.ui.views.providers.TypeSystemTreeContentProvider;
import edu.pitt.dbmi.odie.ui.views.providers.TypeSystemTreeLabelProvider;

public class TypeSystemTree extends FilteredTree {
	public TypeSystemTree(Composite parent, PatternFilter filter) {
		this(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER,
				filter);
	}

	public TypeSystemTree(Composite parent, int treeStyle, PatternFilter filter) {
		super(parent, treeStyle, filter, true);
		attachProviders();
		applyLayout();
	}

	public TypeSystemTree(Composite parent) {
		this(parent, new PatternFilter());
	}

	protected void attachProviders() {
		this.getViewer()
				.setContentProvider(new TypeSystemTreeContentProvider());
		this.getViewer().setLabelProvider(
				new TypeSystemTreeLabelProvider(this, Display.getCurrent()));
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

	public void setInput(TypeSystem ts) {
		if (ts == null)
			this.getViewer().setInput(new Object());
		else
			this.getViewer().setInput(ts);
	}
}
