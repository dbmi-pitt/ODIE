package edu.pitt.dbmi.odie.ui.views.details.providers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IProperty;
import edu.pitt.terminology.lexicon.Concept;
import edu.pitt.terminology.util.TerminologyException;

public class PClassDetailsContentProvider implements ITreeContentProvider {

	Logger logger = Logger.getLogger(this.getClass());

	public static final String PROPERTIES_LABEL = "Properties";

	private IClass currentClass;

	@Override
	public Object[] getElements(Object inputElement) {
		List l = new ArrayList();
		l.addAll(classFVList);

		if (propertiesFVList.size() > 0)
			l.add(PROPERTIES_LABEL);

		return l.toArray();
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object inputElement) {
		if (inputElement instanceof IClass) {
			this.currentClass = (IClass) inputElement;

			initClassFVList();
			setupFeatureValueMap();
		}
	}

	private void initClassFVList() {
		classFVList = new ArrayList<FeatureValue>();
		Concept c = currentClass.getConcept();

		classFVList.add(new FeatureValue("Name", c.getName()));
		if (c.getSynonyms().length > 0)
			classFVList.add(new FeatureValue("Synonyms", convertArrayToString(c
					.getSynonyms())));
		if (hasContent(c.getDefinition()))
			classFVList.add(new FeatureValue("Definition", c.getDefinition()));
		if (c.getSemanticTypes().length > 0)
			classFVList.add(new FeatureValue("Semantic Types",
					convertArrayToString(c.getSemanticTypes())));

		try {
			if (c.getParentConcepts().length > 0)
				classFVList.add(new FeatureValue("Parents",
						convertArrayToString(c.getParentConcepts())));

			// if(c.getChildrenConcepts().length>0)
			// classFVList.add(new FeatureValue("Children",
			// convertArrayToString(c.getChildrenConcepts())));
		} catch (TerminologyException e) {
			e.printStackTrace();
		}

		classFVList.add(new FeatureValue("URI", currentClass.getURI()
				.toASCIIString()));
	}

	private boolean hasContent(String s) {
		return (s != null && s.trim().length() > 0);
	}

	public static String convertArrayToString(Object[] strArr) {
		String out = "  ";
		for (Object s : strArr) {
			String n = "";
			if (s instanceof Concept)
				n = ((Concept) s).getName();
			else
				n = s.toString();
			out += n + ", ";
		}

		return out.substring(0, out.length() - 2);
	}

	HashMap<String, List<FeatureValue>> featureValueMap = new HashMap<String, List<FeatureValue>>();
	private ArrayList<FeatureValue> propertiesFVList = new ArrayList<FeatureValue>();
	private ArrayList<FeatureValue> classFVList = new ArrayList<FeatureValue>();

	private void setupFeatureValueMap() {
		featureValueMap.clear();

		if (currentClass == null)
			return;

		else {
			initPropertiesFVList();
			featureValueMap.put(PROPERTIES_LABEL, propertiesFVList);
		}
	}

	private void initPropertiesFVList() {
		propertiesFVList = new ArrayList<FeatureValue>();
		for (IProperty p : currentClass.getProperties()) {
			String feature = p.getName();
			String value = convertArrayToString(currentClass
					.getPropertyValues(p));
			propertiesFVList.add(new FeatureValue(feature, value));
		}
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
			if (propertiesFVList.contains(element))
				return PROPERTIES_LABEL;
			else
				return null;
		}

	}

	@Override
	public boolean hasChildren(Object element) {
		return (element instanceof String);
	}

}
