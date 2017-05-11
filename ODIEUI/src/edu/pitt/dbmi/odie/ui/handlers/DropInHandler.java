package edu.pitt.dbmi.odie.ui.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.dbmi.odie.model.Dropin;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.ui.wizards.DropInInstallerWizard;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class DropInHandler extends AbstractHandler implements IHandler {

	private List<File> newFiles;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (newDropInsDetected()) {
			DropInInstallerWizard wizard = new DropInInstallerWizard(newFiles);
			WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench()
					.getDisplay().getActiveShell(), wizard);
			dialog.open();
		}
		return null;
	}

	public void executeIfRequired() throws ExecutionException {
		if (isEnabled())
			execute(null);
	}

	private boolean newDropInsDetected() {
		List<File> dropinFiles = getFilesInDropinDirectory();
		newFiles = getNewFiles(dropinFiles);

		return newFiles != null && newFiles.size() > 0;
	}

	private List<File> getFilesInDropinDirectory() {
		File f = GeneralUtils.getDropinDir();

		if (f.exists() && f.isDirectory()) {
			return Arrays.asList(f.listFiles());
		}

		return null;
	}

	private List<File> getNewFiles(List<File> dropinFiles) {
		if (Activator.getDefault().getMiddleTier() == null)
			return null;

		List<Dropin> dropInList = Activator.getDefault().getMiddleTier()
				.getInstalledDropIns();

		List<File> newFiles = new ArrayList<File>();
		for (File f : dropinFiles) {
			boolean alreadyInstalled = false;
			for (Dropin d : dropInList) {
				if (f.getName().equals(d.getFilenname())) {
					alreadyInstalled = true;
					if (d.getLastModified() == null
							|| d.getLastModified() < f.lastModified()) {
						newFiles.add(f);
					}
					break;
				}
			}
			if (!alreadyInstalled && (GeneralUtils.getTypeForFile(f) != ODIEConstants.FILETYPE_UNKNOWN))
				newFiles.add(f);
		}
		return newFiles;
	}

	@Override
	public boolean isEnabled() {
		return newDropInsDetected();
	}

}
