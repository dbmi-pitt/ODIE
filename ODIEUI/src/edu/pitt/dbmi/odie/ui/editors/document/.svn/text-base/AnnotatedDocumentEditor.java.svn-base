package edu.pitt.dbmi.odie.ui.editors.document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.cas.ArrayFS;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.text.source.AnnotationPainter;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.StatusTextEditor;

import edu.mayo.bmi.uima.core.type.NamedEntity;
import edu.mayo.bmi.uima.coref.type.Markable;
import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.model.AnalysisEngineMetadata;
import edu.pitt.dbmi.odie.model.Datapoint;
import edu.pitt.dbmi.odie.model.Suggestion;
import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.dbmi.odie.ui.editors.annotations.AnnotationSubTypeRegistry;
import edu.pitt.dbmi.odie.ui.editors.annotations.IAnnotationSubType;
import edu.pitt.dbmi.odie.ui.editors.providers.AnnotatedDocumentProvider;
import edu.pitt.dbmi.odie.ui.editors.providers.SuggestionAnnotation;
import edu.pitt.dbmi.odie.ui.views.AnnotationTypeView;
import edu.pitt.dbmi.odie.ui.views.ConceptsView;
import edu.pitt.dbmi.odie.ui.views.CoreferenceView;
import edu.pitt.dbmi.odie.ui.views.DetailsView;
import edu.pitt.dbmi.odie.ui.views.OrderedStructuredSelection;
import edu.pitt.dbmi.odie.ui.views.SuggestionsView;
import edu.pitt.dbmi.odie.uima.util.ODIE_IFConstants;
import edu.pitt.dbmi.odie.uima.utils.UIMAUtils;
import edu.pitt.dbmi.odie.utils.GeneralUtils;
import edu.pitt.dbmi.odie.utils.StopWatch;

