package edu.pitt.dbmi.odie.uima.church.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;

public interface ODIE_AssociationEntryFactoryInf {

	public ODIE_AssociationEntryInf next() ;

	public void clearAssociationEntries() ;

	public void updateAssociationEntries() ;

	public void updateAssociationEntry(ODIE_AssociationEntryInf entry) ;
	
	public void updateAndCacheAssociationEntry(ODIE_AssociationEntryInf entry) ;

	public ODIE_AssociationEntryInf fetchAssociationEntry(ODIE_HistogramEntryInf wordOneEntry,
			ODIE_HistogramEntryInf wordTwoEntry) ;
	
	public Collection<ODIE_AssociationEntryInf> scoreAssociationEntries(
			Long numberOfEntriesUpperBound, Long wordFrequencyUpperBound) ;

	public void insertBatch() ;

	public void insertBatch(Collection<ODIE_AssociationEntryInf> associationEntries) ;
	
	public void updateBatch(Collection<ODIE_AssociationEntryInf> associationEntries) ;

	public HashMap<String, ODIE_AssociationEntryInf> getCachedAssociationEntries() ;

	public TreeSet<ODIE_AssociationEntryInf> getSortedEntries() ;

	public ODIE_HistogramEntryFactoryInf getHistogramEntryFactory() ;

	public void setHistogramEntryFactory(
			ODIE_HistogramEntryFactoryInf histogramEntryFactory) ;
	
}
