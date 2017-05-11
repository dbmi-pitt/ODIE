package edu.pitt.dbmi.odie.uima.gate.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.tcas.DocumentAnnotation;

import edu.mayo.bmi.uima.core.type.BaseToken;
import edu.pitt.dbmi.odie.uima.gate.resource.ODIE_GateResource;
import edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateAnnotation;
import edu.pitt.dbmi.odie.uima.gate.type.ODIE_GateFeature;
import edu.pitt.dbmi.odie.uima.util.ODIE_UimaUtils;
import gate.Document;
import gate.FeatureMap;
import gate.util.InvalidOffsetException;

public class ODIE_Uima2GateXFerrer {
	
	/**
	 * Field logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ODIE_Uima2GateXFerrer.class);
	
	private ODIE_GateResource factoryDelegate;

	private final HashMap<Integer, Annotation> annotsToRemove = new HashMap<Integer, Annotation>();

	private final List<String> reservedAccessors = new ArrayList<String>();

	private JCas aJCas;
	private Document gateDocument;

	public ODIE_Uima2GateXFerrer() {
		;
	}

	@SuppressWarnings("unchecked")
	public void tranferJCasToDocument(JCas aJCas, Document gateDocument) {

		try {

			if (!this.reservedAccessors.isEmpty()) {
				getReservedAccessors(aJCas, this.reservedAccessors);
				logger.debug("ReservedAccessors: " + stringifyList(this.reservedAccessors)) ;
			}

			this.aJCas = aJCas;
			this.gateDocument = gateDocument;

			FSIndex uimaAnnotIndex = this.aJCas
					.getAnnotationIndex(Annotation.type);

			Iterator<Annotation> uimaAnnotIter = uimaAnnotIndex.iterator();

			while (uimaAnnotIter.hasNext()) {
				Annotation uimaAnnot = uimaAnnotIter.next();
				if (uimaAnnot instanceof ODIE_GateFeature) {
					;
				} else if (uimaAnnot instanceof ODIE_GateAnnotation) {
					processIncomingGateAnnotation((ODIE_GateAnnotation) uimaAnnot);
				} else {
					processIncomingUimaAnnotation(uimaAnnot);
				}

			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InvalidOffsetException e) {
			e.printStackTrace();
		}

	}

	private void processIncomingGateAnnotation(ODIE_GateAnnotation uimaAnnot)
			throws InvalidOffsetException {
		FeatureMap gateFeatureMap = fetchFeatureMapForAnnotation(uimaAnnot) ;
		int sPos = uimaAnnot.getBegin();
		int ePos = uimaAnnot.getEnd();
		String gateAnnotationType = uimaAnnot.getGateAnnotationType();
		int gateAnnotId = gateDocument.getAnnotations().add(new Long(sPos),
				new Long(ePos), gateAnnotationType, gateFeatureMap);
		annotsToRemove.put(new Integer(gateAnnotId), uimaAnnot);
	}
	
	private FeatureMap fetchFeatureMapForAnnotation(Annotation uimaAnnot) {
		FeatureMap result = this.factoryDelegate.newFeatureMap() ;
		if (uimaAnnot instanceof ODIE_GateAnnotation) {
			HashMap<String, String> featureMap = ODIE_UimaUtils.fetchFeatureMapForAnnotation((ODIE_GateAnnotation) uimaAnnot) ;
			for (Iterator<String> keysIterator = featureMap.keySet().iterator() ; keysIterator.hasNext();) {
				String key = keysIterator.next();
				String value = featureMap.get(key) ;
				result.put(key, value) ;
			}
		}
		return result ;
	}
	
	private void processIncomingUimaAnnotation(Annotation uimaAnnot)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, InvalidOffsetException {
		Class<?> uimaAnnotCls = uimaAnnot.getClass();
		String uimaAnnotClsName = uimaAnnotCls.getSimpleName();
		FeatureMap features = getFeatureMapForUimaAnnotation(uimaAnnot,
				reservedAccessors);
		int sPos = uimaAnnot.getBegin();
		int ePos = uimaAnnot.getEnd();
		if (BaseToken.class.isAssignableFrom(uimaAnnotCls)) {
			uimaAnnotClsName = "Token";
			features.put("string", uimaAnnot.getCoveredText());
		}
		int gateAnnotId = gateDocument.getAnnotations().add(new Long(sPos),
				new Long(ePos), uimaAnnotClsName, features);
		annotsToRemove.put(new Integer(gateAnnotId), uimaAnnot);
	}
	
	private ODIE_GateAnnotation duplicateAnnotation(Annotation srcAnnotation) {
		// Add a new ODIE_GateAnnotation to JCas for Token thus preserving the original
		ODIE_GateAnnotation duplicateTokenAnnot = new ODIE_GateAnnotation(this.aJCas) ;
		duplicateTokenAnnot.setBegin(srcAnnotation.getBegin()) ;
		duplicateTokenAnnot.setEnd(srcAnnotation.getEnd()) ;
		duplicateTokenAnnot.setGateAnnotationId(0) ;
		duplicateTokenAnnot.setGateAnnotationType("Token") ;
		duplicateTokenAnnot.addToIndexes(this.aJCas) ;
		return duplicateTokenAnnot ;
	}
	
	private FeatureMap getFeatureMapForUimaAnnotation(Annotation uimaAnnot,
			List<String> reservedAccessors) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		FeatureMap features = factoryDelegate.newFeatureMap();
		Class<?> uimaAnnotCls = uimaAnnot.getClass();
		Method[] methods = uimaAnnotCls.getMethods();
		for (int mdx = 0; mdx < methods.length; mdx++) {
			Method method = methods[mdx];
			if (isAccessorMethod(method)) {
				// only consider getters with null parameters
				String featureKey = method.getName();
				featureKey = method.getName().replaceAll("^get", "");
				featureKey = featureKey.substring(0, 1).toLowerCase()
						+ featureKey.substring(1, featureKey.length());
				if (!reservedAccessors.contains(featureKey)) {
					Object[] emptyArgs = new Object[0];
					Object featureValue = method.invoke(uimaAnnot, emptyArgs);
					features.put(featureKey, String.valueOf(featureValue));
				}
			}
		}
		return features;
	}
	
	@SuppressWarnings("unchecked")
	private void getReservedAccessors(JCas aJCas, List<String> reservedAccessors) {

		try {
			FSIndex uimaAnnotIndex = aJCas
					.getAnnotationIndex(DocumentAnnotation.type);
			Iterator<Annotation> uimaAnnotIter = uimaAnnotIndex.iterator();
			while (uimaAnnotIter.hasNext()) {
				FeatureMap features = getFeatureMapForUimaAnnotation(
						uimaAnnotIter.next(), new ArrayList<String>());
				for (Iterator<Object> keyIterator = features.keySet()
						.iterator(); keyIterator.hasNext();) {
					String key = (String) keyIterator.next();
					reservedAccessors.add(key);
				}
				break;
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}
	
	private String stringifyList(List<String> listOfStrings) {
		StringBuffer sb = new StringBuffer();
		sb.append("\nList Entries");
		for (String listEntry : listOfStrings) {
			sb.append(listEntry + "\n");
		}
		sb.append("\n");
		return sb.toString();
	}
	
	private boolean isAccessorMethod(Method method) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		Class<?> returnType = method.getReturnType();
		boolean result = method.getName().startsWith("get");
		result &= (parameterTypes == null || parameterTypes.length == 0);
		result &= returnType != null;
		result &= returnType.getSimpleName().equals("String") ;
		return result;
	}
	
	public ODIE_GateResource getFactoryDelegate() {
		return factoryDelegate;
	}

	public void setFactoryDelegate(ODIE_GateResource factoryDelegate) {
		this.factoryDelegate = factoryDelegate;
	}

	public HashMap<Integer, Annotation> getAnnotsToRemove() {
		return this.annotsToRemove;
	}

}
