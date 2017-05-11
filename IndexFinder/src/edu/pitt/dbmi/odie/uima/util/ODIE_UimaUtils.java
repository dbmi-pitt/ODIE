package edu.pitt.dbmi.odie.uima.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;

import edu.mayo.bmi.uima.chunker.type.Chunk;
import edu.mayo.bmi.uima.core.type.NamedEntity;
import edu.mayo.bmi.uima.core.type.UmlsConcept;
import edu.mayo.bmi.uima.core.type.WordToken;
import edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateAnnotation;
import edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateFeature;

public class ODIE_UimaUtils {

	private static final String[] englishCommonWords = { "the", "of", "and",
			"to", "a", "in", "that", "is", "was", "he", "for", "it", "with",
			"as", "his", "on", "be", "at", "by", "i", "this", "had", "not",
			"are", "but", "from", "or", "have", "an", "they", "which", "one",
			"were", "you", "all", "her", "she", "there", "would", "their",
			"we", "him", "been", "has", "when", "who", "will", "no", "more",
			"if", "out", "its", "so", "up", "said", "what", "about", "than",
			"into", "them", "can", "only", "other", "time", "new", "some",
			"could", "these", "two", "may", "first", "then", "do", "any",
			"like", "my", "now", "over", "such", "our", "man", "me", "even",
			"most", "made", "after", "also", "well", "did", "many", "before",
			"must", "years", "back", "through", "much", "where", "your", "way",
			"down", "should", "because", "long", "each", "just", "state",
			"those", "too", "how", "little", "good", "make", "very", "still",
			"see", "own", "work", "day", "get", "here", "between", "both",
			"being", "under", "never", "know", "same", "last", "another",
			"while", "us", "off", "might", "great", "go", "come", "since",
			"against", "right", "came", "take", "used", "himself", "few",
			"use", "place", "during", "without", "again", "around", "however",
			"found", "thought", "went", "say", "every", "don't", "does", "got",
			"until", "always", "away", "something", "less", "through", "put",
			"think", "called", "set", "almost", "enough", "end", "took", "yet",
			"better", "nothing", "told", "going", "why", "didn't", "look",
			"find", "asked", "later", "next", "knew", "give", "toward", "let",
			"given", "per", "several", "possible", "rather", "among", "often",
			"early", "things", "looked", "ever", "become", "best", "need",
			"within", "felt", "along", "saw", "least", "family", "others",
			"thing", "seemed", "want", "done", "although", "whole", "certain",
			"different", "kind", "began", "perhaps", "times", "itself",
			"whether", "either", "gave", "across", "taken", "anything",
			"having", "seen", "i'm", "really", "tell", "making", "sure",
			"themselves", "together", "full", "shall", "held", "known", "keep",
			"probably", "seems", "behind", "cannot", "brought", "whose",
			"self", "heard", "ago", "became", "available", "am", "ill", "seem",
			"wanted", "necessary", "following", "sometimes", "feel", "provide",
			"therefore", "evidence", "believe", "says", "nor", "that's",
			"everything", "except", "particular", "recent", "data", "person",
			"beyond", "coming", "else", "couldn't", "can't", "including",
			"actually", "shown", "especially", "wasn't", "likely",
			"particularly", "whom", "below", "certainly", "maybe", "continued",
			"generally", "wouldn't", "clearly", "somewhat", "apparently",
			"he's" };

	public static String[] getEnglishCommonWords() {
		return englishCommonWords;
	}

	public static List<String> englishCommonWordsList = Arrays
			.asList(englishCommonWords);

	public static String filterWord(String word, int lengthBoundry) {
		String result = null;
		if (word.toLowerCase().matches("^[a-z-][a-z-\\s]+$")
				&& word.length() > lengthBoundry
		// && !englishCommonWordsList.contains(word.toLowerCase())
		) {
			result = word;
		}
		return result;
	}

	public static String filterTerm(String term, int lengthBoundry) {
		String result = null;
		if (term.toLowerCase().matches("^[a-zA-Z][a-zA-Z-\\s]*$")
				&& (lengthBoundry < 0 || term.length() > lengthBoundry)
				&& !englishCommonWordsList.contains(term)) {
			result = term;
		}
		return result;
	}

