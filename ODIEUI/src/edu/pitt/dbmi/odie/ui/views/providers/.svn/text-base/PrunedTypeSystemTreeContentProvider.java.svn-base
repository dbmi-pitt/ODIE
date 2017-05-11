package edu.pitt.dbmi.odie.ui.views.providers;

import org.apache.uima.cas.TypeSystem;

public class PrunedTypeSystemTreeContentProvider extends
		TypeSystemTreeContentProvider {

	String rootTypeName;

	public String getRootTypeName() {
		return rootTypeName;
	}

	public void setRootTypeName(String rootTypeName) {
		this.rootTypeName = rootTypeName;
	}

	public PrunedTypeSystemTreeContentProvider(String rootTypeName) {
		super();
		this.rootTypeName = rootTypeName;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof TypeSystem) {
			this.typeSystem = (TypeSystem) inputElement;
			if (rootTypeName == null)
				return new Object[] { typeSystem.getTopType() };
			else {
				return new Object[] { typeSystem.getType(rootTypeName) };
			}
		} else {
			logger.error("Input not a TypeSystem");
			return new Object[] {};
		}
	}
}
