package edu.pitt.dbmi.odie.uima.dekanlin.consumer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import edu.pitt.dbmi.odie.uima.util.ODIE_IFConstants;

public class ODIE_MiniparStatisticsGeneratorWithCaching extends
		CasConsumer_ImplBase {

	private static final long serialVersionUID = 1L;

	/* LOG4J logger based on class name */
	private Logger logger = Logger.getLogger(getClass().getName());

	public static final String CONST_WILD_CARD = "%";

	private Connection conn = null;

	private long startTime;

	private String currentPosTaggerAlgorithm = "cTAKES";

	private String[] categories = { "Noun", "Verb", "AdjOrAdv" };
	private String currentCategory;

	private String[][] categorizedSimilarityPatternGroups = { { "N%" },
			{ "V%" }, { "J%", "R%" } };

	private int currentId = -1;
	private PreparedStatement preparedStatementInsertSimilarity;
	private PreparedStatement preparedStatementUpdateInfo = null;

	private String databaseName = "lin_analysis";

	private static final HashMap<Integer, ODIE_MiniparTriple> cachedTriples = new HashMap<Integer, ODIE_MiniparTriple>();

	private static final HashMap<String, ArrayList<ODIE_MiniparTriple>> cachedTriplesByFirstWord = new HashMap<String, ArrayList<ODIE_MiniparTriple>>();

	private static final TreeSet<ODIE_Similarity> sortedSimilaritiesByHindler = new TreeSet<ODIE_Similarity>(
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

	private static final HashMap<Integer, ODIE_Histogram> cachedHistogram = new HashMap<Integer, ODIE_Histogram>();

	private static final TreeSet<ODIE_Histogram> sortedHistogram = new TreeSet<ODIE_Histogram>(
			new Comparator<ODIE_Histogram>() {
				public int compare(ODIE_Histogram o1, ODIE_Histogram o2) {
					return o1.word.compareTo(o2.word);
				}
			});

	private static final HashMap<String, Integer> __r___counts = new HashMap<String, Integer>();

	private static final HashMap<String, Integer> w_r___counts = new HashMap<String, Integer>();

	private static final HashMap<String, Integer> __r_w_counts = new HashMap<String, Integer>();

	private static int similarityIndex = 1;

	private static final int CONST_MAX_SIMILARITIES = 20000;

	public static void main(String[] args) {
		(new ODIE_MiniparStatisticsGeneratorWithCaching()).runStandAlone();
	}

	public ODIE_MiniparStatisticsGeneratorWithCaching() {
	}

	public void runStandAlone() {
		startTime = System.currentTimeMillis();
		standAloneConfigJdbcInit();
		createSimilarityTable();
		prepareStatements();
		cacheHistogram();
		computeMutualInformation();
		computeCategorizedSimilarity();
		insertSimilarityBatchMode();
	}

	private void prepareStatements() {
		try {
			String sql = null;

			// sql =
			// "select ID, WORD_ONE, RELATION, WORD_TWO, FREQ from lin_triples where ID > ? order by ID limit 1";
			// conn.prepareStatement(sql);

			// sql =
			// "select sum(FREQ) from lin_triples where WORD_ONE like ? and RELATION like ? and WORD_TWO like ?";
			// conn.prepareStatement(sql);

			sql = "update lin_triples set INFO = ? where ID = ?";
			preparedStatementUpdateInfo = conn.prepareStatement(sql);

			// sql = "select min(WORD_ONE) from lin_triples";
			// conn.prepareStatement(sql);

			// sql =
			// "select min(WORD_ONE) from lin_triples where WORD_ONE > ? ;";
			// conn
			// .prepareStatement(sql);

			// sql =
			// "select * from lin_triples where WORD_ONE = ? and RELATION like ? and INFO > 0.0";
			// conn.prepareStatement(sql);

			sql = "insert into lin_similarity (CATEGORY, WORD_ONE, WORD_TWO, SIMILARITY, HINDLE, HINDLER, COSINE, DICE, JACARD) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			preparedStatementInsertSimilarity = conn.prepareStatement(sql);

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

	private void computeMutualInformation() {
		try {
			int idx = 0;
			String sql = "select ID, WORD_ONE, RELATION, WORD_TWO, FREQ from lin_triples";
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
				cachedTriples.put(odieMiniparTriple.id, odieMiniparTriple);
				if (cachedTriplesByFirstWord.get(odieMiniparTriple.wordOne) == null) {
					cachedTriplesByFirstWord.put(odieMiniparTriple.wordOne,
							new ArrayList<ODIE_MiniparTriple>());
				}
				cachedTriplesByFirstWord.get(odieMiniparTriple.wordOne).add(
						odieMiniparTriple);
				if (idx % 10000 == 0) {
					logger.debug("Read triple " + currentId + ", " + wordOne
							+ ", " + relation + ", " + wordTwo);
				}
				idx++;
			}
			logger.debug("Finished caching triples");

			idx = 0;
			for (ODIE_MiniparTriple odieMiniparTriple : cachedTriples.values()) {
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
					preparedStatementUpdateInfo.clearParameters();
					preparedStatementUpdateInfo.setDouble(1,
							odieMiniparTriple.info);
					preparedStatementUpdateInfo.setInt(2, odieMiniparTriple.id);
					preparedStatementUpdateInfo.executeUpdate();
				}
				if (idx % 10000 == 0) {
					logger.debug("Computed Info for triple "
							+ odieMiniparTriple.id + ", "
							+ odieMiniparTriple.wordOne + ", "
							+ odieMiniparTriple.relation + ", "
							+ odieMiniparTriple.wordTwo + ", "
							+ odieMiniparTriple.freq + ", "
							+ odieMiniparTriple.info);
				}
				idx++;
			}
			logger.debug("Finished computing information for triples.");
			__r___counts.clear();
			w_r___counts.clear();
			__r_w_counts.clear();
			cachedTriples.clear();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void computeCategorizedSimilarity() {
		for (int idx = 0; idx < categories.length; idx++) {
			this.currentCategory = categories[idx];
			String[] categorizedSimilarityPatternGroup = categorizedSimilarityPatternGroups[idx];
			fillHistogramBasedOnPatternGroup(categorizedSimilarityPatternGroup);
			sortedSimilaritiesByHindler.clear();
			computeSimilarity();
			insertSimilarityBatchMode();
		}
	}

	private void fillHistogramBasedOnPatternGroup(
			String[] categorizedSimilarityPatternGroup) {
		sortedHistogram.clear();
		for (ODIE_Histogram h : cachedHistogram.values()) {
			if (h.word.toLowerCase().matches("^[a-z-]+$")
					&& h.word.length() > 2) {
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

	private void displaySortedHistogram() {
		int jdx = 0;
		for (ODIE_Histogram h : sortedHistogram) {
			if (jdx % 100 == 0) {
				logger.debug(h);
			}
			jdx++;
		}
	}

	private void computeSimilarity() {
		int idx = 0;
		Iterator<ODIE_Histogram> iter1 = sortedHistogram.iterator();
		while (iter1.hasNext()) {
			ODIE_Histogram h1 = iter1.next();
			String currentWordOne = h1.word;
			SortedSet<ODIE_Histogram> tailSet = sortedHistogram.tailSet(h1,
					false);
			if (tailSet != null && !tailSet.isEmpty()) {
				Iterator<ODIE_Histogram> iter2 = tailSet.iterator();
				while (iter2.hasNext()) {
					ODIE_Histogram h2 = iter2.next();
					String currentWordTwo = h2.word;
					if (!currentWordOne.equalsIgnoreCase(currentWordTwo)) {
						ArrayList<ODIE_MiniparTriple> Tw1 = fetchTw(
								currentWordOne, CONST_WILD_CARD);
						ArrayList<ODIE_MiniparTriple> Tw2 = fetchTw(
								currentWordTwo, CONST_WILD_CARD);
						ArrayList<ODIE_MiniparTriplePair> Tw1_intersect_Tw2 = computeIntersection(
								Tw1, Tw2);

						double scoreSimilarity = computeSimilarity(
								Tw1_intersect_Tw2, Tw1, Tw2);
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
							insertSimilarity(this.currentCategory,
									currentWordOne, currentWordTwo,
									scoreSimilarity, scoreHindle, scoreHindleR,
									scoreCosine, scoreDice, scoreJacard);
						}
					}

				}
				if (idx % 10 == 0) {
					logger.debug("Computed similarity for  " + idx + " of "
							+ sortedHistogram.size());
				}
				idx++;

			}
		}

	}

	private void standAloneConfigJdbcInit() {
		try {
			String driver = (String) System
					.getProperty(ODIE_IFConstants.PARAM_DB_DRIVER);
			String url = (String) System
					.getProperty(ODIE_IFConstants.IF_PARAM_DB_URL);
			String userName = (String) System
					.getProperty(ODIE_IFConstants.PARAM_DB_USERNAME);
			String password = (String) System
					.getProperty(ODIE_IFConstants.PARAM_DB_PASSWORD);
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url, userName, password);
			logger.info("Connected to the database at:" + url);
			this.databaseName = extractDatabaseNameFromDbUrl(url);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void initialize() throws ResourceInitializationException {
		try {
			configJdbcInit();

			this.currentPosTaggerAlgorithm = (String) getConfigParameterValue(ODIE_IFConstants.IF_PARAM_POS_TAGGER);

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

	private void configJdbcInit() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		String driver = (String) getConfigParameterValue(ODIE_IFConstants.PARAM_DB_DRIVER);
		String url = (String) getConfigParameterValue(ODIE_IFConstants.IF_PARAM_DB_URL);
		String userName = (String) getConfigParameterValue(ODIE_IFConstants.PARAM_DB_USERNAME);
		String password = (String) getConfigParameterValue(ODIE_IFConstants.PARAM_DB_PASSWORD);
		Class.forName(driver).newInstance();
		conn = DriverManager.getConnection(url, userName, password);
		logger.info("Connected to the database at:" + url);
		this.databaseName = extractDatabaseNameFromDbUrl(url);
	}

	private String extractDatabaseNameFromDbUrl(String dbUrl) {
		int sPos = dbUrl.lastIndexOf("/");
		String result = dbUrl.substring(sPos + 1, dbUrl.length());
		return result;
	}

	public void processCas(CAS acas) throws ResourceProcessException {
	}

	public void collectionProcessComplete(ProcessTrace arg0)
			throws ResourceProcessException, IOException {
		createSimilarityTable();
		prepareStatements();
		cacheHistogram();
		computeMutualInformation();
		computeCategorizedSimilarity();
	}

	private double sumOfMutualInformationOverTriplePairs(
			ArrayList<ODIE_MiniparTriplePair> tw) {
		double result = 0.0d;
		for (ODIE_MiniparTriplePair pair : tw) {
			result += Math.max(0.0d, pair.tripleOne.info);
			result += Math.max(0.0d, pair.tripleTwo.info);
		}
		return result ;
	}
	
	private double sumOfMutualInformationOverTriples(
			ArrayList<ODIE_MiniparTriple> tw) {
		double result = 0.0d;
		for (ODIE_MiniparTriple triple : tw) {
			result += Math.max(0.0d, triple.info);
		}
		return result ;
	}

	private double computeSimilarity(
			ArrayList<ODIE_MiniparTriplePair> tw1_intersect_tw2,
			ArrayList<ODIE_MiniparTriple> tw1, ArrayList<ODIE_MiniparTriple> tw2) {
		double sum_tw1_intersect_tw2 = sumOfMutualInformationOverTriplePairs(tw1_intersect_tw2) ;
		double sum_tw1 = sumOfMutualInformationOverTriples(tw1) ;
		double sum_tw2 = sumOfMutualInformationOverTriples(tw2) ;
		double denom = sum_tw1 + sum_tw2;
		double result = (denom > 0.0d || denom < 0.0d) ? sum_tw1_intersect_tw2
				/ denom
				: 0.0d;
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
			double scoreJacard) {

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
		sortedSimilaritiesByHindler.add(odieSimilarity);
		if (sortedSimilaritiesByHindler.size() > CONST_MAX_SIMILARITIES) {
			ODIE_Similarity lastSimilarity = sortedSimilaritiesByHindler.last();
			sortedSimilaritiesByHindler.remove(lastSimilarity);
		}
	}

	private void insertSimilarityBatchMode() {
		try {
			int idx = 0;
			for (ODIE_Similarity odieSimilarity : sortedSimilaritiesByHindler) {
				preparedStatementInsertSimilarity.clearParameters();
				preparedStatementInsertSimilarity.setString(1,
						odieSimilarity.category);
				preparedStatementInsertSimilarity.setString(2,
						odieSimilarity.wordOne);
				preparedStatementInsertSimilarity.setString(3,
						odieSimilarity.wordTwo);
				preparedStatementInsertSimilarity.setDouble(4,
						odieSimilarity.similarity);
				preparedStatementInsertSimilarity.setDouble(5,
						odieSimilarity.hindle);
				preparedStatementInsertSimilarity.setDouble(6,
						odieSimilarity.hindler);
				preparedStatementInsertSimilarity.setDouble(7,
						odieSimilarity.cosine);
				preparedStatementInsertSimilarity.setDouble(8,
						odieSimilarity.dice);
				preparedStatementInsertSimilarity.setDouble(9,
						odieSimilarity.jacard);
				preparedStatementInsertSimilarity.executeUpdate();
				if (idx % 100 == 0) {
					logger.debug("ODIE_Association: saved " + idx + " of "
							+ sortedSimilaritiesByHindler.size() + " records");
				}
				idx++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void destroy() {
		if(conn==null)
			return;
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	private void createSimilarityTable() {
		// Databases typically use user-names and passwords; these can
		// be passed as //properties to the getConnection method.

		// drop the table in case it's already present
		// This isn't needed because we're starting from an empty
		// database,
		// but leave here for tutorial reasons
		Statement sqlStmt = null;
		try {
			sqlStmt = conn.createStatement();
			sqlStmt.execute("drop table if exists " + this.databaseName
					+ ".lin_similarity");
			sqlStmt.close();

			StringBuffer sb = new StringBuffer();
			sb.append("CREATE TABLE IF NOT EXISTS  " + this.databaseName
					+ ".lin_similarity (\n");
			sb.append("  ID bigint(20) NOT NULL AUTO_INCREMENT,\n");
			sb
					.append("  CATEGORY varchar(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb
					.append("  WORD_ONE varchar(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb
					.append("  WORD_TWO varchar(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb.append("  SIMILARITY decimal(11,2) NOT NULL DEFAULT 0.00,\n");
			sb.append("  HINDLE decimal(11,2) NOT NULL DEFAULT 0.00,\n");
			sb.append("  HINDLEr decimal(11,2) NOT NULL DEFAULT 0.00,\n");
			sb.append("  COSINE decimal(11,2) NOT NULL DEFAULT 0.00,\n");
			sb.append("  DICE decimal(11,2) NOT NULL DEFAULT 0.00,\n");
			sb.append("  JACARD decimal(11,2) NOT NULL DEFAULT 0.00,\n");
			sb.append("  PRIMARY KEY (ID),\n");
			sb.append("  KEY INDEX_WORD (WORD_ONE, WORD_TWO)\n");
			sb
					.append(") ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC");
			sqlStmt = conn.createStatement();
			sqlStmt.execute(sb.toString());
			sqlStmt.close();

			logger
					.debug("Time: "
							+ (System.currentTimeMillis() - startTime)
							+ " ODIE_MiniparStatisticsGenerator: Created the SIMILARITY table.");

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	class ODIE_MiniparTriplePair {
		public ODIE_MiniparTriple tripleOne;
		public ODIE_MiniparTriple tripleTwo;

		public ODIE_MiniparTriplePair() {
			;
		}

		public double getMinimumInfo() {
			return Math.min(tripleOne.info, tripleTwo.info);
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("Triple Pair: ");
			sb.append(tripleOne.toString());
			sb.append(", ");
			sb.append(tripleTwo.toString());
			return sb.toString();
		}
	}

	class ODIE_MiniparTriple {
		public int id;
		public String wordOne;
		public String relation;
		public String wordTwo;
		public double freq;
		public double info;

		public ODIE_MiniparTriple() {
		}

		public ODIE_MiniparTriple(int id, String wordOne, String relation,
				String wordTwo, double freq, double info) {
			this.id = id;
			this.wordOne = wordOne;
			this.relation = relation;
			this.wordTwo = wordTwo;
			this.freq = freq;
			this.info = info;
		}

		public String get___r___key() {
			return relation;
		}

		public String get_w_r___key() {
			return wordOne + ":" + relation;
		}

		public String get___r_w_key() {
			String result = relation + ":" + wordTwo;
			return result;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(this.id);
			sb.append(", ");
			sb.append(this.wordOne);
			sb.append(", ");
			sb.append(this.relation);
			sb.append(", ");
			sb.append(this.wordTwo);
			sb.append(", ");
			sb.append(this.freq);
			sb.append(", ");
			sb.append(this.info);
			return sb.toString();
		}
	}

	class ODIE_MiniparTripleTwComparator implements
			Comparator<ODIE_MiniparTriple> {
		public int compare(ODIE_MiniparTriple tripleOne,
				ODIE_MiniparTriple tripleTwo) {
			String relOne = tripleOne.relation;
			String relTwo = tripleTwo.relation;
			String wordOne = tripleOne.wordTwo;
			String wordTwo = tripleTwo.wordTwo;
			int compareRelations = relOne.compareTo(relTwo);
			int compareWords = wordOne.compareTo(wordTwo);
			return (compareRelations == 0) ? compareWords : compareRelations;
		}
	}

	class ODIE_MiniparTripleIdComparator implements
			Comparator<ODIE_MiniparTriple> {

		public int compare(ODIE_MiniparTriple tripleOne,
				ODIE_MiniparTriple tripleTwo) {

			int idOne = tripleOne.id;
			int idTwo = tripleTwo.id;

			if (idOne > idTwo)

				return 1;

			else if (idOne < idTwo)

				return -1;

			else

				return 0;

		}

	}

	class ODIE_Similarity {
		public Integer id;
		public String category;
		public String wordOne;
		public String wordTwo;
		public Double similarity;
		public Double hindle;
		public Double hindler;
		public Double cosine;
		public Double dice;
		public Double jacard;

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("id = " + id);
			sb.append(", ");
			sb.append("wordOne = " + wordOne);
			sb.append(", ");
			sb.append("wordTwo = " + wordTwo);
			sb.append(", ");
			sb.append("hindler = " + hindler);
			sb.append("\n ");
			return sb.toString();
		}
	}

	private void cacheHistogram() {
		try {
			String sql = "select id, word, pos, tagger, freq from lin_histogram";
			PreparedStatement selectHistogramPreparedStatement = conn
					.prepareStatement(sql);
			ResultSet rs = selectHistogramPreparedStatement.executeQuery();
			while (rs.next()) {
				ODIE_Histogram h = new ODIE_Histogram();
				h.id = rs.getInt("id");
				h.word = rs.getString("word");
				h.pos = rs.getString("pos");
				h.tagger = rs.getString("tagger");
				h.freq = rs.getInt("freq");
				cachedHistogram.put(h.id, h);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	class ODIE_Histogram {
		public Integer id;
		public String word;
		public String pos;
		public String tagger;
		public Integer freq;

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("id = " + id + ", ");
			sb.append("word = " + word + ", ");
			sb.append("pos = " + pos + ", ");
			sb.append("tagger = " + tagger + ", ");
			sb.append("freq = " + freq);
			return sb.toString();
		}
	}

}
