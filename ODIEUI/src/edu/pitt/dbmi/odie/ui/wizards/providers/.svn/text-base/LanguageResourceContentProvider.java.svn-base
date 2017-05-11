package edu.pitt.dbmi.odie.ui.wizards.providers;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.pitt.dbmi.odie.model.LanguageResource;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.ontology.IRepository;

public class LanguageResourceContentProvider implements IStructuredContentProvider {

	Comparator<LanguageResource> LSOntologyNamesComparator = new Comparator<LanguageResource>() {
		@Override
		public int compare(LanguageResource o1, LanguageResource o2) {
			return o1.getName().compareTo(o2.getName());
		}

	};

	@Override
	public Object[] getElements(Object inputElement) {
		List<LanguageResource> lsList = Activator.getDefault().getMiddleTier()
				.getLanguageResources(IRepository.TYPE_ONTOLOGY);

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
