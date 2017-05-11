package edu.pitt.dbmi.odie.ui.views.sorters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import edu.pitt.dbmi.odie.model.Suggestion;
import edu.pitt.dbmi.odie.ui.views.IPrioritizedSorter;

public class SuggestionSorter extends ViewerComparator implements IPrioritizedSorter {

	private int[] priorities;
	private int[] directions;
	
	public static final int NAME = 0;
	public static final int WEIGHT = 1;
	public static final int FREQUENCY = 2;
	public static final int ASCENDING = 1;
	public static final int DESCENDING = -1;

	final static int[] DEFAULT_PRIORITIES = { WEIGHT, FREQUENCY, NAME };
	
	final static int DEFAULT_DIRECTION = 1;
	
	final static int[] DEFAULT_DIRECTIONS = { ASCENDING, //name
		DESCENDING //weight.
		, DESCENDING}; //freq
	/**
	 * Creates a new task sorter.
	 */
	public SuggestionSorter() {
		resetState();
	}

	/*
	 * (non-Javadoc) Method declared on ViewerSorter.
	 */
	/**
	 * Compares two markers, sorting first by the main column of this sorter,
	 * then by subsequent columns, depending on the column sort order.
	 */
	public int compare(Viewer viewer, Object e1, Object e2) {
		Suggestion m1 = (Suggestion) e1;
		Suggestion m2 = (Suggestion) e2;
		return compareColumnValue(m1, m2, 0);
	}

	public void setTopPriority(int priority) {
		if (priority < 0 || priority >= priorities.length) {
			return;
		}

		int index = -1;
		for (int i = 0; i < priorities.length; i++) {
			if (priorities[i] == priority) {
				index = i;
				break;
			}
		}

		if (index == -1) {
			resetState();
			return;
		}

		// shift the array
		for (int i = index; i > 0; i--) {
			priorities[i] = priorities[i - 1];
		}
		priorities[0] = priority;
		directions[priority] = DEFAULT_DIRECTIONS[priority];
	}

	public int getTopPriority() {
		return priorities[0];
	}

	public int[] getPriorities() {
		return priorities;
	}

	public void setTopPriorityDirection(int direction) {
		if (direction == DEFAULT_DIRECTION) {
			directions[priorities[0]] = DEFAULT_DIRECTIONS[priorities[0]];
		} else if (direction == ASCENDING || direction == DESCENDING) {
			directions[priorities[0]] = direction;
		}
	}

	public int getTopPriorityDirection() {
		return directions[priorities[0]];
	}

	public void reverseTopPriority() {
		directions[priorities[0]] *= -1;
	}

	public void resetState() {
		priorities = new int[DEFAULT_PRIORITIES.length];
		System.arraycopy(DEFAULT_PRIORITIES, 0, priorities, 0,
				priorities.length);
		directions = new int[DEFAULT_DIRECTIONS.length];
		System.arraycopy(DEFAULT_DIRECTIONS, 0, directions, 0,
				directions.length);
	}

	/*
	 * (non-Javadoc) Method declared on ViewerSorter.
	 */
	 /**
		 * Compares two markers, based only on the value of the specified
		 * column.
		 */
	private int compareColumnValue(Suggestion m1, Suggestion m2, int depth) {
		if (depth >= priorities.length) {
			return 0;
		}

		int columnNumber = priorities[depth];
		int direction = directions[columnNumber];
		switch (columnNumber) {
		case NAME: {
			/* category */
			int result = getNameOrder(m1,m2);
			if (result == 0) {
				return compareColumnValue(m1, m2, depth + 1);
			}
			return result * direction;
		}
		case FREQUENCY: {
			/* priority */
			long result = getFrequencyOrder(m1,m2);
			if (result == 0) {
				return compareColumnValue(m1, m2, depth + 1);
			}
			return (int)result * direction;
		}
		case WEIGHT: {
			/* priority */
			long result = getWeightOrder(m1,m2);
			if (result == 0) {
				return compareColumnValue(m1, m2, depth + 1);
			}
			return (int)result * direction;
		}
		default:
			return 0;
		}
	}

	/**
	 * Compares the concept names of two datapoints
	 */
	private int getNameOrder(Suggestion m1, Suggestion m2) {
		return m1.getText().compareTo(m2.getText());
	}
	
	private long getFrequencyOrder(Suggestion m1, Suggestion m2) {
		return m1.getFrequency()-m2.getFrequency();
	}

	private long getWeightOrder(Suggestion m1, Suggestion m2) {
		return (long) (m1.getWeight()-m2.getWeight());
	}
}
