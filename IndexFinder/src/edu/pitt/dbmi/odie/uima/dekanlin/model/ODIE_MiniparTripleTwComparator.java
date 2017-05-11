package edu.pitt.dbmi.odie.uima.dekanlin.model;

import java.util.Comparator;

public class ODIE_MiniparTripleTwComparator implements
		Comparator<ODIE_MiniparTriple> {
	public int compare(ODIE_MiniparTriple tripleOne,
			ODIE_MiniparTriple tripleTwo) {
		String relOne = tripleOne.relation;
		String relTwo = tripleTwo.relation;
		String wordOne = tripleOne.wordTwo;
		String wordTwo = tripleTwo.wordTwo;
		int compareRelations = relOne.compareTo(relTwo);
		int compareWords = wordOne.compareTo(wordTwo);
		return (compareRelations == 0) ? compareWords : compareRelations;
	}
}
