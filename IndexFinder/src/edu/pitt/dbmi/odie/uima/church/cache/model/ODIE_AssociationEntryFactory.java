package edu.pitt.dbmi.odie.uima.church.cache.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import edu.pitt.dbmi.odie.uima.church.model.ODIE_AssociationEntryFactoryInf;
import edu.pitt.dbmi.odie.uima.church.model.ODIE_AssociationEntryInf;
import edu.pitt.dbmi.odie.uima.church.model.ODIE_HistogramEntryFactoryInf;
import edu.pitt.dbmi.odie.uima.church.model.ODIE_HistogramEntryInf;

public class ODIE_AssociationEntryFactory implements ODIE_AssociationEntryFactoryInf {

	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ODIE_AssociationEntryFactory.class);

	private final HashMap<String, ODIE_AssociationEntryInf> cachedAssociationEntries = new HashMap<String, ODIE_AssociationEntryInf>() ;

	private final TreeSet<ODIE_AssociationEntryInf> sortedEntries = new TreeSet<ODIE_AssociationEntryInf>(
			new Comparator<ODIE_AssociationEntryInf>() {
				public int compare(ODIE_AssociationEntryInf o1,
						ODIE_AssociationEntryInf o2) {
					return o1.getId() - o2.getId();
				}
			});

	private final TreeSet<ODIE_AssociationEntryInf> scoredEntries = new TreeSet<ODIE_AssociationEntryInf>(
			new Comparator<ODIE_AssociationEntryInf>() {
				public int compare(ODIE_AssociationEntryInf o1,
						ODIE_AssociationEntryInf o2) {
					Double ixyOne = o1.getIxy();
					Double ixyTwo = o2.getIxy();
					if (ixyOne < ixyTwo) {
						return 1;
					} else if (ixyOne > ixyTwo) {
						return -1;
					} else {
						return 1;
					}
				}
			});
	
	private int idGeneratingIdx = 0 ;
	
	private Iterator<Entry<String, ODIE_AssociationEntryInf>> iterator ;

	private ODIE_HistogramEntryFactoryInf histogramEntryFactory = null;

	public void iterate() {
		iterator = this.cachedAssociationEntries.entrySet().iterator();
	}

	public ODIE_AssociationEntryInf next() {
		ODIE_AssociationEntryInf association = null ;
		if (iterator.hasNext()) {
			association = iterator.next().getValue() ;
		}
		return association ;
	}

	public void clearAssociationEntries() {
		cachedAssociationEntries.clear();
	}

	public void updateAssociationEntries() {
		;
	}

	public void updateAssociationEntry(ODIE_AssociationEntry entry) {
		this.cachedAssociationEntries.put(entry.getKey()+"", entry) ;
	}
	
	public ODIE_AssociationEntryInf fetchAssociationEntry(ODIE_HistogramEntryInf wordOneEntry,
			ODIE_HistogramEntryInf wordTwoEntry) {
		String associationKey = ODIE_AssociationEntry.buildKey(wordOneEntry.getId(), wordTwoEntry.getId());
		ODIE_AssociationEntryInf result = cachedAssociationEntries
				.get(associationKey);
		if (result == null) {
			result = fetchOrCreateAssociationEntry(wordOneEntry.getId(), wordTwoEntry.getId()) ;
		}
		return result;
	}
	
	public ODIE_AssociationEntryInf fetchOrCreateAssociationEntry(
			Integer wordOneId, Integer wordTwoId)  {
		  	String associationKey = ODIE_AssociationEntry.buildKey(wordOneId, wordTwoId);
			ODIE_AssociationEntryInf result = cachedAssociationEntries.get(associationKey);
			if (result == null) {
				result = new ODIE_AssociationEntry() ;
				result.setId(this.idGeneratingIdx++) ;
				result.setKey(associationKey) ;
				result.setWordOneId(wordOneId) ;
				result.setWordTwoId(wordTwoId) ;
				result.setFreq(1.0d) ;
				updateAssociationEntry(result) ;
				logger.debug("Entered new ODIE_AssociationEntry " + result.getKey()) ;
			}
		return result;
	}
	
	@SuppressWarnings("unused")
	private ODIE_AssociationEntryInf selectAssociationEntry(ODIE_HistogramEntryInf wordOneEntry,
			ODIE_HistogramEntryInf wordTwoEntry)  {
		return fetchAssociationEntry(wordOneEntry, wordTwoEntry);
	}

	public Collection<ODIE_AssociationEntryInf> scoreAssociationEntries(
			Long numberOfEntriesUpperBound, Long wordFrequencyUpperBound) {
		for (ODIE_AssociationEntryInf entry : cachedAssociationEntries.values()) {
			Integer wordOneFreq = histogramEntryFactory.lookUpWordFreqById(entry
					.getWordOneId());
			Integer wordTwoFreq = histogramEntryFactory.lookUpWordFreqById(entry
					.getWordTwoId());
			if (wordOneFreq > wordFrequencyUpperBound
					&& wordTwoFreq > wordFrequencyUpperBound) {
				scoredEntries.add(entry);
			}
		}
		return convertToArray(numberOfEntriesUpperBound, scoredEntries) ;
	}
	
	private ArrayList<ODIE_AssociationEntryInf> convertToArray(Long numberOfEntriesUpperBound, TreeSet<ODIE_AssociationEntryInf> treeSet) {
		final ArrayList<ODIE_AssociationEntryInf> result = new ArrayList<ODIE_AssociationEntryInf>();
		int idx = 0;
		for (ODIE_AssociationEntryInf entry : treeSet) {
			if (numberOfEntriesUpperBound > 0
					&& idx > numberOfEntriesUpperBound) {
				break;
			}
			result.add(entry);
			idx++;
		}
		return result ;
	}

	public HashMap<String, ODIE_AssociationEntryInf> getCachedAssociationEntries() {
		return cachedAssociationEntries;
	}

	public TreeSet<ODIE_AssociationEntryInf> getSortedEntries() {
		return sortedEntries;
	}

	public ODIE_HistogramEntryFactoryInf getHistogramEntryFactory() {
		return histogramEntryFactory;
	}

	@Override
	public void insertBatch() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertBatch(
			Collection<ODIE_AssociationEntryInf> associationEntries) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setHistogramEntryFactory(
			ODIE_HistogramEntryFactoryInf histogramEntryFactory) {
		this.histogramEntryFactory = histogramEntryFactory ;
	}

	@Override
	public void updateAndCacheAssociationEntry(ODIE_AssociationEntryInf entry) {
		this.cachedAssociationEntries.put(entry.getKey(), entry) ;
	}

	public void updateAssociationEntry(ODIE_AssociationEntryInf entry) {
		this.cachedAssociationEntries.put(entry.getKey(), entry) ;
	}

	@Override
	public void updateBatch(Collection<ODIE_AssociationEntryInf> associationEntries) {
		// TODO Auto-generated method stub
		
	}

}
