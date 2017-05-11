/*
 */
package edu.pitt.dbmi.odie.uima.lucenefinder.ae;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.FSDirectory;
import org.apache.uima.analysis_engine.ResultSpecification;
import org.apache.uima.analysis_engine.annotator.AnnotatorConfigurationException;
import org.apache.uima.analysis_engine.annotator.AnnotatorContext;
import org.apache.uima.analysis_engine.annotator.AnnotatorContextException;
import org.apache.uima.analysis_engine.annotator.AnnotatorInitializationException;
import org.apache.uima.analysis_engine.annotator.AnnotatorProcessException;
import org.apache.uima.analysis_engine.annotator.JTextAnnotator_ImplBase;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;

import edu.mayo.bmi.uima.core.type.NamedEntity;
import edu.mayo.bmi.uima.core.type.Segment;
import edu.mayo.bmi.uima.core.type.Sentence;
import edu.mayo.bmi.uima.core.type.UmlsConcept;
import edu.mayo.bmi.uima.core.type.WordToken;
import edu.mayo.bmi.uima.core.util.TypeSystemConst;
import edu.pitt.dbmi.odie.server.indexfinder.ODIE_IndexFinderAnnotation;
import edu.pitt.dbmi.odie.server.indexfinder.ODIE_IndexFinderNode;
import edu.pitt.dbmi.odie.uima.lucenefinder.ae.stategy.ODIE_LuceneNerStrategyInterface;
import edu.pitt.dbmi.odie.uima.util.ODIE_IFConstants;
import edu.pitt.dbmi.odie.uima.util.ODIE_UimaUtils;
import edu.pitt.text.tools.Stemmer;

/**
 * UIMA annotator that identified entities based on IndexFinder algorithm.
 * 
 * @author University of Pittsburgh ODIE Program
 * 
 */
@SuppressWarnings("deprecation")
public class ODIE_LuceneNerAnnotationEngine extends JTextAnnotator_ImplBase {

	// LOG4J logger based on class name
	private Logger logger = Logger.getLogger(getClass().getName());

	private ODIE_LuceneNerStrategyInterface strategyEngine;

	private String segmentId = null;

	private Connection conn = null;

	private Stemmer stemmer = null;

	private String fsDirectoryPath = null;

	protected FSDirectory fsDirectory = null;

	protected Searcher searcher = null;

	public void initialize(AnnotatorContext aContext)
			throws AnnotatorConfigurationException,
			AnnotatorInitializationException {
		super.initialize(aContext);
		try {
			configInit(aContext);
		} catch (AnnotatorContextException e) {
			throw new AnnotatorConfigurationException(e);
		}
	}

