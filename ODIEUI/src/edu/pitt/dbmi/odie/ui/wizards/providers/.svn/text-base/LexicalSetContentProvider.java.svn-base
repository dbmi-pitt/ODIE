package edu.pitt.dbmi.odie.ui.wizards.providers;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.pitt.dbmi.odie.model.LexicalSet;
import edu.pitt.dbmi.odie.ui.Activator;

public class LexicalSetContentProvider implements IStructuredContentProvider {

	Comparator<LexicalSet> LSOntologyNamesComparator = new Comparator<LexicalSet>() {
		@Override
		public int compare(LexicalSet o1, LexicalSet o2) {
			return o1.getOntologyNames().compareTo(o2.getOntologyNames());
		}

	};

	@Override
	public Object[] getElements(Object inputElement) {
		List<LexicalSet> lsList = Activator.getDefault().getMiddleTier()
				.getLexicalSets();

		Collections.sort(lsList, LSOntologyNamesComparator);

		return lsList.toArray();

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		;

	}

}
