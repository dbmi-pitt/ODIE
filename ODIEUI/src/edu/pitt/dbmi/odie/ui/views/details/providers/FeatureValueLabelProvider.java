package edu.pitt.dbmi.odie.ui.views.details.providers;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import edu.pitt.dbmi.odie.ui.Aesthetics;

public class FeatureValueLabelProvider implements ITableLabelProvider,
		ITableFontProvider {
	static Logger logger = Logger.getLogger(FeatureValueLabelProvider.class);

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		return null;
	}

	public static final String clickMessage = "(click to edit)";

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof String) {
			switch (columnIndex) {
			case 0:
				return element.toString();
			default:
				return "";
			}
		} else if (element instanceof FeatureValue) {
			switch (columnIndex) {
			case 0:
				return ((FeatureValue) element).feature;
			case 1:
				return ((FeatureValue) element).value;
			default:
				return "";
			}
		}
		return "";
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
	public Font getFont(Object element, int columnIndex) {
		if (columnIndex == 0 && !(element instanceof String)) {
			return Aesthetics.getBoldFont();
		}
		return Aesthetics.getRegularFont();
	}

}
