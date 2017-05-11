package edu.pitt.dbmi.odie.ui.editors.providers;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.resource.metadata.ConfigurationGroup;
import org.apache.uima.resource.metadata.ConfigurationParameter;
import org.apache.uima.resource.metadata.ConfigurationParameterDeclarations;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ConfigParamsTableContentProvider implements
		IStructuredContentProvider {

	public class ConfigParameterWithValue {
		public ConfigurationParameter cp;
		public ConfigurationGroup cg;
		public Object value;

		public ConfigParameterWithValue(ConfigurationParameter cp, Object value) {
			this(cp, null, value);
		}

		public ConfigParameterWithValue(ConfigurationParameter cp,
				ConfigurationGroup cg, Object value) {
			super();
			this.value = value;
			this.cp = cp;
			this.cg = cg;
		}
	}

	Logger logger = Logger.getLogger(this.getClass());

	@Override
	public Object[] getElements(Object inputElement) {
		List<ConfigParameterWithValue> cpList = new ArrayList<ConfigParameterWithValue>();

		if (inputElement instanceof AnalysisEngine) {

			AnalysisEngine ae = (AnalysisEngine) inputElement;
			ConfigurationParameterDeclarations cpd = ae
					.getAnalysisEngineMetaData()
					.getConfigurationParameterDeclarations();

			for (ConfigurationGroup cg : cpd.getConfigurationGroups()) {
				logger.error("Config params within groups not supported yet");
				// for(ConfigurationParameter
				// cp:cg.getConfigurationParameters())
				// cpList.add(new ConfigParameterWithAEM(aem, cp, cg));
			}

			for (ConfigurationParameter cp : cpd.getConfigurationParameters())
				cpList.add(new ConfigParameterWithValue(cp, ae
						.getConfigParameterValue(cp.getName())));

			return cpList.toArray();

		}
		return new Object[] {};
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

}
