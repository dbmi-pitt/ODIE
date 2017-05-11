package edu.pitt.dbmi.odie.ui.views.filters;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.model.Datapoint;
import edu.pitt.dbmi.odie.model.Suggestion;
import edu.pitt.dbmi.odie.ui.Activator;

public class DocumentsFilter extends ViewerFilter {
	private List<AnalysisDocument> documentSelection = new ArrayList<AnalysisDocument>();

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof AnalysisDocument) {
			if (documentSelection.isEmpty())
				return true;
			if (documentSelection.contains(element)) {
				return true;
			} else
				return false;
		}

		return true;
	}

	public void setDatapointSelection(List<Datapoint> selectedDatapoints) {
		documentSelection.clear();

		for (Datapoint dp : selectedDatapoints)
			documentSelection.addAll(dp.getAnalysisDocuments());
	}

	public void setSuggestionSelection(List<Suggestion> selectedSuggestions) {
		documentSelection.clear();

		for (Suggestion s : selectedSuggestions) {
			if (s.getAnalysisDocuments() == null)
				s.setAnalysisDocuments(Activator.getDefault().getMiddleTier()
						.getDocumentsContainingSuggestion(s));

			documentSelection.addAll(s.getAnalysisDocuments());
		}
	}

	public void clearSelection() {
		documentSelection.clear();
	}
}
