package edu.pitt.dbmi.odie.ui.views;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import edu.pitt.dbmi.odie.ui.editors.annotations.IAnnotationSubType;

public class AnnotationsPatternFilter extends ViewerFilter {

	CAS aCas;

	public CAS getCas() {
		return aCas;
	}

	public void setCas(CAS cas) {
		aCas = cas;
	}

	private boolean hasAnnotations(Type type) {
		if (aCas != null && aCas.getAnnotationIndex(type) != null)
			return aCas.getAnnotationIndex(type).size() > 0;
		else
			return false;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		boolean hasAnnotations = false;

		if (element instanceof Type && !(element instanceof IAnnotationSubType))
			hasAnnotations = hasAnnotations((Type) element);
		else
			return true;

		return hasAnnotations;
	}

}
