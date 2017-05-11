package edu.pitt.dbmi.odie.uima.dekanlin.consumer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import edu.mayo.bmi.uima.core.type.NamedEntity;
import edu.mayo.bmi.uima.core.type.UmlsConcept;
import edu.pitt.dbmi.odie.ODIEUtils;
import edu.pitt.dbmi.odie.uima.dekanlin.model.ODIE_HistogramEntry;
import edu.pitt.dbmi.odie.uima.dekanlin.model.ODIE_HistogramEntryFactory;
import edu.pitt.dbmi.odie.uima.dekanlin.model.ODIE_MiniparTriple;
import edu.pitt.dbmi.odie.uima.dekanlin.model.ODIE_MiniparTripleFactory;
import edu.pitt.dbmi.odie.uima.dekanlin.model.ODIE_MiniparTriplePair;
import edu.pitt.dbmi.odie.uima.dekanlin.model.ODIE_MiniparTripleTwComparator;
import edu.pitt.dbmi.odie.uima.dekanlin.model.ODIE_Similarity;
import edu.pitt.dbmi.odie.uima.dekanlin.model.ODIE_SimilarityFactory;
import edu.pitt.dbmi.odie.uima.util.ODIE_Connection;
import edu.pitt.dbmi.odie.uima.util.ODIE_IFConstants;
import edu.pitt.dbmi.odie.uima.util.ODIE_NormalizationUtils;
import edu.pitt.dbmi.odie.uima.util.ODIE_UimaUtils;

public class ODIE_CASC_LinSuggestionGenerator extends CasConsumer_ImplBase {

	private static final long serialVersionUID = 1L;

	/* LOG4J logger based on class name */
	private Logger logger = Logger.getLogger(getClass().getName());

	public static final String CONST_WILD_CARD = "%";

	private ODIE_Connection conn = null;

	private String[] categories = { "Noun", "Verb", "AdjOrAdv" };
	private String currentCategory;

	private String[][] categorizedSimilarityPatternGroups = { { "N%" },
			{ "V%" }, { "J%", "R%" } };

	private int currentId = -1;

	private PreparedStatement preparedStatementUpdateInfo = null;

	private String databaseName = "lin_analysis";

	private final HashMap<Integer, ODIE_MiniparTriple> cachedTriplesById = new HashMap<Integer, ODIE_MiniparTriple>();

	public final HashMap<String, ArrayList<ODIE_MiniparTriple>> cachedTriplesByFirstWord = new HashMap<String, ArrayList<ODIE_MiniparTriple>>();

	private static final HashMap<String, Integer> __r___counts = new HashMap<String, Integer>();

	private static final HashMap<String, Integer> w_r___counts = new HashMap<String, Integer>();

	private static final HashMap<String, Integer> __r_w_counts = new HashMap<String, Integer>();

	private static int similarityIndex = 1;

	private static final int CONST_MAX_SIMILARITIES = 20000;

	private ODIE_HistogramEntryFactory factoryHistogramEntry;

	private ODIE_MiniparTripleFactory factoryMiniparTriple;

	private ODIE_SimilarityFactory factorySimilarity;

	private boolean isWriteGapsOnly = true;

	private ODIE_HistogramEntryFactory nerHistogramEntryFactory = null;

	private ArrayList<NamedEntity> namedEntityArray = new ArrayList<NamedEntity>();

	private final TreeSet<ODIE_Similarity> sortedSimilaritiesByHindler = new TreeSet<ODIE_Similarity>(
			new Comparator<ODIE_Similarity>() {
				public int compare(ODIE_Similarity o1, ODIE_Similarity o2) {
					int wordOneComparison = o1.wordOne
							.compareToIgnoreCase(o2.wordOne);
					int wordTwoComparison = o1.wordTwo
							.compareToIgnoreCase(o2.wordTwo);
					Double scoreOne = o1.hindler;
					Double scoreTwo = o2.hindler;
					if (wordOneComparison == 0 && wordTwoComparison == 0) {
						// This is a redundant entry
						return 0;
					} else {
						if (scoreOne < scoreTwo) {
							return 1;
						} else if (scoreOne > scoreTwo) {
							return -1;
						} else {
							return o1.id - o2.id;
						}
					}
				}
			});

	private static final TreeSet<ODIE_HistogramEntry> sortedHistogram = new TreeSet<ODIE_HistogramEntry>(
			new Comparator<ODIE_HistogramEntry>() {
				public int compare(ODIE_HistogramEntry o1,
						ODIE_HistogramEntry o2) {
					return o1.wordText.compareTo(o2.wordText);
				}
			});

