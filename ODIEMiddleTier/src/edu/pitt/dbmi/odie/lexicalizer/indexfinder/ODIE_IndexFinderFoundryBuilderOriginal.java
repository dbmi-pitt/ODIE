package edu.pitt.dbmi.odie.lexicalizer.indexfinder;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IOntology;
import edu.pitt.ontology.IResource;
import edu.pitt.text.tools.Stemmer;

public class ODIE_IndexFinderFoundryBuilderOriginal {
	private static final Logger logger = Logger
			.getLogger(ODIE_IndexFinderFoundryBuilderOriginal.class);
	public static final String IF_PARAM_CHAIN_SEPARATOR = "|";
	private String propertyBundleFile;
	private Map<String, IClass> conceptMap = new HashMap<String, IClass>();
	private Connection connection;
	private int currentNsid = -1;
	private int currentCui = -1;
	private List<String> currentWords = null;
	private Stemmer normApi = null;
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);
	private boolean isProcessingSynonyms = true;

	private int displayCurrentClsCount = 0;

	public ODIE_IndexFinderFoundryBuilderOriginal() {
		initializeStemmer();
	}

	public void initializeStemmer() {
		this.normApi = new Stemmer();
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void storeFoundryAsIndexFinderTables(List<IResource> resources) {
		String message = "Creating tables";
		changeProgressMessageTo(message);
		createTables();
		clearTables();

		changeProgressMessageTo(message);
		pullVocabularyFromFoundry(resources);

		changeProgressMessageTo(message);
		sortPhraseTable();

		changeProgressMessageTo(message);
		buildWid2Pids();

		changeProgressMessageTo(message);
		buildPlen();

		changeProgressMessageTo(message);
		buildCuis();
	}

	private void createTables() {
		createNamespace();
		createCui2Cls();
		createCuis();
		createPhraseTable("if_phrase");
		// createPhraseTable("if_phrase_sorted");
		createWordHt();
		createWid2Pids();
		createPLen();
	}

	public void createNamespace() {
		try {
			dropTableIfExists("if_namespace");

			String sql = "";
			sql = "CREATE TABLE IF NOT EXISTS if_namespace ( "
					+ "   cui int NOT NULL AUTO_INCREMENT, "
					+ "   namespace text, " + "   PRIMARY KEY (cui)" + " ) ";

			PreparedStatement statement = connection.prepareStatement(sql);
			statement.executeUpdate();

			sql = "ALTER TABLE if_namespace AUTO_INCREMENT=1";
			statement = connection.prepareStatement(sql);
			statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createCui2Cls() {
		try {
			dropTableIfExists("if_cui2cls");

			String sql = "";
			sql = "create table IF NOT EXISTS if_cui2cls ( "
					+ "   cui int NOT NULL AUTO_INCREMENT," + "   nsid int, "
					+ "   parents text, " + "   ancestory text, "
					+ "   cname text, " + "   PRIMARY KEY (cui)" + " ) ";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.executeUpdate();

			sql = "ALTER TABLE if_cui2cls AUTO_INCREMENT=1000000";
			statement = connection.prepareStatement(sql);
			statement.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createCuis() {
		try {
			dropTableIfExists("if_cuis");

			String sql = "";
			sql = "create table IF NOT EXISTS if_cuis ( " + "   pid int, "
					+ "   cui int" + " ) ";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.executeUpdate();

		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public void createPhraseTable(String tableName) {
		try {
			dropTableIfExists(tableName);

			String sql = "";
			sql = "create table IF NOT EXISTS " + tableName + "( "
					+ "   pid int not null AUTO_INCREMENT PRIMARY KEY, "
					+ "   words text, " + "   word_count int, " + "   cui int"
					+ " ) ";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.executeUpdate();

			sql = "create index " + tableName + "_idx on " + tableName
					+ "(word_count, words(712), cui)";
			Statement createIndexStatement = connection.createStatement();
			createIndexStatement.executeUpdate(sql);
			createIndexStatement.close();

		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public void createPhraseSortedTable(String tableName) {
		try {
			dropTableIfExists(tableName);

			String sql = "";
			sql = "create table IF NOT EXISTS " + tableName + "( "
					+ "   pid int not null PRIMARY KEY, " + "   words text, "
					+ "   word_count int, " + "   cui int" + " ) ";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.executeUpdate();

			sql = "create index " + tableName + "_idx on " + tableName
					+ "(word_count, words(712), cui)";
			Statement createIndexStatement = connection.createStatement();
			createIndexStatement.executeUpdate(sql);
			createIndexStatement.close();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public void createWordHt() {
		try {
			String sql = "";

			sql = "drop table if exists if_word_ht";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.executeUpdate();
			sql = "CREATE TABLE IF NOT EXISTS if_word_ht ("
					+ " wid int NOT NULL AUTO_INCREMENT,"
					+ " word varchar(1000) DEFAULT NULL,"
					+ " PRIMARY KEY (wid)," + " KEY if_word_ht_idx (word)"
					+ " ) ENGINE=InnoDB DEFAULT CHARSET=latin1 ";
			statement = connection.prepareStatement(sql);
			statement.executeUpdate();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public void createWid2Pids() {
		try {
			dropTableIfExists("if_wid2pids");

			String sql = "";
			sql = "create table IF NOT EXISTS if_wid2pids ( " + "   wid int, "
					+ "   pids varchar(10000) default 'ODIE-UNDEFINED'" + " ) ";
			sql = "create table IF NOT EXISTS if_wid2pids ( " + "   wid int, "
					+ "   pids longtext" + " ) ";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.executeUpdate();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public void createPLen() {
		try {
			dropTableIfExists("if_plen");
			String sql = "";
			sql = "create table IF NOT EXISTS if_plen ( " + "   len int, "
					+ "   uppid int" + " ) ";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void dropTableIfExists(String tableName) {
		try {
			String sql = "DROP TABLE IF EXISTS " + tableName;
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void pullVocabularyFromFoundry(List<IResource> resources) {
		try {
			for (IResource resource : resources) {
				String resourceUri = ODIE_IndexFinderUtils
						.deContaminateNamespace(resource.getURI().toString());
				logger.debug("Will pull resource " + resourceUri);
			}
			TreeSet<IClass> sortedNoDuplicateClassEntryPoints = new TreeSet<IClass>(
					new Comparator<IClass>() {
						public int compare(IClass o1, IClass o2) {
							String uri1 = o1.getURI().toASCIIString();
							String uri2 = o2.getURI().toASCIIString();
							int result = uri1.compareTo(uri2);
							return result;
						}
					});
			for (IResource resource : resources) {
				if (resource instanceof IOntology) {
					IOntology ontology = (IOntology) resource;
					List<IClass> topLevelClassList = Arrays.asList(ontology
							.getRootClasses());
					if (topLevelClassList != null
							&& !topLevelClassList.isEmpty()) {
						sortedNoDuplicateClassEntryPoints
								.addAll(topLevelClassList);
					}
				} else if (resource instanceof IClass) {
					sortedNoDuplicateClassEntryPoints.add((IClass) resource);
				}
			}

			displayCurrentClsCount = 0;
			for (IResource resource : sortedNoDuplicateClassEntryPoints) {
				this.currentNsid = -1;
				pullVocabularyFromClass((IClass) resource,
						IF_PARAM_CHAIN_SEPARATOR, IF_PARAM_CHAIN_SEPARATOR, 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// private void pullVocabularyFromOntology(IOntology ontology) {
	// Collection<IClass> topLevelClassList = Arrays.asList(ontology
	// .getRootClasses());
	// this.currentNsid = -1; // Derive this from first class
	//
	// if (topLevelClassList != null) {
	// for (IClass cls : topLevelClassList) {
	//
	// pullVocabularyFromClass(cls, IF_PARAM_CHAIN_SEPARATOR,
	// IF_PARAM_CHAIN_SEPARATOR);
	// }
	// } else {
	// logger.error("No top level classes for ontology "
	// + ODIE_IndexFinderUtils.deContaminateNamespace(ontology
	// .getURI().toString()));
	// }
	// }

	private void pullVocabularyFromClass(IClass cls, String parentChain,
			String ancestorChain, int level) {

		displayCurrentClsCount++;

		if (this.currentNsid < 0) {
			storeOntologyNamespace(cls);
		}

		if (level == 0) {
			parentChain = this.currentNsid + parentChain;
			ancestorChain = this.currentNsid + ancestorChain;
		}

		indexCls(cls, parentChain, ancestorChain, displayCurrentClsCount);
		int levelCui = this.currentCui;

		//
		// Potentially time consuming technique used to avoid infinite loops
		// in
		// graph traversal
		List<IClass> children = new ArrayList<IClass>(Arrays.asList(cls
				.getDirectSubClasses()));
		List<IClass> parents = Arrays.asList(cls.getSuperClasses());
		children.removeAll(parents);

		// Children order here is not important.
		// Collections.sort(children);

		if (children != null && !children.isEmpty()) {
			for (IClass childCls : children) {
				pullVocabularyFromClass(childCls, levelCui
						+ IF_PARAM_CHAIN_SEPARATOR, levelCui
						+ IF_PARAM_CHAIN_SEPARATOR + ancestorChain, level + 1);
			}
		}

	}

	private void indexCls(IClass cls, String parentChain, String ancestorChain,
			int count) {

		if (cls.getName() != null) {
			String classUri = cls.getURI().toString();

			String indexableClassName = cls.getName();
			getSimpleName(classUri);

			// Score the className vs the labels
			indexableClassName = bestGuessForMneumonicLabel(indexableClassName,
					cls.getLabels());

			String msg = "Processing(" + count + "):" + indexableClassName;
			changeProgressMessageTo(msg);

			boolean isStoredClass = storeClassAsCui(cls.getName(), parentChain,
					ancestorChain);

			if (isStoredClass) {

				final HashMap<String, ArrayList<String>> term2wordLists = new HashMap<String, ArrayList<String>>();

				ArrayList<String> termAndWordsForClsName = buildWordListFromTerm(indexableClassName);
				String term = termAndWordsForClsName.remove(0);
				term2wordLists.put(term, termAndWordsForClsName);

				String[] synonyms = cls.getConcept().getSynonyms();
				if (isProcessingSynonyms && synonyms != null) {
					for (String synonym : synonyms) {
						if (!synonym.toLowerCase().equals(
								cls.getName().toLowerCase())) {
							termAndWordsForClsName = buildWordListFromTerm(synonym);
							term = termAndWordsForClsName.remove(0);
							term2wordLists.put(term, termAndWordsForClsName);
						}
					}
				}

				for (String termKey : term2wordLists.keySet()) {
					ArrayList<String> words = term2wordLists.get(termKey);
					this.currentWords = new ArrayList<String>();
					this.currentWords.add(termKey);
					this.currentWords.addAll(words);
					storeCuiToPhraseTable();
					displayConceptWords(this.currentWords);
				}
			}

		}
	}

	private String bestGuessForMneumonicLabel(String className,
			String[] rdfsLabels) {
		ArrayList<ScoredTerm> scores = new ArrayList<ScoredTerm>();
		scores.add(new ScoredTerm(className, new Integer(0)));
		if (rdfsLabels != null && rdfsLabels.length > 0) {
			for (String rdfsLabel : rdfsLabels) {
				scores.add(new ScoredTerm(rdfsLabel, new Integer(10)));
			}
		}
		// Favor alphabetic characters, remain neutral on white space
		// and penalize anything else thus we will prefer the
		// longest alphabetic name
		for (ScoredTerm scoredTerm : scores) {
			char[] chars = scoredTerm.term.toCharArray();
			for (char c : chars) {
				if (Character.isLetter(c)) {
					scoredTerm.score += 1;
				} else if (Character.isWhitespace(c)) {
					;
				} else {
					scoredTerm.score -= 1;
				}
			}
		}

		Collections.sort(scores, new Comparator<ScoredTerm>() {
			@Override
			public int compare(ScoredTerm o1, ScoredTerm o2) {
				return o2.score - o1.score;
			}
		});

		return scores.get(0).term;
	}

	@SuppressWarnings("unused")
	private String bestGuessForMneumonicLabelRdfs(String className,
			String[] rdfsLabels) {
		String result = className;
		String diagnostic = "bestGuessForMneumonicLabel " + className;
		if (rdfsLabels != null && rdfsLabels.length > 0) {
			for (String rdfsLabel : rdfsLabels) {
				diagnostic += "," + rdfsLabel;
			}
		}
		if (rdfsLabels != null && rdfsLabels.length > 0) {
			for (String rdfsLabel : rdfsLabels) {
				result = rdfsLabel;
				break;
			}
		}
		diagnostic += " result ==> " + result;
		logger.debug(diagnostic);
		return result;
	}

	class ScoredTerm {
		public String term;
		public Integer score;

		public ScoredTerm(String term, Integer score) {
			this.term = term;
			this.score = score;
		}
	}

	private ArrayList<String> buildWordListFromTerm(String term) {
		if (term.matches("^[A-Za-z]+$")) {
			boolean isAddingWords = false;
			term = ODIE_IndexFinderUtils.unCamelCase(term, isAddingWords)
					.get(0);
		}
		term = ODIE_IndexFinderUtils.deContaminate(term);
		ArrayList<String> currentWords = ODIE_IndexFinderUtils
				.explodeTermToWordList(term, "_");
		currentWords = normalizePhraseWords(currentWords);
		currentWords.add(0, term); // Place the term at
		// front
		return currentWords;
	}

	private ArrayList<String> normalizePhraseWords(
			ArrayList<String> currentWords) {
		ArrayList<String> normalizedWords = new ArrayList<String>();
		for (int idx = 0; idx < currentWords.size(); idx++) {
			String currentWord = currentWords.get(idx);
			this.normApi.add(currentWord);
			this.normApi.stem();
			String stemmedWord = this.normApi.getResultString();
			logger.debug("Stemmed " + currentWord + " ==> " + stemmedWord);
			normalizedWords.add(stemmedWord);
		}
		return normalizedWords;
	}

	private void storeOntologyNamespace(IClass cls) {
		try {
			String clsUri = cls.getURI().toString();
			int lastPoundIndex = clsUri.lastIndexOf("#");
			String namespace = clsUri.substring(0, lastPoundIndex);
			namespace = ODIE_IndexFinderUtils.deContaminateNamespace(namespace);

			// Check to see if it already exists
			this.currentNsid = -1;
			String sqlQuery = "select cui from if_namespace where namespace = '"
					+ namespace + "'";
			PreparedStatement pstmt = this.connection
					.prepareStatement(sqlQuery);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				this.currentNsid = rs.getInt(1);
			}
			pstmt.close();

			if (this.currentNsid < 0) {

				String sqlUpdate = "insert into if_namespace (namespace) values (?)";
				pstmt = this.connection.prepareStatement(sqlUpdate);
				pstmt.setString(1, namespace);
				pstmt.executeUpdate();
				pstmt.close();

				sqlQuery = "select max(cui) cui from if_namespace";
				pstmt = this.connection.prepareStatement(sqlQuery);
				rs = pstmt.executeQuery();
				if (rs.next()) {
					this.currentNsid = rs.getInt(1);
				}
				pstmt.close();

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private boolean storeClassAsCui(String className, String parentChain,
			String ancestorChain) {

		boolean isAddingCui = true;

		try {

			Integer existingCui = null;
			String existingParentChain = null;
			String existingAncestorChain = null;

			String sqlQuery = "select cui, parents, ancestory from if_cui2cls where nsid = ? and cname = ?";
			PreparedStatement pstmt = this.connection
					.prepareStatement(sqlQuery);
			pstmt.setInt(1, this.currentNsid);
			pstmt.setString(2, className);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				existingCui = rs.getInt(1);
				existingParentChain = rs.getString(2);
				existingAncestorChain = rs.getString(3);
				isAddingCui = false;
			}
			pstmt.close();

			if (isAddingCui) {
				String sqlUpdate = "insert into if_cui2cls (nsid, parents, ancestory, cname) values (?, ?, ?, ?)";
				pstmt = this.connection.prepareStatement(sqlUpdate);
				pstmt.setInt(1, this.currentNsid);
				pstmt.setString(2, IF_PARAM_CHAIN_SEPARATOR + parentChain);
				pstmt.setString(3, IF_PARAM_CHAIN_SEPARATOR + ancestorChain);
				pstmt.setString(4, className);
				pstmt.executeUpdate();
				pstmt.close();

				sqlQuery = "select max(cui) cui from if_cui2cls";
				pstmt = this.connection.prepareStatement(sqlQuery);
				rs = pstmt.executeQuery();
				rs.next();
				this.currentCui = rs.getInt(1);
				pstmt.close();
			} else { // Merge the new parent chain with the existing one
				String sqlUpdate = "update if_cui2cls set parents = ?, ancestory = ? where nsid = ? and cname = ?";
				pstmt = this.connection.prepareStatement(sqlUpdate);
				pstmt
						.setString(1, mergeChain(existingParentChain,
								parentChain));
				pstmt.setString(2, mergeChain(existingAncestorChain,
						ancestorChain));
				pstmt.setInt(3, this.currentNsid);
				pstmt.setString(4, className);
				pstmt.executeUpdate();
				pstmt.close();

				this.currentCui = existingCui;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return isAddingCui;
	}

	private String mergeChain(String oldChain, String newChain) {
		TreeSet<String> mergedAndSortedChain = new TreeSet<String>();
		oldChain = oldChain.substring(1, oldChain.length() - 1);
		newChain = newChain.substring(1, newChain.length() - 1);
		String[] oldChainCuis = oldChain.split("\\" + IF_PARAM_CHAIN_SEPARATOR);
		String[] newChainCuis = newChain.split("\\" + IF_PARAM_CHAIN_SEPARATOR);
		Collection<String> oldChainCuiLis = Arrays.asList(oldChainCuis);
		Collection<String> newChainCuiLis = Arrays.asList(newChainCuis);
		mergedAndSortedChain.addAll(oldChainCuiLis);
		mergedAndSortedChain.addAll(newChainCuiLis);
		StringBuffer sb = new StringBuffer();
		sb.append(IF_PARAM_CHAIN_SEPARATOR);
		Iterator<String> iter = mergedAndSortedChain.descendingIterator();
		while (iter.hasNext()) {
			String cui = iter.next();
			sb.append(cui);
			sb.append(IF_PARAM_CHAIN_SEPARATOR);
		}
		return sb.toString();
	}

	public void clearTables() {
		try {
//			final String[] tableNames = { "if_namespace", "if_cui2cls",
//					"if_phrase", "if_phrase_sorted", "if_word_ht",
//					"if_wid2pids", "if_plen", "if_cuis" };
			final String[] tableNames = { "if_namespace", "if_cui2cls",
					"if_phrase", "if_word_ht",
					"if_wid2pids", "if_plen", "if_cuis" };
			for (int idx = 0; idx < tableNames.length; idx++) {
				String tableName = tableNames[idx];
				String sqlUpdate = "delete from " + tableName;
				PreparedStatement pstmt = this.connection
						.prepareStatement(sqlUpdate);
				pstmt.executeUpdate();
				pstmt.close();

				// sqlUpdate = "ALTER TABLE " + tableName + " AUTO_INCREMENT =
				// 1";
				// pstmt = this.conn.prepareStatement(sqlUpdate);
				// pstmt.executeUpdate();
				// pstmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * storeCuiToPhraseTable
	 * 
	 * Expects global context to include
	 * 
	 * currentNsid - IndexFinder internal name space identifier currentCui -
	 * IndexFinder internal concept unique identifier currentWords - [<term>,
	 * <w0>, <w1>, ..., <wN>]
	 * 
	 * currentWords shall be sorted and contain no duplicates
	 * 
	 */
	private void storeCuiToPhraseTable() {
		try {
			if (this.currentCui > -1 && this.currentNsid > -1
					&& this.currentWords != null) {
				if (this.currentWords.size() > 1) {
					this.currentWords.remove(0); // Don't use full term
				}
				Collections.sort(this.currentWords); // use default
				// comparison of String
				StringBuffer commaSeparatedWords = new StringBuffer();
				for (Iterator<String> wordIterator = this.currentWords
						.iterator(); wordIterator.hasNext();) {
					String word = (String) wordIterator.next();
					commaSeparatedWords.append(word);
					commaSeparatedWords.append(",");
				}
				commaSeparatedWords
						.deleteCharAt(commaSeparatedWords.length() - 1);
				int wordCount = this.currentWords.size();

				String sqlUpdate = "insert into if_phrase (words, word_count, cui) values (?, ?, ?)";
				PreparedStatement pstmt = this.connection
						.prepareStatement(sqlUpdate);
				pstmt.setString(1, commaSeparatedWords.toString());
				pstmt.setInt(2, wordCount);
				pstmt.setInt(3, this.currentCui);
				pstmt.executeUpdate();
				pstmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void mergeSortPhraseTable(String src, String tgt, int srcSPos,
			int srcEPos) throws SQLException {
		if ((srcEPos - srcSPos + 1) > 1) {
			int srcMPos = (srcEPos - srcSPos) / 2;
			mergeSortPhraseTable(src, tgt, srcSPos, srcMPos);
			mergeSortPhraseTable(src, tgt, srcMPos + 1, srcEPos);
			int leftIdx = srcSPos;
			int rightIdx = srcMPos + 1;
			for (int tgtIdx = srcSPos; tgtIdx < srcEPos; tgtIdx++) {
				String cacheSrcRecordSql = "select words, word_count, cui from "
						+ src + " where id = ?";
				PreparedStatement cacheSrcRecordStmt = this.connection
						.prepareStatement(cacheSrcRecordSql);
				if (leftIdx <= srcMPos && rightIdx <= srcEPos) {
					// Do a comparison
					cacheSrcRecordStmt.clearParameters();
					cacheSrcRecordStmt.setInt(1, leftIdx);
					ResultSet leftRs = cacheSrcRecordStmt.executeQuery();
					cacheSrcRecordStmt.clearParameters();
					cacheSrcRecordStmt.setInt(1, rightIdx);
					ResultSet rightRs = cacheSrcRecordStmt.executeQuery();
					ResultSet tgtRs = getLeastResultSet(leftRs, rightRs);
					writeTgtRs(tgt, tgtRs, tgtIdx);
				}
			}
		}
	}

	private void writeTgtRs(String tgt, ResultSet tgtRs, int tgtIdx)
			throws SQLException {
		String writeTgtRsStmtSql = "update " + tgt
				+ "set words = ?, word_count = ?, cui = ? where pid = ?";
		PreparedStatement writeTgtRsStmt = this.connection
				.prepareStatement(writeTgtRsStmtSql);
		writeTgtRsStmt.setString(1, tgtRs.getString("words"));
		writeTgtRsStmt.setInt(2, tgtRs.getInt("word_count"));
		writeTgtRsStmt.setInt(3, tgtRs.getInt("cui"));
		writeTgtRsStmt.setInt(4, tgtIdx);
		writeTgtRsStmt.executeUpdate();
	}

	private ResultSet getLeastResultSet(ResultSet leftRs, ResultSet rightRs)
			throws SQLException {
		ResultSet tgtRs = null;
		String leftWords = leftRs.getString("words");
		int leftWordCount = leftRs.getInt("word_count");
		int leftCui = leftRs.getInt("cui");
		String rightWords = rightRs.getString("words");
		int rightWordCount = rightRs.getInt("word_count");
		int rightCui = rightRs.getInt("cui");
		if (leftWordCount <= rightWordCount) {
			tgtRs = leftRs;
		} else {
			if (leftWords.compareTo(rightWords) <= 0) {
				tgtRs = leftRs;
			} else {
				if (leftCui <= rightCui) {
					tgtRs = leftRs;
				} else {
					tgtRs = rightRs;
				}
			}
		}
		return tgtRs;
	}

	public void sortPhraseTable() {
		try {
			String tgtTableName = "if_phrase_sorted";
			dropTableIfExists(tgtTableName);
			String sql = "create table if_phrase_sorted as select words, word_count, cui from if_phrase order by word_count, words, cui";
			PreparedStatement createStatement = connection
					.prepareStatement(sql);
			createStatement.executeUpdate();

			sql = "alter table if_phrase_sorted add column pid int(11) not null auto_increment primary key first";
			createStatement = connection.prepareStatement(sql);
			createStatement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void sortPhraseTableOriginal() {
		try {
			String sql = "select * from if_phrase where word_count > ? or (word_count = ? and words > ?) or  (word_count = ? and words = ? and cui > ?) order by word_count, words, cui limit 50";
			PreparedStatement selectStatement = connection
					.prepareStatement(sql);
			sql = "insert into if_phrase_sorted (words, word_count, cui) values (?, ?, ?)";
			PreparedStatement insertStatement = connection
					.prepareStatement(sql);
			int currentWordCount = -1;
			String currentWords = "0";
			int currentCui = -1;
			int totalRecordsProcessed = 0;
			boolean isSearching = true;
			String msg = "";
			while (isSearching) {
				selectStatement.setInt(1, currentWordCount);
				selectStatement.setInt(2, currentWordCount);
				selectStatement.setString(3, currentWords);
				selectStatement.setInt(4, currentWordCount);
				selectStatement.setString(5, currentWords);
				selectStatement.setInt(6, currentCui);
				ResultSet rs = selectStatement.executeQuery();
				isSearching = false;
				while (rs.next()) {
					currentWords = rs.getString("words");
					currentWordCount = rs.getInt("word_count");
					currentCui = rs.getInt("cui");
					insertStatement.setString(1, currentWords);
					insertStatement.setInt(2, currentWordCount);
					insertStatement.setInt(3, currentCui);
					insertStatement.executeUpdate();
					isSearching = true;
					totalRecordsProcessed++;
					if (totalRecordsProcessed % 1000 == 0) {
						msg = "Sorted " + totalRecordsProcessed + " phrases";
						changeProgressMessageTo(msg);
						logger.debug("Inserted " + totalRecordsProcessed);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public void buildWid2PidsNew() {
		try {

			String sql = "";
			sql = "select count(*) numberOfSortedPhrases from if_phrase_sorted";
			PreparedStatement countSortedPhrases = connection
					.prepareStatement(sql);

			sql = "select words, word_count, cui from if_phrase_sorted where pid > ? limit 1";
			PreparedStatement phraseLoader = connection.prepareStatement(sql);

			sql = "select wid from if_word_ht where word = ?";
			PreparedStatement wordKeyFinder = connection.prepareStatement(sql);

			sql = "select pids from if_wid2pids where wid = ?";
			PreparedStatement phraseListFinder = connection
					.prepareStatement(sql);

			sql = "update if_wid2pids set pids = ? where wid = ?";
			PreparedStatement reverseIndexBuilder = connection
					.prepareStatement(sql);

			
			ResultSet sortedPhraseCountRs = countSortedPhrases.executeQuery();
			sortedPhraseCountRs.next();
			int numberOfSortedPhrases = sortedPhraseCountRs.getInt(1) ;
			String msg = "";
			for (int currentPid = 0 ; currentPid < numberOfSortedPhrases ; currentPid++) {
			
				// On MySQL 6.0 beta the query does not return null when
				// there is only one row in the table and the id is 1 so
				// we do this additional check;
				// if(currentPid == phraseCursorRs.getInt(1))
				// break;

				phraseLoader.setInt(1, currentPid);
				ResultSet phraseRs = phraseLoader.executeQuery();
				if (phraseRs.next()) {
					String words = phraseRs.getString(1);
					if (words == null) {
						break;
					}
					// int wordCount = phraseRs.getInt(2);
					// int cui = phraseRs.getInt(3);
					String[] wordArray = words.split(",");
					for (int wdx = 0; wdx < wordArray.length; wdx++) {
						String word = wordArray[wdx];
						int currentWid = buildWordHtEntry(word);
						phraseListFinder.setInt(1, currentWid);
						ResultSet pidsRs = phraseListFinder.executeQuery();
						pidsRs.next();
						String currentPids = pidsRs.getString(1);
						if (currentPids.equalsIgnoreCase("ODIE-UNDEFINED")) {
							currentPids = currentPid + "";
						} else {
							currentPids += "," + currentPid;
						}
						reverseIndexBuilder.setString(1, currentPids);
						reverseIndexBuilder.setInt(2, currentWid);
						reverseIndexBuilder.executeUpdate();
					}

				}
				
				if (currentPid % 1000 == 0) {
					msg = "Processed " + currentPid + " phrases";
					changeProgressMessageTo(msg);
					logger.debug("Processed " + currentPid);
				}
			
			}

			reverseIndexBuilder.close();
			phraseListFinder.close();
			wordKeyFinder.close();
			phraseLoader.close();
			countSortedPhrases.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void buildWid2Pids() {
		try {

			String sql = "";
			sql = "select min(pid) from if_phrase_sorted where pid > ?";
			PreparedStatement phraseCursorStatement = connection
					.prepareStatement(sql);

			sql = "select words, word_count, cui from if_phrase_sorted where pid = ?";
			PreparedStatement phraseLoader = connection.prepareStatement(sql);

			sql = "select wid from if_word_ht where word = ?";
			PreparedStatement wordKeyFinder = connection.prepareStatement(sql);

			sql = "select pids from if_wid2pids where wid = ?";
			PreparedStatement phraseListFinder = connection
					.prepareStatement(sql);

			sql = "update if_wid2pids set pids = ? where wid = ?";
			PreparedStatement reverseIndexBuilder = connection
					.prepareStatement(sql);

			int currentPid = -1;
			phraseCursorStatement.setInt(1, currentPid);
			ResultSet phraseCursorRs = phraseCursorStatement.executeQuery();
			int totalPhrasesProcessed = 0;
			String msg = "";
			while (phraseCursorRs.next()) {

				currentPid = phraseCursorRs.getInt(1);
				if (currentPid <= 0) {
					break; // Since min returns null and getInt returns zero
					// for NULL
				}

				// On MySQL 6.0 beta the query does not return null when
				// there is only one row in the table and the id is 1 so
				// we do this additional check;
				// if(currentPid == phraseCursorRs.getInt(1))
				// break;

				phraseLoader.setInt(1, currentPid);
				ResultSet phraseRs = phraseLoader.executeQuery();
				if (phraseRs.next()) {
					String words = phraseRs.getString(1);
					if (words == null) {
						break;
					}
					// int wordCount = phraseRs.getInt(2);
					// int cui = phraseRs.getInt(3);
					String[] wordArray = words.split(",");
					for (int wdx = 0; wdx < wordArray.length; wdx++) {
						String word = wordArray[wdx];
						int currentWid = buildWordHtEntry(word);
						phraseListFinder.setInt(1, currentWid);
						ResultSet pidsRs = phraseListFinder.executeQuery();
						pidsRs.next();
						String currentPids = pidsRs.getString(1);
						if (currentPids.equalsIgnoreCase("ODIE-UNDEFINED")) {
							currentPids = currentPid + "";
						} else {
							currentPids += "," + currentPid;
						}
						reverseIndexBuilder.setString(1, currentPids);
						reverseIndexBuilder.setInt(2, currentWid);
						reverseIndexBuilder.executeUpdate();
					}

				}
				totalPhrasesProcessed++;
				if (totalPhrasesProcessed % 1000 == 0) {

					msg = "Processed " + totalPhrasesProcessed + " phrases";
					changeProgressMessageTo(msg);
					logger.debug("Processed " + totalPhrasesProcessed);
				}
				phraseCursorStatement.setInt(1, currentPid);
				phraseCursorRs = phraseCursorStatement.executeQuery();
			}

			reverseIndexBuilder.close();
			phraseListFinder.close();
			wordKeyFinder.close();
			phraseLoader.close();
			phraseCursorStatement.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private String previousMessage = "";

	private void changeProgressMessageTo(String message) {
		propertyChangeSupport.firePropertyChange(
				ODIEConstants.PROPERTY_PROGRESS_MSG, previousMessage, message);
		previousMessage = message;
	}

	private int buildWordHtEntry(String word) {
		int wid = -1;
		try {
			String sql = "";

			sql = "select wid from if_word_ht where word = ?";
			PreparedStatement wordKeyFinder = connection.prepareStatement(sql);

			sql = "insert into if_word_ht (word) values (?)";
			PreparedStatement wordInserter = connection.prepareStatement(sql);

			sql = "insert into if_wid2pids (wid, pids) values (?, ?)";
			PreparedStatement wid2pidsInserter = connection
					.prepareStatement(sql);

			// int currentPid = -1;
			wordKeyFinder.setString(1, word);
			ResultSet wordRs = wordKeyFinder.executeQuery();

			if (wordRs.next()) {
				wid = wordRs.getInt(1);
			} else {
				wordInserter.setString(1, word);
				wordInserter.executeUpdate();

				wordRs = wordKeyFinder.executeQuery();
				wordRs.next();
				wid = wordRs.getInt(1);

				wid2pidsInserter.setInt(1, wid);
				wid2pidsInserter.setString(2, "ODIE-UNDEFINED");
				wid2pidsInserter.executeUpdate();
			}

			wordKeyFinder.close();
			wordInserter.close();
			wid2pidsInserter.close();
			wordKeyFinder = null;
			wordInserter = null;
			wid2pidsInserter = null;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return wid;
	}

	public void buildPlen() {
		try {
			String sql = "";
			sql = "select max(word_count) from if_phrase_sorted";
			PreparedStatement phraseRetrieveMaximumUpperBoundStatement = connection
					.prepareStatement(sql);
			sql = "select max(pid) from if_phrase_sorted where word_count = ?";
			PreparedStatement phraseCursorStatement = connection
					.prepareStatement(sql);
			sql = "select cui from if_phrase_sorted where pid = ?";
			PreparedStatement phraseLoader = connection.prepareStatement(sql);
			sql = "insert into if_plen (len, uppid) values (?, ?)";
			PreparedStatement insertStatement = connection
					.prepareStatement(sql);

			ResultSet rs = phraseRetrieveMaximumUpperBoundStatement
					.executeQuery();
			rs.next();
			int phraseMaximumUpperBound = rs.getInt(1);

			int phraseUpperBound = 1;
			while (phraseUpperBound <= phraseMaximumUpperBound) {
				phraseCursorStatement.setInt(1, phraseUpperBound);
				ResultSet phraseCursorRs = phraseCursorStatement.executeQuery();
				if (phraseCursorRs.next()) {
					int currentPid = phraseCursorRs.getInt(1);
					phraseLoader.setInt(1, currentPid);
					ResultSet phraseRs = phraseLoader.executeQuery();
					if (phraseRs.next()) {
						insertStatement.setInt(1, phraseUpperBound);
						insertStatement.setInt(2, currentPid);
						insertStatement.executeUpdate();
					} else {
						insertStatement.setInt(1, phraseUpperBound);
						insertStatement.setInt(2, -1);
						insertStatement.executeUpdate();
					}
				}
				phraseUpperBound++;
			}
			phraseCursorStatement.close();
			phraseLoader.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * buildCuis
	 * 
	 * This is a slice of the if_phrase_sorted table, columns
	 */
	public void buildCuis() {
		try {
			String sql = "";
			sql = "select min(pid) pid from if_phrase_sorted where pid > ?";
			PreparedStatement phraseCursorStatement = connection
					.prepareStatement(sql);
			sql = "select cui from if_phrase_sorted where pid = ?";
			PreparedStatement phraseLoader = connection.prepareStatement(sql);
			sql = "insert into if_cuis (pid, cui) values (?, ?)";
			PreparedStatement insertStatement = connection
					.prepareStatement(sql);
			int currentPid = -1;
			phraseCursorStatement.setInt(1, currentPid);
			ResultSet phraseCursorRs = phraseCursorStatement.executeQuery();
			while (phraseCursorRs.next()) {
				currentPid = phraseCursorRs.getInt(1);
				if (currentPid <= 0) {
					break; // Since min returns null and getInt returns zero
					// for NULL
				}
				phraseLoader.setInt(1, currentPid);
				ResultSet phraseLoaderRS = phraseLoader.executeQuery();
				if (phraseLoaderRS.next()) {
					int cui = phraseLoaderRS.getInt(1);
					insertStatement.setInt(1, currentPid);
					insertStatement.setInt(2, cui);
					insertStatement.executeUpdate();
				}
				phraseCursorStatement.setInt(1, currentPid);
				phraseCursorRs = phraseCursorStatement.executeQuery();
			}
			insertStatement.close();
			phraseLoader.close();
			phraseCursorStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private String getSimpleName(String uri) {
		String result = "";
		result = uri.substring(uri.lastIndexOf("#") + 1, uri.length());
		return result;
	}

	private void displayConceptWords(List<String> words) {
		logger.debug("Found concept words: ");
		for (Iterator<String> iterator = words.iterator(); iterator.hasNext();) {
			logger.debug(String.valueOf(iterator.next()) + " ");
		}
	}

	public String getPropertyBundleFile() {
		return propertyBundleFile;
	}

	public void setPropertyBundleFile(String propertyBundleFileInput) {
		propertyBundleFile = propertyBundleFileInput;
	}

	public Map<String, IClass> getIClassMap() {
		return conceptMap;
	}

	public String getCnForCui(int cui) {
		String result = "ERROR";
		try {
			String sql = "select t2.namespace, t1.cname from if_cui2cls t1 left join if_namespace t2 on t1.nsid = t2.nsid where t1.cui = ?";
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
		}
		return result;
	}

	public Connection getConn() {
		return connection;
	}

	public void setConn(Connection conn) {
		this.connection = conn;
	}

	public static void main(String[] args) {
		ODIE_IndexFinderFoundryBuilderOriginal foundryBuilder = new ODIE_IndexFinderFoundryBuilderOriginal();
		String oldChain = "|1000003|1000002|1000000|1|";
		String newChain = "|1000003|1000001|1000000|1|";
		logger.debug(foundryBuilder.mergeChain(oldChain, newChain));

		// foundryBuilder.initializeStemmer();
		// foundryBuilder.storeFoundryAsIndexFinderTables();
		// foundryBuilder.loadInMemoryTables();
		// foundryBuilder.displayInMemoryTables();
	}
}
