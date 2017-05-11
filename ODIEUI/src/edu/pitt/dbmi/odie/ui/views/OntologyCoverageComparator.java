/**
 * @author Girish Chavan
 *
 * Sep 16, 2008
 */
package edu.pitt.dbmi.odie.ui.views;

import java.util.Comparator;

import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.ontology.IOntology;

public class OntologyCoverageComparator implements Comparator<IOntology> {
	Analysis analysis;

	public OntologyCoverageComparator(Analysis a) {
		super();
		this.analysis = a;
	}

	public int compare(IOntology o1, IOntology o2) {
		MiddleTier mt = edu.pitt.dbmi.odie.ui.Activator.getDefault()
				.getMiddleTier();
		int ret = (int) (mt.getOntologyHitCount(analysis, o1, true) - mt
				.getOntologyHitCount(analysis, o2, true));

		if (ret == 0)
			ret = (int) (mt.getOntologyHitCount(analysis, o1, false) - mt
					.getOntologyHitCount(analysis, o2, false));
		else
			return ret;

		if (ret == 0)
			return o1.getURI().compareTo(o2.getURI());
		else
			return ret;
	}

	public int compare(String o1uriString, String o2uriString) {
		MiddleTier mt = edu.pitt.dbmi.odie.ui.Activator.getDefault()
				.getMiddleTier();
		int ret = (int) (mt.getOntologyHitCount(analysis, o1uriString, true) - mt
				.getOntologyHitCount(analysis, o2uriString, true));

		if (ret == 0)
			ret = (int) (mt.getOntologyHitCount(analysis, o1uriString, false) - mt
					.getOntologyHitCount(analysis, o2uriString, false));
		else
			return ret;

		if (ret == 0)
			return o1uriString.compareTo(o2uriString);
		else
			return ret;
	}
}
