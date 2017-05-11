package edu.pitt.dbmi.odie.uima.lsp.ae;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.ResultSpecification;
import org.apache.uima.analysis_engine.annotator.AnnotatorConfigurationException;
import org.apache.uima.analysis_engine.annotator.AnnotatorContext;
import org.apache.uima.analysis_engine.annotator.AnnotatorContextException;
import org.apache.uima.analysis_engine.annotator.AnnotatorInitializationException;
import org.apache.uima.analysis_engine.annotator.AnnotatorProcessException;
import org.apache.uima.analysis_engine.annotator.JTextAnnotator_ImplBase;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.mayo.bmi.uima.core.type.NamedEntity;
import edu.pitt.dbmi.odie.ODIEUtils;
import edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateAnnotation;
import edu.pitt.dbmi.odie.uima.lsp.model.ODIE_ScoredTerm;
import edu.pitt.dbmi.odie.uima.util.ODIE_IFConstants;
import edu.pitt.dbmi.odie.uima.util.ODIE_UimaUtils;

@SuppressWarnings("deprecation")
public class ODIE_LspTAE extends JTextAnnotator_ImplBase {

	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger.getLogger(ODIE_LspTAE.class);

	private AnnotatorContext annotatorContext;

	private Connection conn = null;
	
	private ODIE_ConceptConsolidator consolidator ;

	private static String scoringKeySeparator = ":";

	/*
	 * Scoring rules
	 */
	private static final String[][] scoringTable = {
			{ "NERNegative", "LspPositive", "5" },
			{ "NERNegative", "LspNegative", "4" },
			{ "NERPartial",  "LspPositive", "3" },
			{ "NERPartial",  "LspNegative", "2" },
			{ "NERPositive", "LspPositive", "1" },
			{ "NERPositive", "LspNegative", "0" } };

	private static final HashMap<String, Long> scoringHashMap = new HashMap<String, Long>();

	public void initialize(AnnotatorContext aContext)
			throws AnnotatorConfigurationException,
			AnnotatorInitializationException {
		super.initialize(aContext);
		annotatorContext = aContext;
		try {
			configJdbcInit();
			ODIE_ScoredTerm.setConn(this.conn);
			ODIE_ScoredTerm.setDatabaseName(getDatabaseNameFromUrl());
			ODIE_ScoredTerm.initialize();
			buildScoringHashMap();

			this.consolidator = new ODIE_ConceptConsolidator() ;
			this.consolidator.setConn(this.conn) ;
			this.consolidator.initialize() ;
	
		} catch (AnnotatorContextException ace) {
			throw new AnnotatorConfigurationException(ace);
		}
	}

	private String getDatabaseNameFromUrl() {
		String result = null;
		try {
			String dbUrl = (String) annotatorContext
					.getConfigParameterValue(ODIE_IFConstants.IF_PARAM_DB_URL);
			result = ODIEUtils.extractDatabaseNameFromDbUrl(dbUrl);

		} catch (AnnotatorContextException e) {

			e.printStackTrace();
		}
		return result;
	}

	private void buildScoringHashMap() {
		for (int idx = 0; idx < scoringTable.length; idx++) {
			String[] score = scoringTable[idx];
			String key = score[0] + scoringKeySeparator + score[1];
			Long value = new Long(score[2]);
			scoringHashMap.put(key, value);
		}
	}

	@SuppressWarnings("deprecation")
	private void configJdbcInit() throws AnnotatorContextException,
			AnnotatorConfigurationException {
		try {
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

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void process(JCas cas, ResultSpecification resultSpec)
			throws AnnotatorProcessException {
		ArrayList<NamedEntity> namedEntities = ODIE_UimaUtils.pullNamedEntitiesFromJCas(cas);
		ArrayList<Annotation> lspAnnotations = pullLspAnnotationsFromJCas(cas) ;
		ArrayList<Annotation> nounPhraseAnnotations = pullNPAnnotatonFromJCas(cas) ;
		this.consolidator.processDocument(cas);
		
		// For each nounPhrase
		for (Annotation nounPhraseAnnot : nounPhraseAnnotations) {
			String lspState = getLspState(nounPhraseAnnot, lspAnnotations);
			String nerState = ODIE_UimaUtils.getNerState(nounPhraseAnnot, namedEntities);
			String key = nerState + scoringKeySeparator + lspState;
			Long score = scoringHashMap.get(key);
			String nounPhraseCoveredText = nounPhraseAnnot.getCoveredText();
			logger.debug("Score of " + key + " given to "
					+ nounPhraseCoveredText);
			ODIE_ScoredTerm odieTerm = ODIE_ScoredTerm
					.fetchOrCreateOdieTerm(nounPhraseCoveredText);
			odieTerm.setFreq(odieTerm.getFreq() + 1L);
			odieTerm.setScore(Math.max(odieTerm.getScore(), score));
			ODIE_ScoredTerm.updateOdieTerm(odieTerm);
		}

	}

	private String getLspState(Annotation nounPhraseAnnot,
			ArrayList<Annotation> lspAnnotations) {
		String lspState = "LspNegative";

		for (Annotation lspAnnotation : lspAnnotations) {
			long nounPhraseSPos = nounPhraseAnnot.getBegin();
			long nounPhraseEPos = nounPhraseAnnot.getEnd();
			long lspSPos = lspAnnotation.getBegin();
			long lspEPos = lspAnnotation.getEnd();
			if (nounPhraseSPos >= lspSPos && nounPhraseEPos <= lspEPos) {
				lspState = "LspPositive";
			}
		}
		return lspState;
	}


	
	@SuppressWarnings("unchecked")
	private ArrayList<Annotation> pullNPAnnotatonFromJCas(JCas cas) {
		ArrayList<Annotation> result = new ArrayList<Annotation>() ;
		try {
			FSIndex uimaAnnotIndex = cas
					.getAnnotationIndex(ODIE_GateAnnotation.type);
			Iterator<ODIE_GateAnnotation> uimaAnnotIter = uimaAnnotIndex.iterator();
			while (uimaAnnotIter.hasNext()) {
				ODIE_GateAnnotation uimaAnnot = uimaAnnotIter.next();
				if (uimaAnnot.getGateAnnotationType().equals("NP")) {
					result.add(uimaAnnot) ;
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private ArrayList<Annotation> pullLspAnnotationsFromJCas(JCas cas) {
		ArrayList<Annotation> result = new ArrayList<Annotation>() ;
		try {
			FSIndex uimaAnnotIndex = cas
					.getAnnotationIndex(ODIE_GateAnnotation.type);
			Iterator<ODIE_GateAnnotation> uimaAnnotIter = uimaAnnotIndex.iterator();
			while (uimaAnnotIter.hasNext()) {
				ODIE_GateAnnotation uimaAnnot = uimaAnnotIter.next();
				if (uimaAnnot.getGateAnnotationType().endsWith("Pattern")) {
					result.add(uimaAnnot) ;
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return result;
	}


}

