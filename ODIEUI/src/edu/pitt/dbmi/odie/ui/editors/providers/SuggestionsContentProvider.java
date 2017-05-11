package edu.pitt.dbmi.odie.ui.editors.providers;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.model.Suggestion;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.ui.views.providers.ItemCountProvider;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class SuggestionsContentProvider implements ITreeContentProvider,
		ItemCountProvider {

	Logger logger = Logger.getLogger(this.getClass());

	Analysis currentAnalysis;
	AnalysisDocument currentAnalysisDocument;

	boolean analysisMode = true;

	@Override
	public Object[] getElements(Object inputElement) {
		return suggestionMap.values().toArray();
	}

	HashMap<String, Suggestion> suggestionMap = new HashMap<String, Suggestion>();

	private void init(Analysis analysis) {
		analysisMode = true;
		List<Suggestion> sugList = GeneralUtils
				.getAggregatedSuggestions(analysis);
		itemCount = sugList.size();
		suggestionMap.clear();

		for (Suggestion s : sugList) {
			suggestionMap.put(s.getNerNegative(), s);
		}

		currentAnalysis = analysis;
	}

	private void init(AnalysisDocument ad) {
		analysisMode = false;

		if (ad.getSuggestions() == null)
			ad.setSuggestions(Activator.getDefault().getMiddleTier()
					.getSuggestions(ad,
							GeneralUtils.getSuggestions(ad.getAnalysis())));

		suggestionMap = ad.getUniqueSuggestions();
		itemCount = suggestionMap.values().size();

		currentAnalysisDocument = ad;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object inputElement) {
		if (inputElement == null) {
			currentAnalysis = null;
			currentAnalysisDocument = null;
			return;
		}
		if (inputElement instanceof Analysis) {
			init((Analysis) inputElement);
		} else if (inputElement instanceof AnalysisDocument) {
			init((AnalysisDocument) inputElement);
		}
	}

	long itemCount = 0;

	@Override
	public long getItemCount() {
		return itemCount;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Suggestion
				&& ((Suggestion) parentElement).isAggregate()) {
			Suggestion s = (Suggestion) parentElement;
			if (analysisMode)
				return GeneralUtils.getSuggestionsForAggregate(s).toArray();
			else
				return currentAnalysisDocument.getSuggestionsForAggregate(s)
						.toArray();
		}
		return new Object[] {};
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof Suggestion
				&& !((Suggestion) element).isAggregate()) {
			Suggestion s = (Suggestion) element;
			return suggestionMap.get(s.getNerNegative());
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof Suggestion)
			return ((Suggestion) element).isAggregate();

		return false;
	}
}
