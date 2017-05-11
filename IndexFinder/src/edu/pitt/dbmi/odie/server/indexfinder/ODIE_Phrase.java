package edu.pitt.dbmi.odie.server.indexfinder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.log4j.Logger;

public class ODIE_Phrase extends ODIE_IndexedEntity {

	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ODIE_Phrase.class);
	
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
			if (result == 0) {
				result = p1.cui.compareTo(p2.cui) ;
			}
			return result;
		}
	};

	public TreeSet<ODIE_Word> sortedWords = new TreeSet<ODIE_Word>(
			ODIE_Word.wordComparator);
	public TreeSet<ODIE_IndexFinderAnnotation> annotations = new TreeSet<ODIE_IndexFinderAnnotation>(
			new ODIE_IndexFinderAnnotationComparator());
	public ArrayList<ODIE_Word> temporaryWordAccumulator = null;
	public String sui = "";
	public String cui = "";
	public int wordCount = 0;
	public int wordHits = 0;

	public ODIE_Phrase() {
		;
	}
	
	public ODIE_Phrase(TreeSet<ODIE_Word> odieWords) {
		for (Iterator<ODIE_Word> iterator = odieWords.iterator(); iterator
				.hasNext();) {
			sortedWords.add(iterator.next());
		}
	}

	public ODIE_Phrase(ODIE_Phrase copyCandidatePhrase) {
		for (Iterator<ODIE_Word> wordIterator = copyCandidatePhrase.sortedWords
				.iterator(); wordIterator.hasNext();) {
			sortedWords.add(wordIterator.next());
		}
		if (copyCandidatePhrase.temporaryWordAccumulator != null) {
			temporaryWordAccumulator = new ArrayList<ODIE_Word>();
			for (Iterator<ODIE_Word> wordIterator = copyCandidatePhrase.temporaryWordAccumulator
					.iterator(); wordIterator.hasNext();) {
				temporaryWordAccumulator.add(wordIterator.next());
			}
		}
		for (Iterator<ODIE_IndexFinderAnnotation> annotationsIterator = copyCandidatePhrase.annotations
				.iterator(); annotationsIterator.hasNext();) {
			annotations.add(annotationsIterator.next());
		}
		sui = copyCandidatePhrase.sui;
		cui = copyCandidatePhrase.cui;
		wordCount = copyCandidatePhrase.wordCount;
		wordHits = copyCandidatePhrase.wordHits;
	}

	public void reset() {
		this.temporaryWordAccumulator = null;
		annotations.clear();
		wordHits = 0;
	}

	public void incrementHits(ODIE_Word word, ODIE_IndexFinderAnnotation tokenAnnotation) {
		if (this.temporaryWordAccumulator == null) {
			this.temporaryWordAccumulator = new ArrayList<ODIE_Word>();
		}
		int idx = this.temporaryWordAccumulator.indexOf(word) ;
		if (idx < 0) {
			logger.debug("Hitting " + cui + "." + sui + " with "
					+ word.word);
			this.temporaryWordAccumulator.add(word);
			this.annotations.add(tokenAnnotation);
			this.wordHits++;
		}
		else {
			// Replace the annotation representing the word with this
			// annotation thus moving the match words toward the end
			// of the stream.  This will compact the hit.
			TreeSet<ODIE_IndexFinderAnnotation> newAnnotations = new TreeSet<ODIE_IndexFinderAnnotation>(
					new ODIE_IndexFinderAnnotationComparator());
			ArrayList<ODIE_Word> newWordAccumulator = new ArrayList<ODIE_Word>() ;
			int jdx = 0 ;
			for (ODIE_IndexFinderAnnotation annot : this.annotations) {
				if (idx != jdx) {
					newWordAccumulator.add(this.temporaryWordAccumulator.get(jdx)) ;
					newAnnotations.add(annot) ;
				}
				jdx++ ;
			}
			newWordAccumulator.add(word) ;
			newAnnotations.add(tokenAnnotation) ;
			this.temporaryWordAccumulator = newWordAccumulator ;
			this.annotations = newAnnotations ;
		}

	}

	public boolean equals(Object obj) {
		boolean result = false;
		if (obj instanceof ODIE_Phrase) {
			ODIE_Phrase comparePhrase = (ODIE_Phrase) obj;
			result = this.sui.equals(comparePhrase.sui);
			if (result && this.sortedWords != null && !this.sortedWords.isEmpty()
					&& comparePhrase.sortedWords != null
					&& !comparePhrase.sortedWords.isEmpty()) {
				result = this.getTermFromWords().equals(
						comparePhrase.getTermFromWords());
			}
		}
		return result;
	}

	public int hashCode() {
		return this.sui.hashCode();
	}

	public String toString() {
		String result = "";
		result += (this.cui != null && this.cui.length() > 0) ? this.cui : "";
		result += (this.sui != null && this.sui.length() > 0) ? "." + this.sui
				: "";
		result += (this.id > -1) ? "." + this.id : "";
		result += ") ==> " + this.wordCount + " [ ";
		if (sortedWords != null && sortedWords.size() > 0) {
			for (Iterator<ODIE_Word> iterator = sortedWords.iterator(); iterator
					.hasNext();) {
				ODIE_Word word = (ODIE_Word) iterator.next();
				result += word.word + " ";
			}
			result += " ] " ;
//			for (Iterator<ODIE_Word> iterator = words.iterator(); iterator
//					.hasNext();) {
//				ODIE_Word word = (ODIE_Word) iterator.next();
//				result += "\t" + word + "\n";
//			}
		}
		return result;
	}

	public String getTerm() {
		String result = "";
		for (Iterator<ODIE_Word> iterator = this.temporaryWordAccumulator
				.iterator(); iterator.hasNext();) {
			ODIE_Word word = (ODIE_Word) iterator.next();
			result += word.word + " ";
		}
		if (result.endsWith(" ")) {
			result = result.substring(0, result.length() - " ".length());
		}
		return result;
	}

	public String getTermFromWords() {
		String result = "";
		if (this.sortedWords != null && !this.sortedWords.isEmpty()) {
			for (Iterator<ODIE_Word> iterator = this.sortedWords.iterator(); iterator
					.hasNext();) {
				ODIE_Word word = (ODIE_Word) iterator.next();
				result += word.word + " ";
			}
			if (result.endsWith(" ")) {
				result = result.substring(0, result.length() - " ".length());
			}
		}

		return result;
	}

	public ArrayList<ODIE_IndexFinderAnnotation> getAnnotationList() {
		ArrayList<ODIE_IndexFinderAnnotation> result = new ArrayList<ODIE_IndexFinderAnnotation>();
		Iterator<ODIE_IndexFinderAnnotation> iterator = annotations.iterator();
		while (iterator.hasNext()) {
			result.add(iterator.next());
		}
		return result;
	}

}
