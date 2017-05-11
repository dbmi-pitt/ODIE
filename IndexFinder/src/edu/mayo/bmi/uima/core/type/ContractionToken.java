

/* First created by JCasGen Fri Jul 24 10:02:29 EDT 2009 */
package edu.mayo.bmi.uima.core.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Thu Oct 29 16:39:50 EDT 2009
 * XML source: C:/Documents and Settings/chavgx/work/ODIE/workspace/IndexFinder/desc/odie_pipe/ODIE_AAE_NER_COREF.xml
 * @generated */
public class ContractionToken extends BaseToken {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(ContractionToken.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected ContractionToken() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public ContractionToken(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public ContractionToken(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public ContractionToken(JCas jcas, int begin, int end) {
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

    