package edu.pitt.dbmi.odie.middletier.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Table;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import edu.pitt.dbmi.odie.ODIEException;
import edu.pitt.dbmi.odie.middletier.Configuration;
import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.middletier.MiddleTierJDBC;
import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.model.AnalysisEngineMetadata;
import edu.pitt.dbmi.odie.model.Datapoint;
import edu.pitt.dbmi.odie.model.Document;
import edu.pitt.dbmi.odie.model.LanguageResource;
import edu.pitt.dbmi.odie.model.LexicalSet;
import edu.pitt.ontology.IResource;
import edu.pitt.ontology.bioportal.BioPortalRepository;

public class MiddleTierHibernateTester {
	
	private static final String DUMMY_STR1 = "dummy string one";
	private static final String DUMMY_STR2 = "dummy string two";
	private static final String DUMMY_URI1 = "file:/c:/html/dataone.txt#conceptA";
	private static final String DUMMY_URI2 = "file:/c:/html/datatwo.txt#conceptB";
	private static final String DIFF1 = "1";
	private static final String DIFF2 = "2";
	
	private static final String DUMMY_DOCURI1 = "file:/c:/dummy/radiology1.txt";
	private static final String DUMMY_DOCURI2 = "file:/c:/dummy/radiology2.txt";
	
	static Logger logger = Logger.getLogger(MiddleTierHibernateTester.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		logger.setLevel(Level.ALL);
		
//		boolean pass = doExternalDBModTest();
//		boolean pass = doRWTest();
//		boolean pass = doAccessTest();
		boolean pass = doBioportalTest();
		
		if(pass)
			logger.info("All Tests Passed.");
		else
			logger.error("Some Tests Failed.");
				
	}

