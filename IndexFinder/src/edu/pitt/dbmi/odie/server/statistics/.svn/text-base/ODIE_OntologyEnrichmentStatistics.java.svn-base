package edu.pitt.dbmi.odie.server.statistics;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.upmc.opi.caBIG.caTIES.client.dispatcher.temporal.CaTIES_CombinationGenerator;
import edu.upmc.opi.caBIG.caTIES.common.CaTIES_FormatUtils;

public class ODIE_OntologyEnrichmentStatistics {
	
	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ODIE_OntologyEnrichmentStatistics.class);
	
	public static final String KEY_DB_DRIVER = "odie.db.driver";
	public static final String KEY_DB_USER_NAME = "odie.db.user.name";
	public static final String KEY_DB_USER_PASSWORD = "odie.db.user.password";
	
	private String lspDatabaseName = "kaihong102008" ;
	private String lspDatabaseUrl = "jdbc:mysql://localhost:3306/" + lspDatabaseName ;
	private String lspSentenceTableName = "odie_unique_sentence" ;
	private int lspTermsPerLspBoundry = 12 ;
	private String lspTermPrefix = "term_" ;
	private String lspBlank = "BLANK" ;
	
	private Connection conn = null ;
	
	public static void main(String[] args) {
		new ODIE_OntologyEnrichmentStatistics() ;
	}
	
	public ODIE_OntologyEnrichmentStatistics() {
		this.conn = getDatabaseConnection(this.lspDatabaseUrl) ;
		scoreLspTerms() ;
	}
	
	private Connection getDatabaseConnection(String url) {
		Connection result = null ;
		try {
			String driver = System.getProperty(KEY_DB_DRIVER) ;
			String userName = System.getProperty(KEY_DB_USER_NAME) ;
			String userPassword = System.getProperty(KEY_DB_USER_PASSWORD) ;
			Class.forName(driver).newInstance();
			result = DriverManager.getConnection(url, userName, userPassword);
		} catch (Exception e) {
			e.printStackTrace() ;
		}
		return result ;
	}
	
	public void destroy() {
		if(conn==null)
			return;
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	private void scoreLspTerms() {
		try {
			ODIE_LspWordPair.setDatabaseName(this.lspDatabaseName) ;
			ODIE_LspWordPair.setConn(this.conn) ;
			ODIE_LspWordPair.initialize();
			
			long minId = getMinLspId() ;
			long maxId = getMaxLspId() ;
			for (long idx = minId ; idx <= maxId ; idx++) {
				if (idx % 100 == 0) {
					logger.debug("Processed " + idx + " lsp sentences.") ;
				}
				String sql = "select * from " + this.lspSentenceTableName + " where id = ?";
				PreparedStatement fetchLspSentencePreparedStatement = this.conn.prepareStatement(sql) ;
				fetchLspSentencePreparedStatement.setLong(1, idx) ;
				ResultSet rs = fetchLspSentencePreparedStatement.executeQuery();
				ArrayList<String> lspTerms = new ArrayList<String>() ;
				if (rs.next()) {
					for (int jdx = 0 ; jdx < lspTermsPerLspBoundry ; jdx++) {
						String columnName = lspTermPrefix + CaTIES_FormatUtils.formatIntegerAsDigitString(jdx, "00") ;
						String lspTerm = rs.getString(columnName) ;
						if (lspTerm.equals(lspBlank)) {
							break ;
						}
						else {
							lspTerms.add(lspTerm) ;
						}
					}
				}
				fetchLspSentencePreparedStatement.close();
				scoreLspTerms(lspTerms) ;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	
	private void scoreLspTerms(ArrayList<String> lspTerms) {
		if (lspTerms.size() > 1) {
			CaTIES_CombinationGenerator combinationGenerator = new CaTIES_CombinationGenerator() ;
			int[][] combiniationIndices = combinationGenerator.nChooseKWithNoReplacement(lspTerms.size(), 2, CaTIES_CombinationGenerator.NONCONTIGUOUS) ;
			for (int idx = 0 ; idx < combiniationIndices.length ; idx++) {
				String termOne = lspTerms.get(combiniationIndices[idx][0]) ;
				String termTwo = lspTerms.get(combiniationIndices[idx][1]) ;
				scoreLspTermPair(termOne, termTwo) ;
			}
		}
	}

	private void scoreLspTermPair(String termOne, String termTwo) {
		String[] termOneWords = termOne.split("\\s") ;
		String[] termTwoWords = termTwo.split("\\s") ;
		for (int idx = 0 ; idx < termOneWords.length ; idx++) {
			for (int jdx = 0 ; jdx < termTwoWords.length ; jdx++) {
				String wordOne = termOneWords[idx] ;
				String wordTwo = termTwoWords[jdx] ;
				if (!wordOne.toLowerCase().equals(wordTwo.toLowerCase())) {
					ODIE_LspWordPair odieLspWordPair = ODIE_LspWordPair.fetchOrCreateOdieLspWordPair(wordOne, wordTwo) ;
					odieLspWordPair.setFreq(odieLspWordPair.getFreq()+1L) ;
					ODIE_LspWordPair.updateOdieLspWordPair(odieLspWordPair) ;
				}
			}
		}
	}

	private long getMinLspId() throws SQLException {
		long minId = Long.MAX_VALUE ;
		String sql = "select min(id) from " + this.lspSentenceTableName ;
		PreparedStatement fetchMinIdPreparedStatement = this.conn.prepareStatement(sql) ;
		ResultSet rs = fetchMinIdPreparedStatement.executeQuery() ;
		if (rs.next()) {
			minId = rs.getLong(1) ;
		}
		fetchMinIdPreparedStatement.close();
		return minId ;
	}
	
	private long getMaxLspId() throws SQLException {
		long maxId = Long.MIN_VALUE ;
		String sql = "select max(id) from " + this.lspSentenceTableName ;
		PreparedStatement fetchMaxIdPreparedStatement = this.conn.prepareStatement(sql) ;
		ResultSet rs = fetchMaxIdPreparedStatement.executeQuery() ;
		if (rs.next()) {
			maxId = rs.getLong(1) ;
		}
		fetchMaxIdPreparedStatement.close();
		return maxId ;
	}

}
