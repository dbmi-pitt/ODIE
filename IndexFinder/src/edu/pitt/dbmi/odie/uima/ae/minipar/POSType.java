

/* First created by JCasGen Fri Jul 24 09:50:22 EDT 2009 */
package edu.pitt.dbmi.odie.uima.ae.minipar;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** Holds the Minipar derived POS tag that is related to
other tags through its ids
 * Updated by JCasGen Fri Jul 24 10:23:03 EDT 2009
 * XML source: C:/workspace/ws-uima-tutorial/IndexFinder/desc/dekanlin/ODIE_DekanLinMutualInfoAggregateTAE.xml
 * @generated */
public class POSType extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(POSType.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected POSType() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public POSType(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public POSType(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public POSType(JCas jcas, int begin, int end) {
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
  //* Feature: tagName

  /** getter for tagName - gets POSTag including det, obj, pred, s and others.
   * @generated */
  public String getTagName() {
    if (POSType_Type.featOkTst && ((POSType_Type)jcasType).casFeat_tagName == null)
      jcasType.jcas.throwFeatMissing("tagName", "edu.pitt.dbmi.odie.uima.ae.minipar.POSType");
    return jcasType.ll_cas.ll_getStringValue(addr, ((POSType_Type)jcasType).casFeatCode_tagName);}
    
  /** setter for tagName - sets POSTag including det, obj, pred, s and others. 
   * @generated */
  public void setTagName(String v) {
    if (POSType_Type.featOkTst && ((POSType_Type)jcasType).casFeat_tagName == null)
      jcasType.jcas.throwFeatMissing("tagName", "edu.pitt.dbmi.odie.uima.ae.minipar.POSType");
    jcasType.ll_cas.ll_setStringValue(addr, ((POSType_Type)jcasType).casFeatCode_tagName, v);}    
   
    
  //*--------------*
  //* Feature: headId

  /** getter for headId - gets Id for the head word
   * @generated */
  public int getHeadId() {
    if (POSType_Type.featOkTst && ((POSType_Type)jcasType).casFeat_headId == null)
      jcasType.jcas.throwFeatMissing("headId", "edu.pitt.dbmi.odie.uima.ae.minipar.POSType");
    return jcasType.ll_cas.ll_getIntValue(addr, ((POSType_Type)jcasType).casFeatCode_headId);}
    
  /** setter for headId - sets Id for the head word 
   * @generated */
  public void setHeadId(int v) {
    if (POSType_Type.featOkTst && ((POSType_Type)jcasType).casFeat_headId == null)
      jcasType.jcas.throwFeatMissing("headId", "edu.pitt.dbmi.odie.uima.ae.minipar.POSType");
    jcasType.ll_cas.ll_setIntValue(addr, ((POSType_Type)jcasType).casFeatCode_headId, v);}    
   
    
  //*--------------*
  //* Feature: headWord

  /** getter for headWord - gets The head word.
   * @generated */
  public String getHeadWord() {
    if (POSType_Type.featOkTst && ((POSType_Type)jcasType).casFeat_headWord == null)
      jcasType.jcas.throwFeatMissing("headWord", "edu.pitt.dbmi.odie.uima.ae.minipar.POSType");
    return jcasType.ll_cas.ll_getStringValue(addr, ((POSType_Type)jcasType).casFeatCode_headWord);}
    
  /** setter for headWord - sets The head word. 
   * @generated */
  public void setHeadWord(String v) {
    if (POSType_Type.featOkTst && ((POSType_Type)jcasType).casFeat_headWord == null)
      jcasType.jcas.throwFeatMissing("headWord", "edu.pitt.dbmi.odie.uima.ae.minipar.POSType");
    jcasType.ll_cas.ll_setStringValue(addr, ((POSType_Type)jcasType).casFeatCode_headWord, v);}    
   
    
  //*--------------*
  //* Feature: childId

  /** getter for childId - gets Id of child word
   * @generated */
  public int getChildId() {
    if (POSType_Type.featOkTst && ((POSType_Type)jcasType).casFeat_childId == null)
      jcasType.jcas.throwFeatMissing("childId", "edu.pitt.dbmi.odie.uima.ae.minipar.POSType");
    return jcasType.ll_cas.ll_getIntValue(addr, ((POSType_Type)jcasType).casFeatCode_childId);}
    
  /** setter for childId - sets Id of child word 
   * @generated */
  public void setChildId(int v) {
    if (POSType_Type.featOkTst && ((POSType_Type)jcasType).casFeat_childId == null)
      jcasType.jcas.throwFeatMissing("childId", "edu.pitt.dbmi.odie.uima.ae.minipar.POSType");
    jcasType.ll_cas.ll_setIntValue(addr, ((POSType_Type)jcasType).casFeatCode_childId, v);}    
   
    
  //*--------------*
  //* Feature: childWord

  /** getter for childWord - gets The child word.
   * @generated */
  public String getChildWord() {
    if (POSType_Type.featOkTst && ((POSType_Type)jcasType).casFeat_childWord == null)
      jcasType.jcas.throwFeatMissing("childWord", "edu.pitt.dbmi.odie.uima.ae.minipar.POSType");
    return jcasType.ll_cas.ll_getStringValue(addr, ((POSType_Type)jcasType).casFeatCode_childWord);}
    
  /** setter for childWord - sets The child word. 
   * @generated */
  public void setChildWord(String v) {
    if (POSType_Type.featOkTst && ((POSType_Type)jcasType).casFeat_childWord == null)
      jcasType.jcas.throwFeatMissing("childWord", "edu.pitt.dbmi.odie.uima.ae.minipar.POSType");
    jcasType.ll_cas.ll_setStringValue(addr, ((POSType_Type)jcasType).casFeatCode_childWord, v);}    
  }

    