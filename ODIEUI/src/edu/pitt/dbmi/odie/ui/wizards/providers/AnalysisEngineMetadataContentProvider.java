package edu.pitt.dbmi.odie.ui.wizards.providers;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.pitt.dbmi.odie.ui.Activator;

public class AnalysisEngineMetadataContentProvider implements
		IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement != null && inputElement instanceof String
				&& ((String) inputElement).length() > 0)
			return Activator.getDefault().getMiddleTier()
					.getAnalysisEngineMetadatas((String) inputElement)
					.toArray();
		else
			return Activator.getDefault().getMiddleTier()
					.getAnalysisEngineMetadatas(null).toArray();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		;
	}

}
