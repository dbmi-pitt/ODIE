/* First created by JCasGen Fri Jun 12 16:17:31 EDT 2009 */
package edu.pitt.dbmi.odie.uima.types;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.tcas.Annotation_Type;

/**
 * Stores the mapping information to this CASes persisted instance. Updated by
 * JCasGen Fri Jun 26 10:29:39 EDT 2009
 * 
 * @generated
 */
public class DatabaseInformation_Type extends Annotation_Type {
	/** @generated */
	protected FSGenerator getFSGenerator() {
		return fsGenerator;
	}

	/** @generated */
	private final FSGenerator fsGenerator = new FSGenerator() {
		public FeatureStructure createFS(int addr, CASImpl cas) {
			if (DatabaseInformation_Type.this.useExistingInstance) {
				// Return eq fs instance if already created
				FeatureStructure fs = DatabaseInformation_Type.this.jcas
						.getJfsFromCaddr(addr);
				if (null == fs) {
					fs = new DatabaseInformation(addr,
							DatabaseInformation_Type.this);
					DatabaseInformation_Type.this.jcas
							.putJfsFromCaddr(addr, fs);
					return fs;
				}
				return fs;
			} else
				return new DatabaseInformation(addr,
						DatabaseInformation_Type.this);
		}
	};
	/** @generated */
	public final static int typeIndexID = DatabaseInformation.typeIndexID;
	/**
	 * @generated
	 * @modifiable
	 */
	public final static boolean featOkTst = JCasRegistry
			.getFeatOkTst("edu.pitt.dbmi.odie.uima.types.DatabaseInformation");

	/** @generated */
	final Feature casFeat_documentAnalysisId;
	/** @generated */
	final int casFeatCode_documentAnalysisId;

	/** @generated */
	public long getDocumentAnalysisId(int addr) {
		if (featOkTst && casFeat_documentAnalysisId == null)
			jcas.throwFeatMissing("documentAnalysisId",
					"edu.pitt.dbmi.odie.uima.types.DatabaseInformation");
		return ll_cas.ll_getLongValue(addr, casFeatCode_documentAnalysisId);
	}

	/** @generated */
	public void setDocumentAnalysisId(int addr, long v) {
		if (featOkTst && casFeat_documentAnalysisId == null)
			jcas.throwFeatMissing("documentAnalysisId",
					"edu.pitt.dbmi.odie.uima.types.DatabaseInformation");
		ll_cas.ll_setLongValue(addr, casFeatCode_documentAnalysisId, v);
	}

	/**
	 * initialize variables to correspond with Cas Type and Features
	 * 
	 * @generated
	 */
	public DatabaseInformation_Type(JCas jcas, Type casType) {
		super(jcas, casType);
		casImpl.getFSClassRegistry().addGeneratorForType(
				(TypeImpl) this.casType, getFSGenerator());

		casFeat_documentAnalysisId = jcas.getRequiredFeatureDE(casType,
				"documentAnalysisId", "uima.cas.Long", featOkTst);
		casFeatCode_documentAnalysisId = (null == casFeat_documentAnalysisId) ? JCas.INVALID_FEATURE_CODE
				: ((FeatureImpl) casFeat_documentAnalysisId).getCode();

	}
}
