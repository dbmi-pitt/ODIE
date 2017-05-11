package edu.pitt.dbmi.odie.ui.views;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TableColumn;

/**
 * This class handles selections of the column headers. Selection of the column
 * header will cause resorting of the shown tasks using that column's sorter.
 * Repeated selection of the header will toggle sorting order (ascending versus
 * descending).
 */
public class TableHeaderSortListener extends SelectionAdapter {
	TableViewer viewer;

	public TableHeaderSortListener(TableViewer viewer) {
		super();
		this.viewer = viewer;
	}

	/**
	 * Handles the case of user selecting the header area.
	 * <p>
	 * If the column has not been selected previously, it will set the sorter of
	 * that column to be the current tasklist sorter. Repeated presses on the
	 * same column header will toggle sorting order (ascending/descending).
	 */
	public void widgetSelected(SelectionEvent e) {

		IPrioritizedSorter comparator = (IPrioritizedSorter) viewer
				.getComparator();
		// column selected - need to sort
		int column = viewer.getTable().indexOf((TableColumn) e.widget);
		if (column == comparator.getTopPriority()) {
			comparator.reverseTopPriority();
		} else {
			comparator.setTopPriority(column);
		}
		viewer.refresh();
	}
}