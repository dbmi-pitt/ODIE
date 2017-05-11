/**
 * 
 */
package edu.pitt.dbmi.odie.ui.views;

import java.util.Comparator;

import edu.pitt.dbmi.odie.model.Datapoint;
import edu.pitt.ontology.IOntology;

/**
 * @author Girish Chavan
 * 
 */
public class DatapointComparator implements Comparator<Datapoint> {

	public enum SortOrder {
		FREQUENCY_ONLY, ONTOLOGY_DOCFREQUENCY, ONTOLOGY_OCCURENCES, ONTOLOGY_ONLY
	}

	boolean sortOntByFreq = false;

	public SortOrder order;

	public DatapointComparator(SortOrder order) {
		super();
		this.order = order;
		sortOntByFreq = false;
	}

	/**
	 * @param o1
	 * @param o2
	 * 
	 * @return
	 */
	private int compareFrequency(Datapoint dp1, Datapoint dp2) {
		return (int) (dp1.getDocumentFrequency() - dp2.getDocumentFrequency());
	}

	private int compareOccurences(Datapoint dp1, Datapoint dp2) {
		return (int) (dp1.getOccurences() - dp2.getOccurences());
	}

	/**
	 * @param dp1
	 * @param dp2
	 * @return
	 */
	private int compareOntology(Datapoint dp1, Datapoint dp2) {
		// IOntology o1 = dp1.getOntology();
		// IOntology o2 = dp2.getOntology();
		//
		// if (o1 == null || o2 == null)
		// return 0;

		if (sortOntByFreq) {
			OntologyCoverageComparator c = new OntologyCoverageComparator(dp1
					.getAnalysis());
			return c.compare(dp1.getOntologyURIString(), dp2
					.getOntologyURIString());
		} else {
			return dp1.getOntologyURIString().compareTo(
					dp2.getOntologyURIString());
		}
	}

	/**
	 * @param dp1
	 * @param dp2
	 * @return
	 */
	private int compareOntologyFrequency(Datapoint dp1, Datapoint dp2) {
		int i = compareOntology(dp1, dp2);

		return (i == 0 ? compareFrequency(dp1, dp2) : i);
	}

	private int compareOntologyOccurences(Datapoint dp1, Datapoint dp2) {
		int i = compareOntology(dp1, dp2);

		return (i == 0 ? compareOccurences(dp1, dp2) : i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Datapoint o1, Datapoint o2) {
		int i = o1.getType().compareTo(o2.getType());

		if (i != 0)
			return i;

		switch (order) {
		case FREQUENCY_ONLY:
			return -compareFrequency(o1, o2);
		case ONTOLOGY_ONLY:
			return -compareOntology(o1, o2);
		case ONTOLOGY_DOCFREQUENCY:
			return -compareOntologyFrequency(o1, o2);
		default:
			return -compareOntologyOccurences(o1, o2);
		}
	}

}
