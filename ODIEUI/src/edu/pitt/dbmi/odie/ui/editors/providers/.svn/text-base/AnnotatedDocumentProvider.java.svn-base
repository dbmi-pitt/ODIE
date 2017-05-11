package edu.pitt.dbmi.odie.ui.editors.providers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.texteditor.AbstractDocumentProvider;

import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.model.Suggestion;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.ui.editors.annotations.EclipseAnnotationPeer;
import edu.pitt.dbmi.odie.ui.editors.annotations.IAnnotationSubType;
import edu.pitt.dbmi.odie.ui.editors.document.AnnotatedDocument;
import edu.pitt.dbmi.odie.ui.editors.document.DocumentEditorInput;
import edu.pitt.dbmi.odie.ui.editors.document.ODIEAnnotationModel;
import edu.pitt.dbmi.odie.utils.GeneralUtils;
import edu.pitt.dbmi.odie.utils.StopWatch;

public class AnnotatedDocumentProvider extends AbstractDocumentProvider {

	Logger logger = Logger.getLogger(AnnotatedDocumentProvider.class);

	@Override
	protected IDocument createDocument(Object element) throws CoreException {
		if (element instanceof DocumentEditorInput) {
			StopWatch s = new StopWatch();
			s.start();
			DocumentEditorInput deInput = (DocumentEditorInput) element;
			AnalysisDocument ad = deInput.getAnalysisDocument();
			try {
				GeneralUtils.initCASIfRequired(ad);
			} catch (Exception e) {
				e.printStackTrace();
			}
			s.stop();
			logger.debug("Document creation:" + s.getElapsedTime());
			return new AnnotatedDocument(ad);
		} else {
			IStatus status;

			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.OK,
					"Editor input is incorrect.", null);
			elementErrorStatus.put(element, status);
			return null;
		}
	}

	@Override
	public boolean isModifiable(Object element) {
		return false;
	}

	@Override
	public boolean isReadOnly(Object element) {
		return true;
	}

	protected Map<Object, IStatus> elementErrorStatus = new HashMap<Object, IStatus>();
	private ODIEAnnotationModel annotationModel;
	private AnalysisDocument analysisDocument;

	@Override
	public IStatus getStatus(Object element) {
		IStatus status = elementErrorStatus.get(element);

		if (status == null) {
			status = super.getStatus(element);
		}

		return status;
	}

	@Override
	protected void doSaveDocument(IProgressMonitor monitor, Object element,
			IDocument document, boolean overwrite) throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	protected IRunnableContext getOperationRunner(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IAnnotationModel createAnnotationModel(Object element)
			throws CoreException {
		StopWatch s = new StopWatch();
		s.start();
		annotationModel = new ODIEAnnotationModel();

		if (element instanceof DocumentEditorInput) {
			DocumentEditorInput deInput = (DocumentEditorInput) element;
			analysisDocument = deInput.getAnalysisDocument();

			if (analysisDocument.getCas() == null) {
				// if(!initCAS(analysisDocument))
				return annotationModel;
			}

			createSuggestionAnnotations();

			// addAnnotationsFilteredByType();

		} else {
			IStatus status;

			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.OK,
					"Editor input is incorrect.", null);
			elementErrorStatus.put(element, status);
			return null;
		}

		s.stop();
		logger.debug("Annotation Model creation:" + s.getElapsedTime());
		return annotationModel;
	}

	private void addAnnotationsFilteredByType() {

		CAS aCas = analysisDocument.getCas();

		TypeSystem ts = aCas.getTypeSystem();

		for (Iterator typeIterator = ts.getTypeIterator(); typeIterator
				.hasNext();) {
			Type type = (Type) typeIterator.next();

			if (visibleTypes.contains(type) || hasParentVisible(type, ts)) {
				if (visibleUserParentTypes.contains(type)) {
					for (Iterator it = aCas.getAnnotationIndex(type).iterator(); it
							.hasNext();) {
						AnnotationFS annFS = (AnnotationFS) it.next();
						if (meetsUserTypeRequirements(annFS)) {
							EclipseAnnotationPeer ann = new EclipseAnnotationPeer(
									annFS, analysisDocument);
							annotationModel.addAnnotation(ann, new Position(
									annFS.getBegin(), annFS.getEnd()
											- annFS.getBegin()));
						}
					}
				} else {
					for (Iterator it = aCas.getAnnotationIndex(type).iterator(); it
							.hasNext();) {
						AnnotationFS annFS = (AnnotationFS) it.next();
						EclipseAnnotationPeer ann = new EclipseAnnotationPeer(
								annFS, analysisDocument);
						annotationModel
								.addAnnotation(ann, new Position(annFS
										.getBegin(), annFS.getEnd()
										- annFS.getBegin()));
					}
				}
			}
		}

	}

	private boolean meetsUserTypeRequirements(AnnotationFS annFS) {
		for (IAnnotationSubType type : visibleUserTypes) {
			if (type.meetsSubTypeCriteria(annFS))
				return true;
		}
		return false;
	}

	private void addAnnotationsFilteredByAnnotationList() {
		for (AnnotationFS annFS : visibleAnnotations) {
			// logger.debug("Adding " + annFS.getType() + " at " +
			// annFS.getBegin() + "," + annFS.getEnd());
			EclipseAnnotationPeer ann = new EclipseAnnotationPeer(annFS,
					analysisDocument);
			annotationModel.addAnnotation(ann, new Position(annFS.getBegin(),
					annFS.getEnd() - annFS.getBegin()));
		}
	}

	private void addAnnotations() {
		StopWatch s = new StopWatch();
		s.start();
		// we want to disable repainting of annotations as we make changes to
		// the model
		annotationModel.disableModelChangeFiring();

		if (filteringByType)
			addAnnotationsFilteredByType();
		else
			addAnnotationsFilteredByAnnotationList();

		// notify painter that model has changed after all annotations have been
		// added.
		annotationModel.enableModelChangeFiring();
		annotationModel.fireModelChanged();
		s.stop();
		logger.debug("Adding annotations:" + s.getElapsedTime());
	}

	private ArrayList<Type> visibleTypes = new ArrayList<Type>();
	private ArrayList<Type> visibleUserParentTypes = new ArrayList<Type>();
	private ArrayList<IAnnotationSubType> visibleUserTypes = new ArrayList<IAnnotationSubType>();
	private List<AnnotationFS> visibleAnnotations = new ArrayList<AnnotationFS>();

	boolean filteringByType = false;

	public void setVisibleTypes(List<Type> visibleTypes) {

		this.visibleTypes.clear();
		this.visibleTypes.addAll(visibleTypes);

		handleUserTypesInVisibleTypesList();

		filteringByType = true;

		resetAnnotations();

	}

	private void handleUserTypesInVisibleTypesList() {
		CAS aCas = analysisDocument.getCas();
		TypeSystem ts = aCas.getTypeSystem();

		visibleUserTypes.clear();
		visibleUserParentTypes.clear();

		for (Type t : visibleTypes) {
			if (t instanceof IAnnotationSubType) {
				IAnnotationSubType ut = (IAnnotationSubType) t;
				Type parentType = ts.getType(ut.getParentTypeName());

				visibleUserTypes.add(ut);
				visibleUserParentTypes.add(parentType);
			}
		}
		visibleTypes.removeAll(visibleUserTypes);
		visibleTypes.addAll(visibleUserParentTypes);

	}

	private void resetAnnotations() {
		annotationModel.removeAllAnnotations();
		addAnnotations();

	}

	public void setVisibleAnnotations(List<AnnotationFS> visibleAnnotations) {
		this.visibleAnnotations = visibleAnnotations;
		filteringByType = false;
		resetAnnotations();
	}

	private boolean hasParentVisible(Type type, TypeSystem typeSystem) {
		for (Object stype : visibleTypes) {
			if (typeSystem.subsumes((Type) stype, type))
				return true;
		}
		return false;
	}

	public boolean isAnnotationTypeVisible(Type type) {
		return (visibleTypes.contains(type) || hasParentVisible(type,
				analysisDocument.getCas().getTypeSystem()));

	}

	public boolean isAnnotationVisible(AnnotationFS ann) {
		if (filteringByType)
			return isAnnotationTypeVisible(ann.getType());
		else {
			return visibleAnnotations.contains(ann);
		}
	}

	private List<AnnotationFS> getAnnotationsForSuggestion(String suggestiontext) {
		int fromIndex = 0;
		int length = suggestiontext.length();

		List<AnnotationFS> outlist = new ArrayList<AnnotationFS>();

		String text = analysisDocument.getDocument().getText().toLowerCase();
		int index = text.indexOf(suggestiontext.toLowerCase(), fromIndex);

		while (index > -1) {
			outlist.add(new SuggestionAnnotation(index, index + length,
					suggestiontext));

			fromIndex = index + length;
			index = text.indexOf(suggestiontext.toLowerCase(), fromIndex);
		}
		return outlist;
	}

	private HashMap<String, List<AnnotationFS>> suggestionsMap = new HashMap<String, List<AnnotationFS>>();

	private void createSuggestionAnnotations() {
		Analysis analysis = analysisDocument.getAnalysis();
		if (!GeneralUtils.isOE(analysis))
			return;

		MiddleTier mt = Activator.getDefault().getMiddleTier();

		if (analysisDocument.getSuggestions() == null) {
			analysisDocument.setSuggestions(mt.getSuggestions(analysisDocument,
					GeneralUtils.getSuggestions(analysis)));
		}

		List<Suggestion> list = analysisDocument.getSuggestions();
		for (Suggestion s : list) {
			String suggText = s.getNerNegative();
			if (suggestionsMap.get(suggText) == null) {
				suggestionsMap.put(suggText,
						getAnnotationsForSuggestion(suggText));
			}
		}
	}

	public void setVisibleSuggestions(List<Suggestion> visibleSuggestions) {
		List<AnnotationFS> outlist = new ArrayList<AnnotationFS>();
		for (Suggestion s : visibleSuggestions) {
			List<AnnotationFS> l = suggestionsMap.get(s.getNerNegative());

			if (l != null)
				outlist.addAll(l);
		}
		setVisibleAnnotations(outlist);

	}

	public List<AnnotationFS> getVisibleAnnotations() {
		return visibleAnnotations;
	}

}