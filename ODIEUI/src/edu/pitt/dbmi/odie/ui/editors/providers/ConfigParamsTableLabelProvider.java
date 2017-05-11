package edu.pitt.dbmi.odie.ui.editors.providers;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import edu.pitt.dbmi.odie.ui.editors.providers.ConfigParamsTableContentProvider.ConfigParameterWithValue;

public class ConfigParamsTableLabelProvider implements ITableLabelProvider {
	static Logger logger = Logger
			.getLogger(ConfigParamsTableLabelProvider.class);

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof ConfigParameterWithValue) {
			ConfigParameterWithValue cp = (ConfigParameterWithValue) element;
			switch (columnIndex) {
			case 0:
				return cp.cp.getName();
			case 1:
				return getValue(cp);
			case 2:
				return cp.cp.getType();
			default:
				return "incorrect column";

			}
		}
		return "object not ConfigParameterWithAEM";
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
