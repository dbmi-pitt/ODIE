package edu.pitt.dbmi.odie.uima.lsp.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ODIE_ScoredTerm {

	private static Connection conn;

	private static String databaseName;

	private static String qualifiedTableName;

	private Long id;
	private String term;
	private Long freq;
	private Double score;
	private String method;

	public static ODIE_ScoredTerm getInstance(String term) {
		return null;
	}

	public static void initialize() {
		Statement sqlStmt = null;
		try {
			qualifiedTableName = databaseName + ".lsp_score";
			sqlStmt = conn.createStatement();
			sqlStmt.execute("drop table if exists " + qualifiedTableName);
			sqlStmt.close();

			StringBuffer sb = new StringBuffer();
			sb.append("CREATE TABLE IF NOT EXISTS  " + qualifiedTableName + "(\n");
			sb.append("  ID bigint(20) NOT NULL AUTO_INCREMENT,\n");
			sb.append("  TERM varchar(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb.append("  FREQ int(11) NOT NULL DEFAULT 0,\n");
			sb.append("  SCORE decimal(11,2) NOT NULL DEFAULT 0.0,\n");
			sb.append("  METHOD varchar(255) NOT NULL DEFAULT 'LSP',\n");
			sb.append("  PRIMARY KEY (ID),\n");
			sb.append("  KEY INDEX_WORD (TERM)\n");
			sb
					.append(") ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC");
			sqlStmt = conn.createStatement();
			sqlStmt.execute(sb.toString());
			sqlStmt.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private ODIE_ScoredTerm() {
		;
	}

	public static Connection getConn() {
		return conn;
	}

	public static void setConn(Connection conn) {
		ODIE_ScoredTerm.conn = conn;
	}

	public static String getDatabaseName() {
		return databaseName;
	}

	public static void setDatabaseName(String databaseName) {
		ODIE_ScoredTerm.databaseName = databaseName;
	}

	public static ODIE_ScoredTerm fetchOrCreateOdieTerm(String term) {
		ODIE_ScoredTerm result = fetchOdieTerm(term);
		if (result == null) {
			insertOdieTerm(term);
			result = fetchOdieTerm(term);
		}
		return result;
	}

	public static ODIE_ScoredTerm fetchOdieTerm(String term) {
		ODIE_ScoredTerm result = null;
		try {
			PreparedStatement preparedStatementFetchOdieTerm = conn
					.prepareStatement("select * from " + qualifiedTableName
							+ " where TERM = ?");
			preparedStatementFetchOdieTerm.setString(1, term);
			ResultSet rs = preparedStatementFetchOdieTerm.executeQuery();
			while (rs.next()) {
				result = new ODIE_ScoredTerm();
				result.setId(rs.getLong("id"));
				result.setTerm(rs.getString("term"));
				result.setFreq(rs.getLong("freq"));
				result.setScore(rs.getDouble("score"));
				result.setMethod(rs.getString("method"));
				break;
			}
			preparedStatementFetchOdieTerm.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static ODIE_ScoredTerm insertOdieTerm(String term) {
		ODIE_ScoredTerm result = null;
		try {
			PreparedStatement preparedStatementEnterWord = conn
					.prepareStatement("insert into "
							+ qualifiedTableName
							+ " (TERM, FREQ, SCORE, METHOD) values (?, 0, 0.0, 'LSP')");
			preparedStatementEnterWord.setString(1, term);
			preparedStatementEnterWord.executeUpdate();
			preparedStatementEnterWord.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void updateOdieTerm(ODIE_ScoredTerm odieTerm) {
		try {
			PreparedStatement preparedStatementUpdateWord = conn
					.prepareStatement("update " + qualifiedTableName
							+ " set FREQ = ?, SCORE = ? where id = ?");
			preparedStatementUpdateWord.setLong(1, odieTerm.getFreq());
			preparedStatementUpdateWord.setDouble(2, odieTerm.getScore());
			preparedStatementUpdateWord.setLong(3, odieTerm.getId());
			preparedStatementUpdateWord.executeUpdate();
			preparedStatementUpdateWord.close();
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

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public Long getFreq() {
		return freq;
	}

	public void setFreq(Long freq) {
		this.freq = freq;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

}
