package edu.pitt.dbmi.odie.ui.views.providers;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class AnalysisLabelProvider implements ILabelProvider {

	@Override
	public Image getImage(Object element) {
		if (GeneralUtils.isOE((Analysis) element))
			return Aesthetics.getOeAESmallIcon();
		else if (GeneralUtils.isOther((Analysis) element))
			return Aesthetics.getOtherAESmallIcon();
		else
			return Aesthetics.getNerAESmallIcon();
	}

	@Override
	public String getText(Object element) {
		return ((Analysis) element).getName();
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

}
