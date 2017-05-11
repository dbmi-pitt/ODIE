package edu.pitt.dbmi.odie.uima.lsp.ae;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.mayo.bmi.uima.core.type.Sentence;
import edu.pitt.dbmi.odie.uima.church.rdbms.model.ODIE_AssociationEntryFactory;
import edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateAnnotation;
import edu.pitt.dbmi.odie.uima.lsp.model.ODIE_LSP;
import edu.pitt.dbmi.odie.uima.lsp.model.ODIE_Relation;
import edu.pitt.dbmi.odie.uima.lsp.model.ODIE_Relationship;
import edu.pitt.dbmi.odie.uima.lsp.model.ODIE_Sentence;
import edu.pitt.dbmi.odie.uima.lsp.model.ODIE_Term;
import edu.pitt.dbmi.odie.uima.util.ODIE_IndexFinderUtils;
import edu.pitt.dbmi.odie.uima.util.ODIE_SortedAnnotationSet;
import edu.pitt.dbmi.odie.uima.util.ODIE_UimaUtils;

public class ODIE_ConceptConsolidator {
	
	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ODIE_ConceptConsolidator.class);

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
			{ "NP_is_a_NP",  "Snow", "hypernomy", "B" },
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

	private final static String[] corpusTable = { "cTAKES" };

	private static final String LPS_KEY_SEPERATOR = "#";

	private Connection conn;

	private HashMap<String, String> rule2AuthorMap = new HashMap<String, String>();
	private HashMap<String, String> rule2RelationshipMap = new HashMap<String, String>();
	private HashMap<String, String> rule2DirectionMap = new HashMap<String, String>();
	private HashMap<String, String> rule2DefinitionMap = new HashMap<String, String>();
	private HashMap<String, ODIE_LSP> rule2LspMap = new HashMap<String, ODIE_LSP>();
	private HashMap<String, Boolean> rule2DocumentHit = new HashMap<String, Boolean>();

	private String currentCorpus = "cTAKES";
	private String currentRule = "NP_such_as_NP_and_NP";
	private static Long currentDocumentId = 0L;

	private ODIE_LSP allLsp;
	
	private JCas aJCas;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//
		// Set up the ConceptConsoldation engine
		//
		//		ODIE_ConceptConsolidator consolidator = new ODIE_ConceptConsolidator();

	}

	public ODIE_ConceptConsolidator() {
	}

	/**
	 * Method createGateDocumentFromString.
	 * 
	 * Should be called after setting the connection
	 */
	public void initialize() {
		//
		// Initialize database objects
		//
		initializeDbObjects();

		//
		// Load rule tables
		//
		loadRuleTables();
	}

	private void initializeDbObjects() {

		ODIE_LSP.setConn(this.conn);
		ODIE_Sentence.setConn(this.conn);
		ODIE_Relation.setConn(this.conn);
		ODIE_Term.setConn(this.conn);
		ODIE_Relationship.setConn(this.conn);

		ODIE_LSP.dropLsp();
		ODIE_Sentence.setTableName("lsp_sentence");
		ODIE_Sentence.dropSentence();
		ODIE_Sentence.setTableName("lsp_unique_sentence");
		ODIE_Sentence.dropSentence();
		ODIE_Relation.dropRelation();
		ODIE_Relationship.dropRelationship();
		ODIE_Term.dropTerm();

		ODIE_LSP.createLsp();
		ODIE_Sentence.setTableName("lsp_sentence");
		ODIE_Sentence.createSentence();
		ODIE_Sentence.setTableName("lsp_unique_sentence");
		ODIE_Sentence.createSentence();
		ODIE_Relationship.createRelationship();
		ODIE_Term.createTerm();
		ODIE_Relation.createRelation();

		ODIE_Relationship.generateDefaultRelationships();

		this.allLsp = generateAllLSPForCorpus("*");

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
			for (int jdx = 0; jdx < corpusTable.length; jdx++) {
				String corpus = corpusTable[jdx];
				ODIE_LSP lsp = new ODIE_LSP();
				lsp.setLspCorpus(corpus);
				lsp.setLspName(rule);
				lsp.setLspAuthor(this.rule2AuthorMap.get(rule));
				lsp.setLspDefinition(this.rule2DefinitionMap.get(rule));
				lsp.setLspRelationship(this.rule2RelationshipMap.get(rule));
				lsp.setLspDirection(this.rule2DirectionMap.get(rule));
				lsp.setLspDocuments(0);
				lsp.setLspSentences(0);
				lsp.setLspUniqueSentence(0);
				lsp.setLspBytes(0L);
				lsp = ODIE_LSP.fetchOrCreateLsp(lsp);
				String key = rule + "#" + corpus;
				rule2LspMap.put(key, lsp);
			}
		}
	}

	private ODIE_LSP generateAllLSPForCorpus(String corpus) {
		ODIE_LSP lsp = new ODIE_LSP();
		lsp.setLspCorpus(corpus);
		lsp.setLspAuthor("*");
		lsp.setLspName("*");
		lsp.setLspDefinition("*");
		lsp.setLspRelationship("*");
		lsp.setLspDirection("*");
		lsp.setLspDocuments(0);
		lsp.setLspSentences(0);
		lsp.setLspUniqueSentence(0);
		lsp.setLspBytes(0L);
		lsp = ODIE_LSP.fetchOrCreateLsp(lsp);
		return lsp;
	}

	@SuppressWarnings("unchecked")
	public void processDocument(JCas aJCas) {
		this.aJCas = aJCas ;
		incrementDocumentCount(allLsp, 1);
		try {
			FSIndex uimaAnnotIndex = aJCas
					.getAnnotationIndex(Sentence.type);
			int sentenceCount = 0 ;
			rule2DocumentHit.clear();
			Iterator<Annotation> uimaAnnotIter = uimaAnnotIndex.iterator();
			while (uimaAnnotIter.hasNext()) {
				Annotation uimaAnnot = uimaAnnotIter.next();
				processDocumentSentence(uimaAnnot);
				sentenceCount++ ;
			}
			incrementSentenceCount(allLsp, sentenceCount);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		currentDocumentId++;
	}
	
	private void processDocumentSentence(Annotation sentenceAnnot) {
		String sentence = 
				sentenceAnnot.getCoveredText() ;
		ODIE_GateAnnotation subsumingAnnotation = new ODIE_GateAnnotation(aJCas) ;
		subsumingAnnotation.setBegin(sentenceAnnot.getBegin()) ;
		subsumingAnnotation.setEnd(sentenceAnnot.getEnd()) ;
		List<ODIE_GateAnnotation> defaultAnnots = ODIE_UimaUtils.getUnderLyingAnnotations(subsumingAnnotation) ;
		if (defaultAnnots == null) {
			System.err.println("No annotations found for sentence " + sentence);
		} else {
			HashSet<String> lspPatterns = getLspPatternTemplate();
			List<ODIE_GateAnnotation> lspAnnots = ODIE_UimaUtils.gateGet(defaultAnnots, lspPatterns);
			if (lspAnnots != null && lspAnnots.size() > 0) {
				if (sentence.length() > ODIE_Sentence.MAX_SENTENCE_LENGTH) {
					System.err
							.println("Sentence exceeds maximum length for storage "
									+ sentence);
				} else {
					ODIE_GateAnnotation lspAnnot = lspAnnots.iterator().next();
					processLspAnnot(lspAnnot);
				}
			}
		}
	}

	private void processLspAnnot(ODIE_GateAnnotation lspAnnot) {
		String lspKey = getLspKey(lspAnnot);
		ODIE_LSP lsp = getLspForKey(lspKey);
		LinkedList<ODIE_GateAnnotation> sortedNounPhraseAnnots = eliminateStopwordNounPhrases(getNounPhrasesForLsp(
				lsp, lspAnnot));
		if (!sortedNounPhraseAnnots.isEmpty()) {
			oneTimeIncrementLspDocumentCount(lsp, lspKey);
			lsp.setLspSentences(lsp.getLspSentences() + 1);
			ODIE_Sentence odieSentence = ODIE_Sentence.newBlankSentence();
			odieSentence.setLsp(lsp);
			odieSentence.setLspId(lsp.getId());
			odieSentence.setDocumentId(currentDocumentId);
			odieSentence.setSPosition(new Long(lspAnnot.getBegin()));
			odieSentence.setEPosition(new Long(lspAnnot.getEnd()));
			odieSentence.setFragment(getDocumentFragmentUnder(lspAnnot));
			odieSentence.setHeadTerm(getTermUnder(sortedNounPhraseAnnots
					.removeFirst()));
			odieSentence
					.setTailTerms(generateSortedTerms(sortedNounPhraseAnnots));
			ODIE_Sentence existingSentence = ODIE_Sentence
					.fetchSentence(odieSentence);
			if (existingSentence.getFragment().equals(ODIE_Sentence.BLANK)) {
				lsp.setLspUniqueSentence(lsp.getLspUniqueSentence() + 1);
				existingSentence.setId(null);
				existingSentence.setLsp(odieSentence.getLsp());
				existingSentence.setLspId(odieSentence.getLspId());
				existingSentence.setDocumentId(odieSentence.getDocumentId());
				existingSentence.setFragment(odieSentence.getFragment());
				existingSentence.setTerms(odieSentence.getTerms());
				existingSentence.setSPosition(odieSentence.getSPosition());
				existingSentence.setEPosition(odieSentence.getEPosition());
				ODIE_Sentence.setTableName("lsp_unique_sentence");
				ODIE_Sentence.insertSentence(existingSentence);
			}
			ODIE_Sentence.setTableName("lsp_sentence");
			ODIE_Sentence.insertSentence(odieSentence);
		}
		lsp = ODIE_LSP.updateLsp(lsp);
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
		try {
			Long sPosNounPhrase = new Long(nounPhrase.getBegin());
			Long ePosNounPhrase = new Long(nounPhrase.getEnd()) ;
			HashSet<String> stopWordFilter = new HashSet<String>() ;
			stopWordFilter.add("Stopword") ;
			List<ODIE_GateAnnotation> sortedStopWords = ODIE_UimaUtils.getUnderLyingAnnotations(nounPhrase, stopWordFilter) ;
			if (!sortedStopWords.isEmpty() && sortedStopWords.size() == 1) {
				Annotation stopWordAnnot = (Annotation) sortedStopWords
						.iterator().next();
				result = (stopWordAnnot.getBegin() == sPosNounPhrase
						.intValue())
						&& (stopWordAnnot.getEnd() == ePosNounPhrase
								.intValue());

			}
		} catch (Exception x) {
			;
		}

		return result;
	}

	private void incrementSentenceCount(ODIE_LSP lsp, int increment) {
		lsp.setLspSentences(lsp.getLspSentences() + increment);
		ODIE_LSP.updateLsp(lsp);
	}

	private void incrementDocumentCount(ODIE_LSP lsp, int increment) {
		lsp.setLspDocuments(lsp.getLspDocuments() + increment);
		ODIE_LSP.updateLsp(lsp);
	}

	private HashSet<String> getLspPatternTemplate() {
		HashSet<String> lspPatterns = new HashSet<String>();
		lspPatterns.add("HearstPattern");
		lspPatterns.add("SnowPattern");
		lspPatterns.add("LuiPattern");
		lspPatterns.add("MeronymyPattern");
		return lspPatterns;
	}

	private ArrayList<String> generateSortedTerms(
			LinkedList<ODIE_GateAnnotation> sortedNounPhraseAnnots) {
		ArrayList<String> termsToSort = new ArrayList<String>();
		for (ODIE_GateAnnotation nounPhraseAnnot : sortedNounPhraseAnnots) {
			termsToSort.add(getTermUnder(nounPhraseAnnot));
		}
		Collections.sort(termsToSort);
		return termsToSort;
	}

	private String getLspKey(ODIE_GateAnnotation lspAnnot) {
		HashMap<String, String> features = ODIE_UimaUtils.fetchFeatureMapForAnnotation(lspAnnot) ;
		String ruleName = (String) features.get("rule");
		String lspKey = ruleName + LPS_KEY_SEPERATOR + this.currentCorpus;
		return lspKey;
	}

	private ODIE_LSP getLspForKey(String lspKey) {
		return this.rule2LspMap.get(lspKey);
	}

	private void oneTimeIncrementLspDocumentCount(ODIE_LSP lsp, String lspKey) {
		if (this.rule2DocumentHit.get(lspKey) == null) {
			incrementDocumentCount(lsp, 1);
			this.rule2DocumentHit.put(lspKey, new Boolean(true));
		}
	}

	@SuppressWarnings("unchecked")
	private LinkedList<ODIE_GateAnnotation> getNounPhrasesForLsp(ODIE_LSP lsp,
			Annotation lspAnnot) {
		List<ODIE_GateAnnotation> defaultAnnots = ODIE_UimaUtils.gateGet(this.aJCas) ;
		Long sPos = new Long(lspAnnot.getBegin()) ;
		Long ePos = new Long(lspAnnot.getEnd()) ;
		List<ODIE_GateAnnotation> nounPhraseAnnots = ODIE_UimaUtils.gateGet(defaultAnnots, "NP", sPos,
						ePos);
		boolean isAscending = lsp.getLspDirection().toUpperCase().startsWith(
				"F");
		ODIE_SortedAnnotationSet sortedNounPhraseAnnots = new ODIE_SortedAnnotationSet(
				nounPhraseAnnots, isAscending);
		LinkedList<ODIE_GateAnnotation> result = new LinkedList<ODIE_GateAnnotation>();
		result.addAll(sortedNounPhraseAnnots);
		return result;
	}

	private String getDocumentFragmentUnder(Annotation annot) {
		return annot.getCoveredText() ;
	}

	private String getTermUnder(Annotation annot) {
		String termFragment = getDocumentFragmentUnder(annot);
		termFragment = ODIE_IndexFinderUtils.forceToSingleLine(termFragment);
		return termFragment;
	}

	@SuppressWarnings({ "unused", "unchecked" })
	private void storeRelation(Annotation lspAnnot,
			ODIE_LSP lsp) {
		List<ODIE_GateAnnotation> defaultAnnots = ODIE_UimaUtils.gateGet(aJCas) ;
		Long sPos = new Long(lspAnnot.getBegin()) ;
		Long ePos = new Long(lspAnnot.getEnd()) ;
		List<ODIE_GateAnnotation> nounPhrases = ODIE_UimaUtils.gateGet(defaultAnnots, "NP",
				sPos,
				ePos);
		ODIE_SortedAnnotationSet sortedNounPhrases = new ODIE_SortedAnnotationSet(
				lsp.getLspDirection().toUpperCase().startsWith("F"));
		sortedNounPhrases.setAnnotationSet(nounPhrases);
		ODIE_Relationship relationship = ODIE_Relationship
				.fetchOrCreateRelationship(lsp.getLspRelationship());
		Iterator<ODIE_GateAnnotation> iterator = sortedNounPhrases.iterator();
		if (iterator.hasNext()) {
			Annotation termOneNounPhrase = iterator.next();
			String termOneText = 
					termOneNounPhrase.getCoveredText() ;
			termOneText = ODIE_IndexFinderUtils.forceToSingleLine(termOneText);
			ODIE_Term odieTermOne = ODIE_Term.fetchOrCreateTerm(termOneText);
			while (iterator.hasNext()) {
				Annotation termTwoNounPhrase = iterator.next();
				String termTwoText = 
						termTwoNounPhrase.getCoveredText() ;
				termTwoText = ODIE_IndexFinderUtils
						.forceToSingleLine(termTwoText);
				ODIE_Term odieTermTwo = ODIE_Term
						.fetchOrCreateTerm(termTwoText);
				ODIE_Relation odieRelation = ODIE_Relation
						.fetchOrCreateRelation(odieTermOne, odieTermTwo,
								relationship);
				ODIE_Relation.incrementRelation(odieRelation);
			}
		}

	}

	@SuppressWarnings("unused")
	private void buildSomeTestDatabaseObjects() {
		ODIE_Term testTerm1 = ODIE_Term.fetchOrCreateTerm("Gleason grade");
		System.out.println("Stored and retrieved term => " + testTerm1);
		ODIE_Term testTerm2 = ODIE_Term.fetchOrCreateTerm("Disease grade");
		System.out.println("Retrieved term => " + testTerm2);
		ODIE_Relationship relationship = ODIE_Relationship
				.fetchOrCreateRelationship(ODIE_Relationship.RELATIONSHIP_HYPERNOMY);
		System.out.println("Retrieved relationship => " + relationship);
		ODIE_Relation relation = ODIE_Relation.fetchOrCreateRelation(testTerm1,
				testTerm2, relationship);
		System.out.println("Retrieved relations => " + relation);
		ODIE_Relation.incrementRelation(relation);
		System.out.println("Incremented relation => " + relation);
	}

	@SuppressWarnings("unused")
	private String parseAuthorFromRuleName(String ruleName) {
		// Use the representational invariant here that HearstRule_01 => Hearst
		// etc.
		String result = ruleName;
		result = ruleName.substring(0, ruleName.indexOf("Pattern"));
		return result;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public String getCurrentRule() {
		return currentRule;
	}

	public void setCurrentRule(String currentRule) {
		this.currentRule = currentRule;
	}

}
