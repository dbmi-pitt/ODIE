package edu.pitt.dbmi.odie.ui.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Display;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.ui.workers.AnalysisDeleter;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class DeleteAnalysisHandler extends AbstractHandler {

	Logger logger = Logger.getLogger(this.getClass());

	public DeleteAnalysisHandler() {
		super();
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		logger.info("deleting analysis");

		List<Analysis> selectedAnalyses = GeneralUtils.getSelectionsInAnalysisView();
		if(selectedAnalyses.isEmpty()){
			GeneralUtils.showInformationDialog("No Analysis selected", "Atleast one analysis must be selected to be removed");
			return null;
		}
		
		
//		Analysis currentAnalysis = GeneralUtils.getFirstSelectionInAnalysisView();

		for(Analysis currentAnalysis:selectedAnalyses){
			if (currentAnalysis == null) {
				logger.error("No Analysis selected in AnalysesView.");
				return null;
			}
			String proposalOntologyMsg = "";
			if (GeneralUtils.isOE(currentAnalysis))
				proposalOntologyMsg = "The '"
						+ currentAnalysis.getProposalLexicalSet().getName()
						+ "' "
						+ "lexical set and ontology will have to be removed separately.\n";
			if (MessageDialog.openQuestion(Display.getCurrent().getActiveShell(),
					"Remove Analysis", "This will remove the '"
							+ currentAnalysis.getName() + "' analysis. \n"
							+ proposalOntologyMsg + "\n Do you want to continue?")) {
	
				ProgressMonitorDialog pd = GeneralUtils.getProgressMonitorDialog();
				AnalysisDeleter ad = new AnalysisDeleter(currentAnalysis);
				try {
					pd.run(true, true, ad);
	
					if (ad.isSuccessful()) {
						GeneralUtils.closeEditor(currentAnalysis);
						GeneralUtils.refreshViews();
					}
	
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

}
