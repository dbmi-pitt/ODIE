package edu.pitt.dbmi.odie.uima.dekanlin.ae;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.apache.uima.jcas.tcas.Annotation;

import edu.mayo.bmi.uima.chunker.type.Chunk;
import edu.mayo.bmi.uima.core.type.Sentence;
import edu.pitt.dbmi.odie.ODIEUtils;
import edu.pitt.dbmi.odie.uima.dekanlin.model.ODIE_MiniparTriple;
import edu.pitt.dbmi.odie.uima.dekanlin.model.ODIE_MiniparTripleFactory;
import edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateAnnotation;
import edu.pitt.dbmi.odie.uima.minipar.type.DepTreeNode;
import edu.pitt.dbmi.odie.uima.minipar.type.POSType;
import edu.pitt.dbmi.odie.uima.util.ODIE_IFConstants;
import edu.pitt.dbmi.odie.uima.util.ODIE_IndexFinderUtils;
import edu.pitt.dbmi.odie.uima.util.ODIE_UimaUtils;

@SuppressWarnings("deprecation")
public class ODIE_MiniparSimilarityClusterTAE extends JTextAnnotator_ImplBase {

	private static final long serialVersionUID = 1L;

	/* LOG4J logger based on class name */
	private Logger logger = Logger.getLogger(getClass().getName());

	private AnnotatorContext annotatorContext;

	private Connection conn = null;

	private String databaseName = "lin_analysis";

	private ODIE_MiniparTripleFactory tripleFactory;

	private final HashMap<Integer, DepTreeNode> depTreeNodeMap = new HashMap<Integer, DepTreeNode>();

