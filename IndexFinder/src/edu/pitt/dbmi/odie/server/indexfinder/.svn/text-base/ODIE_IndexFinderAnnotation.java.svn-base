package edu.pitt.dbmi.odie.server.indexfinder;

import java.util.HashMap;

public class ODIE_IndexFinderAnnotation {

	private Integer annotationId;
	private String annoationSetName;
	private String annotationTypeName;
	private ODIE_IndexFinderNode sNode;
	private ODIE_IndexFinderNode eNode;
	private HashMap<String, Object> featureMap = new HashMap<String, Object>();

	public ODIE_IndexFinderAnnotation() {
	}

	public Integer getAnnotationId() {
		return annotationId;
	}

	public void setAnnotationId(Integer annotationId) {
		this.annotationId = annotationId;
	}

	public String getAnnotationSetName() {
		return annoationSetName;
	}

	public void setAnnotationSetName(String annotationSetName) {
		this.annoationSetName = annotationSetName;
	}

	public String getAnnotationTypeName() {
		return annotationTypeName;
	}

	public void setAnnotationTypeName(String annotationTypeName) {
		this.annotationTypeName = annotationTypeName;
	}

	public ODIE_IndexFinderNode getStartNode() {
		return sNode;
	}

	public void setStartNode(ODIE_IndexFinderNode node) {
		sNode = node;
	}

	public ODIE_IndexFinderNode getEndNode() {
		return eNode;
	}

	public void setEndNode(ODIE_IndexFinderNode node) {
		eNode = node;
	}

	public HashMap<String, Object> getFeatures() {
		return featureMap;
	}

	public void setFeatures(HashMap<String, Object> featureMap) {
		this.featureMap = featureMap;
	}

	public String getCname() {
		return (String) featureMap.get("string");
	}

	public String getNormalizedForm() {
		return (String) featureMap.get("normalizedForm");
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer() ;
		sb.append("ODIEAnnotation: (");
		sb.append(this.getStartNode().getOffset());
		sb.append(",");
		sb.append(this.getEndNode().getOffset());
		sb.append(") ");
		sb.append(getAnnotationSetName() + " " + getNormalizedForm());
		for (String key : getFeatures().keySet()) {
			String value = (String) getFeatures().get(key);
			sb.append("\n\t" + key + " ==> " + value) ;
		}
		return sb.toString();
	}
}
