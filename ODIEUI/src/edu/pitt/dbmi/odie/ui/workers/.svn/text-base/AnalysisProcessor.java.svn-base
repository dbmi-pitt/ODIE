package edu.pitt.dbmi.odie.ui.workers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import org.apache.log4j.Logger;
import org.apache.uima.UIMAFramework;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionProcessingManager;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.ConfigurationParameterSettings;
import org.apache.uima.util.InvalidXMLException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;

import edu.pitt.dbmi.odie.ODIEException;
import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.model.Datapoint;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.uima.CPMMonitor;
import edu.pitt.dbmi.odie.uima.collection.AnalysisCasConsumer;
import edu.pitt.dbmi.odie.uima.collection.AnalysisCollectionReader;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class AnalysisProcessor implements IRunnableWithProgress {
	Analysis analysis;
	private CAS cas = null;

	Logger logger = Logger.getLogger(this.getClass());

	public AnalysisProcessor(Analysis analysis) {
		this.analysis = analysis;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		MiddleTier mt = Activator.getDefault().getMiddleTier();
		
		int noOfDocs = mt.getAnalysisDocumentCount(analysis, null);
		
		int jobCount = 2; // load analysis engine
		jobCount += 2; // save analysis, update document status
		jobCount += noOfDocs; // run analysis

		monitor.beginTask("Configuring analysis engine...", jobCount);

		analysis.getAnalysisEngineMetadata().setAnalysisEngine(null);
		if (!loadAnalysisEngine(new SubProgressMonitor(monitor, 2))) {
			monitor.done();
			return;
		}
		monitor.worked(1);

		if (monitor.isCanceled())
			return;

		monitor.setTaskName("Preparing to start analysis...");
		resetAnalysis(monitor); //uses 2
		if (monitor.isCanceled())
			return;

		runAnalysis(new SubProgressMonitor(monitor, noOfDocs));

		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				refreshViews();
			}
		});

	}

	protected void resetAnalysis(IProgressMonitor monitor) {
		analysis.setMetadata(null);
		analysis.resetStatistics();
		
		updateAnalysisDocuments();
		try {
			removeExistingDatapoints();
		} catch (Exception e) {
			e.printStackTrace();
			monitor.done();
			return;
		}

		monitor.worked(1);

		if (!saveAnalysis(new SubProgressMonitor(monitor, 1))) {
			monitor.done();
			return;
		}
	}

	protected void refreshViews() {
		GeneralUtils.refreshViews();

	}

	protected void closeDocumentEditors() {
		GeneralUtils.closeAllAnnotatedDocumentEditorsFor(analysis);

	}

	protected void removeExistingDatapoints() throws Exception {
		// remove analysis link
		List<Datapoint> datapoints = analysis.getDatapoints();
		analysis.setDatapoints(new ArrayList<Datapoint>());

		// delete the datapoints
		MiddleTier mt = Activator.getDefault().getMiddleTier();
		mt.deleteList(datapoints);
	}

	protected boolean updateAnalysisDocuments() {
		MiddleTier mt = Activator.getDefault().getMiddleTier();
		List<AnalysisDocument> docs;
		docs = analysis.getAnalysisDocuments();
		for (AnalysisDocument ad : docs) {
			// not saved to database here as we will be saving Analysis later
			// anyways
			ad.setStatus(AnalysisDocument.STATUS_PROCESSING);

			ad.setDatapoints(new ArrayList<Datapoint>());
			ad.getDocument().setText(null);
			ad.setCas(null);
			try {
				ad.setSerializedCAS(null);
			} catch (ODIEException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
			try {
				mt.persist(ad);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	/**
	 * @param monitor
	 */
	protected boolean saveAnalysis(IProgressMonitor monitor) {

		try {
			monitor.beginTask("Saving analysis...", 1);

			// ////////////////// Save Analysis
			MiddleTier mt = Activator.getDefault().getMiddleTier();

			if (analysis.getId() > 0) {
				monitor.subTask("Updating analysis...");
			} else {
				monitor.subTask("Saving analysis...");
			}
			mt.persist(analysis);

			monitor.worked(1);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			monitor.done();
		}

	}

	protected boolean loadAnalysisEngine(SubProgressMonitor monitor) {
		monitor.beginTask("Loading analysis engine...", 1);
		try {
			if (analysis.getAnalysisEngine() == null) {
				GeneralUtils.loadAndConfigureAnalysisEngine(analysis);
			}
			monitor.worked(1);
			if (monitor.isCanceled())
				return true;
			monitor.done();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			monitor.done();
			return false;
		}

	}
	protected void runAnalysis(final IProgressMonitor monitor)
			throws InvocationTargetException {
		monitor.setTaskName("Running analysis engine...");
		try {
			
			CollectionProcessingManager mCPM = configureCPM(monitor);
			Timer cancelTimer = createCPMCancellationTimer(monitor, mCPM);

			AnalysisCollectionReader collectionReader = setupCollectionReader();

			registerProgressMonitorWithCPM(collectionReader, mCPM, monitor,
					cancelTimer);

			cancelTimer.start();
			mCPM.process(collectionReader);

			synchronized (analysis) {
				logger.debug("Waiting on analysis");
				analysis.wait();
				logger.debug("Done waiting");
			}

			analysis.addPerformanceReport(mCPM.getPerformanceReport());
		} catch (InvalidXMLException e) {
			throw new InvocationTargetException(e);
		} catch (ResourceInitializationException e) {
			throw new InvocationTargetException(e);
		} catch (ResourceConfigurationException e) {
			throw new InvocationTargetException(e);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void registerProgressMonitorWithCPM(
			AnalysisCollectionReader collectionReader,
			CollectionProcessingManager mCPM, IProgressMonitor monitor,
			Timer cancelTimer) {
		int numDocs = collectionReader.getNumberOfDocuments();

		// monitor operations delegated to ProgressMonitorUpdater
		CPMMonitor pmu = new CPMMonitor(analysis, monitor, numDocs, cancelTimer);
		mCPM.addStatusCallbackListener(pmu);
	}

	private Timer createCPMCancellationTimer(final IProgressMonitor monitor,
			final CollectionProcessingManager mCPM) {
		return new Timer(100, new ActionListener() {
			public void actionPerformed(ActionEvent ee) {
				if (monitor.isCanceled()) {
					mCPM.stop();
					synchronized (analysis) {
						analysis.notify();
					}
				}
			}
		});
	}

	private CollectionProcessingManager configureCPM(
			final IProgressMonitor monitor)
			throws ResourceConfigurationException, InvalidXMLException,
			ResourceInitializationException {
		final CollectionProcessingManager mCPM = UIMAFramework
				.newCollectionProcessingManager();
		mCPM.setAnalysisEngine(analysis.getAnalysisEngine());
		mCPM.addCasConsumer(UIMAFramework
				.produceCasConsumer(AnalysisCasConsumer.getDescription()));
		return mCPM;

	}

	private AnalysisCollectionReader setupCollectionReader()
			throws InvalidXMLException, ResourceInitializationException {
		CollectionReaderDescription collectionReaderDesc = AnalysisCollectionReader
				.getDescription();
		ConfigurationParameterSettings paramSettings = collectionReaderDesc
				.getMetaData().getConfigurationParameterSettings();
		paramSettings.setParameterValue(
				AnalysisCollectionReader.PARAM_ANALYSIS_NAME, analysis
						.getName());
		AnalysisCollectionReader collectionReader = (AnalysisCollectionReader) UIMAFramework
				.produceCollectionReader(collectionReaderDesc);
		return collectionReader;
	}

	public CAS getCas() {
		return cas;
	}
}
