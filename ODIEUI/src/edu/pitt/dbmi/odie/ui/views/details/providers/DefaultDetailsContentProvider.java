package edu.pitt.dbmi.odie.ui.views.details.providers;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class DefaultDetailsContentProvider implements ITreeContentProvider {

	Logger logger = Logger.getLogger(this.getClass());

	public Object[] getElements(Object inputElement) {
		return new Object[] {};
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object inputElement) {
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return new Object[] {};
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return false;
	}
}
