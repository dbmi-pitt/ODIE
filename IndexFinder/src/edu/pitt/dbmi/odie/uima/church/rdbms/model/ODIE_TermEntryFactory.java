package edu.pitt.dbmi.odie.uima.church.rdbms.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.pitt.dbmi.odie.uima.church.model.ODIE_TermEntryFactoryInf;
import edu.pitt.dbmi.odie.uima.church.model.ODIE_TermEntryInf;

public class ODIE_TermEntryFactory implements ODIE_TermEntryFactoryInf {

	private Connection conn;
	private String tableNamePrefix;
	private String databaseName;
	private String qualifiedTableName;

	private boolean isDatabaseCleaning = false;

	public void initialize(Connection connInput, String databaseNameParam,
			String tableNamePrefixParam) {
		try {
			conn = connInput;
			tableNamePrefix = tableNamePrefixParam;
			databaseName = databaseNameParam;
			qualifiedTableName = databaseName + "." + tableNamePrefix
					+ "_term";

			if (isDatabaseCleaning()) {
				dropTable(qualifiedTableName);
			}
			createTableIfNotExists(qualifiedTableName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void createTableIfNotExists(String qualifiedTableName)
			throws SQLException {
		Statement sqlStmt = null;
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE IF NOT EXISTS " + qualifiedTableName + "(\n");
		sb.append("  ID int NOT NULL AUTO_INCREMENT,\n");
		sb.append("  HISTOGRAM_ID int NOT NULL,\n");
		sb.append("  TERM varchar(255) NOT NULL DEFAULT 'UNDEFINED',\n");
		sb.append("  PRIMARY KEY (ID)\n");
		sb.append(") ENGINE=MyISAM");
		sqlStmt = conn.createStatement();
		sqlStmt.execute(sb.toString());
		sqlStmt.close();

		try {
			sb = new StringBuffer();
			sb.append("create index HISTOGRAM_IDX on " + qualifiedTableName
					+ " (HISTOGRAM_ID, TERM)");
			sqlStmt = conn.createStatement();
			sqlStmt.execute(sb.toString());
			sqlStmt.close();
		} catch (Exception x) {
			;
		}
	}

	private void dropTable(String qualifiedTableName) throws SQLException {
		Statement sqlStmt = null;
		sqlStmt = conn.createStatement();
		sqlStmt.execute("DROP TABLE IF EXISTS " + qualifiedTableName);
		sqlStmt.close();
	}
	
	public ODIE_TermEntryInf fetchOrCreateTermEntry(Integer histogramId, String term) {
		ODIE_TermEntryInf result = readTermEntry(histogramId, term) ;
		if (result == null) {
			createTermEntry(histogramId, term) ;
			result = readTermEntry(histogramId, term) ;
		}
		return result ;
	}
	
	public ODIE_TermEntryInf createTermEntry(Integer histogramId, String term) {
		ODIE_TermEntryInf result = null ;
		try {
			StringBuffer sb = new StringBuffer() ;
			sb.append("insert into ") ;
			sb.append(qualifiedTableName) ;
			sb.append(" (") ;
			sb.append("histogram_id, ") ;
			sb.append("term ") ;
			sb.append(" )") ;
			sb.append(" values (?,?) ") ;
			String sql = sb.toString();
			PreparedStatement pStmt =  conn.prepareStatement(sql);
			pStmt.setInt(1, histogramId) ;
			pStmt.setString(2, term) ;
			pStmt.executeUpdate() ;
			pStmt.close();
		}
		catch (Exception x) {
			x.printStackTrace();
		}
		return result;
	}
	
	public ODIE_TermEntryInf readTermEntry(Integer histogramId, String term) {
		ODIE_TermEntryInf result = null ;
		try {
			StringBuffer sb = new StringBuffer() ;
			sb.append("select ") ;
			sb.append("id,	") ;
			sb.append("histogram_id, ") ;
			sb.append("term ") ;
			sb.append(" from ") ;
			sb.append(qualifiedTableName) ;
			sb.append(" where ") ;
			sb.append(" term = ? ") ;
			String sql = sb.toString();
			PreparedStatement pStmt =  conn.prepareStatement(sql);
			pStmt.setString(1, term) ;
			ResultSet rs = pStmt.executeQuery();
			if (rs.next()) {
				result = xFerResultSetToTermEntry(new ODIE_TermEntry(), rs) ;
			}	
			pStmt.close() ;
		}
		catch (Exception x) {
			x.printStackTrace();
		}
		return result;
	}

	public void updateTermEntry(ODIE_TermEntryInf entry) {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("update ");
			sb.append(databaseName + "." + tableNamePrefix + "_term ");
			sb.append("set  ");
			sb.append(" histogram_id = ?, ");
			sb.append(" term = ? ");
			sb.append("where ");
			sb.append("  id = ? ");
			String sql = sb.toString();
			PreparedStatement pStmt = conn
					.prepareStatement(sql);
			pStmt.clearParameters();
			pStmt.setInt(1, entry.getHistogramId());
			pStmt.setString(2, entry.getTerm());
			pStmt.setInt(3, entry.getId());
			pStmt.executeUpdate();
			pStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteTermEntry(ODIE_TermEntryInf entry) {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("delete from ");
			sb.append(qualifiedTableName);
			sb.append(" where ");
			sb.append("  id = ? ");
			String sql = sb.toString();
			PreparedStatement pStmt = conn
					.prepareStatement(sql);
			pStmt.clearParameters();
			pStmt.setInt(1, entry.getId());
			pStmt.executeUpdate();
			pStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private ODIE_TermEntryInf xFerResultSetToTermEntry(ODIE_TermEntryInf entry,
			ResultSet rs) {
		try {
			Integer id = rs.getInt(1);
			Integer histogramId = rs.getInt(2);
			String term = rs.getString(3);
			entry.setId(id);
			entry.setHistogramId(histogramId) ;
			entry.setTerm(term);
		} catch (Exception x) {
			x.printStackTrace();
		}
		return entry;
	}

	public boolean isDatabaseCleaning() {
		return isDatabaseCleaning;
	}

	public void setDatabaseCleaning(boolean isDatabaseCleaning) {
		this.isDatabaseCleaning = isDatabaseCleaning;
	}

	public ODIE_TermEntryInf smallestTermOfHistogramId(Integer histogramId) {
		ODIE_TermEntryInf result = null ;
		try {
			StringBuffer sb = new StringBuffer() ;
			sb.append("select ") ;
			sb.append("id,	") ;
			sb.append("histogram_id, ") ;
			sb.append("term ") ;
			sb.append(" from ") ;
			sb.append(qualifiedTableName) ;
			sb.append(" where ") ;
			sb.append(" histogram_id = ? ") ;
			sb.append(" order by length(term) asc ") ;
			String sql = sb.toString();
			PreparedStatement pStmt =  conn.prepareStatement(sql);
			pStmt.setInt(1, histogramId) ;
			ResultSet rs = pStmt.executeQuery();
			if (rs.next()) {
				result = xFerResultSetToTermEntry(new ODIE_TermEntry(), rs) ;
			}	
			pStmt.close();
		}
		catch (Exception x) {
			x.printStackTrace();
		}
		return result;
	}

}
