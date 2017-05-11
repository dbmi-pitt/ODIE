package edu.pitt.dbmi.odie.ui.editors.document;

import org.eclipse.jface.text.source.AnnotationModel;

public class ODIEAnnotationModel extends AnnotationModel {

	boolean modelChangeFiringDisabled;

	public void disableModelChangeFiring() {
		modelChangeFiringDisabled = true;
	}

	public void enableModelChangeFiring() {
		modelChangeFiringDisabled = false;
	}

	@Override
	public void fireModelChanged() {
		if (!modelChangeFiringDisabled)
			super.fireModelChanged();
	}

}
