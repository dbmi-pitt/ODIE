package edu.pitt.dbmi.odie.uima.church.rdbms.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import edu.pitt.dbmi.odie.uima.church.model.ODIE_AssociationEntryFactoryInf;
import edu.pitt.dbmi.odie.uima.church.model.ODIE_AssociationEntryInf;
import edu.pitt.dbmi.odie.uima.church.model.ODIE_HistogramEntryFactoryInf;
import edu.pitt.dbmi.odie.uima.church.model.ODIE_HistogramEntryInf;

public class ODIE_AssociationEntryFactory implements
		ODIE_AssociationEntryFactoryInf {

	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ODIE_AssociationEntryFactory.class);

	private final String CONST_KEY_SEPERATOR = ":";

	// private final HashMap<String, ODIE_AssociationEntryInf>
	// cachedAssociationEntries = new HashMap<String,
	// ODIE_AssociationEntryInf>();

	private final TreeSet<ODIE_AssociationEntryInf> sortedEntries = new TreeSet<ODIE_AssociationEntryInf>(
			new Comparator<ODIE_AssociationEntryInf>() {
				public int compare(ODIE_AssociationEntryInf o1,
						ODIE_AssociationEntryInf o2) {
					return o1.getId() - o2.getId();
				}
			});

	private final TreeSet<ODIE_AssociationEntryInf> scoredEntries = new TreeSet<ODIE_AssociationEntryInf>(
			new Comparator<ODIE_AssociationEntryInf>() {
				public int compare(ODIE_AssociationEntryInf o1,
						ODIE_AssociationEntryInf o2) {
					Double ixyOne = o1.getIxy();
					Double ixyTwo = o2.getIxy();
					if (ixyOne < ixyTwo) {
						return 1;
					} else if (ixyOne > ixyTwo) {
						return -1;
					} else {
						return 1;
					}
				}
			});

	private ODIE_AssociationEntryInf focusEntry = null;

	private boolean isDatabaseCleaning = false;

	private Connection conn;

	private String tableNamePrefix;
	private String databaseName;
	private String qualifiedTableName;

	private ODIE_HistogramEntryFactory histogramEntryFactory = null;

	public ODIE_AssociationEntryFactory() {
		;
	}

	public void iterate() {
		this.focusEntry = null;
	}

	public ODIE_AssociationEntryInf next() {
		ODIE_AssociationEntryInf association = null;

		try {
			if (this.focusEntry == null) {
				association = this.focusEntry = first();
			} else {
				PreparedStatement pStmt = conn
						.prepareStatement("select * from "
								+ tableNamePrefix
								+ "_association where id > ? order by id limit 1");
				pStmt.setLong(1, this.focusEntry.getId());
				ResultSet rs = pStmt.executeQuery();
				if (rs.next()) {
					association = this.focusEntry = new ODIE_AssociationEntry();
					xFerResultSetToAssociationEntry(association, rs);
				} else {
					logger.debug("No more associations to iterator over.");
				}
				rs.close();
				pStmt.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (association != null) {
			cacheAssociationEntry(association);
		}

		return association;
	}

	private ODIE_AssociationEntryInf first() {

		ODIE_AssociationEntryInf result = null;

		try {
			PreparedStatement pStmt = conn.prepareStatement("select * from "
					+ tableNamePrefix + "_association order by id limit 1");
			ResultSet rs = pStmt.executeQuery();
			if (rs.next()) {
				result = new ODIE_AssociationEntry();
				xFerResultSetToAssociationEntry(result, rs);
			}
			rs.close();
			pStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}
	
	
	public void initializeQualifiedTableName(Connection connInput, String databaseNameParam,
			String tableNamePrefixParam) {
		conn = connInput ;
		tableNamePrefix = tableNamePrefixParam ;
		databaseName = databaseNameParam ;
		qualifiedTableName = databaseName + "." + tableNamePrefix
				+ "_association" ;

	}

	public void initialize(Connection connInput, String databaseNameParam,
			String tableNamePrefixParam) {
		try {
			conn = connInput;
			tableNamePrefix = tableNamePrefixParam;
			databaseName = databaseNameParam;
			qualifiedTableName = databaseName + "." + tableNamePrefix
					+ "_association";

			if (isDatabaseCleaning()) {
				dropTable();
			}

			createTable(qualifiedTableName);

//			preparedStatementFetchAssociationEntry = getPreparedStatementFetchAssociationEntry();
//			preparedStatementUpdateAssociationEntry = getPreparedStatementUpdateAssociationEntry();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private PreparedStatement getPreparedStatementUpdateAssociationEntry()
			throws SQLException {
		StringBuffer sb = new StringBuffer();
		sb.append("update " + qualifiedTableName);
		sb.append(" set ");
		sb.append("word_one_id = ?,");
		sb.append("word_two_id = ?,");
		sb.append("word_one_freq = ?,");
		sb.append("word_two_freq = ?,");
		sb.append("is_ner_one = ?,");
		sb.append("is_ner_two = ?,");
		sb.append("freq = ?,");
		sb.append("i_x_y = ?");
		sb.append("where id = ?");
		PreparedStatement preparedStatementUpdateAssociationEntry = conn.prepareStatement(sb
				.toString());
		return preparedStatementUpdateAssociationEntry;
	}

	private PreparedStatement getPreparedStatementCreateAssociationEntry()
			throws SQLException {
		StringBuffer sb = new StringBuffer();
		sb.append("insert into ");
		sb.append(qualifiedTableName);
		sb.append(" ( ");
		sb.append("word_one_id,");
		sb.append("word_two_id,");
		sb.append("word_one_freq,");
		sb.append("word_two_freq,");
		sb.append("is_ner_one,");
		sb.append("is_ner_two,");
		sb.append("freq,");
		sb.append("i_x_y");
		sb.append(" ) ");
		sb.append(" values (?,?,?,?,?,?,?,?)");
		PreparedStatement preparedStatementCreateAssociationEntry = conn.prepareStatement(sb
				.toString());
		return preparedStatementCreateAssociationEntry;
	}

	private void dropTable() throws SQLException {
		// drop the table in case it's already present
		Statement sqlStmt = null;
		sqlStmt = conn.createStatement();
		sqlStmt.execute("drop table if exists " + databaseName + "."
				+ tableNamePrefix + "_association");
		sqlStmt.close();
	}

	private void createTable(String qualifiedTableName) throws SQLException {
		Statement sqlStmt = null;
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE IF NOT EXISTS " + qualifiedTableName + " (\n");
		sb.append("  ID int NOT NULL AUTO_INCREMENT,\n");
		sb.append("  WORD_ONE_ID int NOT NULL DEFAULT -1,\n");
		sb.append("  WORD_TWO_ID int NOT NULL DEFAULT -1,\n");
		sb.append("  WORD_ONE_FREQ decimal(10,2) NOT NULL DEFAULT 0,\n");
		sb.append("  WORD_TWO_FREQ decimal(10,2) NOT NULL DEFAULT 0,\n");
		sb.append("  IS_NER_ONE tinyint(1) NOT NULL DEFAULT 0,\n");
		sb.append("  IS_NER_TWO tinyint(1) NOT NULL DEFAULT 0,\n");
		sb.append("  FREQ decimal(10,2) NOT NULL DEFAULT 0,\n");
		sb.append("  I_X_Y decimal(10,2) NOT NULL DEFAULT 0.00,\n");
		sb.append("  PRIMARY KEY (ID)\n");
		sb.append(") ENGINE=MyISAM");
		sqlStmt = conn.createStatement();
		sqlStmt.execute(sb.toString());
		sqlStmt.close();

		try {
			sb = new StringBuffer();
			sb.append("create index WORD_IDX on " + qualifiedTableName
					+ " (WORD_ONE_ID, WORD_TWO_ID)");
			sqlStmt = conn.createStatement();
			sqlStmt.execute(sb.toString());
			sqlStmt.close();
		} catch (Exception x) {
			; // may already exist
		}

	}

	public void clearAssociationEntries() {
		;
	}

	public void updateAssociationEntries() {
		;
	}

	public void updateAssociationEntry(ODIE_AssociationEntryInf entry) {
		try {
			PreparedStatement preparedStatementUpdateAssociationEntry = this.getPreparedStatementUpdateAssociationEntry() ;
			preparedStatementUpdateAssociationEntry.clearParameters();
			preparedStatementUpdateAssociationEntry.setInt(1, entry
					.getWordOneId());
			preparedStatementUpdateAssociationEntry.setInt(2, entry
					.getWordTwoId());
			preparedStatementUpdateAssociationEntry.setDouble(3, entry
					.getWordOneFreq());
			preparedStatementUpdateAssociationEntry.setDouble(4, entry
					.getWordTwoFreq());
			preparedStatementUpdateAssociationEntry.setInt(5, entry
					.getIsNerOne());
			preparedStatementUpdateAssociationEntry.setInt(6, entry
					.getIsNerTwo());
			preparedStatementUpdateAssociationEntry.setDouble(7, entry
					.getFreq());
			preparedStatementUpdateAssociationEntry
					.setDouble(8, entry.getIxy());
			preparedStatementUpdateAssociationEntry.setInt(9, entry.getId());
			preparedStatementUpdateAssociationEntry.executeUpdate();
			preparedStatementUpdateAssociationEntry.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateAndCacheAssociationEntry(ODIE_AssociationEntry entry) {
	}

	public ODIE_AssociationEntryInf fetchAssociationEntry(
			ODIE_HistogramEntryInf wordOneEntry,
			ODIE_HistogramEntryInf wordTwoEntry) {

		ODIE_AssociationEntryInf result = null;

		// Swap so lowest id is wordOneEntry
		if (wordOneEntry.getId() > wordTwoEntry.getId()) {
			ODIE_HistogramEntryInf temp = wordTwoEntry;
			wordTwoEntry = wordOneEntry;
			wordOneEntry = temp;
		}
//		String associationKey = buildKey(wordOneEntry.getId(), wordTwoEntry
//				.getId());
		// ODIE_AssociationEntryInf result = cachedAssociationEntries
		// .get(associationKey);
		try {
			if (result == null) {
				result = fetchOrCreateAssociationEntry(wordOneEntry,
						wordTwoEntry);
				// cachedAssociationEntries.put(associationKey, result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	private void cacheAssociationEntry(ODIE_AssociationEntryInf association) {
//		String associationKey = buildKey(association.getWordOneId(),
//				association.getWordOneId());
		// this.cachedAssociationEntries.put(associationKey, association) ;
	}

	@SuppressWarnings("unused")
	private String buildKey(Integer wordOneId, Integer wordTwoId) {
		return wordOneId + CONST_KEY_SEPERATOR + wordTwoId;
	}

	private ODIE_AssociationEntryInf fetchOrCreateAssociationEntry(
			ODIE_HistogramEntryInf wordOneEntry,
			ODIE_HistogramEntryInf wordTwoEntry) throws SQLException {
		ODIE_AssociationEntryInf result = null;
		result = selectAssociationEntry(wordOneEntry.getId(), wordTwoEntry
				.getId());
		if (result == null) {
			PreparedStatement preparedStatementCreateAssociationEntry = getPreparedStatementCreateAssociationEntry() ;
			preparedStatementCreateAssociationEntry.clearParameters();
			preparedStatementCreateAssociationEntry.setInt(1, wordOneEntry
					.getId());
			preparedStatementCreateAssociationEntry.setInt(2, wordTwoEntry
					.getId());
			preparedStatementCreateAssociationEntry.setDouble(3, wordOneEntry
					.getFreq());
			preparedStatementCreateAssociationEntry.setDouble(4, wordTwoEntry
					.getFreq());
			preparedStatementCreateAssociationEntry.setInt(5, wordOneEntry
					.getIsNer());
			preparedStatementCreateAssociationEntry.setInt(6, wordTwoEntry
					.getIsNer());
			preparedStatementCreateAssociationEntry.setDouble(7, new Double(
					0.0d));
			preparedStatementCreateAssociationEntry.setDouble(8, new Double(
					0.0d));
			preparedStatementCreateAssociationEntry.executeUpdate();
			preparedStatementCreateAssociationEntry.close();
			result = selectAssociationEntry(wordOneEntry.getId(), wordTwoEntry
					.getId());
			
		}

		return result;
	}

	private ODIE_AssociationEntryInf xFerResultSetToAssociationEntry(
			ODIE_AssociationEntryInf entry, ResultSet rs) {
		try {
			entry.setId(rs.getInt("ID"));
			entry.setWordOneId(rs.getInt("WORD_ONE_ID"));
			entry.setWordTwoId(rs.getInt("WORD_TWO_ID"));
			entry.setWordOneFreq(rs.getDouble("WORD_ONE_FREQ"));
			entry.setWordTwoFreq(rs.getDouble("WORD_TWO_FREQ"));
			entry.setIsNerOne(rs.getInt("IS_NER_ONE"));
			entry.setIsNerTwo(rs.getInt("IS_NER_TWO"));
			entry.setFreq(rs.getDouble("FREQ"));
			entry.setIxy(rs.getDouble("I_X_Y"));
		} catch (Exception x) {
			x.printStackTrace();
		}
		return entry;
	}

	public ODIE_AssociationEntryInf selectAssociationEntry(Integer wordOneId,
			Integer wordTwoId) {
		ODIE_AssociationEntry result = null;
		try {
			PreparedStatement preparedStatementFetchAssociationEntry = conn
			.prepareStatement("select * from "
					+ tableNamePrefix
					+ "_association where word_one_id = ? and word_two_id = ?");
			preparedStatementFetchAssociationEntry.clearParameters();
			preparedStatementFetchAssociationEntry.setInt(1, wordOneId);
			preparedStatementFetchAssociationEntry.setInt(2, wordTwoId);
			ResultSet rs = preparedStatementFetchAssociationEntry
					.executeQuery();
			if (rs.next()) {
				result = new ODIE_AssociationEntry();
				xFerResultSetToAssociationEntry(result, rs);
			}
			rs.close();
			preparedStatementFetchAssociationEntry.close();
		} catch (Exception x) {
			x.printStackTrace();
		}

		return result;
	}

	public Collection<ODIE_AssociationEntryInf> scoreAssociationEntries(
			Long numberOfEntriesUpperBound, Long wordFrequencyUpperBound) {
		final ArrayList<ODIE_AssociationEntryInf> result = new ArrayList<ODIE_AssociationEntryInf>();
		int idx = 0;
		for (ODIE_AssociationEntryInf entry : scoredEntries) {
			if (numberOfEntriesUpperBound > 0
					&& idx > numberOfEntriesUpperBound) {
				break;
			}
			result.add(entry);
			idx++;
		}
		return result;
	}

	public void insertBatch() {
	}

	public void insertBatch(
			Collection<ODIE_AssociationEntryInf> associationEntries) {
		try {
			int idx = 0;
			for (ODIE_AssociationEntryInf entry : associationEntries) {
				PreparedStatement preparedStatementCreateAssociationEntry = getPreparedStatementCreateAssociationEntry() ;
				preparedStatementCreateAssociationEntry.clearParameters();
				preparedStatementCreateAssociationEntry.setInt(1, entry
						.getWordOneId());
				preparedStatementCreateAssociationEntry.setInt(2, entry
						.getWordTwoId());
				preparedStatementCreateAssociationEntry.setDouble(3, entry
						.getFreq());
				preparedStatementCreateAssociationEntry.setDouble(4, entry
						.getIxy());
				preparedStatementCreateAssociationEntry.executeUpdate();
				preparedStatementCreateAssociationEntry.close() ;
				if (idx % 1000 == 0) {
					logger.debug("ODIE_Association: saved " + idx + " of "
							+ associationEntries.size() + " records");
				}
				idx++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateBatch(
			Collection<ODIE_AssociationEntryInf> associationEntries) {
		try {
			int idx = 0;
			for (ODIE_AssociationEntryInf entry : associationEntries) {
				StringBuffer sb = new StringBuffer();
				sb.append("update ");
				sb.append(databaseName + ".");
				sb.append(tableNamePrefix);
				sb.append("_association ");
				sb.append("set ");
				sb.append(" I_X_Y = ? ");
				sb.append("where ");
				sb.append(" id = ?");
				String sql = sb.toString();
				PreparedStatement pStmt = conn.prepareStatement(sql);
				pStmt.setDouble(1, entry.getIxy());
				pStmt.setInt(2, entry.getId());
				pStmt.executeUpdate();
				pStmt.close();
				if (idx % 1000 == 0) {
					logger.debug("ODIE_Association: updated " + idx + " of "
							+ associationEntries.size() + " records");
				}
				idx++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void close() {
	}

	public HashMap<String, ODIE_AssociationEntryInf> getCachedAssociationEntries() {
		// return cachedAssociationEntries;
		return null;
	}

	public void buildAssociationView() {

		try {
			String associationTableName = databaseName + "." + tableNamePrefix
					+ "_association ";
			String histogramTableName = databaseName + "." + tableNamePrefix
					+ "_histogram ";

			StringBuffer sb = new StringBuffer();
			sb.append("update ");
			sb.append(associationTableName + " a, ");
			sb.append(histogramTableName + " h ");
			sb.append("set a.word_one_freq = h.freq, ");
			sb.append("a.is_ner_one = h.is_ner ");
			sb.append(" where ");
			sb.append("a.word_one_id = h.id");
			String sql = sb.toString();
			logger.debug(sql);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();

			sb = new StringBuffer();
			sb.append("update ");
			sb.append(associationTableName + " a, ");
			sb.append(histogramTableName + " h ");
			sb.append("set a.word_two_freq = h.freq, ");
			sb.append("a.is_ner_two = h.is_ner ");
			sb.append(" where ");
			sb.append("a.word_two_id = h.id");
			sql = sb.toString();
			logger.debug(sql);
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (Exception x) {
			x.printStackTrace();
		}

	}

	public void calculateIxy(Double numberOfWords) {
		try {
			//
			// Ixy = (fxy * N) / (fx * fy)
			//
			StringBuffer sb = new StringBuffer();
			sb.append("update " + databaseName + "." + tableNamePrefix
					+ "_association ");
			sb.append("set ");
			sb.append("i_x_y = ");
			sb.append("log2(");
			sb.append("(freq * " + numberOfWords + ")");
			sb.append(" / ");
			sb.append("(word_one_freq * word_two_freq)");
			sb.append(")");
			sb.append(" where ");
			sb.append("(word_one_freq * word_two_freq) > 0 ");
			String sql = sb.toString();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();

			// Other I_x_y are zero
			sb = new StringBuffer();
			sb.append("update " + databaseName + "." + tableNamePrefix
					+ "_association ");
			sb.append("set ");
			sb.append("i_x_y = 0.00");
			sb.append(" where ");
			sb.append("(word_one_freq * word_two_freq) <= 0 ");
			sql = sb.toString();
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public TreeSet<ODIE_AssociationEntryInf> getSortedEntries() {
		return sortedEntries;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public String getTableNamePrefix() {
		return tableNamePrefix;
	}

	public void setTableNamePrefix(String tableNamePrefix) {
		this.tableNamePrefix = tableNamePrefix;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public ODIE_HistogramEntryFactory getHistogramEntryFactory() {
		return histogramEntryFactory;
	}

	public void setHistogramEntryFactory(
			ODIE_HistogramEntryFactory histogramEntryFactory) {
		this.histogramEntryFactory = histogramEntryFactory;
	}

	public boolean isDatabaseCleaning() {
		return isDatabaseCleaning;
	}

	public void setDatabaseCleaning(boolean isDatabaseCleaning) {
		this.isDatabaseCleaning = isDatabaseCleaning;
	}

	@Override
	public void setHistogramEntryFactory(
			ODIE_HistogramEntryFactoryInf histogramEntryFactory) {
	}

	@Override
	public void updateAndCacheAssociationEntry(ODIE_AssociationEntryInf entry) {
	}

	public long pruneInfrequentStaleAssociationEntries(
			long maximunNumberOfAssociations) {
		String qualifiedTableName = databaseName + "." + tableNamePrefix
				+ "_association";
		try {
			long numberOfAssociations = 0L;
			String sql = "select count(*) from " + qualifiedTableName;
			PreparedStatement pStmt = conn.prepareStatement(sql);
			ResultSet rs = pStmt.executeQuery();
			if (rs.next()) {
				numberOfAssociations = rs.getLong(1);
			}
			pStmt.close();

			long overRun = numberOfAssociations - maximunNumberOfAssociations;
			if (overRun > 0) {
				sql = "delete from " + qualifiedTableName;
				sql += " order by freq limit ?";
				pStmt = conn.prepareStatement(sql);
				pStmt.setLong(1, overRun);
				pStmt.executeUpdate();
				pStmt.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void deleteSmallFrequencyContributors(long threshold) {
		try {
			String sql = "delete from " + qualifiedTableName
					+ " where word_one_freq <= ? or word_two_freq <= ?";
			System.out.println(sql) ;
			PreparedStatement pStmt = conn.prepareStatement(sql);
			pStmt.setLong(1, threshold);
			pStmt.setLong(2, threshold);
			pStmt.executeUpdate();
			pStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deletePairsWithNoNamedEntity() {
		try {
			String sql = "delete from " + qualifiedTableName
					+ " where is_ner_one = 0 and is_ner_two = 0";
			PreparedStatement pStmt = conn.prepareStatement(sql);
			pStmt.executeUpdate(sql);
			pStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deletePairsWithTwoNamedEntities() {
		try {
			String sql = "delete from " + qualifiedTableName
					+ " where is_ner_one = 1 and is_ner_two = 1";
			PreparedStatement pStmt = conn.prepareStatement(sql);
			pStmt.executeUpdate(sql);
			pStmt.close() ;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void uniformilyOrganizeWordPairs() {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("update church_association set ");
			sb.append("          word_one_id=(@temp:=word_one_id), ");
			sb.append("          word_one_id = word_two_id, ");
			sb.append("          word_two_id = @temp,");
			sb.append("          is_ner_one = 0,");
			sb.append("          is_ner_two = 1");
			sb.append(" where ");
			sb.append("          is_ner_two = 0");
			String sql = sb.toString();
			System.out.println(sql) ;
			PreparedStatement pStmt = conn.prepareStatement(sql);
			pStmt.executeUpdate();
			pStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Long getNumberOfDistinctSuggestions() {
		Long result = new Long(0L);
		try {
			String sql = "select count(distinct(word_one_id)) from "
					+ qualifiedTableName;
			PreparedStatement pStmt = conn.prepareStatement(sql);
			ResultSet rs = pStmt.executeQuery(sql);
			if (rs.next()) {
				result = rs.getLong(1);
			}
			rs.close();
			pStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public ODIE_AssociationEntryInf selectAssociationEntryById(int jdx) {
		ODIE_AssociationEntry result = null;
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("select * from " + qualifiedTableName + " where id = ?");
			PreparedStatement pStmt = this.conn.prepareStatement(sb.toString());
			pStmt.clearParameters();
			pStmt.setInt(1, jdx);
			ResultSet rs = pStmt
					.executeQuery();
			if (rs.next()) {
				result = new ODIE_AssociationEntry();
				xFerResultSetToAssociationEntry(result, rs);
			}
			rs.close();
			pStmt.close();
		} catch (Exception x) {
			x.printStackTrace();
		}

		return result;
	}
}
