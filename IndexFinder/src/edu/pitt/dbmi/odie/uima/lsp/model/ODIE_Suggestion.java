package edu.pitt.dbmi.odie.uima.lsp.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class ODIE_Suggestion {
	
	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ODIE_Suggestion.class);

	private static Connection conn;

	private static String databaseName;

	private Long id;
	private String nerNegative = "UNDEFINED";
	private String nerPositive = "UNDEFINED";
	private String method = "UNDEFINED";
	private String rule = "UNDEFINED";
	private String relation = "UNDEFINED" ;


	private Double score = new Double(0.0d);

	public ODIE_Suggestion() {
	}

	public static void dropSuggested() {
		try {
			String qualifiedTableName = databaseName + "." + "suggestion";
			Statement dropStatement = conn.createStatement();
			String sql = "drop table if exists " + qualifiedTableName;
			dropStatement.executeUpdate(sql);
			dropStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void createSuggested() {
		try {
			String qualifiedTableName = databaseName + "." + "suggestion";
			StringBuffer sb = new StringBuffer();
			sb.append("CREATE TABLE IF NOT EXISTS " + qualifiedTableName + " (\n");
			sb.append("  ID int NOT NULL AUTO_INCREMENT,\n");
			sb.append("  NER_NEGATIVE VARCHAR(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb.append("  NER_POSITIVE VARCHAR(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb.append("  METHOD VARCHAR(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb.append("  RULE VARCHAR(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb.append("  RELATION VARCHAR(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb.append("  SCORE decimal(6,2) NOT NULL DEFAULT 0.00,\n");
			sb.append("  PRIMARY KEY (ID)\n");
			sb.append(") ENGINE=InnoDB");
			
			System.out.println(sb.toString());
			
			Statement sqlStmt = conn.createStatement();
			sqlStmt.execute(sb.toString());
			sqlStmt.close();
			
			

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static ODIE_Suggestion fetchOrCreateSuggested(String newNegative,
			String newPositive, ODIE_LSP lsp) {
		ODIE_Suggestion result = fetchSuggested(newNegative, newPositive, lsp);
		if (result.getId() == null) {
			insertSuggested(newNegative, newPositive, lsp);
			result = fetchSuggested(newNegative, newPositive, lsp);
		}
		return result;
	}

	public static ODIE_Suggestion insertSuggested(String newNegative,
			String newPositive, ODIE_LSP lsp) {
		ODIE_Suggestion result = new ODIE_Suggestion();
		try {
			PreparedStatement pStmt = conn
					.prepareStatement("insert into suggestion (NER_NEGATIVE, NER_POSITIVE, METHOD, RULE, RELATION, SCORE) values (?, ?, ?, ?, ?, ?)");
			pStmt.setString(1, newNegative);
			pStmt.setString(2, newPositive);
			pStmt.setString(3, "LSP (" + lsp.getLspAuthor() + ")");
			pStmt.setString(4, lsp.getLspName());
			pStmt.setString(5, lsp.getLspRelationship());
			pStmt.setDouble(6, new Double(0.0d));
			pStmt.executeUpdate();
			pStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static ODIE_Suggestion fetchSuggested(String newNegative,
			String newPositive, ODIE_LSP lsp) {
		ODIE_Suggestion result = new ODIE_Suggestion();
		try {
			PreparedStatement pStmt = conn
					.prepareStatement("select * from suggestion where NER_NEGATIVE = ? and NER_POSITIVE = ? and METHOD like 'LSP%' and RULE = ? and RELATION = ?");
			pStmt.setString(1, newNegative);
			pStmt.setString(2, newPositive);
			pStmt.setString(3, lsp.getLspName());
			pStmt.setString(4, lsp.getLspRelationship());
			ResultSet rs = pStmt.executeQuery();
			if (rs.next()) {
				result.setId(rs.getLong("id"));
				result.setNerNegative(rs.getString("NER_NEGATIVE"));
				result.setNerPositive(rs.getString("NER_POSITIVE"));
				result.setMethod(rs.getString("METHOD"));
				result.setRule(rs.getString("RULE"));
				result.setRelation(rs.getString("RELATION"));
				result.setScore(rs.getDouble("SCORE"));
			}
			rs.close();
			pStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void incrementSuggested(ODIE_Suggestion suggestion) {
		try {
			PreparedStatement pStmt = conn
					.prepareStatement("update suggestion set score = ? where id = ?");
			pStmt.setDouble(1, new Double(suggestion.getScore() + 1.0d));
			pStmt.setLong(2, suggestion.getId());
			pStmt.executeUpdate();
			pStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Connection getConn() {
		return conn;
	}

	public static void setConn(Connection conn) {
		ODIE_Suggestion.conn = conn;
	}

	public static String getDatabaseName() {
		return databaseName;
	}

	public static void setDatabaseName(String databaseName) {
		ODIE_Suggestion.databaseName = databaseName;
	}

	public String toString() {
		return super.toString();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNerNegative() {
		return nerNegative;
	}

	public void setNerNegative(String nerNegative) {
		this.nerNegative = nerNegative;
	}

	public String getNerPositive() {
		return nerPositive;
	}

	public void setNerPositive(String nerPositive) {
		this.nerPositive = nerPositive;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}
	
	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

}
