package edu.pitt.dbmi.odie.ui.workers;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.ui.PlatformUI;

import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.ui.Activator;

public class AnalysisReprocessor extends AnalysisProcessor {

	Logger logger = Logger.getLogger(this.getClass());

	public AnalysisReprocessor(Analysis analysis) {
		super(analysis);
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {

		int jobCount = 1; // update document status
		jobCount++; // save analysis
		jobCount += 2; // load analysis engine
		jobCount++; // clear analysis space
		jobCount += 6; // run analysis

		monitor.beginTask("Clearing old results...", jobCount);
		clearAnalysisSpace();
		monitor.worked(1);

		monitor.setTaskName("Configuring analysis engine...");

		// Currently we don't have a way to run an analysis engine twice.
		// it needs to be recreated from the start so we
		// set this to null. In the future we want the ability to change the
		// config params
		// on the fly and reload the analysis engine with those params.
		analysis.getAnalysisEngineMetadata().setAnalysisEngine(null);
		if (!loadAnalysisEngine(new SubProgressMonitor(monitor, 2))) {
			monitor.done();
			return;
		}
		monitor.worked(1);

		if (monitor.isCanceled())
			return;

		monitor.setTaskName("Preparing to start analysis...");
		resetAnalysis(monitor);

		if (monitor.isCanceled())
			return;

		runAnalysis(new SubProgressMonitor(monitor, 6));

		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				closeDocumentEditors();
				refreshViews();
			}
		});
	}

	private void clearAnalysisSpace() {
		MiddleTier mt = Activator.getDefault().getMiddleTier();
		mt.dropSchema(analysis.getDatabaseName());
		mt.createSchema(analysis.getDatabaseName());
		Activator.getDefault().resetAnalysisMiddleTier(analysis);
	}
}
