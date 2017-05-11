package edu.pitt.dbmi.odie.ui.views.details.providers;

import org.apache.log4j.Logger;
import org.apache.uima.cas.Type;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.uima.utils.UIMAUtils;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class TypeContentProvider implements ITreeContentProvider {

	Logger logger = Logger.getLogger(this.getClass());

	public static final String INSTANCES_LABEL = "Instances";
	String[] categories = new String[] { INSTANCES_LABEL };

	private Type currentType;

	@Override
	public Object[] getElements(Object inputElement) {
		return categories;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object inputElement) {
		if (inputElement instanceof Type) {
			this.currentType = (Type) inputElement;
		}
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement.toString().equals(INSTANCES_LABEL)) {
			AnalysisDocument currentAnalysisDocument = GeneralUtils
					.getCurrentAnalysisDocument();

			if (currentAnalysisDocument == null)
				return new Object[] {};

			return UIMAUtils.getInstances(currentAnalysisDocument.getCas(),
					currentType);
		}
		return new Object[] {};
	}

	@Override
	public Object getParent(Object element) {
		if (element.toString().equals(INSTANCES_LABEL))
			return null;
		else
			return INSTANCES_LABEL;
	}

	@Override
	public boolean hasChildren(Object element) {
		return (element.toString().equals(INSTANCES_LABEL));
	}

}
