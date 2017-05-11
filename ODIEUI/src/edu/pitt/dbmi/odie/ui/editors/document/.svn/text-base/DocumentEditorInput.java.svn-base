package edu.pitt.dbmi.odie.ui.editors.document;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import edu.pitt.dbmi.odie.model.AnalysisDocument;

public class DocumentEditorInput implements IEditorInput {

	private AnalysisDocument analysisDocument;

	public DocumentEditorInput(AnalysisDocument obj) {
		analysisDocument = obj;
	}

	@Override
	public boolean exists() {
		return analysisDocument != null;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		if (exists())
			return analysisDocument.getAnalysis().getName() + ":"
					+ analysisDocument.getDocument().getName();
		else
			return "null document";
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return getName();
	}

	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof DocumentEditorInput) {
			return this.analysisDocument
					.equals(((DocumentEditorInput) o).analysisDocument);
		}
		return super.equals(o);
	}

	public AnalysisDocument getAnalysisDocument() {
		return analysisDocument;
	}
}
