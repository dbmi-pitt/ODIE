package edu.pitt.dbmi.odie.ui.handlers;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.utils.GeneralUtils;
import edu.pitt.ontology.IOntology;

public class DeleteOntologyHandler extends AbstractHandler {

	Logger logger = Logger.getLogger(this.getClass());

	public DeleteOntologyHandler() {
		super();
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		logger.info("deleting ontology");

		IOntology ontology = GeneralUtils.getFirstSelectionInOntologiesView();

		if (ontology == null) {
			logger.error("No ontology selected in OntologiesView.");
			return null;
		}

		if (MessageDialog.openQuestion(Display.getCurrent().getActiveShell(),
				"Remove Ontology", "This will remove the '"
						+ ontology.getName() + "' ontology. \n" +  "\n Do you want to continue?")) {
			GeneralUtils.closeEditor(ontology);
			MiddleTier mt = Activator.getDefault().getMiddleTier();
			mt.removeOntology(ontology);
			GeneralUtils.refreshViews();
		}
		return null;

	}

}
