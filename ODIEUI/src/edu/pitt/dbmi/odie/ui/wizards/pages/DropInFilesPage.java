package edu.pitt.dbmi.odie.ui.wizards.pages;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.dbmi.odie.ui.views.ODIEMessages;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class DropInFilesPage extends WizardPage {

	public static final String PAGE_NAME = "DropInFilesPage";
	private static final String initDesc = "Select files to be installed";

	public static final int COLUMN_NAME_INDEX = 0;
	public static final int COLUMN_TYPE_INDEX = 1;
	public static final int COLUMN_MDATE_INDEX = 2;
	private List<File> newFiles;

	private CheckboxTableViewer tableViewer;

	public DropInFilesPage(List<File> newFiles) {
		super(PAGE_NAME);
		setTitle("New dropins found...");
		setDescription(initDesc);
		this.newFiles = newFiles;

	}

	protected boolean validatePage() {

		if (tableViewer.getCheckedElements().length <= 0) {
			setMessage("Select at least one file to install");
			return false;
		}

		setMessage(tableViewer.getCheckedElements().length
				+ " file(s) selected for installation");
		return true;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);

		GridLayout gl = new GridLayout(2, false);
		gl.marginWidth = Aesthetics.LEFT_MARGIN * 2;
		gl.marginHeight = Aesthetics.TOP_MARGIN * 2;

		container.setLayout(gl);

		// Label instructionsLabel = new Label(container,SWT.NONE);
		// instructionsLabel.setText("Select the filters that you want to use:");
		GridData gd = new GridData();
		// gd.horizontalSpan = 2;
		// instructionsLabel.setLayoutData(gd);

		createTable(container);
		gd = new GridData();
		gd.heightHint = 150;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		tableViewer.getTable().setLayoutData(gd);

		Composite buttonsComposite = new Composite(container, SWT.NONE);
		buttonsComposite.setLayout(new GridLayout(1, false));
		gd = new GridData();
		gd.verticalAlignment = SWT.TOP;
		gd.verticalIndent = 0;
		buttonsComposite.setLayoutData(gd);

		createSelectButtons(buttonsComposite);

		tableViewer.setInput(new Object());
		tableViewer.setAllChecked(true);

		hookListeners();

		setPageComplete(validatePage());

		setControl(container);
	}

	private String columnHeaders[] = { ODIEMessages.headerFilename,
			ODIEMessages.headerType, ODIEMessages.headerModifiedDate };

	private ColumnLayoutData columnLayouts[] = { new ColumnWeightData(75),
			new ColumnWeightData(30),new ColumnWeightData(45)};

	void createColumns(Table table) {
		TableLayout layout = new TableLayout();
		table.setLayout(layout);
		table.setHeaderVisible(true);

		for (int i = 0; i < columnHeaders.length; i++) {
			TableColumn tc = new TableColumn(table, SWT.NONE, i);
			tc.setText(columnHeaders[i]);
			tc.setResizable(columnLayouts[i].resizable);
			layout.addColumnData(columnLayouts[i]);
		}
	}

	private void createTable(Composite parent) {
		Table table = new Table(parent, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		createColumns(table);

		tableViewer = new CheckboxTableViewer(table);

		tableViewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public Object[] getElements(Object inputElement) {
				return newFiles.toArray();
			}

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}

		});

		tableViewer.setLabelProvider(new ITableLabelProvider() {

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

			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getColumnText(Object element, int columnIndex) {
				if (element instanceof File) {
					switch (columnIndex) {
					case COLUMN_NAME_INDEX:
						return ((File) element).getName();
					case COLUMN_TYPE_INDEX:
						return GeneralUtils.getTypeForFile((File) element);
					case COLUMN_MDATE_INDEX:
						SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
						return df.format(new Date(((File) element).lastModified()));
					default:
						return "Invalid column";
					}
				} else
					return "Error";
			}

		});
	}

	private void createSelectButtons(Composite buttonsComposite) {
		Button button = new Button(buttonsComposite, SWT.PUSH);
		button.setText("Select All");
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setAllChecked(true);
				setPageComplete(validatePage());
			}
		});

		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.TOP;
		button.setLayoutData(gd);

		button = new Button(buttonsComposite, SWT.PUSH);
		button.setText("Deselect All");
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setAllChecked(false);
				setPageComplete(validatePage());
			}
		});

		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.TOP;
		button.setLayoutData(gd);
	}

	/**
	 * 
	 */
	private void hookListeners() {
		tableViewer.addCheckStateListener(new ICheckStateListener() {

			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				setPageComplete(validatePage());
			}

		});
	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell parent = new Shell(display);
		parent.setLayout(new FillLayout());

		Wizard wiz = new Wizard() {

			@Override
			public void addPages() {
				setWindowTitle("Wizard Test");

				DropInFilesPage propPage = new DropInFilesPage(
						new ArrayList<File>());
				addPage(propPage);
			}

			@Override
			public boolean performFinish() {
				// TODO Auto-generated method stub
				return false;
			}

		};

		WizardDialog d = new WizardDialog(parent, wiz);
		d.open();
		while (!parent.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();

		display.dispose();
	}

	public List<File> getSelectedFiles() {
		Object[] objArr = tableViewer.getCheckedElements();
		List<File> outlist = new ArrayList<File>();
		for (Object o : objArr) {
			outlist.add((File) o);
		}
		return outlist;
	}
}
