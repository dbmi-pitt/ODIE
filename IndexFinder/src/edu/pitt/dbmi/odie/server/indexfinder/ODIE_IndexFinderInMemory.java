package edu.pitt.dbmi.odie.server.indexfinder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class ODIE_IndexFinderInMemory implements ODIE_IndexFinderInMemoryInf {

	private static final Logger logger = Logger
			.getLogger(ODIE_IndexFinderInMemory.class);

	private Connection connection;

	private HashMap<String, ODIE_Word> word_ht_cache = new HashMap<String, ODIE_Word>();
	private HashMap<String, Integer> word_ht = new HashMap<String, Integer>();
	private int[][] wid2pids = null;
	private int[] cuis = null;
	private int[] plen = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

	public ODIE_IndexFinderInMemory(Connection connection) {
		logger.debug("Loading IF Tables into memory");
		setConnection(connection);
		loadInMemoryTables();
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public void loadInMemoryTables() {
		if (connection == null) {
			logger.error("No database connection set");
			return;
		}
		try {
			loadWordHt() ;
			loadWidToPids() ;
			loadCuis() ;
			loadPlen() ;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}

	}

	private void loadWordHt() throws SQLException {
		
		Date timeStart = new Date();
		
		String sql = "select count(*) from if_word_ht";
		PreparedStatement wordHtCounter = connection.prepareStatement(sql);
		
		sql = "select wid, word from if_word_ht where wid > ? order by wid limit ?";
		PreparedStatement wordHtCursor = connection.prepareStatement(sql);

		int lastWid = -1;
		int recordsPerQuery = 1000;
		boolean isSearching = true;
		while (isSearching) {
			wordHtCursor.setInt(1, lastWid);
			wordHtCursor.setInt(2, recordsPerQuery);
			ResultSet rs = wordHtCursor.executeQuery();
			isSearching = false;
			while (rs.next()) {
				lastWid = rs.getInt(1);
				String word = rs.getString(2);
				word_ht.put(word, new Integer(lastWid));
				isSearching = true;
			}
			rs.close();
			wordHtCursor.clearParameters();
		}
		wordHtCursor.close();
		long elapsedTime = (new Date()).getTime() - timeStart.getTime();
		logger.debug("Successfully loaded word_ht in " + elapsedTime
				+ " milliseconds");
	}
	
	private void loadWidToPids() throws SQLException {
		
		Date timeStart = new Date();
		
		String sql = "select count(*) from if_wid2pids";
		PreparedStatement wid2pidsCounter = connection
				.prepareStatement(sql);
	
		sql = "select wid, pids from if_wid2pids where wid > ? order by wid limit ?";
		PreparedStatement wid2pidsCursor = connection.prepareStatement(sql);
		
		int lastWid = -1;
		int recordsPerQuery = 1000;
		
		ResultSet wid2pidsRs = wid2pidsCounter.executeQuery();
		wid2pidsRs.next();
		int numberWid2Pids = wid2pidsRs.getInt(1);
		wid2pids = new int[numberWid2Pids][];
	
		boolean isSearching = true;
		while (isSearching) {
			wid2pidsCursor.setInt(1, lastWid);
			wid2pidsCursor.setInt(2, recordsPerQuery);
			ResultSet rs = wid2pidsCursor.executeQuery();
			isSearching = false;
			while (rs.next()) {
				lastWid = rs.getInt(1);
				String pids = rs.getString(2);
				String[] pidsArray = pids.split(",");
				wid2pids[lastWid - 1] = new int[pidsArray.length];
				for (int idx = 0; idx < pidsArray.length; idx++) {
					wid2pids[lastWid - 1][idx] = Integer.valueOf(
							pidsArray[idx]).intValue();
				}
				isSearching = true;
			}
			rs.close();
			wid2pidsCursor.clearParameters();
		}
		long elapsedTime = (new Date()).getTime() - timeStart.getTime();
		logger.debug("Successfully loaded wid2pids in " + elapsedTime
				+ " milliseconds");
	}
	
	private void loadCuis() throws SQLException {
		
		Date timeStart = new Date();
		
		String sql = "select count(*) from if_cuis";
		PreparedStatement cuisCounter = connection.prepareStatement(sql);

		sql = "select pid, cui from if_cuis where pid > ? order by pid limit ?";
		PreparedStatement cuisCursor = connection.prepareStatement(sql);
		
		int lastPid = -1;
		int recordsPerQuery = 1000;
		
		ResultSet cuisRs = cuisCounter.executeQuery();
		cuisRs.next();
		int numberCuis = cuisRs.getInt(1);
		cuis = new int[numberCuis];
	
		boolean isSearching = true;
		while (isSearching) {
			cuisCursor.setInt(1, lastPid);
			cuisCursor.setInt(2, recordsPerQuery);
			ResultSet rs = cuisCursor.executeQuery();
			isSearching = false;
			while (rs.next()) {
				lastPid = rs.getInt(1);
				int cui = rs.getInt(2);
				cuis[lastPid - 1] = cui;
				isSearching = true;
			}
			rs.close();
			cuisCursor.clearParameters();
		}
		long elapsedTime = (new Date()).getTime() - timeStart.getTime();
		logger.debug("Successfully loaded cuis in " + elapsedTime
				+ " milliseconds");

	}
	
	private void loadPlen() throws SQLException {
		
		Date timeStart = new Date();
		
		String sql = "select count(*) from if_plen";
		PreparedStatement plenCounter = connection.prepareStatement(sql);

		sql = "select len, uppid from if_plen order by len";
		PreparedStatement plenCursor = connection.prepareStatement(sql);
		
		ResultSet plenRs = plenCounter.executeQuery();
		plenRs.next();
		int numberPlen = plenRs.getInt(1);
		plen = new int[numberPlen];
		ResultSet rs = plenCursor.executeQuery();
		while (rs.next()) {
			int len = rs.getInt(1);
			int uppid = rs.getInt(2);
			plen[len - 1] = uppid;
		}
		rs.close();
		plenCursor.clearParameters();
		long elapsedTime = (new Date()).getTime() - timeStart.getTime();
		logger.debug("Successfully loaded plen in " + elapsedTime
				+ " milliseconds");
	}

	public void displayInMemoryTables() {
		logger.debug("word_ht:\n\n");
		for (Iterator<String> keysIterator = word_ht.keySet().iterator(); keysIterator
				.hasNext();) {
			String word = keysIterator.next();
			int wid = word_ht.get(word);
			logger.debug("\t" + word + " ==> " + wid);
		}
		logger.debug("\nwid2pids:\n");
		for (int idx = 0; idx < wid2pids.length; idx++) {
			int[] pids = wid2pids[idx];
			int wid = idx + 1;
			logger.debug(wid + ": (");
			for (int jdx = 0; jdx < pids.length; jdx++) {
				logger.debug(pids[jdx] + ",");
			}
			logger.debug(")");
		}
		logger.debug("\ncuis:\n");
		for (int idx = 0; idx < cuis.length; idx++) {
			logger.debug(idx + 1 + " ==> " + cuis[idx]);
		}
		logger.debug("\nplen:\n");
		for (int idx = 0; idx < plen.length; idx++) {
			logger.debug(idx + 1 + " ==> " + plen[idx]);
		}

	}

	/*
	 * getWordForKey
	 * 
	 * Work horse method that builds the ODIE_Word objects
	 * from the in memory index finder tables if_word_ht and if_wid2pids
	 * 
	 */
	public ODIE_Word getWordForKey(String wordKey) {
		ODIE_Word result = word_ht_cache.get(wordKey);
		if (result != null) {
			return result;
		}
		Integer widAsInteger = word_ht.get(wordKey);
		if (widAsInteger != null) {
			result = new ODIE_Word(wordKey);
			result.id = widAsInteger;
			result.frequency = new Integer(0);
			int[] pids = wid2pids[widAsInteger.intValue() - 1];
			for (int pdx = 0; pdx < pids.length; pdx++) {
				int pid = pids[pdx];
				int cui = cuis[pid - 1];
				ODIE_Phrase phrase = new ODIE_Phrase();
				phrase.id = pid;
				phrase.sui = pid + "";
				phrase.cui = cui + "";
				phrase.wordCount = calculateWordCountForPhrase(pid);
				phrase.wordHits = 0;
				result.phraseList.add(phrase);
			}
			word_ht_cache.put(wordKey, result) ;
		}
		return result;
	}

	private int calculateWordCountForPhrase(int pid) {
		int result = 1;
		for (int idx = 0; idx < plen.length; idx++) {
			int upperBoundIdx = plen[idx];
			if (pid > upperBoundIdx) {
				result++;
			} else {
				break;
			}
		}
		return result;
	}

	private void removeLongPhrases() {
		// Remove phrases with more than four words and words
		// that have no shorter phrases
		ArrayList<ODIE_Word> wordsToBeRemoved = new ArrayList<ODIE_Word>();
		for (Iterator<ODIE_Word> wordIterator = word_ht_cache.values()
				.iterator(); wordIterator.hasNext();) {
			ODIE_Word word = wordIterator.next();
			ArrayList<ODIE_Phrase> longPhrases = new ArrayList<ODIE_Phrase>();
			for (Iterator<ODIE_Phrase> phraseIterator = word.phraseList
					.iterator(); phraseIterator.hasNext();) {
				ODIE_Phrase phrase = phraseIterator.next();
				if (phrase.wordCount > 4) {
					longPhrases.add(phrase);
				}
			}
			word.phraseList.removeAll(longPhrases);
			if (word.phraseList.isEmpty()) {
				wordsToBeRemoved.add(word);
			}
		}
		word_ht_cache.values().removeAll(wordsToBeRemoved);
	}

	public String getCnForCui(int cui) {
		String result = "ERROR";
		try {
			String sql = "select t2.namespace, t1.cname from if_cui2cls t1 left join if_namespace t2 on t1.nsid = t2.cui where t1.cui = ?";
			PreparedStatement selectConceptName = connection
					.prepareStatement(sql);
			selectConceptName.setInt(1, cui);
			ResultSet rs = selectConceptName.executeQuery();
			if (rs.next()) {
				result = rs.getString(1) + "#" + rs.getString(2);
			}
			selectConceptName.close();
		} catch (SQLException e) {
			result = "ERROR";
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return result;
	}

	public String fetchConceptUriForCui(Integer cui) {
		String result = null;

		try {
			String sqlQuery = "";
			sqlQuery += "select \n";
			sqlQuery += "    ns.namespace,\n";
			sqlQuery += "    c2c.cname\n";
			sqlQuery += "from\n";
			sqlQuery += "    if_namespace ns,\n";
			sqlQuery += "    if_cui2cls c2c\n";
			sqlQuery += "where\n";
			sqlQuery += "    ns.cui = c2c.nsid and\n";
			sqlQuery += "    c2c.cui = ?";
			PreparedStatement pstmt = this.connection
					.prepareStatement(sqlQuery);
			pstmt.setInt(1, cui);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				String nameSpace = rs.getString(1);
				String conceptName = rs.getString(2);
				result = nameSpace + "#" + conceptName;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return result;
	}

	public void addClass(String classQName) {

	}

	public void addSynonym(String classQName, String synonymousTerm) {

	}

	public void clear() {

	}

	public Connection getConnection() {
		return this.connection;
	}

	public void removeClass(String classQName) {

	}

	public void removeSynonym(String classQName, String synonymousTerm) {

	}

	public static void reset() {

	}
}
