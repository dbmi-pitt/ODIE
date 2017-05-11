package edu.pitt.dbmi.odie.ui.editors.analysis;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class AnalysisEditorInput implements IEditorInput {

	Analysis analysis;

	public Analysis getAnalysis() {
		return analysis;
	}

	@Override
	public boolean exists() {
		return !(analysis == null);
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
		// if(GeneralUtils.isOE(analysis))
		// return Aesthetics.getOeAEIconDescriptor();
		// else if(GeneralUtils.isOther(analysis))
		// return Aesthetics.getOtherAEIconDescriptor();
		// else
		// return Aesthetics.getNerAEIconDescriptor();
	}

	@Override
	public String getName() {
		return analysis.getName();
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return getName();
	}

	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	public AnalysisEditorInput(Analysis analysis) {
		super();
		this.analysis = analysis;

	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof AnalysisEditorInput && this.analysis != null
				&& ((AnalysisEditorInput) obj).analysis != null)
			return ((AnalysisEditorInput) obj).analysis.equals(this.analysis);
		else
			return super.equals(obj);
	}

}
