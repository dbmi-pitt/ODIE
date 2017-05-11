package edu.pitt.dbmi.odie.model;

import java.util.HashMap;
import java.util.List;

public class DisplayPreferences {

	AnalysisEngineMetadata analysisEngineMetadata;
	HashMap<String,TypeUIPreference> typeUIPreferences;
	List<String> filteredTypes;
	
	
	public AnalysisEngineMetadata getAnalysisEngineMetadata() {
		return analysisEngineMetadata;
	}

	public void setAnalysisEngineMetadata(
			AnalysisEngineMetadata analysisEngineMetadata) {
		this.analysisEngineMetadata = analysisEngineMetadata;
	}

	public HashMap<String, TypeUIPreference> getTypeUIPreferences() {
		return typeUIPreferences;
	}

	public void setTypeUIPreferences(
			HashMap<String, TypeUIPreference> typeUIPreferences) {
		this.typeUIPreferences = typeUIPreferences;
	}

	public List<String> getFilteredTypes() {
		return filteredTypes;
	}

	public void setFilteredTypes(List<String> filteredTypes) {
		this.filteredTypes = filteredTypes;
	}
	
	
	
}
