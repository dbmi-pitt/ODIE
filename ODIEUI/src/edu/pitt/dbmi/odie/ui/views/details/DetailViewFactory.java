package edu.pitt.dbmi.odie.ui.views.details;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.uima.cas.impl.AnnotationImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;

import edu.pitt.dbmi.odie.ui.views.details.providers.AnnotationInstanceContentProvider;
import edu.pitt.dbmi.odie.ui.views.details.providers.DefaultDetailsContentProvider;
import edu.pitt.dbmi.odie.ui.views.details.providers.DefaultDetailsLabelProvider;
import edu.pitt.dbmi.odie.ui.views.details.providers.FeatureValueLabelProvider;
import edu.pitt.dbmi.odie.ui.views.details.providers.PClassDetailsContentProvider;
import edu.pitt.dbmi.odie.ui.views.details.providers.TypeContentProvider;
import edu.pitt.dbmi.odie.ui.views.details.providers.TypeLabelProvider;
import edu.pitt.ontology.protege.PClass;

public class DetailViewFactory {
	Logger logger = Logger.getLogger(DetailViewFactory.class);
	// public static Comparator<Class> classHierarchyComparator = new
	// Comparator<Class>() {
	//
	// @Override
	// public int compare(Class o1, Class o2) {
	// if(o1.isAssignableFrom(o2)){
	// return 1;
	// }
	// else if(o2.isAssignableFrom(o1)){
	// return -1;
	// }
	// else
	// return 0;
	// }
	//		
	// };

	// public static void main(String args[]){
	// classHierarchyComparator.compare(Type.class, NamedEntity.class);
	// }

	private HashMap<Class, IContentProvider> contentProviders = new HashMap<Class, IContentProvider>();
	private HashMap<Class, IBaseLabelProvider> labelProviders = new HashMap<Class, IBaseLabelProvider>();

	private static DetailViewFactory singleton;

	public static DetailViewFactory getInstance() {
		if (singleton == null) {
			singleton = new DetailViewFactory();
		}
		return singleton;
	}

	public DetailViewFactory() {
		super();

		contentProviders.put(TypeImpl.class, new TypeContentProvider());
		labelProviders.put(TypeImpl.class, new TypeLabelProvider());

		AnnotationInstanceContentProvider aicp = new AnnotationInstanceContentProvider();
		FeatureValueLabelProvider fvl = new FeatureValueLabelProvider();
		contentProviders.put(Annotation.class, aicp);
		labelProviders.put(Annotation.class, fvl);

		contentProviders.put(AnnotationImpl.class, aicp);
		labelProviders.put(AnnotationImpl.class, fvl);

		contentProviders.put(PClass.class, new PClassDetailsContentProvider());
		labelProviders.put(PClass.class, new FeatureValueLabelProvider());

		contentProviders.put(Object.class, new DefaultDetailsContentProvider());
		labelProviders.put(Object.class, new DefaultDetailsLabelProvider());

	}

	public IContentProvider getContentProvider(Class c) {
		IContentProvider cp = contentProviders.get(c);

		if (cp == null)
			return getContentProvider(c.getSuperclass());

		return cp;
	}

	public IBaseLabelProvider getLabelProvider(Class c) {
		IBaseLabelProvider lp = labelProviders.get(c);

		if (lp == null)
			return getLabelProvider(c.getSuperclass());

		return lp;
	}
}
