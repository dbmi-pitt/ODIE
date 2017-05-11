/**
 * 
 */
package edu.pitt.dbmi.odie.ui.views.providers;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.pitt.dbmi.odie.model.Statistics;

/**
 * @author Girish Chavan
 *
 */
public class PhraseContentProvider implements IStructuredContentProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof Statistics){
			Statistics s = (Statistics)inputElement;
			if(s.getPhraseSuggestions() !=null)
				return s.getPhraseSuggestions().toArray();
		}

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