	public static String getNerState(Annotation srcAnnot,
			ArrayList<NamedEntity> nerAnnotations) {
		//
		// The default state when there is no overlap
		//
		String newState = "NERNegative";
		//
		// NERPartial when at least one NERs partially overlaps with the
		// annotation.
		//
		for (NamedEntity nerAnnotation : nerAnnotations) {
			long sPos = srcAnnot.getBegin();
			long ePos = srcAnnot.getEnd();
			long nerSPos = nerAnnotation.getBegin();
			long nerEPos = nerAnnotation.getEnd();
			if ((sPos >= nerSPos && sPos <= nerEPos)
					|| (ePos >= nerSPos && ePos <= nerEPos)) {
				newState = "NERPartial" + "["
						+ getUriFromNamedEntity(nerAnnotation) + "]";
				break;
			}
		}
		//
		// NERPositive when at least one NER over lays the annotation exactly.
		//
		for (NamedEntity nerAnnotation : nerAnnotations) {
			long sPos = srcAnnot.getBegin();
			long ePos = srcAnnot.getEnd();
			long nerSPos = nerAnnotation.getBegin();
			long nerEPos = nerAnnotation.getEnd();
			if (sPos == nerSPos && ePos == nerEPos) {
				newState = "NERPositive" + "["
						+ getUriFromNamedEntity(nerAnnotation) + "]";
				break;
			}
		}
		return newState;
	}

	public static String getUriFromNamedEntity(NamedEntity ne) {
		String uri = null;
		FSArray conceptArr = ne.getOntologyConceptArr();
		if (conceptArr != null) {
			UmlsConcept concept = (UmlsConcept) conceptArr.get(0);
			uri = concept.getCodingScheme() + "#" + concept.getCode();
		}
		return uri;
	}

	public static String parseUriFromGenSymStateValue(String nerState) {
		String uri = "UNDEFINED";
		try {
			int leftBracketPos = nerState.indexOf("[");
			int rightBracketPos = nerState.lastIndexOf("]");
			uri = nerState.substring(leftBracketPos + 1, rightBracketPos);
		} catch (Exception x) {
			;
		}
		return uri;
	}
	
	public static String getNpAnnotationState(WordToken srcAnnot,
			ArrayList<Annotation> npAnnotations) {
		String newState = "NPNegative";
		for (Annotation npAnnotation : npAnnotations) {
			long srcAnnotSPos = srcAnnot.getBegin();
			long srcAnnotEPos = srcAnnot.getEnd();
			long npSPos = npAnnotation.getBegin();
			long npEPos = npAnnotation.getEnd();
			if ((srcAnnotSPos >= npSPos && srcAnnotSPos <= npEPos)
					|| (srcAnnotEPos >= npSPos && srcAnnotEPos <= npEPos)) {
				newState = "NPPartial";
				break;
			}
		}
		for (Annotation npAnnotation : npAnnotations) {
			long srcAnnotSPos = srcAnnot.getBegin();
			long srcAnnotEPos = srcAnnot.getEnd();
			long npSPos = npAnnotation.getBegin();
			long npEPos = npAnnotation.getEnd();
			if ((srcAnnotSPos >= npSPos && srcAnnotSPos <= npEPos)
					&& (srcAnnotEPos >= npSPos && srcAnnotEPos <= npEPos)) {
				newState = "NPPositive";
				break;
			}
		}
		return newState;
	}

	public static String getNpState(WordToken srcAnnot,
			ArrayList<Chunk> npAnnotations) {
		String newState = "NPNegative";
		for (Chunk npAnnotation : npAnnotations) {
			long srcAnnotSPos = srcAnnot.getBegin();
			long srcAnnotEPos = srcAnnot.getEnd();
			long npSPos = npAnnotation.getBegin();
			long npEPos = npAnnotation.getEnd();
			if ((srcAnnotSPos >= npSPos && srcAnnotSPos <= npEPos)
					|| (srcAnnotEPos >= npSPos && srcAnnotEPos <= npEPos)) {
				newState = "NPPartial";
				break;
			}
		}
		for (Chunk npAnnotation : npAnnotations) {
			long srcAnnotSPos = srcAnnot.getBegin();
			long srcAnnotEPos = srcAnnot.getEnd();
			long npSPos = npAnnotation.getBegin();
			long npEPos = npAnnotation.getEnd();
			if ((srcAnnotSPos >= npSPos && srcAnnotSPos <= npEPos)
					&& (srcAnnotEPos >= npSPos && srcAnnotEPos <= npEPos)) {
				newState = "NPPositive";
				break;
			}
		}
		return newState;
	}