	public void initialize(AnnotatorContext aContext)
			throws AnnotatorConfigurationException,
			AnnotatorInitializationException {
		super.initialize(aContext);
		annotatorContext = aContext;
		try {
			configJdbcInit();
			configInit();
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

	private void configJdbcInit() throws AnnotatorContextException,
			AnnotatorConfigurationException, InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {

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

	public void configInit() {
		try {
			this.tripleFactory = new ODIE_MiniparTripleFactory();
			this.tripleFactory.initialize(conn, databaseName, "lin");
			this.tripleFactory.dropMiniparTriple();
			this.tripleFactory.createMiniparTriple();
			this.tripleFactory.prepareDMLStatements();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see JCasAnnotator_ImplBase#process(JCas)
	 */
	@SuppressWarnings("unchecked")
	public void process(JCas jcas, ResultSpecification resultSpec) {
		FSIndex sentenceTypeIndex = jcas.getAnnotationIndex(Sentence.type);
		Iterator<Sentence> sentenceTypeIterator = sentenceTypeIndex.iterator();
		while (sentenceTypeIterator.hasNext()) {
			Sentence sentence = sentenceTypeIterator.next();
			buildNounPhraseSubsumedDepTreeNodeMap(jcas, sentence);
			convertPOSTypeToTermGranularity(jcas, sentence);
		}
		cachePOSTypes(jcas);
	}

	/**
	 * @see JCasAnnotator_ImplBase#process(JCas)
	 */
	@SuppressWarnings("unchecked")
	public void cachePOSTypes(JCas jcas) {

		FSIndex posTypeIndex = jcas.getAnnotationIndex(POSType.type);
		Iterator<POSType> posTypeIterator = posTypeIndex.iterator();
		while (posTypeIterator.hasNext()) {
			POSType annot = posTypeIterator.next();
			String headWord = annot.getHeadWord();
			String childWord = annot.getChildWord();
			String relation = annot.getTagName();
			if (headWord != null && childWord != null) {
				reportAnnotation(annot);
				headWord = headWord.toLowerCase();
				childWord = childWord.toLowerCase();
				headWord = ODIE_IndexFinderUtils.dbColConform(headWord) ;
				childWord = ODIE_IndexFinderUtils.dbColConform(childWord) ;
				ODIE_MiniparTriple triple = new ODIE_MiniparTriple();
				triple.setWordOne(childWord);
				triple.setRelation(relation);
				triple.setWordTwo(headWord);
				triple = this.tripleFactory.fetchMiniparTriple(triple);
				triple.setFreq(triple.getFreq() + 1.0d);
			}
		}
		this.tripleFactory.updateMiniparTriples();
		this.tripleFactory.clearMiniparTriples();
	}

	@SuppressWarnings("unchecked")
	public void convertPOSTypeToTermGranularity(JCas jcas, Sentence sentence) {
		ArrayList<POSType> posTypesToRemove = new ArrayList<POSType>();
		ArrayList<POSType> posTypesToAdd = new ArrayList<POSType>();
		FSIndex posTypeIndex = jcas.getAnnotationIndex(POSType.type);
		Iterator<POSType> posTypeIterator = posTypeIndex.iterator();
		while (posTypeIterator.hasNext()) {
			POSType posType = posTypeIterator.next();
			if (posType.getBegin() >= sentence.getBegin()
					&& posType.getEnd() <= sentence.getEnd()) {
				posTypesToRemove.add(posType);
				Integer originalHeadId = posType.getHeadId();
				Integer originalChildId = posType.getChildId();
				DepTreeNode headDepTreeNode = this.depTreeNodeMap
						.get(originalHeadId);
				DepTreeNode childDepTreeNode = this.depTreeNodeMap
						.get(originalChildId);
				if (headDepTreeNode == null || childDepTreeNode == null) {
					;
				} else if (headDepTreeNode.getMiniparId() == childDepTreeNode
						.getMiniparId()) {
					; // Omit this intra-noun phrase relation
				} else {
					Integer sPos = Math.min(headDepTreeNode.getBegin(),
							childDepTreeNode.getBegin());
					Integer ePos = Math.max(headDepTreeNode.getEnd(),
							childDepTreeNode.getEnd());
					POSType convertedPOSType = new POSType(jcas);
					convertedPOSType.setBegin(sPos);
					convertedPOSType.setEnd(ePos);
					convertedPOSType.setHeadWord(headDepTreeNode.getWord());
					convertedPOSType.setChildWord(childDepTreeNode.getWord());
					convertedPOSType.setHeadId(headDepTreeNode.getMiniparId());
					convertedPOSType
							.setChildId(childDepTreeNode.getMiniparId());
					convertedPOSType.setTagName(posType.getTagName());
					posTypesToAdd.add(convertedPOSType);
				}
			}
		}

		for (POSType posTypeToRemove : posTypesToRemove) {
			posTypeToRemove.removeFromIndexes(jcas);
		}
		for (POSType posTypeToAdd : posTypesToAdd) {
			posTypeToAdd.addToIndexes(jcas);
		}

	}

	@SuppressWarnings("unchecked")
	public void buildNounPhraseSubsumedDepTreeNodeMap(JCas jcas,
			Sentence sentence) {

		this.depTreeNodeMap.clear();

		//
		// Cache the NPs for this document. Use Transient GateAnnotations if
		// they exist otherwise
		// use cTAKES NounChunks
		//
		ArrayList<ODIE_GateAnnotation> npGateNounPhrases = ODIE_UimaUtils
				.pullSubsumedGateNPsFromJCas(jcas, sentence);
		ArrayList<Chunk> npCTakesNounPhrases = ODIE_UimaUtils
				.pullSubsumedCTakesNPsFromJCas(jcas, sentence);
		ArrayList<Annotation> nounPhrases = (ArrayList<Annotation>) ((!npGateNounPhrases
				.isEmpty()) ? npGateNounPhrases : npCTakesNounPhrases);

		// Conflate constituent DepTreeNodes beneath NounPhrases and map the
		// conflated DepTreeNode by each constituent DepTreeNode
		// This HashMap will be used later to alter the POSTypes making them
		// work with term (or NP) granularity.
		for (Iterator<Annotation> nounPhraseIterator = nounPhrases.iterator(); nounPhraseIterator
				.hasNext();) {
			Annotation currentNounPhrase = nounPhraseIterator.next();
			logger.debug("Processing NP (" + currentNounPhrase.getBegin()
					+ ", " + currentNounPhrase.getEnd() + " ==> "
					+ currentNounPhrase.getCoveredText());
			ArrayList<DepTreeNode> coveredDepTreeNodes = new ArrayList<DepTreeNode>();
			FSIndex depTreeNodeIndex = jcas
					.getAnnotationIndex(DepTreeNode.type);
			Iterator<DepTreeNode> depTreeNodeIterator = depTreeNodeIndex
					.iterator();
			while (depTreeNodeIterator.hasNext()) {
				DepTreeNode depTreeNode = depTreeNodeIterator.next();
				if (depTreeNode.getBegin() >= currentNounPhrase.getBegin()
						&& depTreeNode.getEnd() <= currentNounPhrase.getEnd()) {
					coveredDepTreeNodes.add(depTreeNode);
				}
			}
			if (!coveredDepTreeNodes.isEmpty()) {
				logger.debug("Found " + coveredDepTreeNodes.size()
						+ " underlying DTNs.");
				DepTreeNode nounPhraseDepTreeNode = new DepTreeNode(jcas);
				nounPhraseDepTreeNode.setBegin(currentNounPhrase.getBegin());
				nounPhraseDepTreeNode.setEnd(currentNounPhrase.getEnd());
				nounPhraseDepTreeNode.setMiniparId(coveredDepTreeNodes.get(0)
						.getMiniparId());
				nounPhraseDepTreeNode.setWord(currentNounPhrase
						.getCoveredText().toLowerCase());
				nounPhraseDepTreeNode.addToIndexes(jcas);
				for (DepTreeNode currentCoveredDepTreeNode : coveredDepTreeNodes) {
					this.depTreeNodeMap.put(currentCoveredDepTreeNode
							.getMiniparId(), nounPhraseDepTreeNode);
					currentCoveredDepTreeNode.removeFromIndexes(jcas);
				}
			}
		}

		// Also map those DepTreeNode that remain unmapped. These are one to one
		// mappings.
		FSIndex depTreeNodeIndex = jcas.getAnnotationIndex(DepTreeNode.type);
		Iterator<DepTreeNode> depTreeNodeIterator = depTreeNodeIndex.iterator();
		while (depTreeNodeIterator.hasNext()) {
			DepTreeNode depTreeNode = depTreeNodeIterator.next();
			boolean isSentenceContext = depTreeNode.getBegin() >= sentence
					.getBegin()
					&& depTreeNode.getEnd() <= sentence.getEnd();
			boolean isIndexingSomeNounPhrase = this.depTreeNodeMap
					.get(depTreeNode.getMiniparId()) != null;
			if (isSentenceContext && !isIndexingSomeNounPhrase) {
				this.depTreeNodeMap
						.put(depTreeNode.getMiniparId(), depTreeNode);
			}
		}

	}

	private void reportAnnotation(POSType annot) {
		StringBuffer diagnostic = new StringBuffer();
		diagnostic.append(" Head: " + annot.getHeadWord());
		diagnostic.append(" Relation: " + annot.getTagName());
		diagnostic.append(" Child: " + annot.getChildWord());
		diagnostic.append(" SPOS: " + annot.getBegin());
		diagnostic.append(" EPOS: " + annot.getEnd() + "\n");
		logger.debug(diagnostic.toString());
	}

}
