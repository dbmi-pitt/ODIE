package edu.pitt.dbmi.odie.lexicalizer.lucenefinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ODIE_IndexFinderBestMneumonicNameDeterminer {
	
	public static ODIE_IndexFinderBestMneumonicNameDeterminer singleton = null ;
	
	public static ODIE_IndexFinderBestMneumonicNameDeterminer getInstance() {
		if (singleton == null) {
			singleton = new ODIE_IndexFinderBestMneumonicNameDeterminer() ;
		}
		return singleton ;
	}
	
	private ODIE_IndexFinderBestMneumonicNameDeterminer() {
		;
	}

	public String bestGuessForMneumonicLabel(String className,
			String[] rdfsLabels) {
		ArrayList<ScoredTerm> scores = new ArrayList<ScoredTerm>();
		scores.add(new ScoredTerm(className, new Integer(0)));
		if (rdfsLabels != null && rdfsLabels.length > 0) {
			for (String rdfsLabel : rdfsLabels) {
				scores.add(new ScoredTerm(rdfsLabel, new Integer(10)));
			}
		}
		// Favor alphabetic characters, remain neutral on white space
		// and penalize anything else thus we will prefer the
		// longest alphabetic name
		for (ScoredTerm scoredTerm : scores) {
			char[] chars = scoredTerm.term.toCharArray();
			for (char c : chars) {
				if (Character.isLetter(c)) {
					scoredTerm.score += 1;
				} else if (Character.isWhitespace(c)) {
					;
				} else {
					scoredTerm.score -= 1;
				}
			}
		}

		Collections.sort(scores, new Comparator<ScoredTerm>() {
			@Override
			public int compare(ScoredTerm o1, ScoredTerm o2) {
				return o2.score - o1.score;
			}
		});

		return scores.get(0).term;
	}

	@SuppressWarnings("unused")
	private String bestGuessForMneumonicLabelRdfs(String className,
			String[] rdfsLabels) {
		String result = className;
		String diagnostic = "bestGuessForMneumonicLabel " + className;
		if (rdfsLabels != null && rdfsLabels.length > 0) {
			for (String rdfsLabel : rdfsLabels) {
				diagnostic += "," + rdfsLabel;
			}
		}
		if (rdfsLabels != null && rdfsLabels.length > 0) {
			for (String rdfsLabel : rdfsLabels) {
				result = rdfsLabel;
				break;
			}
		}
		diagnostic += " result ==> " + result;
//		logger.debug(diagnostic);
		return result;
	}

	class ScoredTerm {
		public String term;
		public Integer score;

		public ScoredTerm(String term, Integer score) {
			this.term = term;
			this.score = score;
		}
	}

}
