package edu.pitt.dbmi.odie.ui.views.providers;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class AnnotationLabelProvider implements ITableLabelProvider {

	Logger logger = Logger.getLogger(this.getClass());

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof AnnotationFS) {
			AnnotationFS a = (AnnotationFS) element;
			switch (columnIndex) {
			case 0:
				return a.getCoveredText();
			default:
				return getValueForColumn(a, columnIndex - 1);
			}
		}
		return "";
	}

	private String getValueForColumn(AnnotationFS a, int featureIndex) {
		Type t = a.getType();
		if (t.getFeatures().size() > featureIndex) {
			Feature f = (Feature) t.getFeatures().get(featureIndex);
			return getDisplayValue(a, f);
		} else {
			logger.error("no feature for column index");
			return "error";
		}
	}

	private String getDisplayValue(AnnotationFS a, Feature f) {
		String s = "";
		try {
			s = a.getFeatureValueAsString(f);
		} catch (CASRuntimeException e) {
			FeatureStructure fs = a.getFeatureValue(f);
			if (fs != null)
				s = fs.toString();
		}
		return s;

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
