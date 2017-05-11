/**
 * 
 */
package edu.pitt.dbmi.odie.ui.workers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import edu.pitt.ontology.IOntology;
import edu.pitt.ontology.IOntologyException;

/**
 * @author Girish Chavan
 * 
 */
public class OntologyLoader implements IRunnableWithProgress {

	IOntology ontology;

	/**
	 * @param o
	 */
	public OntologyLoader(IOntology o) {
		ontology = o;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core
	 * .runtime.IProgressMonitor)
	 */
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {

		monitor.beginTask("Loading " + ontology.getName(), IProgressMonitor.UNKNOWN);
		monitor.subTask("");
		try {
			ontology.load();
			monitor.worked(1);
		} catch (IOntologyException e) {
			e.printStackTrace();
			monitor.setTaskName("Error loading " + ontology.getName());
			monitor.setCanceled(true);
		}

		monitor.done();
	}

}
