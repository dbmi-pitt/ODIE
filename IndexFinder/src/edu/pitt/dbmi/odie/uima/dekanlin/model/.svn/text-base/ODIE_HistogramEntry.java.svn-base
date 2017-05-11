package edu.pitt.dbmi.odie.uima.dekanlin.model;

public class ODIE_HistogramEntry {
	
	public Integer id;
	public String wordText;
	public String pos;
	public String tagger;
	public Integer freq;
	public Integer isNer;
	public Integer isNp;
	public String ontologyUri = "UNDEFINED" ;
	
	public ODIE_HistogramEntry() {
	}
	
	public ODIE_HistogramEntry(Integer id, String wordText, Integer freq,
			String pos, String tagger, Integer isNer, Integer isNp, String ontologyUri) {
		this.id = id ;
		this.wordText = wordText ;
		this.freq = freq ;
		this.pos = pos ;
		this.tagger = tagger ;
		this.isNer = isNer ;
		this.isNp = isNp ;
		this.ontologyUri = ontologyUri ;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id = " + id + ", ");
		sb.append("word = " + wordText + ", ");
		sb.append("pos = " + pos + ", ");
		sb.append("tagger = " + tagger + ", ");
		sb.append("freq = " + freq);
		sb.append("ontologyUri = " + ontologyUri);
		return sb.toString();
	}
	
	public String getWordText() {
		return wordText;
	}

	public void setWordText(String wordText) {
		this.wordText = wordText;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getTagger() {
		return tagger;
	}

	public void setTagger(String tagger) {
		this.tagger = tagger;
	}

	public Integer getFreq() {
		return freq;
	}

	public void setFreq(Integer freq) {
		this.freq = freq;
	}

	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getIsNer() {
		return isNer;
	}

	public void setIsNer(Integer isNer) {
		this.isNer = isNer;
	}

	public Integer getIsNp() {
		return isNp;
	}

	public void setIsNp(Integer isNp) {
		this.isNp = isNp;
	}
	
	public String getOntologyUri() {
		return ontologyUri;
	}

	public void setOntologyUri(String ontologyUri) {
		this.ontologyUri = ontologyUri;
	}

	
}
