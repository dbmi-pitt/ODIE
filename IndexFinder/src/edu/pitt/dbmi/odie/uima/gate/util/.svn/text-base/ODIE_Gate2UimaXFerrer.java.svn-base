package edu.pitt.dbmi.odie.uima.gate.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.pitt.dbmi.odie.uima.gate.resource.ODIE_GateResource;
import edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateAnnotation;
import edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateFeature;
import gate.AnnotationSet;
import gate.Document;
import gate.FeatureMap;

public class ODIE_Gate2UimaXFerrer {

	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ODIE_Gate2UimaXFerrer.class);

	private ODIE_GateResource factoryDelegate;

	private HashMap<Integer, Annotation> annotsToRemove = new HashMap<Integer, Annotation>();

	private JCas aJCas;
	private Document gateDocument;

	public void transferDocumentToJCas(Document gateDocument, JCas aJCas,
			HashMap<Integer, Annotation> annotsToRemove) {

		logger.debug("Calling transferDocumentToJCas");

		this.gateDocument = gateDocument;
		this.aJCas = aJCas;
		this.annotsToRemove = annotsToRemove;

		createUimaDualsForNewGateAnnotations();
		removeAnnotationScrubbedByGateProcessor();
	}

	private void createUimaDualsForNewGateAnnotations() {
		AnnotationSet defaultAnnotations = gateDocument.getAnnotations();
		Iterator<gate.Annotation> gateAnnotationIterator = defaultAnnotations
				.iterator();
		while (gateAnnotationIterator.hasNext()) {
			gate.Annotation gateAnnot = gateAnnotationIterator.next();
			Annotation uimAnnotToUpdate = this.annotsToRemove.remove(gateAnnot
					.getId());
			if (uimAnnotToUpdate == null) {
				createNewUimaAnnotation(gateAnnot);
			} else {
				// This annotation may need updating with respect it feature
				// vector
				// For now we will assume that a GATE PR will not manipulate
				// incoming
				// annotations other than scrubbing them.
			}
		}
	}

	private void removeAnnotationScrubbedByGateProcessor() {
		try {
			for (Annotation uimaAnnot : this.annotsToRemove.values()) {
				// System.out.println("Removing unpreserved uima annotation ...");
				// System.out.println(uimaAnnot);
				if (uimaAnnot instanceof ODIE_GateAnnotation) {
					List<ODIE_GateFeature> uimaFeatureAnnots = fetchFeaturesForAnnotation((ODIE_GateAnnotation) uimaAnnot);
					for (ODIE_GateFeature uimaFeatureAnnot : uimaFeatureAnnots) {
						uimaFeatureAnnot.removeFromIndexes(aJCas);
					}
				}
				uimaAnnot.removeFromIndexes(aJCas);
			}
			this.annotsToRemove.clear();
		} catch (Exception x) {
			x.printStackTrace();
		}

	}

	private void createNewUimaAnnotation(gate.Annotation gateAnnot) {
		ODIE_GateAnnotation newAnnotation = new ODIE_GateAnnotation(aJCas);
		newAnnotation.setGateAnnotationId(gateAnnot.getId());
		newAnnotation.setGateAnnotationType(gateAnnot.getType());
		newAnnotation.setBegin(gateAnnot.getStartNode().getOffset().intValue());
		newAnnotation.setEnd(gateAnnot.getEndNode().getOffset().intValue());
		for (Iterator<Object> gateFeatureKeys = gateAnnot.getFeatures()
				.keySet().iterator(); gateFeatureKeys.hasNext();) {
			String gateFeatureKey = (String) gateFeatureKeys.next();
			String gateFeatureValue = String.valueOf(gateAnnot.getFeatures()
					.get(gateFeatureKey));
			ODIE_GateFeature uimaFeature = new ODIE_GateFeature(aJCas);
			uimaFeature.setGateAnnotationId(gateAnnot.getId());
			uimaFeature.setGateFeatureKey(gateFeatureKey);
			uimaFeature.setGateFeatureValue(gateFeatureValue);
			uimaFeature.addToIndexes();
		}
		newAnnotation.addToIndexes();
	}

	private String stringifyGateAnnotation(gate.Annotation annot) {
		StringBuffer sb = new StringBuffer();
		sb.append("\nid = " + annot.getId() + "\n");
		sb.append("Type = " + annot.getType() + "\n");
		sb.append("sPos = " + annot.getStartNode().getOffset() + "\n");
		sb.append("ePos = " + annot.getEndNode().getOffset() + "\n");
		FeatureMap features = annot.getFeatures();
		if (features != null && !features.isEmpty()) {
			for (Iterator<Object> keyIterator = features.keySet().iterator(); keyIterator
					.hasNext();) {
				String key = (String) keyIterator.next();
				String value = String.valueOf(features.get(key));
				if (key != null && value != null && !value.equals("null")) {
					sb.append("Key: " + key + " ==> " + value + "\n");
				}
			}
		}
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	private List<ODIE_GateFeature> fetchFeaturesForAnnotation(
			ODIE_GateAnnotation uimaAnnot) {
		List<ODIE_GateFeature> result = new ArrayList<ODIE_GateFeature>();
		FSIndex uimaAnnotIndex = this.aJCas
				.getAnnotationIndex(ODIE_GateFeature.type);
		Iterator<ODIE_GateFeature> uimaAnnotIter = uimaAnnotIndex.iterator();
		while (uimaAnnotIter.hasNext()) {
			ODIE_GateFeature feature = uimaAnnotIter.next();
			if (feature.getGateAnnotationId() == uimaAnnot
					.getGateAnnotationId()) {
				result.add(feature);
			}
		}
		return result;
	}

	public ODIE_GateResource getFactoryDelegate() {
		return factoryDelegate;
	}

	public void setFactoryDelegate(ODIE_GateResource factoryDelegate) {
		this.factoryDelegate = factoryDelegate;
	}

}
