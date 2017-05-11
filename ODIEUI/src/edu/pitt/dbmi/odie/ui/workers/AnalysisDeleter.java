package edu.pitt.dbmi.odie.ui.workers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.ui.Activator;

public class AnalysisDeleter implements IRunnableWithProgress {
	Analysis analysis;
	private boolean successful;

	public boolean isSuccessful() {
		return successful;
	}

	public AnalysisDeleter(Analysis analysis) {
		this.analysis = analysis;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {

		try {
			monitor.beginTask("Deleting Analysis: ", IProgressMonitor.UNKNOWN);
			deleteAnalysis();

			successful = true;
		} catch (Exception e) {
			successful = false;
			e.printStackTrace();
			monitor.setTaskName("Error Deleting Analysis");
			monitor.done();
		} finally {
			monitor.done();
		}

	}

	private void deleteAnalysis() throws Exception {
		Activator.getDefault().getMiddleTier().dropSchema(
				analysis.getDatabaseName());
		Activator.getDefault().getMiddleTier().delete(analysis);
		Activator.getDefault().resetAnalysisMiddleTier(analysis);
	}
}
