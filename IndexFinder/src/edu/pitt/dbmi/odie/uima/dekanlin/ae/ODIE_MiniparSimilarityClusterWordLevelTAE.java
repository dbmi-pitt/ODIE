package edu.pitt.dbmi.odie.uima.dekanlin.ae;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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

import edu.pitt.dbmi.odie.ODIEUtils;
import edu.pitt.dbmi.odie.uima.dekanlin.model.ODIE_MiniparTriple;
import edu.pitt.dbmi.odie.uima.dekanlin.model.ODIE_MiniparTripleFactory;
import edu.pitt.dbmi.odie.uima.minipar.type.POSType;
import edu.pitt.dbmi.odie.uima.util.ODIE_IFConstants;

@SuppressWarnings("deprecation")
public class ODIE_MiniparSimilarityClusterWordLevelTAE extends JTextAnnotator_ImplBase  {

	private static final long serialVersionUID = 1L;

	/* LOG4J logger based on class name */
	private Logger logger = Logger.getLogger(getClass().getName());

	private AnnotatorContext annotatorContext;

	private Connection conn = null;
	
	private String databaseName = "lin_analysis" ;

	private ODIE_MiniparTripleFactory tripleFactory;

	public void initialize(AnnotatorContext aContext)
			throws AnnotatorConfigurationException,
			AnnotatorInitializationException {
		super.initialize(aContext);
		annotatorContext = aContext;
		try {
			configJdbcInit();
			configInit() ;
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
			AnnotatorConfigurationException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {

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
		
		this.databaseName = ODIEUtils.extractDatabaseNameFromDbUrl(url) ;

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
		
		FSIndex posTypeIndex = jcas.getAnnotationIndex(POSType.type);
		Iterator<POSType> posTypeIterator = posTypeIndex.iterator();
		while (posTypeIterator.hasNext()) {
			POSType annot = posTypeIterator.next();
			String headWord = annot.getHeadWord();
			String childWord = annot.getChildWord();
			String relation = annot.getTagName();
			if (headWord != null && childWord != null) {
				reportAnnotation(annot);
				headWord = headWord.toLowerCase() ;
				childWord = childWord.toLowerCase() ;
				ODIE_MiniparTriple triple = new ODIE_MiniparTriple() ;
				triple.setWordOne(childWord) ;
				triple.setRelation(relation) ;
				triple.setWordTwo(headWord) ;
				triple = this.tripleFactory.fetchMiniparTriple(triple) ;
				triple.setFreq(triple.getFreq()+1.0d) ;
			}
		}
		this.tripleFactory.updateMiniparTriples() ;
		this.tripleFactory.clearMiniparTriples() ;
	}
	
	private void reportAnnotation(POSType annot) {
		StringBuffer diagnostic = new StringBuffer();
		diagnostic.append(" Head: " + annot.getHeadWord()) ;
		diagnostic.append(" Relation: " + annot.getTagName()) ;
		diagnostic.append(" Child: " + annot.getChildWord()) ;
		diagnostic.append(" SPOS: " + annot.getBegin());
		diagnostic.append(" EPOS: " + annot.getEnd() + "\n");
		logger.debug(diagnostic.toString());
	}

}
