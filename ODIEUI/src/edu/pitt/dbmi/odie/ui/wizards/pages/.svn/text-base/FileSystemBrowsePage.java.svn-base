/**
 * 
 */
package edu.pitt.dbmi.odie.ui.wizards.pages;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Girish Chavan
 * 
 */
public abstract class FileSystemBrowsePage extends WizardPage {

	/**
	 * @param pageName
	 */
	protected FileSystemBrowsePage(String pageName) {
		super(pageName);
	}

	protected Text locationTextField;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		container.setLayout(gridLayout);
		setControl(container);

		final Label label = new Label(container, SWT.NONE);
		label.setText(getLabelText());

		locationTextField = new Text(container, SWT.BORDER);
		locationTextField.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});

		locationTextField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Button button = new Button(container, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				browse();
			}
		});

		button.setText("Browse...");

		GridData gd = new GridData();
		gd = new GridData();
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalAlignment = GridData.FILL;
		gd.horizontalSpan = 3;
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = true;
		Composite emptySpaceContainer = new Composite(container, SWT.NULL);
		emptySpaceContainer.setLayoutData(gd);

		createAdditionalControls(emptySpaceContainer);

		// locationTextField.setText("C:\\DUMMY");
		setPageComplete(validatePage());

	}

	abstract void createAdditionalControls(Composite container);

	/**
	 * @return
	 */
	protected abstract String getLabelText();

	IPath getLocation() {
		String text = locationTextField.getText().trim();
		if (text.length() == 0)
			return null;

		IPath path = new Path(text);
		return path.makeAbsolute();
	}

	protected void browse() {
		IPath path = openBrowseDialog(getLocation());
		locationTextField.setText(path.toOSString());

	}

	protected abstract IPath openBrowseDialog(IPath sourceLocation);

	protected abstract boolean validatePage();

	protected boolean isValidPath() {
		return getFile().exists();
	}

	public File getFile() {
		File f = new File(locationTextField.getText());
		return f;
	}
}
