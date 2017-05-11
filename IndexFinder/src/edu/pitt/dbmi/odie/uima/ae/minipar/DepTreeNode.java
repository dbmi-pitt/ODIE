

/* First created by JCasGen Fri Jul 24 09:50:22 EDT 2009 */
package edu.pitt.dbmi.odie.uima.ae.minipar;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** Represents a node in the Minipar output representation.  DepTreeNodes are joined by POSTypes.
 * Updated by JCasGen Fri Jul 24 10:23:03 EDT 2009
 * XML source: C:/workspace/ws-uima-tutorial/IndexFinder/desc/dekanlin/ODIE_DekanLinMutualInfoAggregateTAE.xml
 * @generated */
public class DepTreeNode extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(DepTreeNode.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected DepTreeNode() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public DepTreeNode(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public DepTreeNode(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public DepTreeNode(JCas jcas, int begin, int end) {
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
  //* Feature: miniparId

  /** getter for miniparId - gets Minipar generated id.
   * @generated */
  public int getMiniparId() {
    if (DepTreeNode_Type.featOkTst && ((DepTreeNode_Type)jcasType).casFeat_miniparId == null)
      jcasType.jcas.throwFeatMissing("miniparId", "edu.pitt.dbmi.odie.uima.ae.minipar.DepTreeNode");
    return jcasType.ll_cas.ll_getIntValue(addr, ((DepTreeNode_Type)jcasType).casFeatCode_miniparId);}
    
  /** setter for miniparId - sets Minipar generated id. 
   * @generated */
  public void setMiniparId(int v) {
    if (DepTreeNode_Type.featOkTst && ((DepTreeNode_Type)jcasType).casFeat_miniparId == null)
      jcasType.jcas.throwFeatMissing("miniparId", "edu.pitt.dbmi.odie.uima.ae.minipar.DepTreeNode");
    jcasType.ll_cas.ll_setIntValue(addr, ((DepTreeNode_Type)jcasType).casFeatCode_miniparId, v);}    
   
    
  //*--------------*
  //* Feature: word

  /** getter for word - gets The word.
   * @generated */
  public String getWord() {
    if (DepTreeNode_Type.featOkTst && ((DepTreeNode_Type)jcasType).casFeat_word == null)
      jcasType.jcas.throwFeatMissing("word", "edu.pitt.dbmi.odie.uima.ae.minipar.DepTreeNode");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DepTreeNode_Type)jcasType).casFeatCode_word);}
    
  /** setter for word - sets The word. 
   * @generated */
  public void setWord(String v) {
    if (DepTreeNode_Type.featOkTst && ((DepTreeNode_Type)jcasType).casFeat_word == null)
      jcasType.jcas.throwFeatMissing("word", "edu.pitt.dbmi.odie.uima.ae.minipar.DepTreeNode");
    jcasType.ll_cas.ll_setStringValue(addr, ((DepTreeNode_Type)jcasType).casFeatCode_word, v);}    
  }

    