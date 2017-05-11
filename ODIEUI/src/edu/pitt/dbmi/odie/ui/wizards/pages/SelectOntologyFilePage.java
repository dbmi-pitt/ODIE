package edu.pitt.dbmi.odie.ui.wizards.pages;

import java.net.URI;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;

import edu.pitt.dbmi.odie.ODIEConstants;

public class SelectOntologyFilePage extends FileSystemBrowsePage {

	public static final String PAGE_NAME = "SelectOntologyFilePage";
	private static final String SourceLocationEmpty = "Select an .OWL,.PPRJ or .OBO file to import";
	private static final String InvalidSourceLocation = "File does not exist.";

	public static final String OBO_NAME = "OBO Format(*.obo)";
	public static final String PPRJ_NAME = "Protege Project Format(*.pprj)";
	public static final String OWL_NAME = "OWL Format(*.owl)";

	public static final String OBO_EXTENSION = "*."
			+ ODIEConstants.EXTENSION_OBO_FILE;
	public static final String OWL_EXTENSION = "*."
			+ ODIEConstants.EXTENSION_OWL_FILE;
	public static final String PPRJ_EXTENSION = "*."
		+ ODIEConstants.EXTENSION_PPRJ_FILE;
	
	public SelectOntologyFilePage() {
		super(PAGE_NAME);
		setTitle("Import Ontology");
		setDescription(SourceLocationEmpty);

	}

	protected IPath openBrowseDialog(IPath sourceLocation) {
		FileDialog dialog = new FileDialog(getShell());
		dialog.setFilterNames(new String[] { OWL_NAME, PPRJ_NAME, OBO_NAME });
		dialog
				.setFilterExtensions(new String[] { OWL_EXTENSION, PPRJ_EXTENSION, OBO_EXTENSION});

		if (sourceLocation != null)
			dialog.setFilterPath(sourceLocation.toOSString());

		String result = dialog.open();

		if (result == null)
			return null;

		return new Path(result);
	}

	protected boolean validatePage() {
		if (locationTextField == null)
			return false;
		if (locationTextField.getText().trim().length() == 0) {
			setErrorMessage(null);
			setMessage(SelectOntologyFilePage.SourceLocationEmpty);
			return false;
		} else if (!isValidPath()) {
			setErrorMessage(SelectOntologyFilePage.InvalidSourceLocation);
			return false;
		} else {
			setErrorMessage(null);
			setMessage(null, DialogPage.INFORMATION);

			return true;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pitt.dbmi.odie.ui.wizards.FileSystemBrowsePage#getLabelText()
	 */
	@Override
	protected String getLabelText() {
		// TODO Auto-generated method stub
		return "From file:";
	}

	/**
	 * @return
	 */
	public URI getSelectedFileURI() {
		return getLocation().toFile().toURI();
	}

	@Override
	void createAdditionalControls(Composite container) {
		// TODO Auto-generated method stub

	}

}
