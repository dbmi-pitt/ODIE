package edu.pitt.dbmi.odie.ui.editors.providers;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import edu.pitt.dbmi.odie.ui.editors.providers.ConfigParamsTableContentProvider.ConfigParameterWithAEM;

public class ConfigParamsTableLabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if(element instanceof ConfigParameterWithAEM){
			ConfigParameterWithAEM cp = (ConfigParameterWithAEM)element;
			switch(columnIndex){
			case 0:
				return cp.cp.getName();
			case 1:
				if(cp.cg!=null){
					return getValueForGroupedParameter(cp);
				}
				return cp.aem.getConfigurationParameterSettings().getParameterValue(cp.cp.getName()).toString();
			default:
				return "incorrect column";
					
			}
		}
		return "object not ConfigParameterWithAEM";
	}

	private String getValueForGroupedParameter(ConfigParameterWithAEM cp) {
		return null;
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
