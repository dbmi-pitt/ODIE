package edu.pitt.dbmi.odie.uima.dekanlin.model;

import edu.pitt.dbmi.odie.uima.util.ODIE_IndexFinderUtils;

public class ODIE_MiniparTriple {
	
	private static final String CONST_KEY_SEPARATOR = ":" ;
	
	public int id;
	
	public String wordOne;
	public String relation;
	public String wordTwo;
	public double freq;
	public double info;

	public ODIE_MiniparTriple() {
	}

	public ODIE_MiniparTriple(int id, String wordOne, String relation,
			String wordTwo, double freq, double info) {
		this.id = id;
		this.wordOne = ODIE_IndexFinderUtils.dbColConform(wordOne);
		this.relation = relation;
		this.wordTwo = ODIE_IndexFinderUtils.dbColConform(wordTwo);
		this.freq = freq;
		this.info = info;
	}

	public String get___r___key() {
		return relation;
	}

	public String get_w_r___key() {
		return wordOne + CONST_KEY_SEPARATOR + relation;
	}

	public String get___r_w_key() {
		String result = relation + CONST_KEY_SEPARATOR + wordTwo;
		return result;
	}
	
	public String getKey() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.wordOne);
		sb.append(CONST_KEY_SEPARATOR);
		sb.append(this.relation);
		sb.append(CONST_KEY_SEPARATOR);
		sb.append(this.wordTwo);
		return sb.toString();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.id);
		sb.append(", ");
		sb.append(this.wordOne);
		sb.append(", ");
		sb.append(this.relation);
		sb.append(", ");
		sb.append(this.wordTwo);
		sb.append(", ");
		sb.append(this.freq);
		sb.append(", ");
		sb.append(this.info);
		return sb.toString();
	}
	
	public String getWordOne() {
		return wordOne;
	}

	public void setWordOne(String wordOne) {
		this.wordOne = ODIE_IndexFinderUtils.dbColConform(wordOne);
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public String getWordTwo() {
		return wordTwo;
	}

	public void setWordTwo(String wordTwo) {
		this.wordTwo = ODIE_IndexFinderUtils.dbColConform(wordTwo);
	}

	public double getFreq() {
		return freq;
	}

	public void setFreq(double freq) {
		this.freq = freq;
	}

	public double getInfo() {
		return info;
	}

	public void setInfo(double info) {
		this.info = info;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}


}
