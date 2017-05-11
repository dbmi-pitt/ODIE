package edu.pitt.dbmi.odie.ui.workers;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CAS;
import org.apache.uima.resource.metadata.FsIndexDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.model.Datapoint;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.uima.utils.UIMAUtils;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class AnalysisLoader implements IRunnableWithProgress {

	Analysis analysis;
	MiddleTier mt;
	private Logger logger = Logger.getLogger(AnalysisLoader.class);

	public AnalysisLoader(Analysis analysis) {
		this.analysis = analysis;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {

		mt = Activator.getDefault().getMiddleTier();

		int work = 1; // doc ts init
		int numDocs = analysis.getAnalysisDocuments().size();
		int numDps = analysis.getDatapoints().size();
		work += numDocs + numDps;

		if (analysis.getAnalysisEngine() == null)
			work++;

		monitor.beginTask("Loading Analysis...", work);
//		try {
//			GeneralUtils.initTypeSystemIfRequired(analysis
//					.getAnalysisEngineMetadata());
//		} catch (CASRuntimeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ResourceInitializationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvalidXMLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		// loadDatapoints(new SubProgressMonitor(monitor,numDps));
		// loadDocuments(analysis,new SubProgressMonitor(monitor,numDocs+1));

		// if(analysis.getAnalysisEngine()==null){
		// loadAnalysisEngine(new SubProgressMonitor(monitor,1));
		// }

		monitor.done();
	}

	private boolean loadAnalysisEngine(SubProgressMonitor monitor) {
		monitor.beginTask("Initializing Analysis Engine...", 1);
		try {
			GeneralUtils.loadAndConfigureAnalysisEngine(analysis);
		} catch (Exception e) {
			e.printStackTrace();
			monitor.done();
			return false;
		}
		monitor.worked(1);
		monitor.done();
		return true;
	}

	private void loadDatapoints(SubProgressMonitor monitor) {
		int numDps = analysis.getDatapoints().size();
		monitor.beginTask("Loading Datapoints...", numDps);
		int i = 0;
		for (Datapoint dp : analysis.getDatapoints()) {
			i++;
			monitor.setTaskName("Loading Datapoints(" + i + "/" + numDps
					+ ")...");
			monitor.subTask(dp.getConceptId());
			if (dp.getConceptClass() == null) {
				try {
					dp.setConceptClass(GeneralUtils.getConceptClass(dp.getAnalysis(),dp
							.getConceptURIString()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			monitor.worked(1);
		}
		monitor.done();
	}

	public static boolean loadDocuments(Analysis analysis,
			SubProgressMonitor monitor) {
		int numDocs = analysis.getAnalysisDocuments().size();
		monitor.beginTask("Loading Documents...", numDocs + 1);

		boolean status = false;
		try {
			monitor.subTask("Initializing Type System");
			GeneralUtils.initTypeSystemIfRequired(analysis
					.getAnalysisEngineMetadata());

			TypeSystemDescription tsDesc = UIMAUtils
					.deSerializeTypeSystemDescriptor(analysis
							.getAnalysisEngineMetadata()
							.getSerializedTypeSystemDescriptor());
			tsDesc.resolveImports();
			monitor.worked(1);
			int i = 0;
			for (AnalysisDocument ad : analysis.getAnalysisDocuments()) {
				i++;
				monitor.setTaskName("Loading Documents(" + i + "/" + numDocs
						+ ")...");
				monitor.subTask(ad.getDocument().getName());
				if (!ad.getStatus().equals(AnalysisDocument.STATUS_DONE)) {
					continue;
				} else
					assert ad.getSerializedCAS() != null;

				CAS aCAS = CasCreationUtils.createCas(tsDesc, null,
						new FsIndexDescription[0]);
				UIMAUtils.deSerializeCAS(ad.getSerializedCAS(), aCAS);
				ad.setCas(aCAS);
				monitor.worked(1);
			}
			status = true;
		} catch (Exception e) {
			e.printStackTrace();
			status = false;
		} finally {
			monitor.done();
		}

		return status;

	}
}
