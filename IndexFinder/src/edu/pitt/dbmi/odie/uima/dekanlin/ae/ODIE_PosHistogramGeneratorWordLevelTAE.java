/*
 *  ODIE_PosHistogramGeneratorWordLevelTAE.java
 *
 *  Incrementally builds a corpus word histogram including information
 *  on Part Of Speech, coverage for NamedEntity 
 *  and coverage for Noun Phrase.
 *
 *  The cache is cleared after each document is processed.
 *
 *
 */

package edu.pitt.dbmi.odie.uima.dekanlin.ae;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.ResultSpecification;
import org.apache.uima.analysis_engine.annotator.AnnotatorConfigurationException;
import org.apache.uima.analysis_engine.annotator.AnnotatorContext;
import org.apache.uima.analysis_engine.annotator.AnnotatorContextException;
import org.apache.uima.analysis_engine.annotator.AnnotatorInitializationException;
import org.apache.uima.analysis_engine.annotator.JTextAnnotator_ImplBase;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;

import edu.mayo.bmi.uima.chunker.type.Chunk;
import edu.mayo.bmi.uima.core.type.NamedEntity;
import edu.mayo.bmi.uima.core.type.WordToken;
import edu.pitt.dbmi.odie.ODIEUtils;
import edu.pitt.dbmi.odie.uima.dekanlin.model.ODIE_HistogramEntry;
import edu.pitt.dbmi.odie.uima.dekanlin.model.ODIE_HistogramEntryFactory;
import edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateAnnotation;
import edu.pitt.dbmi.odie.uima.util.ODIE_IFConstants;
import edu.pitt.dbmi.odie.uima.util.ODIE_UimaUtils;

@SuppressWarnings("deprecation")
public class ODIE_PosHistogramGeneratorWordLevelTAE extends JTextAnnotator_ImplBase {

	private static final long serialVersionUID = 1L;

	/* LOG4J logger based on class name */
	private Logger logger = Logger.getLogger(getClass().getName());

	private AnnotatorContext annotatorContext;

	private Connection conn = null;

	private String tagger = null;

	private String databaseName;

	private ODIE_HistogramEntryFactory histogramFactory;

	public void initialize(AnnotatorContext aContext)
			throws AnnotatorConfigurationException,
			AnnotatorInitializationException {
		super.initialize(aContext);
		annotatorContext = aContext;
		try {
			configJdbcInit();
			configTaggerInit();
			configInit();
			logger.debug("Initialization complete.");
		} catch (AnnotatorContextException ace) {
			throw new AnnotatorConfigurationException(ace);
		} catch (InstantiationException e) {
			throw new AnnotatorConfigurationException(e);
		} catch (IllegalAccessException e) {
			throw new AnnotatorConfigurationException(e);
		} catch (ClassNotFoundException e) {
			throw new AnnotatorConfigurationException(e);
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
	}

	private void configJdbcInit() throws AnnotatorContextException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		String driver = (String) annotatorContext
				.getConfigParameterValue(ODIE_IFConstants.PARAM_DB_DRIVER);
		String url = (String) annotatorContext
				.getConfigParameterValue(ODIE_IFConstants.IF_PARAM_DB_URL);
		String userName = (String) annotatorContext
				.getConfigParameterValue(ODIE_IFConstants.PARAM_DB_USERNAME);
		String password = (String) annotatorContext
				.getConfigParameterValue(ODIE_IFConstants.PARAM_DB_PASSWORD);
		Class.forName(driver).newInstance();
		conn = DriverManager.getConnection(url, userName, password);
		logger.info("Connected to the database at:" + url);

		this.databaseName = ODIEUtils.extractDatabaseNameFromDbUrl(url);
	}

	private void configTaggerInit() throws AnnotatorContextException,
			AnnotatorConfigurationException {
		tagger = (String) annotatorContext
				.getConfigParameterValue(ODIE_IFConstants.IF_PARAM_POS_TAGGER);
	}

	public void configInit() {
		try {
			this.histogramFactory = new ODIE_HistogramEntryFactory();
			this.histogramFactory.initialize(conn, databaseName, "lin");
			this.histogramFactory.createHistogramEntry();
			this.histogramFactory.prepareDMLStatements();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @see JCasAnnotator_ImplBase#process(JCas)
	 */
	@SuppressWarnings("unchecked")
	public void process(JCas aJCas, ResultSpecification resultSpec) {

		//
		// Cache the NamedEntities for this document
		//
		ArrayList<NamedEntity> namedEntities = ODIE_UimaUtils
				.pullNamedEntitiesFromJCas(aJCas);

		//
		// Cache the NPs for this document
		//
		ArrayList<ODIE_GateAnnotation> npTransientNpEntities = ODIE_UimaUtils
				.pullGateNPsFromJCas(aJCas);
		
		ArrayList<Chunk> npChunkEntities = ODIE_UimaUtils
				.pullCTakesNPsFromJCas(aJCas);

		//
		// Histogram the WordTokens while tracking both NER and NP
		// subsume status. (i.e., whether or not each word lies
		// beneath an NER and or an NP.
		//
		FSIndex wordIndex = aJCas.getAnnotationIndex(WordToken.type);
		Iterator<WordToken> wordIter = wordIndex.iterator();
		while (wordIter.hasNext()) {
			WordToken wordToken = wordIter.next();
			String word = wordToken.getCoveredText().toLowerCase();
			String posTag = wordToken.getPartOfSpeech();
			if (posTag == null) {
				posTag = "UNKNOWN";
			}
			String undefinedOntologyUri = "UNDEFINED";
			ODIE_HistogramEntry histogramEntry = this.histogramFactory
					.fetchHistogramEntry(word, posTag, this.tagger,
							undefinedOntologyUri);

			// Increment the frequency
			histogramEntry.setFreq(histogramEntry.getFreq() + 1);

			// Determine whether these words are known named NamedEntities
			String nerState = ODIE_UimaUtils.getNerState(wordToken,
					namedEntities);
			if (histogramEntry.getIsNer() == 0
					&& !nerState.equals("NERNegative")) {
				histogramEntry.setOntologyUri(ODIE_UimaUtils
						.parseUriFromGenSymStateValue(nerState));
				histogramEntry.setIsNer(1);
			}

			// Determine whether these words are known named Noun Phrases
			String npState = "NPNegative" ;
			if (!npTransientNpEntities.isEmpty()) {
				npState = ODIE_UimaUtils.getTransientNpState(wordToken, npTransientNpEntities);
			}
			else {
				npState = ODIE_UimaUtils.getNpState(wordToken, npChunkEntities);
			}
			if (histogramEntry.getIsNp() == 0 && !npState.equals("NPNegative")) {
				histogramEntry.setIsNp(1);
			}
		}

		// Cache the histogram entry
		this.histogramFactory.updateHistogramEntries();
		this.histogramFactory.clearHistogramEntries();

	}

} // class PosHistogramGeneratorPR
