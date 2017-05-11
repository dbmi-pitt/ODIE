/**
 * @author Girish Chavan
 *
 * Sep 16, 2008
 */
package edu.pitt.dbmi.odie.ui.views;

import java.util.Comparator;

import edu.pitt.dbmi.odie.model.AnalysisLanguageResource;

public class AnalysisLRComparator implements Comparator<AnalysisLanguageResource> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(AnalysisLanguageResource alr1,
			AnalysisLanguageResource alr2) {
//		OntologyCoverageComparator oc = 
//				new OntologyCoverageComparator(alr1.getAnalysis().getStatistics());
//			
//			return  oc.compare((IOntology)alr2.getLanguageResource().getResource(),
//					(IOntology)alr1.getLanguageResource().getResource());
		return 1;
	}
}
