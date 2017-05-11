package edu.pitt.dbmi.odie.ui.views.providers;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import edu.pitt.dbmi.odie.model.AnalysisDocument;

public class DocumentsLabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		AnalysisDocument ad = (AnalysisDocument) element;
		switch (columnIndex) {
		case 0:
			return ad.getDocument().getName();
			// case 1:
			// CAS cas = ad.getCas();
			// if(cas!=null){
			// TypeSystem ts =
			// ad.getAnalysis().getAnalysisEngineMetadata().getTypeSystem();
			// Type namedEntityType =
			// ts.getType(IFConstants.NAMED_ENTITY_TYPE_FULLNAME);
			// if(namedEntityType == null)
			// return ""+cas.getAnnotationIndex().size();
			// else
			// return ""+cas.getAnnotationIndex(namedEntityType).size();
			// }
			// else
			// return "";
		case 1:
			return ((AnalysisDocument) element).getStatus();
			// SortedSet<Annotation> ann =
			// ((AnalysisDocument)element).getAnnotations();
			// if(ann!=null)
			// return ""+ann.size();
			// else
			// return ""+0;
		default:
			return "Invalid column index";
		}
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

}
