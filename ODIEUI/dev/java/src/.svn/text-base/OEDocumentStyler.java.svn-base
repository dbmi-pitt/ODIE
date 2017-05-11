package edu.pitt.dbmi.odie.ui.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;

import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.model.Annotation;
import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.model.Suggestion;
import edu.pitt.dbmi.odie.model.utils.ChildAnnotation;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.dbmi.odie.ui.views.providers.AnnotationTypeTreeContentProvider;
import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IOntology;

public class OEDocumentStyler extends DocumentStyler {

	private List<Suggestion> suggestions = null;
	
	/**
	 * @param da
	 * @param equals
	 */
	public OEDocumentStyler(AnalysisDocument da) {
		super(da);
//		this.suggestions = da.getAnalysis().getStatistics().getPhraseSuggestions(da);
	}

	protected List<Annotation> getValidAnnotations(List<Annotation> annotations) {
		List<Annotation> validAnnotations = new ArrayList<Annotation>();
		MiddleTier middleTier = Activator.getDefault().getMiddleTier();
		for(Annotation ann:annotations){
			if( isSuggestion(ann) || middleTier.isLSPAnnotation(ann)
					|| !middleTier.isSystemAnnotation(ann)){
				validAnnotations.add(ann);
			}
		}
		return validAnnotations;
	}
	
	/**
	 * @param selectedAnnotations
	 * @return
	 */
	protected List<Annotation> sortAnnotationsByOrder(
			List<Annotation> annotations) {
		
		List<Annotation> sortedAnnotations = new ArrayList<Annotation>();
		
		for(Object orderElement:displayOrder){
			
			if(orderElement.equals(AnnotationTypeTreeContentProvider.SUGGESTIONS_LABEL)){
				sortedAnnotations.addAll(getSuggestionAnnotations(annotations));
			}
			else if(orderElement.equals(AnnotationTypeTreeContentProvider.NER_LABEL)){
				sortedAnnotations.addAll(getNERAnnotations(annotations));
			}
			else if(orderElement.equals(AnnotationTypeTreeContentProvider.LSP_LABEL)){
				sortedAnnotations.addAll(getLSPAnnotations(annotations));
			}
		}
		
		Collections.reverse(sortedAnnotations);
		
		if(sortedAnnotations.isEmpty())
			return annotations;
		else
			return sortedAnnotations;
	}

	/**
	 * @param annotations
	 * @param sortedAnnotations
	 * @param middleTier
	 */
	private List<Annotation> getLSPAnnotations(List<Annotation> annotations) {
		MiddleTier middleTier = Activator.getDefault().getMiddleTier();
		List<Annotation> lspAnnotations = new ArrayList<Annotation>();
		for(Annotation ann:annotations){
			if(MiddleTier.isLSPAnnotation(ann) && !lspAnnotations.contains(ann)){
				lspAnnotations.add(ann);
			}
		}
		
		return lspAnnotations;
	}

	/**
	 * @param annotations
	 * @param sortedAnnotations
	 * @param middleTier
	 */
	private List<Annotation> getNERAnnotations(List<Annotation> annotations) {
		MiddleTier middleTier = Activator.getDefault().getMiddleTier();
		List<Annotation> nerAnnotations = new ArrayList<Annotation>();
		for(Annotation ann:annotations){
			if(!middleTier.isSystemAnnotation(ann) && !nerAnnotations.contains(ann)){
				nerAnnotations.add(ann);
			}
		}
		return nerAnnotations;
	}

	/**
	 * @param annotations
	 * @param sortedAnnotations
	 */
	private List<Annotation> getSuggestionAnnotations(List<Annotation> annotations) {
		List<Annotation> suggestionAnnotations = new ArrayList<Annotation>();
		
		for(Annotation ann:annotations){
			if(isSuggestion(ann) && !suggestionAnnotations.contains(ann)){
				suggestionAnnotations.add(ann);
			}
		}
		return suggestionAnnotations;
	}
		
	/**
	 * @param ann
	 * @return
	 */
	private boolean isSuggestion(Annotation ann) {
		return getSuggestion(ann)!=null;
	}

	protected boolean isAnnotationSelected(Annotation ann) {
		for(Object selectedItem:selectedItems){
			if(selectedItem instanceof String){
				if(selectedItem.toString().equals(AnnotationTypeTreeContentProvider.SUGGESTIONS_LABEL)){
					if(isSuggestion(ann))
						return true;
				}
				else if(selectedItem.toString().equals(AnnotationTypeTreeContentProvider.LSP_LABEL)){
					if(MiddleTier.isLSPAnnotation(ann))
						return true;
				}
				else if(selectedItem.toString().equals(AnnotationTypeTreeContentProvider.NER_LABEL)){
					if(!Activator.getDefault().getMiddleTier().isSystemAnnotation(ann))
						return true;
				}
			}
			else if(selectedItem instanceof Suggestion){
				Suggestion s = (Suggestion)selectedItem;
				return s.containsAnnotation(ann);
			}
			else if(selectedItem instanceof IClass && ann.getAnnotationClass().equals(selectedItem))
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
		MiddleTier middleTier = Activator.getDefault().getMiddleTier();
		
		int startOffset = ann.getStartOffset();
		int length = ann.getLength();
		
		if(isSuggestion(ann)){
			annotationColor = Aesthetics.getColorForAnnotationType(AnnotationTypeTreeContentProvider.SUGGESTIONS_LABEL);
			ChildAnnotation ca = getChildAnnotation(ann);
			if(ca!=null){
				startOffset = ca.getStartOffset();
				length = ca.getLength();
			}
		}
		else if(middleTier.isLSPAnnotation(ann))
			annotationColor = Aesthetics.getColorForAnnotationType(AnnotationTypeTreeContentProvider.LSP_LABEL);
		else
			annotationColor = Aesthetics.getColorForAnnotationType(AnnotationTypeTreeContentProvider.NER_LABEL);
		
		return new StyleRange(startOffset, length, null ,annotationColor);
	}

	private ChildAnnotation getChildAnnotation(Annotation ann) {
		Suggestion s = getSuggestion(ann);
		
		return s.getChildAnnotation(ann);
	}


	private Suggestion getSuggestion(Annotation ann) {
		for(Suggestion sugg:suggestions){
			if(sugg.containsAnnotation(ann))
				return sugg;
		}
		return null;
	}
}