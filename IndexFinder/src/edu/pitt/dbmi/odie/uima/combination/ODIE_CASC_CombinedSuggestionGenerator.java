package edu.pitt.dbmi.odie.uima.combination;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import edu.pitt.dbmi.odie.ODIEUtils;
import edu.pitt.dbmi.odie.uima.util.ODIE_IFConstants;

public class ODIE_CASC_CombinedSuggestionGenerator extends CasConsumer_ImplBase {

	/* LOG4J logger based on class name */
	private static Logger logger = Logger
			.getLogger(ODIE_CASC_CombinedSuggestionGenerator.class.getName());

	private String databaseName;

	private Connection conn = null;

	public ODIE_CASC_CombinedSuggestionGenerator() {
	}

	public void initialize() throws ResourceInitializationException {
		try {
			configJdbcInit();

		} catch (InstantiationException e) {
			throw new ResourceInitializationException(e);
		} catch (IllegalAccessException e) {
			throw new ResourceInitializationException(e);
		} catch (ClassNotFoundException e) {
			throw new ResourceInitializationException(e);
		} catch (SQLException e) {
			throw new ResourceInitializationException(e);
		}
	}

	private void configJdbcInit() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		String driver = (String) getConfigParameterValue(ODIE_IFConstants.PARAM_DB_DRIVER);
		String url = (String) getConfigParameterValue(ODIE_IFConstants.IF_PARAM_DB_URL);
		String userName = (String) getConfigParameterValue(ODIE_IFConstants.PARAM_DB_USERNAME);
		String password = (String) getConfigParameterValue(ODIE_IFConstants.PARAM_DB_PASSWORD);
		Class.forName(driver).newInstance();
		this.conn = DriverManager.getConnection(url, userName, password);
		logger.info("Connected to the database at:" + url);
		this.databaseName = ODIEUtils.extractDatabaseNameFromDbUrl(url);
	}

	public void processCas(CAS arg0) throws ResourceProcessException {
		;
	}

	public void collectionProcessComplete(ProcessTrace arg0)
			throws ResourceProcessException, IOException {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("create or replace view " + this.databaseName + "."
					+ "suggestion_combined_vw as ");
			sb
					.append("    select ner_negative, ner_positive, method, rule, score from suggestion where method like 'LSP%'");
			sb.append("    union ");
			sb
					.append("    select ner_negative, ner_positive, method, rule, score from suggestion where method like 'Lin%'");
			sb.append("    union ");
			sb
					.append("    select ner_negative, ner_positive, method, rule, score from suggestion where method like 'Church%'");
			PreparedStatement createCombinedViewPreparedStatement = conn
					.prepareStatement(sb.toString());
			createCombinedViewPreparedStatement.executeUpdate();
			createCombinedViewPreparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
