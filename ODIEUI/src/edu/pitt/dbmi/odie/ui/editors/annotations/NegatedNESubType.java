package edu.pitt.dbmi.odie.ui.editors.annotations;

import java.util.List;
import java.util.Vector;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;

import edu.pitt.dbmi.odie.uima.util.ODIE_IFConstants;

public class NegatedNESubType implements IAnnotationSubType {

	private static final String NAME = "Negated Named Entity";
	private static final String PARENT_TYPE = ODIE_IFConstants.NAMED_ENTITY_TYPE_FULLNAME;

	@Override
	public String getShortName() {
		return NAME;
	}

	@Override
	public boolean meetsSubTypeCriteria(AnnotationFS annotation) {
		if (annotation.getType().getName().equals(
				ODIE_IFConstants.NAMED_ENTITY_TYPE_FULLNAME)) {
			Feature f = annotation.getType().getFeatureByBaseName(
					ODIE_IFConstants.NAMED_ENTITY_CERTAINTY_FEATURE_NAME);
			int certainty = annotation.getIntValue(f);
			return (certainty < 0);
		}
		return false;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getParentTypeName() {
		return PARENT_TYPE;
	}

	@Deprecated
	@Override
	public Vector<Feature> getAppropriateFeatures() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type getComponentType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Feature getFeatureByBaseName(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Feature> getFeatures() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfFeatures() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isArray() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFeatureFinal() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInheritanceFinal() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPrimitive() {
		// TODO Auto-generated method stub
		return false;
	}

}
