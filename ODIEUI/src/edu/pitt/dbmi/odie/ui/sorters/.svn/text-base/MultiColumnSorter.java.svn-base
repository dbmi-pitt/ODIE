package edu.pitt.dbmi.odie.ui.sorters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

public abstract class MultiColumnSorter<T> extends ViewerComparator {

	private int[] priorities;
	private int[] directions;
	private int[] columnOrder;

	public static final int ASCENDING = 1;
	public static final int DESCENDING = -1;
	final static int DEFAULT_DIRECTION = 0;

	protected abstract int[] getDefaultPriorities();

	protected abstract int[] getDefaultDirections();

	/**
	 * Creates a new task sorter.
	 */
	public MultiColumnSorter(int[] columnOrder) {
		setColumnOrder(columnOrder);
	}

	public int[] getColumnOrder() {
		return columnOrder;
	}

	public void setColumnOrder(int[] columnOrder) {
		this.columnOrder = columnOrder;
		resetState();
	}

	public void resetState() {
		int[] defaultPriorities = getDefaultPriorities();
		int[] defaultDirections = getDefaultDirections();

		priorities = new int[columnOrder.length];
		directions = new int[columnOrder.length];
		int x = 0;
		for (int i = 0; i < defaultPriorities.length; i++) {
			int order = getColumnOrder(defaultPriorities[i]);
			if (order >= 0) {
				priorities[x] = order;
				directions[x] = defaultDirections[i];
				x++;
			}
		}
	}

	/**
	 * Compares two markers, sorting first by the main column of this sorter,
	 * then by subsequent columns, depending on the column sort order.
	 */
	public int compare(Viewer viewer, Object e1, Object e2) {
		T o1 = (T) e1;
		T o2 = (T) e2;
		return compareColumnValue(o1, o2, 0);
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
		directions[priority] = getDefaultDirections()[priority];
	}

	private int getColumn(int columnIndex) {
		if (columnIndex < 0 || columnIndex >= columnOrder.length)
			return -1;

		return columnOrder[columnIndex];
	}

	private int getColumnOrder(int column) {
		for (int i = 0; i < columnOrder.length; i++) {
			if (columnOrder[i] == column)
				return i;
		}
		return -1;
	}

	public int getTopPriority() {
		return priorities[0];
	}

	public int[] getPriorities() {
		return priorities;
	}

	public void setTopPriorityDirection(int direction) {
		if (direction == DEFAULT_DIRECTION) {
			directions[priorities[0]] = getDefaultDirection(priorities[0]);
		} else if (direction == ASCENDING || direction == DESCENDING) {
			directions[priorities[0]] = direction;
		}
	}

	private int getDefaultDirection(int columnOrder) {
		int column = getColumn(columnOrder);

		int[] defaultPriorities = getDefaultPriorities();
		int[] defaultDirections = getDefaultDirections();

		for (int i = 0; i < getDefaultPriorities().length; i++) {
			if (defaultPriorities[i] == column)
				return defaultDirections[i];
		}
		return DEFAULT_DIRECTION;
	}

	public int getTopPriorityDirection() {
		return directions[priorities[0]];
	}

	public void reverseTopPriority() {
		directions[priorities[0]] *= -1;
	}

	/**
	 * Compares two markers, based only on the value of the specified column.
	 */
	private int compareColumnValue(T o1, T o2, int depth) {
		if (depth >= priorities.length) {
			return 0;
		}

		int columnNumber = priorities[depth];
		int direction = directions[priorities[depth]];

		int result = compare(o1, o2, getColumn(columnNumber));
		if (result == 0) {
			return compareColumnValue(o1, o2, depth + 1);
		}
		return result * direction;
	}

	protected abstract int compare(T o1, T o2, int column);
}