	// 1) all of these 10 relations together- conj, det, lex-mod, mod, nn, obj,
	// pcomp-n, punc, s, subj
	// 2) obj alone
	// 3) mod alone
	// 4) nn alone
	// 5) conj alone
	// 6) combination of obj,mod, conj, nn
	// 7) combination of obj,mod, conj, nn, det
	// 8) combination of obj,mod, conj, nn, det, subj
	// 9) combination of obj,mod, conj, nn, det, s
	// 10) subj alone
	// 11) lex-mod alone

	private static final String[] kaiSet000 = { "conj", "det", "lex-mod",
			"mod", "nn", "obj", "pcomp-n", "punc", "s", "subj" };
	private static final String[] kaiSet001 = { "obj" };
	private static final String[] kaiSet002 = { "mod" };
	private static final String[] kaiSet003 = { "nn" };
	private static final String[] kaiSet004 = { "nn" };
	private static final String[] kaiSet005 = { "conj" };
	private static final String[] kaiSet006 = { "obj", "mod", "conj", "nn" };
	private static final String[] kaiSet007 = { "obj", "mod", "conj", "nn",
			"det" };
	private static final String[] kaiSet008 = { "obj", "mod", "conj", "nn",
			"det", "subj" };
	private static final String[] kaiSet009 = { "obj", "mod", "conj", "nn",
			"det", "s" };
	private static final String[] kaiSet010 = { "subj" };
	private static final String[] kaiSet011 = { "lex-mod" };

	private String[] currentKaiSet = null;

	public ODIE_CASC_LinSuggestionGenerator() {
	}

	public void initialize() throws ResourceInitializationException {
		try {

			configJdbcInit();
			
			Connection c = conn.getConnection();
			if (c == null) {
				return;
			}
			//
			// Set up the named entity histogram
			//
			this.nerHistogramEntryFactory = new ODIE_HistogramEntryFactory();
			this.nerHistogramEntryFactory.initialize(conn, databaseName,
					"document_ner");
			this.nerHistogramEntryFactory.prepareDMLStatements();
			this.nerHistogramEntryFactory.createHistogramEntry();

			//
			// Assign current Kai set partition
			//
			assignCurrentKaiSet();

			String writeGapsOnlyAsString = (String) getConfigParameterValue(ODIE_IFConstants.IF_PARAM_ODIE_LIN_WRITE_GAPS_ONLY);
			if (writeGapsOnlyAsString != null
					&& writeGapsOnlyAsString.equals("false")) {
				setWriteGapsOnly(false);
			}

		} catch (InstantiationException e) {
			throw new ResourceInitializationException(e);
		} catch (IllegalAccessException e) {
			throw new ResourceInitializationException(e);
		} catch (ClassNotFoundException e) {
			throw new ResourceInitializationException(e);
		} catch (SQLException e) {
			throw new ResourceInitializationException(e);
		}
	}

	private void assignCurrentKaiSet() {
		try {
			String url = (String) getConfigParameterValue(ODIE_IFConstants.IF_PARAM_DB_URL);
			int lastUnderScoreIdx = url.lastIndexOf("_");
			String potentialNumericSuffix = url.substring(
					lastUnderScoreIdx + 1, url.length());
			int numericSuffix = Integer.parseInt(potentialNumericSuffix);

			switch (numericSuffix) {
			case 0:
				this.currentKaiSet = kaiSet000;
				System.out.println("Using kaiSet000");
				break;
			case 1:
				this.currentKaiSet = kaiSet001;
				System.out.println("Using kaiSet001");
				break;
			case 2:
				this.currentKaiSet = kaiSet002;
				System.out.println("Using kaiSet002");
				break;
			case 3:
				this.currentKaiSet = kaiSet003;
				System.out.println("Using kaiSet003");
				break;
			case 4:
				this.currentKaiSet = kaiSet004;
				System.out.println("Using kaiSet004");
				break;
			case 5:
				this.currentKaiSet = kaiSet005;
				System.out.println("Using kaiSet005");
				break;
			case 6:
				this.currentKaiSet = kaiSet006;
				System.out.println("Using kaiSet006");
				break;
			case 7:
				this.currentKaiSet = kaiSet007;
				System.out.println("Using kaiSet007");
				break;
			case 8:
				this.currentKaiSet = kaiSet008;
				System.out.println("Using kaiSet008");
				break;
			case 9:
				this.currentKaiSet = kaiSet009;
				System.out.println("Using kaiSet009");
				break;
			case 10:
				this.currentKaiSet = kaiSet010;
				System.out.println("Using kaiSet010");
				break;
			case 11:
				this.currentKaiSet = kaiSet011;
				System.out.println("Using kaiSet011");
				break;
			default:
				this.currentKaiSet = kaiSet000;
				System.out.println("Using kaiSet000");
				break;
			}
		} catch (Exception x) {
			this.currentKaiSet = kaiSet000;
			x.printStackTrace();
		}

	}

