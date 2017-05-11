package edu.pitt.dbmi.odie.ui.views.providers;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.pitt.dbmi.odie.model.Analysis;

public class DocumentsContentProvider implements IStructuredContentProvider,
		ItemCountProvider {

	long itemCount = 0;

	@Override
	public Object[] getElements(Object inputElement) {

		if (inputElement instanceof Analysis) {
			Analysis a = (Analysis) inputElement;
			itemCount = a.getAnalysisDocuments().size();
			return a.getAnalysisDocuments().toArray();
		} else
			return new Object[] {};

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	@Override
	public long getItemCount() {
		return itemCount;
	}

}
