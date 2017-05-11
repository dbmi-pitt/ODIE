package edu.pitt.dbmi.odie.uima.lucenefinder.ae.stategy;

import java.util.ArrayList;

import edu.pitt.dbmi.odie.server.indexfinder.ODIE_IndexFinderAnnotation;

public interface ODIE_LuceneNerStrategyInterface {
	
	public void setSortedTokens(ArrayList<ODIE_IndexFinderAnnotation> sentenceTokensAnnots) ;
	public void execute()  ;
	public ArrayList<ODIE_IndexFinderAnnotation> getResultingConcepts() ;
	
}
