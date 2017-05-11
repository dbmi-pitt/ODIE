package edu.pitt.dbmi.odie.ui;

import org.apache.log4j.Logger;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.ui.editors.analysis.AnalysisEditor;
import edu.pitt.dbmi.odie.ui.editors.analysis.AnalysisEditorInput;
import edu.pitt.dbmi.odie.ui.editors.document.AnnotatedDocumentEditor;
import edu.pitt.dbmi.odie.ui.editors.document.DocumentEditorInput;
import edu.pitt.dbmi.odie.ui.editors.ontology.OntologyEditor;
import edu.pitt.dbmi.odie.ui.views.AnnotationTypeView;
import edu.pitt.dbmi.odie.ui.views.ConceptsView;
import edu.pitt.dbmi.odie.ui.views.CoreferenceView;
import edu.pitt.dbmi.odie.ui.views.DetailsView;
import edu.pitt.dbmi.odie.ui.views.DocumentsView;
import edu.pitt.dbmi.odie.ui.views.StackedViewPart;
import edu.pitt.dbmi.odie.ui.views.SuggestionsView;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class View2EditorLinker implements IPartListener {
	Logger logger = Logger.getLogger(View2EditorLinker.class);

	private void hideViewsUnusedForAnnotatedDocumentEditor(IWorkbenchPart part) {
	}

	private void hideViewsUnusedForAnalysisEditor(IWorkbenchPart part) {
		hideView(AnnotationTypeView.ID);
		hideView(DetailsView.ID);
		hideView(SuggestionsView.ID);
		hideView(CoreferenceView.ID);
	}

	private void hideViewsUnusedForOntologyEditor(IWorkbenchPart part) {
		hideView(AnnotationTypeView.ID);
		hideView(DetailsView.ID);
		hideView(DocumentsView.ID);
		hideView(ConceptsView.ID);
		hideView(SuggestionsView.ID);
		hideView(CoreferenceView.ID);
	}

	private void hideView(String viewId) {
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		IViewPart part = page.findView(viewId);
		if (part != null) {
			if (part instanceof StackedViewPart)
				((StackedViewPart) part).showDefaultControl();
		}
	}

	private void showViewsForAnnotatedDocumentEditor(IWorkbenchPart part) {
		showView(AnnotationTypeView.ID);
		showView(DocumentsView.ID);
		showView(DetailsView.ID);

		if (GeneralUtils.isCOREF(((DocumentEditorInput) ((IEditorPart) part)
				.getEditorInput()).getAnalysisDocument().getAnalysis()))
			showView(CoreferenceView.ID);
		else
			hideView(CoreferenceView.ID);

//		if (!GeneralUtils.isOther(((DocumentEditorInput) ((IEditorPart) part)
//				.getEditorInput()).getAnalysisDocument().getAnalysis()))
//			showView(ConceptsView.ID);
		Analysis a = ((DocumentEditorInput)((IEditorPart) part)
				.getEditorInput()).getAnalysisDocument().getAnalysis();
		try {
			GeneralUtils.loadAnalysisEngine(a);
			if (GeneralUtils.generatesNamedEntityAnnotationType(a.getAnalysisEngine().getAnalysisEngineMetaData()))
				showView(ConceptsView.ID);
			else
				hideView(ConceptsView.ID);

		} catch (Exception e) {
			e.printStackTrace();
			hideView(ConceptsView.ID);
		}

		if (GeneralUtils.isOE(((DocumentEditorInput) ((IEditorPart) part)
				.getEditorInput()).getAnalysisDocument().getAnalysis()))
			showView(SuggestionsView.ID);
		else
			hideView(SuggestionsView.ID);

	}

	private void showViewsForAnalysisEditor(IWorkbenchPart part) {
		showView(DocumentsView.ID);

		if(Activator.getDefault().getConfiguration().isNoCharts())
			return;
		
		try {
			Analysis a = ((AnalysisEditorInput) ((IEditorPart) part).getEditorInput()).getAnalysis();
			GeneralUtils.loadAnalysisEngine(a);
			if (GeneralUtils.generatesNamedEntityAnnotationType(a.getAnalysisEngine().getAnalysisEngineMetaData()))
				showView(ConceptsView.ID);
			else
				hideView(ConceptsView.ID);

		} catch (Exception e) {
			e.printStackTrace();
			hideView(ConceptsView.ID);
		}

//		if (!GeneralUtils.isOther(((AnalysisEditorInput) ((IEditorPart) part)
//				.getEditorInput()).getAnalysis()))
//			showView(ConceptsView.ID);
//		else
//			hideView(ConceptsView.ID);
	}

	IViewPart showView(String viewId) {
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		IViewPart part = page.findView(viewId);
		if (part == null) {
			try {
				part = page.showView(viewId);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}

		if (part != null) {
			page.bringToTop(part);
			if (part instanceof StackedViewPart)
				((StackedViewPart) part).showDelegateControl();
		}

		return part;
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		logger.debug(part.getTitle() + " brought to top");

		if (part instanceof AnalysisEditor) {
			showViewsForAnalysisEditor(part);
			hideViewsUnusedForAnalysisEditor(part);
		} else if (part instanceof OntologyEditor) {
			hideViewsUnusedForOntologyEditor(part);
		} else if (part instanceof AnnotatedDocumentEditor) {
			showViewsForAnnotatedDocumentEditor(part);
			hideViewsUnusedForAnnotatedDocumentEditor(part);
			((AnnotatedDocumentEditor) part).handleActivation();
		}
	}

	@Override
	public void partActivated(IWorkbenchPart part) {
		logger.debug(part.getTitle() + " activated");

		// if(part instanceof AnnotatedDocumentEditor){
		// ((AnnotatedDocumentEditor)part).handleActivation();
		// }
	};

	@Override
	public void partClosed(IWorkbenchPart part) {
		logger.debug(part.getTitle() + " closed");

//		if (part instanceof AnalysisEditor) {
//			GeneralUtils
//					.closeAllAnnotatedDocumentEditorsFor(((AnalysisEditorInput) ((AnalysisEditor) part)
//							.getEditorInput()).getAnalysis());
//		}

		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		if (page == null)
			return;
		if (page.getActiveEditor() == null)
			hideAllViews();

	};

	private void hideAllViews() {
		hideView(AnnotationTypeView.ID);
		hideView(DetailsView.ID);
		hideView(ConceptsView.ID);
		hideView(DocumentsView.ID);
		hideView(CoreferenceView.ID);
		hideView(SuggestionsView.ID);
	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
		logger.debug(part.getTitle() + " deactivated");

	};

	@Override
	public void partOpened(IWorkbenchPart part) {
		logger.debug(part.getTitle() + " opened");
	};

}
