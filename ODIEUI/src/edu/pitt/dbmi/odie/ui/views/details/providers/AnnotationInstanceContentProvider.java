package edu.pitt.dbmi.odie.ui.views.details.providers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.cas.FSArray;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.mayo.bmi.uima.core.type.UmlsConcept;
import edu.pitt.dbmi.odie.ODIEConstants;

public class AnnotationInstanceContentProvider implements ITreeContentProvider {

	Logger logger = Logger.getLogger(this.getClass());

	public static final String GENERAL_LABEL = "General";
	public static final String TYPESPECIFIC_LABEL = "Type Specific";
	String[] categories = new String[] { GENERAL_LABEL, TYPESPECIFIC_LABEL };

	private AnnotationFS currentAnnotationFS;

	@Override
	public Object[] getElements(Object inputElement) {
		return categories;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object inputElement) {
		if (inputElement instanceof AnnotationFS) {
			this.currentAnnotationFS = (AnnotationFS) inputElement;

			setupFeatureValueMap();
		}
	}

	HashMap<String, List<FeatureValue>> featureValueMap = new HashMap<String, List<FeatureValue>>();

	private ArrayList<FeatureValue> generalFVList;

	private ArrayList<FeatureValue> typeSpecificFVList;

	private void setupFeatureValueMap() {
		featureValueMap.clear();

		if (currentAnnotationFS == null)
			return;

		else {
			generalFVList = new ArrayList<FeatureValue>();
			generalFVList.add(new FeatureValue("Type", currentAnnotationFS
					.getType().getShortName()));
			generalFVList.add(new FeatureValue("Covered Text",
					currentAnnotationFS.getCoveredText()));
			generalFVList.add(new FeatureValue("Start Offset",
					currentAnnotationFS.getBegin() + ""));
			generalFVList.add(new FeatureValue("End Offset",
					currentAnnotationFS.getEnd() + ""));
			featureValueMap.put(GENERAL_LABEL, generalFVList);

			typeSpecificFVList = new ArrayList<FeatureValue>();
			for (Object o : currentAnnotationFS.getType().getFeatures()) {
				String feature = ((Feature) o).getShortName();
				String value = getDisplayValue((Feature) o);
				typeSpecificFVList.add(new FeatureValue(feature, value));
			}
			featureValueMap.put(TYPESPECIFIC_LABEL, typeSpecificFVList);
		}

	}

	private String getDisplayValue(Feature f) {
		
		String s = "";
		
		if(f.getName().equals(ODIEConstants.NAMED_ENTITY_ONTOLOGY_CONCEPTS_FEATURE_NAME))
			return getCUIListAsString(f);
		try {
			s = currentAnnotationFS.getFeatureValueAsString(f);
		} catch (CASRuntimeException e) {
			FeatureStructure fs = currentAnnotationFS.getFeatureValue(f);
			if (fs != null)
				s = fs.toString();
		}
		return s;
	}

	private String getCUIListAsString(Feature f) {
		FeatureStructure fs = currentAnnotationFS.getFeatureValue(f);
		String s = "";
		if(fs instanceof FSArray){
			FSArray fsa = (FSArray)fs;
			for(int i=0;i<fsa.size();i++){
				FeatureStructure fss = fsa.get(i);
				UmlsConcept umlsc = (UmlsConcept)fss;
				s += umlsc.getCui() + ", ";
			}
		}
		if(s.length()>2)
			s =s.substring(0,s.length()-2); 
		
		return s; 
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof String) {
			List<FeatureValue> l = featureValueMap.get(parentElement);

			if (l != null)
				return l.toArray();
		}
		return new Object[] {};
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof String)
			return null;
		else {
			if (generalFVList.contains(element))
				return GENERAL_LABEL;
			else if (typeSpecificFVList.contains(element))
				return TYPESPECIFIC_LABEL;
			else
				return null;
		}

	}

	@Override
	public boolean hasChildren(Object element) {
		return (element instanceof String);
	}

}
