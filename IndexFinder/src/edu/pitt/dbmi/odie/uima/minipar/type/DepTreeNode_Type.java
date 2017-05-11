
/* First created by JCasGen Wed Jul 22 10:16:47 EDT 2009 */
package edu.pitt.dbmi.odie.uima.minipar.type;

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

/** 
 * Updated by JCasGen Mon Oct 19 13:24:13 EDT 2009
 * @generated */
public class DepTreeNode_Type extends Annotation_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (DepTreeNode_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = DepTreeNode_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new DepTreeNode(addr, DepTreeNode_Type.this);
  			   DepTreeNode_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new DepTreeNode(addr, DepTreeNode_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = DepTreeNode.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.pitt.dbmi.odie.uima.minipar.type.DepTreeNode");
 
  /** @generated */
  final Feature casFeat_miniparId;
  /** @generated */
  final int     casFeatCode_miniparId;
  /** @generated */ 
  public int getMiniparId(int addr) {
        if (featOkTst && casFeat_miniparId == null)
      jcas.throwFeatMissing("miniparId", "edu.pitt.dbmi.odie.uima.minipar.type.DepTreeNode");
    return ll_cas.ll_getIntValue(addr, casFeatCode_miniparId);
  }
  /** @generated */    
  public void setMiniparId(int addr, int v) {
        if (featOkTst && casFeat_miniparId == null)
      jcas.throwFeatMissing("miniparId", "edu.pitt.dbmi.odie.uima.minipar.type.DepTreeNode");
    ll_cas.ll_setIntValue(addr, casFeatCode_miniparId, v);}
    
  
 
  /** @generated */
  final Feature casFeat_word;
  /** @generated */
  final int     casFeatCode_word;
  /** @generated */ 
  public String getWord(int addr) {
        if (featOkTst && casFeat_word == null)
      jcas.throwFeatMissing("word", "edu.pitt.dbmi.odie.uima.minipar.type.DepTreeNode");
    return ll_cas.ll_getStringValue(addr, casFeatCode_word);
  }
  /** @generated */    
  public void setWord(int addr, String v) {
        if (featOkTst && casFeat_word == null)
      jcas.throwFeatMissing("word", "edu.pitt.dbmi.odie.uima.minipar.type.DepTreeNode");
    ll_cas.ll_setStringValue(addr, casFeatCode_word, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public DepTreeNode_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_miniparId = jcas.getRequiredFeatureDE(casType, "miniparId", "uima.cas.Integer", featOkTst);
    casFeatCode_miniparId  = (null == casFeat_miniparId) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_miniparId).getCode();

 
    casFeat_word = jcas.getRequiredFeatureDE(casType, "word", "uima.cas.String", featOkTst);
    casFeatCode_word  = (null == casFeat_word) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_word).getCode();

  }
}



    