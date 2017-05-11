package edu.pitt.dbmi.odie.middletier.test;

import java.util.List;

import javax.persistence.Table;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import edu.pitt.dbmi.odie.middletier.AnalysisSpaceMiddleTier;
import edu.pitt.dbmi.odie.middletier.AnalysisSpaceMiddleTierFactory;
import edu.pitt.dbmi.odie.middletier.Configuration;
import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.LanguageResource;

public class AnalysisMiddleTierHibernateTester {

	static Logger logger = Logger.getLogger(AnalysisMiddleTierHibernateTester.class);
	private static AnalysisSpaceMiddleTier middleTier;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		logger.setLevel(Level.ALL);
		
		init();
		boolean pass = testHibernateCriteria();
		
		if(pass)
			logger.info("All Tests Passed.");
		else
			logger.error("Some Tests Failed.");
				
		destroy();
	}

	private static void destroy() {
		middleTier.dispose();
	}

	private static boolean testHibernateCriteria() {
		List l = middleTier.getAggregatedSuggestions(0.9f);
		return l.size()>0;
	}

	private static void init() {
		
		Configuration conf = new Configuration();
		conf.setHBM2DDLPolicy("create");
		conf.setDatabaseDriver("com.mysql.jdbc.Driver");
		conf.setDatabaseURL("jdbc:mysql://localhost:3306/odie");
		conf.setUsername("odieuser");
		conf.setPassword("odiepass");
		conf.setRepositoryTableName(LanguageResource.class.getAnnotation(Table.class).name());
		conf.setTemporaryDirectory(System.getProperty("java.io.tmpdir"));
		conf.setLuceneIndexDirectory("c:/tmp/indices");
		
		AnalysisSpaceMiddleTierFactory mtFactory = new AnalysisSpaceMiddleTierFactory(conf);
		
		Analysis a = new Analysis();
		a.setDatabaseName("od_space_oencit100");
		middleTier = mtFactory.getInstance(a);
	}

	
}
