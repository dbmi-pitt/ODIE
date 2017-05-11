package edu.pitt.dbmi.odie.gate.pr.dekanlin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ODIE_MiniparStatisticsGenerator {

	/**
	 * JDBC connection information for RDBMS
	 */
	public static final String KEY_DB_DRIVER = "odie.db.driver";
	public static final String KEY_DB_URL = "odie.db.url";
	public static final String KEY_DB_USER_NAME = "odie.db.user.name";
	public static final String KEY_DB_USER_PASSWORD = "odie.db.user.password";
	public static final String KEY_LANGUAGE = "odie.language";

	private Connection conn;

	private long startTime;
	
	private String currentPosTaggerAlgorithm = "HEPPLE" ;

	private String[] categories = { "Noun", "Verb", "AdjOrAdv" } ;
	private String currentCategory;
	
	private String[][] categorizedSimilarityPatternGroups = { { "N%" },
			{ "V%" }, { "J%", "R%" } };

	private int currentId = -1;
	private PreparedStatement preparedStatementTripleCursor = null;
	private PreparedStatement preparedStatementCardinality = null;
	private PreparedStatement preparedStatementUpdateInfo = null;
	private PreparedStatement preparedStatementFetchMinimumWord = null;
	private PreparedStatement preparedStatementFetchNextSequentialWord = null;
	private PreparedStatement preparedStatementFetchTw;
	private PreparedStatement preparedStatementInsertSimilarity;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ODIE_MiniparStatisticsGenerator();
	}

	public ODIE_MiniparStatisticsGenerator() {
		startTime = System.currentTimeMillis();

		openDatabaseConnection();
		createSimilarityTable();
		prepareStatements();
		computeMutualInformation();
		computeCategorizedSimilarity();
	}
	
	private void computeCategorizedSimilarity() {
		for (int idx = 0; idx < categorizedSimilarityPatternGroups.length; idx++) {
			this.currentCategory = categories[ idx ] ;
			String[] categorizedSimilarityPatternGroup = categorizedSimilarityPatternGroups[idx];
			prepareCursorStatements(categorizedSimilarityPatternGroup);
			computeSimilarity();
		}
	}

	private void computeSimilarity() {
		String currentWordOne = fetchMininumWord();
		do {
			String currentWordTwo = fetchNextSequentialWord(currentWordOne);
			while (currentWordTwo != null) {

				ArrayList<ODIE_MiniparTriple> Tw1 = fetchTw(currentWordOne, "%");
				ArrayList<ODIE_MiniparTriple> Tw2 = fetchTw(currentWordTwo, "%");
				ArrayList<ODIE_MiniparTriplePair> Tw1_intersect_Tw2 = computeIntersection(
						Tw1, Tw2);

				double scoreSimilarity = computeSimilarity(Tw1_intersect_Tw2,
						Tw1, Tw2);
				double scoreHindle = computeSimilarityHindle(currentWordOne,
						currentWordTwo);
				double scoreHindleR = computeSimilarityHindleR(
						Tw1_intersect_Tw2, Tw1, Tw2);
				double scoreCosine = computeSimilarityCosine(Tw1_intersect_Tw2,
						Tw1, Tw2);
				double scoreDice = computeSimilarityDice(Tw1_intersect_Tw2,
						Tw1, Tw2);
				double scoreJacard = computeSimilarityJacard(Tw1_intersect_Tw2,
						Tw1, Tw2);
				if (scoreSimilarity > 0.0d || scoreHindle > 0.0d
						|| scoreHindleR > 0.0d || scoreCosine > 0.0d
						|| scoreDice > 0.0d || scoreJacard > 0.0d) {
					insertSimilarity(this.currentCategory, currentWordOne, currentWordTwo,
							scoreSimilarity, scoreHindle, scoreHindleR,
							scoreCosine, scoreDice, scoreJacard);
				}
				currentWordTwo = fetchNextSequentialWord(currentWordTwo);
			}
			currentWordOne = fetchNextSequentialWord(currentWordOne);
		} while (currentWordOne != null);

	}

	private double computeSimilarity(
			ArrayList<ODIE_MiniparTriplePair> tw1_intersect_tw2,
			ArrayList<ODIE_MiniparTriple> tw1, ArrayList<ODIE_MiniparTriple> tw2) {
		double count_tw1_intersect_tw2 = tw1_intersect_tw2.size();
		double count_tw1 = tw1.size();
		double count_tw2 = tw2.size();
		double denom = count_tw1 + count_tw2;
		double result = (denom > 0.0d || denom < 0.0d) ? count_tw1_intersect_tw2
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
		Tw1.addAll(fetchTw(currentWordOne, "obj%"));
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

	private String fetchNextSequentialWord(String currentWord) {
		String result = null;
		try {
			preparedStatementFetchNextSequentialWord.clearParameters();
			preparedStatementFetchNextSequentialWord.setString(1, currentWord);
			ResultSet rs = preparedStatementFetchNextSequentialWord
					.executeQuery();
			if (rs.next()) {
				result = rs.getString(1);
			}
		} catch (SQLException e) {
			result = null;
			e.printStackTrace();
		}
		return result;
	}

	private String fetchMininumWord() {
		String result = null;
		try {
			preparedStatementFetchMinimumWord.clearParameters();
			ResultSet rs = preparedStatementFetchMinimumWord.executeQuery();
			if (rs.next()) {
				result = rs.getString(1);
			}
		} catch (SQLException e) {
			result = null;
			e.printStackTrace();
		}
		return result;
	}

	private ArrayList<ODIE_MiniparTriple> fetchTw(String word, String relation) {
		ArrayList<ODIE_MiniparTriple> result = new ArrayList<ODIE_MiniparTriple>();
		try {
			preparedStatementFetchTw.clearParameters();
			preparedStatementFetchTw.setString(1, word);
			preparedStatementFetchTw.setString(2, relation);
			ResultSet rs = preparedStatementFetchTw.executeQuery();
			while (rs.next()) {
				ODIE_MiniparTriple triple = new ODIE_MiniparTriple();
				triple.id = rs.getInt("ID");
				triple.wordOne = rs.getString("WORD_ONE");
				triple.relation = rs.getString("RELATION");
				triple.wordTwo = rs.getString("WORD_TWO");
				triple.freq = rs.getDouble("FREQ");
				triple.info = rs.getDouble("INFO");
				result.add(triple);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	private void insertSimilarity(String category, String wordOne,
			String wordTwo, double scoreSimilarity, double scoreHindle,
			double scoreHindleR, double scoreCosine, double scoreDice,
			double scoreJacard) {
		try {
			preparedStatementInsertSimilarity.clearParameters();
			preparedStatementInsertSimilarity.setString(1, category);
			preparedStatementInsertSimilarity.setString(2, wordOne);
			preparedStatementInsertSimilarity.setString(3, wordTwo);
			preparedStatementInsertSimilarity.setDouble(4, scoreSimilarity);
			preparedStatementInsertSimilarity.setDouble(5, scoreHindle);
			preparedStatementInsertSimilarity.setDouble(6, scoreHindleR);
			preparedStatementInsertSimilarity.setDouble(7, scoreCosine);
			preparedStatementInsertSimilarity.setDouble(8, scoreDice);
			preparedStatementInsertSimilarity.setDouble(9, scoreJacard);
			preparedStatementInsertSimilarity.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void prepareStatements() {
		try {
			String sql = "select ID, WORD_ONE, RELATION, WORD_TWO from TRIPLES where ID > ? order by ID limit 1";
			preparedStatementTripleCursor = conn.prepareStatement(sql);

			sql = "select sum(FREQ) from triples where WORD_ONE like ? and RELATION like ? and WORD_TWO like ?";
			preparedStatementCardinality = conn.prepareStatement(sql);

			sql = "update TRIPLES set INFO = ? where ID = ?";
			preparedStatementUpdateInfo = conn.prepareStatement(sql);

			sql = "select min(WORD_ONE) from TRIPLES";
			preparedStatementFetchMinimumWord = conn.prepareStatement(sql);

			sql = "select min(WORD_ONE) from TRIPLES where WORD_ONE > ? ;";
			preparedStatementFetchNextSequentialWord = conn
					.prepareStatement(sql);

			sql = "select * from TRIPLES where WORD_ONE = ? and RELATION like ? and INFO > 0.0";
			preparedStatementFetchTw = conn.prepareStatement(sql);

			sql = "insert into similarity (CATEGORY, WORD_ONE, WORD_TWO, SIMILARITY, HINDLE, HINDLER, COSINE, DICE, JACARD) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			preparedStatementInsertSimilarity = conn.prepareStatement(sql);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void prepareCursorStatements(
			String[] categorizedSimilarityPatternGroup) {
		try {
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("select min(WORD) from HISTOGRAM where ");
			sqlBuffer.append("TAGGER = '" + this.currentPosTaggerAlgorithm + "' and (") ;
			for (int idx = 0; idx < categorizedSimilarityPatternGroup.length; idx++) {
				String categorizedSimilarityPattern = categorizedSimilarityPatternGroup[idx];
				sqlBuffer.append("POS like '" + categorizedSimilarityPattern
						+ "' or ");
			}
			String sql = sqlBuffer.toString();
			sql = sql.substring(0, sql.length() - " or ".length());
			sql += ")" ;
//			System.out.println(sql) ;
			preparedStatementFetchMinimumWord = conn.prepareStatement(sql);

			sqlBuffer = new StringBuffer();
			sqlBuffer
					.append("select min(WORD) from HISTOGRAM where WORD > ? and ");
			sqlBuffer.append("TAGGER = '" + this.currentPosTaggerAlgorithm + "' and (") ;
			for (int idx = 0; idx < categorizedSimilarityPatternGroup.length; idx++) {
				String categorizedSimilarityPattern = categorizedSimilarityPatternGroup[idx];
				sqlBuffer.append("POS like '" + categorizedSimilarityPattern
						+ "' or ");
			}
			sql = sqlBuffer.toString();
			sql = sql.substring(0, sql.length() - " or ".length());
			sql += ")" ;
//			System.out.println(sql) ;
			preparedStatementFetchNextSequentialWord = conn
					.prepareStatement(sql);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void computeMutualInformation() {
		try {
			while (true) {
				preparedStatementTripleCursor.clearParameters();
				preparedStatementTripleCursor.setInt(1, currentId);
				ResultSet rs = preparedStatementTripleCursor.executeQuery();
				if (rs.next()) {
					currentId = rs.getInt(1);
					String wordOne = rs.getString(2);
					String relation = rs.getString(3);
					String wordTwo = rs.getString(4);
//					System.out.println("Processing " + currentId + ", "
//							+ wordOne + ", " + relation + ", " + wordTwo);
					String wildCard = "%";

					double wrw_count = computeCardinality(wordOne, relation,
							wordTwo);
					double _r__count = computeCardinality(wildCard, relation,
							wildCard);
					double wr__count = computeCardinality(wordOne, relation,
							wildCard);
					double _rw_count = computeCardinality(wildCard, relation,
							wordTwo);
					double numerator = wrw_count * _r__count;
					double denominator = wr__count * _rw_count;
					if (denominator > 0.0d) {
						double I_w_r_w = Math.log(numerator / denominator);
						preparedStatementUpdateInfo.clearParameters();
						preparedStatementUpdateInfo.setDouble(1, I_w_r_w);
						preparedStatementUpdateInfo.setInt(2, currentId);
						preparedStatementUpdateInfo.executeUpdate();
					}
				} else {
					break;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private double computeCardinality(String wordOne, String relation,
			String wordTwo) {
		double result = 0;
		try {
			preparedStatementCardinality.clearParameters();
			preparedStatementCardinality.setString(1, wordOne);
			preparedStatementCardinality.setString(2, relation);
			preparedStatementCardinality.setString(3, wordTwo);
			ResultSet rs = preparedStatementCardinality.executeQuery();
			while (rs.next()) {
				result = rs.getDouble(1);
				break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;

	}

	private void openDatabaseConnection() {

		String driver = System.getProperty("odie.db.driver");
		String url = System.getProperty("odie.db.url");
		String userName = System.getProperty("odie.db.user.name");
		String password = System.getProperty("odie.db.user.password");

		try {
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url, userName, password);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (conn != null) {
//			System.out.println("Time: "
//					+ (System.currentTimeMillis() - startTime)
//					+ " ODIE_MiniparStatisticsGenerator: Connected to RDBMS: "
//					+ url);
		} else {
			System.out
					.println("Time: "
							+ (System.currentTimeMillis() - startTime)
							+ " ODIE_MiniparStatisticsGenerator: FAILED to Connect to RDBMS: "
							+ url);
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
			sqlStmt.execute("drop table if exists lin_analysis.similarity");
			sqlStmt.close();

			StringBuffer sb = new StringBuffer();
			sb.append("CREATE TABLE lin_analysis.similarity (\n");
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

//			System.out
//					.println("Time: "
//							+ (System.currentTimeMillis() - startTime)
//							+ " ODIE_MiniparStatisticsGenerator: Created the SIMILARITY table.");

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

}
