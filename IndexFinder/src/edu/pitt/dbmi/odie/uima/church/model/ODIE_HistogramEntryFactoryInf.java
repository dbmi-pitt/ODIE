package edu.pitt.dbmi.odie.uima.church.model;

public interface ODIE_HistogramEntryFactoryInf {
	
	public long getNumberOfEntries() ;

	public void updateHistogramEntry(ODIE_HistogramEntryInf entry) ;

	public ODIE_HistogramEntryInf fetchHistogramEntry(String word) ;

	public ODIE_HistogramEntryInf lookUpEntryById(Integer wordOneId) ;

	public Integer lookUpWordFreqById(Integer wordOneId) ;

}
