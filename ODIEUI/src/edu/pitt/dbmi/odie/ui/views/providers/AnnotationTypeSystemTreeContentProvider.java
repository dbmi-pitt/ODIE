package edu.pitt.dbmi.odie.ui.views.providers;

import java.util.Arrays;
import java.util.List;

import org.apache.uima.cas.Type;

import edu.pitt.dbmi.odie.ui.editors.annotations.AnnotationSubTypeRegistry;
import edu.pitt.dbmi.odie.ui.editors.annotations.IAnnotationSubType;

public class AnnotationTypeSystemTreeContentProvider extends
		PrunedTypeSystemTreeContentProvider {

	AnnotationSubTypeRegistry registry = AnnotationSubTypeRegistry
			.getInstance();

	public AnnotationTypeSystemTreeContentProvider(String rootTypeName) {
		super(rootTypeName);
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof IAnnotationSubType) {
			return typeSystem.getType(((IAnnotationSubType) element)
					.getParentTypeName());
		} else if (element instanceof Type)
			return super.getParent(element);
		else
			return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof IAnnotationSubType)
			return false;

		boolean hasChildren = super.hasChildren(element);

		if (!hasChildren) {
			if (element instanceof Type) {
				List userSubTypes = registry
						.getRegisteredSubTypes(((Type) element).getName());
				hasChildren = (userSubTypes != null && !userSubTypes.isEmpty());
				logger.debug(((Type) element).getShortName() + ":"
						+ hasChildren);
			}
		}

		return hasChildren;
	}

	@Override
	public Object[] getChildren(Object parentElement) {

		if (parentElement instanceof Type
				&& !(parentElement instanceof IAnnotationSubType)) {
			Object[] subTypeArr = super.getChildren(parentElement);
			if (subTypeArr == null)
				return new Object[] {};

			List<Object> subTypeList = Arrays.asList(subTypeArr);

			List userSubTypes = registry
					.getRegisteredSubTypes(((Type) parentElement).getName());

			if (subTypeList.isEmpty() && userSubTypes != null)
				subTypeList = userSubTypes;
			else {
				if (userSubTypes != null && !userSubTypes.isEmpty())
					subTypeList.addAll(userSubTypes);
			}

			return subTypeList.toArray();
		}

		return new Object[] {};

	}
}
