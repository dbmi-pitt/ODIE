package edu.pitt.dbmi.odie.ui.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.ui.dialogs.FiltersDialog;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public abstract class FilteredStackedViewPart extends StackedViewPart {

	private static final String NO_MATCHES_FILTERED = "No matches filtered from view";
	private Label messageLabel;

	private static ImageDescriptor filtersIcon;

	static {
		filtersIcon = Activator.getImageDescriptor("images/filter_ps.gif");
	}

	@Override
	public void createDelegatePartControl(Composite parent) {
		doParentLayoutAndAesthetics(parent);
		createMessageControl(parent);
		addFiltersButtonToActionBar();

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		createDelegateControl(composite);
	}

	protected abstract void createDelegateControl(Composite parent);

	private void doParentLayoutAndAesthetics(Composite parent) {
		GridLayout gl = new GridLayout(1, false);
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		// gl.horizontalSpacing = 0;
		// gl.verticalSpacing = 0;
		// gl.numColumns = 1;
		parent.setLayout(gl);
	}

	private void createMessageControl(Composite parent) {
		messageLabel = new Label(parent, SWT.NONE);
		messageLabel.setText(NO_MATCHES_FILTERED);
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.TOP;
		gd.verticalIndent = 1;
		gd.horizontalIndent = 1;
		messageLabel.setLayoutData(gd);
		setMessageVisible(false);
	}

	private void setMessageVisible(boolean show) {
		GridData data = (GridData) messageLabel.getLayoutData();
		data.exclude = !show;
		messageLabel.setVisible(!data.exclude);
		messageLabel.getParent().layout();
	}

	HashMap<ViewerFilter, String> registeredFilters = new HashMap<ViewerFilter, String>();

	public void registerFilter(ViewerFilter filter, String filterName) {
		if (registeredFilters.keySet().contains(filter))
			return;

		registeredFilters.put(filter, filterName);

		activeFilters.add(filter);
		applyFilters();
	}

	public void unregisterFilter(ViewerFilter filter) {
		if (!registeredFilters.keySet().contains(filter))
			return;

		registeredFilters.remove(filter);

		activeFilters.remove(filter);
		applyFilters();
	}

	public abstract StructuredViewer getStructuredViewer();

	private List<ViewerFilter> activeFilters = new ArrayList<ViewerFilter>();

	private void applyFilters() {
		getStructuredViewer().setFilters(
				activeFilters.toArray(new ViewerFilter[] {}));
	}

	Logger logger = Logger.getLogger(this.getClass());

	private void setActiveFilters(List<ViewerFilter> filterList) {
		activeFilters = filterList;
		applyFilters();
	}

	public void filterChanged() {
		getStructuredViewer().refresh();
		updateMessage();
		messageLabel.getParent().redraw();
	}

	public abstract long getUnfilteredItemCount();

	public abstract long getFilteredItemCount();

	private void updateMessage() {
		messageLabel.setText("Filters matched " + getFilteredItemCount()
				+ " of " + getUnfilteredItemCount() + " items");
		if (getFilteredItemCount() == getUnfilteredItemCount())
			setMessageVisible(false);
		else
			setMessageVisible(true);
	}

	private void addFiltersButtonToActionBar() {
		IToolBarManager toolbar = getViewSite().getActionBars()
				.getToolBarManager();
		toolbar.add(new Action("Filters", filtersIcon) {

			@Override
			public void run() {
				FiltersDialog fdialog = new FiltersDialog(GeneralUtils
						.getShell(), registeredFilters, activeFilters);
				int returnCode = fdialog.open();

				if (returnCode == Dialog.OK) {
					setActiveFilters(fdialog.getSelectedFilters());
				}

			}

		});
	}
}
