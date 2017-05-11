package edu.pitt.dbmi.odie.uima.church.cache.casconsumer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import edu.mayo.bmi.uima.chunker.type.Chunk;
import edu.mayo.bmi.uima.core.type.NamedEntity;
import edu.pitt.dbmi.odie.ODIEUtils;
import edu.pitt.dbmi.odie.uima.church.cache.model.ODIE_AssociationEntryFactory;
import edu.pitt.dbmi.odie.uima.church.cache.model.ODIE_HistogramEntryFactory;
import edu.pitt.dbmi.odie.uima.church.cache.model.ODIE_SuggestionFactory;
import edu.pitt.dbmi.odie.uima.church.model.ODIE_AssociationEntryInf;
import edu.pitt.dbmi.odie.uima.church.model.ODIE_HistogramEntryInf;
import edu.pitt.dbmi.odie.uima.church.types.ODIE_StopWord;
import edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateAnnotation;
import edu.pitt.dbmi.odie.uima.util.ODIE_Connection;
import edu.pitt.dbmi.odie.uima.util.ODIE_IFConstants;
import edu.pitt.dbmi.odie.uima.util.ODIE_UimaUtils;

/**
 * Implement algorithms from
 * "Word Association Norms, Mutual Information, and Lexicography" paper by
 * Kenneth Ward Church (Bell Labs) and Patrick Hanks (Glasgow Scottland)
 * 
 * I(x,y) = log2 P(x,y) / ( P(x) P(y) )
 * 
 * where P(x) = f(x) / N P(y) = f(y) / N
 * 
 * P(x, y) are estimated by counting the number of times that x is followed by y
 * in a window of W words f{w}(x, y).
 * 
 * Final results and partial information is accumulated in a RDBMS
 * 
 */
