package edu.pitt.dbmi.odie.server.indexfinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Vector;

import org.apache.log4j.Logger;

import gov.nih.nlm.nls.lvg.Api.NormApi;

public class ODIE_MorphologicalNormalizer {

	private static ODIE_MorphologicalNormalizer singleton = null;
	private static NormApi api = null;
	Logger logger = Logger.getLogger(this.getClass());
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {

			Vector<String> v = api.Mutate("left");
			for (String mutation : v) {
//				logger.debug(mutation);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ODIE_MorphologicalNormalizer() {
		api = new NormApi();
	}

	public static ODIE_MorphologicalNormalizer getInstance() {
		if (singleton == null) {
			singleton = new ODIE_MorphologicalNormalizer();
		}
		return singleton;
	}

	public Vector<String> Mutate(String inputString) {
		Vector<String> result = null;
		synchronized (api) {
			try {
				result = api.Mutate(inputString);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public String mutatedWord(String word) {
		String result = word ;
		Vector<String> mutatedWordPayload = Mutate(word) ;
		if (mutatedWordPayload != null && mutatedWordPayload.size() > 0) {
			result = mutatedWordPayload.get(0) ;
		}
		return result ;
	}

	public String normalizeTerm(String term) {

		String result = "";

		synchronized (api) {
			String[] wordArray = term.split("\\s");
			HashSet<String> uniqueWords = new HashSet<String>();
			for (int wdx = 0; wdx < wordArray.length; wdx++) {
				uniqueWords.add(wordArray[wdx]);
			}
			HashSet<String> mutatedWords = new HashSet<String>();
			for (String word : uniqueWords) {
				String mutatedWord = mutatedWord(word) ;
				if (mutatedWord != null) {
					mutatedWords.add(mutatedWord) ;
				}
			}
			ArrayList<String> sortedTerms = new ArrayList<String>();
			sortedTerms.addAll(mutatedWords);
			Collections.sort(sortedTerms);
			for (String word : sortedTerms) {
				result += word + " ";
			}
		}

		return result.trim();
	}

}
