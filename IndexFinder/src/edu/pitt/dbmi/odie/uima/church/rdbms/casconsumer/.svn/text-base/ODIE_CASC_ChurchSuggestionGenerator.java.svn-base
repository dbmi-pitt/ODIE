package edu.pitt.dbmi.odie.uima.church.rdbms.casconsumer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
import edu.mayo.bmi.uima.core.type.UmlsConcept;
import edu.pitt.dbmi.odie.ODIEUtils;
import edu.pitt.dbmi.odie.server.indexfinder.ODIE_IndexFinderUtils;
import edu.pitt.dbmi.odie.uima.church.rdbms.model.ODIE_SuggestionFactory;
import edu.pitt.dbmi.odie.uima.church.model.ODIE_AssociationEntryInf;
import edu.pitt.dbmi.odie.uima.church.model.ODIE_HistogramEntryInf;
import edu.pitt.dbmi.odie.uima.church.rdbms.model.ODIE_AssociationEntryFactory;
import edu.pitt.dbmi.odie.uima.church.rdbms.model.ODIE_HistogramEntryFactory;
import edu.pitt.dbmi.odie.uima.church.rdbms.model.ODIE_TermEntryFactory;
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

	private ArrayList<Annotation> termArray = new ArrayList<Annotation>();

	private ArrayList<NamedEntity> namedEntityArray = new ArrayList<NamedEntity>();

	private ArrayList<String> termTextArray = new ArrayList<String>();

	private String databaseName;
	private String tableNamePrefix;

	private ODIE_HistogramEntryFactory nerHistogramEntryFactory = null;

	private ODIE_HistogramEntryFactory histogramEntryFactory = null;

	private ODIE_AssociationEntryFactory associationEntryFactory = null;

	private ODIE_TermEntryFactory termEntryFactory;

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
		Connection c = conn.getConnection();
		if (c == null) {
			return;
		}

		this.tableNamePrefix = (String) getConfigParameterValue(ODIE_IFConstants.IF_PARAM_TABLE_NAME_PREFIX);

		this.nerHistogramEntryFactory = new ODIE_HistogramEntryFactory();
		this.histogramEntryFactory = new ODIE_HistogramEntryFactory();
		this.associationEntryFactory = new ODIE_AssociationEntryFactory();
		this.termEntryFactory = new ODIE_TermEntryFactory();

		this.nerHistogramEntryFactory.setDatabaseCleaning(isDatabaseCleaning());
		this.histogramEntryFactory.setDatabaseCleaning(isDatabaseCleaning());
		this.associationEntryFactory.setDatabaseCleaning(isDatabaseCleaning());
		this.termEntryFactory.setDatabaseCleaning(isDatabaseCleaning());

		this.nerHistogramEntryFactory.initialize(conn, databaseName,
				"document_ner");
		this.histogramEntryFactory.initialize(conn, databaseName,
				tableNamePrefix);
		this.associationEntryFactory.initialize(conn, databaseName,
				tableNamePrefix);
		this.associationEntryFactory
				.setHistogramEntryFactory(this.histogramEntryFactory);
		this.termEntryFactory.initialize(conn, databaseName, tableNamePrefix);

		this.suggestionFactory = new ODIE_SuggestionFactory();
		this.suggestionFactory.setDatabaseName(databaseName);
		this.suggestionFactory.setConnection(this.conn);
		this.suggestionFactory
				.setAssociationEntryFactory(this.associationEntryFactory);
		this.suggestionFactory
				.setHistogramEntryFactory(this.histogramEntryFactory);
		this.suggestionFactory.setTermEntryFactory(termEntryFactory);
		if (isDatabaseCleaning()) {
			this.suggestionFactory.dropSuggestionTable();
		}
		this.suggestionFactory.buildSuggestedNamedEntityTable();
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
			// Clear cache structures
			//
			this.termArray.clear();
			this.termTextArray.clear();
			this.namedEntityArray.clear();

			//
			// Derive the set of terms stored contiguously in the
			// document. Store results in the this.termArray
			//
			deriveTermArrayFromNounPhraseAnnotations(jcas);

			//
			// Cache the NamedEntities for this document
			// these are prerequisite contributions of earlier
			// pipeline components
			//
			this.namedEntityArray = ODIE_UimaUtils
					.pullNamedEntitiesFromJCas(jcas);

			//
			// Histogram the NamedEntities across the document set
			//
			histogramNamedEntities(this.namedEntityArray);

			//
			// Histogram only those terms that meet the filter criteria
			// of alphabetic characters with length greater than
			// getTermLengthLowerBound().
			//
			histogramTermArray();

			//
			// Gather association information
			//

			// Look at a window 4 words ahead
			moveWindowForwardIncrementingMutualInfo();

			// Look a window 4 words behind
			// Note: this is essentially a double count
			// of forward comparison and can be ignored.
			// If used the contribution should be set to 0.5
			// in each direction
			// moveWindowBackwardIncrementingMutualInfo() ;

			// When the association table size has exceeded its maximum
			// allowable for
			// continued efficient processing prune association entries that are
			// infrequent
			// and stale
			//
			// this.associationEntryFactory.pruneInfrequentStaleAssociationEntries(500000L)
			// ;

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	private void histogramNamedEntities(ArrayList<NamedEntity> namedEntityArray) {
		for (NamedEntity namedEntity : namedEntityArray) {
			UmlsConcept umlsConcept = (UmlsConcept) namedEntity
					.getOntologyConceptArr(0);
			String codeName = umlsConcept.getCode();
			ODIE_HistogramEntryInf histogramEntry = lookUpNeHistogramEntry(codeName);
			histogramEntry.setIsNer(1);
			String uri = umlsConcept.getOid() ;
			if (uri != null && uri.contains("#")) {
				final String[] uriParts = uri.split("#") ;
				uri = uriParts[1] + "/" + uriParts[0] ;
				histogramEntry.setOntologyUri(uri) ;
				histogramEntry.setFreq(histogramEntry.getFreq() + 1);
				this.nerHistogramEntryFactory.updateHistogramEntry(histogramEntry);
			}
			
		}
	}

	private void histogramTermArray() {
		//
		// Histogram only those terms that meet the filter criteria
		// of alphabetic characters with length greater than
		// getTermLengthLowerBound().
		//
		for (Annotation term : this.termArray) {
			String coveredTermText = term.getCoveredText();
			coveredTermText = coveredTermText.toLowerCase();
			// coveredTermText =
			// ODIE_IndexFinderUtils.normalizeTerm(coveredTermText) ;
			String normalizedTermText = ODIE_IndexFinderUtils
					.normalizeTermSortedOrder(coveredTermText);
			this.termTextArray.add(normalizedTermText); // Count - may be an
														// empty String
														// placeholder
			if (normalizedTermText.length() == 0) {
				continue;
			}
			String chunkType = deriveChunkTypeFromTermAnnotation(term);
			if (isNounPhraseChunkType(chunkType)) {
				if (ODIE_UimaUtils.filterTerm(normalizedTermText,
						getTermLengthLowerBound()) != null) {
					ODIE_HistogramEntryInf histogramEntry = lookUpWordHistogramEntry(normalizedTermText);

					histogramEntry.setFreq(histogramEntry.getFreq() + 1);
					updateNamedEntityStatusOfHistogramEntry(term,
							histogramEntry, namedEntityArray);
					this.histogramEntryFactory
							.updateHistogramEntry(histogramEntry);

					//
					// Save one copy of the actual term before normalization
					//
					this.termEntryFactory.fetchOrCreateTermEntry(
							histogramEntry.getId(), coveredTermText);
				}
			}
		}
	}

	private void moveWindowForwardIncrementingMutualInfo() {
		// Look at a window four terms ahead
		for (int idx = 0; idx < this.termTextArray.size() - 1; idx++) {
			ODIE_HistogramEntryInf wordOneEntry = this.histogramEntryFactory
					.selectHistogramEntry(termTextArray.get(idx));
			if (wordOneEntry == null) {
				continue;
			}
			for (int jdx = idx + 1; jdx < Math.min(this.termTextArray.size(),
					idx + getWindowSize()); jdx++) {
				ODIE_HistogramEntryInf wordTwoEntry = this.histogramEntryFactory
						.selectHistogramEntry(termTextArray.get(jdx));
				if (wordTwoEntry == null) {
					continue;
				}
				String wordOneText = ODIE_UimaUtils.filterTerm(
						this.termTextArray.get(idx), getTermLengthLowerBound());
				String wordTwoText = ODIE_UimaUtils.filterTerm(
						this.termTextArray.get(jdx), getTermLengthLowerBound());
				if (wordOneText != null && wordTwoText != null
						&& wordOneEntry.getId() != wordTwoEntry.getId()) {
					logger.debug("Forward Incrementing : " + wordOneText + "<"
							+ wordOneEntry.getId() + ">, " + wordTwoText + "<"
							+ wordTwoEntry.getId() + ">, ");
					incrementMutualInformationFrequency(wordOneEntry,
							wordTwoEntry, 1.0d);
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private void moveWindowBackwardIncrementingMutualInfo() {
		// Look at an window 4 terms behind
		for (int idx = this.termTextArray.size() - 1; idx >= 0; idx--) {
			ODIE_HistogramEntryInf wordOneEntry = this.histogramEntryFactory
					.selectHistogramEntry(termTextArray.get(idx));
			if (wordOneEntry == null) {
				continue;
			}
			for (int jdx = idx - 1; jdx >= Math.max(0, idx - getWindowSize()
					+ 1); jdx--) {
				ODIE_HistogramEntryInf wordTwoEntry = this.histogramEntryFactory
						.selectHistogramEntry(termTextArray.get(jdx));
				if (wordTwoEntry == null) {
					continue;
				}
				String wordOneText = ODIE_UimaUtils.filterTerm(
						this.termTextArray.get(idx), getTermLengthLowerBound());
				String wordTwoText = ODIE_UimaUtils.filterTerm(
						this.termTextArray.get(jdx), getTermLengthLowerBound());
				logger.debug("Backward Incrementing : " + wordOneText + "<"
						+ wordOneEntry.getId() + ">, " + wordTwoText + "<"
						+ wordTwoEntry.getId() + ">, ");
				incrementMutualInformationFrequency(wordOneEntry, wordTwoEntry,
						0.5d);
			}
		}
	}

	private void updateNamedEntityStatusOfHistogramEntry(Annotation term,
			ODIE_HistogramEntryInf histogramEntry,
			ArrayList<NamedEntity> namedEntities) {
		// Determine whether these words are known named
		// NamedEntities
		String nerState = ODIE_UimaUtils.getNerState(term, namedEntities);
		if (histogramEntry.getIsNer() == 0
				&& !nerState.startsWith("NERNegative")) {
			String uri = ODIE_UimaUtils.parseUriFromGenSymStateValue(nerState);
			histogramEntry.setIsNer(1);
			histogramEntry.setOntologyUri(uri);
		}
	}

	private boolean isNounPhraseChunkType(String chunkType) {
		return chunkType.equals("TransientNP")
				|| chunkType.equalsIgnoreCase("NP");
	}

	private String deriveChunkTypeFromTermAnnotation(Annotation term) {
		String chunkType = null;
		if (term instanceof ODIE_GateAnnotation) {
			ODIE_GateAnnotation theChunk = (ODIE_GateAnnotation) term;
			chunkType = theChunk.getGateAnnotationType();
		} else if (term instanceof Chunk) {
			chunkType = ((Chunk) term).getChunkType();
		}
		return chunkType;
	}

	private void deriveTermArrayFromNounPhraseAnnotations(JCas jcas) {
		//
		// Disclaimer:
		// This code is complex because it alternate prerequisite
		// pipeline configurations
		//
		// 1) A pipeline that generates ODIE_GateAnnotation wrappers for
		// Tokens, Lookups, and NounPhrases
		// 2) A pipeline that works on first class Chunk objects from
		// cTAKES.
		// The default behavior is (1)

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
			nounPhraseArray.addAll(ODIE_UimaUtils.pullCTakesNPsFromJCas(jcas));
		}

		//
		// Filter - used only for cTAKES NounChunks
		//
		for (Annotation nounPhraseOrWord : nounPhraseArray) {
			if ((nounPhraseOrWord instanceof ODIE_StopWord)) {
				;
			} else if (isChunkOtherThanNoun(nounPhraseOrWord)) {
				;
			} else {
				this.termArray.add(nounPhraseOrWord);
			}
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

	private void incrementMutualInformationFrequency(
			ODIE_HistogramEntryInf wordOneEntry,
			ODIE_HistogramEntryInf wordTwoEntry, double incrementAmount) {
		ODIE_AssociationEntryInf associationEntry = this.associationEntryFactory
				.fetchAssociationEntry(wordOneEntry, wordTwoEntry);
		associationEntry.setWordOneFreq(wordOneEntry.getFreq() + 0.0d);
		associationEntry.setWordTwoFreq(wordTwoEntry.getFreq() + 0.0d);
		associationEntry.setIsNerOne(wordOneEntry.getIsNer());
		associationEntry.setIsNerTwo(wordTwoEntry.getIsNer());
		associationEntry.setFreq(associationEntry.getFreq() + incrementAmount);
		// ixy is calculated internally
		this.associationEntryFactory.updateAssociationEntry(associationEntry);
	}

	private ODIE_HistogramEntryInf lookUpWordHistogramEntry(String wordText) {
		ODIE_HistogramEntryInf result = this.histogramEntryFactory
				.fetchHistogramEntry(wordText);
		return result;
	}

	private ODIE_HistogramEntryInf lookUpNeHistogramEntry(String neCodeName) {
		ODIE_HistogramEntryInf result = this.nerHistogramEntryFactory
				.fetchHistogramEntry(neCodeName);
		return result;
	}
	
	public void collectionProcessComplete(ProcessTrace arg0)
			throws ResourceProcessException, IOException {
		this.associationEntryFactory.buildAssociationView();
		double numberOfWords = this.histogramEntryFactory.getNumberOfEntries();
		this.associationEntryFactory.calculateIxy(numberOfWords);
		this.suggestionFactory.buildSuggestedNamedEntityAdditions();
		this.histogramEntryFactory.close();
		this.associationEntryFactory.close();
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
