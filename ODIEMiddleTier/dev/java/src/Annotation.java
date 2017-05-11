package edu.pitt.dbmi.odie.model;

import java.net.URI;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.ontology.IClass;

public class Annotation implements Comparable<Annotation> {
	private IClass annotationClass;
	private AnalysisDocument da;
	private int endOffset;
	
	private int ID = -1;
	private String metadata;
	private int startOffset;
	private URI uri;

	/**
	 * 
	 */
	private void lazyFetchSelf() {
		if (annotationClass == null && uri != null) {
			annotationClass = (IClass) MiddleTier.getInstance(null)
					.getResourceForURI(uri);
		}
	}

	/**
	 * compare one annotation vs another
	 */
	public int compareTo(Annotation o) {
		// make sure they are from same document
		if (getDocument().equals(o.getDocument())) {
			int d = getStartOffset() - o.getStartOffset();
			int returnv = (d == 0) ? getEndOffset() - o.getEndOffset() : d;

			if (returnv == 0) {
				if (getAnnotationClass().equals(o.getAnnotationClass()))
					return 0;
				else
					return getAnnotationClassURI().compareTo(
							o.getAnnotationClassURI());
			}

			return returnv;
		} else
			return getDocument().getURI().compareTo(o.getDocument().getURI());
	}

	/**
	 * does this annotation intersect the other
	 */
	public boolean contains(Annotation a) {
		if (getDocument().equals(a.getDocument())) {
			int sa = getStartOffset();
			int ea = getEndOffset();
			int sb = a.getStartOffset();
			int eb = a.getEndOffset();
			return sb >= sa && eb <= ea;
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Annotation) {
			Annotation a = (Annotation) obj;
			if (getDocument().equals(a.getDocument())) {
				if (startOffset == a.getStartOffset()
						&& endOffset == a.getEndOffset()
						&& getAnnotationClassURI().equals(
								a.getAnnotationClassURI()))
					return true;
				else
					return false;
			} else
				return false;
		} else
			return super.equals(obj);
	}

	public IClass getAnnotationClass() {
		lazyFetchSelf();
		return annotationClass;
	}

	public URI getAnnotationClassURI() {
		return uri;
	}

	public Document getDocument() {
		return getDocumentAnalysis().getDocument();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pitt.dbmi.odie.model.IAnnotation#getAnnotationClass()
	 */

	public AnalysisDocument getDocumentAnalysis() {
		return da;
	}

	public int getEndOffset() {
		return endOffset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pitt.dbmi.odie.model.IAnnotation#setAnnotationClass(edu.pitt.ontology.IClass)
	 */

	public int getID() {
		return ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pitt.dbmi.odie.model.IAnnotation#getDocument()
	 */

	public int getLength() {
		return endOffset - startOffset;
	}

	public String getMetadata() {
		return metadata;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pitt.dbmi.odie.model.IAnnotation#getAnnotationClassURI()
	 */

	public int getStartOffset() {
		return startOffset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pitt.dbmi.odie.model.IAnnotation#setAnnotationClassURI(java.net.URI)
	 */

	public String getText() {
		String text = getDocumentAnalysis().getDocument().getText();
		return (text == null ? null : text.substring(startOffset, endOffset));
	}

	@Override
	public int hashCode() {
		return getDocument().hashCode() + startOffset + endOffset
				+ getAnnotationClassURI().hashCode();
	}

	/**
	 * is this annotation intersects or contains the other
	 */
	public boolean intersects(Annotation a) {
		if (getDocument().equals(a.getDocument())) {
			int sa = getStartOffset();
			int ea = getEndOffset();
			int sb = a.getStartOffset();
			int eb = a.getEndOffset();
			return (sa >= sb && sa < eb) || (sb >= sa && sb < ea);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pitt.dbmi.odie.model.IAnnotation#getText()
	 */

	public void setAnnotationClass(IClass annotationClass) {
		this.annotationClass = annotationClass;
	}

	public void setAnnotationClassURI(URI uri) {
		this.uri = uri;

	}

	public void setDocumentAnalysis(AnalysisDocument da) {
		this.da = da;
	}

	public void setEndOffset(int endOffset) {
		this.endOffset = endOffset;
	}

	public void setID(int id) {
		ID = id;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public void setStartOffset(int startOffset) {
		this.startOffset = startOffset;
	}

}
