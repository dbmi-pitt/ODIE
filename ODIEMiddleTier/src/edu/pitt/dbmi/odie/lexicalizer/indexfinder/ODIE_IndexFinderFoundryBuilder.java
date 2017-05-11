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
import java.util.Comparator;
import java.util.Date;
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

/**
 * 
 * @author mitchellkj
 * 
 *         The Foundry Builder traverses the ontologies visiting each named
 *         class once and using the terminology associated with that class to
 *         seed the Named Entity Recognition engine of ODIE which is a modified
 *         version of the IndexFinder method.
 * 
 *         The primary component of the IndexFinder representation is the
 *         inverse index of word to phrase, here populated in the table
 *         if_wid2pids
 * 
 *         Build sorted phrase table. Phrase here is used loosely to be the
 *         equivalent of a term of a possibly multiple term class identifier.
 * 
 *         Foreach phrase Add the the phrase to a word histogram that includes a
 *         text field of phrase identifiers (pids). Make use of the invariant
 *         condition that phrases are accessible in order
 */

public class ODIE_IndexFinderFoundryBuilder {

	private static final Logger logger = Logger
			.getLogger(ODIE_IndexFinderFoundryBuilder.class);

	public static final String IF_PARAM_CHAIN_SEPARATOR = "|";

	private String propertyBundleFile;
	private Map<String, IClass> conceptMap = new HashMap<String, IClass>();
	private Connection connection;
	private int currentNsid = -1;
	private int currentCui = -1;
	private boolean isCurrentClassViable = false;

	private PreparedStatement pStmtInsertIfCui2Cls = null;

	private String previousMessage = "";

