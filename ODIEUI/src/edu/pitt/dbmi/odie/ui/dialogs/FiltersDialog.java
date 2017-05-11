package edu.pitt.dbmi.odie.ui.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import edu.pitt.dbmi.odie.ui.Aesthetics;

public class FiltersDialog extends Dialog {

	private HashMap<ViewerFilter, String> registeredFilters;
	private List<ViewerFilter> activeFilters;
	private CheckboxTableViewer filtersTableViewer;

	private List<ViewerFilter> selectedFilters = new ArrayList<ViewerFilter>();

	public FiltersDialog(Shell shell,
			HashMap<ViewerFilter, String> registeredFilters,
			List<ViewerFilter> activeFilters) {
		super(shell);

		this.registeredFilters = registeredFilters;
		this.activeFilters = activeFilters;
		this.selectedFilters.addAll(activeFilters);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Filters");
	}

	public List<ViewerFilter> getSelectedFilters() {
		return selectedFilters;
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite container = (Composite) super.createDialogArea(parent);

		GridLayout gl = new GridLayout(2, false);
		gl.marginWidth = Aesthetics.LEFT_MARGIN * 2;
		gl.marginHeight = Aesthetics.TOP_MARGIN * 2;

		container.setLayout(gl);

		Label instructionsLabel = new Label(container, SWT.NONE);
		instructionsLabel.setText("Select the filters that you want to use:");
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		instructionsLabel.setLayoutData(gd);

		createFiltersTable(container);
		gd = new GridData();
		gd.heightHint = 150;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		filtersTableViewer.getTable().setLayoutData(gd);

		Composite buttonsComposite = new Composite(container, SWT.NONE);
		buttonsComposite.setLayout(new GridLayout(1, false));
		gd = new GridData();
		gd.verticalAlignment = SWT.TOP;
		buttonsComposite.setLayoutData(gd);

		createSelectButtons(buttonsComposite);

		filtersTableViewer.setInput(new Object());

		checkCurrentActiveFilters();
		return container;
	}

	private void createSelectButtons(Composite buttonsComposite) {
		Button button = new Button(buttonsComposite, SWT.PUSH);
		button.setText("Use All");
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				filtersTableViewer.setAllChecked(true);
			}
		});

		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.TOP;
		button.setLayoutData(gd);

		button = new Button(buttonsComposite, SWT.PUSH);
		button.setText("Use None");
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				filtersTableViewer.setAllChecked(false);
			}
		});

		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.TOP;
		button.setLayoutData(gd);
	}

	private void checkCurrentActiveFilters() {
		for (ViewerFilter filter : activeFilters)
			filtersTableViewer.setChecked(filter, true);
	}

	private void createFiltersTable(Composite parent) {
		filtersTableViewer = CheckboxTableViewer.newCheckList(parent,
				SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		filtersTableViewer.getTable().setLayoutData(
				new GridData(GridData.FILL_BOTH));
		filtersTableViewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public Object[] getElements(Object inputElement) {
				return registeredFilters.keySet().toArray();
			}

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}

		});

		filtersTableViewer.setLabelProvider(new ILabelProvider() {

			@Override
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getText(Object element) {
				if (element instanceof ViewerFilter)
					return registeredFilters.get(element);
				else
					return "Error";
			}

			@Override
			public void addListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub

			}

			@Override
			public void dispose() {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean isLabelProperty(Object element, String property) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void removeListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub

			}

		});

		/*
		 * After the dialog gets closed we can no longer access
		 * filtersTableViewer to get the list of selected filters. Hence we need
		 * to maintain a list of selected filters as and when it is updated.
		 */

		filtersTableViewer.addCheckStateListener(new ICheckStateListener() {

			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getChecked())
					selectedFilters.add((ViewerFilter) event.getElement());
				else
					selectedFilters.remove((ViewerFilter) event.getElement());
			}

		});
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, true);
	}
}
