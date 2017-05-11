/**
 * @author Girish Chavan
 *
 * Oct 21, 2008
 */
package edu.pitt.dbmi.odie.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.ui.Aesthetics;

public class HelpToggleAction extends Action {

	private static final String SHOW_HELP_NAME = "Show Help";
	private static final String SHOW_HELP_TOOLTIP = "Show Help";

	private static final String HIDE_HELP_NAME = "Hide Help";
	private static final String HIDE_HELP_TOOLTIP = "Hide Help";
	private Control helpPanel;
	private Control mainPanel;
	private StackLayout stack;
	private Composite parent;

	public HelpToggleAction(Control helpPanel, Control mainPanel,
			Composite stackParent) {
		super(SHOW_HELP_NAME, IAction.AS_CHECK_BOX);
		setToolTipText(SHOW_HELP_TOOLTIP);
		setImageDescriptor(Aesthetics.getHelpImage());

		this.helpPanel = helpPanel;
		this.mainPanel = mainPanel;
		this.parent = stackParent;
		this.stack = (StackLayout) parent.getLayout();

	}

	@Override
	public void run() {
		if (stack.topControl == helpPanel) {
			toggleToMain();
		} else
			toggleToHelp();

	}

	private void toggleToHelp() {
		stack.topControl = helpPanel;
		parent.layout();
		setText(HIDE_HELP_NAME);
		setToolTipText(HIDE_HELP_TOOLTIP);
		this.setChecked(true);
	}

	private void toggleToMain() {
		stack.topControl = mainPanel;
		parent.layout();
		setText(SHOW_HELP_NAME);
		setToolTipText(SHOW_HELP_TOOLTIP);
		this.setChecked(false);
	}

}
