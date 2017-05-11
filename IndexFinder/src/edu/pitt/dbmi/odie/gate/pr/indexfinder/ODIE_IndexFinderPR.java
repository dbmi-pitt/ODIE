/*
 *  IndexFinderPR.java
 *
 *
 * Copyright (c) 2000-2001, The University of Sheffield.
 *
 * This file is part of GATE (see http://gate.ac.uk/), and is free
 * software, licenced under the GNU Library General Public License,
 * Version 2, June1991.
 *
 * A copy of this licence is included in the distribution in the file
 * licence.html, and is also available at http://gate.ac.uk/gate/licence.html.
 *
 *  mitchellkj, 1/2/2008
 *
 *  $Id: IndexFinderPR.jav 2820 2001-11-14 17:15:43Z oana $
 */

package edu.pitt.dbmi.odie.gate.pr.indexfinder;

import edu.pitt.dbmi.odie.server.indexfinder.ODIE_IndexFinderAnnotation;
import edu.pitt.dbmi.odie.server.indexfinder.ODIE_IndexFinderGenericPipeComponent;
import edu.pitt.dbmi.odie.server.indexfinder.ODIE_IndexFinderInMemoryInf;
import edu.pitt.dbmi.odie.server.indexfinder.ODIE_IndexFinderNode;
import edu.pitt.dbmi.odie.server.indexfinder.ODIE_Phrase;
import edu.upmc.opi.caBIG.caTIES.creole.CaTIES_SortedAnnotationSet;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Factory;
import gate.FeatureMap;
import gate.Resource;
import gate.creole.ResourceInstantiationException;
import gate.creole.coref.AbstractCoreferencer;
import gate.util.InvalidOffsetException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * This class is the implementation of the resource INDEXFINDERPR.
 */
public class ODIE_IndexFinderPR extends AbstractCoreferencer {