	private void configJdbcInit() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		String driver = (String) getConfigParameterValue(ODIE_IFConstants.PARAM_DB_DRIVER);
		String url = (String) getConfigParameterValue(ODIE_IFConstants.IF_PARAM_DB_URL);
		String userName = (String) getConfigParameterValue(ODIE_IFConstants.PARAM_DB_USERNAME);
		String password = (String) getConfigParameterValue(ODIE_IFConstants.PARAM_DB_PASSWORD);
		Class.forName(driver).newInstance();
		conn = new ODIE_Connection(driver, url, userName, password);
		
		System.out.println("Connected to the database");
		this.databaseName = ODIEUtils.extractDatabaseNameFromDbUrl(url);
	}

	public void processCas(CAS aCAS) throws ResourceProcessException {

		JCas jcas;
		try {
			jcas = aCAS.getJCas();
		} catch (CASException e) {
			throw new ResourceProcessException(e);
		}

		//
		// Clear cache structures
		//
		this.namedEntityArray.clear();

		//
		// Cache the NamedEntities for this document
		// these are prerequisite contributions of earlier
		// pipeline components
		//
		this.namedEntityArray = ODIE_UimaUtils.pullNamedEntitiesFromJCas(jcas);

		//
		// Histogram the NamedEntities across the document set
		//
		histogramNamedEntities(this.namedEntityArray);
	}

	private void histogramNamedEntities(ArrayList<NamedEntity> namedEntityArray) {
		for (NamedEntity namedEntity : namedEntityArray) {
			UmlsConcept umlsConcept = (UmlsConcept) namedEntity
					.getOntologyConceptArr(0);
			String codeName = umlsConcept.getCode();
			ODIE_HistogramEntry histogramEntry = lookUpNeHistogramEntry(codeName);
			histogramEntry.setIsNer(1);
			String uri = umlsConcept.getOid();
			if (uri != null && uri.contains("#")) {
				final String[] uriParts = uri.split("#");
				uri = uriParts[1] + "/" + uriParts[0];
				histogramEntry.setOntologyUri(uri);
				histogramEntry.setFreq(histogramEntry.getFreq() + 1);
				this.nerHistogramEntryFactory
						.updateHistogramEntry(histogramEntry);
			}

		}
	}

	private ODIE_HistogramEntry lookUpNeHistogramEntry(String neCodeName) {
		ODIE_HistogramEntry result = this.nerHistogramEntryFactory
				.fetchHistogramEntry(neCodeName);
		return result;
	}

	public void collectionProcessComplete(ProcessTrace arg0)
			throws ResourceProcessException, IOException {

		this.factoryHistogramEntry = new ODIE_HistogramEntryFactory();
		this.factoryHistogramEntry.initialize(conn, databaseName, "lin");

		this.factoryMiniparTriple = new ODIE_MiniparTripleFactory();
		this.factoryMiniparTriple.initialize(conn, databaseName, "lin");

		this.factorySimilarity = new ODIE_SimilarityFactory();
		this.factorySimilarity.initialize(conn, databaseName, "lin");
		factorySimilarity.dropSimilarityTable();
		factorySimilarity.createSimilarityTable();
		factorySimilarity.createSuggestedNamedEntityTable();

		this.factoryHistogramEntry.cacheHistogramFromDatabaseById();

		System.out.println("Computing mutual information");
		computeMutualInformation();
//		pullPreComputedMutualInformation();
		System.out.println("Finished Computing mutual information");

		System.out.println("Computing categorized similarity");
		computeCategorizedSimilarity();
		System.out.println("Finished computing categorized similarity");

		// Normalize the score column
		String qualifiedTableName = databaseName + ".suggestion";
		String whereClause = "METHOD like 'Lin%'";
		ODIE_NormalizationUtils.normalizeRanges(conn, qualifiedTableName,
				whereClause, "SCORE", "SCORE");

		// Format similarity data in preparation for ROC curve plotting
		factorySimilarity.createROCCurvePrecursorTable();

	}

	@SuppressWarnings("unused")
	private void pullPreComputedMutualInformation() {
		try {
			int idx = 0;
			String sql = "select ID, WORD_ONE, RELATION, WORD_TWO, FREQ, INFO from "
					+ this.factoryMiniparTriple.getQualifiedTableName();
			PreparedStatement selectTriplesPreparedStatement = conn
					.prepareStatement(sql);
			ResultSet rs = selectTriplesPreparedStatement.executeQuery();
			while (rs.next()) {
				ODIE_MiniparTriple odieMiniparTriple = new ODIE_MiniparTriple();
				currentId = rs.getInt(1);
				String wordOne = rs.getString(2);
				String relation = rs.getString(3);
				String wordTwo = rs.getString(4);
				Long freq = rs.getLong(5);
				Double mutualInformation = rs.getDouble(6);
				odieMiniparTriple.id = currentId;
				odieMiniparTriple.wordOne = wordOne;
				odieMiniparTriple.relation = relation;
				odieMiniparTriple.wordTwo = wordTwo;
				odieMiniparTriple.freq = freq;
				odieMiniparTriple.info = mutualInformation;
				if (cachedTriplesByFirstWord.get(odieMiniparTriple.wordOne) == null) {
					cachedTriplesByFirstWord.put(odieMiniparTriple.wordOne,
							new ArrayList<ODIE_MiniparTriple>());
				}
				cachedTriplesByFirstWord.get(odieMiniparTriple.wordOne).add(
						odieMiniparTriple);
				if (idx % 10000 == 0) {
					System.out.println("Read triple " + currentId + ", "
							+ wordOne + ", " + relation + ", " + wordTwo);
				}
				idx++;
			}
			System.out.println("Finished caching triples");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void computeMutualInformation() {
		try {
			int idx = 0;
			String sql = "select ID, WORD_ONE, RELATION, WORD_TWO, FREQ from "
					+ this.factoryMiniparTriple.getQualifiedTableName();
			PreparedStatement selectTriplesPreparedStatement = conn
					.prepareStatement(sql);
			ResultSet rs = selectTriplesPreparedStatement.executeQuery();
			while (rs.next()) {
				ODIE_MiniparTriple odieMiniparTriple = new ODIE_MiniparTriple();
				currentId = rs.getInt(1);
				String wordOne = rs.getString(2);
				String relation = rs.getString(3);
				String wordTwo = rs.getString(4);
				Long freq = rs.getLong(5);
				odieMiniparTriple.id = currentId;
				odieMiniparTriple.wordOne = wordOne;
				odieMiniparTriple.relation = relation;
				odieMiniparTriple.wordTwo = wordTwo;
				odieMiniparTriple.freq = freq;
				incrementPermutationCounts(odieMiniparTriple, __r___counts,
						odieMiniparTriple.get___r___key());
				incrementPermutationCounts(odieMiniparTriple, w_r___counts,
						odieMiniparTriple.get_w_r___key());
				incrementPermutationCounts(odieMiniparTriple, __r_w_counts,
						odieMiniparTriple.get___r_w_key());
				cachedTriplesById.put(odieMiniparTriple.id, odieMiniparTriple);
				if (cachedTriplesByFirstWord.get(odieMiniparTriple.wordOne) == null) {
					cachedTriplesByFirstWord.put(odieMiniparTriple.wordOne,
							new ArrayList<ODIE_MiniparTriple>());
				}
				cachedTriplesByFirstWord.get(odieMiniparTriple.wordOne).add(
						odieMiniparTriple);
				if (idx % 10000 == 0) {
					System.out.println("Read triple " + currentId + ", "
							+ wordOne + ", " + relation + ", " + wordTwo);
				}
				idx++;
			}
			System.out.println("Finished caching triples");

			idx = 0;
			for (ODIE_MiniparTriple odieMiniparTriple : cachedTriplesById
					.values()) {
				double wrw_count = odieMiniparTriple.freq;
				double _r__count = __r___counts.get(odieMiniparTriple
						.get___r___key());
				double wr__count = w_r___counts.get(odieMiniparTriple
						.get_w_r___key());
				double _rw_count = __r_w_counts.get(odieMiniparTriple
						.get___r_w_key());
				double numerator = wrw_count * _r__count;
				double denominator = wr__count * _rw_count;
				if (denominator > 0.0d) {
					double I_w_r_w = Math.log(numerator / denominator);
					odieMiniparTriple.info = I_w_r_w;
					preparedStatementUpdateInfo = conn
							.prepareStatement("update "
									+ this.factoryMiniparTriple
											.getQualifiedTableName()
									+ " set INFO = ? where ID = ?");
					preparedStatementUpdateInfo.clearParameters();
					preparedStatementUpdateInfo.setDouble(1,
							odieMiniparTriple.info);
					preparedStatementUpdateInfo.setInt(2, odieMiniparTriple.id);
					preparedStatementUpdateInfo.executeUpdate();
					preparedStatementUpdateInfo.close();
				}
				if (idx % 10000 == 0) {
					System.out.println("Computed Info for triple "
							+ odieMiniparTriple.id + ", "
							+ odieMiniparTriple.wordOne + ", "
							+ odieMiniparTriple.relation + ", "
							+ odieMiniparTriple.wordTwo + ", "
							+ odieMiniparTriple.freq + ", "
							+ odieMiniparTriple.info);
				}
				idx++;
			}
			System.out.println("Finished computing information for triples.");
			__r___counts.clear();
			w_r___counts.clear();
			__r_w_counts.clear();
			cachedTriplesById.clear();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private ArrayList<ODIE_MiniparTriple> fetchTw(String word, String relation) {
		ArrayList<ODIE_MiniparTriple> result = new ArrayList<ODIE_MiniparTriple>();
		ArrayList<ODIE_MiniparTriple> cachedTriplesForFirstWord = cachedTriplesByFirstWord
				.get(word.toLowerCase());
		if (cachedTriplesForFirstWord != null) {
			for (ODIE_MiniparTriple triple : cachedTriplesForFirstWord) {
				if (relation.equals(CONST_WILD_CARD)) {
					result.add(triple);
				} else if (relation.endsWith(CONST_WILD_CARD)
						&& triple.relation.startsWith(relation.substring(0,
								relation.length() - 1))) {
					result.add(triple);
				} else if (triple.relation.equals(relation)) {
					result.add(triple);
				}
			}
		}
		return result;
	}

	private void incrementPermutationCounts(ODIE_MiniparTriple triple,
			HashMap<String, Integer> countMap, String key) {
		if (countMap.get(key) == null) {
			countMap.put(key, new Integer(0));
		}
		Integer incrementValue = countMap.get(key)
				+ (new Double(triple.freq)).intValue();
		countMap.put(key, incrementValue);
	}

	private void computeCategorizedSimilarity() {
		for (int idx = 0; idx < categories.length; idx++) {
			this.currentCategory = categories[idx];
			System.out.println("computeCategorizedSimilarity with "
					+ this.currentCategory);
			String[] categorizedSimilarityPatternGroup = categorizedSimilarityPatternGroups[idx];
			fillHistogramBasedOnPatternGroup(categorizedSimilarityPatternGroup);
			sortedSimilaritiesByHindler.clear();
			computeSimilarity();
			this.factorySimilarity
					.insertSimilarityBatchMode(sortedSimilaritiesByHindler);
			this.factorySimilarity
					.insertSuggestedNamedEntityTable(sortedSimilaritiesByHindler);
		}
	}

	private void fillHistogramBasedOnPatternGroup(
			String[] categorizedSimilarityPatternGroup) {
		sortedHistogram.clear();
		for (ODIE_HistogramEntry h : this.factoryHistogramEntry.cachedByIdHistogramEntries
				.values()) {
			if (ODIE_UimaUtils.filterWord(h.wordText, 2) != null) {
				for (int jdx = 0; jdx < categorizedSimilarityPatternGroup.length; jdx++) {
					String category = categorizedSimilarityPatternGroup[jdx];
					if (category.endsWith(CONST_WILD_CARD)) {
						String categoryWithoutWildcard = category.substring(0,
								category.length() - 1);
						if (h.pos.startsWith(categoryWithoutWildcard)) {
							sortedHistogram.add(h);
							break;
						}
					} else if (h.pos.equals(category)) {
						sortedHistogram.add(h);
						break;
					}
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private void displaySortedHistogram() {
		int jdx = 0;
		for (ODIE_HistogramEntry h : sortedHistogram) {
			if (jdx % 100 == 0) {
				logger.debug(h);
			}
			jdx++;
		}
	}

	private void displayMiniparTriplePairList(String label,
			ArrayList<ODIE_MiniparTriplePair> triplePairList) {
		logger.debug(label);
		for (ODIE_MiniparTriplePair triplePair : triplePairList) {
			logger.debug(triplePair);
		}
	}

	private void displayMiniparTripleList(String label,
			ArrayList<ODIE_MiniparTriple> tripleList) {
		logger.debug(label);
		for (ODIE_MiniparTriple triple : tripleList) {
			logger.debug(triple);
		}
	}

	private void computeSimilarity() {
		int idx = 0;
		Iterator<ODIE_HistogramEntry> iter1 = sortedHistogram.iterator();
		while (iter1.hasNext()) {
			ODIE_HistogramEntry h1 = iter1.next();
			String currentWordOne = h1.wordText;
			SortedSet<ODIE_HistogramEntry> tailSet = sortedHistogram.tailSet(
					h1, false);
			if (tailSet != null && !tailSet.isEmpty()) {
				Iterator<ODIE_HistogramEntry> iter2 = tailSet.iterator();
				while (iter2.hasNext()) {
					ODIE_HistogramEntry h2 = iter2.next();
					String currentWordTwo = h2.wordText;
					// logger.debug("Computing similarity between '" +
					// currentWordOne + "' and '" + currentWordTwo + "'") ;
					boolean isDisplayingDiagnostics = false;
					// if (currentWordOne.equalsIgnoreCase("copd") &&
					// currentWordTwo.equalsIgnoreCase("prostatitis")) {
					// logger.debug("Computing similarity between 'copd' and 'prostatitis'")
					// ;
					// isDisplayingDiagnostics = true ;
					// }
					boolean isDistinctWords = !currentWordOne
							.equalsIgnoreCase(currentWordTwo);
					//
					// The default behavior is to only output NERs vs nonNERs.
					// However if isWriteGapsOnly is turned
					// off. (i.e., isWriteGapsOnly == false) then we will write
					// out all similarity pairs
					//
					boolean isVocabularyGap = !isWriteGapsOnly()
							|| representsVocabularityGap(h1, h2);
					// logger.debug("isDistinctWords == " + isDistinctWords) ;
					// logger.debug("isVocabularyGap == " + isVocabularyGap) ;
					if (isDistinctWords && isVocabularyGap) {
						ArrayList<ODIE_MiniparTriple> Tw1 = fetchTw(
								currentWordOne, CONST_WILD_CARD);
						ArrayList<ODIE_MiniparTriple> Tw2 = fetchTw(
								currentWordTwo, CONST_WILD_CARD);
						ArrayList<ODIE_MiniparTriplePair> Tw1_intersect_Tw2 = computeIntersection(
								Tw1, Tw2);

						if (isDisplayingDiagnostics) {
							displayMiniparTriplePairList("Tw1_intersect_Tw2",
									Tw1_intersect_Tw2);
							displayMiniparTripleList("Tw1", Tw1);
							displayMiniparTripleList("Tw2", Tw2);
						}

						// double scoreSimilarity = computeSimilarity(
						// Tw1_intersect_Tw2, Tw1, Tw2);
						double scoreSimilarity = computeSimilarityHindleExplicitSubset(
								currentWordOne, currentWordTwo,
								this.currentKaiSet);
						double scoreHindle = computeSimilarityHindle(
								currentWordOne, currentWordTwo);
						double scoreHindleR = computeSimilarityHindleR(
								Tw1_intersect_Tw2, Tw1, Tw2);
						double scoreCosine = computeSimilarityCosine(
								Tw1_intersect_Tw2, Tw1, Tw2);
						double scoreDice = computeSimilarityDice(
								Tw1_intersect_Tw2, Tw1, Tw2);
						double scoreJacard = computeSimilarityJacard(
								Tw1_intersect_Tw2, Tw1, Tw2);
						if (scoreSimilarity > 0.0d || scoreHindle > 0.0d
								|| scoreHindleR > 0.0d || scoreCosine > 0.0d
								|| scoreDice > 0.0d || scoreJacard > 0.0d) {
							if (h1.getIsNer() == 0) {
								insertSimilarity(this.currentCategory,
										currentWordOne, currentWordTwo,
										scoreSimilarity, scoreHindle,
										scoreHindleR, scoreCosine, scoreDice,
										scoreJacard, h2.ontologyUri);
							} else {
								insertSimilarity(this.currentCategory,
										currentWordTwo, currentWordOne,
										scoreSimilarity, scoreHindle,
										scoreHindleR, scoreCosine, scoreDice,
										scoreJacard, h1.ontologyUri);
							}
						}
					}

				}
				if (idx % 100 == 0) {
					System.out.println("Computed similarity for  " + idx
							+ " of " + sortedHistogram.size());
				}
				idx++;

			}
		}

	}

	private boolean representsVocabularityGap(ODIE_HistogramEntry h1,
			ODIE_HistogramEntry h2) {
		boolean result = false;
		if (h1.isNp == 1 && h2.isNp == 1) {
			if (h1.isNer == 1 && h2.isNer == 0) {
				result = true;
			} else if (h2.isNer == 1 && h1.isNer == 0) {
				result = true;
			}
		}
		return result;
	}

	private double sumOfMutualInformationOverTriplePairs(
			ArrayList<ODIE_MiniparTriplePair> tw) {
		double result = 0.0d;
		for (ODIE_MiniparTriplePair pair : tw) {
			result += Math.max(0.0d, pair.tripleOne.info);
			result += Math.max(0.0d, pair.tripleTwo.info);
		}
		return result;
	}

	private double sumOfMutualInformationOverTriples(
			ArrayList<ODIE_MiniparTriple> tw) {
		double result = 0.0d;
		for (ODIE_MiniparTriple triple : tw) {
			result += Math.max(0.0d, triple.info);
		}
		return result;
	}

	@SuppressWarnings("unused")
	private double computeSimilarity(
			ArrayList<ODIE_MiniparTriplePair> tw1_intersect_tw2,
			ArrayList<ODIE_MiniparTriple> tw1, ArrayList<ODIE_MiniparTriple> tw2) {
		double sum_tw1_intersect_tw2 = sumOfMutualInformationOverTriplePairs(tw1_intersect_tw2);
		double sum_tw1 = sumOfMutualInformationOverTriples(tw1);
		double sum_tw2 = sumOfMutualInformationOverTriples(tw2);
		double denom = sum_tw1 + sum_tw2;
		double result = (denom > 0.0d || denom < 0.0d) ? sum_tw1_intersect_tw2
				/ denom : 0.0d;
		return result;
	}

	private double computeSimilarityHindle(String currentWordOne,
			String currentWordTwo) {
		double similarityHindle = 0.0d;
		ArrayList<ODIE_MiniparTriple> Tw1 = fetchTw(currentWordOne, "s");
		ArrayList<ODIE_MiniparTriple> Tw2 = fetchTw(currentWordTwo, "s");
		Tw1.addAll(fetchTw(currentWordOne, "obj%"));
		Tw2.addAll(fetchTw(currentWordTwo, "obj%"));
		ArrayList<ODIE_MiniparTriplePair> Tw1_intersect_Tw2 = computeIntersection(
				Tw1, Tw2);
		for (ODIE_MiniparTriplePair pair : Tw1_intersect_Tw2) {
			similarityHindle += pair.getMinimumInfo();
		}
		return similarityHindle;
	}

	private double computeSimilarityHindleExplicitSubset(String currentWordOne,
			String currentWordTwo, String[] relations) {
		double similarityHindle = 0.0d;
		ArrayList<ODIE_MiniparTriple> Tw1 = null;
		ArrayList<ODIE_MiniparTriple> Tw2 = null;
		for (String relation : relations) {
			if (Tw1 == null) {
				Tw1 = fetchTw(currentWordOne, relation);
				Tw2 = fetchTw(currentWordTwo, relation);
			} else {
				Tw1.addAll(fetchTw(currentWordOne, relation));
				Tw2.addAll(fetchTw(currentWordTwo, relation));
			}
		}
		ArrayList<ODIE_MiniparTriplePair> Tw1_intersect_Tw2 = computeIntersection(
				Tw1, Tw2);
		for (ODIE_MiniparTriplePair pair : Tw1_intersect_Tw2) {
			similarityHindle += pair.getMinimumInfo();
		}
		return similarityHindle;
	}

	private double computeSimilarityHindleR(
			ArrayList<ODIE_MiniparTriplePair> Tw1_intersect_Tw2,
			ArrayList<ODIE_MiniparTriple> Tw1, ArrayList<ODIE_MiniparTriple> Tw2) {
		double similarityHindleR = 0.0d;
		for (ODIE_MiniparTriplePair pair : Tw1_intersect_Tw2) {
			similarityHindleR += pair.getMinimumInfo();
		}
		return similarityHindleR;
	}

	private double computeSimilarityCosine(
			ArrayList<ODIE_MiniparTriplePair> tw1_intersect_tw2,
			ArrayList<ODIE_MiniparTriple> tw1, ArrayList<ODIE_MiniparTriple> tw2) {
		double count_tw1_intersect_tw2 = tw1_intersect_tw2.size();
		double count_tw1 = tw1.size();
		double count_tw2 = tw2.size();
		double denom_squared = count_tw1 + count_tw2;
		double denom = Math.pow(denom_squared, 0.5d);
		double result = (denom > 0.0d || denom < 0.0d) ? count_tw1_intersect_tw2
				/ denom
				: 0.0d;
		return result;
	}

	private double computeSimilarityDice(
			ArrayList<ODIE_MiniparTriplePair> tw1_intersect_tw2,
			ArrayList<ODIE_MiniparTriple> tw1, ArrayList<ODIE_MiniparTriple> tw2) {
		double count_tw1_intersect_tw2 = tw1_intersect_tw2.size();
		double numerator = 2.0d * count_tw1_intersect_tw2;
		double count_tw1 = tw1.size();
		double count_tw2 = tw2.size();
		double denom = count_tw1 + count_tw2;
		double result = (denom > 0.0d || denom < 0.0d) ? numerator / denom
				: 0.0d;
		return result;
	}

	private double computeSimilarityJacard(
			ArrayList<ODIE_MiniparTriplePair> tw1_intersect_tw2,
			ArrayList<ODIE_MiniparTriple> tw1, ArrayList<ODIE_MiniparTriple> tw2) {
		double count_tw1_intersect_tw2 = tw1_intersect_tw2.size();
		double numerator = count_tw1_intersect_tw2;
		double count_tw1 = tw1.size();
		double count_tw2 = tw2.size();
		double denom = count_tw1 + count_tw2 - count_tw1_intersect_tw2;
		double result = (denom > 0.0d || denom < 0.0d) ? numerator / denom
				: 0.0d;
		return result;
	}

	private ArrayList<ODIE_MiniparTriplePair> computeIntersection(
			ArrayList<ODIE_MiniparTriple> Tw1, ArrayList<ODIE_MiniparTriple> Tw2) {
		ArrayList<ODIE_MiniparTriplePair> pairs = new ArrayList<ODIE_MiniparTriplePair>();
		ODIE_MiniparTripleTwComparator comparator = new ODIE_MiniparTripleTwComparator();
		Collections.sort(Tw1, comparator);
		Collections.sort(Tw2, comparator);
		int idx = 0;
		int jdx = 0;
		while (idx < Tw1.size() && jdx < Tw2.size()) {
			ODIE_MiniparTriple tw1Entry = Tw1.get(idx);
			ODIE_MiniparTriple tw2Entry = Tw2.get(jdx);
			if (comparator.compare(tw1Entry, tw2Entry) < 0) {
				idx++;
			} else if (comparator.compare(tw1Entry, tw2Entry) > 0) {
				jdx++;
			} else {
				ODIE_MiniparTriplePair pair = new ODIE_MiniparTriplePair();
				pair.tripleOne = tw1Entry;
				pair.tripleTwo = tw2Entry;
				pairs.add(pair);
				idx++;
				jdx++;
			}

		}

		return pairs;
	}

	private void insertSimilarity(String category, String wordOne,
			String wordTwo, double scoreSimilarity, double scoreHindle,
			double scoreHindleR, double scoreCosine, double scoreDice,
			double scoreJacard, String ontologyUri) {
		wordOne = ODIE_UimaUtils.filterTerm(wordOne, 2);
		wordTwo = ODIE_UimaUtils.filterTerm(wordTwo, 2);
		if (wordOne != null && wordTwo != null) {
			ODIE_Similarity odieSimilarity = new ODIE_Similarity();
			odieSimilarity.id = similarityIndex++;
			odieSimilarity.category = category;
			odieSimilarity.wordOne = wordOne;
			odieSimilarity.wordTwo = wordTwo;
			odieSimilarity.similarity = scoreSimilarity;
			odieSimilarity.hindle = scoreHindle;
			odieSimilarity.hindler = scoreHindleR;
			odieSimilarity.cosine = scoreCosine;
			odieSimilarity.dice = scoreDice;
			odieSimilarity.jacard = scoreJacard;
			odieSimilarity.ontologyUri = ontologyUri;
			sortedSimilaritiesByHindler.add(odieSimilarity);
			if (sortedSimilaritiesByHindler.size() > CONST_MAX_SIMILARITIES) {
				ODIE_Similarity lastSimilarity = sortedSimilaritiesByHindler
						.last();
				sortedSimilaritiesByHindler.remove(lastSimilarity);
			}
		}

	}

	public boolean isWriteGapsOnly() {
		return isWriteGapsOnly;
	}

	public void setWriteGapsOnly(boolean isWriteGapsOnly) {
		this.isWriteGapsOnly = isWriteGapsOnly;
	}

	public void destroy() {
		if (conn == null)
			return;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
