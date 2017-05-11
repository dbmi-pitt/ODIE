package edu.pitt.dbmi.odie.ui.wizards.pages;

import java.util.List;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.ui.Aesthetics;

public class AnalysisPropertiesPage extends WizardPage {

	public static final String PAGE_NAME = "AnalysisPropertiesPage";
	private static final String initDesc = "Enter a name and option description for the analysis";
	private static final String nameEmpty = "Enter a name for the new analysis";
	private static final String nameInUse = "An analysis with that name already exists. Please enter another name.";
	private static final String DEFAULT_DESCRIPTION_TEXT = "Enter an optional description for this analysis...";
	private static final String ALL_OK_DESC = "Click Finish to create the analysis.";
	Text nameField;
//	Text descriptionField;
	private List<String> existingNames;

	public AnalysisPropertiesPage() {
		super(PAGE_NAME);
		setTitle("Name the analysis");
		setDescription(initDesc);

		loadExistingAnalysisNames();

	}

	private void loadExistingAnalysisNames() {
		existingNames = Activator.getDefault().getMiddleTier()
				.getAnalysisNames();

	}

	protected boolean validatePage() {
		if (nameField == null)
			return false;
		if (nameField.getText().trim().length() == 0) {
			setErrorMessage(null);
			setMessage(AnalysisPropertiesPage.nameEmpty);
			return false;
		} else if (existingNames.contains(nameField.getText())) {
			setErrorMessage(AnalysisPropertiesPage.nameInUse);
			return false;
		} else {
			setErrorMessage(null);
			setMessage(ALL_OK_DESC, DialogPage.INFORMATION);

			return true;
		}

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

		GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = Aesthetics.INTERGROUP_SPACING;
		gridLayout.numColumns = 2;
		gridLayout.verticalSpacing = 2;
		container.setLayout(gridLayout);
		setControl(container);

		Label label = new Label(container, SWT.NONE);
		label.setText("Analysis name:");

		nameField = new Text(container, SWT.BORDER);
		nameField.setTextLimit(ODIEConstants.MAX_DB_NAME_LENGTH
				- ODIEConstants.ANALYSIS_SPACE_PREFIX.length());
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		nameField.setLayoutData(gd);

//		label = new Label(container, SWT.NONE);
//		label.setText("Description:");
//		gd = new GridData();
//		gd.horizontalSpan = 2;
//		gd.horizontalAlignment = GridData.BEGINNING;
//		gd.verticalIndent = 2;
//		label.setLayoutData(gd);
//
//		descriptionField = new Text(container, SWT.MULTI | SWT.WRAP
//				| SWT.BORDER | SWT.V_SCROLL);
//		gd = new GridData();
//		gd.grabExcessHorizontalSpace = true;
//		gd.horizontalSpan = 2;
//		gd.horizontalAlignment = GridData.FILL;
//		gd.heightHint = 100;
//		descriptionField.setLayoutData(gd);

		hookListeners();
		setPageComplete(validatePage());
	}

	/**
	 * 
	 */
	private void hookListeners() {
		nameField.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});
		nameField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				nameField.selectAll();
			}

		});

		nameField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				setPageComplete(validatePage());
			}
		});

	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell parent = new Shell(display);
		parent.setLayout(new FillLayout());

		AnalysisPropertiesPage p = new AnalysisPropertiesPage();

		Wizard wiz = new Wizard() {

			@Override
			public void addPages() {
				setWindowTitle("New Document Coding Analysis");

				AnalysisPropertiesPage propPage = new AnalysisPropertiesPage();
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

	// public boolean isDetectConcepts() {
	// return detectConcepts;
	// }
	//
	// public void setDetectConcepts(boolean detectConcepts) {
	// this.detectConcepts = detectConcepts;
	// }
	//
	// public boolean isFillModel() {
	// return fillModel;
	// }
	//
	// public void setFillModel(boolean fillModel) {
	// this.fillModel = fillModel;
	// }

	/**
	 * @return
	 */
	public String getName() {
		return nameField.getText();
	}

	public String getDescription() {
		return null;
//		return descriptionField.getText();
	}

}