	private static boolean doBioportalTest() {
		Configuration conf = getConfiguration();
		MiddleTier mt = MiddleTier.getInstance(conf);
		BioPortalRepository bp = mt.getBioportalRepository();
		try {
			IResource r = bp.getResource(new URI("http://bioportal.bioontology.org/ontologies/Amino_Acid#RefiningFeature"));
			return (r!=null);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	private static boolean doAccessTest() {
		Configuration conf = getConfiguration();
		MiddleTier mt = MiddleTier.getInstance(conf);
		List<Analysis> analyses = mt.getAnalyses();
//		mt.getHitCountForAllOntologies(analyses.get(9), false);
		mt.getOntologyHitStatistics(analyses.get(9));
		return true;
	}

	private static boolean doExternalDBModTest() {
		Configuration conf = getConfiguration();
		
		MiddleTierJDBC mtjdbc = null;
		try {
			mtjdbc = new MiddleTierJDBC(conf);
		} catch (SQLException e) {
			e.printStackTrace();
			
			if(mtjdbc!=null)
				mtjdbc.dispose();
			
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		
		MiddleTier mt = MiddleTier.getInstance(conf);
		
		createRowsInLR(mt);
		int beforeDelete = countRowsInLR(mt);
		int retval = deleteRowInLR(mtjdbc);
//		mt.resetSession();
		boolean pass = false;
		if(retval == 1){
			int afterDelete = countRowsInLR(mt);
			pass = beforeDelete > afterDelete;
		}

		mt.dispose();

		return pass;
	}

	private static Configuration getConfiguration() {
		String schemaName = "odie";
		Configuration conf = new Configuration();
		conf.setHBM2DDLPolicy("update");
		conf.setDatabaseDriver("com.mysql.jdbc.Driver");
		conf.setDatabaseURL("jdbc:mysql://localhost:3306/" + schemaName);
		conf.setUsername("odieuser");
		conf.setPassword("odiepass");
		conf.setRepositoryTableName(LanguageResource.class.getAnnotation(Table.class).name());
		conf.setTemporaryDirectory(System.getProperty("java.io.tmpdir"));
		conf.setLuceneIndexDirectory("c:/tmp/indices");
		return conf;
	}

	private static int deleteRowInLR(MiddleTierJDBC mtjdbc) {
		String sql = "DELETE FROM ODIE_LR WHERE ID = 5";
		try {
			return mtjdbc.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
		
		
	}

	private static void createRowsInLR(MiddleTier mt) {
		
		try {
			LanguageResource lr = createDummyLanguageResource("1");
			mt.persist(lr);
			
			lr = createDummyLanguageResource("2");
			mt.persist(lr);
			
			lr = createDummyLanguageResource("3");
			mt.persist(lr);
			
			lr = createDummyLanguageResource("4");
			mt.persist(lr);
			
			lr = createDummyLanguageResource("5");
			mt.persist(lr);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

	private static int countRowsInLR(MiddleTier mt) {
		return mt.getLanguageResources(null).size();
	}

	private static boolean doRWTest() {
		Configuration conf = getConfiguration();
		
		MiddleTier mt = MiddleTier.getInstance(conf);
		
		
//		
		writeData(mt);
		boolean pass = readBackData(mt);
//		deleteSelective(mt);
		deleteData(mt);
		mt.dispose();
		return pass;
	}
	
	private static void deleteSelective(MiddleTier mt) {
		List<Analysis> analysisList = mt.getAnalyses();
		
		Analysis a = analysisList.get(0);
		
		for(AnalysisDocument ad:a.getAnalysisDocuments())
			ad.setDatapoints(new ArrayList<Datapoint>());
		
		try {
			List<Datapoint> dlist = a.getDatapoints();
			a.setDatapoints(new ArrayList<Datapoint>());
			mt.deleteList(dlist);
			mt.persist(a);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void writeData(MiddleTier mt) {
		
		try {
			mt.createSchema("odietest");
			LanguageResource lr = createDummyLanguageResource(DIFF1);
			mt.persist(lr);
			LanguageResource lr2 = createDummyLanguageResource(DIFF2);
			mt.persist(lr2);
			
			List<LanguageResource> lsList = new ArrayList<LanguageResource>();
			
			lsList.add(lr);
			LexicalSet ls1 = createDummyLexicalSet(lsList);
			mt.persist(ls1);
			
			lsList.add(lr2);
			LexicalSet ls2 = createDummyLexicalSet(lsList);
			mt.persist(ls2);
			
			Document d = createDummyDocument(DUMMY_DOCURI1);
			mt.persist(d);
			Document d2 = createDummyDocument(DUMMY_DOCURI2);
			mt.persist(d2);
			
			AnalysisEngineMetadata aem = createDummyAEM(DIFF1);
			mt.persist(aem);
			aem = createDummyAEM(DIFF2);
			mt.persist(aem);
			
			Analysis a = createDummyAnalysis(DIFF1,aem);
			
			a.setProposalLexicalSet(ls1);
			a.setLexicalSet(ls2);
//			AnalysisLanguageResource alr= createDummyLanguageResource(a,lr);
//			a.addAnalysisLanguageResource(alr);
//			
//			alr= createDummyLanguageResource(a,lr2);
//			a.addAnalysisLanguageResource(alr);
			
			AnalysisDocument ad = createDummyAnalysisDocument(a,d,DIFF1);
			a.addAnalysisDocument(ad);

			AnalysisDocument ad2 = createDummyAnalysisDocument(a,d2,DIFF2);
			a.addAnalysisDocument(ad2);
			mt.persist(a);
			
			Datapoint dp = createDummyDatapoint(ad,DIFF1);
			dp.addAnalysisDocument(ad2);
			mt.persist(dp);

			dp = createDummyDatapoint(ad,DIFF2);
			dp.addAnalysisDocument(ad2);
			mt.persist(dp);
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
	}
	

	private static boolean readBackData(MiddleTier mt) {
		List<Analysis> analysisList = mt.getAnalyses();
		List<LanguageResource> lrList = mt.getLanguageResources(null);
		List<AnalysisEngineMetadata> aemList = mt.getAnalysisEngineMetadatas(null);
		List<LexicalSet> lsList = mt.getLexicalSets();
		List<Document> docList = mt.getDocuments();
		boolean pass = true;
		
				
		if(analysisList.size()!=1){
			logger.error("Incorrect no. of Analysis retrieved");
			pass=false;
			return pass;
		}
		if(lrList.size()<2){
			logger.error("Incorrect no. of LanguageResource retrieved");
			pass=false;
			return pass;
		}
		if(aemList.size()!=2){
			logger.error("Incorrect no. of AnalysisEngineMetadata retrieved");
			pass=false;
			return pass;
		}
		
		if(lsList.size()!=2){
			logger.error("Incorrect no. of LexicalSet retrieved");
			pass=false;
			return pass;
		}
		
		if(docList.size()!=2){
			logger.error("Incorrect no. of Documents retrieved");
			pass=false;
			return pass;
		}
		for(Analysis a:analysisList){
			if(a.getName().equals(DUMMY_STR1+DIFF1) && 
					a.getDescription().equals(DUMMY_STR1+DIFF1))
				;
			else{
				logger.error("Incorrect Analysis data retrieved");
				pass = false;
			}
			
			if(a.getDatapoints().size() != 2){
				logger.error("Incorrect no. of Datapoints retrieved");
				pass=false;
			}
			
			for(Datapoint d:a.getDatapoints()){
				logger.info("Checking Datapoint id="+d.getId());
				logger.info("Back linking to Analysis accurately:" + (d.getAnalysis() == a));
				if(d.getAnalysisDocuments().size() != 2){
					logger.error("Incorrect no. of AnalysisDocuments linked to Datapoint");
					pass=false;
				}
			}

			for(AnalysisDocument ad:a.getAnalysisDocuments()){
				mt.getSession().refresh(ad); //required since if we wrote data in this session then
											 //it needs to be updated	
				logger.info("Checking AnalysisDocument id="+ad.getId());
				logger.info("Back linking to Analysis accurately:" + (ad.getAnalysis() == a));
				if(ad.getDatapoints().size()!=2){
					logger.error("Incorrect no. of Datapoints linked");
					pass = false;
				}
			}
			
			
			logger.info("Checking LexicalSet id="+lsList.get(0).getId());
			if(lsList.get(0).getLexicalSetLanguageResources().size()!=1){
				logger.error("Incorrect no. of LanguageResource linked");
				pass = false;
			}
			logger.info("Back linking to Analysis accurately:" + (a.getProposalLexicalSet() == lsList.get(0)));

			logger.info("Checking LexicalSet id="+lsList.get(1).getId());
			if(lsList.get(1).getLexicalSetLanguageResources().size()!=2){
				logger.error("Incorrect no. of LanguageResource linked");
				pass = false;
			}
			logger.info("Back linking to Analysis accurately:" + (a.getLexicalSet() == lsList.get(1)));
		}
		
		if(!testLuceneSearchDocuments(mt))
			pass = false;
		
		return pass;
	}
	
	private static boolean testLuceneSearchDocuments(MiddleTier mt) {
		List<Document> docs = mt.searchDocumentText("impression");
		return docs.size() == 2;
	}

	private static void deleteData(MiddleTier mt) {
		
		List<Analysis> analysisList = mt.getAnalyses();
		List<LexicalSet> lsList = mt.getLexicalSets();
		List<LanguageResource> lrList = mt.getLanguageResources(null);
		List<AnalysisEngineMetadata> aemList = mt.getAnalysisEngineMetadatas(null);
		List<Document> docList = mt.getDocuments();
		
		List<Object> objList = new ArrayList<Object>();
		objList.addAll(aemList);
		objList.addAll(analysisList);
		objList.addAll(lrList);
		objList.addAll(lsList);
		objList.addAll(docList);
		
		for(Object obj:objList){
			try {
				mt.delete(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	
	private static LexicalSet createDummyLexicalSet(List<LanguageResource> lsList) {
		LexicalSet ls = new LexicalSet();
		ls.setName(DUMMY_STR1);
		ls.setDescription(DUMMY_STR2);
		
		for(LanguageResource lr:lsList){
			ls.addLanguageResource(lr);
		}
		
		return ls;
	}

	private static Datapoint createDummyDatapoint(AnalysisDocument ad, String diff) {
		try {
			return new Datapoint(DUMMY_STR1,DUMMY_URI1+diff, ad);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static AnalysisEngineMetadata createDummyAEM(String diff) {
		AnalysisEngineMetadata aem = new AnalysisEngineMetadata();
		aem.setName(DUMMY_STR2 + diff);
		aem.setType(DUMMY_STR2 + diff);
		try {
			aem.setURL(new URL(DUMMY_URI2+diff));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return aem;
	}

	



	private static AnalysisDocument createDummyAnalysisDocument(Analysis a,
			Document d, String differentiator) {
		AnalysisDocument ad = new AnalysisDocument(a,d);
		try {
			ad.setSerializedCAS(DUMMY_STR2+differentiator);
		} catch (ODIEException e) {
			e.printStackTrace();
		}
		return ad;
	}

	private static Document createDummyDocument(String dummyURI) {
		Document d = new Document();
		
		try {
			d.setURI(new URI(dummyURI));
			d.setName(dummyURI.substring(dummyURI.lastIndexOf("/")));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return d;
	}

//	private static AnalysisLanguageResource createDummyLanguageResource(Analysis a, LanguageResource lr) {
//		AnalysisLanguageResource alr = new AnalysisLanguageResource();
//		alr.setAnalysis(a);
//		alr.setIsProposalOntology(false);
//		alr.setLanguageResource(lr);
//		return alr;
//		
//	}

	private static LanguageResource createDummyLanguageResource(String differentiator) {
		LanguageResource lr = new LanguageResource();
		lr.setName(DUMMY_STR1 + differentiator);
		lr.setDescription(DUMMY_STR1 + differentiator);
		lr.setFormat(DUMMY_STR1 + differentiator);
		lr.setType(DUMMY_STR1 + differentiator);
		lr.setVersion(DUMMY_STR1 + differentiator);
		lr.setLocation(DUMMY_STR1 + differentiator);
		try {
			lr.setURI(new URI(DUMMY_URI2 + differentiator));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return lr;
	}

	private static Analysis createDummyAnalysis(String differentiator, AnalysisEngineMetadata aem) {
		Analysis a = new Analysis();
		a.setName(DUMMY_STR1 + differentiator);
		a.setDescription(DUMMY_STR1 + differentiator);
		a.setAnalysisEngineMetadata(aem);
		return a;
	}

}
