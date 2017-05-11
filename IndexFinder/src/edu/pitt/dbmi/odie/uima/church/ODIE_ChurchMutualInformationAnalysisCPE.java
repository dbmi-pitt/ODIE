package edu.pitt.dbmi.odie.uima.church;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.collection.metadata.CpeDescription;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.CasPool;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;

public class ODIE_ChurchMutualInformationAnalysisCPE {
	
	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ODIE_ChurchMutualInformationAnalysisCPE.class);

	private static CollectionProcessingEngine mCPE;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
		     //parse CPE descriptor in file specified on command line
			CpeDescription cpeDesc = UIMAFramework.getXMLParser().
			        parseCpeDescription(new XMLInputSource(args[0]));
			      
			      //instantiate CPE
			mCPE = UIMAFramework.produceCollectionProcessingEngine(cpeDesc);

			      //Create and register a Status Callback Listener
			mCPE.addStatusCallbackListener(new ODIE_StatusCallbackListenerImpl());

			      //Start Processing
			mCPE.process();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidXMLException e) {
			e.printStackTrace();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}

	}
	

	
	private static void testRunAE() throws IOException, InvalidXMLException, ResourceInitializationException {
 
		// get Resource Specifier from XML file
		XMLInputSource in = new XMLInputSource(
				"desc/church/ODIE_AE_Tokeniser.xml");

		ResourceSpecifier specifier = UIMAFramework.getXMLParser()
				.parseResourceSpecifier(in);

		// create AE here
		AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(specifier);

		logger.debug("Constructed the AE");

		testAEwithFragments(ae);

		logger.debug("Tested the AE");

		ae.destroy();

		logger.debug("Destroyed the AE");
	}

	private static void testAEwithFragments(AnalysisEngine ae) {
		try {
			// create a JCas, given an Analysis Engine (ae)
			JCas jcas = ae.newJCas();
			// analyze a document
			jcas.setDocumentText("Hello world.");
			ae.process(jcas);
			// doSomethingWithResults(jcas);
			jcas.reset();
			// analyze another document
			jcas.setDocumentText("Goodbye cruel world.");
			ae.process(jcas);
			// doSomethingWithResults(jcas);
			jcas.reset();

		} catch (AnalysisEngineProcessException e) {
			e.printStackTrace();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Example of a CAS Pool
	 */
	private static AnalysisEngine mAnalysisEngine;
	private static CasPool mCasPool;
	
	private static void testCasPool() {
		XMLInputSource in;
		try {
			// get Resource Specifier from XML file
			in = new XMLInputSource("descriptors/cpe/ODIE_Tokeniser.xml");
			ResourceSpecifier specifier = UIMAFramework.getXMLParser()
					.parseResourceSpecifier(in);
			// Create multithreadable AE that will
			// Accept 3 simultaneous requests
			// The 3rd parameter specifies a timeout.
			// When the number of simultaneous requests exceeds 3,
			// additional requests will wait for other requests to finish.
			// This parameter determines the maximum number of milliseconds
			// that a new request should wait before throwing an
			// - a value of 0 will cause them to wait forever.
			mAnalysisEngine = UIMAFramework.produceAnalysisEngine(specifier, 3,
					0);

			// create CAS pool with 3 CAS instances
			mCasPool = new CasPool(3, mAnalysisEngine);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidXMLException e) {
			e.printStackTrace();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}

	}

	public void analyzeDocument(String aDoc) {
		// check out a CAS instance (argument 0 means no timeout)
		CAS cas = mCasPool.getCas(0);
		try {
			// analyze a document
			cas.setDocumentText(aDoc);
			mAnalysisEngine.process(cas);
			// doSomethingWithResults(cas);
		} catch (AnalysisEngineProcessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// MAKE SURE we release the CAS instance
			mCasPool.releaseCas(cas);
		}
	}

}
