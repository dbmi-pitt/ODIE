package edu.pitt.dbmi.odie.model;

import java.util.Collection;

public class Statistics {
	public Object context;
	public long uniqueConceptsCount;
	public long namedEntityCount;
	public long nounPhraseCount;
	public long sentenceCount;
	public long totalCharCount;
	public long coveredCharCount;
	
	public float getCoverage(){
		if(coveredCharCount > 0 && totalCharCount > 0){
			return ((float) coveredCharCount / (float) totalCharCount);
		}
		return 0;
	}
	public Collection<Statistics> ontologyStatistics;
}
