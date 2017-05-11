package edu.pitt.dbmi.odie.uima.church.resource;

public interface ODIE_Gazetteer {
	public boolean contains(String entry) ;
	public void setCaseSensitive(boolean isCaseSensitive) ;
	public boolean isCaseSensitive() ;
}
