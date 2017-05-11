package edu.pitt.dbmi.odie.ui.editors.analysis.summary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.model.Document;

public class DocumentPerformanceContentProvider implements ITreeContentProvider	 {

	Logger logger = Logger.getLogger(this.getClass());
	List<Analysis> analyses;
	
	
	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof List){
			HashMap<Long,ComparisonValuesData> cvdMap = new HashMap<Long,ComparisonValuesData>();
			analyses = (List)inputElement;
			
			List<ComparisonValuesData> outlist = new ArrayList<ComparisonValuesData>();
			
			for(int i=0;i<analyses.size();i++){
				Analysis a = analyses.get(i);
				
				for(AnalysisDocument ad:a.getAnalysisDocuments()){
					ComparisonValuesData cvd =  cvdMap.get(ad.getDocument().getId());
					if(cvd==null){
						cvd = new ComparisonValuesData();
						cvdMap.put(ad.getDocument().getId(), cvd);
						cvd.setLabel(ad.getDocument().getName());
						outlist.add(cvd);
					}
					cvd.addValueAtIndex(i,new Integer(ad.getDatapoints().size()));
				}
			}			
			return outlist.toArray();
		}
		return new Object[]{};
	}

	@Override
	public void dispose() {
		analyses = null;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object inputElement) {
	}


	@Override
	public Object[] getChildren(Object parentElement) {

		return new Object[] {};
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof ComparisonValuesData) {
			ComparisonValuesData s = (ComparisonValuesData) element;
			return s.getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return false;
	}
}
