package edu.pitt.dbmi.odie.ui.editors.providers;

import java.util.List;
import java.util.Vector;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;

public class SuggestionAnnotation implements AnnotationFS {
	int begin;
	int end;
	String coveredText;

	public static Type SUGGESTION_TYPE = new Type() {

		@Override
		public Type getComponentType() {

			return null;
		}

		@Override
		public Feature getFeatureByBaseName(String arg0) {

			return null;
		}

		@Override
		public List<Feature> getFeatures() {

			return null;
		}

		@Override
		public String getName() {
			return "Suggestion";
		}

		@Override
		public int getNumberOfFeatures() {

			return 0;
		}

		@Override
		public String getShortName() {
			return getName();
		}

		@Override
		public boolean isArray() {

			return false;
		}

		@Override
		public boolean isFeatureFinal() {

			return false;
		}

		@Override
		public boolean isInheritanceFinal() {

			return false;
		}

		@Override
		public boolean isPrimitive() {

			return false;
		}

		@Deprecated
		@Override
		public Vector<Feature> getAppropriateFeatures() {
			// TODO Auto-generated method stub
			return null;
		}

	};

	public SuggestionAnnotation(int begin, int end, String coveredText) {
		super();
		this.begin = begin;
		this.end = end;
		this.coveredText = coveredText;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public void setCoveredText(String coveredText) {
		this.coveredText = coveredText;
	}

	public int getBegin() {
		return begin;
	}

	public int getEnd() {
		return end;
	}

	public String getCoveredText() {
		return coveredText;
	}

	@Override
	public CAS getView() {
		return null;
	}

	@Override
	public boolean getBooleanValue(Feature arg0) throws CASRuntimeException {
		return false;
	}

	@Override
	public byte getByteValue(Feature arg0) throws CASRuntimeException {
		return 0;
	}

	@Override
	public CAS getCAS() {
		return null;
	}

	@Override
	public double getDoubleValue(Feature arg0) throws CASRuntimeException {
		return 0;
	}

	@Override
	public FeatureStructure getFeatureValue(Feature arg0)
			throws CASRuntimeException {
		return null;
	}

	@Override
	public String getFeatureValueAsString(Feature arg0)
			throws CASRuntimeException {
		return null;
	}

	@Override
	public float getFloatValue(Feature arg0) throws CASRuntimeException {
		return 0;
	}

	@Override
	public int getIntValue(Feature arg0) throws CASRuntimeException {
		return 0;
	}

	@Override
	public long getLongValue(Feature arg0) throws CASRuntimeException {
		return 0;
	}

	@Override
	public short getShortValue(Feature arg0) throws CASRuntimeException {
		return 0;
	}

	@Override
	public String getStringValue(Feature arg0) throws CASRuntimeException {
		return null;
	}

	@Override
	public Type getType() {
		return SUGGESTION_TYPE;
	}

	@Override
	public void setBooleanValue(Feature arg0, boolean arg1)
			throws CASRuntimeException {
	}

	@Override
	public void setByteValue(Feature arg0, byte arg1)
			throws CASRuntimeException {
	}

	@Override
	public void setDoubleValue(Feature arg0, double arg1)
			throws CASRuntimeException {
	}

	@Override
	public void setFeatureValue(Feature arg0, FeatureStructure arg1)
			throws CASRuntimeException {
	}

	@Override
	public void setFeatureValueFromString(Feature arg0, String arg1)
			throws CASRuntimeException {
	}

	@Override
	public void setFloatValue(Feature arg0, float arg1)
			throws CASRuntimeException {
	}

	@Override
	public void setIntValue(Feature arg0, int arg1) throws CASRuntimeException {
	}

	@Override
	public void setLongValue(Feature arg0, long arg1)
			throws CASRuntimeException {
	}

	@Override
	public void setShortValue(Feature arg0, short arg1)
			throws CASRuntimeException {
	}

	@Override
	public void setStringValue(Feature arg0, String arg1)
			throws CASRuntimeException {
	}

	public Object clone() {
		return null;
	}
}