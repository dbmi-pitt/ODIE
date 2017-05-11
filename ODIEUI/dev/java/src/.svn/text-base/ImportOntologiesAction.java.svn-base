package edu.pitt.dbmi.odie.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;

import edu.pitt.dbmi.odie.ui.wizards.ImportOntologiesWizard;
@Deprecated 
public class ImportOntologiesAction extends Action {

	IWorkbenchWindow window;
	public ImportOntologiesAction(IWorkbenchWindow window) {
		super("&Import Ontologies");
		this.window = window;
	}
	
	@Override
	public void run() {
		ImportOntologiesWizard wizard = new ImportOntologiesWizard();
		 WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
		 
		 dialog.open();
	}
}
