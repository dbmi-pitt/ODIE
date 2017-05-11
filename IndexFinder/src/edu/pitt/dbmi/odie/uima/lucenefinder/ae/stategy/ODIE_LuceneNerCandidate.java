package edu.pitt.dbmi.odie.uima.lucenefinder.ae.stategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import edu.pitt.dbmi.odie.server.indexfinder.ODIE_IndexFinderAnnotation;
import edu.pitt.dbmi.odie.server.indexfinder.ODIE_IndexFinderNode;

public abstract class ODIE_LuceneNerCandidate {

	public static enum CandidateStatus {
		ACCUMULATING, REJECTED, ATTAINED
	}

	public String odieCui;
	public String cName;
	public String uri;
	public CandidateStatus candidateStatus = CandidateStatus.ACCUMULATING;
	public final HashSet<String> wordsMatched = new HashSet<String>();
	public ArrayList<ODIE_IndexFinderAnnotation> annotations = new ArrayList<ODIE_IndexFinderAnnotation>();
	public int acquiredNumberOfWords;
	public String targetNumberOfWordsAsString;
	public int targetNumberOfWords;
	public int sPos = 0;

	// Precondition these attributes should only be set once subsequent tallies
	// should have the same information as it will be duplicated for each
	// word of the concept term in the index.
	public String umlsCui = "UNKNOWN" ;
	public String umlsTuis = "UNKNOWN" ; // space separated when more than one
	public int cTakesSemanticType = -1;

	public abstract void tally(ODIE_IndexFinderAnnotation annotation, int caret);

	public abstract boolean isAttained();

	public abstract CandidateStatus getStatus(int caret);
	
	public String getStorageKey() {
		return this.odieCui + "_" + this.targetNumberOfWordsAsString;
	}

	public ODIE_IndexFinderAnnotation generateCoveringAnnotation() {
		ODIE_IndexFinderAnnotation sAnnotation = this.annotations.get(0);
		ODIE_IndexFinderAnnotation eAnnotation = this.annotations
				.get(this.annotations.size() - 1);
		HashMap<String, Object> featureMap = new HashMap<String, Object>();
		featureMap.put(ODIE_LuceneFinderStrategyConstants.FIELD_CUI, odieCui);
		featureMap.put(ODIE_LuceneFinderStrategyConstants.FIELD_STR, cName);
		featureMap.put(ODIE_LuceneFinderStrategyConstants.FIELD_CN, uri);
		featureMap.put(ODIE_LuceneFinderStrategyConstants.FIELD_UMLS_CUI, umlsCui);
		featureMap.put(ODIE_LuceneFinderStrategyConstants.FIELD_UMLS_TUIS, umlsTuis);
		featureMap.put(ODIE_LuceneFinderStrategyConstants.FIELD_CTAKES_SEMANTIC_TYPE, cTakesSemanticType + "");
		ODIE_IndexFinderAnnotation conceptAnnotation = new ODIE_IndexFinderAnnotation();
		ODIE_IndexFinderNode sNode = new ODIE_IndexFinderNode();
		ODIE_IndexFinderNode eNode = new ODIE_IndexFinderNode();
		sNode.setOffset(sAnnotation.getStartNode().getOffset());
		eNode.setOffset(eAnnotation.getEndNode().getOffset());
		conceptAnnotation.setStartNode(sNode);
		conceptAnnotation.setEndNode(eNode);
		conceptAnnotation.setAnnotationTypeName("Concept");
		conceptAnnotation.setAnnotationSetName("Default");
		conceptAnnotation.setFeatures(featureMap);
		return conceptAnnotation;
	}

	public void loadCandidateFromIndexedDocument(Document doc,
			ODIE_IndexFinderAnnotation annotation, int caret) {
		this.odieCui = nullSafeGetDocumentField(doc, ODIE_LuceneFinderStrategyConstants.FIELD_ODIE_CUI) ;
		this.cName = nullSafeGetDocumentField(doc, ODIE_LuceneFinderStrategyConstants.FIELD_CLASS_NAME) ;
		this.uri = nullSafeGetDocumentField(doc, ODIE_LuceneFinderStrategyConstants.FIELD_URI) ;
		this.targetNumberOfWordsAsString = doc.getField(ODIE_LuceneFinderStrategyConstants.FIELD_WORD_COUNT)
		.stringValue();
		this.targetNumberOfWords = Integer.valueOf(this.targetNumberOfWordsAsString);
		this.umlsCui = nullSafeGetDocumentField(doc, ODIE_LuceneFinderStrategyConstants.FIELD_UMLS_CUI) ;
		this.umlsTuis = nullSafeGetDocumentField(doc, ODIE_LuceneFinderStrategyConstants.FIELD_UMLS_TUIS) ;
		String cTakesSemanticTypeAsString = nullSafeGetDocumentField(doc, ODIE_LuceneFinderStrategyConstants.FIELD_CTAKES_SEMANTIC_TYPE) ;
				
		this.cTakesSemanticType = (!cTakesSemanticTypeAsString.matches("\\d+")) ? -1
				: Integer.valueOf(cTakesSemanticTypeAsString);
		this.acquiredNumberOfWords = 1;
		this.wordsMatched.add(annotation.getNormalizedForm());
		this.annotations.add(annotation);
		this.sPos = caret;
	}
	
	
	private String nullSafeGetDocumentField(Document doc, String fieldName) {
		String result = "UNKNOWN" ;
		Field field = doc.getField(fieldName) ;
		if (field != null) {
			result = field.stringValue() ;
		}
		return result ;
	}

}
