package edu.pitt.dbmi.odie.ui.views.providers;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.ui.Activator;

public class AnalysisContentProvider implements ITreeContentProvider {

	final Viewer viewer;

	public AnalysisContentProvider(Viewer viewer) {
		super();
		this.viewer = viewer;

		// Activator.getDefault().getMiddleTier().addModelChangeListener(this);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement.equals("root"))
			return getElements(root);
		else
			return new Object[] {};
	}

	@Override
	public Object getParent(Object element) {
		if (element.equals("root"))
			return null;
		else
			return root;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element.equals("root"))
			return true;
		else
			return false;
	}

	Object root = null;

	@Override
	public Object[] getElements(Object inputElement) {
		root = inputElement;
		if (Activator.getDefault().getMiddleTier() == null)
			return new Object[] {};

		List<Analysis> olist = Activator.getDefault().getMiddleTier()
				.getAnalyses();
		if (olist == null)
			return new Object[] {};

		Object[] oarr = new Object[olist.size()];

		for (int i = 0; i < olist.size(); i++) {
			oarr[i] = olist.get(i);
		}

		return oarr;

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

}
