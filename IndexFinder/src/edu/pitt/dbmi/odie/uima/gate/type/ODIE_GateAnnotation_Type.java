
/* First created by JCasGen Wed Sep 09 12:36:40 EDT 2009 */
package edu.pitt.dbmi.odie.uima.gate.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** Used to store transient GATE annotations in the CAS
between calls to Wrapped GATE PRs
 * Updated by JCasGen Mon Nov 08 13:14:55 EST 2010
 * @generated */
public class ODIE_GateAnnotation_Type extends Annotation_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (ODIE_GateAnnotation_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = ODIE_GateAnnotation_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new ODIE_GateAnnotation(addr, ODIE_GateAnnotation_Type.this);
  			   ODIE_GateAnnotation_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new ODIE_GateAnnotation(addr, ODIE_GateAnnotation_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = ODIE_GateAnnotation.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateAnnotation");
 
  /** @generated */
  final Feature casFeat_gateAnnotationId;
  /** @generated */
  final int     casFeatCode_gateAnnotationId;
  /** @generated */ 
  public int getGateAnnotationId(int addr) {
        if (featOkTst && casFeat_gateAnnotationId == null)
      jcas.throwFeatMissing("gateAnnotationId", "edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateAnnotation");
    return ll_cas.ll_getIntValue(addr, casFeatCode_gateAnnotationId);
  }
  /** @generated */    
  public void setGateAnnotationId(int addr, int v) {
        if (featOkTst && casFeat_gateAnnotationId == null)
      jcas.throwFeatMissing("gateAnnotationId", "edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateAnnotation");
    ll_cas.ll_setIntValue(addr, casFeatCode_gateAnnotationId, v);}
    
  
 
  /** @generated */
  final Feature casFeat_gateAnnotationType;
  /** @generated */
  final int     casFeatCode_gateAnnotationType;
  /** @generated */ 
  public String getGateAnnotationType(int addr) {
        if (featOkTst && casFeat_gateAnnotationType == null)
      jcas.throwFeatMissing("gateAnnotationType", "edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_gateAnnotationType);
  }
  /** @generated */    
  public void setGateAnnotationType(int addr, String v) {
        if (featOkTst && casFeat_gateAnnotationType == null)
      jcas.throwFeatMissing("gateAnnotationType", "edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_gateAnnotationType, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public ODIE_GateAnnotation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_gateAnnotationId = jcas.getRequiredFeatureDE(casType, "gateAnnotationId", "uima.cas.Integer", featOkTst);
    casFeatCode_gateAnnotationId  = (null == casFeat_gateAnnotationId) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_gateAnnotationId).getCode();

 
    casFeat_gateAnnotationType = jcas.getRequiredFeatureDE(casType, "gateAnnotationType", "uima.cas.String", featOkTst);
    casFeatCode_gateAnnotationType  = (null == casFeat_gateAnnotationType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_gateAnnotationType).getCode();

  }
}



    