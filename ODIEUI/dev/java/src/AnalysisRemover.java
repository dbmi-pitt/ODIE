package edu.pitt.dbmi.odie.ui.workers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.ui.Activator;

public class AnalysisRemover implements IRunnableWithProgress {


	Analysis analysis;
	
	public AnalysisRemover(Analysis analysis) {
		this.analysis = analysis;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		
		monitor.beginTask("Removing Analysis...", IProgressMonitor.UNKNOWN);
		Activator.getDefault().getMiddleTier().removeAnalysis(analysis);
		monitor.done();
	}
}
