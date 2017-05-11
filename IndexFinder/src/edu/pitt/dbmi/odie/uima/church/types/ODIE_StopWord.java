

/* First created by JCasGen Wed Jun 03 13:34:55 EDT 2009 */
package edu.pitt.dbmi.odie.uima.church.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** Word from a set of stop words
 * Updated by JCasGen Tue Aug 17 15:28:56 EDT 2010
 * XML source: C:/workspace/ws-uima-tutorial/IndexFinder/desc/combination/ODIE_AAE_CombinedKaiPipe2010Tester.xml
 * @generated */
public class ODIE_StopWord extends ODIE_Word {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(ODIE_StopWord.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected ODIE_StopWord() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public ODIE_StopWord(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public ODIE_StopWord(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public ODIE_StopWord(JCas jcas, int begin, int end) {
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
     
}

    