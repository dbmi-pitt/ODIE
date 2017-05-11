package edu.pitt.dbmi.odie.ui.editors.providers;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.metadata.AnalysisEngineMetaData;
import org.apache.uima.resource.metadata.ConfigurationGroup;
import org.apache.uima.resource.metadata.ConfigurationParameter;
import org.apache.uima.resource.metadata.ConfigurationParameterDeclarations;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ConfigParamsTableContentProvider implements
		IStructuredContentProvider {

	class ConfigParameterWithAEM {
		AnalysisEngineMetaData aem;
		ConfigurationParameter cp;
		ConfigurationGroup cg;
		
		public ConfigParameterWithAEM(AnalysisEngineMetaData aem, ConfigurationParameter cp) {
			this(aem,cp,null);
		}

		public ConfigParameterWithAEM(AnalysisEngineMetaData aem,
				ConfigurationParameter cp, ConfigurationGroup cg) {
			super();
			this.aem = aem;
			this.cp = cp;
			this.cg = cg;
		}
		
		
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
		List<ConfigParameterWithAEM> cpList = new ArrayList<ConfigParameterWithAEM>();
		
		if(inputElement instanceof AnalysisEngineMetaData){
			
			AnalysisEngineMetaData aem = (AnalysisEngineMetaData)inputElement;
			
			ConfigurationParameterDeclarations cpd = aem.getConfigurationParameterDeclarations();
			
			for(ConfigurationGroup cg:cpd.getConfigurationGroups()){
				for(ConfigurationParameter cp:cg.getConfigurationParameters())
					cpList.add(new ConfigParameterWithAEM(aem, cp, cg));
			}
			
			for(ConfigurationParameter cp:cpd.getConfigurationParameters())
				cpList.add(new ConfigParameterWithAEM(aem, cp));
			
			return cpList.toArray();
			
		}
		return new Object[]{};
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
