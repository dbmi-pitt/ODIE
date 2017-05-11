package edu.pitt.dbmi.odie.uima.church.rdbms.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import edu.pitt.dbmi.odie.uima.church.model.ODIE_AssociationEntryFactoryInf;
import edu.pitt.dbmi.odie.uima.church.model.ODIE_AssociationEntryInf;
import edu.pitt.dbmi.odie.uima.church.model.ODIE_HistogramEntryFactoryInf;
import edu.pitt.dbmi.odie.uima.church.model.ODIE_HistogramEntryInf;
import edu.pitt.dbmi.odie.uima.church.model.ODIE_TermEntryFactoryInf;
import edu.pitt.dbmi.odie.uima.church.model.ODIE_TermEntryInf;
import edu.pitt.dbmi.odie.uima.util.ODIE_NormalizationUtils;

public class ODIE_SuggestionFactory {
	
	private String databaseName;
	
	private Connection connection;
	
	private ODIE_AssociationEntryFactoryInf associationEntryFactory;

	private ODIE_HistogramEntryFactoryInf histogramEntryFactory;
	
	private ODIE_TermEntryFactoryInf termEntryFactory;
	
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
			sb.append(") ENGINE=MyISAM");
			Statement sqlStmt = connection.createStatement();
			sqlStmt.execute(sb.toString());
			sqlStmt.close();

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
				Integer histogramOneId = associationEntry.getWordOneId();
				Integer histogramTwoId = associationEntry.getWordTwoId();
				double i_x_y = associationEntry.getIxy();
				ODIE_HistogramEntryInf histogramOneEntry = this.histogramEntryFactory
						.lookUpEntryById(histogramOneId);
				ODIE_HistogramEntryInf histogramTwoEntry = this.histogramEntryFactory
						.lookUpEntryById(histogramTwoId);

				ODIE_HistogramEntryInf histogramNerPositive = null;
				ODIE_HistogramEntryInf histogramNerNegative = null;
				if (histogramOneEntry.getIsNer() == 0
						&& histogramTwoEntry.getIsNer() == 1) {
					histogramNerPositive = histogramTwoEntry;
					histogramNerNegative = histogramOneEntry;
				} else if (histogramOneEntry.getIsNer() == 1
						&& histogramTwoEntry.getIsNer() == 0) {
					histogramNerPositive = histogramOneEntry;
					histogramNerNegative = histogramTwoEntry;
				}
				
				ODIE_TermEntryInf termNerNegativeEntry = null ;
				
				if (histogramNerNegative != null) {
					 termNerNegativeEntry = this.termEntryFactory.smallestTermOfHistogramId(histogramNerNegative.getId()) ;
				}

				if (histogramNerPositive != null
						&& termNerNegativeEntry != null) {
					String qualifiedTableName = databaseName + ".suggestion";
					PreparedStatement preparedStatementCreateSuggested = connection
					.prepareStatement("insert into "
							+ qualifiedTableName
							+ " (NER_NEGATIVE, NER_POSITIVE, METHOD, RULE, SCORE) values (?, ?, 'Church', 'Mutual Information', ?)");
					preparedStatementCreateSuggested.setString(1,
							termNerNegativeEntry.getTerm());
					preparedStatementCreateSuggested.setString(2,
							histogramNerPositive.getOntologyUri());
					preparedStatementCreateSuggested.setDouble(3, new Double(
							i_x_y));
					preparedStatementCreateSuggested.executeUpdate();
					preparedStatementCreateSuggested.close();
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

	public void setAssociationEntryFactory(
			ODIE_AssociationEntryFactory associationEntryFactory) {
		this.associationEntryFactory = associationEntryFactory ;
	}
	

	public ODIE_TermEntryFactoryInf getTermEntryFactory() {
		return termEntryFactory;
	}

	public void setTermEntryFactory(ODIE_TermEntryFactoryInf termEntryFactory) {
		this.termEntryFactory = termEntryFactory;
	}


}