public class AnnotatedDocumentEditor extends StatusTextEditor implements
		ISelectionListener, ISelectionProvider {

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		getSite().setSelectionProvider(null); // why?
		getSite().setSelectionProvider(this);
		hookListeners();
	}

	Logger logger = Logger.getLogger(this.getClass());
	public static final String ID = "edu.pitt.dbmi.odie.ui.editors.AnnotatedDocumentEditor";
	private AnnotationPainter annotationPainter;
	private AnnotationAccess annotationAccess;

	public AnnotatedDocumentEditor() {
		super();
		setDocumentProvider(new AnnotatedDocumentProvider());
	}

	@Override
	protected ISourceViewer createSourceViewer(Composite parent,
			IVerticalRuler ruler, int styles) {
		StopWatch s = new StopWatch();
		s.start();
		SourceViewer sv = (SourceViewer) super.createSourceViewer(parent,
				ruler, styles | SWT.WRAP);
		try {
			initAnnotationPainter(sv);
			sv.addPainter(annotationPainter);
			sv.addTextPresentationListener(annotationPainter);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			s.stop();
			logger.debug("SourceViewer creation:" + s.getElapsedTime());
			return sv;
		}

	}

	private void initAnnotationPainter(SourceViewer sv) throws Exception {
		StopWatch s = new StopWatch();
		s.start();
		AnalysisEngineMetadata aem = ((DocumentEditorInput) getEditorInput())
				.getAnalysisDocument().getAnalysis()
				.getAnalysisEngineMetadata();
		GeneralUtils.initTypeSystemIfRequired(aem);
		TypeSystem ts = aem.getTypeSystem();

		annotationAccess = new AnnotationAccess(ts);
		annotationPainter = new AnnotationPainter(sv,
				(IAnnotationAccess) annotationAccess);

		addAnnotationTypesToPainter(ts);
		s.stop();
		logger.debug("Init Annotation Painter:" + s.getElapsedTime());
	}

	/*
	 * This method registers the maps the various type names to to their correct
	 * colors. The Aesthetics object always stores the colors mapped to the Type
	 * or AnnotationSubType object.
	 */
	private void addAnnotationTypesToPainter(TypeSystem ts) {
		Type annotationType = ts.getType("uima.tcas.Annotation");
		for (Iterator it = ts.getTypeIterator(); it.hasNext();) {
			Type type = (Type) it.next();
			if (ts.subsumes(annotationType, type)) {
				// logger.info("Adding annotation type:" + type.getName());
				annotationPainter.addHighlightAnnotationType(type.getName());
				annotationPainter.setAnnotationTypeColor(type.getName(),
						Aesthetics.getColorForObject(type));
			}
		}
		Type suggestionType = SuggestionAnnotation.SUGGESTION_TYPE;
		annotationPainter.addHighlightAnnotationType(suggestionType.getName());
		annotationPainter.setAnnotationTypeColor(suggestionType.getName(),
				Aesthetics.getColorForObject(suggestionType));

		AnnotationSubTypeRegistry registry = AnnotationSubTypeRegistry
				.getInstance();

		for (IAnnotationSubType st : registry.getRegisteredSubTypes()) {
			annotationPainter.addHighlightAnnotationType(st.getName());
			annotationPainter.setAnnotationTypeColor(st.getName(), Aesthetics
					.getColorForObject(st));
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.
	 * IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		StopWatch s = new StopWatch();
		s.start();
		if (part instanceof AnnotationTypeView) {
			if (selection instanceof OrderedStructuredSelection) {
				OrderedStructuredSelection dss = (OrderedStructuredSelection) selection;
				setVisibleTypes(dss.toList());
			}
		} else if (part instanceof ConceptsView) {
			if (selection instanceof StructuredSelection) {
				StructuredSelection ss = (StructuredSelection) selection;
				setVisibleAnnotations(getAnnotationsForDatapoints(ss.toList()));
			}
		} else if (part instanceof SuggestionsView) {
			if (selection instanceof StructuredSelection) {
				StructuredSelection ss = (StructuredSelection) selection;
				setVisibleSuggestions(ss.toList());
			}
		} else if (part instanceof DetailsView) {
			if (selection instanceof StructuredSelection) {
				StructuredSelection ss = (StructuredSelection) selection;
				selectAnnotations(ss.toList());
			}
		} else if (part instanceof CoreferenceView) {
			if (selection instanceof StructuredSelection) {
				StructuredSelection ss = (StructuredSelection) selection;
				setVisibleAnnotations(getAnnotationsForMarkables(ss.toList()));
			}
		}
		s.stop();
		logger.debug("Selection change:" + s.getElapsedTime());
	}

	private List<AnnotationFS> getAnnotationsForMarkables(List markableList) {
		List<AnnotationFS> outList = new ArrayList<AnnotationFS>();

		for (Object o : markableList) {
			Markable m = ((Markable) o);
			outList.add(m.getContent());
		}
		return outList;
	}

	private List<AnnotationFS> getAnnotationsForDatapoints(List<Datapoint> list) {
		DocumentEditorInput deInput = (DocumentEditorInput) getEditorInput();
		AnalysisDocument ad = deInput.getAnalysisDocument();
		CAS cas = ad.getCas();
		List<String> curiList = new ArrayList<String>();
		List<AnnotationFS> outList = new ArrayList<AnnotationFS>();
		// TODO It is assumed here Datapoints are created only for NamedEntity
		// annotations
		// this might not necessarily be true, in which case non NamedEntity
		// datapoints will
		// not get highlighted when they are selected.

		// AnnotationIndex annIndex =
		// cas.getAnnotationIndex(UIMAUtils.getNamedEntityType(cas));

		try {
			AnnotationIndex annIndex = cas.getJCas().getAnnotationIndex(
					NamedEntity.type);

			for (Datapoint dp : list) {
				curiList.add(dp.getConceptURIString());
			}

			for (Iterator it = annIndex.iterator(); it.hasNext();) {
				AnnotationFS ann = (AnnotationFS) it.next();

				ArrayFS arr = UIMAUtils.getOntologyConceptArray(ann);

				for (int i = 0; i < arr.size(); i++) {
					FeatureStructure fs = arr.get(i);
					String conceptURIString = GeneralUtils.getConceptURIFromFS(fs);

					if (curiList.contains(conceptURIString))
						outList.add(ann);
				}
			}
		} catch (CASRuntimeException e) {
			logger.error(e.getMessage());
		} catch (CASException e) {
			logger.error(e.getMessage());
		} finally {
			return outList;
		}

	}

	private void selectAnnotations(List list) {
		Object o = list.get(0);
		if (o instanceof AnnotationFS) {
			AnnotationFS a = (AnnotationFS) o;

			selectAndRevealAnnotation(a);

		}
	}

	private void selectAndRevealAnnotation(AnnotationFS a) {
		this.selectAndReveal(a.getBegin(), a.getEnd() - a.getBegin());
		fireSelectionChanged();
	}

	private void revealAnnotation(AnnotationFS a) {
		this.selectAndReveal(a.getBegin(), 0, a.getBegin(), a.getEnd()
				- a.getBegin());
	}

	private AnnotationFS selectedAnnotation;

	private void setVisibleTypes(List<Type> visibleTypes) {
		((AnnotatedDocumentProvider) getDocumentProvider())
				.setVisibleTypes(visibleTypes);
	}

	private void setVisibleAnnotations(List<AnnotationFS> visibleAnnotations) {
		if (visibleAnnotations.size() > 0) {
			((AnnotatedDocumentProvider) getDocumentProvider())
					.setVisibleAnnotations(visibleAnnotations);
			revealAnnotation(visibleAnnotations.get(0));
		}
	}

	private void setVisibleSuggestions(List<Suggestion> visibleSuggestions) {
		if (visibleSuggestions.size() > 0) {
			((AnnotatedDocumentProvider) getDocumentProvider())
					.setVisibleSuggestions(visibleSuggestions);
			List<AnnotationFS> visibleAnnotations = ((AnnotatedDocumentProvider) getDocumentProvider())
					.getVisibleAnnotations();
			if (visibleAnnotations.size() > 0) {
				revealAnnotation(visibleAnnotations.get(0));
			}
		}
	}

	private void hookListeners() {

		getSite().getPage().addSelectionListener(this);

		this.getSourceViewer().getTextWidget().addMouseListener(
				new MouseListener() {
					@Override
					public void mouseDoubleClick(MouseEvent e) {
						if (e.button == 1) {
							int offset = AnnotatedDocumentEditor.this
									.getSourceViewer().getTextWidget()
									.getOffsetAtLocation(new Point(e.x, e.y));

							AnnotationFS ann = getAnnotationAtOffset(offset);

							if (ann != null && isAnnotationVisible(ann)) {
								GeneralUtils
										.openAnnotationClassInOntologyEditor(UIMAUtils
												.getOntologyConceptArray(ann));
							}
						}

					}

					@Override
					public void mouseDown(MouseEvent e) {
						// TODO Auto-generated method stub

					}

					@Override
					public void mouseUp(MouseEvent e) {
						if (e.button == 1) {
							int offset = AnnotatedDocumentEditor.this
									.getSourceViewer().getTextWidget()
									.getOffsetAtLocation(new Point(e.x, e.y));

							AnnotationFS ann = getAnnotationAtOffset(offset);

							if (ann != null && isAnnotationVisible(ann)) {
								selectedAnnotation = ann;
								selectAndRevealAnnotation(selectedAnnotation);
							} else {
								selectedAnnotation = null;
							}
							fireSelectionChanged();
						}

					}
				});

		// this.addMouseMoveListener(new MouseMoveListener(){
		//
		//			
		// @Override
		// public void mouseMove(MouseEvent e) {
		// int offset = text.getOffsetAtLocation(new Point(e.x,e.y));
		// List<Annotation> annList = styler.getAnnotationsAtOffset(offset);
		// if(annList.isEmpty() ||
		// Activator.getDefault().getMiddleTier().isSystemAnnotation(annList.get(0)))
		// text.setToolTipText(null);
		// else{
		// IClass annotationClass = annList.get(0).getAnnotationClass();
		// String tooltip = annotationClass.getName();
		// tooltip += "(" + annotationClass.getOntology().getName() + ")";
		// text.setToolTipText(tooltip);
		// }
		// }
		//			
		// });

		//		
		// text.addListener(SWT.MenuDetect, new Listener(){
		//
		// @Override
		// public void handleEvent(Event event) {
		// if(da.getAnalysis().getType().equals(AnalysisEngineMetadata.TYPE_OE))
		// showMenu(event);
		// }
		//			
		// private void showMenu(Event e){
		// Menu menu = new Menu(self.getEditorSite().getShell(), SWT.POP_UP);
		//				
		// MenuItem item = new MenuItem(menu, SWT.PUSH);
		// item.setText("Add as a proposal...");
		// item.addListener(SWT.Selection, new Listener() {
		// public void handleEvent(Event e) {
		// AddToOntologyDialog d = new
		// AddToOntologyDialog(Display.getCurrent().getActiveShell());
		//		        	  
		// d.setPreferredName(text.getSelectionText().toLowerCase().trim().replaceAll("\\W","_"));
		//		        	  
		// int status = d.open();
		// if(status == IDialogConstants.OK_ID){
		// da.getAnalysis().createNewProposal(d.getPreferredName().toLowerCase().trim().replaceAll("\\W","_"));
		// }
		// else if(status == IDialogConstants.CANCEL_ID)
		// ;
		//		        	  
		//		        	  
		// }
		// });
		//		        
		// menu.setLocation(e.x, e.y);
		// menu.setVisible(true);
		// while (!menu.isDisposed() && menu.isVisible()) {
		// if (!Display.getCurrent().readAndDispatch())
		// Display.getCurrent().sleep();
		// }
		// menu.dispose();
		// }
		// });
	}

	protected boolean isAnnotationVisible(AnnotationFS ann) {
		return ((AnnotatedDocumentProvider) getDocumentProvider())
				.isAnnotationVisible(ann);
	}

	protected AnnotationFS getAnnotationAtOffset(int offset) {
		CAS cas = ((DocumentEditorInput) getEditorInput())
				.getAnalysisDocument().getCas();
		AnnotationIndex ai = cas.getAnnotationIndex();

		AnnotationFS smallestAnnotation = null;
		for (Iterator it = ai.iterator(); it.hasNext();) {
			AnnotationFS ann = (AnnotationFS) it.next();
			if (!isAnnotationVisible(ann))
				continue;

			if (offset >= ann.getBegin() && ann.getEnd() >= offset) {
				smallestAnnotation = getSmallest(smallestAnnotation, ann);
			}
		}

		return smallestAnnotation;
	}

	private AnnotationFS getSmallest(AnnotationFS ann1, AnnotationFS ann2) {
		if (ann1 == null)
			return ann2;

		if (ann2 == null)
			return ann1;

		if ((ann1.getEnd() - ann1.getBegin()) < (ann2.getEnd() - ann2
				.getBegin()))
			return ann1;
		else
			return ann2;
	}

	ListenerList selectionChangedListeners = new ListenerList();

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);

	}

	@Override
	public ISelection getSelection() {
		if (selectedAnnotation == null)
			return new StructuredSelection();

		return new StructuredSelection(selectedAnnotation);
	}

	/**
	 * 
	 */
	private void fireSelectionChanged() {
		SelectionChangedEvent se = new SelectionChangedEvent(
				AnnotatedDocumentEditor.this, getSelection());
		fireSelectionChanged(se);
	}

	/**
	 * Notifies any selection changed listeners that the viewer's selection has
	 * changed. Only listeners registered at the time this method is called are
	 * notified.
	 * 
	 * @param event
	 *            a selection changed event
	 * 
	 * @see ISelectionChangedListener#selectionChanged
	 */
	protected void fireSelectionChanged(final SelectionChangedEvent event) {
		Object[] listeners = selectionChangedListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			final ISelectionChangedListener l = (ISelectionChangedListener) listeners[i];
			SafeRunnable.run(new SafeRunnable() {
				public void run() {
					l.selectionChanged(event);
				}
			});
		}
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);

	}

	@Override
	public void setSelection(ISelection selection) {

	}

	public void handleActivation() {
		IWorkbenchPage page = getSite().getPage();
		ConceptsView cv = (ConceptsView) page.findView(ConceptsView.ID);
		if (page.isPartVisible(cv)) {
			selectionChanged(cv, cv.getStructuredViewer().getSelection());
		}
		SuggestionsView sv = (SuggestionsView) page
				.findView(SuggestionsView.ID);
		if (page.isPartVisible(sv)) {
			selectionChanged(sv, sv.getStructuredViewer().getSelection());
		}
	}
}
