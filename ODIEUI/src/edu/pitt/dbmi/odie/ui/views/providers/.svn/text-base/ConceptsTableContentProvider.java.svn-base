package edu.pitt.dbmi.odie.ui.views.providers;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.AnalysisDocument;

public class ConceptsTableContentProvider implements
		IStructuredContentProvider, ItemCountProvider {

	long itemCount = 0;

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof Analysis) {
			Analysis a = (Analysis) inputElement;
			itemCount = a.getDatapoints().size();
			return a.getDatapoints().toArray();
		} else if (inputElement instanceof AnalysisDocument) {
			AnalysisDocument ad = (AnalysisDocument) inputElement;
			itemCount = ad.getDatapoints().size();
			return ad.getDatapoints().toArray();
		} else
			return new Object[] {};
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public long getItemCount() {
		return itemCount;
	}

}
