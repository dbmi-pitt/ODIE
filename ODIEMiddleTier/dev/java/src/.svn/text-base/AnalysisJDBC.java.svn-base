/**
 * 
 */
package edu.pitt.dbmi.odie.model;

import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import edu.pitt.dbmi.odie.middletier.MiddleTierJDBC;
import edu.pitt.dbmi.odie.model.DatapointComparator.SortOrder;
import edu.pitt.dbmi.odie.model.utils.StatisticsUpdater;

/**
 * @author Girish Chavan
 * 
 */

@Deprecated
public class AnalysisJDBC extends Analysis {
	Logger logger = Logger.getLogger(this.getClass());
	public AnalysisJDBC() {
		super();
		// TODO Auto-generated constructor stub
	}

	public boolean isLoaded(){
		return isStatisticsFetched();
	}
	
	private boolean isStatisticsFetched() {
		return (super.getStatistics()!=null);
	}

	private void findAndSetProposalOntology() {
		for (AnalysisLanguageResource ao : analysisLanguageResources) {
			if (ao.isProposalOntology()) {
				setProposalOntology(ao);
				break;
			}
		}
	}

	private void lazyFetchAnalysisLanguageResources() {
		if (analysisLanguageResources == null) {
			logger.debug("Lazy fetching alr");
			// assumes that the singleton is already instantiated.
			MiddleTierJDBC mt = MiddleTierJDBC.getInstance(null);
			analysisLanguageResources = mt.getAnalysisLanguageResources(this);
			findAndSetProposalOntology();
		}

	}

	/**
	 * 
	 */
	private void lazyFetchDocumentAnalysis() {
		if (analysisDocuments == null) {
			logger.debug("Lazy fetching document analysis");
			// assumes that the singleton is already instantiated.
			MiddleTierJDBC mt = MiddleTierJDBC.getInstance(null);
			analysisDocuments = mt.getDocumentAnalyses(this);
		}
	}

	/**
	 * 
	 */
	private void lazyFetchStatistics() {
		if (statistics == null) {
			statistics = new Statistics();
			logger.debug("Lazy fetching statistics");

			logger.debug("Loading annotations from database");
			SortedSet<Annotation> annlist = getAllAnnotations();

			logger.debug("Updating Statistics");
			updateStatisticsUsingAnnotations(annlist);
		}
	}

	private void updateStatisticsUsingAnnotations(SortedSet<Annotation> annlist) {
		StatisticsUpdater su = new StatisticsUpdater(statistics);
		su.setTrackSuggestions(this.getType().equals(AnalysisEngineMetadata.TYPE_OE));
		su.updateStatistics(annlist);
		if (su.isTrackSuggestions()) {
			logger.debug("Calculating weights for suggestions");
			su.redoSuggestionsRanking();
		}
		Collections.sort(statistics.getDatapoints(),
				new DatapointComparator(SortOrder.ONTOLOGY_FREQUENCY,
						statistics));
	}

	private SortedSet<Annotation> getAllAnnotations() {
		SortedSet<Annotation> annlist = new TreeSet<Annotation>();
		for (AnalysisDocument da : getAnalysisDocuments(AnalysisDocument.STATUS_DONE)) {
			annlist.addAll(da.getAnnotations());
		}
		return annlist;
	}

	@Override
	public List<AnalysisLanguageResource> getAnalysisLanguageResources() {
		lazyFetchAnalysisLanguageResources();
		return super.getAnalysisLanguageResources();
	}

	@Override
	public List<AnalysisDocument> getAnalysisDocuments() {
		lazyFetchDocumentAnalysis();
		return super.getAnalysisDocuments();
	}

	@Override
	public List<AnalysisDocument> getAnalysisDocuments(String status) {
		return super.getAnalysisDocuments(status);
	}

	@Override
	public AnalysisLanguageResource getProposalOntology() {
		lazyFetchAnalysisLanguageResources();
		return super.getProposalOntology();
	}

	@Override
	public Statistics getStatistics() {
		lazyFetchStatistics();
		return super.getStatistics();
	}

	@Override
	public void setStatistics(Statistics statistics) {
		super.setStatistics(statistics);
	}

}
