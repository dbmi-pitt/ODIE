package edu.pitt.dbmi.odie.ui.wizards.providers;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.viewers.Viewer;

import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.ui.editors.providers.OntologyTreeContentProvider;
import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IOntology;

public class AllOntologiesTreeContentProvider extends
		OntologyTreeContentProvider {

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IOntology)
			return super.getElements(parentElement);
		else
			return super.getChildren(parentElement);
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof IOntology)
			return null;
		else if (element instanceof IClass) {
			Object parent = super.getParent(element);
			// this means the class is a root class of the ontology, return the
			// ontology in use
			if (parent == null)
				return ((IClass) element).getOntology();
		}

		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof IOntology)
			return true;
		else
			return super.hasChildren(element);
	}

	Object root = null;
	private Object[] ontologies;

	@Override
	public Object[] getElements(Object inputElement) {
		return ontologies;
	}

	@Override
	public void dispose() {
		ontologies = null;

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		ontologies = Activator.getDefault().getMiddleTier().getRepository()
				.getOntologies();
		List olist = Arrays.asList(ontologies);

		Collections.sort(olist, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				return ((IOntology) o1).getName().compareTo(
						((IOntology) o2).getName());
			}

		});
		ontologies = olist.toArray();

	}
}
