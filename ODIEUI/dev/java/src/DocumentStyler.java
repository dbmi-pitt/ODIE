package edu.pitt.dbmi.odie.ui.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;

import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;

import edu.pitt.dbmi.odie.model.Annotation;
import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IOntology;

public class DocumentStyler implements LineStyleListener {

	protected SortedSet<Annotation> annotations;
	protected AnalysisDocument documentAnalysis;
	List<Object> displayOrder = new ArrayList<Object>();
	protected List<Object> selectedItems = new ArrayList<Object>();
	
	/**
	 * @param da
	 * @param equals
	 */
	public DocumentStyler(AnalysisDocument da) {
		this.documentAnalysis = da;
		this.annotations = da.getAnnotations();
	}


	@Override
	public void lineGetStyle(LineStyleEvent event) {
		int startOffset = event.lineOffset;
		int length = event.lineText.length();
		
		Range range = new Range(startOffset,length);
		
		List<StyleRange> styles = getStylesForRange(range);
		event.styles = styles.toArray(new StyleRange[0]);
	}

	
	
	public StyleRange[] getAllStyleRanges(){
		List<Annotation> annList = new ArrayList<Annotation>();
		annList.addAll(annotations);
		
		List<Annotation> sortedAnnotations = getDisplayedAnnotations(annList);
		
		List<StyleRange> styleRanges = createStylesForAnnotations(sortedAnnotations);
		return styleRanges.toArray(new StyleRange[0]);
	}
	
	private List<StyleRange> getStylesForRange(Range range){
		List<Annotation> annotationsInRange = getAnnotationsInRange(range);
		
		List<Annotation> sortedAnnotations = getDisplayedAnnotations(annotationsInRange);
		
		List<StyleRange> styleRanges = createStylesForAnnotations(sortedAnnotations);
		return styleRanges;
	}


	public List<Annotation> getDisplayedAnnotations(
			List<Annotation> annotationsInRange) {
		List<Annotation> validAnnotations = getValidAnnotations(annotationsInRange);
		
		List<Annotation> selectedAnnotations = getSelectedAnnotations(validAnnotations);
		
		List<Annotation> sortedAnnotations = sortAnnotationsByOrder(selectedAnnotations);
		return sortedAnnotations;
	}
	
	


	private List<Annotation> getAnnotationsInRange(Range range) {
		
		List<Annotation> annList = new ArrayList<Annotation>();

		for(Annotation ann:annotations){
			if(isAnnotationIntersectingRange(ann,range))
				annList.add(ann);
		}
		return annList;
	}

	/**
	 * @param startoffset
	 * @param length
	 * @return
	 */
	protected List<Annotation> getValidAnnotations(List<Annotation> annotations) {
		List<Annotation> validAnnotations = new ArrayList<Annotation>();
		
		
		for(Annotation ann:annotations){
			if(!Activator.getDefault().getMiddleTier().isSystemAnnotation(ann))
				validAnnotations.add(ann);
		}
		
		return validAnnotations;
	}

	
	/**
	 * @param startoffset
	 * @param length
	 * @return
	 */
	private List<Annotation> getSelectedAnnotations(List<Annotation> annotations) {
		
		List<Annotation> selectedAnnotations = new ArrayList<Annotation>(); 
		
		if(selectedItems.isEmpty())
			return annotations;
		
		for(Annotation ann:annotations){
			if(isAnnotationSelected(ann))
				selectedAnnotations.add(ann);
		}
		
		return selectedAnnotations;
	}
	
	/**
	 * @param selectedAnnotations
	 * @return
	 */
	protected List<Annotation> sortAnnotationsByOrder(
			List<Annotation> annotations) {
		
		List<Annotation> sortedAnnotations = new ArrayList<Annotation>();
		
		for(Object orderElement:displayOrder){
			for(Annotation ann:annotations){
				if(ann.getAnnotationClass().getOntology().equals(orderElement) && !sortedAnnotations.contains(ann)){
					sortedAnnotations.add(ann);
				}
			}
		}
		Collections.reverse(sortedAnnotations);
		if(sortedAnnotations.isEmpty())
			return annotations;
		else
			return sortedAnnotations;
	}
	
	StyleRangeSorter styleRangeSorter  = new StyleRangeSorter();
	/**
	 * @param annList
	 * @return
	 */
	private List<StyleRange> createStylesForAnnotations(List<Annotation> annotationsToBeStyled) {
		List<StyleRange> styles = new ArrayList<StyleRange>();
		
		for(Annotation ann:annotationsToBeStyled){
			styles.add(getStyleForAnnotation(ann));
		}
		Collections.sort(styles,styleRangeSorter);
		return styles;
	}
	
	
	
	/**
	 * @param ann
	 * @param startOffset
	 * @param length
	 * @return
	 */
	private boolean isAnnotationIntersectingRange(Annotation ann,
			Range range) {
		
		//if beginning of annotation is within the line
		if(ann.getStartOffset() >= range.getStartOffset() && ann.getStartOffset() <= range.getEndOffset()){
			return true;
		}
		//or end of annotation is within the line 
		else if(ann.getEndOffset() >= range.getStartOffset() && ann.getEndOffset() <= range.getEndOffset()){
			return true;
		}
		else
			return false;
	}






	/**
	 * @param ann
	 * @return
	 */
	protected boolean isAnnotationSelected(Annotation ann) {
		for(Object selectedItem:selectedItems){
			if(selectedItem instanceof IClass && ann.getAnnotationClass().equals(selectedItem))
				return true;
			else if(selectedItem instanceof IOntology && ann.getAnnotationClass().getOntology().equals(selectedItem))
				return true;
		}
		
		return false;
	}


	


	/**
	 * @param styles
	 * @param ann
	 * @return 
	 */
	protected StyleRange getStyleForAnnotation(Annotation ann) {
		Color annotationColor;
		annotationColor = Aesthetics.getColorForAnnotationType(ann.getAnnotationClass());
		return new StyleRange(ann.getStartOffset(), ann.getLength(), null ,annotationColor);
	}




	public List<Annotation> getAnnotationsAtOffset(int offset) {
		List<Annotation> annList = new ArrayList<Annotation>();
		for(Annotation ann:annotations){
			if(offset >= ann.getStartOffset() && ann.getEndOffset() >= offset){
				annList.add(ann);
			}
		}
		
		return getDisplayedAnnotations(annList);
	}

	
	public void setAnnotations(SortedSet<Annotation> annotations) {
		this.annotations = annotations;
		
	}


	


	/**
	 * @param checkedItems
	 */
	public void setDisplayOrder(List<Object> displayOrder) {
		this.displayOrder = displayOrder;
	}

	/**
	 * @param selectedItems
	 */
	public void setSelectedItems(List<Object> selectedItems) {
		this.selectedItems = selectedItems;
		
	}

}
