package edu.pitt.dbmi.odie.uima.collection;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.uima.UIMAFramework;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.collection.CasConsumerDescription;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.xml.sax.SAXException;

import edu.pitt.dbmi.odie.ODIEException;
import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.uima.types.DatabaseInformation;
import edu.pitt.dbmi.odie.uima.utils.UIMAUtils;
import edu.pitt.dbmi.odie.utils.StatisticsTracker;
import edu.pitt.dbmi.odie.utils.StopWatch;

public class AnalysisCasConsumer extends CasConsumer_ImplBase {

	StatisticsTracker dt = new StatisticsTracker();
	Logger logger = Logger.getLogger(AnalysisCasConsumer.class);
	@Override
	public void processCas(CAS acas) throws ResourceProcessException {
		JCas jcas;
		try {
			jcas = acas.getJCas();

			AnnotationIndex anindex = jcas
					.getAnnotationIndex(DatabaseInformation.type);
			Iterator timeIter = anindex.iterator();
			if(timeIter.hasNext()) {
//				StopWatch st = new StopWatch();
//				st.start();
				DatabaseInformation di = (DatabaseInformation) timeIter.next();
				Long daId = di.getDocumentAnalysisId();

				MiddleTier mt = Activator.getDefault().getMiddleTier();
				AnalysisDocument ad = mt.getAnalysisDocument(daId);
//				logger.info("Fetched AD" + st.getElapsedTime());
				if (ad == null)
					throw new ResourceProcessException(
							ResourceProcessException.RESOURCE_DATA_NOT_VALID,
							new String[] { "" + daId, "DocumentAnalysis ID" });

				String serializedCas;

				serializedCas = UIMAUtils.serializeCAS(acas);
				ad.setSerializedCAS(serializedCas);
				ad.setStatus(AnalysisDocument.STATUS_DONE);
//				logger.info("Serialized CAS:" + st.getElapsedTime());
				dt.processDocument(acas, ad);
//				logger.info("Tracked datapoints:" + st.getElapsedTime());
				mt.persist(ad);
				mt.persist(ad.getAnalysis());
//				logger.info("Saved analysis:" + st.getElapsedTime());
//				st.stop();
				return;
			}

			throw new ResourceProcessException(
					ResourceProcessException.REQUIRED_FEATURE_STRUCTURE_MISSING_FROM_CAS,
					new String[] { DatabaseInformation.class.getName() });

		} catch (CASException e) {
			e.printStackTrace();
			throw new ResourceProcessException(e);
		} catch (SAXException e) {
			e.printStackTrace();
			throw new ResourceProcessException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResourceProcessException(e);
		} catch (ODIEException e) {
			e.printStackTrace();
			throw new ResourceProcessException(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResourceProcessException(e);
		}
	}

	public static CasConsumerDescription getDescription()
			throws InvalidXMLException {
		InputStream descStream = AnalysisCasConsumer.class
				.getResourceAsStream("AnalysisCasConsumer.xml");
		return UIMAFramework.getXMLParser().parseCasConsumerDescription(
				new XMLInputSource(descStream, null));
	}
}
