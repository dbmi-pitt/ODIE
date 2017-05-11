package edu.pitt.dbmi.odie.uima.church.cache.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

import edu.pitt.dbmi.odie.uima.church.model.ODIE_AssociationEntryFactoryInf;
import edu.pitt.dbmi.odie.uima.church.model.ODIE_AssociationEntryInf;
import edu.pitt.dbmi.odie.uima.church.model.ODIE_HistogramEntryFactoryInf;
import edu.pitt.dbmi.odie.uima.church.model.ODIE_HistogramEntryInf;
import edu.pitt.dbmi.odie.uima.church.rdbms.model.ODIE_AssociationEntryFactory;
import edu.pitt.dbmi.odie.uima.util.ODIE_NormalizationUtils;

public class ODIE_SuggestionFactory {

	private String databaseName;

	private PreparedStatement preparedStatementCreateSuggested = null;
	private Connection connection;

	private ODIE_AssociationEntryFactoryInf associationEntryFactory;
	
	private ODIE_HistogramEntryFactoryInf histogramEntryFactory;

	public void buildSuggestedNamedEntityTable() {
		try {
			String qualifiedTableName = databaseName + ".suggestion";
			StringBuffer sb = new StringBuffer();
			sb.append("CREATE TABLE IF NOT EXISTS " + qualifiedTableName
					+ " (\n");
			sb.append("  ID int NOT NULL AUTO_INCREMENT,\n");
			sb
					.append("  NER_NEGATIVE VARCHAR(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb
					.append("  NER_POSITIVE VARCHAR(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb.append("  METHOD VARCHAR(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb.append("  RULE VARCHAR(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb
					.append("  RELATION VARCHAR(255) NOT NULL DEFAULT 'UNDEFINED',\n");
			sb.append("  SCORE decimal(6,2) NOT NULL DEFAULT 0.00,\n");
			sb.append("  PRIMARY KEY (ID)\n");
			sb.append(") ENGINE=InnoDB");
			Statement sqlStmt = connection.createStatement();
			sqlStmt.execute(sb.toString());
			sqlStmt.close();

			this.preparedStatementCreateSuggested = connection
					.prepareStatement("insert into "
							+ qualifiedTableName
							+ " (NER_NEGATIVE, NER_POSITIVE, METHOD, RULE, SCORE) values (?, ?, 'Church', 'Mutual Information', ?)");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void dropSuggestionTable() {
		try {
			String qualifiedTableName = databaseName + ".suggestion";
			Statement dropStatement;
			dropStatement = connection.createStatement();
			String sql = "drop table if exists " + qualifiedTableName;
			dropStatement.executeUpdate(sql);
			dropStatement.close();
		} catch (SQLException e) {
			;
		}
	}

	public void buildSuggestedNamedEntityAdditions() {
		try {
			((ODIE_AssociationEntryFactory) associationEntryFactory).iterate();
			while (true) {
				ODIE_AssociationEntryInf associationEntry = associationEntryFactory
						.next();
				if (associationEntry == null) {
					break;
				}
				Integer wordOneId = associationEntry.getWordOneId();
				Integer wordTwoId = associationEntry.getWordTwoId();
				double i_x_y = associationEntry.getIxy();
				ODIE_HistogramEntryInf wordOneEntry = this.histogramEntryFactory
						.lookUpEntryById(wordOneId);
				ODIE_HistogramEntryInf wordTwoEntry = this.histogramEntryFactory
						.lookUpEntryById(wordTwoId);

				ODIE_HistogramEntryInf wordNerPositiveEntry = null;
				ODIE_HistogramEntryInf wordNerNegativeEntry = null;
				if (wordOneEntry.getIsNer() == 0
						&& wordTwoEntry.getIsNer() == 1) {
					wordNerPositiveEntry = wordTwoEntry;
					wordNerNegativeEntry = wordOneEntry;
				} else if (wordOneEntry.getIsNer() == 1
						&& wordTwoEntry.getIsNer() == 0) {
					wordNerPositiveEntry = wordOneEntry;
					wordNerNegativeEntry = wordTwoEntry;
				}

				if (wordNerPositiveEntry != null
						&& wordNerNegativeEntry != null) {
					preparedStatementCreateSuggested.setString(1,
							wordNerNegativeEntry.getWordText());
					preparedStatementCreateSuggested.setString(2,
							wordNerPositiveEntry.getOntologyUri());
					preparedStatementCreateSuggested.setDouble(3, new Double(
							i_x_y));
					preparedStatementCreateSuggested.executeUpdate();
				}
			}

			// Normalize the score column
			String qualifiedTableName = databaseName + ".suggestion";
			String whereClause = "METHOD like 'Church%'";
			ODIE_NormalizationUtils.normalizeRanges(connection,
					qualifiedTableName, whereClause, "SCORE", "SCORE");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void buildSuggestedNamedEntityAdditions2(
			Collection<ODIE_AssociationEntryInf> associationEntries) {
		try {
			for (ODIE_AssociationEntryInf associationEntry : associationEntries) {
				Integer wordOneId = associationEntry.getWordOneId();
				Integer wordTwoId = associationEntry.getWordTwoId();
				double i_x_y = associationEntry.getIxy();
				ODIE_HistogramEntryInf wordOneEntry = this.histogramEntryFactory
						.lookUpEntryById(wordOneId);
				ODIE_HistogramEntryInf wordTwoEntry = this.histogramEntryFactory
						.lookUpEntryById(wordTwoId);

				ODIE_HistogramEntryInf wordNerPositiveEntry = null;
				ODIE_HistogramEntryInf wordNerNegativeEntry = null;
				if (wordOneEntry.getIsNer() == 0
						&& wordTwoEntry.getIsNer() == 1) {
					wordNerPositiveEntry = wordTwoEntry;
					wordNerNegativeEntry = wordOneEntry;
				} else if (wordOneEntry.getIsNer() == 1
						&& wordTwoEntry.getIsNer() == 0) {
					wordNerPositiveEntry = wordOneEntry;
					wordNerNegativeEntry = wordTwoEntry;
				}

				if (wordNerPositiveEntry != null
						&& wordNerNegativeEntry != null) {
					preparedStatementCreateSuggested.setString(1,
							wordNerNegativeEntry.getWordText());
					preparedStatementCreateSuggested.setString(2,
							wordNerPositiveEntry.getOntologyUri());
					preparedStatementCreateSuggested.setDouble(3, new Double(
							i_x_y));
					preparedStatementCreateSuggested.executeUpdate();
				}
			}

			// Normalize the score column
			String qualifiedTableName = databaseName + ".suggestion";
			String whereClause = "METHOD like 'Church%'";
			ODIE_NormalizationUtils.normalizeRanges(connection,
					qualifiedTableName, whereClause, "SCORE", "SCORE");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public ODIE_HistogramEntryFactoryInf getHistogramEntryFactory() {
		return histogramEntryFactory;
	}

	public void setHistogramEntryFactory(
			ODIE_HistogramEntryFactoryInf histogramEntryFactory) {
		this.histogramEntryFactory = histogramEntryFactory;
	}
	
	public ODIE_AssociationEntryFactoryInf getAssociationEntryFactory() {
		return associationEntryFactory;
	}

	public void setAssociationEntryFactory(
			ODIE_AssociationEntryFactoryInf associationEntryFactory) {
		this.associationEntryFactory = associationEntryFactory;
	}

}
