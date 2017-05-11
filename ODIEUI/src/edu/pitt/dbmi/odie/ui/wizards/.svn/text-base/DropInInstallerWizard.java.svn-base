package edu.pitt.dbmi.odie.ui.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Monitor;

import edu.pitt.dbmi.odie.ui.wizards.pages.DropInFilesPage;
import edu.pitt.dbmi.odie.ui.workers.DropinInstaller;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class DropInInstallerWizard extends Wizard {

	List<File> newFiles;
	DropInFilesPage dropinFilesPage;

	public DropInInstallerWizard(List<File> newFiles) {
		super();
		this.newFiles = newFiles;
		setNeedsProgressMonitor(true);
	}

	@Override
	public boolean performFinish() {
		try {
			final DropinInstaller di = new DropinInstaller(dropinFilesPage
					.getSelectedFiles());
			getContainer().run(true, true, di);

			if (di.wasSuccessful()) {
				GeneralUtils.refreshOntologiesView();
				MessageDialog.openInformation(GeneralUtils.getShell(),
						"Installation Successful",
						"All selected files were installed successfully");
			} else {
				GeneralUtils.showScrolledErrorDialog("Installation Error", di
						.getErrorMessage());
				return false;
			}
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	@Override
	public void addPages() {
		setWindowTitle("Install Dropins");

		dropinFilesPage = new DropInFilesPage(newFiles);
		addPage(dropinFilesPage);

	}
	
	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		pageContainer.getShell().setSize(485, 400);
		Monitor primary = pageContainer.getShell().getMonitor();
	    Rectangle bounds = primary.getBounds ();
	    Rectangle rect = pageContainer.getShell().getBounds ();
	    int x = bounds.x + (bounds.width - rect.width) / 2;
	    int y = bounds.y + (bounds.height - rect.height) / 2;
	    pageContainer.getShell().setLocation (x, y);
	}
}
