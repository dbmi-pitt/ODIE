package edu.pitt.dbmi.odie.uima.lsp.consumer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import edu.mayo.bmi.uima.core.type.NamedEntity;
import edu.mayo.bmi.uima.core.type.Sentence;
import edu.pitt.dbmi.odie.ODIEUtils;
import edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateAnnotation;
import edu.pitt.dbmi.odie.uima.lsp.model.ODIE_LSP;
import edu.pitt.dbmi.odie.uima.lsp.model.ODIE_Suggestion;
import edu.pitt.dbmi.odie.uima.util.ODIE_IFConstants;
import edu.pitt.dbmi.odie.uima.util.ODIE_IndexFinderUtils;
import edu.pitt.dbmi.odie.uima.util.ODIE_NormalizationUtils;
import edu.pitt.dbmi.odie.uima.util.ODIE_SortedAnnotationSet;
import edu.pitt.dbmi.odie.uima.util.ODIE_UimaUtils;

public class ODIE_CASC_LspSuggestionGenerator extends CasConsumer_ImplBase {

	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ODIE_CASC_LspSuggestionGenerator.class);

	private final static String[][] definitionTable = {
			{ "NP_such_as_NP_and_NP", "NP0 such as {NP1, NP2, and NP}" },
			{ "NP_such_as_NP_or_NP", "NP0 such as {NP1, NP2, or NP}" },
			{ "Such_NP_as_NP_and_NP", "Such NP0 as {NP1,}* and NP" },
			{ "Such_NP_as_NP_or_NP", "Such NP0 as {NP1,}* or NP" },
			{ "NP_and_other_NP", "NP1 {, NP2} * {,} and other NP0" },
			{ "NP_or_other_NP", "NP1 {, NP2} * {,} or other NP0" },
			{ "NP_including_NP_and_NP", "NP0{,} including {NP1 ,}* and NP2" },
			{ "NP_including_NP_or_NP", "NP0{,} including {NP1 ,}* or NP2" },
			{ "NP_especially_NP_and_NP", "NP0{,} especially {NP1 ,}* and NP2" },
			{ "NP_especially_NP_or_NP", "NP0{,} especially {NP1 ,}* or NP2" },
			{ "NP_like_NP", "NP0 like NP1" },
			{ "NP_called_NP", "NP0 called NP1" },
			{ "NP_is_a_NP", "NP0 is a NP1" },
			{ "NP_a_NP_appositive", "NP0, a NP1 (appositive)" },
			{ "NP_also_known_as_NP", "NP0 also known as NP1, NP2, ..." },
			{ "NP_aka_NP", "NP0 a.k.a. NP1, NP2, ..." },
			{ "NP_also_called_NP", "NP0 also called NP1, NP2, ..." },
			{ "NP_so_called_NP", "NP0 so called NP1, NP2," },
			{ "NP_sometimes_known_as_NP",
					"NP0 sometimes known as NP1, NP2, ..." },
			{ "NP_sometimes_called_NP", "NP0 sometimes called NP1, NP2, ..." },
			{ "NP_also_referred_to_as_NP", "NP0 also referred to as NP1 ..." },
			{ "NN_pos_NN", "NN-PL POS NN-PL" },
			{ "NN_of_the_NN", "NN-PL of {the | a} [JJ|NN]* NN" },
			{ "NN_in_the_NN", "NN in {the | a} [JJ|NN]* NN" },
			{ "NN_of_NN", "NN-PL of NN-PL" }, { "NN_in_NN", "NN-PL in NN-PL" } };

	private final static String[][] ruleTable = {
			{ "NP_such_as_NP_and_NP", "Hearst", "hypernomy", "F" },
			{ "NP_such_as_NP_or_NP", "Hearst", "hypernomy", "F" },
			{ "Such_NP_as_NP_and_NP", "Hearst", "hypernomy", "F" },
			{ "Such_NP_as_NP_or_NP", "Hearst", "hypernomy", "F" },
			{ "NP_and_other_NP", "Hearst", "hypernomy", "B" },
			{ "NP_or_other_NP", "Hearst", "hypernomy", "B" },
			{ "NP_including_NP_and_NP", "Hearst", "hypernomy", "F" },
			{ "NP_including_NP_or_NP", "Hearst", "hypernomy", "F" },
			{ "NP_especially_NP_and_NP", "Hearst", "hypernomy", "F" },
			{ "NP_especially_NP_or_NP", "Hearst", "hypernomy", "F" },
			{ "NP_like_NP", "Snow", "hypernomy", "F" },
			{ "NP_called_NP", "Snow", "synonymy", "F" },
			{ "NP_is_a_NP", "Snow", "hypernomy", "B" },
			{ "NP_a_NP_appositive", "Snow", "hypernomy", "B" },
			{ "NP_also_known_as_NP", "Lui", "synonymy", "F" },
			{ "NP_aka_NP", "Lui", "synonymy", "F" },
			{ "NP_also_called_NP", "Lui", "synonymy", "F" },
			{ "NP_so_called_NP", "Lui", "synonymy", "F" },
			{ "NP_sometimes_known_as_NP", "Lui", "synonymy", "F" },
			{ "NP_sometimes_called_NP", "Lui", "synonymy", "F" },
			{ "NP_also_referred_to_as_NP", "Lui", "synonymy", "F" },
			{ "NN_pos_NN", "Meronomy", "meronymy", "F" },
			{ "NN_of_the_NN", "Meronomy", "meronymy", "B" },
			{ "NN_in_the_NN", "Meronomy", "meronymy", "B" },
			{ "NN_of_NN", "Meronomy", "meronymy", "B" },
			{ "NN_in_NN", "Meronomy", "meronymy", "B" } };

	private String databaseName;

	private HashMap<String, String> rule2AuthorMap = new HashMap<String, String>();
	private HashMap<String, String> rule2RelationshipMap = new HashMap<String, String>();
	private HashMap<String, String> rule2DirectionMap = new HashMap<String, String>();
	private HashMap<String, String> rule2DefinitionMap = new HashMap<String, String>();
	private HashMap<String, ODIE_LSP> rule2LspMap = new HashMap<String, ODIE_LSP>();
	private Connection conn = null;

	private ArrayList<NamedEntity> namedEntities;

	public static void main(String[] args) {
		new ODIE_CASC_LspSuggestionGenerator();
	}

	public void initialize() throws ResourceInitializationException {
		try {
			configJdbcInit();

			ODIE_Suggestion.setConn(conn);
			ODIE_Suggestion.setDatabaseName(databaseName);
			ODIE_Suggestion.createSuggested();

			loadRuleTables();

		} catch (InstantiationException e) {
			throw new ResourceInitializationException(e);
		} catch (IllegalAccessException e) {
			throw new ResourceInitializationException(e);
		} catch (ClassNotFoundException e) {
			throw new ResourceInitializationException(e);
		} catch (SQLException e) {
			// throw new ResourceInitializationException(e);
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
		this.databaseName = ODIEUtils.extractDatabaseNameFromDbUrl(url);
	}

	private void loadRuleTables() {
		for (int idx = 0; idx < ruleTable.length; idx++) {
			String[] record = ruleTable[idx];
			String rule = record[0];
			String author = record[1];
			String relationship = record[2];
			String direction = record[3];
			this.rule2AuthorMap.put(rule, author);
			this.rule2RelationshipMap.put(rule, relationship);
			this.rule2DirectionMap.put(rule, direction);
		}
		for (int idx = 0; idx < ruleTable.length; idx++) {
			String[] record = definitionTable[idx];
			String rule = record[0];
			String definition = record[1];
			this.rule2DefinitionMap.put(rule, definition);
		}
		for (int idx = 0; idx < ruleTable.length; idx++) {
			String[] record = ruleTable[idx];
			String rule = record[0];
			ODIE_LSP lsp = new ODIE_LSP();
			lsp.setLspName(rule);
			lsp.setLspAuthor(this.rule2AuthorMap.get(rule));
			lsp.setLspDefinition(this.rule2DefinitionMap.get(rule));
			lsp.setLspRelationship(this.rule2RelationshipMap.get(rule));
			lsp.setLspDirection(this.rule2DirectionMap.get(rule));
			String key = rule;
			rule2LspMap.put(key, lsp);
		}

	}

	@SuppressWarnings("unchecked")
	public void processCas(CAS cas) throws ResourceProcessException {
		try {

			JCas aJCas = cas.getJCas();

			this.namedEntities = ODIE_UimaUtils
					.pullNamedEntitiesFromJCas(aJCas);

			FSIndex sentenceAnnotIndex = aJCas
					.getAnnotationIndex(Sentence.type);
			Iterator<Annotation> sentenceAnnotIter = sentenceAnnotIndex
					.iterator();
			while (sentenceAnnotIter.hasNext()) {
				Annotation uimaAnnot = sentenceAnnotIter.next();
				processDocumentSentence(aJCas, uimaAnnot);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (CASException e) {
			e.printStackTrace();
		}

	}

	public void collectionProcessComplete(ProcessTrace arg0)
			throws ResourceProcessException, IOException {

		// Normalize the score column
		String qualifiedTableName = databaseName + ".suggestion";
		String whereClause = "METHOD like 'LSP%'";
		ODIE_NormalizationUtils.normalizeRanges(conn, qualifiedTableName,
				whereClause, "SCORE", "SCORE");
	}

	private void processDocumentSentence(JCas aJCas, Annotation sentenceAnnot) {
		ODIE_GateAnnotation subsumingAnnotation = new ODIE_GateAnnotation(aJCas);
		subsumingAnnotation.setBegin(sentenceAnnot.getBegin());
		subsumingAnnotation.setEnd(sentenceAnnot.getEnd());
		List<ODIE_GateAnnotation> defaultAnnots = ODIE_UimaUtils
				.getUnderLyingAnnotations(subsumingAnnotation);
		if (defaultAnnots != null) {
			HashSet<String> lspPatterns = getLspPatternTemplate();
			List<ODIE_GateAnnotation> lspAnnots = ODIE_UimaUtils.gateGet(
					defaultAnnots, lspPatterns);
			if (lspAnnots != null && lspAnnots.size() > 0) {
				ODIE_GateAnnotation lspAnnot = lspAnnots.iterator().next();
				processLspAnnot(aJCas, lspAnnot);
			}
		}
	}

	private void processLspAnnot(JCas aJCas, ODIE_GateAnnotation lspAnnot) {
		String lspKey = getLspKey(lspAnnot);
		ODIE_LSP lsp = getLspForKey(lspKey);
		LinkedList<ODIE_GateAnnotation> nounPhrases = getNounPhrasesForLsp(
				aJCas, lsp, lspAnnot);
		if (nounPhrases == null || nounPhrases.isEmpty()) {
			logger.warn("NPs not found beneath "
					+ lspAnnot.getGateAnnotationType() + " ("
					+ lspAnnot.getBegin() + ", " + lspAnnot.getEnd() + ")");
		} else {
			logger.debug("NPs beneath " + lspAnnot.getGateAnnotationType()
					+ " (" + lspAnnot.getBegin() + ", " + lspAnnot.getEnd()
					+ ")");
			for (ODIE_GateAnnotation nounPhraseAnnot : nounPhrases) {
				logger.debug("NP " + nounPhraseAnnot.getGateAnnotationType()
						+ " (" + nounPhraseAnnot.getBegin() + ", "
						+ nounPhraseAnnot.getEnd() + ")");
			}
		}
		LinkedList<ODIE_GateAnnotation> sortedNounPhraseAnnots = eliminateStopwordNounPhrases(nounPhrases);
		if (!sortedNounPhraseAnnots.isEmpty()) {
			ODIE_GateAnnotation headAnnot = sortedNounPhraseAnnots
					.removeFirst();
			String headAnnotNERState = ODIE_UimaUtils.getNerState(headAnnot,
					this.namedEntities);
			boolean isHeadNer = (!headAnnotNERState.startsWith("NERNegative")) ? true
					: false;
			String headTerm = getTermUnder(headAnnot);
			for (ODIE_GateAnnotation tailAnnot : sortedNounPhraseAnnots) {
				String tailAnnotNERState = ODIE_UimaUtils.getNerState(
						tailAnnot, this.namedEntities);
				String tailTerm = getTermUnder(tailAnnot);
				boolean isTailNer = (!tailAnnotNERState
						.startsWith("NERNegative")) ? true : false;
				if (!isHeadNer && isTailNer) {
					String uri = ODIE_UimaUtils
							.parseUriFromGenSymStateValue(tailAnnotNERState);
					incrementSuggestionFrequency(headTerm, uri, lsp);
				} else if (isHeadNer && !isTailNer) {
					String uri = ODIE_UimaUtils
							.parseUriFromGenSymStateValue(headAnnotNERState);
					incrementSuggestionFrequency(tailTerm, uri, lsp);
				}
			}
		}
	}

	private void incrementSuggestionFrequency(String nerNegative,
			String nerPositive, ODIE_LSP lsp) {
		ODIE_Suggestion suggested = ODIE_Suggestion.fetchOrCreateSuggested(
				nerNegative, nerPositive, lsp);
		ODIE_Suggestion.incrementSuggested(suggested);
	}

	private LinkedList<ODIE_GateAnnotation> eliminateStopwordNounPhrases(
			LinkedList<ODIE_GateAnnotation> nounPhraseSet) {
		LinkedList<ODIE_GateAnnotation> result = new LinkedList<ODIE_GateAnnotation>();
		LinkedList<ODIE_GateAnnotation> nounPhraseList = new LinkedList<ODIE_GateAnnotation>();
		nounPhraseList.addAll(nounPhraseSet);
		if (nounPhraseList.size() > 1) { // A single NP matches no patterns
			ODIE_GateAnnotation headNounPhrase = nounPhraseList.removeFirst();
			if (consistsOfStopwordOnly(headNounPhrase)) {
				headNounPhrase = null;
			} else {
				for (ODIE_GateAnnotation tailAnnot : nounPhraseList) {
					if (!consistsOfStopwordOnly(tailAnnot)) {
						result.add(tailAnnot);
					}
				}
			}
			if (headNounPhrase != null && !result.isEmpty()) {
				result.addFirst(headNounPhrase);
			} else {
				result.clear();
			}
		}
		return result;
	}

	private boolean consistsOfStopwordOnly(ODIE_GateAnnotation nounPhrase) {
		boolean result = false;
		String nounPhraseText = nounPhrase.getCoveredText();
		if (ODIE_UimaUtils.filterTerm(nounPhraseText, 0) == null) {
			result = true;
		}
		return result;
	}

	private boolean consistsOfStopwordOnlyFromAnnotations(
			ODIE_GateAnnotation nounPhrase) {
		boolean result = false;
		try {
			Long sPosNounPhrase = new Long(nounPhrase.getBegin());
			Long ePosNounPhrase = new Long(nounPhrase.getEnd());
			HashSet<String> stopWordFilter = new HashSet<String>();
			stopWordFilter.add("Stopword");
			List<ODIE_GateAnnotation> sortedStopWords = ODIE_UimaUtils
					.getUnderLyingAnnotations(nounPhrase, stopWordFilter);
			if (!sortedStopWords.isEmpty() && sortedStopWords.size() == 1) {
				Annotation stopWordAnnot = (Annotation) sortedStopWords
						.iterator().next();
				result = (stopWordAnnot.getBegin() == sPosNounPhrase.intValue())
						&& (stopWordAnnot.getEnd() == ePosNounPhrase.intValue());

			}
		} catch (Exception x) {
			;
		}

		return result;
	}

	private String getTermUnder(Annotation annot) {
		String termFragment = getDocumentFragmentUnder(annot);
		termFragment = ODIE_IndexFinderUtils.forceToSingleLine(termFragment);
		return termFragment;
	}

	private String getDocumentFragmentUnder(Annotation annot) {
		return annot.getCoveredText();
	}

	private LinkedList<ODIE_GateAnnotation> getNounPhrasesForLsp(JCas aJCas,
			ODIE_LSP lsp, Annotation lspAnnot) {
		List<ODIE_GateAnnotation> defaultAnnots = ODIE_UimaUtils.gateGet(aJCas);
		Long sPos = new Long(lspAnnot.getBegin());
		Long ePos = new Long(lspAnnot.getEnd());
		List<ODIE_GateAnnotation> nounPhraseAnnots = ODIE_UimaUtils.gateGet(
				defaultAnnots, "TransientNP", sPos, ePos);
		boolean isAscending = lsp.getLspDirection().toUpperCase().startsWith(
				"F");
		ODIE_SortedAnnotationSet sortedNounPhraseAnnots = new ODIE_SortedAnnotationSet(
				nounPhraseAnnots, isAscending);
		LinkedList<ODIE_GateAnnotation> result = new LinkedList<ODIE_GateAnnotation>();
		result.addAll(sortedNounPhraseAnnots);
		return result;
	}

	private String getLspKey(ODIE_GateAnnotation lspAnnot) {
		HashMap<String, String> features = ODIE_UimaUtils
				.fetchFeatureMapForAnnotation(lspAnnot);
		String ruleName = (String) features.get("rule");
		String lspKey = ruleName;
		return lspKey;
	}

	private ODIE_LSP getLspForKey(String lspKey) {
		return this.rule2LspMap.get(lspKey);
	}

	private HashSet<String> getLspPatternTemplate() {
		HashSet<String> lspPatterns = new HashSet<String>();
		lspPatterns.add("HearstPattern");
		lspPatterns.add("SnowPattern");
		lspPatterns.add("LuiPattern");
		lspPatterns.add("MeronymyPattern");
		return lspPatterns;
	}

}
