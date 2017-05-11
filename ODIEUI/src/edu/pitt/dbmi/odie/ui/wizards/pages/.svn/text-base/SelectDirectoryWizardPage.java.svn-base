package edu.pitt.dbmi.odie.ui.wizards.pages;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;

import edu.pitt.dbmi.odie.model.Document;
import edu.pitt.dbmi.odie.ui.workers.FSDocumentSetImporter;

public class SelectDirectoryWizardPage extends FileSystemBrowsePage {

	public static final String PAGE_NAME = "SelectDirectoryWizardPage";
	private static final String SourceLocationEmpty = "Specify the location of the documents to be processed.";
	private static final String InvalidSourceLocation = "Invalid directory";
	private static final String NoFilesInDirectory = "No files found in directory";
	List<Document> documentSet;

	public List<Document> getDocuments() {

		return documentSet;
	}

	public SelectDirectoryWizardPage() {
		this(PAGE_NAME);
		setTitle("Documents");
		setDescription(SourceLocationEmpty);

	}

	public SelectDirectoryWizardPage(String pageName) {
		super(PAGE_NAME);
	}

	protected IPath openBrowseDialog(IPath sourceLocation) {
		DirectoryDialog dialog = new DirectoryDialog(getShell());

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
			setMessage(SelectDirectoryWizardPage.SourceLocationEmpty);
			return false;
		} else if (!isValidPath()) {
			setErrorMessage(SelectDirectoryWizardPage.InvalidSourceLocation);
			return false;
		} else {
			IPath p = getLocation();
			int count = 0;
			if (p == null)
				count = -1;
			else{
				File f = p.toFile();
				count = getFileCount(f);
			}

			if (count < 0) {
				setErrorMessage(SelectDirectoryWizardPage.InvalidSourceLocation);
				return false;
			} else if (count == 0) {
				setErrorMessage(SelectDirectoryWizardPage.NoFilesInDirectory);
				return false;
			} else {
				setErrorMessage(null);
				setMessage(count + " files in "
						+ getLocation().toFile().getAbsolutePath(),
						DialogPage.INFORMATION);

				try {
					documentSet = new ArrayList<Document>();
					getContainer().run(
							true,
							true,
							new FSDocumentSetImporter(getLocation(),
									documentSet,count));
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return true;
			}
		}

	}

	private int getFileCount(File f) {
		if (f == null)
			return 0;

		File[] files = f.listFiles();

		int count = 0;

		for (File f2 : files) {
			if (f2.isFile()) {
				count++;
			}
			else if(f2.isDirectory()){
				count += getFileCount(f2);
			}
		}
		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pitt.dbmi.odie.ui.wizards.FileSystemBrowsePage#getLabelText()
	 */
	@Override
	protected String getLabelText() {
		return "Directory:";
	}

	@Override
	void createAdditionalControls(Composite container) {
		// TODO Auto-generated method stub

	}

}
