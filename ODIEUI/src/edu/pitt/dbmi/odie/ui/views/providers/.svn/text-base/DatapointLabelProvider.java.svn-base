package edu.pitt.dbmi.odie.ui.views.providers;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import edu.pitt.dbmi.odie.model.Datapoint;
import edu.pitt.ontology.IOntology;

public class DatapointLabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		switch (columnIndex) {
		case 0:
			String code = ((Datapoint) element).getConceptId();
			if (code == null)
				return ((Datapoint) element).getType();
			else
				return code;
		case 1: {
			IOntology ontology = ((Datapoint) element).getOntology();
			if (ontology != null)
				return ontology.getName();
			else {
				String ouri = ((Datapoint) element).getOntologyURIString();
				return ouri.substring(ouri.lastIndexOf("/") + 1);
				// return "Ontology not found";
			}
		}
		case 2:
			return "" + ((Datapoint) element).getDocumentFrequency();
		default:
			return "Invalid column index";
		}
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

}
