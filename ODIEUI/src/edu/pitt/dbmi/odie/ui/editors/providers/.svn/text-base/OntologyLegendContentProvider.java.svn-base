/**
 * @author Girish Chavan
 *
 * Sep 15, 2008
 */
package edu.pitt.dbmi.odie.ui.editors.providers;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.ui.jfreechart.ChartStatistics;

public class OntologyLegendContentProvider implements
		IStructuredContentProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java
	 * .lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement != null && inputElement instanceof ChartStatistics) {
			Analysis a = ((ChartStatistics) inputElement).getAnalysis();

			return buildElementArray(a);
		} else
			return new Object[] {};
	}

	private Object[] buildElementArray(Analysis a) {
		Collection c = a.getStatistics().ontologyStatistics;
		if (c == null) {
			Object[] out = new Object[] { a.getStatistics() };
			return out;
		}

		Object[] out = Arrays.copyOf(c.toArray(), c.size() + 1);
		
		//add the overall statistics to end of array
		out[c.size()] = a.getStatistics();
		return out;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface
	 * .viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

}
