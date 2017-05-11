package edu.pitt.dbmi.odie.uima.util;

import java.util.List;
import java.util.TreeSet;
import java.util.Iterator;

import org.apache.uima.jcas.tcas.Annotation;

import edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateAnnotation;

/**
 * Annotations sorted by document position.
 * 
 * @author mitchellkj@upmc.edu
 * @version $Id$
 * @since 1.6.15
 */
public class ODIE_SortedAnnotationSet extends TreeSet<ODIE_GateAnnotation> {
	
	/**
	 * Field serialVersionUID. (value is "-1863322709982419803L ;")
	 */
	private static final long serialVersionUID = -1863322709982419803L;


    /**
     * Constructor.
     */
    public ODIE_SortedAnnotationSet() {
        super(new ODIE_Comparator());
    }

    /**
     * Constructor for ODIE_SortedAnnotationSet.
     * 
     * @param isAscending boolean
     */
    public ODIE_SortedAnnotationSet(boolean isAscending) {
        super(new ODIE_Comparator(isAscending));
    }

    /**
     * Constructor for ODIE_SortedAnnotationSet.
     * 
     * @param annotationSet AnnotationSet
     */
    public ODIE_SortedAnnotationSet(List<ODIE_GateAnnotation> annotationSet) {
        super(new ODIE_Comparator());
        setAnnotationSet(annotationSet);
    }

    /**
     * Constructor for ODIE_SortedAnnotationSet.
     * 
     * @param isAscending boolean
     * @param annotationSet AnnotationSet
     */
    public ODIE_SortedAnnotationSet(List<ODIE_GateAnnotation> annotationSet,
            boolean isAscending) {
        super(new ODIE_Comparator(isAscending));
        setAnnotationSet(annotationSet);
    }

    /**
     * Method setAnnotationSet.
     * 
     * @param annotationSet AnnotationSet
     */
    public void setAnnotationSet(List<ODIE_GateAnnotation> annotationSet) {
        if (annotationSet != null) {
            Iterator<ODIE_GateAnnotation> iterator = annotationSet.iterator();
            while (iterator.hasNext()) {
                add((ODIE_GateAnnotation) iterator.next());
            }
        }
    }
    
    /**
     * Method addSortedExclusive.
     * 
     * @param annot Annotation
     * 
     * @return boolean
     */
    public boolean addSortedExclusive(ODIE_GateAnnotation annot) {
        if (annot == null) {
            return false;
        } else {
            Iterator<ODIE_GateAnnotation> iter = iterator();
            while (iter.hasNext()) {
            	ODIE_GateAnnotation currAnnot = (ODIE_GateAnnotation) iter.next();
                if (ODIE_UimaUtils.overlaps(annot, currAnnot)) {
                    return false;
                }
            }
            add(annot);
            return true;
        }
    }

}
