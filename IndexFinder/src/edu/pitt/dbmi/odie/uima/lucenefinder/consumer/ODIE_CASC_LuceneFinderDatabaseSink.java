package edu.pitt.dbmi.odie.uima.lucenefinder.consumer;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import edu.mayo.bmi.uima.core.type.NamedEntity;
import edu.pitt.dbmi.odie.ODIEUtils;
import edu.pitt.dbmi.odie.uima.util.ODIE_Connection;
import edu.pitt.dbmi.odie.uima.util.ODIE_IFConstants;
import edu.pitt.dbmi.odie.uima.util.ODIE_UimaUtils;

/**
 * Stores NamedEntity annotations as determined by the ODIE_LuceneFinder NER engine
 * into an RDBMS table.
 * 
 */
public class ODIE_CASC_LuceneFinderDatabaseSink extends CasConsumer_ImplBase {

	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ODIE_CASC_LuceneFinderDatabaseSink.class);

	private ODIE_Connection conn = null;

	private String databaseName;
	private String tableNamePrefix;
	
	private PreparedStatement preparedStatementCreateSuggested = null;
	
	private boolean isDatabaseCleaning = false ;

	public void initialize() throws ResourceInitializationException {

		String driver = (String) getConfigParameterValue(ODIE_IFConstants.PARAM_DB_DRIVER);
		String url = (String) getConfigParameterValue(ODIE_IFConstants.IF_PARAM_DB_URL);
		this.databaseName = ODIEUtils.extractDatabaseNameFromDbUrl(url);
		String userName = (String) getConfigParameterValue(ODIE_IFConstants.PARAM_DB_USERNAME);
		String password = (String) getConfigParameterValue(ODIE_IFConstants.PARAM_DB_PASSWORD);
		
		conn = new ODIE_Connection(driver, url, userName, password) ;
		
		String isDatabaseCleaningAsString = (String) getConfigParameterValue(ODIE_IFConstants.PARAM_CHURCH_IS_DATABASE_CLEANING);
		setDatabaseCleaning(new Boolean(isDatabaseCleaningAsString)) ;

		if (isDatabaseCleaning()) {
			dropNamedEntitySetTable() ;
		}
		buildNamedEntitySetTable();
	}

	/**
	 * Processes the CasContainer which was populated by the
	 * TextAnalysisEngines. <br>
	 * In this case, the CAS is assumed to contain annotations of type
	 * ODIE_Word, created with the ODIE_Tokeniser. These Annotations are stored
	 * in a database table called church_analysis.
	 * 
	 * @param aCAS
	 *            CasContainer which has been populated by the TAEs
	 * 
	 * @throws ResourceProcessException
	 *             if there is an error in processing the Resource
	 * 
	 * @see org.apache.uima.collection.base_cpm.CasObjectProcessor#processCas(org.apache.uima.cas.CAS)
	 */
	public void processCas(CAS aCAS) throws ResourceProcessException {

		JCas jcas;
		try {
			jcas = aCAS.getJCas();
		} catch (CASException e) {
			throw new ResourceProcessException(e);
		}

		try {
			//
			// Get the singleton instance of the SourceDocumentInformation
			//
			SourceDocumentInformation sdi = (SourceDocumentInformation) jcas
					.getAnnotationIndex(SourceDocumentInformation.type)
					.iterator().next();
		
			//
			// Cache the NamedEntities for this document
			//
			ArrayList<NamedEntity> namedEntities = ODIE_UimaUtils
					.pullNamedEntitiesFromJCas(jcas);

			for (NamedEntity ne : namedEntities) {
				StringBuffer sb = new StringBuffer() ;
				sb.append("insert into ") ;
				sb.append(this.databaseName) ;
				sb.append(".") ;
				sb.append("NAMED_ENTITY ") ;
				sb.append("(") ;
				sb.append("uuid,") ;
				sb.append("spos,") ;
				sb.append("epos,") ;
				sb.append("uri,") ;
				sb.append("ctext") ;
				sb.append(") ") ;
				sb.append(" values ") ;
				sb.append("(?,?,?,?,?)") ;
				String sql = sb.toString() ;
				
				PreparedStatement pStmt = conn.prepareStatement(sql) ;
				pStmt.setString(1, sdi.getUri()) ;
				pStmt.setLong(2, ne.getBegin()) ;
				pStmt.setLong(3, ne.getEnd()) ;
				pStmt.setString(4, ODIE_UimaUtils.getUriFromNamedEntity(ne)) ;
				pStmt.setString(5, ne.getCoveredText()) ;
				pStmt.executeUpdate() ;
				pStmt.close() ;
			}
			
		} catch (SQLException e) {
			throw new ResourceProcessException(e);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	public void collectionProcessComplete(ProcessTrace arg0)
			throws ResourceProcessException, IOException {
		
	}

	public void finalize() {
		try {

			if (conn != null && !conn.isClosed()) {
				conn.close();
				conn = null;
			}

		} catch (SQLException e) {

		}
	}

	@SuppressWarnings("unused")
	private void dropNamedEntitySetTable() {
		try {
			String qualifiedTableName = databaseName + ".NAMED_ENTITY";
			Statement dropStatement;
			dropStatement = conn.createStatement();
			String sql = "drop table if exists " + qualifiedTableName;
			dropStatement.executeUpdate(sql);
			dropStatement.close();
		} catch (SQLException e) {
			;
		}
	}

	private void buildNamedEntitySetTable() {
		try {
			String qualifiedTableName = databaseName + ".NAMED_ENTITY";
			StringBuffer sb = new StringBuffer();
			sb.append("create table if not exists " + qualifiedTableName
					+ " (\n");
			sb.append("  ID int NOT NULL AUTO_INCREMENT,\n");
			sb.append("  uuid VARCHAR(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb.append("  spos INT,\n");
			sb.append("  epos INT,\n");
			sb.append("  uri VARCHAR(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb.append("  ctext VARCHAR(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb.append("  PRIMARY KEY (ID)\n");
			sb.append(") ENGINE=MyISAM");
			Statement sqlStmt = conn.createStatement();
			sqlStmt.execute(sb.toString());
			sqlStmt.close() ;

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean isDatabaseCleaning() {
		return isDatabaseCleaning;
	}

	public void setDatabaseCleaning(boolean isDatabaseCleaning) {
		this.isDatabaseCleaning = isDatabaseCleaning;
	}


}