	private static final long serialVersionUID = -5617890857784398153L;

	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ODIE_IndexFinderPR.class);

	private String coReferenceAnnotationSetName = "IndexFinderCoref";
	private CaTIES_SortedAnnotationSet sortedSentenceTokens = null;

	private ODIE_IndexFinderInMemoryInf inMemoryIndexFinder;
	private ArrayList<String> includedOntologyUris;

	private ODIE_IndexFinderGenericPipeComponent ifPipeComponent;

	private boolean isInitialized = false;

	public static void main(String[] args) {
		ODIE_IndexFinderPR ifPR = new ODIE_IndexFinderPR();
		try {
			ifPR.init();
			ifPR.execute();
		} catch (ResourceInstantiationException e) {
			e.printStackTrace();
		}
	}

	public ODIE_IndexFinderPR() {
		super("IndexFinderPR");
	}

	/** Initialise this resource, and return it. */
	public Resource init() throws ResourceInstantiationException {

		Resource resource = super.init();
		logger.debug("IndexFinderPR initializing...");

		if (!this.isInitialized) {

			if (this.inMemoryIndexFinder == null) {
				logger
						.error("InMemoryIndexFinder was NOT passed in from caller.");
			} else {
				logger.debug("InMemoryIndexFinder was passed in from caller.");
			}
			if (this.includedOntologyUris == null) {
				logger
						.error("includedOntologyUris was NOT passed in from caller.");
				this.includedOntologyUris = new ArrayList<String>();
			} else {
				logger.debug("includedOntologyUris was passed in from caller.");
			}

			this.ifPipeComponent = new ODIE_IndexFinderGenericPipeComponent();
			this.ifPipeComponent.setIncludedOntologyUris(includedOntologyUris);
			this.ifPipeComponent.setInMemoryIndexFinder(inMemoryIndexFinder);

			this.isInitialized = true;
		}
		logger.debug("[IndexFinderPR] Finished initializing...");

		return resource;

	} // init()

	public void reInit() throws ResourceInstantiationException {
		this.isInitialized = false;
		init();
	} // reInit()

	public void execute() {
		AnnotationSet defaultAnnotations = this.document.getAnnotations();
		if (defaultAnnotations == null || defaultAnnotations.isEmpty()) {
			logger.warn("No default set annotations.");
			return;
		}
		HashSet<String> sentenceAnnotationKeys = new HashSet<String>();
		sentenceAnnotationKeys.add("Sentence");
		sentenceAnnotationKeys.add("XSentence");
		AnnotationSet sentenceAnnotations = defaultAnnotations
				.get(sentenceAnnotationKeys);
		if (sentenceAnnotations == null || sentenceAnnotations.isEmpty()) {
			logger.warn("No sentence annotations.");
			return;
		}
		CaTIES_SortedAnnotationSet sortedSentences = new CaTIES_SortedAnnotationSet(
				sentenceAnnotations);
		for (Iterator<Annotation> sentenceAnnotationsIterator = sortedSentences
				.iterator(); sentenceAnnotationsIterator.hasNext();) {
			Annotation sentenceAnnotation = sentenceAnnotationsIterator.next();
			AnnotationSet sentenceTokens = defaultAnnotations.get("Token",
					sentenceAnnotation.getStartNode().getOffset(),
					sentenceAnnotation.getEndNode().getOffset());
			this.sortedSentenceTokens = new CaTIES_SortedAnnotationSet(
					sentenceTokens);
			ArrayList<ODIE_IndexFinderAnnotation> odieAnnots = genericallyWrapTokenAnnotations(sentenceTokens);

			this.ifPipeComponent.setSortedTokens(odieAnnots);
			this.ifPipeComponent.execute();

			unWrapOdieAnnotations(this.ifPipeComponent.getResultingConcepts());
			generateCoReferenceChains(this.ifPipeComponent.getResultingPhrases()) ;
		}
	}

	private ArrayList<ODIE_IndexFinderAnnotation> genericallyWrapTokenAnnotations(
			AnnotationSet sortedSentenceTokens) {
		ArrayList<ODIE_IndexFinderAnnotation> result = new ArrayList<ODIE_IndexFinderAnnotation>();
		for (Iterator<Annotation> tokenAnnotationsIterator = sortedSentenceTokens
				.iterator(); tokenAnnotationsIterator.hasNext();) {
			ODIE_IndexFinderAnnotation odieAnnot = new ODIE_IndexFinderAnnotation();
			ODIE_IndexFinderNode odieAnnotSNode = new ODIE_IndexFinderNode();
			ODIE_IndexFinderNode odieAnnotENode = new ODIE_IndexFinderNode();

			Annotation token = tokenAnnotationsIterator.next();
			String tokenKind = (String) token.getFeatures().get("kind");
			String tokenString = (String) token.getFeatures().get("string");

			odieAnnotSNode.setOffset(token.getStartNode().getOffset());
			odieAnnotENode.setOffset(token.getEndNode().getOffset());
			odieAnnot.setStartNode(odieAnnotSNode);
			odieAnnot.setEndNode(odieAnnotENode);
			odieAnnot.setAnnotationId(token.getId()) ;
			odieAnnot.setAnnotationSetName("Default");
			odieAnnot.setAnnotationTypeName(token.getType());
			odieAnnot.getFeatures().put("kind", tokenKind);
			odieAnnot.getFeatures().put("string", tokenString);

			result.add(odieAnnot);
		}
		return result;
	}

	private void unWrapOdieAnnotations(
			ArrayList<ODIE_IndexFinderAnnotation> odieAnnots) {
		for (Iterator<ODIE_IndexFinderAnnotation> odieAnnotIterator = odieAnnots
				.iterator(); odieAnnotIterator.hasNext();) {
			ODIE_IndexFinderAnnotation odieAnnot = odieAnnotIterator.next();
			FeatureMap featureMap = Factory.newFeatureMap();
			featureMap.put("cui", odieAnnot.getFeatures().get("cui"));
			featureMap.put("sui", odieAnnot.getFeatures().get("sui"));
			featureMap.put("str", odieAnnot.getFeatures().get("str"));
			featureMap.put("cn",  odieAnnot.getFeatures().get("str"));
			try {
				this.document.getAnnotations().add(
						odieAnnot.getStartNode().getOffset(),
						odieAnnot.getEndNode().getOffset(), "Concept", featureMap);
			} catch (InvalidOffsetException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean excludeClassBasedOnFilter(String cn) {
		// displayIncludedOntologyUris() ;
		boolean result = false;
		if (cn == null || cn.indexOf("#") == -1) {
			logger.error("cn is null");
		} else {
			String ontologyUri = cn.substring(0, cn.lastIndexOf("#"));
			result = !this.includedOntologyUris.contains(ontologyUri);
		}

		return result;
	}

	private void displayIncludedOntologyUris() {
		for (String uri : this.includedOntologyUris) {
			logger.debug(uri);
		}
	}

	/** should clear all internal data of the resource. Does nothing now */
	public void cleanup() {

	}

	/**
	 * Checks whether this PR has been interrupted since the last time its
	 * {@link #execute()} method was called.
	 */
	public synchronized boolean isInterrupted() {
		return interrupted;
	}

	/**
	 * Notifies this PR that it should stop its execution as soon as possible.
	 */
	public synchronized void interrupt() {
		interrupted = true;
	}
	
	
	private void generateCoReferenceChains(List<ODIE_Phrase> phrases) {
		for (ODIE_Phrase phrase : phrases) {
			ArrayList<Annotation> gateTokens = new ArrayList<Annotation>() ;
			for (ODIE_IndexFinderAnnotation odieAnnot : phrase.getAnnotationList()) {
				Annotation tokenAnnot = this.document.getAnnotations().get(odieAnnot.getAnnotationId()) ;
				gateTokens.add(tokenAnnot) ;
			}
			if (!gateTokens.isEmpty()) {
				generateCoReferenceChain(gateTokens) ;
			}
		}
	}

	private void generateCoReferenceChain(List<Annotation> sortedTokens) {
		if (sortedTokens.size() < 2) {
			return; // must have two annotations to coreference.
		} else { // make sure the doc has a place to keep global corefs
			if (!document.getFeatures()
					.containsKey(DOCUMENT_COREF_FEATURE_NAME)) {
				document.getFeatures().put(DOCUMENT_COREF_FEATURE_NAME,
						new HashMap<String, ArrayList<ArrayList<Integer>>>());
			}
		}
		HashMap<Annotation, Annotation> ana2ant = new HashMap<Annotation, Annotation>();
		Iterator<Annotation> tokenAnnotationIterator = sortedTokens.iterator();
		Annotation antecedentAnnotation = (Annotation) copyAnnotationToCoRefSet(tokenAnnotationIterator
				.next());
		while (tokenAnnotationIterator.hasNext()) {
			Annotation anaphorAnnotation = tokenAnnotationIterator.next();
			String antecedentWord = (String) antecedentAnnotation.getFeatures()
					.get("string");
			String antecedentType = antecedentAnnotation.getType();
			String anaphorWord = (String) anaphorAnnotation.getFeatures().get(
					"string");
			String anaphorType = anaphorAnnotation.getType();
			logger
					.debug("Adding ana --> ant pair " + anaphorType + " "
							+ anaphorWord + " " + antecedentType + " "
							+ antecedentWord);
			ana2ant.put(anaphorAnnotation, antecedentAnnotation);

			generateCorefChains(ana2ant);

			antecedentAnnotation = document.getAnnotations(
					getAnnotationSetName()).get(
					anaphorAnnotation.getStartNode().getOffset(),
					anaphorAnnotation.getEndNode().getOffset()).iterator()
					.next();

			ana2ant.clear();
		}
	}

	private Annotation copyAnnotationToCoRefSet(Annotation defaultSetAnnotation) {
		Annotation corefSetAnnotation = null;
		try {
			Integer movedAntecedentAnnotationId = document.getAnnotations(
					getAnnotationSetName()).add(
					defaultSetAnnotation.getStartNode().getOffset(),
					defaultSetAnnotation.getEndNode().getOffset(),
					defaultSetAnnotation.getType(),
					defaultSetAnnotation.getFeatures());
			corefSetAnnotation = document
					.getAnnotations(getAnnotationSetName()).get(
							movedAntecedentAnnotationId);
		} catch (InvalidOffsetException e) {
			e.printStackTrace();
		}
		return corefSetAnnotation;
	}

	@Override
	public String getAnnotationSetName() {
		return this.coReferenceAnnotationSetName;
	}

	@Override
	public void setAnnotationSetName(String annotationSetName) {
		this.coReferenceAnnotationSetName = annotationSetName;
	}

	public ODIE_IndexFinderInMemoryInf getInMemoryIndexFinder() {
		return inMemoryIndexFinder;
	}

	public void setInMemoryIndexFinder(
			ODIE_IndexFinderInMemoryInf inMemoryIndexFinder) {
		this.inMemoryIndexFinder = inMemoryIndexFinder;
	}

	public ArrayList<String> getIncludedOntologyUris() {
		return includedOntologyUris;
	}

	public void setIncludedOntologyUris(ArrayList<String> includedOntologyUris) {
		this.includedOntologyUris = includedOntologyUris;
	}

} // class IndexFinderPR
