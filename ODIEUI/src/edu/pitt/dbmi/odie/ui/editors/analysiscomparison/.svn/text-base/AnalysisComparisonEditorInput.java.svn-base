package edu.pitt.dbmi.odie.ui.editors.analysiscomparison;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import edu.pitt.dbmi.odie.model.Analysis;

public class AnalysisComparisonEditorInput implements IEditorInput {

	List<Analysis> analyses;

	public List<Analysis> getAnalyses() {
		return analyses;
	}

	@Override
	public boolean exists() {
		return !(analyses == null);
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
		String name = "Compare(";
		
		for(Analysis a:analyses){
			name += "'" + a.getName() +"',";
		}
		
		name = name.substring(0,name.length()-1);
		name += ")";
		
		return name; 
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

	public AnalysisComparisonEditorInput(List<Analysis> analyses) {
		super();
		this.analyses = analyses;

	}

}
