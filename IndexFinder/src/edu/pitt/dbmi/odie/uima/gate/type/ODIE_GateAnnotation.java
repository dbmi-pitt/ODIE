

/* First created by JCasGen Wed Sep 09 12:36:40 EDT 2009 */
package edu.pitt.dbmi.odie.uima.gate.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.TOP_Type;
import org.apache.uima.jcas.tcas.Annotation;


/** Used to store transient GATE annotations in the CAS
between calls to Wrapped GATE PRs
 * Updated by JCasGen Mon Nov 08 13:14:55 EST 2010
 * XML source: C:/workspace/ws-odie/IndexFinder/desc/church/rdbms/ODIE_CASC_ChurchSuggestionGenerator.xml
 * @generated */
public class ODIE_GateAnnotation extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(ODIE_GateAnnotation.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected ODIE_GateAnnotation() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public ODIE_GateAnnotation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public ODIE_GateAnnotation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public ODIE_GateAnnotation(JCas jcas, int begin, int end) {
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
    if (ODIE_GateAnnotation_Type.featOkTst && ((ODIE_GateAnnotation_Type)jcasType).casFeat_gateAnnotationId == null)
      jcasType.jcas.throwFeatMissing("gateAnnotationId", "edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateAnnotation");
    return jcasType.ll_cas.ll_getIntValue(addr, ((ODIE_GateAnnotation_Type)jcasType).casFeatCode_gateAnnotationId);}
    
  /** setter for gateAnnotationId - sets  
   * @generated */
  public void setGateAnnotationId(int v) {
    if (ODIE_GateAnnotation_Type.featOkTst && ((ODIE_GateAnnotation_Type)jcasType).casFeat_gateAnnotationId == null)
      jcasType.jcas.throwFeatMissing("gateAnnotationId", "edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateAnnotation");
    jcasType.ll_cas.ll_setIntValue(addr, ((ODIE_GateAnnotation_Type)jcasType).casFeatCode_gateAnnotationId, v);}    
   
    
  //*--------------*
  //* Feature: gateAnnotationType

  /** getter for gateAnnotationType - gets Annotation type from GATE
   * @generated */
  public String getGateAnnotationType() {
    if (ODIE_GateAnnotation_Type.featOkTst && ((ODIE_GateAnnotation_Type)jcasType).casFeat_gateAnnotationType == null)
      jcasType.jcas.throwFeatMissing("gateAnnotationType", "edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ODIE_GateAnnotation_Type)jcasType).casFeatCode_gateAnnotationType);}
    
  /** setter for gateAnnotationType - sets Annotation type from GATE 
   * @generated */
  public void setGateAnnotationType(String v) {
    if (ODIE_GateAnnotation_Type.featOkTst && ((ODIE_GateAnnotation_Type)jcasType).casFeat_gateAnnotationType == null)
      jcasType.jcas.throwFeatMissing("gateAnnotationType", "edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((ODIE_GateAnnotation_Type)jcasType).casFeatCode_gateAnnotationType, v);}    
  }

    