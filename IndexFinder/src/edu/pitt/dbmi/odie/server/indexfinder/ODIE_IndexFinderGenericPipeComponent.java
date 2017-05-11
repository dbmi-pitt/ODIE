/*
 *  ODIE_IndexFinderGenericPipeComponent.java
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

package edu.pitt.dbmi.odie.server.indexfinder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * This class is the implementation of the resource INDEXFINDERPR.
 */
public class ODIE_IndexFinderGenericPipeComponent {

	private static final long serialVersionUID = -5617890857784398153L;

	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ODIE_IndexFinderGenericPipeComponent.class);

	private ArrayList<String> includedOntologyUris;

	private boolean isContiguous = false;

	private Boolean isOverlapping = false;

	private ODIE_IndexFinderInMemoryInf inMemoryIndexFinder;

	private ArrayList<ODIE_IndexFinderAnnotation> sortedTokens = null;

	public int totalPhraseCount = 0;
	public int totalWordCount = 0;
	public int totalBaseCount = 0;

	public static Comparator<ODIE_Phrase> phraseComparator = new Comparator<ODIE_Phrase>() {
		public int compare(ODIE_Phrase p1, ODIE_Phrase p2) {
			int result = p1.sortedWords.size() - p2.sortedWords.size();
			if (result == 0) {
				Iterator<ODIE_Word> p1Iter = p1.sortedWords.iterator();
				Iterator<ODIE_Word> p2Iter = p2.sortedWords.iterator();
				for (; p1Iter.hasNext();) {
					ODIE_Word word1 = p1Iter.next();
					ODIE_Word word2 = p2Iter.next();
					result = word1.word.compareTo(word2.word);
					if (result != 0) {
						break;
					}
				}
			}
			return result;
		}
	};

	public static Comparator<ODIE_Phrase> phraseCuiComparator = new Comparator<ODIE_Phrase>() {
		public int compare(ODIE_Phrase p1, ODIE_Phrase p2) {
			int result = 0;
			if (p1.cui != null && p1.cui.length() > 0 && p2.cui != null
					&& p2.cui.length() > 0) {
				result = p1.cui.compareTo(p2.cui);
			}
			return result;
		}
	};

	public static Comparator<ODIE_Phrase> phraseSizeCuiComparator = new Comparator<ODIE_Phrase>() {
		public int compare(ODIE_Phrase p1, ODIE_Phrase p2) {
			int result = p1.wordCount - p2.wordCount;
			if (result == 0) {
				if (p1.cui != null && p1.cui.length() > 0 && p2.cui != null
						&& p2.cui.length() > 0) {
					result = p1.cui.compareTo(p2.cui);
				}
			}
			return result;
		}
	};

	public static Comparator<ODIE_Phrase> phraseHitsAnnotationStartComparator = new Comparator<ODIE_Phrase>() {
		public int compare(ODIE_Phrase p1, ODIE_Phrase p2) {
			ODIE_IndexFinderAnnotation annot1 = p1.annotations.first();
			ODIE_IndexFinderAnnotation annot2 = p2.annotations.first();
			int result = annot1.getStartNode().getOffset().intValue()
					- annot2.getStartNode().getOffset().intValue();
			if (result == 0) {
				result = p2.wordHits - p1.wordHits;
				if (result == 0) {
					result = p1.sui.compareTo(p2.sui);
				}
			}
			return result;
		}
	};

	//
	// Order by the number or wordHits then by the position in the the sentence
	//
	public static Comparator<ODIE_Phrase> phraseHitsAnnotationFrequencyComparator = new Comparator<ODIE_Phrase>() {
		public int compare(ODIE_Phrase p1, ODIE_Phrase p2) {
			int result = p2.wordHits - p1.wordHits;
			if (result == 0) {
				ODIE_IndexFinderAnnotation annot1 = p1.annotations.first();
				ODIE_IndexFinderAnnotation annot2 = p2.annotations.first();
				result = annot1.getStartNode().getOffset().intValue()
						- annot2.getStartNode().getOffset().intValue();
			}
			return result;
		}
	};

	public static Comparator<ODIE_Word> wordFrequencyThenIndexComparator = new Comparator<ODIE_Word>() {
		public int compare(ODIE_Word w1, ODIE_Word w2) {
			int result = w2.frequency.intValue() - w1.frequency.intValue();
			if (result == 0) {
				result = w1.id - w2.id;
			}
			return result;
		}
	};

	public static Comparator<ODIE_IndexedEntity> indexedEntityComparator = new Comparator<ODIE_IndexedEntity>() {
		public int compare(ODIE_IndexedEntity e1, ODIE_IndexedEntity e2) {
			return e1.id.intValue() - e2.id.intValue();
		}
	};

	public TreeSet<ODIE_Phrase> sortedPhraseSet = new TreeSet<ODIE_Phrase>(
			phraseComparator);

	public TreeSet<ODIE_Word> sortedWordSet = new TreeSet<ODIE_Word>(
			ODIE_Word.wordComparator);

	public HashMap<String, ODIE_Phrase> phraseHt = new HashMap<String, ODIE_Phrase>();
	public HashMap<String, ODIE_Phrase> candidates = new HashMap<String, ODIE_Phrase>();
	private ArrayList<ODIE_Phrase> candidateArray = new ArrayList<ODIE_Phrase>() ;
	public TreeSet<ODIE_Phrase> sortedHitSet = new TreeSet<ODIE_Phrase>(
	// phraseHitsAnnotationFrequencyComparator) ;
			phraseHitsAnnotationStartComparator);

	private ArrayList<ODIE_IndexFinderAnnotation> resultingConcepts = new ArrayList<ODIE_IndexFinderAnnotation>();
	private ArrayList<ODIE_Phrase> resultingPhrases = new ArrayList<ODIE_Phrase>();

	public HashSet<String> documentWordSet = new HashSet<String>();

	public HashSet<String> documentSuiSet = new HashSet<String>();

	public static void main(String[] args) {
		ODIE_IndexFinderGenericPipeComponent ifPR = new ODIE_IndexFinderGenericPipeComponent();
		ifPR.execute();
	}

	public ODIE_IndexFinderGenericPipeComponent() {
		// this.normApi = new NormApi();
	}

	public void execute() {
		clearAllCaches();
		processTokenSequence();
	}

	private void clearAllCaches() {

		clearPhraseListsForNewSentence();

		documentWordSet.clear();
		documentSuiSet.clear();
		sortedPhraseSet.clear();
		sortedWordSet.clear();
		phraseHt.clear();
		candidates.clear();
		candidateArray.clear() ;
		sortedHitSet.clear();

		resultingConcepts.clear();
		resultingPhrases.clear();
	}

	private void clearPhraseListsForNewSentence() {
		//
		// Reset for the next sentence
		//
		for (Iterator<ODIE_Phrase> phraseIterator = candidates.values()
				.iterator(); phraseIterator.hasNext();) {
			ODIE_Phrase phrase = phraseIterator.next();
			phrase.wordHits = 0; // clear up for next sentence
			phrase.annotations.clear();
		}
	}

	/**
	 * Method execute.
	 */
	public void processTokenSequence() {
		for (Iterator<ODIE_IndexFinderAnnotation> tokenAnnotationsIterator = this.sortedTokens
				.iterator(); tokenAnnotationsIterator.hasNext();) {
			ODIE_IndexFinderAnnotation token = tokenAnnotationsIterator.next();
			String tokenKind = (String) token.getFeatures().get("kind");
			String tokenString = (String) token.getFeatures().get("string");
			String tokenNormalizedForm = (String) token.getFeatures().get(
					"normalizedForm");
			String tokenCanonicalForm = (String) token.getFeatures().get(
					"canonicalForm");
			//
			// Index on the first match in the progression of
			// 1) lowercased token string
			// 2) normalized lowercased token string
			// 3) canonical form of lowercased token string
			//
			boolean isWordToken = true;
			boolean isWordIndexable = false;
			isWordToken = isWordToken && tokenKind != null;
			isWordToken = isWordToken && tokenKind.equals("word");
			String indexableString = null ;
			if (isWordToken) {
				indexableString = (isWordIndexable(tokenString)) ? tokenString : null ;
				if (indexableString == null) {
					indexableString = (isWordIndexable(tokenNormalizedForm)) ? tokenNormalizedForm : null ;
				}
				if (indexableString == null) {
					indexableString = (isWordIndexable(tokenCanonicalForm)) ? tokenCanonicalForm : null ;
				}
				isWordIndexable = indexableString != null ;
				if (isWordIndexable) {
					indexSentenceWord(indexableString, token);
				}
				else if (isContiguous()) {
					this.candidateArray.clear();
				}
			}
		}
		annotateHitPhrases();

	}
	
	private boolean isWordIndexable(String tokenString) {
		boolean isPositiveLengthString = tokenString != null;
		isPositiveLengthString = isPositiveLengthString
				&& tokenString.length() > 0;
		boolean isWordIndexable = isPositiveLengthString
				&& isWordInDictionary(tokenString);
		return isWordIndexable ;
	}

	private boolean isWordInDictionary(String wordKey) {
		boolean result = false;
		ODIE_Word word = this.inMemoryIndexFinder.getWordForKey(wordKey);
		if (word != null) {
			result = true;
		}
		return result;
	}

	/*
	 * Key algorithmic component of IndexFinder method. Word is used to pull all
	 * phrases that contain Word. These phrase are kept in memory and
	 * incremented with subsequent words. Once the tally exceeds the phrase
	 * length we have a hit and this phrase is moved to the candidates list.
	 */
	private void indexSentenceWord(String wordKey,
			ODIE_IndexFinderAnnotation tokenAnnotation) {
		if (!isContiguous()) {
			indexSentenceWordNonContiguously(wordKey, tokenAnnotation);
		} else {
			indexSentenceWordContiguously(wordKey, tokenAnnotation);
		}
	}
	
	private void indexSentenceWordNonContiguously(String wordKey,
			ODIE_IndexFinderAnnotation tokenAnnotation) {
		ODIE_Word word = this.inMemoryIndexFinder.getWordForKey(wordKey);
		//
		// Add each word phrase to the end of the candidateArray
		//
		for (ODIE_Phrase phrase : word.phraseList) {
			ODIE_Phrase newPhrase = new ODIE_Phrase(phrase) ;
			newPhrase.reset();
			candidateArray.add(newPhrase) ;
		}
		//
		// Increment each phrase in the candidates array based on this word
		// An increment happens if one of the phrases of this word matches the 
		// StringUniqueIdentifier (sui) of existingPhrase and the word contributes
		// positively.
		//
		ArrayList<ODIE_Phrase> continueProcessingArray = new ArrayList<ODIE_Phrase>() ;
		for (ODIE_Phrase existingPhrase : candidateArray) {
			for (ODIE_Phrase phrase : word.phraseList) {
				ODIE_Phrase newPhrase = new ODIE_Phrase(phrase) ;
				newPhrase.reset();
				if (existingPhrase.sui.equals(newPhrase.sui)) {
					existingPhrase.incrementHits(word, tokenAnnotation);
					break ;
				}
			}
			int hitsRequired = existingPhrase.wordCount;
			int hitsObtained = existingPhrase.wordHits;
			if (hitsObtained >= hitsRequired) {
				logger.debug("Hit on term ==> " + existingPhrase.getTerm());
				sortedHitSet.add(new ODIE_Phrase(existingPhrase));
			}
			else {
				continueProcessingArray.add(existingPhrase) ;
			}
		}
		candidateArray = continueProcessingArray ;
	}

	/*
	 * Use this method instead of indexSenteceWord when the word stream must
	 * contiguously increment the candidate phrases.
	 */

	private void indexSentenceWordContiguously(String wordKey,
			ODIE_IndexFinderAnnotation tokenAnnotation) {
		ODIE_Word word = this.inMemoryIndexFinder.getWordForKey(wordKey);
		//
		// Add each word phrase to the end of the candidateArray
		//
		for (ODIE_Phrase phrase : word.phraseList) {
			ODIE_Phrase newPhrase = new ODIE_Phrase(phrase) ;
			newPhrase.reset();
			this.candidateArray.add(newPhrase) ;
		}
		//
		// Increment each phrase in the candidates array based on this word
		// An increment happens if one of the phrases of this word matches the 
		// StringUniqueIdentifier (sui) of existingPhrase and the word contributes
		// positively.
		//
		ArrayList<ODIE_Phrase> continueProcessingArray = new ArrayList<ODIE_Phrase>() ;
		for (ODIE_Phrase existingPhrase : this.candidateArray) {
			int hitsPreviouslyObtained = existingPhrase.wordHits;
			for (ODIE_Phrase phrase : word.phraseList) {
				ODIE_Phrase newPhrase = new ODIE_Phrase(phrase) ;
				newPhrase.reset();
				if (existingPhrase.sui.equals(newPhrase.sui)) {
					existingPhrase.incrementHits(word, tokenAnnotation);
					break ;
				}
			}
			int hitsRequired = existingPhrase.wordCount;
			int hitsObtained = existingPhrase.wordHits;
			if (hitsObtained >= hitsRequired) {
				logger.debug("Hit on term ==> " + existingPhrase.getTerm());
				sortedHitSet.add(new ODIE_Phrase(existingPhrase));
			}
			else if (hitsPreviouslyObtained < hitsObtained) {
				continueProcessingArray.add(existingPhrase) ;
			}
		}
		candidateArray = continueProcessingArray ;
	}

	private void annotateHitPhrases() {
		//
		// Annotate left to right
		//
		int caret = -1;
		for (Iterator<ODIE_Phrase> phraseIterator = sortedHitSet
				.iterator(); phraseIterator.hasNext();) {
			ODIE_Phrase phrase = phraseIterator.next();
			ODIE_IndexFinderAnnotation sAnnotation = phrase.annotations.first();
			ODIE_IndexFinderAnnotation eAnnotation = phrase.annotations.last();
		
			if (!this.isOverlapping
					&& sAnnotation.getStartNode().getOffset().intValue() <= caret) {
				continue;
			}

			String cn = deriveCnForPhrase(phrase);

			if (excludeClassBasedOnFilter(cn)) {
				continue;
			}

			HashMap<String, Object> featureMap = new HashMap<String, Object>();
			featureMap.put("cui", phrase.cui);
			featureMap.put("sui", phrase.sui);
			featureMap.put("str", phrase.getTerm());
			featureMap.put("cn", cn);
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

			this.resultingConcepts.add(conceptAnnotation);
			this.resultingPhrases.add(phrase);

			if (!this.isOverlapping) {
				caret = eAnnotation.getEndNode().getOffset().intValue();
			}

		}

	}

	public boolean excludeClassBasedOnFilter(String cn) {
		// displayIncludedOntologyUris() ;
		boolean result = false;
		if (cn == null || cn.indexOf("#") == -1) {
			logger.error("excludeClassBasedOnFilter: Bad cn ==> " + cn);
		} else {
			String ontologyUri = cn.substring(0, cn.lastIndexOf("#"));
			result = !this.includedOntologyUris.contains(ontologyUri);
		}

		return result;
	}

	@SuppressWarnings("unused")
	private void displayIncludedOntologyUris() {
		for (String uri : this.includedOntologyUris) {
			logger.debug(uri);
		}
	}

	/** should clear all internal data of the resource. Does nothing now */
	public void cleanup() {

	}
	
	private String deriveCnForPhrase(ODIE_Phrase phrase) {
		String cui = phrase.cui;
		String result = phrase.cui;
		if (this.inMemoryIndexFinder != null) {
			int cuiAsInteger = (new Integer(cui)).intValue();
			result = this.inMemoryIndexFinder.getCnForCui(cuiAsInteger);
		}
		return result;
	}

	@SuppressWarnings("unused")
	private void printWords(ArrayList<String> wordArray) {
		StringBuffer sb = new StringBuffer();
		for (Iterator<String> wordIterator = wordArray.iterator(); wordIterator
				.hasNext();) {
			sb.append(wordIterator.next() + " ");
		}
		sb.append("\n");
		logger.debug(sb.toString());
	}

	@SuppressWarnings("unused")
	private void displayPhraseTableDiagnostics() {
		for (Iterator<ODIE_Phrase> phraseIterator = sortedPhraseSet.iterator(); phraseIterator
				.hasNext();) {
			logger.debug(phraseIterator.next());
		}
	}

	@SuppressWarnings("unused")
	private ArrayList<String> tokenize(String input) {
		Pattern tokeniserPattern = Pattern.compile("(\\w+)", Pattern.DOTALL);
		Matcher matcher = tokeniserPattern.matcher(input);
		ArrayList<String> result = new ArrayList<String>();
		while (matcher.find()) {
			String word = matcher.group();
			result.add(word.toLowerCase());
		}

		return result;
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

	public ArrayList<ODIE_IndexFinderAnnotation> getSortedTokens() {
		return sortedTokens;
	}

	public void setSortedTokens(
			ArrayList<ODIE_IndexFinderAnnotation> sortedTokens) {
		this.sortedTokens = sortedTokens;
	}

	public ArrayList<ODIE_IndexFinderAnnotation> getResultingConcepts() {
		return resultingConcepts;
	}

	public void setResultingConcepts(
			ArrayList<ODIE_IndexFinderAnnotation> resultingConcepts) {
		this.resultingConcepts = resultingConcepts;
	}

	public ArrayList<ODIE_Phrase> getResultingPhrases() {
		return resultingPhrases;
	}

	public void setResultingPhrases(ArrayList<ODIE_Phrase> resultingPhrases) {
		this.resultingPhrases = resultingPhrases;
	}

	public boolean isContiguous() {
		return isContiguous;
	}

	public void setContiguous(boolean isContiguous) {
		this.isContiguous = isContiguous;
	}

	public void setOverlapping(Boolean isOverlapping) {
		this.isOverlapping = isOverlapping;
	}

} // class ODIE_IndexFinderGenericPipeComponent
