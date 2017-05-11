package edu.pitt.dbmi.odie.server.statistics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class ODIE_CombinedWordPair {
	
	/* LOG4J logger based on class name */
	private static Logger logger = Logger.getLogger(ODIE_CombinedWordPair.class.getName());

	private static Connection conn;

	private static String databaseName;

	private static String qualifiedTableName;

	private Long id;
	private String wordOne;
	private String wordTwo;
	private Double scoreChurch ;
	private Double scoreLin ;
	private Double scoreLsp ;
	private Double normalizedScoreChurch ;
	private Double normalizedScoreLin ;
	private Double normalizedScoreLsp ;

	public static void initialize() {
		Statement sqlStmt = null;
		try {
			qualifiedTableName = databaseName + ".combined_word_pair";
			sqlStmt = conn.createStatement();
			sqlStmt.execute("drop table if exists " + qualifiedTableName);
			sqlStmt.close();

			StringBuffer sb = new StringBuffer();
			sb.append("CREATE TABLE " + qualifiedTableName + "(\n");
			sb.append("  ID bigint(20) NOT NULL AUTO_INCREMENT,\n");
			sb.append("  WORD_ONE varchar(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb.append("  WORD_TWO varchar(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb.append("  SCORE_CHURCH decimal(11,2) NOT NULL DEFAULT -1.00,\n");
			sb.append("  SCORE_LIN decimal(11,2) NOT NULL DEFAULT -1.00,\n");
			sb.append("  SCORE_LSP decimal(11,2) NOT NULL DEFAULT -1.00,\n");
			sb.append("  NSCORE_CHURCH decimal(11,2) NOT NULL DEFAULT -1.00,\n");
			sb.append("  NSCORE_LIN decimal(11,2) NOT NULL DEFAULT -1.00,\n");
			sb.append("  NSCORE_LSP decimal(11,2) NOT NULL DEFAULT -1.00,\n");
			sb.append("  PRIMARY KEY (ID),\n");
			sb.append("  KEY INDEX_WORD (WORD_ONE, WORD_TWO)\n");
			sb
					.append(") ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC");
			sqlStmt = conn.createStatement();
			sqlStmt.execute(sb.toString());
			sqlStmt.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ODIE_CombinedWordPair() {
		;
	}

	public static Connection getConn() {
		return conn;
	}

	public static void setConn(Connection conn) {
		ODIE_CombinedWordPair.conn = conn;
	}

	public static String getDatabaseName() {
		return databaseName;
	}

	public static void setDatabaseName(String databaseName) {
		ODIE_CombinedWordPair.databaseName = databaseName;
	}

	
	public static ODIE_CombinedWordPair fetchOrCreateOdieCombinedWordPair(String wordOne, String wordTwo) {
		ODIE_CombinedWordPair result = fetchOdieCombinedWordPair(wordOne, wordTwo);
		if (result == null) {
			insertOdieCombinedWordPair(wordOne, wordTwo);
			result = fetchOdieCombinedWordPair(wordOne, wordTwo);
		}
		return result;
	}


	public static ODIE_CombinedWordPair fetchOdieCombinedWordPair(String wordOne, String wordTwo) {
		ODIE_CombinedWordPair result = null;
		try {
			PreparedStatement preparedStatementFetchOdieCombinedWordPair = conn
					.prepareStatement("select * from " + qualifiedTableName
							+ " where WORD_ONE like ? and WORD_TWO like ?");
			preparedStatementFetchOdieCombinedWordPair.setString(1, wordOne);
			preparedStatementFetchOdieCombinedWordPair.setString(2, wordTwo);
			ResultSet rs = preparedStatementFetchOdieCombinedWordPair.executeQuery();
			while (rs.next()) {
				result = new ODIE_CombinedWordPair();
				result.setId(rs.getLong("id"));
				result.setWordOne(rs.getString("word_one"));
				result.setWordTwo(rs.getString("word_two"));
				result.setScoreChurch(rs.getDouble("score_church"));
				result.setScoreLin(rs.getDouble("score_lin"));
				result.setScoreLsp(rs.getDouble("score_lsp"));
				result.setNormalizedScoreChurch(rs.getDouble("nscore_church"));
				result.setNormalizedScoreLin(rs.getDouble("nscore_lin"));
				result.setNormalizedScoreLsp(rs.getDouble("nscore_lsp"));
				break;
			}
			preparedStatementFetchOdieCombinedWordPair.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static ODIE_CombinedWordPair insertOdieCombinedWordPair(String wordOne, String wordTwo) {
		ODIE_CombinedWordPair result = null;
		try {
			PreparedStatement preparedStatementEnterOdieCombinedWordPair = conn
					.prepareStatement("insert into "
							+ qualifiedTableName
							+ " (word_one, word_two) values (?, ?)");
			preparedStatementEnterOdieCombinedWordPair.setString(1, wordOne);
			preparedStatementEnterOdieCombinedWordPair.setString(2, wordTwo);
			preparedStatementEnterOdieCombinedWordPair.executeUpdate();
			preparedStatementEnterOdieCombinedWordPair.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void updateOdieCombinedWordPair(ODIE_CombinedWordPair odieTerm) {
		try {
			StringBuffer sb = new StringBuffer() ;
			sb.append("update " + qualifiedTableName + " set ") ;
			sb.append("score_church = ?, ") ;
			sb.append("score_lin = ?, ") ;
			sb.append("score_lsp = ?, ") ;
			sb.append("nscore_church = ?, ") ;
			sb.append("nscore_lin = ?, ") ;
			sb.append("nscore_lsp = ? ") ;
			sb.append(" where id = ?") ;
			PreparedStatement preparedStatementUpdateOdieCombinedWordPair = conn
					.prepareStatement(sb.toString());
			preparedStatementUpdateOdieCombinedWordPair.setDouble(1, odieTerm.getScoreChurch());
			preparedStatementUpdateOdieCombinedWordPair.setDouble(2, odieTerm.getScoreLin());
			preparedStatementUpdateOdieCombinedWordPair.setDouble(3, odieTerm.getScoreLsp());
			preparedStatementUpdateOdieCombinedWordPair.setDouble(4, odieTerm.getNormalizedScoreChurch());
			preparedStatementUpdateOdieCombinedWordPair.setDouble(5, odieTerm.getNormalizedScoreLin());
			preparedStatementUpdateOdieCombinedWordPair.setDouble(6, odieTerm.getNormalizedScoreLsp());
			preparedStatementUpdateOdieCombinedWordPair.setLong(7, odieTerm.getId());
			preparedStatementUpdateOdieCombinedWordPair.executeUpdate();
			preparedStatementUpdateOdieCombinedWordPair.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void normalizeRanges() {
		normalizeRanges("church") ;
		normalizeRanges("lin") ;
		normalizeRanges("lsp") ;
	}
	
	public static void normalizeRanges(String method) {
		try {
			Double dataMin = getDataMin(method) ;
			Double dataMax = getDataMax(method) ;
			Double dataRange = dataMax - dataMin ;
			Double normalizationRatio = 1.0d / dataRange ;
			String scoreColumnName = "score_" + method ;
			String nScoreColumnName = "nscore_" + method ;
			StringBuffer sb = new StringBuffer() ;
			sb.append("update " + qualifiedTableName) ;
			sb.append(" set " + nScoreColumnName + " = (" + scoreColumnName + " - ?) * ? ") ;
			sb.append(" where " + scoreColumnName + " >= 0.00") ;
			String sql = sb.toString() ;
			logger.debug(sql) ;
			PreparedStatement normalizePreparedStatement;
			normalizePreparedStatement = conn.prepareStatement(sql);
			normalizePreparedStatement.setDouble(1, dataMin) ;
			normalizePreparedStatement.setDouble(2, normalizationRatio) ;
			normalizePreparedStatement.executeUpdate() ;
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	
	private static Double getDataMax(String method) {
		Double result = null ;
		try {
			String columnName = "score_" + method ;
			StringBuffer sb = new StringBuffer() ;
			sb.append("select max(" + columnName + ") ") ;
			sb.append("from " + qualifiedTableName + " ") ;
			sb.append(" where ") ;
			sb.append(columnName + " >= 0.00") ;
			String sql = sb.toString() ;	
			PreparedStatement selectMaxValuePreparedStatement = conn.prepareStatement(sql);
			ResultSet rs = selectMaxValuePreparedStatement.executeQuery() ;
			if (rs.next()) {
				result = rs.getDouble(1) ;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result ;
	}
	
	private static Double getDataMin(String method) {
		Double result = null ;
		try {
			String columnName = "score_" + method ;
			StringBuffer sb = new StringBuffer() ;
			sb.append("select min(" + columnName + ") ") ;
			sb.append("from " + qualifiedTableName + " ") ;
			sb.append(" where ") ;
			sb.append(columnName + " >= 0.00") ;
			String sql = sb.toString() ;	
			PreparedStatement selectMaxValuePreparedStatement = conn.prepareStatement(sql);
			ResultSet rs = selectMaxValuePreparedStatement.executeQuery() ;
			if (rs.next()) {
				result = rs.getDouble(1) ;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result ;
	}
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWordOne() {
		return wordOne;
	}

	public void setWordOne(String wordOne) {
		this.wordOne = wordOne;
	}

	public String getWordTwo() {
		return wordTwo;
	}

	public void setWordTwo(String wordTwo) {
		this.wordTwo = wordTwo;
	}

	public Double getScoreChurch() {
		return scoreChurch;
	}

	public void setScoreChurch(Double scoreChurch) {
		this.scoreChurch = scoreChurch;
	}

	public Double getScoreLin() {
		return scoreLin;
	}

	public void setScoreLin(Double scoreLin) {
		this.scoreLin = scoreLin;
	}

	public Double getScoreLsp() {
		return scoreLsp;
	}

	public void setScoreLsp(Double scoreLsp) {
		this.scoreLsp = scoreLsp;
	}
	
	public Double getNormalizedScoreChurch() {
		return normalizedScoreChurch;
	}
	
	public void setNormalizedScoreChurch(Double normalizedScoreChurch) {
		this.normalizedScoreChurch = normalizedScoreChurch;
	}

	public Double getNormalizedScoreLin() {
		return normalizedScoreLin;
	}

	public void setNormalizedScoreLin(Double normalizedScoreLin) {
		this.normalizedScoreLin = normalizedScoreLin;
	}

	public Double getNormalizedScoreLsp() {
		return normalizedScoreLsp;
	}

	public void setNormalizedScoreLsp(Double normalizedScoreLsp) {
		this.normalizedScoreLsp = normalizedScoreLsp;
	}
	
}