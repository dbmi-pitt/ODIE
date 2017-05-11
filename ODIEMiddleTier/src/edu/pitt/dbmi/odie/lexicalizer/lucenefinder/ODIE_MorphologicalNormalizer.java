package edu.pitt.dbmi.odie.lexicalizer.lucenefinder;

import java.util.HashSet;

import edu.pitt.text.tools.Stemmer;

public class ODIE_MorphologicalNormalizer {

	private static ODIE_MorphologicalNormalizer singleton = null;
	private static Stemmer api = null;

	private ODIE_MorphologicalNormalizer() {
		api = new Stemmer();
	}

	public static ODIE_MorphologicalNormalizer getInstance() {
		if (singleton == null) {
			singleton = new ODIE_MorphologicalNormalizer();
		}
		return singleton;
	}

	public String normalizeTerm(String term) {

		String result = "";

		synchronized (api) {
			
			String[] wordArray = term.split("\\s");
			HashSet<String> uniqueWords = new HashSet<String>();
			for (int wdx = 0; wdx < wordArray.length; wdx++) {
				uniqueWords.add(wordArray[wdx]);
			}
			for (String word : uniqueWords) {
				api.add(word) ;
			}
			api.stem();
			result = api.getResultString() ;
		}

		return result.trim();
	}

}
