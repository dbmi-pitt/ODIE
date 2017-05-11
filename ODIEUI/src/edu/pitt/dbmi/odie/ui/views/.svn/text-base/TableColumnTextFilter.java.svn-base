/**
 * @author Girish Chavan
 *
 * Oct 17, 2008
 */
package edu.pitt.dbmi.odie.ui.views;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class TableColumnTextFilter extends ViewerFilter {
	private String filterText = "";
	private int columnIndex = 0;

	public TableColumnTextFilter(int columnIndex) {
		super();
		this.columnIndex = columnIndex;
	}

	public String getFilterText() {
		return filterText;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (filterText.length() > 0) {
			String name = ((ITableLabelProvider) ((TableViewer) viewer)
					.getLabelProvider()).getColumnText(element, columnIndex);
			return (name.toLowerCase().indexOf(this.filterText.toLowerCase()) > -1);
		} else
			return true;
	}

	public void setFilterText(String filterText) {
		this.filterText = filterText;
	}

}