package edu.pitt.dbmi.odie.uima.dekanlin.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeSet;

import org.apache.log4j.Logger;

public class ODIE_SimilarityFactory {
	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ODIE_SimilarityFactory.class);

//	private PreparedStatement preparedStatementInsertSimilarity;

//	private PreparedStatement preparedStatementCreateSuggestion;

	private Connection conn;

	private String tableNamePrefix;

	private String databaseName;

	private String qualifiedTableName;

	public void initialize(Connection connInput, String databaseNameParam,
			String tableNamePrefixParam) {
		conn = connInput;
		tableNamePrefix = tableNamePrefixParam;
		databaseName = databaseNameParam;
		qualifiedTableName = databaseName + "."+tableNamePrefix
				+ "_similarity";

	}

	public void dropSimilarityTable() {
		Statement sqlStmt = null;
		try {
			sqlStmt = conn.createStatement();
			sqlStmt.execute("drop table if exists " + this.qualifiedTableName);
			sqlStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void createSimilarityTable() {
		// Databases typically use user-names and passwords; these can
		// be passed as //properties to the getConnection method.

		// drop the table in case it's already present
		// This isn't needed because we're starting from an empty
		// database,
		// but leave here for tutorial reasons
		Statement sqlStmt = null;
		try {
			sqlStmt = conn.createStatement();
			sqlStmt.execute("drop table if exists " + this.qualifiedTableName);
			sqlStmt.close();

			StringBuffer sb = new StringBuffer();
			sb.append("CREATE TABLE IF NOT EXISTS  " + this.qualifiedTableName + " (\n");
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

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public void dropSuggestedNamedEntityTable() {
		try {
			String qualifiedTableName = this.databaseName + "." + "suggestion";
			Statement dropStatement;
			dropStatement = conn.createStatement();
			String sql = "drop table if exists " + qualifiedTableName;
			dropStatement.executeUpdate(sql);
			dropStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void createSuggestedNamedEntityTable() {
		try {
			String qualifiedTableName = this.databaseName + "." + "suggestion";
			StringBuffer sb = new StringBuffer();
			sb.append("CREATE TABLE IF NOT EXISTS " + qualifiedTableName + " (\n");
			sb.append("  ID int NOT NULL AUTO_INCREMENT,\n");
			sb
					.append("  NER_NEGATIVE VARCHAR(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb
					.append("  NER_POSITIVE VARCHAR(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb.append("  METHOD VARCHAR(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb.append("  RULE VARCHAR(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb.append("  RELATION VARCHAR(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb.append("  SCORE decimal(6,2) NOT NULL DEFAULT 0.00,\n");
			sb.append("  PRIMARY KEY (ID)\n");
			sb.append(") ENGINE=InnoDB");
			Statement sqlStmt = conn.createStatement();
			sqlStmt.execute(sb.toString());
			sqlStmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void createROCCurvePrecursorTable() {
		try {
			String qualifiedTableName = this.databaseName + "." + "lin_association";
			StringBuffer sb = new StringBuffer();
			sb.append("drop table if exists " + qualifiedTableName) ;
			Statement sqlStmt = conn.createStatement();
			sqlStmt.executeUpdate(sb.toString());
			sqlStmt.close();
			
			sb = new StringBuffer();
			sb.append("create table ") ;
			sb.append(qualifiedTableName) ;
			sb.append(" as ") ;
			sb.append("select ") ;
			sb.append("      histogramOne.id as word_one_id,") ;
			sb.append("      histogramTwo.id as word_two_id, ") ;
			sb.append("      '0' as word_one_freq, ") ;
			sb.append("      '0' as word_two_freq,") ;
			sb.append("      histogramOne.is_ner as is_ner_one,") ;
			sb.append("      histogramTwo.is_ner as is_ner_two,") ;
			sb.append("      '0' as freq,") ;
			sb.append("      s.similarity as i_x_y ") ;
			sb.append("from") ;
			sb.append("      lin_similarity s,") ;
			sb.append("      lin_histogram histogramOne,") ;
			sb.append("      lin_histogram histogramTwo") ;
			sb.append(" where ") ;
			sb.append("      s.word_one = histogramOne.word and") ;
			sb.append("      s.word_two = histogramTwo.word ") ;
			String sql = sb.toString();
			sqlStmt = conn.createStatement();
			sqlStmt.executeUpdate(sql);
			sqlStmt.close();
			
			sb = new StringBuffer() ;
			sb.append("ALTER TABLE ") ;
			sb.append(qualifiedTableName) ;
			sb.append("      add id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST") ;
			sqlStmt = conn.createStatement();
			sqlStmt.executeUpdate(sb.toString());
			sqlStmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertSimilarityBatchMode(
			TreeSet<ODIE_Similarity> sortedSimilaritiesByHindler) {
		try {
			int idx = 0;
			for (ODIE_Similarity odieSimilarity : sortedSimilaritiesByHindler) {
				if (odieSimilarity.wordOne.length() > 244 || odieSimilarity.wordTwo.length() > 244) {
					continue ;
				}
				String sql = "insert into " + this.qualifiedTableName + " (CATEGORY, WORD_ONE, WORD_TWO, SIMILARITY, HINDLE, HINDLER, COSINE, DICE, JACARD) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
				PreparedStatement preparedStatementInsertSimilarity = conn.prepareStatement(sql);
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
				preparedStatementInsertSimilarity.close();
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

	public void insertSuggestedNamedEntityTable(
			TreeSet<ODIE_Similarity> sortedSimilaritiesByHindler) {
		try {
			for (ODIE_Similarity odieSimilarity : sortedSimilaritiesByHindler) {
				String qualifiedTableName = this.databaseName + "." + "suggestion";
				String sql = "insert into "
					+ qualifiedTableName
					+ " (NER_NEGATIVE, NER_POSITIVE, METHOD, RULE, SCORE) values (?, ?, 'Lin', 'Similarity', ?)" ;
				PreparedStatement preparedStatementCreateSuggestion = conn
						.prepareStatement(sql);
				preparedStatementCreateSuggestion.clearParameters();
				preparedStatementCreateSuggestion.setString(1,
						odieSimilarity.wordOne);
				preparedStatementCreateSuggestion.setString(2,
						odieSimilarity.ontologyUri);
				preparedStatementCreateSuggestion.setDouble(3,
						odieSimilarity.similarity);
				preparedStatementCreateSuggestion.executeUpdate();
				preparedStatementCreateSuggestion.close();	
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
