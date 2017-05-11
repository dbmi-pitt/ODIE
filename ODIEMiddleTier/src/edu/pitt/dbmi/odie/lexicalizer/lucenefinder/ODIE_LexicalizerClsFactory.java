package edu.pitt.dbmi.odie.lexicalizer.lucenefinder;

import edu.pitt.ontology.IClass;

public class ODIE_LexicalizerClsFactory {

	private int odieCui = 1000000 ;
	
	public ODIE_LexicalizerClsFactory() {
		;
	}
	
	public ODIE_LexicalizerClsWrapper newCls(IClass cls) {
		ODIE_LexicalizerClsWrapper odieLexicalizerCls = new ODIE_LexicalizerClsWrapper() ;
		odieLexicalizerCls.setCls(cls) ;
		odieLexicalizerCls.setUri(cls.getURI().toString()) ;
		odieLexicalizerCls.setOdieCui(odieCui++) ;
		return odieLexicalizerCls ;
	}
	
	
	
}
