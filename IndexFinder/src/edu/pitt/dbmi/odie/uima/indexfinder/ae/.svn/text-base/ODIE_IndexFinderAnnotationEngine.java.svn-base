/*
 */
package edu.pitt.dbmi.odie.uima.indexfinder.ae;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;

import edu.mayo.bmi.uima.core.type.NamedEntity;
import edu.mayo.bmi.uima.core.type.Segment;
import edu.mayo.bmi.uima.core.type.Sentence;
import edu.mayo.bmi.uima.core.type.UmlsConcept;
import edu.mayo.bmi.uima.core.type.WordToken;
import edu.mayo.bmi.uima.core.util.TypeSystemConst;
import edu.pitt.dbmi.odie.server.indexfinder.ODIE_IndexFinderAnnotation;
import edu.pitt.dbmi.odie.server.indexfinder.ODIE_IndexFinderGenericPipeComponent;
import edu.pitt.dbmi.odie.server.indexfinder.ODIE_IndexFinderInMemory;
import edu.pitt.dbmi.odie.server.indexfinder.ODIE_IndexFinderInMemoryInf;
import edu.pitt.dbmi.odie.server.indexfinder.ODIE_IndexFinderNode;
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
public class ODIE_IndexFinderAnnotationEngine extends JTextAnnotator_ImplBase {

	// LOG4J logger based on class name
	private Logger logger = Logger.getLogger(getClass().getName());

	private AnnotatorContext annotatorContext;

	private ODIE_IndexFinderGenericPipeComponent component;

	private String segmentId = null;

	private Connection conn = null;
	
	private Stemmer stemmer = null ;

	public void initialize(AnnotatorContext aContext)
			throws AnnotatorConfigurationException,
			AnnotatorInitializationException {
		super.initialize(aContext);

		annotatorContext = aContext;
		try {
			ODIE_IndexFinderInMemory.reset();
			configStemmerInit() ;
			configJdbcInit();
			configInit();
		} catch (AnnotatorContextException ace) {
			throw new AnnotatorConfigurationException(ace);
		}
	}

	private void configStemmerInit() {
		this.stemmer = new Stemmer() ;
	}

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

	@Override
	public void destroy() {
		if(conn==null)
			return;
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}



	/**
	 * Reads configuration parameters.
	 */
	private void configInit() throws AnnotatorContextException,
			AnnotatorConfigurationException {

		try {

			ODIE_IndexFinderInMemoryInf inMemoryIndexFinder = new ODIE_IndexFinderInMemory(conn) ;
					
			ArrayList<String> includedOntologies = new ArrayList<String>();
			PreparedStatement pStmt = conn
					.prepareStatement("select namespace from if_namespace");
			ResultSet rs = pStmt.executeQuery();
			while (rs.next()) {
				includedOntologies.add(rs.getString(1));
			}

			this.component = new ODIE_IndexFinderGenericPipeComponent();
			this.component.setIncludedOntologyUris(includedOntologies);
			this.component.setInMemoryIndexFinder(inMemoryIndexFinder);
			String isContiguousAsString = (String) annotatorContext.getConfigParameterValue(ODIE_IFConstants.IF_PARAM_IS_CONTIGUOUS) ;
			Boolean isContiguous = new Boolean(isContiguousAsString != null &&
					isContiguousAsString.toLowerCase().equals("true") ) ;
			this.component.setContiguous(isContiguous) ;
			
			String isOverlappingAsString = (String) annotatorContext.getConfigParameterValue(ODIE_IFConstants.IF_PARAM_IS_OVERLAPPING) ;
			Boolean isOverlapping = new Boolean(isOverlappingAsString != null &&
					isOverlappingAsString.toLowerCase().equals("true") ) ;
			this.component.setOverlapping(isOverlapping) ;
			
		} catch (Exception e) {
			e.printStackTrace();
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
			
			ArrayList<NamedEntity> existingNamedEntities = ODIE_UimaUtils.pullNamedEntitiesFromJCas(jcas) ;
			
			FSIndex sentenceIndex = jcas.getAnnotationIndex(Sentence.type);
			Iterator<Sentence> sentenceIterator = sentenceIndex.iterator();
			while (sentenceIterator.hasNext()) {
				Sentence sentenceAnnot = (Sentence) sentenceIterator.next();
				Iterator<WordToken> wordIterator = constrainToSentenceWindow(
						jcas, sentenceAnnot);
				ArrayList<ODIE_IndexFinderAnnotation> odieAnnots = genericallyWrapTokenAnnotations(wordIterator, existingNamedEntities);
				this.component.setSortedTokens(odieAnnots);
				this.component.execute();
				ArrayList<ODIE_IndexFinderAnnotation> resultingConcepts = this.component
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
			Iterator<WordToken> tokenAnnotationsIterator, ArrayList<NamedEntity> existingNamedEntities) {
		ArrayList<ODIE_IndexFinderAnnotation> result = new ArrayList<ODIE_IndexFinderAnnotation>();
		for (; tokenAnnotationsIterator.hasNext();) {
			// Ignore words that are already covered by NamedEntities
			WordToken token = tokenAnnotationsIterator.next();
			if (!ODIE_UimaUtils.getNerState(token, existingNamedEntities).equals("NERNegative")) {
				continue ;
			}
			// Otherwise generically wrap the word annotation for further processing
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
				this.stemmer.add(tokenString) ;
				this.stemmer.stem();
				String normalizedForm = this.stemmer.getResultString() ;
				odieAnnot.getFeatures().put("normalizedForm",
						normalizedForm);
			}
			else if (token.getNormalizedForm() != null) {
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
			uc.setCui((String) odieAnnot.getFeatures().get("cui"));
			uc.setTui((String) odieAnnot.getFeatures().get("sui"));
//			uc.setCui("Cui0306") ;
//			uc.setTui("T_DD") ;
			conceptArr.set(0, uc);

			NamedEntity neAnnot = new NamedEntity(jcas);
			neAnnot.setTypeID(TypeSystemConst.NE_TYPE_ID_FINDING); // This is a hardcoded int based on TUI
//			neAnnot.setTypeID(TypeSystemConst.NE_TYPE_ID_DISORDER); 
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