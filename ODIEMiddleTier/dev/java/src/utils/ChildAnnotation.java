/**
 * @author Girish Chavan
 *
 * Sep 9, 2008
 */
package edu.pitt.dbmi.odie.model.utils;

import edu.pitt.dbmi.odie.model.Annotation;

public class ChildAnnotation extends Annotation {
	public static final String TYPE_PHRASE = "Phrase";
	public static final String TYPE_WORD = "Word";
	private Annotation parentAnnotation;
	private String type;

	/**
	 * @param a
	 */
	public ChildAnnotation(Annotation a) {
		parentAnnotation = a;
	}

	public Annotation getParentAnnotation() {
		return parentAnnotation;
	}

	public String getType() {
		return type;
	}

	public void setParentAnnotation(Annotation parentAnnotation) {
		this.parentAnnotation = parentAnnotation;
	}

	public void setType(String type) {
		this.type = type;
	}

}
