package edu.pitt.dbmi.odie.uima.lsp.consumer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import edu.pitt.dbmi.odie.ODIEUtils;
import edu.pitt.dbmi.odie.uima.util.ODIE_IFConstants;
import edu.upmc.opi.caBIG.caTIES.client.dispatcher.temporal.CaTIES_CombinationGenerator;
import edu.upmc.opi.caBIG.caTIES.common.CaTIES_FormatUtils;

public class ODIE_LspTermPairCasConsumer extends CasConsumer_ImplBase {

	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ODIE_LspTermPairCasConsumer.class);

	private String lspDatabaseName ;
	private String lspSentenceTableName = "lsp_unique_sentence";
	private int lspTermsPerLspBoundry = 12;
	private String lspTermPrefix = "term_";
	private String lspBlank = "BLANK";

	private Connection lspDatabaseConnection = null;

	public static void main(String[] args) {
		new ODIE_LspTermPairCasConsumer();
	}

	public void initialize() throws ResourceInitializationException {
		try {
			configJdbcInit();
			
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

	public void processCas(CAS arg0) throws ResourceProcessException {
		;
	}

	public void collectionProcessComplete(ProcessTrace arg0)
			throws ResourceProcessException, IOException {
		scoreLspTerms();
	}

	private void configJdbcInit() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		String driver = (String) getConfigParameterValue(ODIE_IFConstants.PARAM_DB_DRIVER);
		String url = (String) getConfigParameterValue(ODIE_IFConstants.IF_PARAM_DB_URL);
		String userName = (String) getConfigParameterValue(ODIE_IFConstants.PARAM_DB_USERNAME);
		String password = (String) getConfigParameterValue(ODIE_IFConstants.PARAM_DB_PASSWORD);
		Class.forName(driver).newInstance();
		lspDatabaseConnection = DriverManager.getConnection(url, userName,
				password);
		logger.info("Connected to the database at:" + url);
		this.lspDatabaseName = ODIEUtils.extractDatabaseNameFromDbUrl(url);
	}

	private void scoreLspTerms() {
		try {
			ODIE_LspWordPair.setDatabaseName(this.lspDatabaseName);
			ODIE_LspWordPair.setConn(this.lspDatabaseConnection);
			ODIE_LspWordPair.initialize();

			long minId = getMinLspId();
			long maxId = getMaxLspId();
			for (long idx = minId; idx <= maxId; idx++) {
				if (idx % 100 == 0) {
					logger.debug("Processed " + idx + " lsp sentences.");
				}
				String sql = "select * from " + this.lspSentenceTableName
						+ " where id = ?";
				PreparedStatement fetchLspSentencePreparedStatement = this.lspDatabaseConnection
						.prepareStatement(sql);
				fetchLspSentencePreparedStatement.setLong(1, idx);
				ResultSet rs = fetchLspSentencePreparedStatement.executeQuery();
				ArrayList<String> lspTerms = new ArrayList<String>();
				if (rs.next()) {
					for (int jdx = 0; jdx < lspTermsPerLspBoundry; jdx++) {
						String columnName = lspTermPrefix
								+ CaTIES_FormatUtils
										.formatIntegerAsDigitString(jdx, "00");
						String lspTerm = rs.getString(columnName);
						if (lspTerm.equals(lspBlank)) {
							break;
						} else {
							lspTerms.add(lspTerm);
						}
					}
				}
				fetchLspSentencePreparedStatement.close();
				scoreLspTerms(lspTerms);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void scoreLspTerms(ArrayList<String> lspTerms) {
		if (lspTerms.size() > 1) {
			CaTIES_CombinationGenerator combinationGenerator = new CaTIES_CombinationGenerator();
			int[][] combiniationIndices = combinationGenerator
					.nChooseKWithNoReplacement(lspTerms.size(), 2,
							CaTIES_CombinationGenerator.NONCONTIGUOUS);
			for (int idx = 0; idx < combiniationIndices.length; idx++) {
				String termOne = lspTerms.get(combiniationIndices[idx][0]);
				String termTwo = lspTerms.get(combiniationIndices[idx][1]);
				scoreLspTermPair(termOne, termTwo);
			}
		}
	}

	private void scoreLspTermPair(String termOne, String termTwo) {
		String[] termOneWords = termOne.split("\\s");
		String[] termTwoWords = termTwo.split("\\s");
		for (int idx = 0; idx < termOneWords.length; idx++) {
			for (int jdx = 0; jdx < termTwoWords.length; jdx++) {
				String wordOne = termOneWords[idx];
				String wordTwo = termTwoWords[jdx];
				if (!wordOne.toLowerCase().equals(wordTwo.toLowerCase())) {
					ODIE_LspWordPair odieLspWordPair = ODIE_LspWordPair
							.fetchOrCreateOdieLspWordPair(wordOne, wordTwo);
					odieLspWordPair.setFreq(odieLspWordPair.getFreq() + 1L);
					ODIE_LspWordPair.updateOdieLspWordPair(odieLspWordPair);
				}
			}
		}
	}

	private long getMinLspId() throws SQLException {
		long minId = Long.MAX_VALUE;
		String sql = "select min(id) from " + this.lspSentenceTableName;
		PreparedStatement fetchMinIdPreparedStatement = this.lspDatabaseConnection
				.prepareStatement(sql);
		ResultSet rs = fetchMinIdPreparedStatement.executeQuery();
		if (rs.next()) {
			minId = rs.getLong(1);
		}
		fetchMinIdPreparedStatement.close();
		return minId;
	}

	private long getMaxLspId() throws SQLException {
		long maxId = Long.MIN_VALUE;
		String sql = "select max(id) from " + this.lspSentenceTableName;
		PreparedStatement fetchMaxIdPreparedStatement = this.lspDatabaseConnection
				.prepareStatement(sql);
		ResultSet rs = fetchMaxIdPreparedStatement.executeQuery();
		if (rs.next()) {
			maxId = rs.getLong(1);
		}
		fetchMaxIdPreparedStatement.close();
		return maxId;
	}

}
