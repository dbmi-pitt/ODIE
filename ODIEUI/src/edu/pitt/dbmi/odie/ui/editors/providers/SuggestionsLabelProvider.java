package edu.pitt.dbmi.odie.ui.editors.providers;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import edu.pitt.dbmi.odie.model.Suggestion;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.ui.editors.providers.ConfigParamsTableContentProvider.ConfigParameterWithValue;
import edu.pitt.ontology.IClass;

public class SuggestionsLabelProvider implements ITableLabelProvider {
	static Logger logger = Logger.getLogger(SuggestionsLabelProvider.class);

	// private static final Image CHECKED = Activator.getImageDescriptor(
	// "images/checked.png").createImage();
	//	
	// private static final Image UNCHECKED = Activator.getImageDescriptor(
	// "images/unchecked.png").createImage();

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// if(columnIndex == 0){
		// if(((Suggestion)element).isGoodSuggestion())
		// return CHECKED;
		// else
		// return UNCHECKED;
		// }
		// else
		return null;
	}

	public static final String clickMessage = "(click to edit)";

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof Suggestion) {
			Suggestion sugg = (Suggestion) element;

			switch (columnIndex) {
			case 0:
				return sugg.getNerNegative();
			case 1:
				return sugg.getScore().toString();
			}

			if (!sugg.isAggregate()) {
				switch (columnIndex) {
				case 2:
					IClass concept = sugg.getConceptContextClass();
					if (concept == null && sugg.getNerPositive() != null) {
						sugg.setConceptContextClass(getConceptClass(sugg
								.getNerPositive()));
						concept = sugg.getConceptContextClass();
					}

					if (concept != null) {
						return concept.getName() + " ("
								+ concept.getOntology().getName() + ")";
					}
					return sugg.getNerPositive();
				case 3:
					return sugg.getMethod();
				case 4:
					return sugg.getRule();

				default:
					return "incorrect column";
				}
			} else {
				return "";
			}

		}
		return "object not ConfigParameterWithAEM";
	}

	private IClass getConceptClass(String nerPositive) {
		IClass c = null;
		try {
			c = (IClass) Activator.getDefault().getMiddleTier()
					.getResourceForURI(new URI(nerPositive));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return c;
	}

	public static String getValue(ConfigParameterWithValue cp) {
		return (cp.value == null ? "" : cp.value.toString());
	}

	private static String getValueForGroupedParameter(
			ConfigParameterWithValue cp) {
		logger.error("Config params within groups not supported yet");
		return "";
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
