/**
 * 
 */
package edu.pitt.dbmi.odie.model;


import edu.pitt.dbmi.odie.middletier.MiddleTierJDBC;

/**
 * @author Girish Chavan
 * 
 */

@Deprecated
public class AnalysisLanguageResourceJDBC extends AnalysisLanguageResource {

	int analysis_id;
	int ontology_id;

	/**
	 * 
	 */
	private void lazyFetchLanguageResource() {
		if (languageResource == null) {
			MiddleTierJDBC mt = MiddleTierJDBC.getInstance(null);
			languageResource = mt.getLanguageResource(this);
		}

	}

	public int getAnalysisId() {
		return analysis_id;
	}

	@Override
	public LanguageResource getLanguageResource() {
		lazyFetchLanguageResource();
		return super.getLanguageResource();
	}

	public int getOntologyId() {
		return ontology_id;
	}

	public void setAnalysisId(int analysis_id) {
		this.analysis_id = analysis_id;
	}

	public void setOntologyId(int ontology_id) {
		this.ontology_id = ontology_id;
	}

}