	public static String getTransientNpState(WordToken srcAnnot,
			ArrayList<ODIE_GateAnnotation> npAnnotations) {
		String newState = "NPNegative";
		for (ODIE_GateAnnotation npAnnotation : npAnnotations) {
			long srcAnnotSPos = srcAnnot.getBegin();
			long srcAnnotEPos = srcAnnot.getEnd();
			long npSPos = npAnnotation.getBegin();
			long npEPos = npAnnotation.getEnd();
			if ((srcAnnotSPos >= npSPos && srcAnnotSPos <= npEPos)
					|| (srcAnnotEPos >= npSPos && srcAnnotEPos <= npEPos)) {
				newState = "NPPartial";
				break;
			}
		}
		for (ODIE_GateAnnotation npAnnotation : npAnnotations) {
			long srcAnnotSPos = srcAnnot.getBegin();
			long srcAnnotEPos = srcAnnot.getEnd();
			long npSPos = npAnnotation.getBegin();
			long npEPos = npAnnotation.getEnd();
			if ((srcAnnotSPos >= npSPos && srcAnnotSPos <= npEPos)
					&& (srcAnnotEPos >= npSPos && srcAnnotEPos <= npEPos)) {
				newState = "NPPositive";
				break;
			}
		}
		return newState;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<NamedEntity> pullNamedEntitiesFromJCas(JCas cas) {
		ArrayList<NamedEntity> result = new ArrayList<NamedEntity>();
		FSIndex namedEntityIndex = cas.getAnnotationIndex(NamedEntity.type);
		Iterator<NamedEntity> namedEntityIterator = namedEntityIndex.iterator();
		while (namedEntityIterator.hasNext()) {
			NamedEntity sentenceAnnot = (NamedEntity) namedEntityIterator
					.next();
			result.add(sentenceAnnot);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Chunk> pullSubsumedCTakesNPsFromJCas(JCas cas,
			Annotation subsumingAnnotation) {
		ArrayList<Chunk> result = new ArrayList<Chunk>();
		FSIndex chunkIndex = cas.getAnnotationIndex(Chunk.type);
		Iterator<Chunk> chunkIterator = chunkIndex.iterator();
		while (chunkIterator.hasNext()) {
			Chunk theChunk = (Chunk) chunkIterator.next();
			String chunkType = theChunk.getChunkType();
			if (chunkType.equals("NP")
					&& theChunk.getBegin() >= subsumingAnnotation.getBegin()
					&& theChunk.getEnd() <= subsumingAnnotation.getEnd()) {
				result.add(theChunk);
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Chunk> pullCTakesNPsFromJCas(JCas cas) {
		ArrayList<Chunk> result = new ArrayList<Chunk>();
		FSIndex chunkIndex = cas.getAnnotationIndex(Chunk.type);
		Iterator<Chunk> chunkIterator = chunkIndex.iterator();
		while (chunkIterator.hasNext()) {
			Chunk theChunk = (Chunk) chunkIterator.next();
			String chunkType = theChunk.getChunkType();
			if (chunkType.equals("NP")) {
				result.add(theChunk);
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<ODIE_GateAnnotation> pullSubsumedGateNPsFromJCas(
			JCas cas, Annotation subsumingAnnotation) {
		ArrayList<ODIE_GateAnnotation> result = new ArrayList<ODIE_GateAnnotation>();
		FSIndex chunkIndex = cas.getAnnotationIndex(ODIE_GateAnnotation.type);
		Iterator<ODIE_GateAnnotation> chunkIterator = chunkIndex.iterator();
		while (chunkIterator.hasNext()) {
			ODIE_GateAnnotation theChunk = (ODIE_GateAnnotation) chunkIterator
					.next();
			String chunkType = theChunk.getGateAnnotationType();
			if (chunkType.equals("TransientNP")
					&& theChunk.getBegin() >= subsumingAnnotation.getBegin()
					&& theChunk.getEnd() <= subsumingAnnotation.getEnd()) {
				result.add(theChunk);
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<ODIE_GateAnnotation> pullGateNPsFromJCas(JCas cas) {
		ArrayList<ODIE_GateAnnotation> result = new ArrayList<ODIE_GateAnnotation>();
		FSIndex chunkIndex = cas.getAnnotationIndex(ODIE_GateAnnotation.type);
		Iterator<ODIE_GateAnnotation> chunkIterator = chunkIndex.iterator();
		while (chunkIterator.hasNext()) {
			ODIE_GateAnnotation theChunk = (ODIE_GateAnnotation) chunkIterator
					.next();
			String chunkType = theChunk.getGateAnnotationType();
			if (chunkType.equals("TransientNP")) {
				result.add(theChunk);
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<ODIE_GateAnnotation> pullTransientNPsAndTokensFromJCas(
			JCas cas) {
		ArrayList<ODIE_GateAnnotation> result = new ArrayList<ODIE_GateAnnotation>();
		FSIndex chunkIndex = cas.getAnnotationIndex(ODIE_GateAnnotation.type);
		Iterator<ODIE_GateAnnotation> chunkIterator = chunkIndex.iterator();
		while (chunkIterator.hasNext()) {
			ODIE_GateAnnotation theChunk = (ODIE_GateAnnotation) chunkIterator
					.next();
			String chunkType = theChunk.getGateAnnotationType();
			if (chunkType.equals("TransientNP")
					|| chunkType.equals("DiscoveryDelimiter")
					|| chunkType.equals("TransientToken")) {
				result.add(theChunk);
			}
		}
		return result;
	}

	public static URI convertToNativeURI(URI uri) {
		URL url = null;
		try {
			url = uri.toURL();
			url = convertToNativeURL(url);
			uri = url.toURI();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return uri;
	}

	public static URL convertToNativeURL(URL url) {
		URL nativeURL = url;
		try {
			if (url.getProtocol().equals("bundleresource")) {
				Class<?> platformCls = Class
						.forName("org.eclipse.core.runtime.Platform");
				Class<?>[] resolveMethodSignature = { java.net.URL.class };
				Method resolveMethod = platformCls.getMethod("resolve",
						resolveMethodSignature);
				Object[] resolveMethodCallArguments = { url };
				nativeURL = (URL) resolveMethod.invoke(platformCls,
						resolveMethodCallArguments);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return nativeURL;
	}

	@SuppressWarnings("unchecked")
	public static List<ODIE_GateAnnotation> getUnderLyingAnnotations(
			ODIE_GateAnnotation annot) {
		List<ODIE_GateAnnotation> result = new ArrayList<ODIE_GateAnnotation>();
		try {
			JCas aJCas = annot.getCAS().getJCas();
			FSIndex candidateAnnotIndex = aJCas
					.getAnnotationIndex(ODIE_GateAnnotation.type);
			Iterator<ODIE_GateAnnotation> candidateAnnotIter = candidateAnnotIndex
					.iterator();
			while (candidateAnnotIter.hasNext()) {
				ODIE_GateAnnotation candidateAnnot = candidateAnnotIter.next();
				boolean isIncluded = true;
				isIncluded = isIncluded && (!candidateAnnot.equals(annot));
				isIncluded = isIncluded
						&& (candidateAnnot.getBegin() >= annot.getBegin());
				isIncluded = isIncluded
						&& (candidateAnnot.getEnd() <= annot.getEnd());
				if (isIncluded) {
					result.add(candidateAnnot);
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (CASException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static List<ODIE_GateAnnotation> getUnderLyingAnnotations(
			ODIE_GateAnnotation annot, HashSet<String> annotationTypeRange) {
		List<ODIE_GateAnnotation> result = new ArrayList<ODIE_GateAnnotation>();
		for (ODIE_GateAnnotation candidateAnnot : getUnderLyingAnnotations(annot)) {
			if (annotationTypeRange.contains(candidateAnnot
					.getGateAnnotationType())) {
				result.add(candidateAnnot);
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static List<ODIE_GateAnnotation> gateGet(JCas aJCas) {
		List<ODIE_GateAnnotation> result = new ArrayList<ODIE_GateAnnotation>();
		try {
			FSIndex uimaAnnotIndex = aJCas
					.getAnnotationIndex(ODIE_GateAnnotation.type);
			Iterator<ODIE_GateAnnotation> uimaAnnotIter = uimaAnnotIndex
					.iterator();
			while (uimaAnnotIter.hasNext()) {
				ODIE_GateAnnotation uimaAnnot = uimaAnnotIter.next();
				result.add(uimaAnnot);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static List<ODIE_GateAnnotation> gateGet(
			List<ODIE_GateAnnotation> inputAS, String gateAnnotationType) {
		List<ODIE_GateAnnotation> result = new ArrayList<ODIE_GateAnnotation>();
		for (ODIE_GateAnnotation candidateAnnot : inputAS) {
			if (candidateAnnot.getGateAnnotationType().equals(
					gateAnnotationType)) {
				result.add(candidateAnnot);
			}
		}
		return result;
	}

	public static List<ODIE_GateAnnotation> gateGet(
			List<ODIE_GateAnnotation> inputAS, Long sPos, Long ePos) {
		List<ODIE_GateAnnotation> result = new ArrayList<ODIE_GateAnnotation>();
		for (ODIE_GateAnnotation candidateAnnot : inputAS) {
			if (candidateAnnot.getBegin() >= sPos
					&& candidateAnnot.getEnd() <= ePos) {
				result.add(candidateAnnot);
			}
		}
		return result;
	}

	public static List<ODIE_GateAnnotation> gateGet(
			List<ODIE_GateAnnotation> inputAS, String gateAnnotationType,
			Long sPos, Long ePos) {
		List<ODIE_GateAnnotation> result = new ArrayList<ODIE_GateAnnotation>();
		for (ODIE_GateAnnotation candidateAnnot : inputAS) {
			if (candidateAnnot.getGateAnnotationType().equals(
					gateAnnotationType)
					&& candidateAnnot.getBegin() >= sPos
					&& candidateAnnot.getEnd() <= ePos) {
				result.add(candidateAnnot);
			}
		}
		return result;
	}

	public static List<ODIE_GateAnnotation> gateGet(
			List<ODIE_GateAnnotation> inputAS, HashSet<String> filterSet) {
		List<ODIE_GateAnnotation> result = new ArrayList<ODIE_GateAnnotation>();
		for (ODIE_GateAnnotation candidateAnnot : inputAS) {
			if (filterSet.contains(candidateAnnot.getGateAnnotationType())) {
				result.add(candidateAnnot);
			}
		}
		return result;
	}

	public static HashMap<String, String> fetchFeatureMapForAnnotation(
			ODIE_GateAnnotation annotationWithFeatures) {
		HashMap<String, String> result = new HashMap<String, String>();
		List<ODIE_GateFeature> uimaFeatures = fetchFeaturesForAnnotation(annotationWithFeatures);
		for (ODIE_GateFeature uimaFeature : uimaFeatures) {
			result.put(uimaFeature.getGateFeatureKey(), uimaFeature
					.getGateFeatureValue());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static List<ODIE_GateFeature> fetchFeaturesForAnnotation(
			ODIE_GateAnnotation uimaAnnot) {
		List<ODIE_GateFeature> result = new ArrayList<ODIE_GateFeature>();
		try {
			JCas aJCas = uimaAnnot.getCAS().getJCas();
			FSIndex uimaAnnotIndex = aJCas
					.getAnnotationIndex(ODIE_GateFeature.type);
			Iterator<ODIE_GateFeature> uimaAnnotIter = uimaAnnotIndex
					.iterator();
			while (uimaAnnotIter.hasNext()) {
				ODIE_GateFeature feature = uimaAnnotIter.next();
				if (feature.getGateAnnotationId() == uimaAnnot
						.getGateAnnotationId()) {
					result.add(feature);
				}
			}
		} catch (CASException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static boolean overlaps(Annotation annotationOne,
			Annotation annotationTwo) {
		boolean result = false;
		result = result
				|| (annotationTwo.getBegin() >= annotationOne.getBegin() && annotationTwo
						.getBegin() <= annotationOne.getEnd());
		result = result
				|| (annotationTwo.getEnd() >= annotationOne.getBegin() && annotationTwo
						.getEnd() <= annotationOne.getEnd());
		return result;
	}

}
