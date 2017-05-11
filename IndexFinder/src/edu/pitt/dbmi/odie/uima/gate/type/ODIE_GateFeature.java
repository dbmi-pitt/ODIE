

/* First created by JCasGen Wed Sep 09 15:30:24 EDT 2009 */
package edu.pitt.dbmi.odie.uima.gate.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Nov 08 13:14:55 EST 2010
 * XML source: C:/workspace/ws-odie/IndexFinder/desc/church/rdbms/ODIE_CASC_ChurchSuggestionGenerator.xml
 * @generated */
public class ODIE_GateFeature extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(ODIE_GateFeature.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected ODIE_GateFeature() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public ODIE_GateFeature(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public ODIE_GateFeature(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public ODIE_GateFeature(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {}
     
 
    
  //*--------------*
  //* Feature: gateAnnotationId

  /** getter for gateAnnotationId - gets 
   * @generated */
  public int getGateAnnotationId() {
    if (ODIE_GateFeature_Type.featOkTst && ((ODIE_GateFeature_Type)jcasType).casFeat_gateAnnotationId == null)
      jcasType.jcas.throwFeatMissing("gateAnnotationId", "edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateFeature");
    return jcasType.ll_cas.ll_getIntValue(addr, ((ODIE_GateFeature_Type)jcasType).casFeatCode_gateAnnotationId);}
    
  /** setter for gateAnnotationId - sets  
   * @generated */
  public void setGateAnnotationId(int v) {
    if (ODIE_GateFeature_Type.featOkTst && ((ODIE_GateFeature_Type)jcasType).casFeat_gateAnnotationId == null)
      jcasType.jcas.throwFeatMissing("gateAnnotationId", "edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateFeature");
    jcasType.ll_cas.ll_setIntValue(addr, ((ODIE_GateFeature_Type)jcasType).casFeatCode_gateAnnotationId, v);}    
   
    
  //*--------------*
  //* Feature: gateFeatureKey

  /** getter for gateFeatureKey - gets 
   * @generated */
  public String getGateFeatureKey() {
    if (ODIE_GateFeature_Type.featOkTst && ((ODIE_GateFeature_Type)jcasType).casFeat_gateFeatureKey == null)
      jcasType.jcas.throwFeatMissing("gateFeatureKey", "edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateFeature");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ODIE_GateFeature_Type)jcasType).casFeatCode_gateFeatureKey);}
    
  /** setter for gateFeatureKey - sets  
   * @generated */
  public void setGateFeatureKey(String v) {
    if (ODIE_GateFeature_Type.featOkTst && ((ODIE_GateFeature_Type)jcasType).casFeat_gateFeatureKey == null)
      jcasType.jcas.throwFeatMissing("gateFeatureKey", "edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateFeature");
    jcasType.ll_cas.ll_setStringValue(addr, ((ODIE_GateFeature_Type)jcasType).casFeatCode_gateFeatureKey, v);}    
   
    
  //*--------------*
  //* Feature: gateFeatureValue

  /** getter for gateFeatureValue - gets 
   * @generated */
  public String getGateFeatureValue() {
    if (ODIE_GateFeature_Type.featOkTst && ((ODIE_GateFeature_Type)jcasType).casFeat_gateFeatureValue == null)
      jcasType.jcas.throwFeatMissing("gateFeatureValue", "edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateFeature");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ODIE_GateFeature_Type)jcasType).casFeatCode_gateFeatureValue);}
    
  /** setter for gateFeatureValue - sets  
   * @generated */
  public void setGateFeatureValue(String v) {
    if (ODIE_GateFeature_Type.featOkTst && ((ODIE_GateFeature_Type)jcasType).casFeat_gateFeatureValue == null)
      jcasType.jcas.throwFeatMissing("gateFeatureValue", "edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateFeature");
    jcasType.ll_cas.ll_setStringValue(addr, ((ODIE_GateFeature_Type)jcasType).casFeatCode_gateFeatureValue, v);}    
  }

    