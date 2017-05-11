package edu.pitt.dbmi.odie.ui.editors.analysis.summary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.Statistics;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class OntologyPerformanceContentProvider implements ITreeContentProvider	 {

	Logger logger = Logger.getLogger(this.getClass());
	List<Analysis> analyses;
	
	
	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof List){
			analyses = (List)inputElement;
			
			List<ComparisonValuesData> outlist = new ArrayList<ComparisonValuesData>();
			
			ComparisonValuesData uccvd = new ComparisonValuesData();
			uccvd.setLabel(ODIEConstants.UCONCEPTS_LABEL);
			
			ComparisonValuesData ocvd = new ComparisonValuesData();
			ocvd.setLabel(ODIEConstants.OCCURENCES_LABEL);
			
			ComparisonValuesData ccvd = new ComparisonValuesData();
			ccvd.setLabel(ODIEConstants.COVERAGE_LABEL);
			
			for(int i=0;i<analyses.size();i++){
				Analysis a = analyses.get(i);
				
				Collection<Statistics> c = a.getStatistics().ontologyStatistics;
				if(c==null)
					continue;
				for(Statistics st:c){
					uccvd.addValue(st.uniqueConceptsCount);
					ocvd.addValue(st.namedEntityCount);

					float f = (float) st.coveredCharCount / (float) st.totalCharCount;
					ccvd.addValue(new Float(f),GeneralUtils.getPrettyPercentageFormat(f));
				}
			}			
			
			outlist.add(uccvd);
			outlist.add(ocvd);
			outlist.add(ccvd);
			return outlist.toArray();
		}
		return new Object[]{};
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object inputElement) {
	}


	@Override
	public Object[] getChildren(Object parentElement) {
//		if (parentElement instanceof ComparisonValuesData) {
//			ComparisonValuesData cvd = (ComparisonValuesData) parentElement;
//			if(cvd.getChildren()==null){
//				if(cvd.getLabel().equals(ODIEConstants.UCONCEPTS_LABEL)){
//					populateUCCVDs(cvd);
//				}
//				else if(cvd.getLabel().equals(ODIEConstants.OCCURENCES_LABEL)){
//					populateOCVDs(cvd);
//				}
//				else if(cvd.getLabel().equals(ODIEConstants.COVERAGE_LABEL)){
//					populateCCVDs(cvd);
//				}
//				else
//					return  new Object[] {};
//			}
//			return cvd.getChildren().toArray();
//		}
		return new Object[] {};
	}


//	private void populateUCCVDs(ComparisonValuesData cvd) {
//		List<ComparisonValuesData> cvdList = new ArrayList<ComparisonValuesData>();
//		
//		HashMap<String,ComparisonValuesData> map = new HashMap<String,ComparisonValuesData>();
//		
//		for(int i=0;i<analyses.size();i++){
//			Analysis a = analyses.get(i);
//			
//			Collection<Statistics> c = a.getStatistics().ontologyStatistics;
//			for(Statistics st:c){
//				URI uri = (URI)st.context;
//				ComparisonValuesData ocvd = map.get(uri.toASCIIString());
//				
//				if(ocvd == null){
//					ocvd = new ComparisonValuesData();
//					cvdList.add(ocvd);
//					ocvd.setLabel(uri.toASCIIString().substring(uri.toASCIIString().lastIndexOf("/") + 1));
//					map.put(uri.toASCIIString(), ocvd);
//				}
//				List<Object> vals = ocvd.getValues();
//				if(vals == null){
//					vals = new ArrayList<Object>();
//					ocvd.setValues(vals);
//				}
//				vals.add(i,new Long(st.uniqueConceptsCount));
//			}
//				
//			
//		}
//		cvd.setChildren(cvdList);
//	}
//
//	private void populateOCVDs(ComparisonValuesData cvd) {
//		List<ComparisonValuesData> cvdList = new ArrayList<ComparisonValuesData>();
//		
//		HashMap<String,ComparisonValuesData> map = new HashMap<String,ComparisonValuesData>();
//		
//		for(int i=0;i<analyses.size();i++){
//			Analysis a = analyses.get(i);
//			
//			Collection<Statistics> c = a.getStatistics().ontologyStatistics;
//			for(Statistics st:c){
//				URI uri = (URI)st.context;
//				ComparisonValuesData ocvd = map.get(uri.toASCIIString());
//				
//				if(ocvd == null){
//					ocvd = new ComparisonValuesData();
//					cvdList.add(ocvd);
//					ocvd.setLabel(uri.toASCIIString().substring(uri.toASCIIString().lastIndexOf("/") + 1));
//					map.put(uri.toASCIIString(), ocvd);
//				}
//				List<Object> vals = ocvd.getValues();
//				if(vals == null){
//					vals = new ArrayList<Object>();
//					ocvd.setValues(vals);
//				}
//				vals.add(i,new Long(st.namedEntityCount));
//			}
//				
//			
//		}
//		cvd.setChildren(cvdList);
//	}
//	
//	
//	private void populateCCVDs(ComparisonValuesData cvd) {
//		List<ComparisonValuesData> cvdList = new ArrayList<ComparisonValuesData>();
//		
//		HashMap<String,ComparisonValuesData> map = new HashMap<String,ComparisonValuesData>();
//		
//		for(int i=0;i<analyses.size();i++){
//			Analysis a = analyses.get(i);
//			
//			Collection<Statistics> c = a.getStatistics().ontologyStatistics;
//			for(Statistics st:c){
//				URI uri = (URI)st.context;
//				ComparisonValuesData ocvd = map.get(uri.toASCIIString());
//				
//				if(ocvd == null){
//					ocvd = new ComparisonValuesData();
//					cvdList.add(ocvd);
//					ocvd.setLabel(uri.toASCIIString().substring(uri.toASCIIString().lastIndexOf("/") + 1));
//					map.put(uri.toASCIIString(), ocvd);
//				}
//				List<Object> vals = ocvd.getValues();
//				if(vals == null){
//					vals = new ArrayList<Object>();
//					ocvd.setValues(vals);
//				}
//				vals.add(i,GeneralUtils
//						.getPrettyPercentageFormat(((float) st.coveredCharCount / (float) st.totalCharCount)));
//			}
//				
//			
//		}
//		cvd.setChildren(cvdList);
//	}
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
//		if (element instanceof ComparisonValuesData) {
//			ComparisonValuesData s = (ComparisonValuesData) element;
//			if(s.getChildren() == null){
//				return (s.getLabel().equals(ODIEConstants.UCONCEPTS_LABEL) || 
//						s.getLabel().equals(ODIEConstants.OCCURENCES_LABEL) ||
//						s.getLabel().equals(ODIEConstants.COVERAGE_LABEL));
//			}
//			return !s.getChildren().isEmpty();
//		}
		return false;
	}
}
