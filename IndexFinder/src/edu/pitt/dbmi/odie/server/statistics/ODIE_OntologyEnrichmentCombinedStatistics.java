package edu.pitt.dbmi.odie.server.statistics;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class ODIE_OntologyEnrichmentCombinedStatistics {
	
	/* LOG4J logger based on class name */
	private static Logger logger = Logger.getLogger(ODIE_OntologyEnrichmentCombinedStatistics.class.getName());

	public static final String KEY_DB_DRIVER = "odie.db.driver";
	public static final String KEY_DB_USER_NAME = "odie.db.user.name";
	public static final String KEY_DB_USER_PASSWORD = "odie.db.user.password";

	private int bestScoreBatchSize = 20000;

	private String churchDatabaseName = "odie_090109";
	private String churchDatabaseUrl = "jdbc:mysql://localhost:3306/"
			+ churchDatabaseName;
	private String churchWordPairScoresTableName = "church_word_pair_scores";

	private String linDatabaseName = "odie_090109";
	private String linDatabaseUrl = "jdbc:mysql://localhost:3306/"
			+ linDatabaseName;
	private String linSimilarityTableName = "lin_similarity";

	private String lspDatabaseName = "odie_090109";
	private String lspDatabaseUrl = "jdbc:mysql://localhost:3306/"
			+ lspDatabaseName;
	private String lspWordPairTableName = "lsp_score";

	private Connection lspDatabaseConnection = null;
	private Connection churchDatabaseConnection = null;
	private Connection linDatabaseConnection = null;

	public static void main(String[] args) {
		new ODIE_OntologyEnrichmentCombinedStatistics();
	}

	public ODIE_OntologyEnrichmentCombinedStatistics() {
		process();
	}

	private void process() {
		this.churchDatabaseConnection = getDatabaseConnection(this.churchDatabaseUrl);
		this.linDatabaseConnection = getDatabaseConnection(this.linDatabaseUrl);
		this.lspDatabaseConnection = getDatabaseConnection(this.lspDatabaseUrl);
		ODIE_CombinedWordPair.setDatabaseName(this.churchDatabaseName);
		ODIE_CombinedWordPair.setConn(this.churchDatabaseConnection);
		ODIE_CombinedWordPair.initialize();
		scoreChurchTermsDirectly();
		scoreLinTermsDirectly();
		scoreLspTermsDirectly();
		ODIE_CombinedWordPair.normalizeRanges();
		closeDatabaseConnection(this.lspDatabaseConnection);
		closeDatabaseConnection(this.linDatabaseConnection);
		closeDatabaseConnection(this.churchDatabaseConnection);
	}
	
	private void closeDatabaseConnection(Connection connection) {
		try {
			connection.close() ;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private Connection getDatabaseConnection(String url) {
		Connection result = null;
		try {
			String driver = System.getProperty(KEY_DB_DRIVER);
			String userName = System.getProperty(KEY_DB_USER_NAME);
			String userPassword = System.getProperty(KEY_DB_USER_PASSWORD);
			Class.forName(driver).newInstance();
			result = DriverManager.getConnection(url, userName, userPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private void scoreChurchTermsDirectly() {
		try {
			StringBuffer sb = new StringBuffer() ;
			sb.append("select * from ") ;
			sb.append(churchWordPairScoresTableName) ;
			sb.append(" order by ixy limit ?") ;
			String sql = sb.toString();
			PreparedStatement queryWordsPreparedStatement = this.churchDatabaseConnection
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
			PreparedStatement queryWordsPreparedStatement = this.linDatabaseConnection
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
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void scoreLspTermsDirectly() {
		try {
			String sql = "select * from lsp_score order by score desc, freq desc limit ?";
			PreparedStatement queryWordsPreparedStatement = this.lspDatabaseConnection
					.prepareStatement(sql);
			queryWordsPreparedStatement.setInt(1, bestScoreBatchSize);
			ResultSet rs = queryWordsPreparedStatement.executeQuery();
			while (rs.next()) {
				StringBuffer sb = new StringBuffer();
				sb.append("ixy = " + rs.getDouble("SCORE") + ", ");
				sb.append("x = " + rs.getString("TERM"));
				logger.debug(sb.toString());

				String wordOne = rs.getString("TERM");
				String wordTwo = "%" ;
				Double score = rs.getDouble("SCORE");

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
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
