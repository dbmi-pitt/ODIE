package edu.pitt.dbmi.odie.uima.church.cache.model;

import java.sql.Timestamp;

import org.apache.log4j.Logger;

import edu.pitt.dbmi.odie.uima.church.model.ODIE_AssociationEntryInf;
import edu.pitt.dbmi.odie.uima.util.ODIE_FormatUtils;

public class ODIE_AssociationEntry implements ODIE_AssociationEntryInf {
	
	private static final Logger logger = Logger
			.getLogger(ODIE_AssociationEntry.class);
	
	private static final String CONST_KEY_SEPERATOR = ":";
	
	private Integer id;
	private String key;
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
		this.key = buildKey(wordOneId, wordTwoId) ;
		this.wordOneId = wordOneId ;
		this.wordTwoId = wordTwoId ;
		this.freq = freq ;
	}
	
	public static String buildKey(Integer wordOneId, Integer wordTwoId) {
		Integer minId = Math.min(wordOneId, wordTwoId) ;
		Integer maxId = Math.max(wordOneId, wordTwoId) ;
		return ODIE_FormatUtils.formatIntegerAsDigitString(minId, "000000000") 
		+ CONST_KEY_SEPERATOR + ODIE_FormatUtils.formatIntegerAsDigitString(maxId, "000000000") ;
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
		double fractionalPart = freq - Math.ceil(freq) ;
		if (fractionalPart > 0.0d) {
			logger.info("freq truncated to " + freq) ;
		}
		this.freq = freq;
	}

	public Double getIxy() {
		return ixy;
	}

	public void setIxy(Double ixy) {
		this.ixy = ixy;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
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
	
	public void setIsNerOne(Integer isNerOne) {
		this.isNerOne = isNerOne;
	}

	public void setIsNerTwo(Integer isNerTwo) {
		this.isNerTwo = isNerTwo;
	}

	public Integer getIsNerOne() {
		return this.isNerOne;
	}

	public Integer getIsNerTwo() {
		return isNerTwo;
	}


}