/**
 * 
 */
package edu.pitt.dbmi.odie.uima;

import java.util.concurrent.TimeUnit;

import javax.swing.Timer;

import org.apache.uima.cas.CAS;
import org.apache.uima.collection.EntityProcessStatus;
import org.apache.uima.collection.StatusCallbackListener;
import org.eclipse.core.runtime.IProgressMonitor;

import edu.pitt.dbmi.odie.model.Analysis;

public class CPMMonitor implements StatusCallbackListener {

	IProgressMonitor monitor;
	private int work;
	int currentIndex = 0;
	long startTime;
	private Timer timer;
	private Analysis analysis;
	String remainingTime;
	String timePerDocument;
	
	@Override
	public void entityProcessComplete(CAS cas, EntityProcessStatus status) {
		if (status.isException()) {
//			monitor.subTask("Error:"
//					+ status.getExceptions().get(0).getMessage());
			monitor.subTask("An error occured.");
			collectionProcessComplete();
			return;
		}
		currentIndex++;
		
		//update time every 10 reports to avoid wide fluctuations
		if(currentIndex < 10 || currentIndex % 5 == 0){
			updateTimeCalculations();
		}
		monitor.setTaskName("Running analysis engine. Time Remaining:" + remainingTime);
		monitor.subTask("Processed " + currentIndex + "/" + work
			+ " documents. " + timePerDocument);
		monitor.worked(1);

	}

	private void updateTimeCalculations() {
		long currentTime = System.currentTimeMillis();
		long tpr = (currentTime - startTime)/currentIndex;
		long rt = tpr*(work-currentIndex);
		
		remainingTime = String.format("%d min. %d sec.", 
			    TimeUnit.MILLISECONDS.toMinutes(rt),
			    TimeUnit.MILLISECONDS.toSeconds(rt) - 
			    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(rt)));
		
		timePerDocument = String.format("%d ms/document",tpr);
	}

	public CPMMonitor(Analysis analysis, IProgressMonitor monitor, int work,
			Timer progressTimer) {
		super();
		this.analysis = analysis;
		this.monitor = monitor;
		this.work = work;
		this.timer = progressTimer;
	}

	@Override
	public void aborted() {
		collectionProcessComplete();
	}

	@Override
	public void batchProcessComplete() {
	}

	@Override
	public void collectionProcessComplete() {
		stopped = true;
		monitor.setTaskName("Loading results...");
//		AnalysisLoader.loadDocuments(analysis, new SubProgressMonitor(monitor,
//				analysis.getAnalysisDocuments().size()));
		monitor.done();
		timer.stop();

		synchronized (analysis) {
			analysis.notify();
		}
	}

	@Override
	public void initializationComplete() {
		startTime = System.currentTimeMillis();
		monitor.beginTask("Processing Documents", work * 2);
		
	}

	@Override
	public void paused() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resumed() {
		// TODO Auto-generated method stub

	}

	boolean stopped = false;

	public boolean isStopped() {
		return stopped;
	}

}