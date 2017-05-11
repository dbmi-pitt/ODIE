package edu.pitt.dbmi.odie.uima.church.cache.model;

import edu.pitt.dbmi.odie.uima.church.model.ODIE_HistogramEntryInf;

public class ODIE_HistogramEntry implements ODIE_HistogramEntryInf {

	private Integer id;
	private String wordText = "UNDEFINED"; 
	private Integer freq = new Integer(0);
	private Integer isNer = new Integer(0);
	private String ontologyUri = "UNDEFINED";
	


	public ODIE_HistogramEntry() {	
	}

	public ODIE_HistogramEntry(Integer id, String wordText, Integer freq, Integer isNer, String ontologyUri) {
		this.id = id;
		this.wordText = wordText;
		this.freq = freq;
		this.isNer = isNer ;
		this.ontologyUri = ontologyUri ;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getWordText() {
		return wordText;
	}

	public void setWordText(String wordText) {
		this.wordText = wordText;
	}

	public Integer getFreq() {
		return freq;
	}

	public void setFreq(Integer freq) {
		this.freq = freq;
	}
	
	public Integer getIsNer() {
		return isNer;
	}

	public void setIsNer(Integer isNer) {
		this.isNer = isNer;
	}
	
	public String getOntologyUri() {
		return ontologyUri;
	}

	public void setOntologyUri(String ontologyUri) {
		this.ontologyUri = ontologyUri;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer() ;
		sb.append("WordText: " + getWordText() + ", ") ;
		sb.append("Freq: " + getFreq()) ;
		return sb.toString() ;
	}

	

}
