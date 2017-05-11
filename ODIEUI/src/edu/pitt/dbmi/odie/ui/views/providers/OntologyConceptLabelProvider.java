package edu.pitt.dbmi.odie.ui.views.providers;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.cas.ArrayFS;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class OntologyConceptLabelProvider implements ITableLabelProvider {

	Logger logger = Logger.getLogger(this.getClass());

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof FeatureStructure) {
			FeatureStructure fs = (FeatureStructure) element;

			Type t = fs.getType();
			List featureList = t.getFeatures();

			if (columnIndex >= featureList.size())
				return "invalid column";

			switch (columnIndex) {
			default:
				return getValueForFeature(fs, (Feature) featureList
						.get(columnIndex));
			}
		}
		return "";
	}

	private String getValueForFeature(FeatureStructure fs, Feature f) {
		String s;
		try {
			return fs.getStringValue(f);
		} catch (CASRuntimeException e) {
			FeatureStructure fs2 = fs.getFeatureValue(f);
			return fs2.toString();
		}
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
