package edu.pitt.dbmi.odie.ui.editors.providers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IOntology;

public class OntologyTreeContentProvider implements ITreeContentProvider {

	Logger logger = Logger.getLogger(this.getClass());

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IClass) {
//			return sort(((IClass) parentElement).getDirectSubClasses());
			return sort(getNonRecursiveChildren((IClass) parentElement));
		} else
			return new Object[] {};
	}

	private IClass[] getNonRecursiveChildren(IClass parentElement) {
		IClass[] parents = parentElement.getSuperClasses();
		IClass[] children = parentElement.getDirectSubClasses();
		
		List<IClass> clist = new ArrayList<IClass>();
		Collections.addAll(clist,children);
		clist.removeAll(Arrays.asList(parents));
		
		return clist.toArray(new IClass[]{});

	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof IClass) {
			IClass[] parents = ((IClass) element).getDirectSuperClasses();
			if (parents.length > 0)
				return parents[0];
			else
				return null;
		} else
			return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof IClass) {
			return ((IClass) element).getDirectSubClasses().length > 0;
		} else
			return false;
		
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof IOntology) {
			return sort(((IOntology) inputElement).getRootClasses());
		} else {
			logger.error("Input not an IOntology");
			return new Object[] {};
		}
	}

	private Object[] sort(IClass[] classes) {
		logger.info(classes.length);
		Arrays.sort(classes, new Comparator<IClass>(){

			@Override
			public int compare(IClass o1, IClass o2) {
				return o1.getName().compareTo(o2.getName());
			}
			
		});
		return classes;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}
}
