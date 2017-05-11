package edu.pitt.dbmi.odie.uima.dekanlin.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;

public class ODIE_HistogramEntryFactory {

	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ODIE_HistogramEntryFactory.class);

	private final HashMap<String, ODIE_HistogramEntry> cachedHistogramEntries = new HashMap<String, ODIE_HistogramEntry>();

	public final HashMap<Integer, ODIE_HistogramEntry> cachedByIdHistogramEntries = new HashMap<Integer, ODIE_HistogramEntry>();

	private final TreeSet<ODIE_HistogramEntry> sortedEntries = new TreeSet<ODIE_HistogramEntry>(
			new Comparator<ODIE_HistogramEntry>() {
				public int compare(ODIE_HistogramEntry o1,
						ODIE_HistogramEntry o2) {
					return o1.getId() - o2.getId();
				}
			});

	private final TreeSet<ODIE_HistogramEntry> sortedByWordHistogramEntries = new TreeSet<ODIE_HistogramEntry>(
			new Comparator<ODIE_HistogramEntry>() {
				public int compare(ODIE_HistogramEntry o1,
						ODIE_HistogramEntry o2) {
					return o1.wordText.compareTo(o2.wordText);
				}
			});

	private int recordIndex = 1;

	private Connection conn;
	private String tableNamePrefix;
	private String databaseName;
	private String qualifiedTableName;

	private boolean isDatabaseCaching = true;

