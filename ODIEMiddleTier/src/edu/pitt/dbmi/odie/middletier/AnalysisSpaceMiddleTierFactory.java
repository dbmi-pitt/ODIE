package edu.pitt.dbmi.odie.middletier;

import java.util.HashMap;

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.dbmi.odie.model.Analysis;

public class AnalysisSpaceMiddleTierFactory {

	private Configuration configuration;
	
	HashMap<Analysis,AnalysisSpaceMiddleTier> analysisMTMap = new HashMap<Analysis,AnalysisSpaceMiddleTier>();
	

	public AnalysisSpaceMiddleTierFactory(Configuration configuration) {
		super();
		this.configuration = configuration;
	}


	public AnalysisSpaceMiddleTier getInstance(Analysis a) {
		AnalysisSpaceMiddleTier mt = analysisMTMap.get(a);
		
		if(mt == null){
			mt = createMiddleTier(a);
			analysisMTMap.put(a,mt);
		}
		return mt;
	}


	private AnalysisSpaceMiddleTier createMiddleTier(Analysis a) {
		Configuration conf = new Configuration(configuration);
		conf.setDatabaseURL(conf.getDatabaseURLWithoutSchema()+a.getDatabaseName());
		conf.setHBM2DDLPolicy(null);
		return new AnalysisSpaceMiddleTier(conf,a);
	}

	public void refreshInstance(Analysis a){
		analysisMTMap.remove(a);
	}
	
}
