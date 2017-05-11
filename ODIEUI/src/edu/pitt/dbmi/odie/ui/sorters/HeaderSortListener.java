package edu.pitt.dbmi.odie.ui.sorters;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * This class handles selections of the column headers. Selection of the column
 * header will cause resorting of the shown tasks using that column's sorter.
 * Repeated selection of the header will toggle sorting order (ascending versus
 * descending).
 */
public class HeaderSortListener extends SelectionAdapter {
	private MultiColumnSorter comparator;
	private ColumnViewer viewer;

	public HeaderSortListener(TableViewer viewer, MultiColumnSorter comparator) {
		assert viewer != null;
		assert comparator != null;

		this.viewer = viewer;
		this.comparator = comparator;
	}

	public HeaderSortListener(TreeViewer viewer, MultiColumnSorter comparator) {
		assert viewer != null;
		assert comparator != null;

		this.viewer = viewer;
		this.comparator = comparator;
	}

	/**
	 * Handles the case of user selecting the header area.
	 * <p>
	 * If the column has not been selected previously, it will set the sorter of
	 * that column to be the current tasklist sorter. Repeated presses on the
	 * same column header will toggle sorting order (ascending/descending).
	 */
	public void widgetSelected(SelectionEvent e) {
		// column selected - need to sort
		// System.out.println("Header clicked");

		int column = 0;

		if (viewer instanceof TableViewer)
			column = ((TableViewer) viewer).getTable().indexOf(
					(TableColumn) e.widget);
		else if (viewer instanceof TreeViewer) {
			TreeColumn[] columns = ((TreeViewer) viewer).getTree().getColumns();
			for (int i = 0; i < columns.length; i++) {
				if (columns[i] == e.widget) {
					column = i;
					break;
				}
			}
		}

		if (column == comparator.getTopPriority()) {
			comparator.reverseTopPriority();
		} else {
			comparator.setTopPriority(column);
		}
		viewer.refresh();
	}
}
