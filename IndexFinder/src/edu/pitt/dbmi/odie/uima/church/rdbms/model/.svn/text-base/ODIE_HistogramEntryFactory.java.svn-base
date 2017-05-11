package edu.pitt.dbmi.odie.uima.church.rdbms.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import edu.pitt.dbmi.odie.uima.church.model.ODIE_HistogramEntryFactoryInf;
import edu.pitt.dbmi.odie.uima.church.model.ODIE_HistogramEntryInf;

public class ODIE_HistogramEntryFactory implements
		ODIE_HistogramEntryFactoryInf {

	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ODIE_HistogramEntryFactory.class);

	private boolean isDatabaseCleaning = false;

	private Connection conn;
	private String tableNamePrefix;
	private String databaseName;
	private String qualifiedTableName;

	public void initialize(Connection connInput, String databaseNameParam,
			String tableNamePrefixParam) {
		try {
			this.conn = connInput;
			this.tableNamePrefix = tableNamePrefixParam;
			this.databaseName = databaseNameParam;
			this.qualifiedTableName = databaseName + "." + tableNamePrefix
					+ "_histogram";

			if (isDatabaseCleaning()) {
				dropTable(qualifiedTableName);
			}

			createTableIfNotExists(qualifiedTableName);

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private PreparedStatement getPreparedStatementUpdateHistogramEntry()
			throws SQLException {
		PreparedStatement pStmt = conn
				.prepareStatement("update "
						+ databaseName
						+ "."
						+ tableNamePrefix
						+ "_histogram set FREQ = ?, IS_NER = ?, ONTOLOGY_URI = ? where ID = ?");
		return pStmt;
	}

	private PreparedStatement getPreparedStatementCreateHistogramEntry()
			throws SQLException {
		PreparedStatement pStmt = null;
		pStmt = conn
				.prepareStatement("insert into "
						+ databaseName
						+ "."
						+ tableNamePrefix
						+ "_histogram (WORD, FREQ, IS_NER, ONTOLOGY_URI) values (?, ?, ?, ?)");
		return pStmt;
	}

	private PreparedStatement getPreparedStatementFetchHistogramEntry()
			throws SQLException {
		PreparedStatement pStmt = null;
		pStmt = conn
				.prepareStatement("select ID, WORD, FREQ, IS_NER, ONTOLOGY_URI from "
						+ tableNamePrefix + "_histogram where WORD = ?");
		return pStmt;
	}

	private void dropTable(String qualifiedTableName) throws SQLException {
		Statement sqlStmt = null;
		sqlStmt = conn.createStatement();
		sqlStmt.execute("drop table if exists " + qualifiedTableName);
		sqlStmt.close();
	}

	private void createTableIfNotExists(String qualifiedTableName)
			throws SQLException {
		Statement sqlStmt = null;
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE IF NOT EXISTS " + qualifiedTableName + "(\n");
		sb.append("  ID int NOT NULL AUTO_INCREMENT,\n");
		sb.append("  WORD varchar(255) NOT NULL DEFAULT 'UNDEFINED',\n");
		sb.append("  FREQ int NOT NULL DEFAULT 0,\n");
		sb.append("  IS_NER TINYINT(1) NOT NULL DEFAULT 0,\n");
		sb
				.append("  ONTOLOGY_URI varchar(255) NOT NULL DEFAULT 'UNDEFINED',\n");
		sb.append("  PRIMARY KEY (ID)\n");
		sb.append(") ENGINE=MyISAM");
		sqlStmt = conn.createStatement();
		sqlStmt.execute(sb.toString());
		sqlStmt.close();

		try {
			sb = new StringBuffer();
			sb.append("create index WORD_IDX on "
					+ qualifiedTableName + " (WORD)");
			sqlStmt = conn.createStatement();
			sqlStmt.execute(sb.toString());
			sqlStmt.close();
		} catch (Exception x) {
			logger.debug("Ignore exception for existing WORD_IDX.") ;
		}

	}

	public long getNumberOfEntries() {
		long result = 0L;
		try {
			PreparedStatement pStmt = null;
			pStmt = conn
					.prepareStatement("select sum(freq) number_of_entries from "
							+ tableNamePrefix + "_histogram");
			ResultSet rs = pStmt.executeQuery();
			if (rs.next()) {
				result = rs.getLong(1);
			}
			pStmt.close();
		} catch (Exception x) {
			x.printStackTrace();
		}
		return result;
	}

	public void updateHistogramEntry(ODIE_HistogramEntryInf entry) {
		try {
			PreparedStatement pStmt = getPreparedStatementUpdateHistogramEntry();
			pStmt.clearParameters();
			pStmt.setInt(1, entry.getFreq());
			pStmt.setInt(2, entry.getIsNer());
			pStmt.setString(3, entry.getOntologyUri());
			pStmt.setInt(4, entry.getId());
			pStmt.executeUpdate();
			pStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ODIE_HistogramEntry fetchHistogramEntry(String word) {
		ODIE_HistogramEntry result = null;
		result = fetchOrCreateHistogramEntry(word);
		return result;
	}

	private ODIE_HistogramEntry fetchOrCreateHistogramEntry(String word) {
		ODIE_HistogramEntry result = null ;
		try {
			if (word != null && word.replace("^\\s+", "").replace("\\s+$", "").length() != 0) {
				result = selectHistogramEntry(word) ;
				if (result == null) {
					PreparedStatement pStmt = getPreparedStatementCreateHistogramEntry();
					pStmt.clearParameters();
					pStmt.setString(1, word);
					pStmt.setInt(2, new Integer(0));
					pStmt.setInt(3, new Integer(0));
					pStmt.setString(4, "");
					pStmt.executeUpdate();
					pStmt.close();
					result = selectHistogramEntry(word);
				}
			}
		}
		catch (Exception x) {
			x.printStackTrace();
		}
		
		return result;
	}

	public ODIE_HistogramEntry selectHistogramEntry(String word) {
		ODIE_HistogramEntry entry = null;
		try {
			PreparedStatement pStmt = getPreparedStatementFetchHistogramEntry();
			pStmt.setString(1, word);
			ResultSet rs = pStmt.executeQuery();
			if (rs.next()) {
				entry = new ODIE_HistogramEntry();
				xFerResultSetToHistogramEntry(entry, rs);
			}
			rs.close();
			pStmt.close();
		} catch (Exception x) {
			x.printStackTrace() ;
		}
		return entry;
	}

	private ODIE_HistogramEntry xFerResultSetToHistogramEntry(
			ODIE_HistogramEntry entry, ResultSet rs) {
		try {
			Integer id = rs.getInt(1);
			String wordText = rs.getString(2);
			Integer freq = rs.getInt(3);
			Integer isNer = rs.getInt(4);
			String ontologyUri = rs.getString(5);
			entry.setId(id);
			entry.setWordText(wordText);
			entry.setFreq(freq);
			entry.setIsNer(isNer);
			entry.setOntologyUri(ontologyUri);
		} catch (Exception x) {
			x.printStackTrace();
		}
		return entry;
	}

	public String getTableNamePrefix() {
		return tableNamePrefix;
	}

	public void setTableNamePrefix(String tableNamePrefixInput) {
		tableNamePrefix = tableNamePrefixInput;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseNameInput) {
		databaseName = databaseNameInput;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection inputConn) {
		conn = inputConn;
	}

	public boolean isDatabaseCleaning() {
		return isDatabaseCleaning;
	}

	public void setDatabaseCleaning(boolean isDatabaseCleaning) {
		this.isDatabaseCleaning = isDatabaseCleaning;
	}

	public void close() {
	}

	public ODIE_HistogramEntry lookUpEntryById(Integer wordOneId) {
		ODIE_HistogramEntry entry = null;
		try {
			PreparedStatement pStmt = null;
			pStmt = conn
					.prepareStatement("select ID, WORD, FREQ, IS_NER, ONTOLOGY_URI from "
							+ tableNamePrefix + "_histogram where ID = ?");
			pStmt.setLong(1, wordOneId);
			ResultSet rs = pStmt.executeQuery();
			if (rs.next()) {
				entry = new ODIE_HistogramEntry();
				xFerResultSetToHistogramEntry(entry, rs);
			}
			pStmt.close();
		} catch (Exception x) {
			x.printStackTrace();
		}
		return entry;
	}

	public Integer lookUpWordFreqById(Integer wordOneId) {
		ODIE_HistogramEntry entry = lookUpEntryById(wordOneId);
		return entry.getFreq();
	}

	public void insertBatch(TreeSet<ODIE_HistogramEntryInf> sortedEntries) {
		try {
			for (ODIE_HistogramEntryInf entry : sortedEntries) {
				PreparedStatement pStmt = getPreparedStatementCreateHistogramEntry();
				pStmt.clearParameters();
				pStmt.setString(1, entry.getWordText());
				pStmt.setInt(2, entry.getFreq());
				pStmt.setInt(3, entry.getIsNer());
				pStmt.setString(4, entry.getOntologyUri());
				pStmt.executeUpdate();
				pStmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void initializeQualifiedTableName(Connection connInput, String databaseNameParam,
			String tableNamePrefixParam) {
		this.conn = connInput ;
		this.tableNamePrefix = tableNamePrefixParam ;
		this.databaseName = databaseNameParam ;
		this.qualifiedTableName = databaseName + "." + tableNamePrefix
				+ "_histogram" ;

	}
	
	public String getQualifiedTableName() {
		return qualifiedTableName;
	}

	public void setQualifiedTableName(String qualifiedTableName) {
		this.qualifiedTableName = qualifiedTableName;
	}


}
