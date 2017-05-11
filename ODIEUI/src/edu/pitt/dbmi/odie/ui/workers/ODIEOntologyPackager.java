/**
 * 
 */
package edu.pitt.dbmi.odie.ui.workers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.model.LanguageResource;
import edu.pitt.dbmi.odie.model.LexicalSet;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.ui.wizards.NewLexicalSetWizard;
import edu.pitt.dbmi.odie.utils.GeneralUtils;
import edu.pitt.ontology.IOntology;
import edu.pitt.ontology.IResource;
import edu.pitt.ontology.protege.ProtegeRepository;

/**
 * @author Girish Chavan
 * 
 */
public class ODIEOntologyPackager implements IRunnableWithProgress {

	LanguageResource[] ontologies;
	Logger logger = Logger.getLogger(ODIEOntologyPackager.class);

	public ODIEOntologyPackager(LanguageResource[] languageResources) {
		super();
		this.ontologies = languageResources;
	}

	boolean success = true;

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

		monitor.beginTask("Running...", ontologies.length);
		
		for(LanguageResource lr:ontologies){
			ODIEDataFileCreator creator = new ODIEDataFileCreator();
			creator.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent e) {
					System.out.println(e.getNewValue());
				}
			});
			monitor.setTaskName("Preparing " + lr.getName());
			MiddleTier mt = Activator.getDefault().getMiddleTier();
			LexicalSet ls = mt.getLexicalSetForLanguageResource(lr);
			
			if(ls == null){
				
				List<IResource> rl = new ArrayList<IResource>();
				rl.add(lr.getResource());
			
				ls = NewLexicalSetWizard.createLexicalSetObject(
						lr.getName(), "Lexicon for " +  lr.getName(),rl);
				monitor.subTask("Building Vocabulary");
				LexicalSetWorker worker = new LexicalSetWorker(ls);
				worker.run(new SubProgressMonitor(monitor, IProgressMonitor.UNKNOWN));
				if(!worker.isSuccessful()){
					success = false;
					MessageDialog.openError(Display.getCurrent().getActiveShell(),
							"Could not create vocabulary",
							"An error occured when trying to create the '"
									+ ls.getName() + "' vocabulary");
					continue;
				}
			}
			
			monitor.setTaskName("Packaging " + lr.getName());
			
			creator.setRepository((ProtegeRepository) Activator.getDefault().getMiddleTier().getRepository());
			creator.setOntologyURI(((IOntology)lr.getResource()).getURI());
			
			creator.setIndexFinderDirectory(new File(ls.getLocation()));
			creator.setSearchIndexDirectory(new File(GeneralUtils.getOntologySearchIndexLocation(lr)));
			creator.setOutputDirectory(new File("C:/temp"));
			
			//now do some work
			try {
				creator.save();
				monitor.worked(1);
			} catch (IOException e1) {
				e1.printStackTrace();
				logger.error(e1.getMessage());
			}
		}
		monitor.done();
	}

	public boolean isSuccessful() {
		return success;
	}
}
