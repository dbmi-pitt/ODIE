/**
 * @author Girish Chavan
 *
 * Oct 21, 2008
 */
package edu.pitt.dbmi.odie.ui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import edu.pitt.dbmi.odie.ui.Aesthetics;

public abstract class StackedViewPart extends ViewPart {

	private final String DEFAULT_MSG = "No information to display at this time.";

	private Composite delegateControl;
	private StackLayout stackLayout;

	private Composite defaultControl;

	private Composite stackComposite;

	@Override
	public void createPartControl(Composite parent) {
		this.stackComposite = parent;
		doParentLayout(parent);

		createAndAddDefaultControl(parent);
		stackLayout.topControl = defaultControl;

		// we do this last so that the delegate has more control over which
		// control is finally visible
		createAndAddDelegateControl(parent);
	}

	Text defaultText;

	protected void createAndAddDefaultControl(Composite parent) {
		defaultControl = new Composite(parent, SWT.NONE);
		FillLayout layout = new FillLayout();
		layout.marginHeight = Aesthetics.INTRAGROUP_SPACING;
		layout.marginWidth = Aesthetics.INTRAGROUP_SPACING;

		defaultControl.setLayout(layout);
		defaultText = new Text(defaultControl, SWT.WRAP | SWT.READ_ONLY);
		defaultText.setText(DEFAULT_MSG);
	}

	private void createAndAddDelegateControl(Composite parent) {
		delegateControl = new Composite(parent, SWT.NONE);
		createDelegatePartControl(delegateControl);

	}

	public StackLayout getStackLayout() {
		return stackLayout;
	}

	public void setDefaultText(String defaultText) {
		this.defaultText.setText(defaultText);
	}

	public abstract void createDelegatePartControl(Composite parent);

	private void doParentLayout(Composite parent) {
		stackLayout = new StackLayout();
		parent.setLayout(stackLayout);
	}

	public void showDefaultControl() {
		stackLayout.topControl = defaultControl;
		stackComposite.layout();
	}

	public void showDelegateControl() {
		stackLayout.topControl = delegateControl;
		stackComposite.layout();
	}

	public void refresh() {
		if (stackLayout.topControl == delegateControl)
			refreshView();
	}

	protected abstract void refreshView();
}
