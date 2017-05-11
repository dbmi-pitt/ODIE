package edu.pitt.dbmi.odie.server.indexfinder;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import gov.nih.nlm.nls.lvg.Api.NormApi;

public class ODIE_IndexFinderInMemoryForProposals implements
		ODIE_IndexFinderInMemoryInf {

	private static final Logger logger = Logger
			.getLogger(ODIE_IndexFinderInMemoryForProposals.class);

	private NormApi normApi;

	public static HashMap<String, Long> cls2CuiHt = new HashMap<String, Long>();
	public static HashMap<Long, String> cui2ClsHt = new HashMap<Long, String>();
	public static HashMap<String, ODIE_Word> wordHt = new HashMap<String, ODIE_Word>();
	public static HashMap<String, ODIE_Phrase> phraseHt = new HashMap<String, ODIE_Phrase>();

	public static TreeSet<ODIE_Phrase> sortedPhraseSet = new TreeSet<ODIE_Phrase>(
			ODIE_Phrase.phraseComparator);

	public static TreeSet<ODIE_Word> sortedWordSet = new TreeSet<ODIE_Word>(
			ODIE_Word.wordComparator);

	public static long cuiAutoIncrement = 1L; // Will increment by one

	public static long suiAutoIncrement = 1L; // Will increment by one

	public static String cuiDigitPattern = "0000000000";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ODIE_IndexFinderInMemoryForProposals indexFinder = new ODIE_IndexFinderInMemoryForProposals();
	
		indexFinder.setNormApi(new NormApi());
		String proposalsPrefix = "http://caties.cabig.upmc.edu/ODIE/ontologies/2008/1/1/proposals.owl#" ;
		//indexFinder.addClass(proposalsPrefix + "Igg1") ;
		indexFinder.addClass(proposalsPrefix + "Igg2") ;
		indexFinder.addClass(proposalsPrefix + "Igg3") ;
		indexFinder.addClass(proposalsPrefix + "Lymphocyte") ;
		
		indexFinder.addClass("http://my.owl#Apples");
		indexFinder.addClass("http://your.owl#Apples");
		indexFinder.addClass("http://your.owl#DeliciousApples");
		indexFinder.addClass("http://your.owl#DeliciousPeaches");
		indexFinder.addSynonym("http://your.owl#DeliciousPeaches",
				"Tasty Peaches");
		indexFinder.addSynonym("http://your.owl#DeliciousPeaches",
				"delicious peaches");

		logger.debug("\n\nFirst pass\n\n");
		indexFinder.displayTables();

		indexFinder.removeSynonym("http://your.owl#DeliciousPeaches",
				"Tasty Peaches");

		logger.debug("\n\nSecond pass\n\n");
		indexFinder.displayTables();

		indexFinder.removeClass("http://your.owl#DeliciousPeaches");

		logger.debug("\n\nThird pass\n\n");
		indexFinder.displayTables();
	}

	public ODIE_IndexFinderInMemoryForProposals() {
	}

	public String fetchConceptUriForCui(Integer cui) {
		return cui2ClsHt.get(new Long(cui.longValue()));
	}

	public String getCnForCui(int cui) {
		String result = null;
		result = cui2ClsHt.get(new Long(cui)) ;
		return result;
	}
	
