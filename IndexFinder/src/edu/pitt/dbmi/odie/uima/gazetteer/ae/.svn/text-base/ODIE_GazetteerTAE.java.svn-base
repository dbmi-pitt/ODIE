package edu.pitt.dbmi.odie.uima.gazetteer.ae;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.uima.analysis_engine.ResultSpecification;
import org.apache.uima.analysis_engine.annotator.AnnotatorConfigurationException;
import org.apache.uima.analysis_engine.annotator.AnnotatorContext;
import org.apache.uima.analysis_engine.annotator.AnnotatorContextException;
import org.apache.uima.analysis_engine.annotator.AnnotatorInitializationException;
import org.apache.uima.analysis_engine.annotator.AnnotatorProcessException;
import org.apache.uima.analysis_engine.annotator.JTextAnnotator_ImplBase;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;

import edu.mayo.bmi.fsm.token.BaseToken;
import edu.mayo.bmi.uima.core.ae.TokenizerAnnotator;
import edu.mayo.bmi.uima.core.fsm.adapters.ContractionTokenAdapter;
import edu.mayo.bmi.uima.core.fsm.adapters.DecimalTokenAdapter;
import edu.mayo.bmi.uima.core.fsm.adapters.IntegerTokenAdapter;
import edu.mayo.bmi.uima.core.fsm.adapters.NewlineTokenAdapter;
import edu.mayo.bmi.uima.core.fsm.adapters.PunctuationTokenAdapter;
import edu.mayo.bmi.uima.core.fsm.adapters.SymbolTokenAdapter;
import edu.mayo.bmi.uima.core.fsm.adapters.WordTokenAdapter;
import edu.mayo.bmi.uima.core.type.ContractionToken;
import edu.mayo.bmi.uima.core.type.NewlineToken;
import edu.mayo.bmi.uima.core.type.NumToken;
import edu.mayo.bmi.uima.core.type.PunctuationToken;
import edu.mayo.bmi.uima.core.type.SymbolToken;
import edu.mayo.bmi.uima.core.type.WordToken;
import edu.pitt.dbmi.odie.uima.gazetteer.type.Lookup;
import edu.pitt.dbmi.odie.uima.util.ODIE_IFConstants;

public class ODIE_GazetteerTAE extends JTextAnnotator_ImplBase {

	private AnnotatorContext annotatorContext;
	
	private ODIE_GazetteerResource gazetteer ;

	public void initialize(AnnotatorContext aContext)
			throws AnnotatorConfigurationException,
			AnnotatorInitializationException {

		super.initialize(aContext);
		annotatorContext = aContext;
		
		try {
			String resourceKey = ODIE_IFConstants.IF_PARAM_ODIE_LSP_GAZETTEER ;
			this.gazetteer = (ODIE_GazetteerResource) annotatorContext.getResourceObject(resourceKey);
			if (this.gazetteer == null) {
				resourceKey = "ODIE_AE_Gazetteer/ODIE_Lsp_Gazetteer" ;
				this.gazetteer = (ODIE_GazetteerResource) annotatorContext.getResourceObject(resourceKey);
			}
			if (this.gazetteer == null) {
				resourceKey = "ODIE_Lsp_Gazetteer" ;
				this.gazetteer = (ODIE_GazetteerResource) annotatorContext.getResourceObject(resourceKey);
			}
			if (this.gazetteer == null) {
				String[] parameterNames = annotatorContext.getConfigParameterNames() ;
				if (parameterNames != null && parameterNames.length > 0) {
					for (int idx = 0 ; idx < parameterNames.length ; idx++) {
						System.out.println(parameterNames[idx]) ;
					}
				}
			} 
		} catch (AnnotatorContextException ace) {
			throw new AnnotatorConfigurationException(ace);
		}
	}

	public void process(JCas aJCas, ResultSpecification arg1)
			throws AnnotatorProcessException {
		
		FSIndex wordIndex = aJCas.getAnnotationIndex(edu.mayo.bmi.uima.core.type.BaseToken.type);
		
		Iterator<edu.mayo.bmi.uima.core.type.BaseToken> wordIter = wordIndex.iterator();

		ArrayList<BaseToken> tokenArrayList = new ArrayList<BaseToken>() ;
		while (wordIter.hasNext()) {
			try {
				tokenArrayList.add(adaptToBaseToken(wordIter.next())) ;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Collection<Lookup> lookupAnnotations = this.gazetteer.process(aJCas, tokenArrayList) ;
		for (Lookup lookup : lookupAnnotations) {
			lookup.addToIndexes(aJCas) ;
		}
		
	}
	
	/**
	 * Adapts JCas objects to BaseToken interfaces expected by the Finite State
	 * Machines.
	 * 
	 * @param obj
	 * @return
	 */
	private BaseToken adaptToBaseToken(edu.mayo.bmi.uima.core.type.BaseToken obj) throws Exception {
		if (obj instanceof WordToken) {
			WordToken wta = (WordToken) obj;
			return new WordTokenAdapter(wta);
		} else if (obj instanceof NumToken) {
			NumToken nta = (NumToken) obj;
			if (nta.getNumType() == TokenizerAnnotator.TOKEN_NUM_TYPE_INTEGER) {
				return new IntegerTokenAdapter(nta);
			} else {
				return new DecimalTokenAdapter(nta);
			}
		} else if (obj instanceof PunctuationToken) {
			PunctuationToken pta = (PunctuationToken) obj;
			return new PunctuationTokenAdapter(pta);
		} else if (obj instanceof NewlineToken) {
			NewlineToken nta = (NewlineToken) obj;
			return new NewlineTokenAdapter(nta);
		} else if (obj instanceof ContractionToken) {
			ContractionToken cta = (ContractionToken) obj;
			return new ContractionTokenAdapter(cta);
		} else if (obj instanceof SymbolToken) {
			SymbolToken sta = (SymbolToken) obj;
			return new SymbolTokenAdapter(sta);
		}

		throw new Exception("No Context Dependent Tokenizer adapter for class: " + obj.getClass());
	}
	
}
