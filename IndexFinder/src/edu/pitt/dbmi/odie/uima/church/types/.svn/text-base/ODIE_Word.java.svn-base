

/* First created by JCasGen Mon Jun 01 15:10:57 EDT 2009 */
package edu.pitt.dbmi.odie.uima.church.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** Annotates words in the document set.
 * Updated by JCasGen Mon Nov 08 13:14:55 EST 2010
 * XML source: C:/workspace/ws-odie/IndexFinder/desc/church/rdbms/ODIE_CASC_ChurchSuggestionGenerator.xml
 * @generated */
public class ODIE_Word extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(ODIE_Word.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected ODIE_Word() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public ODIE_Word(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public ODIE_Word(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public ODIE_Word(JCas jcas, int begin, int end) {
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
  //* Feature: wordText

  /** getter for wordText - gets Text beneath this Word annotation.
   * @generated */
  public String getWordText() {
    if (ODIE_Word_Type.featOkTst && ((ODIE_Word_Type)jcasType).casFeat_wordText == null)
      jcasType.jcas.throwFeatMissing("wordText", "edu.pitt.dbmi.odie.uima.church.types.ODIE_Word");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ODIE_Word_Type)jcasType).casFeatCode_wordText);}
    
  /** setter for wordText - sets Text beneath this Word annotation. 
   * @generated */
  public void setWordText(String v) {
    if (ODIE_Word_Type.featOkTst && ((ODIE_Word_Type)jcasType).casFeat_wordText == null)
      jcasType.jcas.throwFeatMissing("wordText", "edu.pitt.dbmi.odie.uima.church.types.ODIE_Word");
    jcasType.ll_cas.ll_setStringValue(addr, ((ODIE_Word_Type)jcasType).casFeatCode_wordText, v);}    
  }

    