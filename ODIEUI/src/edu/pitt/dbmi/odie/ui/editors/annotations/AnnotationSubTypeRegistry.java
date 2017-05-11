package edu.pitt.dbmi.odie.ui.editors.annotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.uima.cas.text.AnnotationFS;

import edu.pitt.dbmi.odie.uima.util.ODIE_IFConstants;

public class AnnotationSubTypeRegistry {

	private static AnnotationSubTypeRegistry singleton;
	private HashMap<String, List<IAnnotationSubType>> registry = new HashMap<String, List<IAnnotationSubType>>();

	public AnnotationSubTypeRegistry() {
		super();

		registerNamedEntitySubTypes();
	}

	private void registerNamedEntitySubTypes() {
		List<IAnnotationSubType> list = new ArrayList<IAnnotationSubType>();

		list.add(new NegatedNESubType());
		registry.put(ODIE_IFConstants.NAMED_ENTITY_TYPE_FULLNAME, list);
	}

	public static AnnotationSubTypeRegistry getInstance() {
		if (singleton == null) {
			singleton = new AnnotationSubTypeRegistry();
		}
		return singleton;
	}

	public List<IAnnotationSubType> getRegisteredSubTypes(String typeFullName) {
		return registry.get(typeFullName);
	}

	public List<IAnnotationSubType> getRegisteredSubTypes() {
		List<IAnnotationSubType> outList = new ArrayList<IAnnotationSubType>();

		for (List l : registry.values()) {
			outList.addAll(l);
		}
		return outList;
	}

	public IAnnotationSubType getSubType(AnnotationFS annotation) {
		List<IAnnotationSubType> list = registry.get(annotation.getType()
				.getName());

		if (list != null) {
			for (IAnnotationSubType st : list) {
				if (st.meetsSubTypeCriteria(annotation))
					return st;
			}
		}
		return null;
	}

}
