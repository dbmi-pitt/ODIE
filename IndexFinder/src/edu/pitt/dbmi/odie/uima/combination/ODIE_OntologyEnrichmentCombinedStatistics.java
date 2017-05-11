package edu.pitt.dbmi.odie.uima.combination;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import edu.pitt.dbmi.odie.ODIEUtils;
import edu.pitt.dbmi.odie.uima.util.ODIE_IFConstants;

public class ODIE_OntologyEnrichmentCombinedStatistics extends CasConsumer_ImplBase {
	
	/* LOG4J logger based on class name */
	private static Logger logger = Logger.getLogger(ODIE_OntologyEnrichmentCombinedStatistics.class.getName());

	private int bestScoreBatchSize = 20000;

	private String combinedDatabaseName;
	
	private String churchWordPairScoresTableName = "church_word_pair_scores";
	private String linSimilarityTableName = "lin_similarity";
	private String lspWordPairTableName = "lsp_word_pair";
	
	private Connection conn = null;

	public static void main(String[] args) {
		new ODIE_OntologyEnrichmentCombinedStatistics();
	}

	public ODIE_OntologyEnrichmentCombinedStatistics() {
		
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
	
	public void processCas(CAS arg0) throws ResourceProcessException {
		;
	}

	public void collectionProcessComplete(ProcessTrace arg0)
			throws ResourceProcessException, IOException {
		process();
	}

	private void configJdbcInit() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		String driver = (String) getConfigParameterValue(ODIE_IFConstants.PARAM_DB_DRIVER);
		String url = (String) getConfigParameterValue(ODIE_IFConstants.IF_PARAM_DB_URL);
		String userName = (String) getConfigParameterValue(ODIE_IFConstants.PARAM_DB_USERNAME);
		String password = (String) getConfigParameterValue(ODIE_IFConstants.PARAM_DB_PASSWORD);
		Class.forName(driver).newInstance();
		this.conn = DriverManager.getConnection(url, userName,
				password);
		logger.info("Connected to the database at:" + url);
		this.combinedDatabaseName = ODIEUtils.extractDatabaseNameFromDbUrl(url);
	}

	private void process() {
		ODIE_CombinedWordPair.setDatabaseName(this.combinedDatabaseName);
		ODIE_CombinedWordPair.setConn(this.conn);
		ODIE_CombinedWordPair.initialize();
		scoreChurchTermsDirectly();
		scoreLinTermsDirectly();
		scoreLspTermsDirectly();
		ODIE_CombinedWordPair.normalizeRanges();
	}

	private void scoreChurchTermsDirectly() {
		try {
			StringBuffer sb = new StringBuffer() ;
			sb.append("select * from ") ;
			sb.append(churchWordPairScoresTableName) ;
			sb.append(" order by ixy limit ?") ;
			String sql = sb.toString();
			PreparedStatement queryWordsPreparedStatement = this.conn
					.prepareStatement(sql);
			queryWordsPreparedStatement.setInt(1, bestScoreBatchSize);
			ResultSet rs = queryWordsPreparedStatement.executeQuery();
			while (rs.next()) {
				sb = new StringBuffer();
				sb.append("ixy = " + rs.getDouble("ixy") + ", ");
				sb.append("fxy = " + rs.getLong("fxy") + ", ");
				sb.append("fx = " + rs.getLong("fx") + ", ");
				sb.append("x = " + rs.getString("x") + ", ");
				sb.append("fy = " + rs.getLong("fy") + ", ");
				sb.append("y = " + rs.getString("y") + ", ");
				// System.out.println(sb.toString());

				String wordOne = rs.getString("x");
				String wordTwo = rs.getString("y");
				Double score = rs.getDouble("ixy");
				if (score >= 0.0d) {
					ODIE_CombinedWordPair combinedWordPair = ODIE_CombinedWordPair
							.fetchOrCreateOdieCombinedWordPair(wordOne, wordTwo);
					if (combinedWordPair.getScoreChurch() >= 0.0d) {
						combinedWordPair.setScoreChurch(combinedWordPair
								.getScoreChurch()
								+ score);
					} else {
						combinedWordPair.setScoreChurch(score);
					}

					ODIE_CombinedWordPair
							.updateOdieCombinedWordPair(combinedWordPair);
				}
			}
			queryWordsPreparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void scoreLinTermsDirectly() {
		try {
			StringBuffer sb = new StringBuffer() ;
			sb.append("select * from ") ;
			sb.append(this.linSimilarityTableName) ;
			sb.append(" order by similarity desc limit ?") ;
			String sql = sb.toString();
			PreparedStatement queryWordsPreparedStatement = this.conn
					.prepareStatement(sql);
			queryWordsPreparedStatement.setInt(1, bestScoreBatchSize);
			ResultSet rs = queryWordsPreparedStatement.executeQuery();
			while (rs.next()) {
				sb = new StringBuffer();
				sb.append("ixy = " + rs.getDouble("hindler") + ", ");
				sb.append("x = " + rs.getString("WORD_ONE") + ", ");
				sb.append("y = " + rs.getString("WORD_TWO") + ", ");
				logger.debug(sb.toString());

				String wordOne = rs.getString("WORD_ONE");
				String wordTwo = rs.getString("WORD_TWO");
				Double score = rs.getDouble("similarity");

				if (score >= 0.0d) {
					ODIE_CombinedWordPair combinedWordPair = ODIE_CombinedWordPair
							.fetchOrCreateOdieCombinedWordPair(wordOne, wordTwo);
					if (combinedWordPair.getScoreLin() >= 0.0d) {
						combinedWordPair.setScoreLin(combinedWordPair
								.getScoreLin()
								+ score);
					} else {
						combinedWordPair.setScoreLin(score);
					}
					ODIE_CombinedWordPair
							.updateOdieCombinedWordPair(combinedWordPair);
				}
			}
			queryWordsPreparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void scoreLspTermsDirectly() {
		try {
			StringBuffer sb = new StringBuffer() ;
			sb.append("select * from ") ;
			sb.append(this.lspWordPairTableName) ;
			sb.append(" order by freq desc limit ?") ;
			String sql = sb.toString();
			PreparedStatement queryWordsPreparedStatement = this.conn
					.prepareStatement(sql);
			queryWordsPreparedStatement.setInt(1, bestScoreBatchSize);
			ResultSet rs = queryWordsPreparedStatement.executeQuery();
			while (rs.next()) {
				sb = new StringBuffer();
				sb.append("ixy = " + rs.getDouble("FREQ") + ", ");
				sb.append("x = " + rs.getString("WORD_ONE"));
				sb.append("y = " + rs.getString("WORD_TWO"));
				logger.debug(sb.toString());

				String wordOne = rs.getString("WORD_ONE");
				String wordTwo = rs.getString("WORD_TWO");
				Double score = rs.getDouble("FREQ");

				if (score >= 0.0d) {
					ODIE_CombinedWordPair combinedWordPair = ODIE_CombinedWordPair
							.fetchOrCreateOdieCombinedWordPair(wordOne, wordTwo);
					if (combinedWordPair.getScoreLsp() >= 0.0d) {
						combinedWordPair.setScoreLsp(combinedWordPair
								.getScoreLsp()
								+ score);
					} else {
						combinedWordPair.setScoreLsp(score);
					}
					ODIE_CombinedWordPair
							.updateOdieCombinedWordPair(combinedWordPair);
				}
			}
			queryWordsPreparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
