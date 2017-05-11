package edu.pitt.dbmi.odie.server.indexfinder;

import java.util.ArrayList;
import java.util.Comparator;


public class ODIE_Word extends ODIE_IndexedEntity {
	
	public String word = null;
	public ArrayList<ODIE_Phrase> phraseList = new ArrayList<ODIE_Phrase>();
	public Integer frequency = null;

	public ODIE_Word(String word) {
		this.word = word.toLowerCase();
	}

	public boolean equals(Object obj) {
		if (obj instanceof ODIE_Word) {
			return this.word.equals(((ODIE_Word) obj).word);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return word.hashCode();
	}

	public String toString() {
		return id + ") " + word + " ==> " + frequency;
	}
	
	public static Comparator<ODIE_Word> wordComparator = new Comparator<ODIE_Word>() {
		public int compare(ODIE_Word p1, ODIE_Word p2) {
			return p1.word.compareTo(p2.word);
		}
	};
}
