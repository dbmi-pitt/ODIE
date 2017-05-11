package edu.pitt.dbmi.odie.middletier;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import edu.pitt.dbmi.odie.HttpInputStreamWrapper;
import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.dbmi.odie.ODIEUtils;
import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.model.AnalysisEngineMetadata;
import edu.pitt.dbmi.odie.model.Datapoint;
import edu.pitt.dbmi.odie.model.Document;
import edu.pitt.dbmi.odie.model.Dropin;
import edu.pitt.dbmi.odie.model.LanguageResource;
import edu.pitt.dbmi.odie.model.LexicalSet;
import edu.pitt.dbmi.odie.model.LexicalSetLanguageResource;
import edu.pitt.dbmi.odie.model.Statistics;
import edu.pitt.dbmi.odie.model.Suggestion;
import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IOntology;
import edu.pitt.ontology.IOntologyException;
import edu.pitt.ontology.IProperty;
import edu.pitt.ontology.IRepository;
import edu.pitt.ontology.IResource;
import edu.pitt.ontology.OntologyUtils;
import edu.pitt.ontology.bioportal.BioPortalRepository;
import edu.pitt.ontology.protege.ProtegeRepository;
import edu.pitt.terminology.Terminology;

public class MiddleTier {

	
	private static final String CLASS_LSP_NAME = "LSP";
	public static final String CLASS_LSP_URI = "http://caties.cabig.upmc.edu/ODIE/ontologies/2008/1/1/DiscoveryPatterns.owl#LSP";
	public static final String CLASS_NP_NAME = "NP";
	public static final String CLASS_SENTENCE_NAME = "Sentence";
	public static final String CONCEPT_DISCOVERY_ONTOLOGY_URI = "http://caties.cabig.upmc.edu/ODIE/ontologies/2008/1/1/DiscoveryPatterns.owl";
	public static MiddleTier singleton = null;
	private static String errorMessage;
	private Configuration configuration;
	
	
	public static String getErrorMessage() {
		return errorMessage;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void removeOntology(IOntology o){
		repository.removeOntology(o);
		o.delete();
	}
	
	public void createSchema(String name){
		//TODO remove MySQL dependency
		SQLQuery sqlQuery = session.createSQLQuery("CREATE SCHEMA IF NOT EXISTS `" + name + "`");
		sqlQuery.executeUpdate();
	}
	
	/*
	 * Must call init() after setting configuration
	 */
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public static MiddleTier getInstance(Configuration conf) {
		if (singleton == null) {
			singleton = new MiddleTier();
			singleton.setConfiguration(conf);
			try {
				singleton.init(conf);
			} catch (Exception e) {
				e.printStackTrace();
				errorMessage = e.getMessage();
				singleton = null;
			}
		}
		return singleton;
	}

//	public static boolean isLSPAnnotation(Annotation ann) {
//		IClass lspClass = ann.getAnnotationClass().getOntology().getClass(
//				CLASS_LSP_NAME);
//
//		return lspClass != null
//				&& ((IClass) ann.getAnnotationClass()).hasSuperClass(lspClass);
//
//		// String annClassURI = ann.getAnnotationClassURI().toString();
//		// int hashIndex = annClassURI.indexOf("#");
//		// String str = annClassURI.substring(hashIndex);
//		// return str.indexOf("Pattern") >= 0;
//	}

	Logger logger = Logger.getLogger(this.getClass());

	private IRepository repository;
	
	private BioPortalRepository bioportalRepository;

	private Session session;
	private SessionFactory sessionFactory;


	public BioPortalRepository getBioportalRepository() {
		if(bioportalRepository==null){
//			bioportalRepository = new BioPortalRepository();
			URL url;
			try {
				url = new URL(BioPortalRepository.DEFAULT_BIOPORTAL_URL);
				bioportalRepository = new BioPortalRepository(url);				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return bioportalRepository;
	}

	public void delete(Object obj) throws Exception {
		doHibernateChecks(obj);

		// Session session = sessionFactory.getCurrentSession();
		session.delete(obj);
		commitTransaction();
	}

	public void deleteList(List collection) throws Exception {
		for (Object obj : collection) {
			delete(obj);
		}
	}

	public void dispose() {
		closeHibernateSession();
		singleton = null;

	}

	private void closeHibernateSession() {
		if(session!=null)
			session.close();
		
		session = null;
	}

	private void doHibernateChecks(Object obj) throws Exception {
		if (!(obj.getClass().isAnnotationPresent(Entity.class)))
			throw new Exception("Object is not an entity");
		if (session == null) {
			throw new Exception("Hibernate Session not initialized");
		}

	}

	public void evict(Object obj) {
		session.evict(obj);

	}

	public List<Analysis> getAnalyses() {
		// //Session session = sessionFactory.getCurrentSession();
		// session.beginTransaction();
		return session.createCriteria(Analysis.class).list();
	}

	public AnalysisDocument getAnalysisDocument(Long daId) {
		// Session session = sessionFactory.getCurrentSession();
		// session.beginTransaction();
		Criteria c = session.createCriteria(AnalysisDocument.class);
		c.add(Restrictions.eq("id", daId));
		return (AnalysisDocument) c.uniqueResult();
	}

	public List<AnalysisDocument> getAnalysisDocuments(Analysis analysis,
			String status) {
		// Session session = sessionFactory.getCurrentSession();
		// session.beginTransaction();
		Criteria c = session.createCriteria(AnalysisDocument.class);
		if(status!=null)
			c.add(Restrictions.eq("status", status));
		c.add(Restrictions.eq("analysis", analysis));
		return c.list();
	}

	public int getAnalysisDocumentCount(Analysis analysis,
			String status) {
		Criteria c = session.createCriteria(AnalysisDocument.class);
		if(status!=null)
			c.add(Restrictions.eq("status", status));
		c.add(Restrictions.eq("analysis", analysis));
		c.setProjection(Projections.rowCount());
		
		Object o = c.uniqueResult();
		return ((Long) o).intValue();
	}

	public List<AnalysisEngineMetadata> getAnalysisEngineMetadatas(String type) {
		// Session session = sessionFactory.getCurrentSession();
		// session.beginTransaction();
		Criteria c = session.createCriteria(AnalysisEngineMetadata.class);
		if (type != null)
			c.add(Restrictions.eq("type", type));

		return c.list();
	}

	public Analysis getAnalysisForName(String analysisName) {
		Criteria c = session.createCriteria(Analysis.class);
		c.add(Restrictions.eq("name", analysisName));
		return (Analysis) c.uniqueResult();
	}

	public Analysis getAnalysisForId(long analysisId) {
		Criteria c = session.createCriteria(Analysis.class);
		c.add(Restrictions.eq("id", analysisId));
		return (Analysis) c.uniqueResult();
	}
	
	public List<String> getAnalysisNames() {
		// Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery("select name from Analysis");
		return q.list();
	}

	
	public Document getDocument(String uriString) {
		// Session session = sessionFactory.getCurrentSession();
		// session.beginTransaction();
		Criteria c = session.createCriteria(Document.class);
		c.add(Restrictions.eq("uriString", uriString));
		return (Document) c.uniqueResult();
	}

	public List<LanguageResource> getLanguageResources(String type) {
//		sessionFactory.evict(LanguageResource.class);
		// //Session session = sessionFactory.getCurrentSession();
		session.getTransaction().commit();
		session.beginTransaction();
		Criteria c = session.createCriteria(LanguageResource.class);
		if (type != null)
			c.add(Restrictions.eq("type", type));
		
		c.setCacheable(false);
		List<LanguageResource> olist = c.list();
		
//		session.getTransaction().commit();
//		session.beginTransaction();
//		
		return olist;
	}

	
	public IRepository getRepository() {
		return repository;
	}

	public IResource getResourceForURI(URI uri) {
		logger.debug("Searching resource for URI:" + uri.toASCIIString());
		IResource r = repository.getResource(uri);
		if (r == null)
			logger.error("No resource found for URI:" + uri.toASCIIString());
		return r;
	}

	public List<Terminology> getTerminologies() {
		return Arrays.asList(repository.getTerminologies());
	}

	public IOntology importOntology(URI uri) {
		try {
			return repository.importOntology(uri);
		} catch (IOntologyException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return null;
		}
	}

	public void init(Configuration config) throws Exception {
		closeHibernateSession();
		initHibernate(config);
		initRepository(config);

	}

	private void initRepository(Configuration config) throws IOntologyException {
		setRepository(new ProtegeRepository(
				config.getDatabaseDriver(),
				config.getDatabaseURL(),
				config.getUsername(),
				config.getPassword(),
				config.getRepositoryTableName(),
				config.getTemporaryDirectory()));
		
	}

	// private SessionFactory sessionFactory;
	private void initHibernate(Configuration config) {
		AnnotationConfiguration aconfig = new AnnotationConfiguration();

//			 aconfig.setProperty("hibernate.dialect",
//			 "org.hibernate.dialect.HSQLDialect");
		aconfig.setProperty("hibernate.connection.driver_class", config
				.getDatabaseDriver());
		aconfig.setProperty("hibernate.connection.url", config
				.getDatabaseURL());
		aconfig.setProperty("hibernate.connection.username", config
				.getUsername());
		aconfig.setProperty("hibernate.connection.password", config
				.getPassword());
		aconfig.setProperty("hibernate.connection.pool_size", "1");
		aconfig.setProperty("hibernate.cache.provider_class",
				"org.hibernate.cache.NoCacheProvider");
		
		aconfig.setProperty("hibernate.cache.use_second_level_cache","false");

		aconfig.setProperty("hibernate.show_sql", "false");
		aconfig.setProperty("hibernate.transaction.factory_class",
				"org.hibernate.transaction.JDBCTransactionFactory");
		aconfig.setProperty("hibernate.current_session_context_class",
				"managed");
		aconfig.setProperty("hibernate.search.default.indexBase", 
				config.getLuceneIndexDirectory());
		
		if (config.getHBM2DDLPolicy() != null)
			aconfig.setProperty("hibernate.hbm2ddl.auto", config
					.getHBM2DDLPolicy());

		//Order is important
		aconfig.addAnnotatedClass(LanguageResource.class);
		aconfig.addAnnotatedClass(Document.class);
		aconfig.addAnnotatedClass(Dropin.class);
		
		aconfig.addAnnotatedClass(AnalysisEngineMetadata.class);
		aconfig.addAnnotatedClass(LexicalSet.class);
		aconfig.addAnnotatedClass(LexicalSetLanguageResource.class);
		
		aconfig.addAnnotatedClass(Analysis.class);
		aconfig.addAnnotatedClass(AnalysisDocument.class);
		

		aconfig.addAnnotatedClass(Datapoint.class);

		sessionFactory = aconfig.buildSessionFactory();
		resetSession();
	}

	public void resetSession() {
		closeHibernateSession();
	
		session = sessionFactory.openSession();
		session.beginTransaction();
		
	}

//	@Deprecated  
//	/*
//	 * No system ontologies are now used
//	 */
//	public boolean isSystemAnnotation(Annotation ann) {
//		return isSystemOntology(ann.getAnnotationClass().getOntology().getURI());
//	}



	public void persist(Object obj) throws Exception {
		doHibernateChecks(obj);

		// Session session = sessionFactory.getCurrentSession();

		
		session.saveOrUpdate(obj);
		commitTransaction();
	}
	
	

	private void commitTransaction() {
		Transaction t = session.getTransaction();
		t.commit();
		session.beginTransaction();
	}

	public void persistWithoutCommit(Object a) throws Exception {
		doHibernateChecks(a);

		// Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(a);
	}

	protected void setRepository(IRepository repository) {
		this.repository = repository;
	}

	public List<LexicalSet> getLexicalSets() {
		
		return session.createCriteria(LexicalSet.class).list();
	}

	public Session getSession() {
		return session;
	}

	public void dropSchema(String schemaName) {
		//TODO remove MySQL dependency
		session.createSQLQuery("DROP SCHEMA IF EXISTS " + schemaName).executeUpdate();
	}

	public List<String> getLexicalSetNames() {
		Query q = session.createQuery("select name from LexicalSet");
		return q.list();
	}

	public List<Document> getDocuments() {
		return session.createCriteria(Document.class).list();
	}

	public int getOntologyHitCount(Analysis analysis, IOntology o1, boolean unique) {
		String uriString = o1.getURI().toASCIIString();
		
		return getOntologyHitCount(analysis, uriString, unique);
	}

	public int getOntologyHitCount(Analysis analysis, String ontologyURIString, boolean unique) {
		if(unique){
			Query q = session.createQuery("select count(*) from Datapoint where analysis=? and ontologyURIString=?");
			q.setEntity(0, analysis);
			q.setString(1, ontologyURIString);
			
			return ((Long)q.uniqueResult()).intValue();
		}
		else{
			Query q = session.createQuery("select sum(frequency) from Datapoint where analysis=? and ontologyURIString=?");
			q.setEntity(0, analysis);
			q.setString(1, ontologyURIString);
			
			return ((Long)q.uniqueResult()).intValue();
		}
	}
	
	public List<String> getInstalledDropInNames() {
		Query q = session.createQuery("select filename from Dropin");
		return q.list();
	}

	public LexicalSet getLexicalSetForName(String name) {
		Criteria c = session.createCriteria(LexicalSet.class);
		c.add(Restrictions.eq("name", name));
		return (LexicalSet) c.uniqueResult();
	}

	public HashMap<IOntology,Long> getHitCountForAllOntologies(Analysis analysis, boolean unique) {
		Query q;
		if(unique){
			q = session.createQuery("select ontologyURIString,count(*) from Datapoint where analysis=? group by ontologyURIString");
			q.setEntity(0, analysis);
		}
		else{
			q = session.createQuery("select ontologyURIString,sum(occurences) from Datapoint where analysis=? group by ontologyURIString");
			q.setEntity(0, analysis);
		}
		
		List results = q.list();
		
		HashMap<IOntology,Long> out = new HashMap<IOntology,Long>();
		for(Object item:results){
			Object[] arr = (Object[])item;
			
			String uri = (String)arr[0];
			try {
				IOntology o = (IOntology) getResourceForURI(new URI(uri));
				if(o==null)
					continue;
				out.put(o, (Long)(arr[1]));
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		
		return out;
		
	}

	public HashMap<Object,Statistics> getOntologyHitStatistics(Analysis analysis) {
		Query q;
		q = session.createQuery("select ontologyURIString,count(ontologyURIString),sum(occurences) from Datapoint where analysis=? group by ontologyURIString");
		q.setEntity(0, analysis);
		
		List results = q.list();
		
		HashMap<Object,Statistics> out = new HashMap<Object,Statistics>();
		for(Object item:results){
			Object[] arr = (Object[])item;
			
			String uri = (String)arr[0];
			try {
				IOntology o = (IOntology) getResourceForURI(new URI(uri));
				if(o==null)
					continue;
				
				Statistics s = new Statistics();
				s.context = o;
				s.uniqueConceptsCount = (Long)(arr[1]); 
				s.namedEntityCount = (Long)(arr[2]);
				out.put(o, s);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		
		return out;
		
	}
	
	public LanguageResource getLanguageResourceForURI(URI uri) {
		Criteria c = session.createCriteria(LanguageResource.class);
		c.add(Restrictions.eq("uriString", uri.toASCIIString()));
		List<LanguageResource> arr = c.list();
		if(arr.size()>0){
			if(arr.size()>1)
				logger.warn("Two classes with the same URI found in the ontology:" + uri.toASCIIString());
			
			return arr.get(0);
		}
		return null;
	}
	
	public List<Document> searchDocumentText(String text){
		FullTextSession fSession = Search.getFullTextSession(session);
		org.apache.lucene.search.Query lucenceQuery = createLuceneQuery("text",text);
		List<Document> docs = fSession.createFullTextQuery(lucenceQuery, Document.class).list();
		return docs;
//		return new ArrayList<Document>();
	}

	public List<IClass> searchOntology(IOntology o, String searchText){
		int maxHits = 10;
		List<IClass> outList = new ArrayList<IClass>();

		LanguageResource lr = getLanguageResourceForURI(o.getURI());
		IndexSearcher searcher = getIndex(lr);
		
		if(searcher!=null){
		    try {
//		    	 TermQuery query = new TermQuery(new Term(ODIEConstants.LUCENE_ONTOLOGY_FIELD_CONTENT, searchText));
		    	
		    	 Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_29);
//		    	 Query query = new QueryParser(ODIEConstants.LUCENE_ONTOLOGY_FIELD_CONTENT, analyzer).parse(querystr);
		    	 
		    	 QueryParser parser = new QueryParser(Version.LUCENE_29,ODIEConstants.LUCENE_ONTOLOGY_FIELD_CONTENT, analyzer);
		    	 org.apache.lucene.search.Query query = parser.parse(searchText);
		    	 
		    	 
		    	 TopDocs results = searcher.search(query,null,maxHits);
		    	 
			     if(results.totalHits>0){
				        ScoreDoc[] hits = results.scoreDocs;
				        for (ScoreDoc hit : hits) {
				        	org.apache.lucene.document.Document hitDoc = searcher.doc(hit.doc);
				        	String uri = hitDoc.get(ODIEConstants.LUCENE_ONTOLOGY_FIELD_URI);
							IClass c = o.getClass(uri);
							if(c == null){
								logger.error("URI:" + uri + " exists in index but no matching resource found in repository.");
								continue;
							}
							outList.add(c);
				        }
			    }
			    searcher.close();
//				Analyzer analyzer = new StandardAnalyzer();
//		    	QueryParser parser = new QueryParser(ODIEConstants.LUCENE_ONTOLOGY_FIELD_CONTENT, analyzer);
//		        org.apache.lucene.search.Query query = parser.parse(searchText);
//		        TopDocs results = searcher.search(query,maxHits);
//		        logger.info(results.totalHits);
//		        if(results.totalHits>0){
//			        ScoreDoc[] hits = results.scoreDocs;
//			        for (ScoreDoc hit : hits) {
//			        	org.apache.lucene.document.Document hitDoc = searcher.doc(hit.doc);
//			        	String uri = hitDoc.get(ODIEConstants.LUCENE_ONTOLOGY_FIELD_URI);
//			        	try {
//							IResource r = getResourceForURI(new URI(uri));
//							if(r == null){
//								logger.error("URI:" + uri + " exists in index but no matching resource found in repository.");
//								continue;
//							}
//							if(r instanceof IClass){
//								outList.add((IClass) r);
//							}
//						} catch (URISyntaxException e) {
//							e.printStackTrace();
//							logger.error("URI:" + uri + " in index is invalid.");
//							continue;
//						}
//			        }
//		        }
//		        searcher.close();

			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error searching the index:" + e.getMessage());
				try {
					searcher.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		else{
			logger.error("Error searching the index: No index found for " + o.getURI());
		}
		return outList;
	}
	
	public List<AnalysisDocument> getDocumentsContainingSuggestion(Suggestion s) {
		if(s.getNerNegative() == null)
			return new ArrayList<AnalysisDocument>();
		
		String field = "text";
		
		String cleanedtext = s.getNerNegative().replaceAll("\"", "\\\\\"");
		String queryString = "text:\"" + cleanedtext +"\"";

		org.apache.lucene.search.Query luceneQuery = createLuceneQuery(field,queryString);

		FullTextSession fSession = Search.getFullTextSession(session);
		FullTextQuery query = fSession.createFullTextQuery(luceneQuery, Document.class);
		
		List matchedDocs = query.list();
		if(matchedDocs.size() > 0){
			Criteria c = session.createCriteria(AnalysisDocument.class);
			c.add(Restrictions.eq("analysis", s.getAnalysis()));
			c.add(Restrictions.in("document", matchedDocs));
			return c.list();
		}
		else
			return new ArrayList<AnalysisDocument>();
		
	}

	private org.apache.lucene.search.Query createLuceneQuery(String field, String query) {
		QueryParser parser = new QueryParser(Version.LUCENE_29,field, new StandardAnalyzer(Version.LUCENE_29));
		org.apache.lucene.search.Query luceneQuery = null;
		try {
			luceneQuery = parser.parse(query);
			
			
		} catch (ParseException e) {
			throw new RuntimeException("Cannot search with query string",e);
		}
		return luceneQuery;
	}

	public List<Suggestion> getSuggestions(AnalysisDocument ad, List<Suggestion> allSuggestions) {
		String idQuery = "+id:"+ad.getDocument().getId();
		
		
		String defaultField = "text";
		List<Suggestion> outList = new ArrayList<Suggestion>();
		List<String> foundSuggestions = new ArrayList<String>();
		
		for(Suggestion s: allSuggestions){
			
			if(foundSuggestions.contains(s.getNerNegative())){
				s.setAggregate(false);
				outList.add(s);
				continue;
			}

			String cleanedtext = s.getNerNegative().replaceAll("\"", "\\\\\"");
			String queryString = idQuery + " AND text:\"" + cleanedtext +"\"";
			org.apache.lucene.search.Query luceneQuery = createLuceneQuery(defaultField,queryString);
			FullTextSession fSession = Search.getFullTextSession(session);
			FullTextQuery query = fSession.createFullTextQuery(luceneQuery, Document.class);
			
			if(query.list().size()>0 && !outList.contains(s)){
				outList.add(s);
				foundSuggestions.add(s.getNerNegative());
			}
		}
		
		return outList;
	}

	public List<Dropin> getInstalledDropIns() {
		return session.createCriteria(Dropin.class).list();
	}

	public Dropin getInstalledDropIn(File f) {
		Criteria c = session.createCriteria(Dropin.class);
		c.add(Restrictions.eq("filename", f.getName()));
		return (Dropin) c.uniqueResult();
	}

	public AnalysisEngineMetadata getAnalysisEngineMetadata(URL aeURL) {
		Criteria c = session.createCriteria(AnalysisEngineMetadata.class);
		c.add(Restrictions.eq("urlString", aeURL.toExternalForm()));
		return (AnalysisEngineMetadata) c.uniqueResult();
	}

	public IndexSearcher getIndex(LanguageResource lr) {
		String luceneDir = getConfiguration().getLuceneIndexDirectory();
		String indexDir = luceneDir + System.getProperty("file.separator") + lr.getId();
		File f = new File(indexDir);
		if(f.exists()){
			try {
				FSDirectory directory = FSDirectory.open(f);
				return new IndexSearcher(directory,true);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	

	public IClass createClass(IOntology ontology, String className) {
		return  ontology.createClass(OntologyUtils.toResourceName(className)); 
	}

	public void addParent(IClass child, IClass parent){
		if(parent == null)
			return;
		
		IOntology co = child.getOntology();
		IOntology po = parent.getOntology();
		
		// if ontologies are not equal
		if(!co.equals(po)){
			// try to find this class in target ontology
			IClass  cparent = co.getClass(parent.getName());
			if(cparent == null){
				// if cparent not there, do fun stuff
				cparent = co.createClass(parent.getName());
				for(String s: parent.getLabels())
					cparent.addLabel(s);
				for(String s: parent.getComments())
					cparent.addComment(s);
				
				// link to external resource
				cparent.setPropertyValue(co.getProperty(IProperty.RDFS_IS_DEFINED_BY),""+parent.getURI());
			}
			parent = cparent;
		}
		
		child.removeSuperClass(co.getRoot());
		
		for(IClass c:child.getSuperClasses()){
			child.removeSuperClass(c);
		}
		
		
		child.addSuperClass(parent);
	}
	
	public List<IOntology> getOntologies(Analysis a){
		List<IOntology> olist = new ArrayList<IOntology>();
		
		for(LanguageResource lr:a.getLanguageResources()){
			IResource r = getResourceForURI(lr.getURI());
			
			if(r!=null){
				olist.add((IOntology)r);
			}
		}
		return olist;
	}

	public boolean doBioportalNoteRequest(BioportalNote note) {
		try {
			String url = BioPortalRepository.DEFAULT_BIOPORTAL_URL + ODIEConstants.BIOPORTAL_NOTES_URL_POSTFIX + 
			note.getAppliesToOntology().getId();;
			
			HttpInputStreamWrapper result =  ODIEUtils.doHttpPost(url, note.getParameters());
			
			if(result.getStatusCode()!=200){
				 BufferedReader rd = new BufferedReader(new InputStreamReader(result.getInputStream()));
				 String line;
						
				 while ((line = rd.readLine()) != null) {
				 	logger.error(line);
				 }
				 return false;
			}
			else{
				SAXBuilder builder = new SAXBuilder();
				org.jdom.Document doc = builder.build(result.getInputStream());
				XMLOutputter xo = new XMLOutputter();
				
				logger.debug(xo.outputString(doc));

				XPath x = XPath.newInstance("//noteBean/id");
				Element noteid = (Element) x.selectSingleNode(doc);
				x = XPath.newInstance("//ontologyId");
				Element ontologyid = (Element) x.selectSingleNode(doc);
				
				if(noteid!=null && ontologyid!=null){
					note.setId(noteid.getText());
					note.setOntologyId(ontologyid.getText());
					//create a property for bioportal note ID.
					return true;
				}
				logger.debug(doc.toString());
				return false;
			}
				 
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			
			return false;
		}
		
		
	}

	public LanguageResource getLanguageResource(String name) {
//		sessionFactory.evict(LanguageResource.class);
		// //Session session = sessionFactory.getCurrentSession();
		session.getTransaction().commit();
		session.beginTransaction();
		Criteria c = session.createCriteria(LanguageResource.class);
		if (name != null)
			c.add(Restrictions.eq("name", name));
		else
			return null;
		
		c.setCacheable(false);
		List<LanguageResource> olist = c.list();
		
		session.getTransaction().commit();
		session.beginTransaction();
		if(!olist.isEmpty())
			return olist.get(0);
		else 
			return null;
	}

	public List<Analysis> getAnalysesUsingLexicalSet(LexicalSet ls) {
		Criteria c = session.createCriteria(Analysis.class);
		c.add(Restrictions.or(Restrictions.eq("lexicalSet", ls), Restrictions.eq("proposalLexicalSet", ls)));
		c.setCacheable(false);
		List<Analysis> alist = c.list();
		return alist;
	}

	public IOntology[] getSortedOntologies() {
		List<IOntology> olist = Arrays.asList(getRepository().getOntologies());
		Collections.sort(olist, new Comparator<IOntology>() {

			@Override
			public int compare(IOntology o1, IOntology o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		return olist.toArray(new IOntology[]{});
	}

	public LexicalSet getLexicalSetForLanguageResource(LanguageResource lr) {
		Criteria c = session.createCriteria(LexicalSet.class);
		c.add(Restrictions.sizeEq("lexicalSetLanguageResources", 1));
		List<LexicalSet> lslist = c.list();
		
		for(LexicalSet ls:lslist){
			if(ls.getLexicalSetLanguageResources().get(0).getLanguageResource().equals(lr))
				return ls;
		}
		
		return null;
	}

}
