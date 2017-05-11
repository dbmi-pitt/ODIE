package edu.pitt.dbmi.odie.ui.views.details.providers;

import org.apache.log4j.Logger;
import org.apache.uima.cas.text.AnnotationFS;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import edu.pitt.dbmi.odie.ui.Aesthetics;

public class TypeLabelProvider implements ITableLabelProvider,
		ITableFontProvider {
	static Logger logger = Logger.getLogger(TypeLabelProvider.class);

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
		} else if (element instanceof AnnotationFS) {
			switch (columnIndex) {
			case 0:
				return ((AnnotationFS) element).getCoveredText();
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
		if (element instanceof String && columnIndex == 0) {
			return Aesthetics.getBoldFont();
		}
		return null;
	}

}
