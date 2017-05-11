package edu.pitt.dbmi.odie.ui.editors.document;

import org.eclipse.jface.text.Document;

import edu.pitt.dbmi.odie.model.AnalysisDocument;

public class AnnotatedDocument extends Document {
	AnalysisDocument analysisDocument;

	public AnnotatedDocument(AnalysisDocument analysisDocument) {
		super();
		this.analysisDocument = analysisDocument;
		try {
			analysisDocument.getDocument().loadTextFromURI();
			this.set(analysisDocument.getDocument().getText());
		} catch (Exception e) {
			e.printStackTrace();
			this.set("Could not fetch document text:" + e.getMessage());
		}
	}

}
