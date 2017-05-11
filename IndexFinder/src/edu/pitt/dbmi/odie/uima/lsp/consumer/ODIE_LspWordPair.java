package edu.pitt.dbmi.odie.uima.lsp.consumer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ODIE_LspWordPair {

	private static Connection conn;

	private static String databaseName;

	private static String qualifiedTableName;

	private Long id;
	private String wordOne;
	private String wordTwo;
	private Long freq;

	public static void initialize() {
		Statement sqlStmt = null;
		try {
			qualifiedTableName = databaseName + ".lsp_word_pair";
			sqlStmt = conn.createStatement();
			sqlStmt.execute("drop table if exists " + qualifiedTableName);
			sqlStmt.close();

			StringBuffer sb = new StringBuffer();
			sb.append("CREATE TABLE IF NOT EXISTS " + qualifiedTableName + "(\n");
			sb.append("  ID bigint(20) NOT NULL AUTO_INCREMENT,\n");
			sb.append("  WORD_ONE varchar(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb.append("  WORD_TWO varchar(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb.append("  FREQ int(11) NOT NULL DEFAULT 0,\n");
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

	private ODIE_LspWordPair() {
		;
	}

	public static Connection getConn() {
		return conn;
	}

	public static void setConn(Connection conn) {
		ODIE_LspWordPair.conn = conn;
	}

	public static String getDatabaseName() {
		return databaseName;
	}

	public static void setDatabaseName(String databaseName) {
		ODIE_LspWordPair.databaseName = databaseName;
	}

	
	public static ODIE_LspWordPair fetchOrCreateOdieLspWordPair(String wordOne, String wordTwo) {
		ODIE_LspWordPair result = fetchOdieLspWordPair(wordOne, wordTwo);
		if (result == null) {
			insertOdieLspWordPair(wordOne, wordTwo);
			result = fetchOdieLspWordPair(wordOne, wordTwo);
		}
		return result;
	}


	public static ODIE_LspWordPair fetchOdieLspWordPair(String wordOne, String wordTwo) {
		ODIE_LspWordPair result = null;
		try {
			PreparedStatement preparedStatementFetchOdieLspWordPair = conn
					.prepareStatement("select * from " + qualifiedTableName
							+ " where WORD_ONE = ? and WORD_TWO = ?");
			preparedStatementFetchOdieLspWordPair.setString(1, wordOne);
			preparedStatementFetchOdieLspWordPair.setString(2, wordTwo);
			ResultSet rs = preparedStatementFetchOdieLspWordPair.executeQuery();
			while (rs.next()) {
				result = new ODIE_LspWordPair();
				result.setId(rs.getLong("id"));
				result.setWordOne(rs.getString("word_one"));
				result.setWordTwo(rs.getString("word_two"));
				result.setFreq(rs.getLong("freq"));
				break;
			}
			preparedStatementFetchOdieLspWordPair.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static ODIE_LspWordPair insertOdieLspWordPair(String wordOne, String wordTwo) {
		ODIE_LspWordPair result = null;
		try {
			PreparedStatement preparedStatementEnterOdieLspWordPair = conn
					.prepareStatement("insert into "
							+ qualifiedTableName
							+ " (WORD_ONE, WORD_TWO, FREQ) values (?, ?, 0)");
			preparedStatementEnterOdieLspWordPair.setString(1, wordOne);
			preparedStatementEnterOdieLspWordPair.setString(2, wordTwo);
			preparedStatementEnterOdieLspWordPair.executeUpdate();
			preparedStatementEnterOdieLspWordPair.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void updateOdieLspWordPair(ODIE_LspWordPair odieTerm) {
		try {
			PreparedStatement preparedStatementUpdateOdieLspWordPair = conn
					.prepareStatement("update " + qualifiedTableName
							+ " set FREQ = ? where id = ?");
			preparedStatementUpdateOdieLspWordPair.setLong(1, odieTerm.getFreq());
			preparedStatementUpdateOdieLspWordPair.setLong(2, odieTerm.getId());
			preparedStatementUpdateOdieLspWordPair.executeUpdate();
			preparedStatementUpdateOdieLspWordPair.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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

	public Long getFreq() {
		return freq;
	}

	public void setFreq(Long freq) {
		this.freq = freq;
	}

}
