package edu.pitt.dbmi.odie.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import edu.pitt.dbmi.odie.ui.views.AnalysesView;
import edu.pitt.dbmi.odie.ui.views.AnnotationTypeView;
import edu.pitt.dbmi.odie.ui.views.ConceptsView;
import edu.pitt.dbmi.odie.ui.views.CoreferenceView;
import edu.pitt.dbmi.odie.ui.views.DetailsView;
import edu.pitt.dbmi.odie.ui.views.DocumentsView;
import edu.pitt.dbmi.odie.ui.views.OntologiesView;

public class Perspective implements IPerspectiveFactory {

	public Perspective() {
	}

	public void createInitialLayout(IPageLayout layout) {
		IFolderLayout topLeft = layout.createFolder("topLeft",
				IPageLayout.LEFT, 0.25f, IPageLayout.ID_EDITOR_AREA);
		topLeft.addView(AnalysesView.ID);
		topLeft.addView(OntologiesView.ID);

		IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT,
				0.57f, IPageLayout.ID_EDITOR_AREA);

		right.addView(ConceptsView.ID);
		right.addView(AnnotationTypeView.ID);
		right.addView(CoreferenceView.ID);

		IFolderLayout bottom = layout.createFolder("bottom",
				IPageLayout.BOTTOM, 0.70f, IPageLayout.ID_EDITOR_AREA);
		bottom.addView(DetailsView.ID);
		// bottom.addView(AnnotationConceptsView.ID);
		// bottom.addView(ProcessingTraceView.ID);

		layout.addView(DocumentsView.ID, IPageLayout.BOTTOM, 0.25f, "topLeft");

	}

}
