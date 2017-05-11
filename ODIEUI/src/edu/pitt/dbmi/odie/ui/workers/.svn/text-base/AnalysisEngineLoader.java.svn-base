package edu.pitt.dbmi.odie.ui.workers;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.text.DateFormatter;

import org.apache.log4j.Logger;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.internal.util.Timer;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.XMLInputSource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.model.AnalysisEngineMetadata;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.uima.utils.UIMAUtils;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class AnalysisEngineLoader implements IRunnableWithProgress {
	private File aeDescriptorFile;
	URL aeURL;

	Logger logger = Logger.getLogger(this.getClass());

	public AnalysisEngineLoader(URL url) {
		aeURL = url;
	}

	public void destroyAe() {
		if (ae != null) {
			ae.destroy();
			ae = null;
		}
	}

	/*
	 * Adapted from org.apache.uima.tools.cvd.MainFrame.loadAEDescriptor(File)
	 */
	public void loadAEDescriptor(File descriptorFile, IProgressMonitor monitor) {
		monitor.subTask(descriptorFile.getName());
		Timer timer = new Timer();
		timer.start();

		try {
			ae = UIMAUtils.loadAnalysisEngine(descriptorFile);

			success = (ae != null);
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
			setErrorMessage(e.getMessage());
		}

		if (!success) {
			monitor.done();
			return;
		}

		if (ae != null) {
			String annotName = ae.getAnalysisEngineMetaData().getName();
			monitor.subTask("Finished loading '" + annotName + "' ("
					+ timer.getTimeSpan() + ")");

			success = true;
			monitor.worked(1);
			monitor.done();
		}
	}

	boolean success = false;
	String errorMessage;

	public boolean wasSuccessful() {
		return success;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {

		monitor.beginTask("Loading Analysis Engine:", 1);

		aeDescriptorFile = new File(aeURL.getFile());

		if (monitor.isCanceled())
			return;

		loadAEDescriptor(aeDescriptorFile, monitor);

	}

	protected boolean setupAE(File aeFile) {
		try {

			// Destroy old AE.
			if (ae != null) {
				destroyAe();
			}

			// get Resource Specifier from XML file
			XMLInputSource in = new XMLInputSource(aeFile);
			ResourceSpecifier specifier = UIMAFramework.getXMLParser()
					.parseResourceSpecifier(in);

			// create Analysis Engine here
			ae = UIMAFramework.produceAnalysisEngine(specifier);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	AnalysisEngine ae;

	public AnalysisEngine getAnalysisEngine() {
		return ae;
	}

	public AnalysisEngineMetadata getAnalysisEngineMetadata() {
		AnalysisEngineMetadata aem = null;
		try {
			MiddleTier mt = Activator.getDefault().getMiddleTier();

			aem = mt.getAnalysisEngineMetadata(aeURL);

			if (aem == null)
				aem = new AnalysisEngineMetadata();

			aem.setAnalysisEngine(ae);
			SimpleDateFormat sdf = new SimpleDateFormat(" - MM-dd-yy hh:mma");
			aem.setName(ae.getAnalysisEngineMetaData().getName() + sdf.format(new Date()));
			aem.setDescription(ae.getAnalysisEngineMetaData().getDescription());
			aem.setURL(aeURL);

			aem.setTypeSystem(UIMAUtils.createCAS(
					ae.getAnalysisEngineMetaData().getTypeSystem())
					.getTypeSystem());

			aem.setSerializedTypeSystemDescriptor(UIMAUtils
					.serializeTypeSystemDescriptor(aem.getAnalysisEngine()
							.getAnalysisEngineMetaData().getTypeSystem()));

			aem.setType(determineType());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return null;
		}
		return aem;
	}

	private String determineType() {
		if (GeneralUtils.hasAllOEParameters(ae.getAnalysisEngineMetaData()))
			return ODIEConstants.AE_TYPE_OE;
		else if (GeneralUtils.hasAllNERParameters(ae.getAnalysisEngineMetaData()))
//		else if(GeneralUtils.generatesNamedEntityAnnotationType(ae.getAnalysisEngineMetaData()))
			return ODIEConstants.AE_TYPE_NER;
		else
			return ODIEConstants.AE_TYPE_OTHER;

		// ae.getAnalysisEngineMetaData().get
		// if(name.equals(ODIEConstants.IF_NER_AE_NAME))
		// return ODIEConstants.AE_TYPE_NER;
		// else
		// return ODIEConstants.AE_TYPE_OTHER;

	}

}