public class ODIE_CASC_ChurchSuggestionGenerator extends CasConsumer_ImplBase {

	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ODIE_CASC_ChurchSuggestionGenerator.class);

	private int windowSize = 4;

	private int termFrequencyLowerBound = 2;

	private long associationNumberUpperBound = 500L;

	// Only terms with this many words or more are processed.
	private int termLengthLowerBound = 2;

	private ODIE_Connection conn = null;

	private ArrayList<Annotation> wordArray = null;

	private String databaseName;

	private String tableNamePrefix;

	private ODIE_HistogramEntryFactory histogramEntryFactory = null;

	private ODIE_AssociationEntryFactory associationEntryFactory = null;

	private edu.pitt.dbmi.odie.uima.church.rdbms.model.ODIE_HistogramEntryFactory dbHistogramEntryFactory = null;

	private edu.pitt.dbmi.odie.uima.church.rdbms.model.ODIE_AssociationEntryFactory dbAssociationEntryFactory = null;

	private ODIE_SuggestionFactory suggestionFactory = null;

	private boolean isDatabaseCleaning = false;

	public void initialize() throws ResourceInitializationException {

		String driver = (String) getConfigParameterValue(ODIE_IFConstants.PARAM_DB_DRIVER);
		String url = (String) getConfigParameterValue(ODIE_IFConstants.IF_PARAM_DB_URL);
		this.databaseName = ODIEUtils.extractDatabaseNameFromDbUrl(url);
		String userName = (String) getConfigParameterValue(ODIE_IFConstants.PARAM_DB_USERNAME);
		String password = (String) getConfigParameterValue(ODIE_IFConstants.PARAM_DB_PASSWORD);

		String windowSizeAsString = (String) getConfigParameterValue(ODIE_IFConstants.PARAM_CHURCH_WINDOW_SIZE);
		String termFrequencyLowerBoundAsString = (String) getConfigParameterValue(ODIE_IFConstants.PARAM_CHURCH_TERM_FREQUENCY_LOWER_BOUND);
		String associationNumberUpperBoundAsString = (String) getConfigParameterValue(ODIE_IFConstants.PARAM_CHURCH_ASSOCIATION_NUMBER_UPPER_BOUND);
		String termLengthLowerBoundAsString = (String) getConfigParameterValue(ODIE_IFConstants.PARAM_CHURCH_TERM_LENGTH_LOWER_BOUND);

		String isDatabaseCleaningAsString = (String) getConfigParameterValue(ODIE_IFConstants.PARAM_CHURCH_IS_DATABASE_CLEANING);
		setDatabaseCleaning(new Boolean(isDatabaseCleaningAsString));

		setWindowSize(new Integer(windowSizeAsString));
		setTermFrequencyLowerBound(new Integer(termFrequencyLowerBoundAsString));
		setAssociationNumberUpperBound(new Long(
				associationNumberUpperBoundAsString));
		setTermLengthLowerBound(new Integer(termLengthLowerBoundAsString));
		conn = new ODIE_Connection(driver, url, userName, password);

		this.tableNamePrefix = (String) getConfigParameterValue(ODIE_IFConstants.IF_PARAM_TABLE_NAME_PREFIX);

		this.histogramEntryFactory = new ODIE_HistogramEntryFactory();
		this.associationEntryFactory = new ODIE_AssociationEntryFactory();
		this.suggestionFactory = new ODIE_SuggestionFactory();
		this.associationEntryFactory
				.setHistogramEntryFactory(this.histogramEntryFactory);

		this.suggestionFactory.setDatabaseName(databaseName);
		this.suggestionFactory.setConnection(this.conn);
		this.suggestionFactory.setHistogramEntryFactory(this.histogramEntryFactory) ;
		if (isDatabaseCleaning()) {
			this.suggestionFactory.dropSuggestionTable();
		}
		this.suggestionFactory.buildSuggestedNamedEntityTable();

		this.dbHistogramEntryFactory = new edu.pitt.dbmi.odie.uima.church.rdbms.model.ODIE_HistogramEntryFactory();
		this.dbAssociationEntryFactory = new edu.pitt.dbmi.odie.uima.church.rdbms.model.ODIE_AssociationEntryFactory();

		this.dbHistogramEntryFactory.setDatabaseCleaning(isDatabaseCleaning());
		this.dbAssociationEntryFactory
				.setDatabaseCleaning(isDatabaseCleaning());

		this.dbHistogramEntryFactory.initialize(conn, databaseName,
				tableNamePrefix);
		this.dbAssociationEntryFactory.initialize(conn, databaseName,
				tableNamePrefix);
		this.dbAssociationEntryFactory
				.setHistogramEntryFactory(this.dbHistogramEntryFactory);

	}

	/**
	 * Processes the CasContainer which was populated by the
	 * TextAnalysisEngines. <br>
	 * In this case, the CAS is assumed to contain annotations of type
	 * ODIE_Word, created with the ODIE_Tokeniser. These Annotations are stored
	 * in a database table called church_analysis.
	 * 
	 * @param aCAS
	 *            CasContainer which has been populated by the TAEs
	 * 
	 * @throws ResourceProcessException
	 *             if there is an error in processing the Resource
	 * 
	 * @see org.apache.uima.collection.base_cpm.CasObjectProcessor#processCas(org.apache.uima.cas.CAS)
	 */
	public void processCas(CAS aCAS) throws ResourceProcessException {

		JCas jcas;
		try {
			jcas = aCAS.getJCas();
		} catch (CASException e) {
			throw new ResourceProcessException(e);
		}

		try {
			//
			// Disclaimer:
			// This code is complex because it assumes two possible pipeline
			// configurations
			// 1) A pipeline that generates ODIE_GateAnnotation wrappers for
			// Tokens, Lookups, and NounPhrases
			// 2) A pipeline that works on first class Chunk objects from
			// cTAKES.
			// The default behavior is (1)

			//
			// Cache the NamedEntities for this document
			//
			ArrayList<NamedEntity> namedEntities = ODIE_UimaUtils
					.pullNamedEntitiesFromJCas(jcas);

			// Cache the tokens and nounPhrases from this document in a more
			// accessible
			// structure.
			// This will be useful in examining co-ocurrance
			//
			final ArrayList<Annotation> nounPhraseArray = new ArrayList<Annotation>();
			nounPhraseArray.addAll(ODIE_UimaUtils
					.pullTransientNPsAndTokensFromJCas(jcas));

			//
			// If we have no ODIE_GateAnnotation based NPs then pull cTAKES
			// NounChunks
			if (nounPhraseArray.isEmpty()) {
				nounPhraseArray.addAll(ODIE_UimaUtils
						.pullCTakesNPsFromJCas(jcas));
			}

			// Filter - used only for cTAKES NounChunks
			this.wordArray = new ArrayList<Annotation>();
			for (Annotation nounPhraseOrWord : nounPhraseArray) {
				if ((nounPhraseOrWord instanceof ODIE_StopWord)) {
					;
				} else if (isChunkOtherThanNoun(nounPhraseOrWord)) {
					;
				} else {
					this.wordArray.add(nounPhraseOrWord);
				}
			}

			//
			// Histogram only those noun phrases that meet the filter criteria
			// of alphabetic characters with length greater than two.
			//
			for (Annotation word : wordArray) {
				String wordText = word.getCoveredText();
				wordText = wordText.toLowerCase();

				String chunkType = null;
				if (word instanceof ODIE_GateAnnotation) {
					ODIE_GateAnnotation theChunk = (ODIE_GateAnnotation) word;
					chunkType = theChunk.getGateAnnotationType();
				} else if (word instanceof Chunk) {
					chunkType = ((Chunk) word).getChunkType();
				}

				if (chunkType.equals("TransientNP")
						|| chunkType.equalsIgnoreCase("NP")) {
					if (ODIE_UimaUtils.filterTerm(wordText,
							getTermLengthLowerBound()) != null) {
						logger.debug("processing word ==> " + wordText);
						ODIE_HistogramEntryInf histogramEntry = lookUpWordHistogramEntry(wordText);
						histogramEntry.setFreq(histogramEntry.getFreq() + 1);
						// Determine whether these words are known named
						// NamedEntities
						String nerState = ODIE_UimaUtils.getNerState(word,
								namedEntities);
						if (histogramEntry.getIsNer() == 0
								&& !nerState.startsWith("NERNegative")) {
							String uri = ODIE_UimaUtils
									.parseUriFromGenSymStateValue(nerState);
							histogramEntry.setIsNer(1);
							histogramEntry.setOntologyUri(uri);
						}
						this.histogramEntryFactory
								.updateHistogramEntry(histogramEntry);

					}
				}

			}

			// Look at an window 4 words ahead
			for (int idx = 0; idx < this.wordArray.size() - 1; idx++) {
				ODIE_HistogramEntryInf wordOneEntry = lookUpPossiblyNullWordId(this.wordArray
						.get(idx));
				if (wordOneEntry == null) {
					continue;
				}
				for (int jdx = idx + 1; jdx < Math.min(this.wordArray.size(),
						idx + getWindowSize()); jdx++) {
					ODIE_HistogramEntryInf wordTwoEntry = lookUpPossiblyNullWordId(this.wordArray
							.get(idx));
					if (wordTwoEntry == null) {
						continue;
					}
					Annotation wordOneAnnot = this.wordArray.get(idx);
					Annotation wordTwoAnnot = this.wordArray.get(jdx);
					String wordOneText = ODIE_UimaUtils.filterTerm(wordOneAnnot
							.getCoveredText(), 2);
					String wordTwoText = ODIE_UimaUtils.filterTerm(wordTwoAnnot
							.getCoveredText(), 2);
					if (wordOneText != null && wordTwoText != null) {
						logger.debug("Incrementing : " + wordOneText + "<"
								+ wordOneEntry.getId() + ">, " + wordTwoText + "<"
								+ wordTwoEntry.getId() + ">, ");
						incrementMutualInformationFrequency(wordOneEntry,
								wordTwoEntry, 1.0d);
					}
				}
			}

			// Look at an window 4 words behind
			// for (int idx = this.wordArray.size() - 1; idx >= 0; idx--) {
			// long wordOneId =
			// lookUpPossiblyNullWordId(this.wordArray.get(idx));
			// if (wordOneId < 0) {
			// continue ;
			// }
			// for (int jdx = idx - 1; jdx >= Math.max(0, idx - WINDOW_SIZE
			// + 1); jdx--) {
			// long wordTwoId =
			// lookUpPossiblyNullWordId(this.wordArray.get(jdx));
			// if (wordTwoId < 0) {
			// continue ;
			// }
			// Annotation wordOneAnnot = this.wordArray.get(idx);
			// Annotation wordTwoAnnot = this.wordArray.get(jdx);
			// String wordOneText = wordOneAnnot.getCoveredText();
			// String wordTwoText = wordTwoAnnot.getCoveredText();
			// logger.debug("Incrementing : " + wordTwoText + "<"
			// + wordTwoId + ">, " + wordOneText + "<" + wordOneId
			// + ">, ");
			// incrementMutualInformationFrequency(wordOneId, wordTwoId,
			// 0.5d);
			// }
			// }

		} catch (SQLException e) {
			throw new ResourceProcessException(e);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	private boolean isChunkOtherThanNoun(Annotation word) {
		boolean result = false;
		if (word instanceof Chunk) {
			Chunk theChunk = (Chunk) word;
			String chunkType = theChunk.getChunkType();
			if (!chunkType.equals("NP")) {
				result = true;
			}
		}
		return result;
	}

	private void incrementMutualInformationFrequency(ODIE_HistogramEntryInf wordOneEntry,
			ODIE_HistogramEntryInf wordTwoEntry, double incrementAmount) throws SQLException {
		ODIE_AssociationEntryInf associationEntry = this.associationEntryFactory
				.fetchAssociationEntry(wordOneEntry, wordTwoEntry);
		associationEntry.setFreq(associationEntry.getFreq() + incrementAmount);
		this.associationEntryFactory.updateAssociationEntry(associationEntry);
	}

	private ODIE_HistogramEntryInf lookUpWordHistogramEntry(String wordText)
			throws SQLException {
		ODIE_HistogramEntryInf result = this.histogramEntryFactory
				.fetchHistogramEntry(wordText);
		return result;
	}

	private ODIE_HistogramEntryInf lookUpPossiblyNullWordId(Annotation word) throws SQLException {
		ODIE_HistogramEntryInf result = null ;
		String wordText = word.getCoveredText().toLowerCase();
		ODIE_HistogramEntryInf entry = this.histogramEntryFactory
				.fetchHistogramEntry(wordText);
		if (entry != null) {
			result = entry ;
		}
		return result;
	}

	public void collectionProcessComplete(ProcessTrace arg0)
			throws ResourceProcessException, IOException {

		try {
			displayHistogram();

			this.associationEntryFactory.iterate();

			while (true) {

				ODIE_AssociationEntryInf associationEntry = this.associationEntryFactory
						.next();
				if (associationEntry == null) {
					break;
				}

				Integer wordOneId = associationEntry.getWordOneId();
				Integer wordTwoId = associationEntry.getWordTwoId();
				double associationFreq = associationEntry.getFreq();
				ODIE_HistogramEntryInf wordOneEntry = this.histogramEntryFactory
						.lookUpEntryById(wordOneId);
				ODIE_HistogramEntryInf wordTwoEntry = this.histogramEntryFactory
						.lookUpEntryById(wordTwoId);

				//
				// Report mutual information only in the cases where
				// one word isNer = 0 and the other isNer = 1
				//
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
					long wordOneFreq = wordNerPositiveEntry.getFreq();
					long wordTwoFreq = wordNerNegativeEntry.getFreq();
					double N = this.histogramEntryFactory.getNumberOfEntries();
					double fxy = associationFreq;
					double fx = wordOneFreq;
					double fy = wordTwoFreq;
					double Pxy = fxy / N;
					double Px = fx / N;
					double Py = fy / N;
					double denominator = (Px * Py);
					double ixy = 0.0d;
					if (denominator > 0.0d) {
						ixy = log2(Pxy / denominator);
					}
					associationEntry.setIxy(new Double(ixy));
				} else {
					associationEntry.setIxy(new Double(0.0d));
				}
			}
			
			//
			// Persist the histogram
			//
			this.dbHistogramEntryFactory.insertBatch(this.histogramEntryFactory.getSortedEntries()) ;
				
			//
			// Frequency lower bound pruning indicates elimination of
			// associations that appear less than a certain number of times.
			//
			// The number

			Collection<ODIE_AssociationEntryInf> associationEntries = this.associationEntryFactory
					.scoreAssociationEntries(getAssociationNumberUpperBound(),
							new Long(getTermFrequencyLowerBound()));

			
			logger.debug("Prior to inserting we have "
					+ associationEntries.size() + " associations ");
			this.dbAssociationEntryFactory.insertBatch(associationEntries);

			//
			// Save the scored entries as suggestions for new NamedEntities
			//
			this.suggestionFactory
					.buildSuggestedNamedEntityAdditions();

			this.dbHistogramEntryFactory.close();
			this.dbAssociationEntryFactory.close();
			
		} catch (Exception x) {
			x.printStackTrace();
		}

	}

	public void finalize() {
		try {

			if (conn != null && !conn.isClosed()) {
				conn.close();
				conn = null;
			}

		} catch (SQLException e) {

		}
	}

	private void displayHistogram() {
		ODIE_HistogramEntryFactory cacheFactory = (ODIE_HistogramEntryFactory) this.histogramEntryFactory;
		TreeSet<ODIE_HistogramEntryInf> sortedEntries = cacheFactory
				.getSortedEntries();
		for (ODIE_HistogramEntryInf histogramEntry : sortedEntries) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
			PrintStream ps = new PrintStream(bos) ;
			ps.printf("id: %5d term: %s freq: %5d %n", histogramEntry
					.getId(), histogramEntry.getWordText(), histogramEntry
					.getFreq());
			logger.debug(bos.toString()) ;
		}
	}

	private double log2(double num) {
		return (Math.log(num) / Math.log(2));
	}

	@SuppressWarnings("unused")
	private void displayResults() throws SQLException {
		StringBuffer sb = new StringBuffer();
		sb.append("select\n");
		sb.append("   a.i_x_y ixy,\n");
		sb.append("   a.freq fxy,\n");
		sb.append("   h1.freq fx,   \n");
		sb.append("   h1.word x,\n");
		sb.append("   h2.freq fy,   \n");
		sb.append("   h2.word y\n");
		sb.append("from\n");
		sb.append("   " + this.databaseName + "." + this.tableNamePrefix
				+ "_histogram h1,\n");
		sb.append("   " + this.databaseName + "." + this.tableNamePrefix
				+ "_histogram h2,\n");
		sb.append("   " + this.databaseName + "." + this.tableNamePrefix
				+ "_association a\n");
		sb.append("where\n");
		sb.append("   a.word_one_id = h1.id and\n");
		sb.append("   a.word_two_id = h2.id and\n");
		sb.append("   a.freq > 5\n");
		sb.append("order by\n");
		sb.append("   a.i_x_y desc limit 100 ;   \n");
		Statement query = conn.createStatement();
		ResultSet rs = query.executeQuery(sb.toString());
		while (rs.next()) {
			double Ixy = rs.getDouble(1);
			double fxy = rs.getDouble(2);
			double fx = rs.getDouble(3);
			String x = rs.getString(4);
			double fy = rs.getDouble(5);
			String y = rs.getString(6);
			logger.debug(Ixy + ", ");
			logger.debug(fxy + ", ");
			logger.debug(fx + ", ");
			logger.debug(x + ", ");
			logger.debug(fy + ", ");
			logger.debug(y);
			logger.debug("");
		}

	}

	public int getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}

	public int getTermFrequencyLowerBound() {
		return termFrequencyLowerBound;
	}

	public void setTermFrequencyLowerBound(int termFrequencyLowerBound) {
		this.termFrequencyLowerBound = termFrequencyLowerBound;
	}

	public long getAssociationNumberUpperBound() {
		return associationNumberUpperBound;
	}

	public void setAssociationNumberUpperBound(long associationNumberUpperBound) {
		this.associationNumberUpperBound = associationNumberUpperBound;
	}

	public int getTermLengthLowerBound() {
		return termLengthLowerBound;
	}

	public void setTermLengthLowerBound(int termLengthLowerBound) {
		this.termLengthLowerBound = termLengthLowerBound;
	}

	public boolean isDatabaseCleaning() {
		return isDatabaseCleaning;
	}

	public void setDatabaseCleaning(boolean isDatabaseCleaning) {
		this.isDatabaseCleaning = isDatabaseCleaning;
	}

}
