package edu.pitt.dbmi.odie.ui.views.providers;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.FilteredTree;

import edu.pitt.dbmi.odie.ui.editors.annotations.IAnnotationSubType;

public class AnnotationTypeSystemTreeLabelProvider extends
		TypeSystemTreeLabelProvider {

	CAS aCas;

	public CAS getCas() {
		return aCas;
	}

	public void setCas(CAS cas) {
		aCas = cas;
	}

	public AnnotationTypeSystemTreeLabelProvider(FilteredTree filterTree,
			Display display) {
		super(filterTree, display);
	}

	@Override
	public Image getImage(Object element) {
		// do not display color image if type has children
		if (element instanceof Type && !(element instanceof IAnnotationSubType)) {
			Type type = (Type) element;
			if (hasChildren(type))
				return null;
		}
		return super.getImage(element);
	}

	private boolean hasChildren(Type type) {

		if (aCas == null)
			return false;

		TypeSystem ts = aCas.getTypeSystem();
		return !ts.getDirectSubtypes(type).isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {

		if (element instanceof IAnnotationSubType) {
			return ((IAnnotationSubType) element).getShortName();
		} else if (element instanceof Type) {
			String text = super.getText(element);
			int count = getCountForType((Type) element);
			text += "(" + count + ")";
			return text;
		} else
			return "Unexpected:" + element.getClass().getName();
	}

	private int getCountForType(Type element) {
		if (aCas == null || element instanceof IAnnotationSubType)
			return 0;
		return aCas.getAnnotationIndex((Type) element).size();
	}

	public Object getType(String typeName) {
		Type t = aCas.getTypeSystem().getType(typeName);
		return t;
	}
}
