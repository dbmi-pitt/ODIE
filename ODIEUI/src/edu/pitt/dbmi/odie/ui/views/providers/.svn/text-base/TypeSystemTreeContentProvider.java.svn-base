package edu.pitt.dbmi.odie.ui.views.providers;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class TypeSystemTreeContentProvider implements ITreeContentProvider {

	Logger logger = Logger.getLogger(this.getClass());

	protected TypeSystem typeSystem;

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Type) {
			return typeSystem.getDirectSubtypes((Type) parentElement).toArray();
		} else
			return new Object[] {};
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof Type)
			return typeSystem.getParent((Type) element);
		else
			return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof Type) {
			List l = typeSystem.getDirectSubtypes((Type) element);
			return (l != null && l.size() > 0);
		} else
			return false;

	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof TypeSystem) {
			this.typeSystem = (TypeSystem) inputElement;
			return new Object[] { ((TypeSystem) inputElement).getTopType() };
		} else {
			logger.error("Input not a TypeSystem");
			return new Object[] {};
		}
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof TypeSystem)
			typeSystem = (TypeSystem) newInput;
		else {
			logger.error("Input not a TypeSystem");
		}
	}
}
