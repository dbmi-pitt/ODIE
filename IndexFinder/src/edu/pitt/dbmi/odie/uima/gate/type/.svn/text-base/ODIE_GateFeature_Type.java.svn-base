
/* First created by JCasGen Wed Sep 09 15:30:24 EDT 2009 */
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

/** 
 * Updated by JCasGen Mon Nov 08 13:14:55 EST 2010
 * @generated */
public class ODIE_GateFeature_Type extends Annotation_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (ODIE_GateFeature_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = ODIE_GateFeature_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new ODIE_GateFeature(addr, ODIE_GateFeature_Type.this);
  			   ODIE_GateFeature_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new ODIE_GateFeature(addr, ODIE_GateFeature_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = ODIE_GateFeature.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateFeature");
 
  /** @generated */
  final Feature casFeat_gateAnnotationId;
  /** @generated */
  final int     casFeatCode_gateAnnotationId;
  /** @generated */ 
  public int getGateAnnotationId(int addr) {
        if (featOkTst && casFeat_gateAnnotationId == null)
      jcas.throwFeatMissing("gateAnnotationId", "edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateFeature");
    return ll_cas.ll_getIntValue(addr, casFeatCode_gateAnnotationId);
  }
  /** @generated */    
  public void setGateAnnotationId(int addr, int v) {
        if (featOkTst && casFeat_gateAnnotationId == null)
      jcas.throwFeatMissing("gateAnnotationId", "edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateFeature");
    ll_cas.ll_setIntValue(addr, casFeatCode_gateAnnotationId, v);}
    
  
 
  /** @generated */
  final Feature casFeat_gateFeatureKey;
  /** @generated */
  final int     casFeatCode_gateFeatureKey;
  /** @generated */ 
  public String getGateFeatureKey(int addr) {
        if (featOkTst && casFeat_gateFeatureKey == null)
      jcas.throwFeatMissing("gateFeatureKey", "edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateFeature");
    return ll_cas.ll_getStringValue(addr, casFeatCode_gateFeatureKey);
  }
  /** @generated */    
  public void setGateFeatureKey(int addr, String v) {
        if (featOkTst && casFeat_gateFeatureKey == null)
      jcas.throwFeatMissing("gateFeatureKey", "edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateFeature");
    ll_cas.ll_setStringValue(addr, casFeatCode_gateFeatureKey, v);}
    
  
 
  /** @generated */
  final Feature casFeat_gateFeatureValue;
  /** @generated */
  final int     casFeatCode_gateFeatureValue;
  /** @generated */ 
  public String getGateFeatureValue(int addr) {
        if (featOkTst && casFeat_gateFeatureValue == null)
      jcas.throwFeatMissing("gateFeatureValue", "edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateFeature");
    return ll_cas.ll_getStringValue(addr, casFeatCode_gateFeatureValue);
  }
  /** @generated */    
  public void setGateFeatureValue(int addr, String v) {
        if (featOkTst && casFeat_gateFeatureValue == null)
      jcas.throwFeatMissing("gateFeatureValue", "edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateFeature");
    ll_cas.ll_setStringValue(addr, casFeatCode_gateFeatureValue, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public ODIE_GateFeature_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_gateAnnotationId = jcas.getRequiredFeatureDE(casType, "gateAnnotationId", "uima.cas.Integer", featOkTst);
    casFeatCode_gateAnnotationId  = (null == casFeat_gateAnnotationId) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_gateAnnotationId).getCode();

 
    casFeat_gateFeatureKey = jcas.getRequiredFeatureDE(casType, "gateFeatureKey", "uima.cas.String", featOkTst);
    casFeatCode_gateFeatureKey  = (null == casFeat_gateFeatureKey) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_gateFeatureKey).getCode();

 
    casFeat_gateFeatureValue = jcas.getRequiredFeatureDE(casType, "gateFeatureValue", "uima.cas.String", featOkTst);
    casFeatCode_gateFeatureValue  = (null == casFeat_gateFeatureValue) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_gateFeatureValue).getCode();

  }
}



    