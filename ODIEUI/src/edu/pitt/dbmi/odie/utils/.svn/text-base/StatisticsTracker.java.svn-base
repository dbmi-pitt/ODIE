/**
 *@author Girish Chavan
 *Sep 6, 2008 
 *
 */
package edu.pitt.dbmi.odie.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.uima.cas.ArrayFS;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.mayo.bmi.uima.chunker.type.NP;
import edu.mayo.bmi.uima.core.type.NamedEntity;
import edu.mayo.bmi.uima.core.type.NewlineToken;
import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.model.Datapoint;
import edu.pitt.dbmi.odie.model.Statistics;
import edu.pitt.dbmi.odie.uima.utils.UIMAUtils;

public class StatisticsTracker {

	private HashMap<String, Datapoint> datapointMap = new HashMap<String, Datapoint>();
	Logger logger = Logger.getLogger(this.getClass());

	boolean initialized = false;

	public void updateDatapoints(AnnotationFS neAnn, AnalysisDocument ad) {
		
		ArrayFS arr = UIMAUtils.getOntologyConceptArray(neAnn);
		for (int i = 0; i < arr.size(); i++) {
//			StopWatch st = new StopWatch();
//			st.start();
			FeatureStructure fs = arr.get(i);
			String conceptURIString = GeneralUtils.getConceptURIFromFS(fs);
			Datapoint dp = datapointMap.get(fs.getType().getName()
					+ conceptURIString);
//			logger.debug("Searched datapoint:"+st.getElapsedTime() + "msec");
			if (dp == null) {
				dp = new Datapoint(fs.getType().getName(), conceptURIString, ad);
				try {
					dp.setConceptClass(GeneralUtils.getConceptClass(dp.getAnalysis(),dp
							.getConceptURIString()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				datapointMap.put(fs.getType().getName() + conceptURIString, dp);
//				logger.debug("Created datapoint:"+st.getElapsedTime() + "msec");
			} else {
				dp.addAnalysisDocument(ad);
//				logger.debug("Updated datapoint:"+st.getElapsedTime() + "msec");
			}
//			st.stop();
		}

	}

	private void updateOntologyCounts(AnnotationFS neAnn,
			HashMap<String, Statistics> oCC) {
//		StopWatch st = new StopWatch();
//		st.start();
		
		ArrayFS arr = UIMAUtils.getOntologyConceptArray(neAnn);

		for (int i = 0; i < arr.size(); i++) {
			FeatureStructure fs = arr.get(i);
			String ontologyURIString = GeneralUtils.getOntologyURIFromFS(fs);
			String conceptURIString = GeneralUtils.getConceptURIFromFS(fs);

			Statistics s = oCC.get(ontologyURIString);
			if (s == null) {
				s = new Statistics();
				try {
					s.context = new URI(ontologyURIString);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				oCC.put(ontologyURIString, s);
			}


			// count only the first occurence for unique concepts
			Datapoint dp = datapointMap.get(fs.getType().getName()
					+ conceptURIString);
			if (dp.getOccurences() == 1)
				s.uniqueConceptsCount++;

			s.coveredCharCount += (neAnn.getEnd() - neAnn.getBegin());
			s.namedEntityCount++;
		}
//		logger.debug("Updated DP statistics:"+st.getElapsedTime() + "msec");
//		st.stop();
	}

	public void updateDatapoints(CAS acas, AnalysisDocument ad) {
		try {
			AnnotationIndex annIndex = acas.getJCas().getAnnotationIndex(
					NamedEntity.type);

			for (Iterator it = annIndex.iterator(); it.hasNext();) {
//				StopWatch st = new StopWatch();
//				st.start();
				AnnotationFS ann = (AnnotationFS) it.next();
				updateDatapoints(ann, ad);
//				logger.info("datapoints updated:" + st.getElapsedTime());
				updateOntologyCounts(ann, ontologyCoverageCounts);
//				st.stop();
//				logger.info("ontology counts updated:" + st.getElapsedTime());
			}
		} catch (CASRuntimeException e) {
			e.printStackTrace();
		} catch (CASException e) {
			e.printStackTrace();
		}
	}

	HashMap<String, Statistics> ontologyCoverageCounts = new HashMap<String, Statistics>();

	private void init(Analysis analysis) {
		for (Datapoint dp : analysis.getDatapoints()) {
			datapointMap.put(dp.getType() + dp.getConceptURIString(), dp);
		}
		initialized = true;
	}

	public void processDocument(CAS acas, AnalysisDocument ad) {

		logger.debug("Tracking AnalysisDocumentID:" + ad.getId());
//		if (GeneralUtils.isOther(ad.getAnalysis()))
//			return;

//		StopWatch st = new StopWatch();
//		st.start();
		updateDatapoints(acas, ad);
//		logger.info("Updated datapoints:" + st.getElapsedTime());
		updateADMetadata(acas, ad);
//		logger.info("Updated ADMetadata:" + st.getElapsedTime());
		updateAnalysisMetadata(acas, ad);
//		logger.info("Updated AMetadata:" + st.getElapsedTime());
//		st.stop();
	}

	private void updateAnalysisMetadata(CAS acas, AnalysisDocument ad) {
		Analysis a = ad.getAnalysis();
		Statistics analysisStatistics = a.getStatistics();

		try {
			AnnotationIndex annIndex;
			Type npType = UIMAUtils.getNPType(acas);
			int npCount = 0;
			if(npType != null){
				annIndex = acas.getAnnotationIndex(npType);
				npCount = annIndex.size();
			}

			annIndex = acas.getJCas().getAnnotationIndex(NamedEntity.type);
			int neCount = annIndex.size();
			analysisStatistics.nounPhraseCount += npCount;
			analysisStatistics.namedEntityCount += neCount;

			annIndex = acas.getJCas().getAnnotationIndex(NewlineToken.type);
			analysisStatistics.totalCharCount += (ad.getDocument().getText().length() - annIndex
					.size());

			analysisStatistics.ontologyStatistics = ontologyCoverageCounts.values();
		} catch (CASRuntimeException e) {
			e.printStackTrace();
		} catch (CASException e) {
			e.printStackTrace();
		}

		long totalCoveredCharCount = 0;
		long uniqueConceptsCount = 0;
		for (Statistics os : analysisStatistics.ontologyStatistics) {
			totalCoveredCharCount += os.coveredCharCount;
			uniqueConceptsCount += os.uniqueConceptsCount;

			os.totalCharCount = analysisStatistics.totalCharCount;
		}

		analysisStatistics.coveredCharCount = totalCoveredCharCount;
		analysisStatistics.uniqueConceptsCount = uniqueConceptsCount;

		a.refreshMetadataFromStatistics();
	}

	private void updateADMetadata(CAS acas, AnalysisDocument ad) {
		try {
			AnnotationIndex annIndex = acas.getJCas().getAnnotationIndex(
					NP.type);
			int npCount = annIndex.size();

			annIndex = acas.getJCas().getAnnotationIndex(NamedEntity.type);
//			annIndex = acas.getAnnotationIndex(UIMAUtils
//					.getNamedEntityType(acas));
			int neCount = annIndex.size();

			Document doc = buildMetadataShellDocument();
			addAnalysisDocumentStatistics(doc, ad, npCount, neCount);
			XMLOutputter o = new XMLOutputter(Format.getPrettyFormat());
			StringWriter w = new StringWriter();

			o.output(doc, w);
			ad.setMetadata(w.toString());
		} catch (IOException e) {
			logger.error(e.getMessage());
		} catch (CASRuntimeException e) {
			logger.error(e.getMessage());
		} catch (CASException e) {
			logger.error(e.getMessage());
		}
	}

	private void addAnalysisDocumentStatistics(Document doc,
			AnalysisDocument ad, int npCount, int neCount) {
		Element adElement = new Element(
				ODIEConstants.XMLELEMENT_ANALYSIS_DOCUMENT);
		adElement.setAttribute(ODIEConstants.XMLATTRIBUTE_ID, "" + ad.getId());

		Element npC = new Element(ODIEConstants.XMLELEMENT_NP_COUNT);
		npC.setText("" + npCount);
		Element neC = new Element(ODIEConstants.XMLELEMENT_NAMED_ENTITY_COUNT);
		neC.setText("" + neCount);

		adElement.addContent(npC);
		adElement.addContent(neC);

		Element statistics = doc.getRootElement().getChild(
				ODIEConstants.XMLELEMENT_STATISTICS);
		statistics.addContent(adElement);
	}

	private Document buildMetadataShellDocument() {
		Element root = new Element(ODIEConstants.XMLELEMENT_ROOT);
		Document doc = new Document(root);
		Element statistics = new Element(ODIEConstants.XMLELEMENT_STATISTICS);
		root.addContent(statistics);

		return doc;

	}
}
