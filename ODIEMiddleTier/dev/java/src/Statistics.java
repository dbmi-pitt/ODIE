package edu.pitt.dbmi.odie.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;

import edu.pitt.ontology.IOntology;
import edu.pitt.ontology.IResource;

/**
 * @author Girish Chavan
 * 
 */
public class Statistics {
	private List<Datapoint> datapoints = new LinkedList<Datapoint>();
	private SortedSet<Suggestion> phraseSuggestions;

	private List<Datapoint> systemDatapoints = new LinkedList<Datapoint>();
	private SortedSet<Suggestion> wordSuggestions;

	public List<Datapoint> getDatapoints() {
		return datapoints;
	}

	/**
	 * @param o
	 * @return
	 */
	public long getFrequency(IResource res) {
		List<Datapoint> dplist = getDatapoints();

		boolean checkOntology = false;
		if (res instanceof IOntology)
			checkOntology = true;

		int count = 0;
		for (Datapoint dp : dplist) {

			if (checkOntology) {
				if (dp.getConceptClass().getOntology().equals(res))
					count += dp.getFrequency();
			} else {
				if (dp.getConceptClass().equals(res))
					return dp.getFrequency();
			}
		}
		return count;
	}

	public SortedSet<Suggestion> getPhraseSuggestions() {
		return phraseSuggestions;
	}

	/**
	 * @param da
	 * @return
	 */
	public List<Suggestion> getPhraseSuggestions(AnalysisDocument da) {
		List<Suggestion> suggestionList = null;
		if (phraseSuggestions != null) {
			suggestionList = new ArrayList<Suggestion>();
			for (Suggestion s : phraseSuggestions) {
				for (Annotation ann : s.getAnnotations()) {
					if (ann.getDocumentAnalysis().equals(da)) {
						suggestionList.add(s);
						break;
					}
				}
			}
		}
		Collections.sort(suggestionList);
		return suggestionList;

	}

	public List<Datapoint> getSystemDatapoints() {
		return systemDatapoints;
	}

	/**
	 * @param o
	 * @return
	 */
	public long getUniqueConceptCount(IOntology res) {
		List<Datapoint> dplist = getDatapoints();
		int count = 0;
		for (Datapoint dp : dplist) {
			if (dp.getConceptClass().getOntology().equals(res))
				count++;
		}
		return count;
	}

	public SortedSet<Suggestion> getWordSuggestions() {
		return wordSuggestions;
	}

	public void setDatapoints(List<Datapoint> dplist) {
		datapoints = dplist;
	}

	public void setPhraseSuggestions(SortedSet<Suggestion> sortedSet) {
		this.phraseSuggestions = sortedSet;
	}

	public void setSystemDatapoints(List<Datapoint> systemDatapoints) {
		this.systemDatapoints = systemDatapoints;
	}

	public void setWordSuggestions(SortedSet<Suggestion> sortedSet) {
		this.wordSuggestions = sortedSet;
	}
}
