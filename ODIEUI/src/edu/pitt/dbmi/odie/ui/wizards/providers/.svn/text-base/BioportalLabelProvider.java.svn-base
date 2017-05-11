package edu.pitt.dbmi.odie.ui.wizards.providers;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.model.LanguageResource;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.ontology.IOntology;
import edu.pitt.ontology.IRepository;

public class BioportalLabelProvider implements ILabelProvider, IColorProvider {

	@Override
	public Image getImage(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getText(Object element) {
		return ((IOntology) element).getName();
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
		String name = getText(element);
		
		MiddleTier mt = Activator.getDefault().getMiddleTier();
		LanguageResource lr = mt.getLanguageResource(name);
		
		if(lr == null)
			return Aesthetics.getForegroundColor();
		else
			return Aesthetics.getInactiveForegroundColor();
			
		
	}

}
