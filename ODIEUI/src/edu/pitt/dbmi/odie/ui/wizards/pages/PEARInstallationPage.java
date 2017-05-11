package edu.pitt.dbmi.odie.ui.wizards.pages;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.jar.JarFile;

import org.apache.uima.pear.tools.InstallationController;
import org.apache.uima.pear.tools.InstallationDescriptor;
import org.apache.uima.pear.tools.InstallationDescriptorHandler;
import org.apache.uima.pear.util.MessageRouter.StdChannelListener;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.xml.sax.SAXException;

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class PEARInstallationPage extends WizardPage {

	private static final String NAME_HEADER = "Main Component Name: ";
	public static final String ERROR_HEADER = "Error: ";
	private static final String ID_HEADER = "Main Component ID: ";

	public static final String PAGE_NAME = "PEARInstallationPage";
	private static final String PEARLocationEmpty = "Select the PEAR file and an installation location";
	private static final String InstallLocationEmpty = "Select an installation directory";
	private static final String InvalidLocation = "File/Folder does not exist.";

	public static final String DESC_NAME = "PEAR file(*."
			+ ODIEConstants.EXTENSION_UIMA_PEAR_FILE + ")";

	public static final String DESC_EXTENSION = "*."
			+ ODIEConstants.EXTENSION_UIMA_PEAR_FILE;
	private static final String PEARLoadError = "Failed to load PEAR File";
	private static final String Success = "PEAR file valid and ready to install.";

	private StyledText descTextBox;
	private InstallationDescriptor instDescriptor;

	private StyledText consoleTextBox;

	public PEARInstallationPage() {
		super(PAGE_NAME);
		setTitle("Select PEAR File");
		setDescription(PEARLocationEmpty);
	}

	protected IPath openBrowseDialog(IPath sourceLocation, boolean pearFileInput) {
		String result = null;
		if (pearFileInput) {
			FileDialog dialog = new FileDialog(getShell());

			dialog.setFilterNames(new String[] { DESC_NAME });
			dialog.setFilterExtensions(new String[] { DESC_EXTENSION });

			if (sourceLocation != null)
				dialog.setFilterPath(sourceLocation.toOSString());

			result = dialog.open();

		} else {
			DirectoryDialog dialog = new DirectoryDialog(getShell());

			if (sourceLocation != null)
				dialog.setFilterPath(sourceLocation.toOSString());

			result = dialog.open();

		}
		if (result == null)
			return null;

		return new Path(result);
	}

	protected boolean validatePage() {
		if (pearTextField.getText().trim().length() == 0) {
			setErrorMessage(null);
			setMessage(PEARInstallationPage.PEARLocationEmpty);
			return false;
		}
		boolean status;

		try {
			status = loadPEAR();
			updateDescTextAfterPEARLoadAttempt();
		} catch (Exception e) {
			e.printStackTrace();
			setErrorMessage(e.getMessage());
			return false;
		}

		if (!status) {
			setErrorMessage(PEARInstallationPage.PEARLoadError);
			return false;
		} else if (installTextField.getText().trim().length() == 0) {
			setErrorMessage(null);
			setMessage(PEARInstallationPage.InstallLocationEmpty);
			return false;
		} else if (!isValidPath()) {
			setErrorMessage(PEARInstallationPage.InvalidLocation);
			return false;
		}

		setErrorMessage(null);
		setMessage(PEARInstallationPage.Success, DialogPage.INFORMATION);
		return true;

	}

	private void updateDescTextAfterPEARLoadAttempt() {
		if (instDescriptor == null) {
			descTextBox.setText(PEARLoadError);
			return;
		}

		StringBuffer text = new StringBuffer();

		text.append(ID_HEADER + instDescriptor.getMainComponentId());
		text.append("\n");
		text.append(NAME_HEADER + instDescriptor.getMainComponentName());
		text.append("\n");

		setDescriptionText(text.toString());

	}

	private boolean loadPEAR() throws InvocationTargetException,
			InterruptedException, IOException, SAXException {
		File localPearFile = getPEARFile();

		InstallationController.setLocalMode(true);
		InstallationDescriptorHandler installationDescriptorHandler = new InstallationDescriptorHandler();

		JarFile jarFile = new JarFile(localPearFile);
		installationDescriptorHandler.parseInstallationDescriptor(jarFile);
		instDescriptor = installationDescriptorHandler
				.getInstallationDescriptor();

		// this version does not support separate delegate components
		// a UIMA PEAR Installer limitation might change in future versions
		return (instDescriptor.getDelegateComponents().size() <= 0);
	}

	private void applyStylesToText() {
		String s = descTextBox.getText();

		int start = s.indexOf(NAME_HEADER);
		StyleRange sr;
		if (start >= 0) {
			sr = new StyleRange();
			sr.start = start;
			sr.length = NAME_HEADER.length();
			sr.fontStyle = SWT.BOLD;
			descTextBox.setStyleRange(sr);
		}

		start = s.indexOf(ID_HEADER);
		if (start >= 0) {
			sr = new StyleRange();
			sr.start = start;
			sr.length = ID_HEADER.length();
			sr.fontStyle = SWT.BOLD;
			descTextBox.setStyleRange(sr);
		}

		start = s.indexOf(ERROR_HEADER);
		if (start >= 0) {
			sr = new StyleRange();
			sr.start = start;
			sr.length = ID_HEADER.length();
			sr.fontStyle = SWT.BOLD;
			sr.foreground = Aesthetics.getErrorColor();
			descTextBox.setStyleRange(sr);
		}
	}

	public String getMainComponentId() {
		return instDescriptor.getMainComponentId();
	}

	Text pearTextField;
	Text installTextField;

	private Label descLabel;
	private Label consoleLabel;

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		container.setLayout(gridLayout);
		setControl(container);

		addPEARFileInputWidgets(container);

		addDescriptionWidgets(container);

		addInstallLocationInputWidgets(container);

		addConsoleWidgets(container);

		setPageComplete(validatePage());

	}

	private void addConsoleWidgets(Composite container) {
		consoleLabel = new Label(container, SWT.NONE);
		consoleLabel.setText("Installation Progress:");

		consoleTextBox = new StyledText(container, SWT.WRAP | SWT.READ_ONLY
				| SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);

		GridData gd = new GridData();
		gd = new GridData();
		gd.horizontalSpan = 3;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		consoleLabel.setLayoutData(gd);

		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 3;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		consoleTextBox.setLayoutData(gd);

		consoleLabel.setVisible(false);
		consoleTextBox.setVisible(false);
	}

	public void appendToConsole(String s) {
		String prevText = consoleTextBox.getText();
		String newText = prevText + s;

		consoleTextBox.setText(newText);
		consoleTextBox.setTopIndex(consoleTextBox.getLineCount() - 3);
	}

	public void setDescriptionText(String s) {
		descTextBox.setText(s);
		applyStylesToText();
	}

	private void addDescriptionWidgets(Composite container) {
		descLabel = new Label(container, SWT.NONE);
		descLabel.setText("Description:");

		descTextBox = new StyledText(container, SWT.WRAP | SWT.READ_ONLY
				| SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);

		GridData gd = new GridData();
		gd = new GridData();
		gd.horizontalSpan = 3;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		descLabel.setLayoutData(gd);

		gd = new GridData();
		gd.horizontalSpan = 3;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.heightHint = 100;
		descTextBox.setLayoutData(gd);
		descTextBox.setText("No PEAR file selected.");
	}

	private void addPEARFileInputWidgets(Composite container) {
		final Label label = new Label(container, SWT.NONE);
		label.setText("PEAR file:");

		pearTextField = new Text(container, SWT.BORDER);
		pearTextField.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});

		pearTextField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Button button = new Button(container, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				browse(true);
			}
		});

		button.setText("Browse...");
	}

	private void addInstallLocationInputWidgets(Composite container) {
		final Label label = new Label(container, SWT.NONE);
		label.setText("Install at:");

		installTextField = new Text(container, SWT.BORDER);

		installTextField.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});

		final Button button = new Button(container, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				browse(false);
			}
		});

		button.setText("Browse...");

		GridData gd = new GridData();
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.widthHint = 300;
		gd.verticalIndent = 10;
		installTextField.setLayoutData(gd);

		gd = new GridData();
		gd.verticalIndent = 10;
		label.setLayoutData(gd);

		gd = new GridData();
		gd.verticalIndent = 10;
		button.setLayoutData(gd);

		installTextField.setText(GeneralUtils.getAEInstallationDir()
				.getAbsolutePath());
	}

	protected void browse(boolean pearFileInput) {
		Text textField = null;

		if (pearFileInput) {
			textField = pearTextField;
		} else
			textField = installTextField;

		IPath path = openBrowseDialog(getLocation(textField), pearFileInput);
		textField.setText(path.toOSString());

	}

	IPath getLocation(Text textField) {
		String text = textField.getText().trim();
		if (text.length() == 0)
			return null;

		IPath path = new Path(text);
		return path.makeAbsolute();
	}

	protected boolean isValidPath() {
		return getInstallationDir().exists() && getPEARFile().exists();
	}

	public File getPEARFile() {
		File f = new File(pearTextField.getText());
		return f;
	}

	public File getInstallationDir() {
		File f = new File(installTextField.getText());
		return f;
	}

	public void showConsole() {
		consoleLabel.setVisible(true);
		consoleTextBox.setVisible(true);

	}
}
