package edu.pitt.dbmi.odie.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;

import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.ui.wizards.NewAnalysisWizard;
@Deprecated 
public class NewAnalysisTaskAction extends Action {

	
	private IWorkbenchWindow window;

	public NewAnalysisTaskAction(IWorkbenchWindow window) {
		super("Analysis");
		ImageDescriptor analysisImage = Activator.getImageDescriptor("icons/108-graph2.png");
		this.setImageDescriptor(analysisImage);
		this.window = window;
	}

	@Override
	public void run() {
		 NewAnalysisWizard wizard = new NewAnalysisWizard();
		 WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
		 dialog.open();

	}


}
