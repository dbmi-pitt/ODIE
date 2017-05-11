package edu.pitt.dbmi.odie.ui.wizards.providers;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.ui.Activator;

public class BioportalContentProvider implements IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		MiddleTier mt = Activator.getDefault().getMiddleTier();
		return mt.getBioportalRepository().getOntologies();
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
