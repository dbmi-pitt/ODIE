package edu.pitt.dbmi.odie.uima.church.rdbms.model;

import edu.pitt.dbmi.odie.uima.church.model.ODIE_AssociationEntryInf;

public class ODIE_AssociationEntry implements ODIE_AssociationEntryInf {
	
	private Integer id;
	private Integer wordOneId;
	private Integer wordTwoId;
	private Double wordOneFreq;
	private Double wordTwoFreq;
	private Integer isNerOne;
	private Integer isNerTwo;
	private Double freq = new Double(0.00d);
	private Double ixy = new Double(0.00d);

	public ODIE_AssociationEntry() {
	}

	public ODIE_AssociationEntry(Integer id, Integer wordOneId,
			Integer wordTwoId, Double freq) {
		this.id = id;
		this.wordOneId = wordOneId;
		this.wordTwoId = wordTwoId;
		this.freq = freq;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getWordOneId() {
		return wordOneId;
	}

	public void setWordOneId(Integer wordOneId) {
		this.wordOneId = wordOneId;
	}

	public Integer getWordTwoId() {
		return wordTwoId;
	}

	public void setWordTwoId(Integer wordTwoId) {
		this.wordTwoId = wordTwoId;
	}

	public Double getFreq() {
		return freq;
	}

	public void setFreq(Double freq) {
		this.freq = freq;
	}
	
	private double log2(double num) {
		return (Math.log(num) / Math.log(2));
	}

	public Double getIxy() {
		return this.ixy;
	}

	public void setIxy(Double ixy) {
		this.ixy = ixy;
	}

	@Override
	public String getKey() {
		return null;
	}

	@Override
	public void setKey(String key) {
		// Not applicable for this implementation
	}

	public Double getWordOneFreq() {
		return wordOneFreq;
	}

	public void setWordOneFreq(Double wordOneFreq) {
		this.wordOneFreq = wordOneFreq;
	}

	public Double getWordTwoFreq() {
		return wordTwoFreq;
	}

	public void setWordTwoFreq(Double wordTwoFreq) {
		this.wordTwoFreq = wordTwoFreq;
	}

	
	public Integer getIsNerOne() {
		return isNerOne;
	}

	public void setIsNerOne(Integer isNerOne) {
		this.isNerOne = isNerOne;
	}

	public Integer getIsNerTwo() {
		return isNerTwo;
	}

	public void setIsNerTwo(Integer isNerTwo) {
		this.isNerTwo = isNerTwo;
	}
	

	


}