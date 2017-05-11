package edu.pitt.dbmi.odie.ui.editors.providers;

import org.apache.uima.analysis_engine.metadata.AnalysisEngineMetaData;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

public class UIMAAnalysisEngineMetaDataLabelProvider implements ILabelProvider {

	@Override
	public Image getImage(Object element) {
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof AnalysisEngineMetaData)
			return ((AnalysisEngineMetaData) element).getName();
		return "Not an AEM object";
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

}
