/**
 *@author Girish Chavan
 *Sep 6, 2008 
 *
 */
package edu.pitt.dbmi.odie.model.utils;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;

import org.apache.log4j.Logger;

import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.model.Annotation;
import edu.pitt.dbmi.odie.model.Datapoint;
import edu.pitt.dbmi.odie.model.DatapointComparator;
import edu.pitt.dbmi.odie.model.Statistics;
import edu.pitt.dbmi.odie.model.DatapointComparator.SortOrder;
import edu.pitt.ontology.IClass;

public class StatisticsUpdater {

	private HashMap<URI, Datapoint> datapointMap = new HashMap<URI, Datapoint>();
	Logger logger = Logger.getLogger(this.getClass());
	private boolean trackSuggestions;

	Statistics statistics;
	SuggestionTracker tracker = new SuggestionTracker();

	public DatapointComparator ontoAndFreqComparator = new DatapointComparator(
			SortOrder.ONTOLOGY_FREQUENCY);

	public StatisticsUpdater(Statistics statistics) {
		super();
		this.statistics = statistics;
	}

	public boolean isTrackSuggestions() {
		return trackSuggestions;
	}

	/**
	 * 
	 */
	public void redoSuggestionsRanking() {
		tracker.recalculateWeights();
	}

	public void setTrackSuggestions(boolean trackSuggestions) {
		this.trackSuggestions = trackSuggestions;
	}

	public void updateStatistics(Annotation ann) {

		List datapoints;
		IClass c = ann.getAnnotationClass();
		if(c==null){
			logger.debug("StatisticsUpdater: No Annotation class found. Skipping annotation");
			return;
		}
		
		if (ann.getAnnotationClass().getOntology().getURI().toString().equals(
				MiddleTier.CONCEPT_DISCOVERY_ONTOLOGY_URI))
			datapoints = statistics.getSystemDatapoints();
		else
			datapoints = statistics.getDatapoints();

		Datapoint dp = datapointMap.get(ann.getAnnotationClassURI());
		if (dp == null) {
			dp = new Datapoint(ann.getAnnotationClassURI(), ann
					.getDocumentAnalysis());
			datapointMap.put(ann.getAnnotationClassURI(), dp);
		} else {
			dp.getDocumentAnalyses().add(ann.getDocumentAnalysis());
			//remove datapoint so that it can be reinserted in the correct place
			datapoints.remove(dp);
		}

		// Search for the item's location in the list
		int index = Collections.binarySearch(datapoints, dp,
				ontoAndFreqComparator);

		if (!datapoints.contains(dp)) {
			if (index < 0) {
				datapoints.add(-index - 1, dp);
			} else {
				datapoints.add(index, dp);
			}
		}

		if (trackSuggestions) {
			tracker.addAnnotation(ann);
			statistics.setPhraseSuggestions(tracker.getPhraseSuggestions());
			statistics.setWordSuggestions(tracker.getWordSuggestions());
		}

	}

	public void updateStatistics(SortedSet<Annotation> annlist) {
		int count = 0;
		int size = annlist.size();
		for (Annotation ann : annlist) {
			count++;
			logger.debug("Updating statistics for annotation " + count
					+ "/" + size);
			updateStatistics(ann);
		}
	}
}
