package edu.pitt.dbmi.odie.lexicalizer.lucenefinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import edu.pitt.text.tools.Stemmer;

public class ODIE_LexicalizerTerm {

	private static final Logger logger = Logger
			.getLogger(ODIE_LexicalizerTerm.class);

	private static final Stemmer normApi = new Stemmer();

	public String term;

	public final TreeSet<ODIE_LexicalizerWord> words = new TreeSet<ODIE_LexicalizerWord>(ODIE_LexicalizerWord.wordComparator);

	public ODIE_LexicalizerTerm(String term) {
		this.term = term;
		unCamelCaseTerm();
		deContaminate();
		String separator = "_" ;
		explodeTermToWordList(separator);
		filterStopWords() ;
		normalizeTermWords();
	}

	private void unCamelCaseTerm() {
		if (term.matches("^[A-Za-z]+$")) {
			if (ODIE_IndexFinderUtils.isCamelCase(term)) {
				term = unCamelCaseTerm(term);
			}
		}
	}

	/**
	 * unCamelCase
	 * 
	 * @param input
	
	 *         For an input like ProstaticAdenocarcinoma this method returns
	 * 
	 *         returns ['prostatic adenocarcinoma']
	 * 
	 */
	public static String unCamelCaseTerm(String input) {
		StringBuffer sb = new StringBuffer();
		char[] inputChars = input.toCharArray();
		for (int idx = 0; idx < inputChars.length; idx++) {
			char currentChar = inputChars[idx];
			if (Character.isUpperCase(currentChar)) {
				if (idx > 0) {
					sb.append(' ');
				}
				sb.append(Character.toLowerCase(currentChar));
			} else {
				sb.append(currentChar);
			}
		}
		return sb.toString();
	}

	/**
	 * deContaminate
	 *         Replaces all non alphabetic characters with underscore.
	 */
	private void deContaminate() {
		this.term = this.term.replaceAll("\\W+", "_");
	}

	private void filterStopWords() {
		final ArrayList<ODIE_LexicalizerWord> result = new ArrayList<ODIE_LexicalizerWord>() ;
		Iterator<ODIE_LexicalizerWord> iterator = this.words.iterator() ;
		for (;iterator.hasNext();) {
			String currentWord = iterator.next().getWord();
			if ( !ODIE_StopWords.getInstance().isStopWord(currentWord) ) {
				result.add(new ODIE_LexicalizerWord(currentWord)) ;
			}
		}
		this.words.clear();
		this.words.addAll(result) ;
	}

	private void explodeTermToWordList(
			String separator) {
		String regex = "[" + separator + "]+";
		String[] termWords = this.term.split(regex);
		this.words.clear();
		for (String word : termWords) {
			word = word.trim() ;
			if (word.length() > 0) {
				this.words.add(new ODIE_LexicalizerWord(word.toLowerCase())) ;
			}
		}
	}

	private void normalizeTermWords() {
		ArrayList<ODIE_LexicalizerWord> normalizedWords = new ArrayList<ODIE_LexicalizerWord>();
		for (ODIE_LexicalizerWord word : this.words) {
			String currentWord = word.getWord();
			normApi.add(currentWord);
			normApi.stem();
			String stemmedWord = normApi.getResultString();
//			logger.debug("Stemmed " + currentWord + " ==> " + stemmedWord);
			normalizedWords.add(new ODIE_LexicalizerWord(stemmedWord));
		}
		this.words.clear();
		this.words.addAll(normalizedWords) ;
	}
	
	public String getWordsAsCommaSeparatedValues() {
		StringBuffer commaSeparatedWords = new StringBuffer();
		for (Iterator<ODIE_LexicalizerWord> wordIterator = this.words
				.iterator(); wordIterator.hasNext();) {
			ODIE_LexicalizerWord word = (ODIE_LexicalizerWord) wordIterator.next();
			commaSeparatedWords.append(word.getWord());
			commaSeparatedWords.append(",");
		}
		commaSeparatedWords
				.deleteCharAt(commaSeparatedWords.length() - 1);
		return commaSeparatedWords.toString() ;
	}
	
	public boolean isViable() {
		return this.words.size() > 0 ;
	}
	
	public int getWordCount() {
		return this.words.size();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer() ;
		sb.append("term ==> " + this.term + "\n") ;
		for (ODIE_LexicalizerWord word : this.words) {
			sb.append("\tword ==> " + word.getWord() + "\n") ;
		}
		return sb.toString() ;
	}
	
	// This sorted set ensures us that the terms are not duplicates.
	// Duplication is defined by all the words be the same
	public static TreeSet<ODIE_LexicalizerTerm> getSortingTreeSet() {
		final TreeSet<ODIE_LexicalizerTerm> termSortingTreeSet = new TreeSet<ODIE_LexicalizerTerm>(
				new Comparator<ODIE_LexicalizerTerm>() {
					public int compare(ODIE_LexicalizerTerm o1,
							ODIE_LexicalizerTerm o2) {
						Iterator<ODIE_LexicalizerWord> wordIterator1 = o1.words
								.iterator();
						Iterator<ODIE_LexicalizerWord> wordIterator2 = o2.words
								.iterator();
						int result = 0;
						while (result == 0) {
							if (wordIterator1.hasNext()
									&& wordIterator2.hasNext()) {
								result = wordIterator1.next().getWord()
										.compareToIgnoreCase(
												wordIterator2.next()
														.getWord());
							} 
							else if (!wordIterator1.hasNext()
									&& !wordIterator2.hasNext()) {
								result = 0 ;
								break ;
							}else if (!wordIterator1.hasNext()) {
								result = -1;
							} else if (!wordIterator2.hasNext()) {
								result = 1;
							}
						}

						return result;
					}
				});
		return termSortingTreeSet ;
	}
	
	

}
