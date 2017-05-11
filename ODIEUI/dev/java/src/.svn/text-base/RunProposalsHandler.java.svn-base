package edu.pitt.dbmi.odie.ui.handlers;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.ui.PlatformUI;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.ui.workers.AnalysisProcessor;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class RunProposalsHandler extends AbstractHandler implements IHandler {
	Logger logger = Logger.getLogger(this.getClass());
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
    	Analysis currentAnalysis = GeneralUtils.getCurrentAnalysis();
		
    	if(currentAnalysis==null){
    		logger.error("No Analysis selected in AnalysesView.");
    		return null;
    	}
    	
		ProgressMonitorDialog pd = new ProgressMonitorDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
		try {
			pd.run(true, true, new AnalysisProcessor(currentAnalysis, true));
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		GeneralUtils.refreshViews();
		
		return null;
	}
}
