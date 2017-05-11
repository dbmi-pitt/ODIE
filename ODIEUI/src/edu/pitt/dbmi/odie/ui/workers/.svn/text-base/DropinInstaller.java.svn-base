package edu.pitt.dbmi.odie.ui.workers;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.model.AnalysisEngineMetadata;
import edu.pitt.dbmi.odie.model.Dropin;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class DropinInstaller implements IRunnableWithProgress {
	Logger logger = Logger.getLogger(this.getClass());

	private List<File> files;

	public DropinInstaller(List<File> dropinFiles) {
		this.files = dropinFiles;
	}

	String errorMessage;

	boolean success = false;

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
	public void run(final IProgressMonitor monitor)
			throws InvocationTargetException, InterruptedException {

		monitor.beginTask("Installing Files...", files.size());

		for (File f : files) {
			String type = GeneralUtils.getTypeForFile(f);
			if (type.equals(ODIEConstants.FILETYPE_PEAR)) {
				success = installAEFromPEAR(f, new SubProgressMonitor(monitor,
						1));
			} else if (type.equals(ODIEConstants.FILETYPE_ONTOLOGY)) {
				OntologyImporter oi = new OntologyImporter(f.toURI());
				oi.run(new SubProgressMonitor(monitor, 1));

				success = oi.wasSuccessful();
			}
			else if (type.equals(ODIEConstants.FILETYPE_ODIEONTOLOGY)) {
				ODIEOntologyImporter oi = new ODIEOntologyImporter(f.toURI());
				oi.run(new SubProgressMonitor(monitor, 1));

				success = oi.wasSuccessful();
			}
			
			if (wasSuccessful())
				addDropinToTable(f);
		}
	}

	private void addDropinToTable(File f) {
		MiddleTier mt = Activator.getDefault().getMiddleTier();

		Dropin di = mt.getInstalledDropIn(f);

		if (di == null)
			di = new Dropin();

		di.setFilenname(f.getName());
		di.setType(GeneralUtils.getTypeForFile(f));
		di.setLastModified(f.lastModified());

		try {
			mt.persist(di);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean installAEFromPEAR(File f, IProgressMonitor monitor) {

		monitor.beginTask("Installing " + f.getName(), 3);
		monitor.setTaskName("Installing " + f.getName());
		File aeInstallationDir = GeneralUtils.getAEInstallationDir();

		try {
			PEARInstaller pi = new PEARInstaller(f, aeInstallationDir);
			pi.run(new SubProgressMonitor(monitor, 2));
			if (!pi.wasSuccessful()) {
				setErrorMessage(pi.getErrorMessage());
				return false;
			}

			monitor.setTaskName("Registering Analysis Engine...");
			URL url = (new File(pi.getMainComponentDescFilePath())).toURI()
					.toURL();
			AnalysisEngineLoader aeloader = new AnalysisEngineLoader(url);
			aeloader.run(new SubProgressMonitor(monitor, 1));

			if (!aeloader.wasSuccessful()) {
				setErrorMessage(aeloader.getErrorMessage());
				return false;
			}

			AnalysisEngineMetadata aem = aeloader.getAnalysisEngineMetadata();
			Activator.getDefault().getMiddleTier().persist(aem);

			monitor.done();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			setErrorMessage(e.getMessage());
			return false;

		}
	}
}
