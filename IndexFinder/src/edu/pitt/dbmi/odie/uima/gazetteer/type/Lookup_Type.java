
/* First created by JCasGen Thu Sep 03 16:49:19 EDT 2009 */
package edu.pitt.dbmi.odie.uima.gazetteer.type;

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

/** UIMA implementation equivalent to GATE's Lookup annotation.
 * Updated by JCasGen Tue Sep 08 17:38:27 EDT 2009
 * @generated */
public class Lookup_Type extends Annotation_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Lookup_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Lookup_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Lookup(addr, Lookup_Type.this);
  			   Lookup_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Lookup(addr, Lookup_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = Lookup.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.pitt.dbmi.odie.uima.gazetteer.type.Lookup");
 
  /** @generated */
  final Feature casFeat_majorType;
  /** @generated */
  final int     casFeatCode_majorType;
  /** @generated */ 
  public String getMajorType(int addr) {
        if (featOkTst && casFeat_majorType == null)
      jcas.throwFeatMissing("majorType", "edu.pitt.dbmi.odie.uima.gazetteer.type.Lookup");
    return ll_cas.ll_getStringValue(addr, casFeatCode_majorType);
  }
  /** @generated */    
  public void setMajorType(int addr, String v) {
        if (featOkTst && casFeat_majorType == null)
      jcas.throwFeatMissing("majorType", "edu.pitt.dbmi.odie.uima.gazetteer.type.Lookup");
    ll_cas.ll_setStringValue(addr, casFeatCode_majorType, v);}
    
  
 
  /** @generated */
  final Feature casFeat_minorType;
  /** @generated */
  final int     casFeatCode_minorType;
  /** @generated */ 
  public String getMinorType(int addr) {
        if (featOkTst && casFeat_minorType == null)
      jcas.throwFeatMissing("minorType", "edu.pitt.dbmi.odie.uima.gazetteer.type.Lookup");
    return ll_cas.ll_getStringValue(addr, casFeatCode_minorType);
  }
  /** @generated */    
  public void setMinorType(int addr, String v) {
        if (featOkTst && casFeat_minorType == null)
      jcas.throwFeatMissing("minorType", "edu.pitt.dbmi.odie.uima.gazetteer.type.Lookup");
    ll_cas.ll_setStringValue(addr, casFeatCode_minorType, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Lookup_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_majorType = jcas.getRequiredFeatureDE(casType, "majorType", "uima.cas.String", featOkTst);
    casFeatCode_majorType  = (null == casFeat_majorType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_majorType).getCode();

 
    casFeat_minorType = jcas.getRequiredFeatureDE(casType, "minorType", "uima.cas.String", featOkTst);
    casFeatCode_minorType  = (null == casFeat_minorType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_minorType).getCode();

  }
}



    