	private ODIE_IndexFinderFoundryDdlBuilder ddlBuilder = null;

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);
	private boolean isProcessingSynonyms = true;

	private int displayCurrentClsCount = 0;

	private long iterationStartTimeInMilliseconds = 0L;

	public ODIE_IndexFinderFoundryBuilder() {
		this.ddlBuilder = ODIE_IndexFinderFoundryDdlBuilder.getInstance();
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
		this.ddlBuilder.createTables();
		this.ddlBuilder.clearTables();

		changeProgressMessageTo(message);
		pullVocabularyFromFoundry(resources);

		changeProgressMessageTo(message);
		Date timeStart = new Date();
		sortPhraseTable();
		Date timeEnd = new Date();
		logger.debug("Elapsed time to sort the the phrase table is "
				+ (timeEnd.getTime() - timeStart.getTime()));

		changeProgressMessageTo(message);
		buildWid2Pids();

		changeProgressMessageTo(message);
		buildPlen();

		changeProgressMessageTo(message);
		buildCuis();

		closePreparedStatments();
	}

	private void pullVocabularyFromFoundry(List<IResource> resources) {
		TreeSet<IClass> distinctOrderedClses = deriveDistinctOrderedClses(resources);
		this.displayCurrentClsCount = 0;
		this.iterationStartTimeInMilliseconds = (new Date()).getTime();
		for (IResource resource : distinctOrderedClses) {
			this.currentNsid = -1;
			try {
				pullVocabularyFromClass((IClass) resource,
						IF_PARAM_CHAIN_SEPARATOR, IF_PARAM_CHAIN_SEPARATOR, 0);
			} catch (ODIE_ExceptionClassLexicalization e) {
				logger.warn("Failed to lexicalize visited class "
						+ resource.getName());
			}
		}

	}

	private TreeSet<IClass> deriveDistinctOrderedClses(List<IResource> resources) {
		final TreeSet<IClass> distinctOrderedClses = new TreeSet<IClass>(
				new Comparator<IClass>() {
					public int compare(IClass o1, IClass o2) {
						String uri1 = o1.getURI().toASCIIString();
						String uri2 = o2.getURI().toASCIIString();
						int result = uri1.compareTo(uri2);
						return result;
					}
				});
		try {
			for (IResource resource : resources) {
				if (resource instanceof IOntology) {
					IOntology ontology = (IOntology) resource;
					List<IClass> topLevelClassList = Arrays.asList(ontology
							.getRootClasses());
					if (topLevelClassList != null
							&& !topLevelClassList.isEmpty()) {
						distinctOrderedClses.addAll(topLevelClassList);
					}
				} else if (resource instanceof IClass) {
					distinctOrderedClses.add((IClass) resource);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return distinctOrderedClses;
	}

	private void pullVocabularyFromClass(IClass cls, String parentChain,
			String ancestorChain, int level)
			throws ODIE_ExceptionClassLexicalization {

		this.displayCurrentClsCount++;
		if (this.displayCurrentClsCount > 0
				&& this.displayCurrentClsCount % 1000 == 0) {
			long elapsedTimeInMilliseconds = (new Date()).getTime()
					- this.iterationStartTimeInMilliseconds;
			logger.debug("Traversed " + this.displayCurrentClsCount
					+ " classes in " + elapsedTimeInMilliseconds);
		}

		if (this.currentNsid < 0) {
			storeOntologyNamespace(cls);
		}

		if (level == 0) {
			parentChain = this.currentNsid + parentChain;
			ancestorChain = this.currentNsid + ancestorChain;
		}

		indexCls(cls, parentChain, ancestorChain, displayCurrentClsCount);
		indexClsChildren(cls, parentChain, ancestorChain,
				displayCurrentClsCount);

	}

	private void indexClsChildren(IClass cls, String parentChain,
			String ancestorChain, int level)
			throws ODIE_ExceptionClassLexicalization {
		int levelCui = this.currentCui;
		List<IClass> children = new ArrayList<IClass>(Arrays.asList(cls
				.getDirectSubClasses()));
		if (children != null && !children.isEmpty()) {
			for (IClass childCls : children) {
				pullVocabularyFromClass(childCls, levelCui
						+ IF_PARAM_CHAIN_SEPARATOR, levelCui
						+ IF_PARAM_CHAIN_SEPARATOR + ancestorChain, level + 1);
			}
		}
	}

	private void indexCls(IClass cls, String parentChain, String ancestorChain,
			int count) throws ODIE_ExceptionClassLexicalization {

		if (cls.getName() != null) {

			String indexableClassName = deriveClassName(cls);
			String msg = "Processing(" + count + "):" + indexableClassName;
			changeProgressMessageTo(msg);

			storeClassAsCui(cls.getName(), parentChain, ancestorChain);

			// Only if the Class information stores successfully do we continue.

			final TreeSet<ODIE_LexicalizerTerm> termsForCui = ODIE_LexicalizerTerm
					.getSortingTreeSet();
			termsForCui.add(new ODIE_LexicalizerTerm(indexableClassName));
			String[] synonyms = cls.getConcept().getSynonyms();
			if (isProcessingSynonyms && synonyms != null) {
				for (String synonym : synonyms) {
					termsForCui.add(new ODIE_LexicalizerTerm(synonym));
				}
			}

			storeCuiToPhraseTable(termsForCui);
			// displayConceptWords(termsForCui);

		}
	}

	private String deriveClassName(IClass cls) {
		String indexableClassName = cls.getURI().toString();
		indexableClassName = getSimpleName(indexableClassName);
		indexableClassName = ODIE_IndexFinderBestMneumonicNameDeterminer
				.getInstance().bestGuessForMneumonicLabel(indexableClassName,
						cls.getLabels());
		return indexableClassName;

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

	private void closePreparedStatments() {
		try {
			if (pStmtInsertIfCui2Cls != null
					&& !pStmtInsertIfCui2Cls.isClosed()) {
				pStmtInsertIfCui2Cls.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private void storeClassAsCui(String className, String parentChain,
			String ancestorChain) throws ODIE_ExceptionClassLexicalization {

		boolean isAddingCui = true;

		try {

			Integer existingCui = null;
			String existingParentChain = null;
			String existingAncestorChain = null;

			PreparedStatement pstmt = null;
			ResultSet rs = null;

			String sqlQuery = "select cui, parents, ancestory from if_cui2cls where nsid = ? and cname = ?";
			pstmt = this.connection.prepareStatement(sqlQuery);
			pstmt.setInt(1, this.currentNsid);
			pstmt.setString(2, className);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				existingCui = rs.getInt(1);
				existingParentChain = rs.getString(2);
				existingAncestorChain = rs.getString(3);
				isAddingCui = false;
			}
			pstmt.close();
			
			
			if (isAddingCui) {
				String sqlInsert = "insert into if_cui2cls (nsid, parents, ancestory, cname) values (?, ?, ?, ?)";
				if (this.pStmtInsertIfCui2Cls == null) {
					this.pStmtInsertIfCui2Cls = this.connection
							.prepareStatement(sqlInsert,
									Statement.RETURN_GENERATED_KEYS);
				}
				this.pStmtInsertIfCui2Cls.clearParameters();
				this.pStmtInsertIfCui2Cls.setInt(1, this.currentNsid);
				this.pStmtInsertIfCui2Cls.setString(2, IF_PARAM_CHAIN_SEPARATOR
						+ parentChain);
				this.pStmtInsertIfCui2Cls.setString(3, IF_PARAM_CHAIN_SEPARATOR
						+ ancestorChain);
				this.pStmtInsertIfCui2Cls.setString(4, className);
				this.pStmtInsertIfCui2Cls.executeUpdate();
				rs = this.pStmtInsertIfCui2Cls.getGeneratedKeys();
				this.currentCui = rs.next() ? rs.getInt(1) : -1;
				rs.close();
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
			throw new ODIE_ExceptionClassLexicalization(e.getMessage());
		} catch (Exception e) {
			throw new ODIE_ExceptionClassLexicalization(e.getMessage());
		}

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
	 * @param terms
	 * 
	 */
	private void storeCuiToPhraseTable(TreeSet<ODIE_LexicalizerTerm> terms) {
		try {
			if (this.currentCui > -1 && this.currentNsid > -1) {
				for (ODIE_LexicalizerTerm term : terms) {
					if (term.isViable()) {
						int wordCount = term.getWordCount();
						String commaSeparatedWords = term
								.getWordsAsCommaSeparatedValues();
						String sqlUpdate = "insert into if_phrase (cui, word_count, words) values (?, ?, ?)";
						PreparedStatement pstmt = this.connection
								.prepareStatement(sqlUpdate);
						pstmt.setInt(1, this.currentCui);
						pstmt.setInt(2, wordCount);
						pstmt.setString(3, commaSeparatedWords.toString());
						pstmt.executeUpdate();
						pstmt.close();
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void sortPhraseTable() {
		try {
			String tgtTableName = "if_phrase_sorted";
			this.ddlBuilder.dropTableIfExists(tgtTableName);
			String sql = "create table if_phrase_sorted as select pid upid, words, word_count, cui from if_phrase order by word_count, words, cui";
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.executeUpdate();
		
			sql = "alter table if_phrase_sorted add column pid int(11) not null AUTO_INCREMENT primary key first";
			stmt = connection.prepareStatement(sql);
			stmt.executeUpdate();

			sql = "alter table if_phrase_sorted engine = MyISAM";
			stmt = connection.prepareStatement(sql);
			stmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void buildWid2Pids() {
		try {

			String sql = "";

			sql = "select pid, words, word_count, cui from if_phrase_sorted";
			PreparedStatement phraseLoader = connection.prepareStatement(sql);

			sql = "select wid from if_word_ht where word = ?";
			PreparedStatement wordKeyFinder = connection.prepareStatement(sql);

			sql = "select pids from if_wid2pids where wid = ?";
			PreparedStatement phraseListFinder = connection
					.prepareStatement(sql);

			sql = "update if_wid2pids set pids = ? where wid = ?";
			PreparedStatement reverseIndexBuilder = connection
					.prepareStatement(sql);

			int totalPhrasesProcessed = 0;
			Date timeStart = new Date() ;
			ResultSet phraseRs = phraseLoader.executeQuery();
			int currentPid = -1 ;
			while (phraseRs.next()) {
				currentPid = phraseRs.getInt(1) ;
				String words = phraseRs.getString(2);
				if (words == null) {
					continue;
				}
				String[] wordArray = words.split(",");
				for (int wdx = 0; wdx < wordArray.length; wdx++) {
					String word = wordArray[wdx];
					int currentWid = buildWordHtEntry(word);
					phraseListFinder.setInt(1, currentWid);
					ResultSet pidsRs = phraseListFinder.executeQuery();
					pidsRs.next(); // Assert true since wid2pids are added when word_ht entries are
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
				
				totalPhrasesProcessed++;
				if (totalPhrasesProcessed % 1000 == 0) {
					Date timeNow = new Date() ;
					long elapsedTime = timeNow.getTime() - timeStart.getTime() ;
					String msg = "Processed " + totalPhrasesProcessed + " phrases in " + elapsedTime + " milliseconds." ;
					changeProgressMessageTo(msg);
					logger.debug(msg);
				}

			}
		
			reverseIndexBuilder.close();
			phraseListFinder.close();
			wordKeyFinder.close();
			phraseLoader.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

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
			PreparedStatement wordInserter = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			sql = "insert into if_wid2pids (wid, pids) values (?, ?)";
			PreparedStatement wid2pidsInserter = connection
					.prepareStatement(sql);

			wordKeyFinder.setString(1, word);
			ResultSet wordRs = wordKeyFinder.executeQuery();

			if (wordRs.next()) {
				wid = wordRs.getInt(1);
			} else {
				wordInserter.setString(1, word);
				wordInserter.executeUpdate();
				
				ResultSet rs = wordInserter.getGeneratedKeys();
				wid = rs.next() ? rs.getInt(1) : -1;
				rs.close();

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

	@SuppressWarnings("unused")
	private void displayConceptWords(TreeSet<ODIE_LexicalizerTerm> terms) {
		logger.debug("Latest Terms added: ");
		for (ODIE_LexicalizerTerm term : terms) {
			logger.debug(term);
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
		if (this.ddlBuilder != null) {
			this.ddlBuilder.setConn(conn);
		}
	}

	public boolean isCurrentClassViable() {
		return isCurrentClassViable;
	}

	public void setCurrentClassViable(boolean isCurrentClassViable) {
		this.isCurrentClassViable = isCurrentClassViable;
	}

}
