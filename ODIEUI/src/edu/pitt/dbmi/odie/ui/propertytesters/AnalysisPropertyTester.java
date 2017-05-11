package edu.pitt.dbmi.odie.ui.propertytesters;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class AnalysisPropertyTester extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {

		ISelection selection = (ISelection) receiver;

		if (selection instanceof StructuredSelection) {

			StructuredSelection sselection = (StructuredSelection) selection;
			Analysis analysis = getSelectedAnalysis(sselection);

			if (analysis == null)
				return false;

			if ("isAnalysisLoaded".equals(property)) {
				return isAnalysisLoaded(analysis);
			} else if ("isPartiallyProcessed".equals(property)) {
				return GeneralUtils.isPartiallyProcessed(analysis);
			} else if ("isOE".equals(property)) {
				return GeneralUtils.isOE(analysis);
			} else
				return false;
		} else
			return false;
	}

	private boolean isAnalysisLoaded(Analysis analysis) {
		// if(analysis instanceof AnalysisJDBC)
		// return ((AnalysisJDBC)analysis).isLoaded();
		// else
		return true;
	}

	private Analysis getSelectedAnalysis(StructuredSelection selection) {
		if (selection.getFirstElement() instanceof Analysis)
			return (Analysis) selection.getFirstElement();

		return null;
	}
}