//	String formattedCui = ODIE_IndexFinderUtils.formatLongAsDigitString(
//			(long) cui, cuiDigitPattern);
//	for (Iterator<String> phraseKeyIterator = phraseHt.keySet().iterator(); phraseKeyIterator
//			.hasNext();) {
//		String phraseKey = phraseKeyIterator.next() ;
//		ODIE_Phrase phrase = phraseHt.get(phraseKey) ;
//		String phraseCui = phrase.cui;
//		if (phraseCui.equals(formattedCui)) {
//			
//			result = "";
//			for (ODIE_Word nextWord : phrase.words) {
//				result += nextWord.word;
//				result += " ";
//			}
//			result = result.trim();
//			break;
//		}
//	}

	public ODIE_Word getWordForKey(String wordKey) {
		if (wordKey.startsWith("igg")) {
			displayTables() ;
		}
		ODIE_Word result = wordHt.get(wordKey);
		return result ;
	}

	public void addClass(String classQName) {
		addSynonym(classQName, generateTermFromClassQName(classQName));

	}

	private String generateTermFromClassQName(String classQName) {
		String result = null;
		result = classQName;
		result = ODIE_IndexFinderUtils.getSimpleClsNameFromQName(result);
		result = ODIE_IndexFinderUtils.deContaminate(result);
		result = ODIE_IndexFinderUtils.unCamelCase(result, false).get(0);
		result = result.replaceAll("[_]+", " ");
		result = result.toLowerCase() ;
		return result;
	}

	private String normalizeSynonymousPhrase(String synonymousTerm) {
		boolean isAddingWords = true;
		ArrayList<String> currentWords = new ArrayList<String>();
		currentWords = ODIE_IndexFinderUtils.unCamelCase(synonymousTerm,
				isAddingWords);
		currentWords = ODIE_IndexFinderUtils.normalizePhraseWords(currentWords,
				this.normApi);
		currentWords = ODIE_IndexFinderUtils.sortPhraseWords(currentWords);
		String result = ODIE_IndexFinderUtils.implodeWordListToTerm(
				currentWords, " ");
		return result;
	}

	public void addSynonym(String classQName, String synonymousTerm) {
		Long cui = cls2CuiHt.get(classQName);
		if (cui == null) {
			cui = new Long(cuiAutoIncrement++);
			cui2ClsHt.put(cui, classQName);
			cls2CuiHt.put(classQName, cui);
		}
		String cuiAsString = ODIE_IndexFinderUtils.formatLongAsDigitString(cui,
				cuiDigitPattern);
		ArrayList<String> currentWords = new ArrayList<String>();
		currentWords = ODIE_IndexFinderUtils.explodeTermToWordList(
				synonymousTerm, "\\s");
		currentWords = ODIE_IndexFinderUtils.normalizePhraseWords(currentWords,
				this.normApi);
		currentWords = ODIE_IndexFinderUtils.sortPhraseWords(currentWords);
		String currentSearchPhrase = ODIE_IndexFinderUtils
				.implodeWordListToTerm(currentWords, " ");
		ODIE_Phrase odiePhrase = null;
		for (ODIE_Phrase existingPhrase : sortedPhraseSet) {
			if (existingPhrase.getTermFromWords().equals(currentSearchPhrase)
					&& existingPhrase.cui.equals(cuiAsString)) {
				odiePhrase = existingPhrase;
				break;
			}
		}
		if (odiePhrase == null) {
			odiePhrase = new ODIE_Phrase();
			odiePhrase.id = new Integer(cui.intValue());
			odiePhrase.cui = cuiAsString;
			odiePhrase.sui = ODIE_IndexFinderUtils.formatLongAsDigitString(
					new Long(suiAutoIncrement++), cuiDigitPattern);
			for (String word : currentWords) {
				ODIE_Word odieWord = fetchOrCreateWord(word);
				if (!odieWord.phraseList.contains(odiePhrase)) {
					odieWord.phraseList.add(odiePhrase);
				}
				odiePhrase.sortedWords.add(odieWord);
				odiePhrase.wordCount++;
			}
			phraseHt.put(odiePhrase.sui, odiePhrase);
			sortedPhraseSet.add(odiePhrase);
		}
	}

	private ODIE_Word fetchOrCreateWord(String wordKey) {
		ODIE_Word odieWord = wordHt.get(wordKey);
		if (odieWord == null) {
			odieWord = new ODIE_Word(wordKey);
			wordHt.put(wordKey, odieWord);
			sortedWordSet.add(odieWord);
		}
		return odieWord;
	}

	public void removeClass(String classQName) {
		removeSynonym(classQName, null);
	}

	public void removeSynonym(String classQName, String synonymousTerm) {
		Long cuiToRemove = cls2CuiHt.get(classQName);
		if (cuiToRemove != null) {
			String cuiAsString = ODIE_IndexFinderUtils.formatLongAsDigitString(
					cuiToRemove, cuiDigitPattern);
			ArrayList<ODIE_Phrase> phrasesToRemove = new ArrayList<ODIE_Phrase>();
			ArrayList<ODIE_Phrase> phrasesToPreserve = new ArrayList<ODIE_Phrase>();
			for (ODIE_Phrase phrase : sortedPhraseSet) {
				if (phrase.cui.equals(cuiAsString)) {
					String removalTerm = phrase.getTermFromWords();
					if (synonymousTerm != null) {
						removalTerm = normalizeSynonymousPhrase(synonymousTerm);
					}
					if (phrase.getTermFromWords().equals(removalTerm)) {
						phrasesToRemove.add(phrase);
					} else {
						phrasesToPreserve.add(phrase);
					}
				}
			}
			ArrayList<ODIE_Word> wordsToRemove = new ArrayList<ODIE_Word>();
			for (ODIE_Phrase removalPhrase : phrasesToRemove) {
				for (ODIE_Word word : removalPhrase.sortedWords) {
					word.phraseList.remove(removalPhrase);
					if (word.phraseList.isEmpty()) {
						wordsToRemove.add(word);
					}
				}
				sortedPhraseSet.remove(removalPhrase);
				phraseHt.remove(removalPhrase.sui);
			}
			for (ODIE_Word word : wordsToRemove) {
				sortedWordSet.remove(word);
				wordHt.remove(word.word);
			}
			if (phrasesToPreserve.isEmpty()) {
				cls2CuiHt.remove(classQName);
				cui2ClsHt.remove(cuiToRemove);
			}
		}
	}

	public void clear() {
		cls2CuiHt.clear();
		cui2ClsHt.clear();
		wordHt.clear();
		phraseHt.clear();
		sortedPhraseSet.clear();
		sortedWordSet.clear();
		cuiAutoIncrement = 1L;
	}

	private void displayTables() {
		for (Iterator<Long> cuiIterator = cui2ClsHt.keySet().iterator(); cuiIterator
				.hasNext();) {
			Long cui = cuiIterator.next();
			String cls = cui2ClsHt.get(cui);
			logger.debug(cls
					+ " ["
					+ ODIE_IndexFinderUtils.formatLongAsDigitString(cui,
							cuiDigitPattern) + "]");
			String cuiAsString = ODIE_IndexFinderUtils.formatLongAsDigitString(
					cui, cuiDigitPattern);
			for (ODIE_Phrase phrase : sortedPhraseSet) {
				if (phrase.cui.equals(cuiAsString)) {
					logger.debug("\t" + phrase.toString() + "\n");
				}
			}
		}
	}

	public NormApi getNormApi() {
		return normApi;
	}

	public void setNormApi(NormApi normApi) {
		this.normApi = normApi;
	}

	public void loadInMemoryTables() {

	}
}
