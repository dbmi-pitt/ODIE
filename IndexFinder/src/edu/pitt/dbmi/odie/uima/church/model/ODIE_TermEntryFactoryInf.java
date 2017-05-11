package edu.pitt.dbmi.odie.uima.church.model;

import java.sql.Connection;

public interface ODIE_TermEntryFactoryInf {

	public void initialize(Connection connInput, String databaseNameParam,
			String tableNamePrefixParam) ;

	public ODIE_TermEntryInf fetchOrCreateTermEntry(Integer histogramId, String term) ;
	
	public ODIE_TermEntryInf createTermEntry(Integer histogramId, String term)  ;
	
	public ODIE_TermEntryInf readTermEntry(Integer histogramId, String term) ;
	
	public ODIE_TermEntryInf smallestTermOfHistogramId(Integer histogramId) ;
	
	public void updateTermEntry(ODIE_TermEntryInf entry) ;
	
	public void deleteTermEntry(ODIE_TermEntryInf entry) ;

	public boolean isDatabaseCleaning() ;

	public void setDatabaseCleaning(boolean isDatabaseCleaning) ;

}
