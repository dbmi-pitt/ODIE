package edu.pitt.dbmi.odie.ui.editors.document;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.IAnnotationAccessExtension;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;

import edu.pitt.dbmi.odie.ui.editors.annotations.EclipseAnnotationPeer;
import edu.pitt.dbmi.odie.uima.utils.UIMAUtils;

public class AnnotationAccess implements IAnnotationAccess,
		IAnnotationAccessExtension {

	Logger logger = Logger.getLogger(AnnotationAccess.class);
	TypeSystem typeSystem;
	private List typeList = new ArrayList();

	public AnnotationAccess(TypeSystem typeSystem) {
		super();
		this.typeSystem = typeSystem;
	}

	public Object getTypeObject(Annotation annotation) {
		if (annotation instanceof EclipseAnnotationPeer) {
			EclipseAnnotationPeer eap = (EclipseAnnotationPeer) annotation;
			return eap.getAnnotationFS().getType();
		}
		return annotation.getType();
	}


	@Override
	public int getLayer(Annotation annotation) {
		if (annotation instanceof EclipseAnnotationPeer) {
			EclipseAnnotationPeer eap = (EclipseAnnotationPeer) annotation;

			Type type = eap.getAnnotationFS().getType();
			if (typeList.contains(type) || hasParentSelected(type))
				return 1;
			else
				return -1;

			// Type annotationType = typeSystem.getType("uima.tcas.Annotation");
			// Type type = eap.getAnnotationFS().getType();
			// if(typeSystem.subsumes(annotationType, type)){
			// return getDepth(type);
			// // logger.info(type.getName() + " inherits " +
			// annotationType.getName());
			// // return 1;
			// }
			// else
			// logger.info(type.getName() + " does not inherit " +
			// annotationType.getName());
			//			

			// DisplayPreferences dp =
			// eap.getAnalysisDocument().getAnalysis().getAnalysisEngineMetadata().getDisplayPreferences();
			//			
			// return dp.getFilteredTypes().contains(annotation.getType())?0:-1;

		}
		return -1;
	}

	private boolean hasParentSelected(Type type) {
		return true;
		// for(Object stype:typeList){
		// if(typeSystem.subsumes((Type)stype,type))
		// return true;
		// }
		// return false;
	}

	private int getDepth(Type type) {
		Type t = type;
		int depth = 0;

		while (typeSystem.getParent(t) != null) {
			depth++;
			t = typeSystem.getParent(t);
		}
		// logger.info("Depth:" + depth);
		return depth;
	}

	@Override
	public Object[] getSupertypes(Object annotationType) {
		if (annotationType instanceof Type) {
			return UIMAUtils.getAncestors((Type) annotationType, typeSystem);
		} else if (annotationType instanceof String) {
			Type t = typeSystem.getType((String) annotationType);
			if (t != null) {
				return UIMAUtils.getAncestors(t, typeSystem);
			}
		}
		return new Object[] {};

	}

	@Override
	public String getTypeLabel(Annotation annotation) {
		return annotation.getType();
	}

	@Override
	public boolean isPaintable(Annotation annotation) {
		return false;
	}

	@Override
	public boolean isSubtype(Object annotationType, Object potentialSupertype) {
		if (annotationType instanceof Type
				&& potentialSupertype instanceof Type) {
			return typeSystem.subsumes((Type) potentialSupertype,
					(Type) annotationType);
		} else if (annotationType instanceof String
				&& potentialSupertype instanceof String) {
			Type aType = typeSystem.getType((String) annotationType);
			Type sType = typeSystem.getType((String) potentialSupertype);
			if (aType != null && sType != null)
				return typeSystem.subsumes(sType, aType);
		}
		return false;
	}

	@Override
	public void paint(Annotation annotation, GC gc, Canvas canvas,
			Rectangle bounds) {
	}

	public void setSelectedTypes(List typeList) {
		this.typeList = typeList;

	}

	@Deprecated
	@Override
	public boolean isMultiLine(Annotation annotation) {
		return true;
	}

	@Deprecated
	@Override
	public boolean isTemporary(Annotation annotation) {
		return !annotation.isPersistent();
	}

	@Deprecated
	@Override
	public Object getType(Annotation annotation) {
		return annotation.getType();
	}

}
