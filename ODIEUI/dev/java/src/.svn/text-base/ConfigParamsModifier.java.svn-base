package edu.pitt.dbmi.odie.ui.editors;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ICellModifier;

import edu.pitt.dbmi.odie.ui.editors.providers.ConfigParamsTableLabelProvider;
import edu.pitt.dbmi.odie.ui.editors.providers.ConfigParamsTableContentProvider.ConfigParameterWithAEM;

public class ConfigParamsModifier implements ICellModifier {
	Logger logger = Logger.getLogger(this.getClass());
	
	@Override
	public boolean canModify(Object element, String property) {
		boolean isValueColumn = property.equals(ConfigParametersSection.columnHeaders[1]);
		return isValueColumn;
	}

	@Override
	public Object getValue(Object element, String property) {
		if(property.equals(ConfigParametersSection.columnHeaders[1])){
			return ConfigParamsTableLabelProvider.getValue((ConfigParameterWithAEM)element);
		}
		return null;
	}

	@Override
	public void modify(Object element, String property, Object value) {
		ConfigParameterWithAEM cp = (ConfigParameterWithAEM)element;
		
		if(property.equals(ConfigParametersSection.columnHeaders[1])){
			setValue(cp,value);
		}
	}

	private void setValue(ConfigParameterWithAEM cp, Object value) {
		if(cp.cg != null){
			logger.error("Config params within groups not supported yet");
			return;
		}
		
		cp.aem.getConfigurationParameterSettings().setParameterValue(cp.cp.getName(), value);
	}

}
