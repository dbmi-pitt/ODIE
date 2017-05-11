/**
 * 
 */
package edu.pitt.dbmi.odie.ui.workers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.dbmi.odie.lexicalizer.lucenefinder.LexicalSetBuilder;
import edu.pitt.dbmi.odie.model.LexicalSet;
import edu.pitt.dbmi.odie.ui.Activator;

/**
 * @author Girish Chavan
 * 
 */
public class LexicalSetWorker implements IRunnableWithProgress {

	LexicalSet newLexicalSet;

	public LexicalSetWorker(LexicalSet newLexicalSet) {
		super();
		this.newLexicalSet = newLexicalSet;
	}

	boolean success = false;

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

		monitor.beginTask("Creating new lexical set", IProgressMonitor.UNKNOWN);
		LexicalSetBuilder builder = new LexicalSetBuilder(Activator
				.getDefault().getMiddleTier());
		success = builder.createNewLexicalSet(newLexicalSet,
				new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent pce) {
						if (pce.getPropertyName().equals(
								ODIEConstants.PROPERTY_PROGRESS_MSG)) {
							monitor.subTask(pce.getNewValue().toString());
							if(monitor.isCanceled()){
								try {
									Activator.getDefault().getMiddleTier().delete(newLexicalSet);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								return;
							}
						}
					}
				});
		monitor.done();
	}

	public boolean isSuccessful() {
		return success;
	}
}
