package edu.pitt.dbmi.odie.ui.views.providers;

import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.NonEmptyFSList;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import edu.mayo.bmi.uima.coref.type.Chain;
import edu.mayo.bmi.uima.coref.type.Markable;

public class CoreferenceLabelProvider implements ILabelProvider {

	@Override
	public Image getImage(Object element) {
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof Chain) {
			return getAntecedentCoveredText((Chain) element) + "("
					+ ((Chain) element).getSize() + ")";
		} else if (element instanceof Markable) {
			return "Markable " + ((Markable) element).getId() + ":"
					+ ((Markable) element).getCoveredText();
		} else
			return element.toString();
	}

	private String getAntecedentCoveredText(Chain c) {
		FSList m = c.getMembers();

		if (m instanceof NonEmptyFSList) {
			Markable mk = ((Markable) ((NonEmptyFSList) m).getHead());
			return mk.getCoveredText();
		}
		return "No Antecedent";
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
