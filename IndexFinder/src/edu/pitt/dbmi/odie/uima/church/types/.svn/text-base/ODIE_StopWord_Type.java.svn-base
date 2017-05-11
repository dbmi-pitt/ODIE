
/* First created by JCasGen Wed Jun 03 13:34:55 EDT 2009 */
package edu.pitt.dbmi.odie.uima.church.types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;

/** Word from a set of stop words
 * Updated by JCasGen Tue Aug 17 15:28:56 EDT 2010
 * @generated */
public class ODIE_StopWord_Type extends ODIE_Word_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (ODIE_StopWord_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = ODIE_StopWord_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new ODIE_StopWord(addr, ODIE_StopWord_Type.this);
  			   ODIE_StopWord_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new ODIE_StopWord(addr, ODIE_StopWord_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = ODIE_StopWord.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.pitt.dbmi.odie.uima.church.types.ODIE_StopWord");



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public ODIE_StopWord_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

  }
}



    