	@Override
	public void destroy() {
		if (conn == null)
			return;

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Reads configuration parameters.
	 * 
	 * @param annotatorContext
	 */
	private void configInit(AnnotatorContext annotatorContext)
			throws AnnotatorContextException, AnnotatorConfigurationException {

		try {

			this.fsDirectoryPath = (String) annotatorContext
					.getConfigParameterValue(ODIE_IFConstants.IF_PARAM_FS_DIRECTORY_PATH);
			logger.debug("Got path " + this.fsDirectoryPath);

			String isContiguousAsString = (String) annotatorContext
					.getConfigParameterValue(ODIE_IFConstants.IF_PARAM_IS_CONTIGUOUS);
			Boolean isContiguous = new Boolean(isContiguousAsString != null
					&& isContiguousAsString.toLowerCase().equals("true"));

			String isOverlappingAsString = (String) annotatorContext
					.getConfigParameterValue(ODIE_IFConstants.IF_PARAM_IS_OVERLAPPING);
			Boolean isOverlapping = new Boolean(isOverlappingAsString != null
					&& isOverlappingAsString.toLowerCase().equals("true"));

			openIndex();

			openSearcher();

			this.stemmer = new Stemmer();

			if (!isOverlapping && isContiguous) {
				this.strategyEngine = new edu.pitt.dbmi.odie.uima.lucenefinder.ae.stategy.nonoverlapping.contiguous.ODIE_LuceneNerStategy(
						searcher);
			} else if (!isOverlapping && !isContiguous) {
				this.strategyEngine = new edu.pitt.dbmi.odie.uima.lucenefinder.ae.stategy.nonoverlapping.noncontiguous.ODIE_LuceneNerStategy(
						searcher);
			} else if (isOverlapping && isContiguous) {
				this.strategyEngine = new edu.pitt.dbmi.odie.uima.lucenefinder.ae.stategy.overlapping.contiguous.ODIE_LuceneNerStategy(
						searcher);
			} else if (isOverlapping && !isContiguous) {
				this.strategyEngine = new edu.pitt.dbmi.odie.uima.lucenefinder.ae.stategy.overlapping.noncontiguous.ODIE_LuceneNerStategy(
						searcher);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void openIndex() {
		try {
			this.fsDirectory = FSDirectory.open(new File(this.fsDirectoryPath));
			logger.debug("Opened the index at " + this.fsDirectoryPath);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	protected void closeIndex() {
		try {
			this.fsDirectory.close();
		} catch (Exception x) {
			;
		}
	}

	protected void openSearcher() {
		logger.debug("Try to open the searcher with FSDirectory ==> "
				+ this.fsDirectory.getFile().getAbsolutePath());
		try {
			boolean readOnly = true ;
			this.searcher = new IndexSearcher(IndexReader.open(this.fsDirectory, readOnly));
			logger
					.debug("Succeeded in opening the searcher with FSDirectory ==> "
							+ this.fsDirectory.getFile().getAbsolutePath());
		} catch (Exception x) {
			logger.error(x.getMessage());
			x.printStackTrace();
			logger.error("Failed opening the searcher with FSDirectory ==> "
					+ this.fsDirectory.getFile().getAbsolutePath());
		}
	}

	/**
	 * Fetch Segment
	 */
	@SuppressWarnings("unchecked")
	public void fetchSegment(JCas jcas) throws AnnotatorProcessException {
		try {
			FSIndex segmentIndex = jcas.getAnnotationIndex(Segment.type);
			Iterator<Segment> segmentIterator = segmentIndex.iterator();
			while (segmentIterator.hasNext()) {
				Segment segmentAnnot = (Segment) segmentIterator.next();
				this.segmentId = segmentAnnot.getId();
				break;
			}
		} catch (Exception e) {
			throw new AnnotatorProcessException(e);
		}
	}

	/**
	 * Entry point for processing.
	 */
	@SuppressWarnings("unchecked")
	public void process(JCas jcas, ResultSpecification resultSpec)
			throws AnnotatorProcessException {
		try {
			if (segmentId == null) {
				fetchSegment(jcas);
			}

			ArrayList<NamedEntity> existingNamedEntities = ODIE_UimaUtils
					.pullNamedEntitiesFromJCas(jcas);

			FSIndex sentenceIndex = jcas.getAnnotationIndex(Sentence.type);
			Iterator<Sentence> sentenceIterator = sentenceIndex.iterator();
			while (sentenceIterator.hasNext()) {
				Sentence sentenceAnnot = (Sentence) sentenceIterator.next();
				Iterator<WordToken> wordIterator = constrainToSentenceWindow(
						jcas, sentenceAnnot);
				ArrayList<ODIE_IndexFinderAnnotation> odieAnnots = genericallyWrapTokenAnnotations(
						wordIterator, existingNamedEntities);
				this.strategyEngine.setSortedTokens(odieAnnots);
				this.strategyEngine.execute();
				ArrayList<ODIE_IndexFinderAnnotation> resultingConcepts = this.strategyEngine
						.getResultingConcepts();
				unWrapOdieAnnotations(jcas, resultingConcepts);
			}
		} catch (Exception e) {
			throw new AnnotatorProcessException(e);
		}
	}

	/**
	 * Gets a list of LookupToken objects within the specified window
	 * annotation.
	 * 
	 * @param jcas
	 * 
	 * @param window
	 * @param lookupTokenItr
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private Iterator<WordToken> constrainToSentenceWindow(JCas jcas,
			Annotation window) throws Exception {
		List<WordToken> results = new ArrayList<WordToken>();
		FSIndex wordTokenIndex = jcas.getAnnotationIndex(WordToken.type);
		Iterator<WordToken> wordTokenIterator = wordTokenIndex.iterator();
		while (wordTokenIterator.hasNext()) {

			WordToken wordToken = (WordToken) wordTokenIterator.next();

			// only consider if it's within the window
			if ((wordToken.getBegin() >= window.getBegin())
					&& (wordToken.getEnd() <= window.getEnd())) {
				results.add(wordToken);
			}
		}
		return results.iterator();
	}

	private ArrayList<ODIE_IndexFinderAnnotation> genericallyWrapTokenAnnotations(
			Iterator<WordToken> tokenAnnotationsIterator,
			ArrayList<NamedEntity> existingNamedEntities) {
		ArrayList<ODIE_IndexFinderAnnotation> result = new ArrayList<ODIE_IndexFinderAnnotation>();
		for (; tokenAnnotationsIterator.hasNext();) {
			// Ignore words that are already covered by NamedEntities
			WordToken token = tokenAnnotationsIterator.next();
			if (!ODIE_UimaUtils.getNerState(token, existingNamedEntities)
					.equals("NERNegative")) {
				continue;
			}
			// Otherwise generically wrap the word annotation for further
			// processing
			ODIE_IndexFinderAnnotation odieAnnot = new ODIE_IndexFinderAnnotation();
			ODIE_IndexFinderNode odieAnnotSNode = new ODIE_IndexFinderNode();
			ODIE_IndexFinderNode odieAnnotENode = new ODIE_IndexFinderNode();
			int tokenAddress = token.getAddress();
			String tokenKind = "word";
			String tokenString = token.getCoveredText();
			tokenString = (tokenString != null) ? tokenString.toLowerCase()
					: null;
			if (tokenString == null) {
				continue;
			}

			odieAnnotSNode.setOffset(Long.valueOf(token.getBegin()));
			odieAnnotENode.setOffset(Long.valueOf(token.getEnd()));
			odieAnnot.setStartNode(odieAnnotSNode);
			odieAnnot.setEndNode(odieAnnotENode);
			odieAnnot.setAnnotationId(tokenAddress);
			odieAnnot.setAnnotationSetName("Default");
			odieAnnot.setAnnotationTypeName("Token");
			odieAnnot.getFeatures().put("kind", tokenKind);
			odieAnnot.getFeatures().put("string", tokenString);
			if (this.stemmer != null) {
				this.stemmer.add(tokenString);
				this.stemmer.stem();
				String normalizedForm = this.stemmer.getResultString();
				odieAnnot.getFeatures().put("normalizedForm", normalizedForm);
			} else if (token.getNormalizedForm() != null) {
				odieAnnot.getFeatures().put("normalizedForm",
						token.getNormalizedForm());
			}
			if (token.getCanonicalForm() != null) {
				odieAnnot.getFeatures().put("canonicalForm",
						token.getCanonicalForm());
			}

			result.add(odieAnnot);
		}
		return result;
	}

	private void unWrapOdieAnnotations(JCas jcas,
			ArrayList<ODIE_IndexFinderAnnotation> odieAnnots) {
		for (Iterator<ODIE_IndexFinderAnnotation> odieAnnotIterator = odieAnnots
				.iterator(); odieAnnotIterator.hasNext();) {
			ODIE_IndexFinderAnnotation odieAnnot = odieAnnotIterator.next();

			FSArray conceptArr = new FSArray(jcas, 1);
			UmlsConcept uc = new UmlsConcept(jcas);
			
			String clsQName = (String) odieAnnot.getFeatures().get("cn");
			String[] clsQNameParts = clsQName.split("#");
			String ontologyUri = clsQNameParts[0];
			String clsName = clsQNameParts[clsQNameParts.length - 1];
			uc.setOid(ontologyUri);
			uc.setCodingScheme(ontologyUri);
			uc.setCode(clsName);
			uc.setCui((String) odieAnnot.getFeatures().get("umlsCui"));
			uc.setTui((String) odieAnnot.getFeatures().get("umlsTuis"));
			conceptArr.set(0, uc);

			NamedEntity neAnnot = new NamedEntity(jcas);
			int cTakesSemanticType = -1 ;
			try {
				cTakesSemanticType = Integer.valueOf((String) odieAnnot.getFeatures().get("cTakesSemanticType")) ;
			}
			catch (Exception x) {
				;
			}
			neAnnot.setTypeID(cTakesSemanticType); 
			neAnnot.setBegin(odieAnnot.getStartNode().getOffset().intValue());
			neAnnot.setEnd(odieAnnot.getEndNode().getOffset().intValue());
			neAnnot
					.setDiscoveryTechnique(TypeSystemConst.NE_DISCOVERY_TECH_DICT_LOOKUP);
			neAnnot.setOntologyConceptArr(conceptArr);
			neAnnot.setSegmentID(this.segmentId);
			neAnnot.addToIndexes();
		}
	}
}