package edu.pitt.dbmi.odie.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

import edu.pitt.dbmi.odie.ui.wizards.ImportOntologiesWizard;

public class ImportOntologiesHandler extends AbstractHandler implements
		IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ImportOntologiesWizard wizard = new ImportOntologiesWizard(true);
		WizardDialog dialog = new WizardDialog(Display.getCurrent()
				.getActiveShell(), wizard);

		dialog.open();

		return null;
	}

}
