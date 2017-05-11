package edu.pitt.dbmi.odie.uima.church.cache.model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import edu.pitt.dbmi.odie.uima.church.model.ODIE_HistogramEntryFactoryInf;
import edu.pitt.dbmi.odie.uima.church.model.ODIE_HistogramEntryInf;

public class ODIE_HistogramEntryFactory implements ODIE_HistogramEntryFactoryInf {

	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ODIE_HistogramEntryFactory.class);

	private final HashMap<String, ODIE_HistogramEntryInf> cachedByTermHistogramEntries = new HashMap<String, ODIE_HistogramEntryInf>();

	private final HashMap<Integer, ODIE_HistogramEntryInf> cachedByIdHistogramEntries = new HashMap<Integer, ODIE_HistogramEntryInf>();

	private final TreeSet<ODIE_HistogramEntryInf> sortedEntries = new TreeSet<ODIE_HistogramEntryInf>(
			new Comparator<ODIE_HistogramEntryInf>() {
				public int compare(ODIE_HistogramEntryInf o1,
						ODIE_HistogramEntryInf o2) {
					return o1.getId() - o2.getId();
				}
			});
	
	

	private int idGeneratingIdx = 0 ;

	private long numberOfEntries;

	public long getNumberOfEntries() {
		this.numberOfEntries = this.cachedByIdHistogramEntries.values().size();
		return this.numberOfEntries ;
	}

	public void updateHistogramEntry(ODIE_HistogramEntry entry) {
		this.cachedByIdHistogramEntries.put(entry.getId(), entry) ;
	}

	public ODIE_HistogramEntryInf fetchOrCreateHistogramEntry(String word) {
		ODIE_HistogramEntryInf entry = this.cachedByTermHistogramEntries.get(word);
		if (entry == null) {
			entry = new ODIE_HistogramEntry() ;
			entry.setId(new Integer(this.idGeneratingIdx++)) ;
			entry.setFreq(1) ;
			entry.setIsNer(new Integer(0)) ;
			entry.setOntologyUri("") ;
			entry.setWordText(word) ;
			updateHistogramEntry(entry) ;
			logger.debug("Generated new ODIE_HistogramEntry id = " + entry.getId() ) ;
		}
		return entry;
	}
	
	public ODIE_HistogramEntryInf fetchHistogramEntry(String word) {
		ODIE_HistogramEntryInf result = this.cachedByTermHistogramEntries.get(word);
		if (result == null) {
			result = fetchOrCreateHistogramEntry(word) ;
		}
		return result ;
	}


	public ODIE_HistogramEntryInf selectHistogramEntry(String word) {
		return this.cachedByTermHistogramEntries.get(word);	
	}

	public ODIE_HistogramEntryInf lookUpEntryById(Integer wordOneId) {
		ODIE_HistogramEntryInf entry = this.cachedByIdHistogramEntries.get(wordOneId) ;
		if (entry == null) {
			logger.fatal("Faled to lookUpEntryById for id = " + wordOneId) ;
		}
		return entry ;
	}

	public Integer lookUpWordFreqById(Integer wordOneId) {
		Integer result = new Integer(0) ;
		ODIE_HistogramEntryInf entry = lookUpEntryById(wordOneId);
		if (entry == null) {
			logger.fatal("lookUpWordFeqById for " + wordOneId + " fails to find entry") ;
		}
		else {
			result = new Integer(entry.getFreq()) ;
		}
		return result ;
	}

	public void updateHistogramEntry(ODIE_HistogramEntryInf entry) {
		this.cachedByTermHistogramEntries.put(entry.getWordText(), entry) ;
		this.cachedByIdHistogramEntries.put(entry.getId(), entry) ;
	}
	
	public TreeSet<ODIE_HistogramEntryInf> getSortedEntries() {
		sortedEntries.clear() ;
		sortedEntries.addAll(this.cachedByIdHistogramEntries.values()) ;
		return sortedEntries;
	}

}
