package edu.pitt.dbmi.odie.ui.editors.annotations;

import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;

public interface IAnnotationSubType extends Type {

	public String getParentTypeName();

	public boolean meetsSubTypeCriteria(AnnotationFS annotation);
}
