package edu.pitt.dbmi.odie.ui.workers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.utils.GeneralUtils;
import edu.pitt.ontology.IOntology;
import edu.pitt.ontology.IOntologyException;
import edu.pitt.ontology.IRepository;
import edu.pitt.ontology.bioportal.BOntology;
import edu.pitt.ontology.ui.OntologyImporter;

/**
 * @author Girish Chavan
 * 
 */
public class BioportalOntologyImporter implements IRunnableWithProgress {

	IOntology[] ontologyList = null;

	Logger logger = Logger.getLogger(BioportalOntologyImporter.class);

	private static final int PROGRESSBAR_MAX = 200;
	private static final int NUMBER_OF_LOADING_STAGES = 2;

	public BioportalOntologyImporter(IOntology[] ontologyList) {
		this.ontologyList = ontologyList;
	}

	class BioportalPropertyChangeListener implements PropertyChangeListener {
		int classCount = 0;
		int pageCount = 0;
		int pageSize = 0;
		int processedPage = 0;
		int processedClass = 0;
		String currentStage = null;

		String prefix = "Loading ";
		String suffix = "";
		IProgressMonitor monitor;
		private int minPageEvents = 1;
		private int pageIncrement;

		int pageEventCounter = 0;
		private int classEventCounter;
		private int minClassEvents;
		private int classIncrement;

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(BOntology.ONTOLOGY_CLASS_COUNT)) {
				classCount = (Integer) evt.getNewValue();

				// we divide the class count by 1000, since this event is
				// generated every 1000 classes
				float fclassIncrement = (float) PROGRESSBAR_MAX
						/ (float) Math.ceil((float) classCount / 1000f);
				if (fclassIncrement < 1) {
					classIncrement = 1;
					minClassEvents = (int) (1 / fclassIncrement);
				} else
					classIncrement = (int) fclassIncrement;
			} else if (evt.getPropertyName().equals(
					BOntology.ONTOLOGY_PAGE_COUNT)) {
				pageCount = (Integer) evt.getNewValue();

				float fpageIncrement = (float) PROGRESSBAR_MAX
						/ (float) pageCount;
				if (fpageIncrement < 1) {
					pageIncrement = 1;
					minPageEvents = (int) (1 / fpageIncrement);
				} else
					pageIncrement = (int) fpageIncrement;

			} else if (evt.getPropertyName().equals(
					BOntology.ONTOLOGY_PAGE_SIZE)) {
				pageSize = (Integer) evt.getNewValue();
			} else if (evt.getPropertyName().equals(
					BOntology.ONTOLOGY_LOAD_STAGE)) {
				currentStage = (String) evt.getNewValue();

				if (currentStage
						.equals(BOntology.ONTOLOGY_LOAD_STAGE_GETALLCLASSES)) {
					prefix = "Downloading Classes...";
					suffix = "";
				} else if (currentStage
						.equals(BOntology.ONTOLOGY_LOAD_STAGE_BUILDHIERARCHY)) {
					prefix = "Building Hierarchy...";
					suffix = "";
				}
			} else if (evt.getPropertyName().equals(
					BOntology.ONTOLOGY_PROCESSED_PAGE)) {
				processedPage = (Integer) evt.getNewValue();

				suffix = "("
						+ GeneralUtils
								.getPrettyPercentageFormat((float) processedPage
										/ (float) pageCount) + ")";
				pageEventCounter += 1;

				if (minPageEvents <= pageEventCounter) {
					monitor.worked(pageIncrement);
					pageEventCounter = 0;
				}

			} else if (evt.getPropertyName().equals(
					BOntology.ONTOLOGY_PROCESSED_CLASS)) {
				processedClass = (Integer) evt.getNewValue();
				suffix = "("
						+ GeneralUtils
								.getPrettyPercentageFormat((float) processedClass
										/ (float) classCount) + ")";
				classEventCounter += 1;

				if (minClassEvents <= classEventCounter) {
					monitor.worked(classIncrement);
					classEventCounter = 0;
				}
			} else if (evt.getPropertyName().equals(
					IOntology.ONTOLOGY_LOADING_EVENT))
				logger.info(evt.getNewValue());

			monitor.subTask(prefix + suffix);
		}

		public BioportalPropertyChangeListener(IProgressMonitor monitor) {
			super();
			this.monitor = monitor;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core
	 * .runtime.IProgressMonitor)
	 */
	@Override
	public void run(final IProgressMonitor monitor)
			throws InvocationTargetException, InterruptedException {
		MiddleTier mt = Activator.getDefault().getMiddleTier();
		IRepository targetRepository = mt.getRepository();
		if (ontologyList.length > 0) {
			OntologyImporter oi = new OntologyImporter(ontologyList[0]
					.getRepository());
			oi.setDisposeSource(true);

			PropertyChangeListener l = new PropertyChangeListener() {
				int count = 0;

				@Override
				public void propertyChange(PropertyChangeEvent pce) {
					if (pce.getPropertyName().equals(
							OntologyImporter.PROPERTY_PROGRESS_MSG)) {
						count++;
						monitor.setTaskName(pce.getNewValue().toString() + " ("
								+ count + ")");
					}
				}
			};

			oi.addPropertyChangeListener(l);

			int units = PROGRESSBAR_MAX * ontologyList.length
					* NUMBER_OF_LOADING_STAGES;
			monitor.beginTask("Importing selected ontologies...", units);
			try {
				for (IOntology o : ontologyList) {
					monitor.setTaskName("Importing " + o.getName());
					IOntology newOntology = targetRepository.createOntology(o
							.getURI());
					// oi.copyNoCycle(o,newOntology);
					if (o instanceof BOntology) {
						BioportalPropertyChangeListener bpcl = new BioportalPropertyChangeListener(
								monitor);
						o.addPropertyChangeListener(bpcl);
						((BOntology) o).copy2(newOntology);
						o.removePropertyChangeListener(bpcl);
					} else {
						oi.copy(o, newOntology);
					}
					monitor.setTaskName("Saving " + o.getName());
					monitor.subTask("");
					targetRepository.importOntology(newOntology);
					importedOntologies.add(newOntology);
				}

			} catch (IOntologyException e) {
				e.printStackTrace();
			} finally {
				monitor.done();
			}
		}
	}

	public boolean wasSuccessful() {
		return importedOntologies.size() == ontologyList.length;
	}

	List<IOntology> importedOntologies = new ArrayList<IOntology>();

	public List<IOntology> getImportedOntologies() {
		return importedOntologies;
	}
}
