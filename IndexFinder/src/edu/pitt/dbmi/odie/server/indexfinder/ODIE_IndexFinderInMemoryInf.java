package edu.pitt.dbmi.odie.server.indexfinder;

import java.sql.Connection;


public interface ODIE_IndexFinderInMemoryInf {
	public String getCnForCui(int cui) ;
	public ODIE_Word getWordForKey(String wordKey);
	public String fetchConceptUriForCui(Integer cui);
	public void addClass(String classQName);
	public void addSynonym(String classQName, String synonymousTerm) ;
	public void removeClass(String classQName);
	public void removeSynonym(String classQName, String synonymousTerm) ;
	public void clear() ;
//	public Connection getConnection() ;
//	public void setConnection(Connection conn) ;
	public void loadInMemoryTables();
}
