
/* First created by JCasGen Mon Jun 01 15:10:57 EDT 2009 */
package edu.pitt.dbmi.odie.uima.church.types;

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

/** Annotates words in the document set.
 * Updated by JCasGen Mon Nov 08 13:14:55 EST 2010
 * @generated */
public class ODIE_Word_Type extends Annotation_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (ODIE_Word_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = ODIE_Word_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new ODIE_Word(addr, ODIE_Word_Type.this);
  			   ODIE_Word_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new ODIE_Word(addr, ODIE_Word_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = ODIE_Word.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.pitt.dbmi.odie.uima.church.types.ODIE_Word");
 
  /** @generated */
  final Feature casFeat_wordText;
  /** @generated */
  final int     casFeatCode_wordText;
  /** @generated */ 
  public String getWordText(int addr) {
        if (featOkTst && casFeat_wordText == null)
      jcas.throwFeatMissing("wordText", "edu.pitt.dbmi.odie.uima.church.types.ODIE_Word");
    return ll_cas.ll_getStringValue(addr, casFeatCode_wordText);
  }
  /** @generated */    
  public void setWordText(int addr, String v) {
        if (featOkTst && casFeat_wordText == null)
      jcas.throwFeatMissing("wordText", "edu.pitt.dbmi.odie.uima.church.types.ODIE_Word");
    ll_cas.ll_setStringValue(addr, casFeatCode_wordText, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public ODIE_Word_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_wordText = jcas.getRequiredFeatureDE(casType, "wordText", "uima.cas.String", featOkTst);
    casFeatCode_wordText  = (null == casFeat_wordText) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_wordText).getCode();

  }
}



    