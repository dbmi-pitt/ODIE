/**
 * @author Girish Chavan
 *
 * Oct 21, 2008
 */
package edu.pitt.dbmi.odie.ui.views;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;

import edu.pitt.dbmi.odie.ui.actions.HelpToggleAction;

public class AnnotationsViewWithHelp extends AnnotationTypeView {

	private final String DEFAULT_HELP_TEXT = "The Annotations View shows the annotations for the current "
			+ "document. \n\n"
			+ "You can select one or more annotations in this view to "
			+ "highlight them in the document. \n\n"
			+ "The annotations overlap in the document in the order in "
			+ "which they appear here. You may change the order of the groups "
			+ "by dragging the group names up or down with the mouse.";

	private HelpTextPanel helpPanel;
	private Composite mainPanel;
	private StackLayout stackLayout;
	private Composite parent;

	HelpToggleAction helpToggleAction;

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		doParentLayout();
		addMainPanel();
		addHelpFeatures();

		stackLayout.topControl = mainPanel;

	}

	private void addMainPanel() {
		mainPanel = new Composite(parent, SWT.NONE);
		super.createPartControl(mainPanel);
	}

	private void doParentLayout() {
		stackLayout = new StackLayout();
		parent.setLayout(stackLayout);
	}

	private void addHelpFeatures() {
		createHelpPanel(parent);
		helpToggleAction = new HelpToggleAction(helpPanel, mainPanel, parent);
		addHelpButtonToActionBar();
		hookHideHelpListener();
	}

	private void hookHideHelpListener() {
		helpPanel.addHideHelpButtonListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				helpToggleAction.run();
			}

		});
	}

	private void createHelpPanel(Composite parent) {
		helpPanel = new HelpTextPanel(parent, SWT.NONE);
		setHelpText(DEFAULT_HELP_TEXT);
	}

	private void addHelpButtonToActionBar() {
		IToolBarManager toolbar = getViewSite().getActionBars()
				.getToolBarManager();
		toolbar.add(helpToggleAction);
	}

	public void setHelpText(String helpText) {
		helpPanel.setText(helpText);
	}

}
