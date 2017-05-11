package edu.pitt.dbmi.odie.lexicalizer.indexfinder;

import java.util.Comparator;

public class ODIE_LexicalizerWord {
	
	public static Comparator<ODIE_LexicalizerWord> wordComparator = new Comparator<ODIE_LexicalizerWord>() {
		public int compare(ODIE_LexicalizerWord o1, ODIE_LexicalizerWord o2) {
			return o1.getWord().compareTo(o2.getWord());
		}} ;
	
	private String word = null ;
	
	public ODIE_LexicalizerWord(String word) {
		this.setWord(word);
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getWord() {
		return word;
	}
}
