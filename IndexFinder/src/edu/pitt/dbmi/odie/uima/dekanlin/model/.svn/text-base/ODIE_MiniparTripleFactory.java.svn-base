package edu.pitt.dbmi.odie.uima.dekanlin.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class ODIE_MiniparTripleFactory {

	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ODIE_MiniparTripleFactory.class);

	private Connection conn;
	private String tableNamePrefix;
	private String databaseName;

	private String qualifiedTableName;

	private PreparedStatement preparedStatementFetchMiniparTriple = null;
	private PreparedStatement preparedStatementCreateMiniparTriple = null;
	private PreparedStatement preparedStatementUpdateMiniparTriple = null;

	private boolean isDatabaseCaching = true ;

	private int recordIndex = 0 ;
	
	private final HashMap<String, ODIE_MiniparTriple> cachedTriplesByKey = new HashMap<String, ODIE_MiniparTriple>() ;
	
	public void initialize(Connection connInput, String databaseNameParam,
			String tableNamePrefixParam) {
		conn = connInput;
		tableNamePrefix = tableNamePrefixParam;
		databaseName = databaseNameParam;
		qualifiedTableName = databaseName + "." + tableNamePrefix + "_triples";
	}

	public void dropMiniparTriple() {
		Statement sqlStmt = null;
		try {
			sqlStmt = conn.createStatement();
			sqlStmt.execute("drop table if exists " + this.qualifiedTableName);
			sqlStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void createMiniparTriple() {
		Statement sqlStmt = null;
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("CREATE TABLE IF NOT EXISTS  ");
			sb.append(this.qualifiedTableName);
			sb.append(" (\n");
			sb.append("  ID bigint(20) NOT NULL AUTO_INCREMENT,\n");
			sb
					.append("  WORD_ONE varchar(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb
					.append("  RELATION varchar(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb
					.append("  WORD_TWO varchar(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb.append("  FREQ decimal(11,5) NOT NULL DEFAULT 0,\n");
			sb.append("  INFO decimal(11,5) NOT NULL DEFAULT 0.0,\n");
			sb.append("  PRIMARY KEY (ID),\n");
			sb.append("  KEY INDEX_WORD (WORD_ONE, RELATION, WORD_TWO)\n");
			sb
					.append(") ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC");
			sqlStmt = conn.createStatement();
			sqlStmt.execute(sb.toString());
			sqlStmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void prepareDMLStatements() {
		try {
			// Fetch
			StringBuffer sb = new StringBuffer() ;
			sb.append("select ID, WORD_ONE, RELATION, WORD_TWO, FREQ, INFO from ") ;
			sb.append(this.qualifiedTableName) ;
			sb.append(" where WORD_ONE = ? and RELATION = ? and WORD_TWO = ?") ;
			preparedStatementFetchMiniparTriple = conn.prepareStatement(sb.toString()) ;
			
			// Insert
			preparedStatementCreateMiniparTriple = conn
					.prepareStatement("insert into "
							+ this.qualifiedTableName + " (WORD_ONE, RELATION, WORD_TWO, FREQ, INFO) values (?, ?, ?, ?, ?)");
			
			// Update
			preparedStatementUpdateMiniparTriple = conn
			.prepareStatement("update "
					+ this.qualifiedTableName
					+ " set WORD_ONE = ?, RELATION = ?, WORD_TWO = ?, FREQ = ?, INFO = ? where ID = ?");
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public void clearMiniparTriples() {
		cachedTriplesByKey.clear();
	}

	public void updateMiniparTriples() {
		logger.debug("Updating cached Minipar Triple information with "
				+ cachedTriplesByKey.size() + " entries");
		for (String key : cachedTriplesByKey.keySet()) {
			ODIE_MiniparTriple entry = cachedTriplesByKey.get(key);
			try {
				preparedStatementUpdateMiniparTriple.clearParameters();
				preparedStatementUpdateMiniparTriple
						.setString(1, entry.getWordOne());
				preparedStatementUpdateMiniparTriple.setString(2, entry
						.getRelation());
				preparedStatementUpdateMiniparTriple
						.setString(3, entry.getWordTwo());
				preparedStatementUpdateMiniparTriple
				.setDouble(4, entry.getFreq());
				preparedStatementUpdateMiniparTriple
				.setDouble(5, entry.getInfo());
				preparedStatementUpdateMiniparTriple.setInt(6, entry.getId());

				preparedStatementUpdateMiniparTriple.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public ODIE_MiniparTriple fetchMiniparTriple(ODIE_MiniparTriple searchTriple) {
		ODIE_MiniparTriple result = (searchTriple.getKey() != null) ? cachedTriplesByKey.get(searchTriple.getKey()) : null;
		try {
			if (result == null) {
				result = fetchOrCreateMiniparTriple(searchTriple);
				cachedTriplesByKey.put(searchTriple.getKey(), result);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	// preparedStatementCreateMiniparTriple = conn
	// .prepareStatement("insert into "
	// + databaseName
	// + "."
	// + tableNamePrefix
	// +
	// "_histogram (WORD, FREQ, POS, TAGGER, IS_NER, IS_NP) values (?, ?, ?, ?, ?, ?)");

	private ODIE_MiniparTriple fetchOrCreateMiniparTriple(ODIE_MiniparTriple searchTriple)
			throws SQLException {
		ODIE_MiniparTriple result = null;
		if (isDatabaseCaching) {
			result = selectMiniparTriple(searchTriple);
			if (result == null) {
				preparedStatementCreateMiniparTriple.clearParameters();
				preparedStatementCreateMiniparTriple.setString(1, searchTriple.getWordOne());
				preparedStatementCreateMiniparTriple.setString(2, searchTriple.getRelation());
				preparedStatementCreateMiniparTriple.setString(3, searchTriple.getWordTwo());
				preparedStatementCreateMiniparTriple.setDouble(4, searchTriple.getFreq());
				preparedStatementCreateMiniparTriple.setDouble(5, searchTriple.getInfo());
				preparedStatementCreateMiniparTriple.executeUpdate();
				result = selectMiniparTriple(searchTriple);
			}
		} else {
			result = new ODIE_MiniparTriple();
			result.setId(new Integer(recordIndex++));
			result.setWordOne(searchTriple.getWordOne()) ;
			result.setRelation(searchTriple.getRelation()) ;
			result.setWordTwo(searchTriple.getWordTwo()) ;
			result.setFreq(new Double(0.0d));
			result.setInfo(new Double(0.0d)) ;
		}

		return result;
	}

	private ODIE_MiniparTriple selectMiniparTriple(ODIE_MiniparTriple searchTriple)
			throws SQLException {
		ODIE_MiniparTriple result = null;
		
		preparedStatementFetchMiniparTriple.clearParameters();
		preparedStatementFetchMiniparTriple.setString(1, searchTriple.getWordOne());
		preparedStatementFetchMiniparTriple.setString(2, searchTriple.getRelation());
		preparedStatementFetchMiniparTriple.setString(3, searchTriple.getWordTwo());
		ResultSet rs = preparedStatementFetchMiniparTriple.executeQuery();
		if (rs.next()) {
			Integer id = rs.getInt(1);
			String wordOne = rs.getString(2);
			String relation = rs.getString(3);
			String wordTwo = rs.getString(4);
			Double freq = rs.getDouble(5);
			Double info = rs.getDouble(6);
			result = new ODIE_MiniparTriple() ;
			result.setId(id) ;
			result.setWordOne(wordOne) ;
			result.setRelation(relation) ;
			result.setWordTwo(wordTwo) ;
			result.setFreq(freq) ;
			result.setInfo(info) ;
		}
		rs.close();
		return result;
	}

	public void insertBatch() {
		try {
			final ArrayList<ODIE_MiniparTriple> tripleList = new ArrayList<ODIE_MiniparTriple>() ;
			tripleList.addAll(cachedTriplesByKey.values());
			String sql = "insert into "
					+ qualifiedTableName
					+ " (WORD_ONE, RELATION, WORD_TWO, FREQ, INFO) values (?, ?, ?, ?, ?)";
			int idx = 0;
			for (ODIE_MiniparTriple entry : tripleList) {
				PreparedStatement preparedStatementCreateMiniparTriple = conn
						.prepareStatement(sql);
				preparedStatementCreateMiniparTriple.setString(1, entry
						.getWordOne());
				preparedStatementCreateMiniparTriple
						.setString(2, entry.getRelation());
				preparedStatementCreateMiniparTriple.setString(3, entry
						.getWordTwo());
				preparedStatementCreateMiniparTriple.setDouble(4, entry
						.getFreq());
				preparedStatementCreateMiniparTriple.setDouble(5, entry
						.getInfo());
				preparedStatementCreateMiniparTriple.executeUpdate();
				preparedStatementCreateMiniparTriple.close();
				if (idx % 1000 == 0) {
					logger.debug("ODIE_MiniparTripleFactory: saved " + idx + " of "
							+ tripleList.size() + " records");
				}
				idx++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			preparedStatementFetchMiniparTriple.close();
			preparedStatementCreateMiniparTriple.close();
			preparedStatementUpdateMiniparTriple.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

//	public ODIE_MiniparTriple lookUpEntryById(Long id) {
//		cacheEntriesById();
//		return cachedTriplesById.get(id.intValue());
//	}

//	public long lookUpWordFreqById(Long id) {
//		cacheEntriesById();
//		ODIE_MiniparTriple entry = lookUpEntryById(id) ;
//		return (entry != null) ? (new Double(entry.getFreq())).longValue()  : 0L ;
//	}

//	public void cacheEntriesById() {
//		if (cachedTriplesByKey.size() > 0
//				&& cachedTriplesByKey.size() == 0) {
//			for (ODIE_MiniparTriple entry : cachedTriplesByKey.values()) {
//				cachedTriplesById.put(entry.getId(), entry);
//			}
//		}
//	}
	
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

}
