/**
 * @author Girish Chavan
 *
 * Sep 15, 2008
 */
package edu.pitt.dbmi.odie.ui.editors.providers;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.LanguageResource;
import edu.pitt.dbmi.odie.ui.views.AnalysisLRComparator;

public class LegendContentProvider implements IStructuredContentProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement!=null && inputElement instanceof Analysis){
			Analysis analysis = (Analysis)inputElement;
			List<LanguageResource> l = analysis.getLanguageResources();
			
			SortedSet<LanguageResource> returnSet = new TreeSet<LanguageResource>(
					new AnalysisLRComparator());

			if(analysis.isOE())
				returnSet.add(analysis.getProposalOntology());
			else{
				for(AnalysisLanguageResource alr:l){
					returnSet.add(alr);
				}
			}
			
			return returnSet.toArray();
		}
		else
			return new Object[]{};
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}

}
