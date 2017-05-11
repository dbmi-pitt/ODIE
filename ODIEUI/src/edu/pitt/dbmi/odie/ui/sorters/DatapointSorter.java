package edu.pitt.dbmi.odie.ui.sorters;

import edu.pitt.dbmi.odie.model.Datapoint;

public class DatapointSorter extends MultiColumnSorter<Datapoint> {

	public DatapointSorter(int[] columnOrder) {
		super(columnOrder);
		// TODO Auto-generated constructor stub
	}

	public static final int NAME = 0;
	public static final int SOURCE = 1;
	public static final int FREQUENCY = 2;

	final static int[] DEFAULT_PRIORITIES = { SOURCE, FREQUENCY, NAME };
	final static int[] DEFAULT_DIRECTIONS = { ASCENDING, // name
			ASCENDING, // source
			DESCENDING // freq.
	};

	@Override
	protected int compare(Datapoint o1, Datapoint o2, int columnNumber) {
		switch (columnNumber) {
		case NAME:
			return getNameOrder(o1, o2);
		case SOURCE:
			return getSourceOrder(o1, o2);
		case FREQUENCY:
			return (int) getFrequencyOrder(o1, o2);
		default:
			return 0;
		}
	}

	@Override
	protected int[] getDefaultDirections() {
		return DEFAULT_DIRECTIONS;
	}

	@Override
	protected int[] getDefaultPriorities() {
		return DEFAULT_PRIORITIES;
	}

	/**
	 * Compares the concept names of two datapoints
	 */
	private int getNameOrder(Datapoint m1, Datapoint m2) {
		if (m1.getConceptClass() != null && m2.getConceptClass() != null)
			return m1.getConceptClass().getName().compareTo(
					m2.getConceptClass().getName());
		else
			return m1.getConceptURIString().compareTo(m2.getConceptURIString());
	}

	private int getSourceOrder(Datapoint dp1, Datapoint dp2) {
		if (dp1.getOntology() != null && dp2.getOntology() != null)
			return dp1.getOntology().getName().compareTo(
					dp2.getOntology().getName());
		else
			return dp1.getOntologyURIString().compareTo(
					dp2.getOntologyURIString());
	}

	private long getFrequencyOrder(Datapoint m1, Datapoint m2) {
		return m1.getDocumentFrequency() - m2.getDocumentFrequency();
	}
}
