/**
 * 
 */
package edu.pitt.dbmi.odie.middletier;

import morphster.ontology.datamodel.Dbxref;

/**
 * @author Girish Chavan
 * 
 */
public class Configuration {

	private String databaseDriver;
	private String databaseUrl;
	private String password;
	private String repositoryConfigLocation;
	private String username;
	private String gateHome;
	private String hbm2ddl;
	private String repositoryTableName;
	private String temporaryDirectory;
	private String luceneIndexDirectory;
	private boolean noCharts;
	
	private boolean redirectStreams;
	
	
	
	public boolean isNoCharts() {
		return noCharts;
	}

	public void setNoCharts(boolean noCharts) {
		this.noCharts = noCharts;
	}

	public String getLuceneIndexDirectory() {
		return luceneIndexDirectory;
	}

	public void setLuceneIndexDirectory(String luceneIndexDirectory) {
		this.luceneIndexDirectory = luceneIndexDirectory;
	}

	public Configuration(){}
	
	public Configuration(Configuration conf) {
		databaseDriver = conf.databaseDriver;
		databaseUrl = conf.databaseUrl;
		password = conf.password;
		repositoryConfigLocation = conf.repositoryConfigLocation;
		username = conf.username;
		gateHome = conf.gateHome;
		hbm2ddl = conf.hbm2ddl;
		repositoryTableName = conf.repositoryTableName;
		temporaryDirectory = conf.temporaryDirectory;
	}

	public String getTemporaryDirectory() {
		return temporaryDirectory;
	}

	public void setTemporaryDirectory(String temporaryDirectory) {
		this.temporaryDirectory = temporaryDirectory;
	}

	public String getRepositoryTableName() {
		return repositoryTableName;
	}

	public void setRepositoryTableName(String repositoryTableName) {
		this.repositoryTableName = repositoryTableName;
	}

	public String getDatabaseDriver() {
		return databaseDriver;
	}

	public String getDatabaseURL() {
		return databaseUrl;
	}

	/**
	 * @return
	 */
	public String getGATEHome() {
		return gateHome;
	}

	public String getPassword() {
		return password;
	}

	public String getRepositoryConfigLocation() {
		return repositoryConfigLocation;
	}

	public String getUsername() {
		return username;
	}

	public void setDatabaseDriver(String databaseDriver) {
		this.databaseDriver = databaseDriver;
	}

	public void setDatabaseURL(String databaseUrl) {
		this.databaseUrl = databaseUrl;
	}

	public void setGATEHome(String path) {
		gateHome = path;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setRepositoryConfigLocation(String repositoryConfigLocation) {
		this.repositoryConfigLocation = repositoryConfigLocation;
	}

	public void setUsername(String username) {
		this.username = username;
	}


	public String getHBM2DDLPolicy() {
		return hbm2ddl;
	}
	
	public void setHBM2DDLPolicy(String hbm2ddlPolicy){
		hbm2ddl = hbm2ddlPolicy;
	}

	public String getDatabaseURLWithoutSchema() {
		return databaseUrl.substring(0,databaseUrl.lastIndexOf("/")+1);
	}

	public String getDatabaseName() {
		return databaseUrl.substring(databaseUrl.lastIndexOf("/")+1);
	}

	public void setRedirectStandardStreams(String redirect) {
		redirectStreams = new Boolean(redirect).booleanValue();
		
	}
	
	public boolean redirectStandardStreams(){
		return redirectStreams;
	}
}
