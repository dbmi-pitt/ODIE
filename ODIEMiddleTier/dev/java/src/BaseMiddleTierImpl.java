package edu.pitt.dbmi.odie.middletier;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.Annotation;
import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.model.LanguageResource;
import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IOntology;
import edu.pitt.ontology.IOntologyException;
import edu.pitt.ontology.IRepository;
import edu.pitt.ontology.IResource;
import edu.pitt.ontology.protege.ProtegeRepository;
import edu.pitt.terminology.Terminology;

@Deprecated
public class BaseMiddleTierImpl{

	private static final String CLASS_LSP_NAME = "LSP";
	public static final String CLASS_NP_NAME = "NP";
	public static final String CLASS_SENTENCE_NAME = "Sentence";
	public static final String CONCEPT_DISCOVERY_ONTOLOGY_URI = "http://caties.cabig.upmc.edu/ODIE/ontologies/2008/1/1/DiscoveryPatterns.owl";
	public static final String CLASS_LSP_URI =  "http://caties.cabig.upmc.edu/ODIE/ontologies/2008/1/1/DiscoveryPatterns.owl#LSP";
	public static MiddleTier singleton = null;

	private List<Analysis> analyses = new ArrayList<Analysis>();
	private HashMap<String, List<LanguageResource>> languageResourceTypeMap = null;
	private IRepository repository;

	Logger logger = Logger.getLogger(this.getClass());
	public static MiddleTier getInstance(Configuration conf) {
		if (singleton == null) {
			singleton = new BaseMiddleTierImpl();
			singleton.init(conf);
		}
		return singleton;
	}
	
	public void init(Configuration config) {
		initRepositoryFromFile(config.getRepositoryConfigLocation());

	}

	private void initRepositoryFromFile(String configFile) {
		setRepository(new ProtegeRepository(configFile));
	}
	
	protected void setRepository(IRepository repository) {
		this.repository = repository;
		
	}
	
	public IRepository getRepository() {
		return repository;
	}
	
	public List<Terminology> getTerminologies() {
		return Arrays.asList(repository.getTerminologies());
	}
	
	public List<Analysis> getAnalyses() {
		return analyses;
	}
	
	public boolean addAnalysis(Analysis analysis) {
		return addAnalysisInternal(analysis);
	}

	protected boolean addAnalysisInternal(Analysis analysis) {
		analyses.add(analysis);
		return true;
	}
	
	public boolean updateAnalysis(Analysis analysis) {
		updateAnalysisInternal(analysis);
		return true;
	}
	
	protected boolean updateAnalysisInternal(Analysis analysis) {
		return true;
	}
	
	public boolean updateDocumentAnalysis(AnalysisDocument da) {
		return updateDocumentAnalysisInternal(da);
	}
	
	protected boolean updateDocumentAnalysisInternal(AnalysisDocument da) {
		return true;
	}
	
	public List<LanguageResource> getLanguageResources(String type) {
		if (languageResourceTypeMap == null) {
			initLanguageResourceTypeMap();
		}
		List<LanguageResource> returnList = languageResourceTypeMap.get(type);
		
		return (returnList==null?new ArrayList<LanguageResource>():returnList);
	}

	private void initLanguageResourceTypeMap() {
		languageResourceTypeMap = new HashMap<String, List<LanguageResource>>();
		IOntology[] oarr = repository.getOntologies();
		for (IOntology io : oarr) {
			LanguageResource langResource = createLanguageResource(io);
			updateLanguageResourceTypeMap(langResource);
		}
	}

	private LanguageResource createLanguageResource(IOntology io) {
		LanguageResource o = new LanguageResource();
		o.setResource(io);
		o.setName(io.getName());
		if (o.getName().toLowerCase().indexOf("discovery") >= 0) {
			o.setType(IRepository.TYPE_SYSTEM_ONTOLOGY);
		} else {
			o.setType(IRepository.TYPE_ONTOLOGY);
		}
		return o;
	}
	
	private void updateLanguageResourceTypeMap(LanguageResource langResource) {
		List<LanguageResource> l = languageResourceTypeMap.get(langResource.getType());
		if (l == null) {
			l = new ArrayList<LanguageResource>();
			languageResourceTypeMap.put(langResource.getType(), l);
		}
		l.add(langResource);
	}

	public boolean importOntology(URI uri) {
		try {
			repository.importOntology(uri);
			return true;
		} catch (IOntologyException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return false;
		}
	}

	public static  boolean isLSPAnnotation(Annotation ann) {
		IClass lspClass = ann.getAnnotationClass().getOntology().getClass(CLASS_LSP_NAME);
		
		return lspClass!=null && ((IClass)ann.getAnnotationClass()).hasSuperClass(lspClass);
	
		
//		String annClassURI = ann.getAnnotationClassURI().toString();
//		int hashIndex = annClassURI.indexOf("#");
//		String str = annClassURI.substring(hashIndex);
//		return str.indexOf("Pattern") >= 0;
	}

	public boolean isSystemAnnotation(Annotation ann) {
		return isSystemOntology(ann.getAnnotationClass().getOntology().getURI());
	}

	public boolean isSystemOntology(URI uri) {
		List<LanguageResource> list = getLanguageResources(IRepository.TYPE_SYSTEM_ONTOLOGY);

		for (LanguageResource lr : list) {
			if (lr.getResource().getURI().toString().equals(uri.toString()))
				return true;
		}
		return false;
	}

	public IResource getResourceForURI(URI uri) {
		IResource r = repository.getResource(uri);
		if (r == null)
			logger.error("No resource found for URI:" + uri.toString());

		return r;
	}
	
	public void dispose() {
		analyses.clear();
		singleton = null;

	}

	public boolean removeAnalysis(Analysis analysis) {
		return removeAnalysisInternal(analysis);
	}

	protected boolean removeAnalysisInternal(Analysis analysis) {
		analyses.remove(analysis);
		return true;
		
	}

	public Analysis getAnalysisForName(String parameterValue) {
		for(Analysis a:getAnalyses()){
			if(a.getName().equals(parameterValue))
				return a;
		}
		return null;
	}

	public Analysis getAnalysisForId(int analysisid) {
		for(Analysis a:getAnalyses()){
			if(a.getId() == analysisid)
				return a;
		}
		return null;
		
	}

	public AnalysisDocument getDocumentAnalysis(String analysisName, int daId) {
		Analysis a = getAnalysisForName(analysisName);
		for(AnalysisDocument da:a.getAnalysisDocuments()){
			if(da.getId()==daId)
				return da;
		}
		return null;
	}

}
