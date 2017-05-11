/**
 * 
 */
package edu.pitt.dbmi.odie.model;

import java.util.SortedSet;

import org.apache.log4j.Logger;

import edu.pitt.dbmi.odie.middletier.MiddleTierJDBC;

/**
 * @author Girish Chavan
 * 
 */
@Deprecated
public class DocumentAnalysisJDBC extends AnalysisDocument {
	Logger logger = Logger.getLogger(this.getClass());
	int analysisID = -1;
	int documentID = -1;

	/**
	 * @param analysis
	 */
	public DocumentAnalysisJDBC(Analysis analysis, int ID) {
		super(analysis, null);
		setId((long)ID);

		// super class constructor will initialize annotations to
		// empty list so we set to null to force lazy loading.
		setAnnotations(null);
	}

	/**
	 * 
	 */
	private void lazyFetchAnnotations() {
		if (annotations == null) {
			logger.debug("Lazy fetching annotations");
			// assumes that the singleton is already instantiated.
			MiddleTierJDBC mt = MiddleTierJDBC.getInstance(null);
			annotations = mt.getAnnotations(this);
		}

	}

	/**
	 * 
	 */
	
	private void lazyFetchDocument() {
		if (document == null) {

			logger.debug("Lazy fetching  document");
			if (documentID < 0) {
				lazyFetchSelf();
			}
			// assumes that the singleton is already instantiated.
			MiddleTierJDBC mt = MiddleTierJDBC.getInstance(null);
			document = mt.getDocument(this);
		}

	}

	/**
	 * 
	 */
	private void lazyFetchSelf() {
		MiddleTierJDBC mt = MiddleTierJDBC.getInstance(null);
		mt.loadDocumentAnalysisFromDatabase(this);

	}

	public int getAnalysisID() {
		return analysisID;
	}

	@Override
	public SortedSet<Annotation> getAnnotations() {
		lazyFetchAnnotations();
		return super.getAnnotations();
	}

	@Override
	public Document getDocument() {
		lazyFetchDocument();
		return super.getDocument();
	}

	public int getDocumentID() {
		return documentID;
	}

	public void setAnalysisID(int analysisID) {
		this.analysisID = analysisID;
	}

	public void setDocumentID(int documentID) {
		this.documentID = documentID;
	}

}
