package edu.pitt.dbmi.odie.middletier.test;

import javax.persistence.Table;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import edu.pitt.dbmi.odie.middletier.Configuration;
import edu.pitt.dbmi.odie.middletier.MiddleTierJDBC;
import edu.pitt.dbmi.odie.model.LanguageResource;

public class MiddleTierJDBCTester {
	static Logger logger = Logger.getLogger(MiddleTierJDBCTester.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		logger.setLevel(Level.ALL);
		
		Configuration conf = new Configuration();
		conf.setHBM2DDLPolicy("create");
		conf.setDatabaseDriver("com.mysql.jdbc.Driver");
		conf.setDatabaseURL("jdbc:mysql://localhost:3306/odie");
		conf.setUsername("caties");
		conf.setPassword("caties");
		conf.setRepositoryTableName(LanguageResource.class.getAnnotation(Table.class).name());
		conf.setTemporaryDirectory(System.getProperty("java.io.tmpdir"));
		conf.setLuceneIndexDirectory("c:/tmp/indices");
		
		try {
			MiddleTierJDBC mt = new MiddleTierJDBC(conf);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		boolean pass = false;
		if(pass)
			logger.info("All Tests Passed.");
		else
			logger.error("Some Tests Failed.");
				
	}
	
	

}
