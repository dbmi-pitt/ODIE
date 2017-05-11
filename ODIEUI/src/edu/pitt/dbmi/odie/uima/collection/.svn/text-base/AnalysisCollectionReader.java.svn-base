package edu.pitt.dbmi.odie.uima.collection;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.UIMAFramework;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.XMLInputSource;
import org.xml.sax.SAXException;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.uima.types.DatabaseInformation;
import edu.pitt.dbmi.odie.uima.utils.UIMAUtils;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class AnalysisCollectionReader extends CollectionReader_ImplBase {

	// must match the param name used in the descriptor.
	public static final String PARAM_ANALYSIS_NAME = "ANALYSIS_NAME";

	private List<AnalysisDocument> docs;

	int currentIndex = 0;

	@Override
	public void initialize() throws ResourceInitializationException {

		String analysisName = (String) getConfigParameterValue(PARAM_ANALYSIS_NAME);
		Analysis analysis = Activator.getDefault().getMiddleTier()
				.getAnalysisForName(analysisName);

		if (analysis == null) {
			throw new ResourceInitializationException(
					ResourceInitializationException.RESOURCE_DATA_NOT_VALID,
					new String[] { "" + analysisName, PARAM_ANALYSIS_NAME });
		}
		docs = Activator.getDefault().getMiddleTier().getAnalysisDocuments(
				analysis, AnalysisDocument.STATUS_PROCESSING);
	}

	@Override
	public void getNext(CAS acas) throws IOException, CollectionException {
		AnalysisDocument da = docs.get(currentIndex);

		if (da.getSerializedCAS() != null) {
			try {
				UIMAUtils.deSerializeCAS(da.getSerializedCAS(), acas);
			} catch (SAXException e1) {
				e1.printStackTrace();
			}

			// AnnotationIndex annIndex =
			// acas.getAnnotationIndex(UIMAUtils.getNamedEntityType(acas));
			//			
			// for(Iterator it = annIndex.iterator();it.hasNext();){
			// AnnotationFS ann = (AnnotationFS) it.next();
			// }

		} else {
			JCas jcas;

			try {
				jcas = acas.getJCas();
				da.getDocument().loadTextFromURI();
			} catch (CASException e) {
				throw new CollectionException(e);
			}

			String text = da.getDocument().getText();
			jcas.setDocumentText(text);

			jcas.setDocumentLanguage("en");
			DatabaseInformation dbinfo = new DatabaseInformation(jcas);
			dbinfo.setDocumentAnalysisId(da.getId());
			dbinfo.addToIndexes();
		}
		currentIndex++;

	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public Progress[] getProgress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasNext() throws IOException, CollectionException {
		return currentIndex < docs.size();
	}

	public static CollectionReaderDescription getDescription()
			throws InvalidXMLException {
		InputStream descStream = AnalysisCollectionReader.class
				.getResourceAsStream("AnalysisCollectionReader.xml");
		return UIMAFramework.getXMLParser().parseCollectionReaderDescription(
				new XMLInputSource(descStream, null));
	}

	public int getNumberOfDocuments() {
		return docs.size();
	}

}
