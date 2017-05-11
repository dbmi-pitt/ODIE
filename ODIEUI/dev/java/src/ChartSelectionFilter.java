/**
 * 
 */
package edu.pitt.dbmi.odie.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.jfree.chart.entity.CategoryItemEntity;

import edu.pitt.dbmi.odie.model.Annotation;
import edu.pitt.dbmi.odie.model.Datapoint;
import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.model.Suggestion;

/**
 * @author Girish Chavan
 *
 */
public class ChartSelectionFilter extends ViewerFilter {

	private List<Datapoint> datapointSelection = new ArrayList<Datapoint>();
	private List<AnalysisDocument> documentSelection = new ArrayList<AnalysisDocument>();
	
	public static final String NAME = "chart selection";
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
//		System.out.println(element.getClass().getName() + ", " + element.toString());
//		System.out.println(parentElement.getClass().getName() + ", " + parentElement.toString());
		
		if(element instanceof Datapoint){
			if(datapointSelection.isEmpty())
				return true;
			
			if(datapointSelection.contains(element))
				return true;
			else 
				return false;
		}
		else if(element instanceof AnalysisDocument){
			if(documentSelection.isEmpty())
				return true;
			
			if(documentSelection.contains(element))
				return true;
			else 
				return false;
		}
			
		return true;
	}
	/**
	 * @param list
	 */
	public void setSelection(List list) {
		datapointSelection.clear();
		documentSelection.clear();
		for(int i=0;i<list.size();i++){
			Datapoint dp = null;
			
			if(list.get(i) instanceof Suggestion){
				for(Annotation ann:((Suggestion)list.get(i)).getAnnotations()){
					documentSelection.add(ann.getDocumentAnalysis());
				}
			}
			else if(list.get(i) instanceof CategoryItemEntity){
				 dp = (Datapoint) ((CategoryItemEntity)list.get(i)).getColumnKey();
			}
			else if(list.get(i) instanceof Datapoint){
				dp = (Datapoint) list.get(i);
			}
			

			if(dp!=null){
				datapointSelection.add(dp);
				documentSelection.addAll(dp.getAnalysisDocuments());
			}
		}
		
	}

	
}
