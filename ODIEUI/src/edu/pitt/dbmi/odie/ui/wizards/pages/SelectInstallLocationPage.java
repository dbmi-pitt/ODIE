package edu.pitt.dbmi.odie.ui.wizards.pages;

public class SelectInstallLocationPage extends SelectDirectoryWizardPage {

	public static final String PAGE_NAME = "SelectInstallLocationPage";
	private static final String SourceLocationEmpty = "Specify the installation location";
	private static final String InvalidSourceLocation = "Invalid directory";

	public SelectInstallLocationPage() {
		super(PAGE_NAME);
		setTitle("Installation Location");
		setDescription(SourceLocationEmpty);
	}

	protected boolean validatePage() {
		if (locationTextField == null)
			return false;
		if (locationTextField.getText().trim().length() == 0) {
			setErrorMessage(null);
			setMessage(SelectInstallLocationPage.SourceLocationEmpty);
			return false;
		} else if (!isValidPath()) {
			setErrorMessage(SelectInstallLocationPage.InvalidSourceLocation);
			return false;
		} else {
			setErrorMessage(null);
			setMessage(null);

			return true;
		}
	}

}