//	private PreparedStatement preparedStatementCreateHistogramEntry = null;
//	private PreparedStatement preparedStatementUpdateHistogramEntry = null;

	public void initialize(Connection connInput, String databaseNameParam,
			String tableNamePrefixParam) {
		conn = connInput;
		tableNamePrefix = tableNamePrefixParam;
		databaseName = databaseNameParam;
		qualifiedTableName = databaseName + "." + tableNamePrefix
				+ "_histogram";

	}

	public void dropHistogramEntry() {
		// drop the table in case it's already present
		// This isn't needed because we're starting from an empty
		// database,
		// but leave here for tutorial reasons
		try {
			Statement sqlStmt = null;
			sqlStmt = conn.createStatement();
			sqlStmt.execute("drop table if exists " + qualifiedTableName);
			sqlStmt.close();
		} catch (SQLException x) {

		}
	}

	public void createHistogramEntry() {
		try {
			Statement sqlStmt = null;
			StringBuffer sb = new StringBuffer();
			sb.append("CREATE TABLE IF NOT EXISTS " + qualifiedTableName
					+ " (\n");
			sb.append("  ID int NOT NULL AUTO_INCREMENT,\n");
			sb.append("  WORD varchar(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb.append("  FREQ int NOT NULL DEFAULT 0,\n");
			sb.append("  POS varchar(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb.append("  TAGGER varchar(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb.append("  IS_NER TINYINT(1) NOT NULL DEFAULT 0,\n");
			sb.append("  IS_NP TINYINT(1) NOT NULL DEFAULT 0,\n");
			sb.append("  ONTOLOGY_URI varchar(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb.append("  PRIMARY KEY (ID)\n");
			sb.append(") ENGINE=InnoDB");
			logger.debug(sb.toString());
			sqlStmt = conn.createStatement();
			sqlStmt.execute(sb.toString());
			sqlStmt.close();

			createIndex();

		} catch (SQLException x) {
			x.printStackTrace();
		}
	}

	private void createIndex() {
		try {
			Statement sqlStmt = null;
			StringBuffer sb = new StringBuffer();
			sb.append("create index WORD_IDX on " + qualifiedTableName
					+ " (WORD)");
			sqlStmt = conn.createStatement();
			sqlStmt.execute(sb.toString());
			sqlStmt.close();
		} catch (SQLException e) {
			;
		}
	}

	public void prepareDMLStatements() {
	}

	public void clearHistogramEntries() {
		cachedHistogramEntries.clear();
	}

	public void updateHistogramEntries() {
		logger.debug("Updating cached Histogram information with "
				+ cachedHistogramEntries.size() + " entries");
		for (String key : cachedHistogramEntries.keySet()) {
			ODIE_HistogramEntry entry = cachedHistogramEntries.get(key);
			try {
				PreparedStatement preparedStatementUpdateHistogramEntry = conn
				.prepareStatement("update "
						+ qualifiedTableName
						+ " set FREQ = ?, IS_NER = ?, IS_NP = ?, ONTOLOGY_URI = ? where id = ?");

				preparedStatementUpdateHistogramEntry.clearParameters();
				preparedStatementUpdateHistogramEntry
						.setInt(1, entry.getFreq());
				preparedStatementUpdateHistogramEntry.setInt(2,
						entry.getIsNer());
				preparedStatementUpdateHistogramEntry
						.setInt(3, entry.getIsNp());
				preparedStatementUpdateHistogramEntry.setString(4,
						entry.getOntologyUri());
				preparedStatementUpdateHistogramEntry.setInt(5, entry.getId());

				preparedStatementUpdateHistogramEntry.executeUpdate();
				preparedStatementUpdateHistogramEntry.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void updateHistogramEntry(ODIE_HistogramEntry entry) {
		try {
			PreparedStatement preparedStatementUpdateHistogramEntry = conn
					.prepareStatement("update "
							+ qualifiedTableName
							+ " set FREQ = ?, IS_NER = ?, ONTOLOGY_URI = ? where id = ?");
			preparedStatementUpdateHistogramEntry.clearParameters();
			preparedStatementUpdateHistogramEntry.setInt(1, entry.getFreq());
			preparedStatementUpdateHistogramEntry.setInt(2, entry.getIsNer());
			preparedStatementUpdateHistogramEntry.setString(3,
					entry.getOntologyUri());
			preparedStatementUpdateHistogramEntry.setInt(4, entry.getId());
			preparedStatementUpdateHistogramEntry.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ODIE_HistogramEntry fetchHistogramEntry(String word, String pos,
			String tagger, String ontologyUri) {
		ODIE_HistogramEntry result = cachedHistogramEntries.get(word);
		try {
			if (result == null) {
				result = fetchOrCreateHistogramEntry(word, pos, tagger,
						ontologyUri);
				cachedHistogramEntries.put(word, result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	// preparedStatementCreateHistogramEntry = conn
	// .prepareStatement("insert into "
	// + databaseName
	// + "."
	// + tableNamePrefix
	// +
	// "_histogram (WORD, FREQ, POS, TAGGER, IS_NER, IS_NP) values (?, ?, ?, ?, ?, ?)");

	private ODIE_HistogramEntry fetchOrCreateHistogramEntry(String word,
			String pos, String tagger, String ontologyUri) throws SQLException {
		ODIE_HistogramEntry result = null;
		if (isDatabaseCaching) {

			result = selectHistogramEntry(word);
			if (result == null) {
				PreparedStatement preparedStatementCreateHistogramEntry = conn
				.prepareStatement("insert into "
						+ qualifiedTableName
						+ " (WORD, FREQ, POS, TAGGER, IS_NER, IS_NP, ONTOLOGY_URI) values (?, ?, ?, ?, ?, ?, ?)");
				preparedStatementCreateHistogramEntry.clearParameters();
				preparedStatementCreateHistogramEntry.setString(1, word.substring(0, Math.min(word.length(), 254)));
				preparedStatementCreateHistogramEntry.setInt(2, 0);
				preparedStatementCreateHistogramEntry.setString(3, pos);
				preparedStatementCreateHistogramEntry.setString(4, tagger);
				preparedStatementCreateHistogramEntry.setInt(5, 0);
				preparedStatementCreateHistogramEntry.setInt(6, 0);
				preparedStatementCreateHistogramEntry.setString(7, ontologyUri);
				preparedStatementCreateHistogramEntry.executeUpdate();
				preparedStatementCreateHistogramEntry.close();
				result = selectHistogramEntry(word);
			}

		} else {
			result = new ODIE_HistogramEntry();
			result.setId(new Integer(recordIndex++));
			result.setWordText(word);
			result.setFreq(new Integer(0));
			result.setIsNer(new Integer(0));
			result.setIsNp(new Integer(0));
		}

		return result;
	}

	private ODIE_HistogramEntry selectHistogramEntry(String word)
			throws SQLException {
		ODIE_HistogramEntry result = null;
		PreparedStatement preparedStatementFetchHistogramEntry = conn
		.prepareStatement("select id, word, freq, pos, tagger, is_ner, is_np, ontology_uri from "
				+ qualifiedTableName + " where word = ?");
		preparedStatementFetchHistogramEntry.clearParameters();
		preparedStatementFetchHistogramEntry.setString(1, word);
		ResultSet rs = preparedStatementFetchHistogramEntry.executeQuery();
		if (rs.next()) {
			Integer id = rs.getInt(1);
			String wordText = rs.getString(2);
			Integer freq = rs.getInt(3);
			String pos = rs.getString(4);
			String tagger = rs.getString(5);
			Integer isNer = rs.getInt(6);
			Integer isNp = rs.getInt(7);
			String ontologyUri = rs.getString(8);
			result = new ODIE_HistogramEntry(id, wordText, freq, pos, tagger,
					isNer, isNp, ontologyUri);
		}
		rs.close();
		preparedStatementFetchHistogramEntry.close();
		return result;
	}

	public void insertBatch() {
		try {
			sortedEntries.addAll(cachedHistogramEntries.values());
			String qualifiedTableName = databaseName + "." + tableNamePrefix
					+ "_histogram";
			String sql = "insert into "
					+ qualifiedTableName
					+ " (WORD, FREQ, POS, TAGGER, IS_NER, IS_NP) values (?, ?, ?, ?, ?, ?)";
			int idx = 0;
			for (ODIE_HistogramEntry entry : sortedEntries) {
				PreparedStatement preparedStatementCreateHistogramEntry = conn
						.prepareStatement(sql);
				String word = entry.getWordText() ;
				word = (word != null) ? word.substring(0, Math.min(word.length(), 254)) : "UNDEFINED" ;
				preparedStatementCreateHistogramEntry.setString(1,
						word);
				preparedStatementCreateHistogramEntry
						.setInt(2, entry.getFreq());
				preparedStatementCreateHistogramEntry.setString(3,
						entry.getPos());
				preparedStatementCreateHistogramEntry.setString(4,
						entry.getTagger());
				preparedStatementCreateHistogramEntry.setInt(5,
						entry.getIsNer());
				preparedStatementCreateHistogramEntry
						.setInt(6, entry.getIsNp());
				preparedStatementCreateHistogramEntry.executeUpdate();
				preparedStatementCreateHistogramEntry.close();
				if (idx % 1000 == 0) {
					logger.debug("ODIE_Histogram: saved " + idx + " of "
							+ sortedEntries.size() + " records");
				}
				idx++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
	}

	public ODIE_HistogramEntry lookUpEntryById(Long id) {
		cacheEntriesByIndex();
		return cachedByIdHistogramEntries.get(id.intValue());
	}

	public long lookUpWordFreqById(Long id) {
		cacheEntriesByIndex();
		ODIE_HistogramEntry entry = cachedByIdHistogramEntries.get(id
				.intValue());
		return entry.getFreq();
	}

	public void cacheEntriesByIndex() {
		if (cachedHistogramEntries.size() > 0
				&& cachedByIdHistogramEntries.size() == 0) {
			for (ODIE_HistogramEntry entry : cachedHistogramEntries.values()) {
				cachedByIdHistogramEntries.put(entry.getId(), entry);
			}
		}
	}

	public void cacheHistogramFromDatabaseById() {
		try {
			this.cachedByIdHistogramEntries.clear();
			String sql = "select ID, WORD, FREQ, POS, TAGGER, IS_NER, IS_NP, ONTOLOGY_URI  from "
					+ qualifiedTableName;
			PreparedStatement selectHistogramPreparedStatement = conn
					.prepareStatement(sql);
			ResultSet rs = selectHistogramPreparedStatement.executeQuery();
			while (rs.next()) {
				ODIE_HistogramEntry h = new ODIE_HistogramEntry();
				h.setId(rs.getInt("ID"));
				h.setWordText(rs.getString("WORD"));
				h.setFreq(rs.getInt("FREQ"));
				h.setPos(rs.getString("POS"));
				h.setTagger(rs.getString("TAGGER"));
				h.setIsNer(rs.getInt("IS_NER"));
				h.setIsNp(rs.getInt("IS_NP"));
				h.setOntologyUri(rs.getString("ONTOLOGY_URI"));
				this.cachedByIdHistogramEntries.put(h.id, h);
			}
			selectHistogramPreparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//
	// Calls used when no POS information exists
	//
	public ODIE_HistogramEntry fetchHistogramEntry(String word) {
		ODIE_HistogramEntry result = null;
		word = word.substring(0, Math.min(word.length(), 254)) ; // Truncate large value words
		result = fetchOrCreateHistogramEntry(word);
		return result;
	}

	private ODIE_HistogramEntry fetchOrCreateHistogramEntry(String word) {
		ODIE_HistogramEntry result = null;
		try {
			if (word != null
					&& word.replace("^\\s+", "").replace("\\s+$", "").length() != 0) {
				result = selectHistogramEntry(word);
				if (result == null) {
					PreparedStatement pStmt = conn
							.prepareStatement("insert into "
									+ qualifiedTableName
									+ " (WORD, FREQ, POS, TAGGER, IS_NER, IS_NP, ONTOLOGY_URI) values (?, ?, ?, ?, ?, ?, ?)");
					pStmt.clearParameters();
					pStmt.setString(1, word);
					pStmt.setInt(2, new Integer(0));
					pStmt.setString(3, "UNUSED"); // PartOfSpeech slot unused
					pStmt.setString(4, "UNUSED"); // Tagger algorithm unused
					pStmt.setInt(5, new Integer(0));
					pStmt.setInt(6, new Integer(0));
					pStmt.setString(7, "UNSET"); // Yet to be set
					pStmt.executeUpdate();
					pStmt.close();
					result = selectHistogramEntry(word);
				}
			}
		} catch (Exception x) {
			x.printStackTrace();
		}

		return result;
	}

	public String getQualifiedTableName() {
		return qualifiedTableName;
	}

	public void setQualifiedTableName(String qualifiedTableName) {
		this.qualifiedTableName = qualifiedTableName;
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

	public boolean isDatabaseCaching() {
		return isDatabaseCaching;
	}

	public void setDatabaseCaching(boolean isDatabaseCaching) {
		this.isDatabaseCaching = isDatabaseCaching;
	}
}