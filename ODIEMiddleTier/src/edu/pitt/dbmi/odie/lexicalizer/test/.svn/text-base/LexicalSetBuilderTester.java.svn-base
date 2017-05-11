package edu.pitt.dbmi.odie.lexicalizer.test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.persistence.Table;

import org.apache.log4j.Logger;

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.dbmi.odie.ODIEUtils;
import edu.pitt.dbmi.odie.lexicalizer.indexfinder.LexicalSetBuilder;
import edu.pitt.dbmi.odie.middletier.Configuration;
import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.model.LanguageResource;
import edu.pitt.dbmi.odie.model.LexicalSet;

public class LexicalSetBuilderTester {
	static Logger logger = Logger.getLogger(LexicalSetBuilderTester.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Configuration conf = getConfiguration();
		MiddleTier mt = MiddleTier.getInstance(conf);
		LexicalSetBuilder lxb = new LexicalSetBuilder(mt);
		
		LexicalSet lexicalSet = new LexicalSet();
		String name = "testlexicalset";
		lexicalSet.setName(name);
		lexicalSet.setLocation( ODIEUtils.convertToValidDatabaseName(ODIEConstants.LEXSET_PREFIX + name));
		
		List<LanguageResource> languageResources = mt.getLanguageResources(null);
		
		for(LanguageResource lr:languageResources)
			lexicalSet.addLanguageResource(lr);
		
		lxb.createNewLexicalSet(lexicalSet,new PropertyChangeListener(){
			@Override
			public void propertyChange(PropertyChangeEvent pce) {
				if(pce.getPropertyName().equals(ODIEConstants.PROPERTY_PROGRESS_MSG)){
					logger.info(pce.getNewValue().toString());
				}
			}	
		});
	}

	private static Configuration getConfiguration() {
		Configuration conf = new Configuration();
		conf.setHBM2DDLPolicy("update");
		conf.setDatabaseDriver("com.mysql.jdbc.Driver");
		conf.setDatabaseURL("jdbc:mysql://1upmc-opi-cab52:3307/newodie");
		conf.setUsername("caties");
		conf.setPassword("caties");
		conf.setRepositoryTableName(LanguageResource.class.getAnnotation(Table.class).name());
		conf.setTemporaryDirectory(System.getProperty("java.io.tmpdir"));
		return conf;
	}

	
}
