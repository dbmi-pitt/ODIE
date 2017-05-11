package edu.pitt.dbmi.odie.ui.sorters;

import edu.pitt.dbmi.odie.model.Suggestion;

public class SuggestionSorter extends MultiColumnSorter<Suggestion> {

	public SuggestionSorter(int[] columnOrder) {
		super(columnOrder);
		// TODO Auto-generated constructor stub
	}

	public static final int SUGGESTION = 10;
	public static final int SCORE = 11;
	public static final int METHOD = 12;
	public static final int INFO = 13;
	public static final int RELATEDCONCEPT = 14;

	public static final int NEWCONCEPT = 15;
	public static final int GOODSUGGESTION = 16;

	final static int[] DEFAULT_PRIORITIES = { SCORE, SUGGESTION, METHOD,
			RELATEDCONCEPT, INFO };
	final static int[] DEFAULT_DIRECTIONS = { DESCENDING, ASCENDING, ASCENDING,
			ASCENDING, ASCENDING };

	@Override
	protected int compare(Suggestion o1, Suggestion o2, int column) {

		switch (column) {
		case GOODSUGGESTION:
			return getGoodSuggestionOrder(o1, o2);
		case SUGGESTION:
			return getSuggestionOrder(o1, o2);
		case RELATEDCONCEPT:
			return getRelatedConceptOrder(o1, o2);
		case SCORE:
			return getScoreOrder(o1, o2);
		case METHOD:
			return getMethodOrder(o1, o2);
		case INFO:
			return getInfoOrder(o1, o2);
		case NEWCONCEPT:
			return getNewConceptOrder(o1, o2);
		default:
			return 0;
		}
	}

	private int getNewConceptOrder(Suggestion o1, Suggestion o2) {
		return o1.getNewConceptName().compareTo(o2.getNewConceptName());
	}

	private int getGoodSuggestionOrder(Suggestion o1, Suggestion o2) {
		return o1.isGoodSuggestion().compareTo(o2.isGoodSuggestion());
	}

	private int getInfoOrder(Suggestion o1, Suggestion o2) {
		return o1.getRule().compareTo(o2.getRule());
	}

	private int getMethodOrder(Suggestion o1, Suggestion o2) {
		return o1.getMethod().compareTo(o2.getMethod());
	}

	private int getScoreOrder(Suggestion o1, Suggestion o2) {
		return (int) (o2.getScore() * 100 - o1.getScore() * 100);
	}

	private int getRelatedConceptOrder(Suggestion o1, Suggestion o2) {
		return o1.getNerPositive().compareTo(o2.getNerPositive());
	}

	private int getSuggestionOrder(Suggestion o1, Suggestion o2) {
		return o1.getNerNegative().compareTo(o2.getNerNegative());
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
