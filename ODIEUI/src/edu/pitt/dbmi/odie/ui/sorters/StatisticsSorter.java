package edu.pitt.dbmi.odie.ui.sorters;

import java.net.URI;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.Statistics;

public class StatisticsSorter extends MultiColumnSorter<Statistics> {

	public StatisticsSorter(int[] columnOrder) {
		super(columnOrder);
		// TODO Auto-generated constructor stub
	}
	public static final String TOTAL = "TOTAL";
	public static final int NAME = 10;
	public static final int UNIQUECONCEPTS = 11;
	public static final int COVERAGE = 12;

	final static int[] DEFAULT_PRIORITIES = {UNIQUECONCEPTS, COVERAGE, NAME};
	final static int[] DEFAULT_DIRECTIONS = { DESCENDING, DESCENDING, ASCENDING };

	@Override
	protected int compare(Statistics o1, Statistics o2, int column) {

		switch (column) {
		case UNIQUECONCEPTS:
			return getUniqueConceptsOrder(o1, o2);
		case COVERAGE:
			return getCoverageOrder(o1, o2);
		case NAME:
			return getOntologyNameOrder(o1, o2);
		default:
			return 0;
		}
	}

	private int getOntologyNameOrder(Statistics o1, Statistics o2) {
		String n1 = getName(o1);		
		String n2 = getName(o2);
		
		if(n1 == TOTAL)
			return -1;
		else if(n2 == TOTAL)
			return +1;
		else
			return n1.compareTo(n2);
	}

	private String getName(Statistics s) {
		if (s.context instanceof URI) {
			URI ouri = (URI) s.context;
			return ouri.toASCIIString().substring(ouri.toASCIIString().lastIndexOf("/") + 1);
		} 
		else if (s.context instanceof Analysis)
			return TOTAL;
		else 
			return "ZZZ"; 
	}

	private int getCoverageOrder(Statistics o1, Statistics o2) {
		String n1 = getName(o1);		
		String n2 = getName(o2);
		
		if(n1 == TOTAL || n2 == TOTAL)
			return getOntologyNameOrder(o1,o2);
		
		return Float.compare(o1.getCoverage(), o2.getCoverage());
	}

	private int getUniqueConceptsOrder(Statistics o1, Statistics o2) {
		String n1 = getName(o1);		
		String n2 = getName(o2);
		
		if(n1 == TOTAL || n2 == TOTAL)
			return getOntologyNameOrder(o1,o2);
		
		return Float.compare(o1.uniqueConceptsCount,o2.uniqueConceptsCount);
	}

	@Override
	protected int[] getDefaultDirections() {
		return DEFAULT_DIRECTIONS;
	}

	@Override
	protected int[] getDefaultPriorities() {
		return DEFAULT_PRIORITIES;
	}

}
