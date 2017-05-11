package edu.pitt.dbmi.odie.lexicalizer.lucenefinder;

import edu.pitt.ontology.IClass;

public class ODIE_LexicalizerClsWrapper {
	
	private IClass cls = null ;

	private String uri = null ;

	private int odieCui = -1 ;
	
	public ODIE_LexicalizerClsWrapper() {
	}
	
	private String deriveClassName(IClass cls) {
		String indexableClassName = cls.getURI().toString();
		if (indexableClassName.startsWith("RID")) {
			System.out.println("bad name detection.") ;
		}
		indexableClassName = getSimpleName(indexableClassName);
		indexableClassName = ODIE_IndexFinderBestMneumonicNameDeterminer
				.getInstance().bestGuessForMneumonicLabel(indexableClassName,
						cls.getLabels());
		return indexableClassName;

	}
	
	private String getSimpleName(String uri) {
		String result = "";
		result = uri.substring(uri.lastIndexOf("#") + 1, uri.length());
		return result;
	}
	
	
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public int getOdieCui() {
		return odieCui;
	}

	public void setOdieCui(int odieCui) {
		this.odieCui = odieCui;
	}
	

	public IClass getCls() {
		return cls;
	}

	public void setCls(IClass cls) {
		this.cls = cls;
	}

}
