package edu.pitt.dbmi.odie.ui.editors.providers;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.dbmi.odie.utils.GeneralUtils;
import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IResource;

public class OntologyTreeLabelProvider implements ILabelProvider, IColorProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof IResource)
			return ((IResource) element).getName();
		else
			return "Not a IResource";
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public Color getBackground(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getForeground(Object element) {
		if(!(element instanceof IClass))
			return Aesthetics.getForegroundColor();
		
		if(GeneralUtils.isProxyClass((IClass)element)){
			return Aesthetics.getFilterTextColor();
		}
		return Aesthetics.getForegroundColor();

	}
}
