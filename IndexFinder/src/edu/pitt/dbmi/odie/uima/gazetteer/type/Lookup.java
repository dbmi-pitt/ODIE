

/* First created by JCasGen Thu Sep 03 16:49:19 EDT 2009 */
package edu.pitt.dbmi.odie.uima.gazetteer.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** UIMA implementation equivalent to GATE's Lookup annotation.
 * Updated by JCasGen Tue Sep 08 17:38:27 EDT 2009
 * XML source: C:/workspace/ws-uima-tutorial/IndexFinder/desc/jape/ODIE_Jape_Tester_aeDescriptor.xml
 * @generated */
public class Lookup extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(Lookup.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Lookup() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Lookup(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Lookup(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Lookup(JCas jcas, int begin, int end) {
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
  //* Feature: majorType

  /** getter for majorType - gets Major category of this type.
   * @generated */
  public String getMajorType() {
    if (Lookup_Type.featOkTst && ((Lookup_Type)jcasType).casFeat_majorType == null)
      jcasType.jcas.throwFeatMissing("majorType", "edu.pitt.dbmi.odie.uima.gazetteer.type.Lookup");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Lookup_Type)jcasType).casFeatCode_majorType);}
    
  /** setter for majorType - sets Major category of this type. 
   * @generated */
  public void setMajorType(String v) {
    if (Lookup_Type.featOkTst && ((Lookup_Type)jcasType).casFeat_majorType == null)
      jcasType.jcas.throwFeatMissing("majorType", "edu.pitt.dbmi.odie.uima.gazetteer.type.Lookup");
    jcasType.ll_cas.ll_setStringValue(addr, ((Lookup_Type)jcasType).casFeatCode_majorType, v);}    
   
    
  //*--------------*
  //* Feature: minorType

  /** getter for minorType - gets Minor category of this type.
   * @generated */
  public String getMinorType() {
    if (Lookup_Type.featOkTst && ((Lookup_Type)jcasType).casFeat_minorType == null)
      jcasType.jcas.throwFeatMissing("minorType", "edu.pitt.dbmi.odie.uima.gazetteer.type.Lookup");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Lookup_Type)jcasType).casFeatCode_minorType);}
    
  /** setter for minorType - sets Minor category of this type. 
   * @generated */
  public void setMinorType(String v) {
    if (Lookup_Type.featOkTst && ((Lookup_Type)jcasType).casFeat_minorType == null)
      jcasType.jcas.throwFeatMissing("minorType", "edu.pitt.dbmi.odie.uima.gazetteer.type.Lookup");
    jcasType.ll_cas.ll_setStringValue(addr, ((Lookup_Type)jcasType).casFeatCode_minorType, v);}    
  }

    