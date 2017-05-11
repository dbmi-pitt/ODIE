package edu.pitt.dbmi.odie.ui.views.filters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.Datapoint;

public class DatapointsFilter extends ViewerFilter {

	private List<Datapoint> datapointSelection = new ArrayList<Datapoint>();

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof Datapoint) {
			if (datapointSelection.isEmpty())
				return true;

			if (datapointSelection.contains(element))
				return true;
			else
				return false;
		}

		return true;
	}

	public void setSelection(List<Datapoint> selectedDatapoints) {
		datapointSelection.clear();
		for (Datapoint dp : selectedDatapoints)
			datapointSelection.add(dp);
	}

	public void clearSelection() {
		datapointSelection.clear();

	}

	public Analysis getSelectionAnalysis() {
		if(!datapointSelection.isEmpty())
			return datapointSelection.get(0).getAnalysis();
		
		return null;
	}
}
