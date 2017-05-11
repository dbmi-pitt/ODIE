/**
 * 
 */
package edu.pitt.dbmi.odie.ui.wizards;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import edu.pitt.dbmi.odie.ui.wizards.pages.SelectLanguageResourcePage;
import edu.pitt.dbmi.odie.ui.workers.ODIEOntologyPackager;

/**
 * @author Girish Chavan
 * 
 */
public class NewOdieOntologyPackageWizard extends Wizard {
	Logger logger = Logger.getLogger(NewOdieOntologyPackageWizard.class);

	SelectLanguageResourcePage selectLRPage;

	public NewOdieOntologyPackageWizard() {
		super();
		setNeedsProgressMonitor(true);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		try {
			ODIEOntologyPackager worker = new ODIEOntologyPackager(selectLRPage.getLanguageResources());
			getContainer().run(true, true, worker);
			if(worker.isSuccessful()){
				MessageDialog.openInformation(Display.getCurrent().getActiveShell(),
						"ODIE Ontology Packaging Success",
						"All selected ontologies have been packaged");
				return true;
			}
			else {
				MessageDialog.openError(Display.getCurrent().getActiveShell(),
						"ODIE Ontology Packaging Failed",
						"Some ontologies did not get packaged");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
					"ODIE Ontology Packaging Failed",
					"An error occured:"	+ e.getMessage());
			return false;
		}

	}

	

	@Override
	public void addPages() {
		setWindowTitle("New ODIE Ontology Package");

		selectLRPage = new SelectLanguageResourcePage();
		addPage(selectLRPage);

	}

	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		pageContainer.getShell().setSize(485, 600);
	}

}
