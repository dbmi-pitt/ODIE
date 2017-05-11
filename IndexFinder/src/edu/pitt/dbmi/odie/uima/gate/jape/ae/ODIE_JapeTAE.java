package edu.pitt.dbmi.odie.uima.gate.jape.ae;

import org.apache.uima.analysis_engine.ResultSpecification;
import org.apache.uima.analysis_engine.annotator.AnnotatorConfigurationException;
import org.apache.uima.analysis_engine.annotator.AnnotatorContext;
import org.apache.uima.analysis_engine.annotator.AnnotatorContextException;
import org.apache.uima.analysis_engine.annotator.AnnotatorInitializationException;
import org.apache.uima.analysis_engine.annotator.AnnotatorProcessException;
import org.apache.uima.analysis_engine.annotator.JTextAnnotator_ImplBase;
import org.apache.uima.jcas.JCas;

import edu.pitt.dbmi.odie.uima.gate.resource.ODIE_GateProcessingResourceUrlResource;
import edu.pitt.dbmi.odie.uima.gate.resource.ODIE_GateResource;
import edu.pitt.dbmi.odie.uima.gate.util.ODIE_Gate2UimaXFerrer;
import edu.pitt.dbmi.odie.uima.gate.util.ODIE_Uima2GateXFerrer;
import edu.pitt.dbmi.odie.uima.util.ODIE_IFConstants;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.Transducer;

@SuppressWarnings("deprecation")
public class ODIE_JapeTAE extends JTextAnnotator_ImplBase {

	private AnnotatorContext annotatorContext;

	private ODIE_GateResource factoryDelegate;

	private ODIE_GateProcessingResourceUrlResource japeResource;

	private Transducer odieJapePR = null;

	private ODIE_Uima2GateXFerrer xFerUima2Gate;
	
	private ODIE_Gate2UimaXFerrer xFerGate2Uima;

	public void initialize(AnnotatorContext aContext)
			throws AnnotatorConfigurationException,
			AnnotatorInitializationException {

		super.initialize(aContext);
		annotatorContext = aContext;

		try {
			// Pull the location of the GATE factory
			String resourceKey = "ODIE_Gate_Factory";
			this.factoryDelegate = (ODIE_GateResource) annotatorContext
					.getResourceObject(resourceKey);

			// Pull the jape url
			resourceKey = (String) annotatorContext.getConfigParameterValue(ODIE_IFConstants.IF_GATE_SCRIPT_RESOURCE_KEY_KEY) ;
			this.japeResource = (ODIE_GateProcessingResourceUrlResource) annotatorContext
					.getResourceObject(resourceKey);

			// Run the document through the JAPE Transducer
			FeatureMap params = Factory.newFeatureMap();
			String grammarUrlPath = this.japeResource.getUrl().toString();
			params.put("grammarURL", grammarUrlPath);
			params.put("encoding", "UTF-8");
			odieJapePR = (Transducer) Factory.createResource(
					"gate.creole.Transducer", params);
			odieJapePR.init();

			this.xFerUima2Gate = new ODIE_Uima2GateXFerrer();
			this.xFerUima2Gate.setFactoryDelegate(this.factoryDelegate);
			
			this.xFerGate2Uima = new ODIE_Gate2UimaXFerrer();
			this.xFerGate2Uima.setFactoryDelegate(this.factoryDelegate);

		} catch (AnnotatorContextException ace) {
			throw new AnnotatorConfigurationException(ace);
		} catch (ResourceInstantiationException e) {
			e.printStackTrace();
		}
	}

	public void process(JCas aJCas, ResultSpecification resultSpec)
			throws AnnotatorProcessException {
		try {

			Document doc = this.factoryDelegate.newDocument(aJCas
					.getDocumentText());
			this.xFerUima2Gate.tranferJCasToDocument(aJCas, doc);
			odieJapePR.setDocument(doc);
			odieJapePR.execute();
			this.xFerGate2Uima.transferDocumentToJCas(doc, aJCas, this.xFerUima2Gate.getAnnotsToRemove());

			this.factoryDelegate.deleteResource(doc) ;
			
		} catch (ResourceInstantiationException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	public void destroy() {
		if (this.odieJapePR != null) {
			Factory.deleteResource(this.odieJapePR) ;
		}
	}

}
