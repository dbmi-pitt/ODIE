package edu.pitt.dbmi.odie.ui.views;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.cas.TypeSystem;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.PatternFilter;

import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.model.AnalysisEngineMetadata;
import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.dbmi.odie.ui.editors.document.AnnotatedDocumentEditor;
import edu.pitt.dbmi.odie.ui.editors.document.DocumentEditorInput;
import edu.pitt.dbmi.odie.uima.util.ODIE_IFConstants;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

/**
 * 
 */

public class AnnotationTypeView extends StackedViewPart implements
		ISelectionProvider {

	public static final String ID = "edu.pitt.dbmi.odie.ui.views.AnnotationTypeView";
	Logger logger = Logger.getLogger(this.getClass());

	public static void main(String args[]) {
		Display display = new Display();
		Shell parent = new Shell(display);
		parent.setLayout(new FillLayout());

		AnnotationTypeView p = new AnnotationTypeView();

		p.createPartControl(parent);

		parent.pack();

		parent.setSize(900, 600);
		parent.open();
		while (!parent.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();

		display.dispose();
	}

	private AnnotationsTypeSystemTree annotationTree;

	ListenerList selectionChangedListeners = new ListenerList();

	private AnnotationsPatternFilter unusedTypeFilter;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createDelegatePartControl(Composite parent) {
		logger.debug("creating AnnotationTypeView");
		createParentLayout(parent);
		applyAesthetics(parent);

		createTree(parent);

		hookListeners();

		getSite().setSelectionProvider(this);

	}

	/**
	 * @param parent
	 */
	private void createParentLayout(Composite parent) {
		GridLayout gl = new GridLayout();
		gl.marginWidth = Aesthetics.LEFT_MARGIN;
		gl.marginHeight = Aesthetics.TOP_MARGIN;
		parent.setLayout(gl);
	}

	/**
	 * @param parent
	 */
	private void applyAesthetics(Composite parent) {
		parent.setBackground(new Color(parent.getDisplay(), 255, 255, 255));
	}

	/**
	 * @param parent
	 */
	private void createTree(Composite parent) {
		annotationTree = new AnnotationsTypeSystemTree(parent,
				new PatternFilter());

		unusedTypeFilter = new AnnotationsPatternFilter();
		annotationTree.getViewer().addFilter(unusedTypeFilter);
	}

	private void hookListeners() {
		hookTreeListener();
		hookDocumentEditorListener();
		hookDragListener();
		hookDropListener();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener
	 * (org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);

	}

	/**
	 * 
	 */
	private void fireSelectionChanged() {
		SelectionChangedEvent se = new SelectionChangedEvent(this,
				getSelection());
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	@Override
	public ISelection getSelection() {
		OrderedStructuredSelection ss = null;

		List l = ((StructuredSelection) annotationTree.getViewer()
				.getSelection()).toList();
		ss = new OrderedStructuredSelection(l);
		// ss.setDisplayOrder(contentProvider.getDisplayOrder());
		return ss;

	}

	/**
	 * 
	 */
	private void hookDocumentEditorListener() {
		getSite().getPage().addPartListener(new IPartListener() {

			@Override
			public void partActivated(IWorkbenchPart part) {
				// changeInput(part);
			}

			@Override
			public void partBroughtToTop(IWorkbenchPart part) {
				if (part instanceof AnnotatedDocumentEditor)
					changeInput(part);
			}

			@Override
			public void partClosed(IWorkbenchPart part) {
			}

			@Override
			public void partDeactivated(IWorkbenchPart part) {
			}

			@Override
			public void partOpened(IWorkbenchPart part) {
			}

			private void changeInput(IWorkbenchPart part) {
				AnalysisDocument doc = ((DocumentEditorInput) ((IEditorPart) part)
						.getEditorInput()).getAnalysisDocument();

				AnalysisEngineMetadata aem = doc.getAnalysis()
						.getAnalysisEngineMetadata();
				try {
					GeneralUtils.initTypeSystemIfRequired(aem);
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}

				unusedTypeFilter.setCas(doc.getCas());
				annotationTree.getLabelProvider().setCas(doc.getCas());
				annotationTree.setInput(aem.getTypeSystem());
				annotationTree.getViewer().expandAll();

				if (annotationTree.getViewer().getSelection().isEmpty())
					selectDefaultAnnotationType(aem.getTypeSystem());
			}

			private void selectDefaultAnnotationType(TypeSystem ts) {
				StructuredSelection ss = new StructuredSelection(ts
						.getType(ODIE_IFConstants.DOCUMENT_TYPE_FULLNAME));
				annotationTree.getViewer().setSelection(ss);
			}

		});

	}

	/**
	 * 
	 */
	private void hookDragListener() {
		// DragSourceListener dragListener = new DragSourceListener() {
		//
		// Object selection;
		//			
		// @Override
		// public void dragStart(DragSourceEvent event) {
		// selection = getItemAtPoint(new Point(event.x,event.y));
		// if(selection==null || !contentProvider.isRootElement(selection))
		// event.doit = false;
		// else
		// event.doit = true;
		// }
		//			
		// private Object getItemAtPoint(Point point) {
		// Tree tree = annotationTree.getViewer().getTree();
		// int index = tree.indexOf(tree.getTopItem());
		// while (index < tree.getItemCount ()) {
		// TreeItem item = tree.getItem (index);
		// Rectangle rect = item.getBounds();
		// if (rect.contains (point)) {
		// return item.getData();
		// }
		// index++;
		// }
		// return null;
		// }
		//
		// @Override
		// public void dragSetData(DragSourceEvent event) {
		// if (selection != null
		// && TextTransfer.getInstance().isSupportedType(
		// event.dataType)){
		// if(selection instanceof IResource)
		// event.data = ((IResource) selection).getURI().toString();
		// else
		// event.data = selection.toString();
		// }
		// }
		//
		// @Override
		// public void dragFinished(DragSourceEvent event) {
		// }
		// };
		// annotationTree.getViewer().addDragSupport(DND.DROP_MOVE,
		// new Transfer[] { TextTransfer.getInstance() }, dragListener);
	}

	/**
	 * 
	 */
	private void hookDropListener() {
		// DropTargetListener dropListener = new DropTargetAdapter() {
		//
		// @Override
		// public void dragOver(DropTargetEvent event) {
		// event.feedback = DND.FEEDBACK_SCROLL;
		// if (event.item != null) {
		// TreeItem sel = (TreeItem) event.item;
		// if (contentProvider.isRootElement(sel.getData())) {
		// Point pt = annotationTree.getViewer().getTree()
		// .getDisplay().map(null,
		// annotationTree.getViewer().getTree(),
		// event.x, event.y);
		// Rectangle bounds = sel.getBounds();
		// if (pt.y < bounds.y + bounds.height / 3) {
		// event.feedback |= DND.FEEDBACK_INSERT_BEFORE;
		// } else if (pt.y > bounds.y + 2 * bounds.height / 3) {
		// event.feedback |= DND.FEEDBACK_INSERT_AFTER;
		// }
		// }
		// }
		// }
		//
		// @Override
		// public void drop(DropTargetEvent event) {
		// if (event.data == null || event.item == null) {
		// event.detail = DND.DROP_NONE;
		// return;
		// } else {
		// Object anchor = event.item.getData();
		//					
		// TreeItem item = (TreeItem) event.item;
		//
		// Point pt = annotationTree.getViewer().getTree()
		// .getDisplay().map(null,
		// annotationTree.getViewer().getTree(),
		// event.x, event.y);
		// Rectangle bounds = item.getBounds();
		//
		// boolean success = false;
		// URI uri = null;
		//					
		// try {
		// uri = new URI((String) event.data);
		// } catch (URISyntaxException e) {
		//						
		// }
		// Object draggedItem = null;
		//					
		// if(uri!=null){
		// draggedItem =
		// Activator.getDefault().getMiddleTier().getResourceForURI(uri);
		// }
		//					
		// if(draggedItem == null){
		// logger.debug("Dragged item not a IResource, will treat as label");
		// draggedItem = event.data;
		// }
		//
		// if (pt.y < bounds.y + bounds.height / 3)
		// success = contentProvider.reorder(draggedItem, anchor,
		// true);
		// else if (pt.y > bounds.y + 2 * bounds.height / 3)
		// success = contentProvider.reorder(draggedItem, anchor,
		// false);
		//				
		//
		// if (!success) {
		// event.detail = DND.DROP_NONE;
		// } else {
		// annotationTree.getViewer().refresh();
		// fireSelectionChanged();
		// }
		// }
		// }
		// };
		// annotationTree.getViewer().addDropSupport(DND.DROP_MOVE,
		// new Transfer[] { TextTransfer.getInstance() }, dropListener);
	}

	/**
	 * 
	 */
	private void hookTreeListener() {
		annotationTree.getViewer().addPostSelectionChangedListener(
				new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						fireSelectionChanged();
					}
				});

	}

	/**
	 * 
	 */
	public void refreshView() {
		annotationTree.getViewer().refresh();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener
	 * (org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		annotationTree.setFocus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse
	 * .jface.viewers.ISelection)
	 */
	@Override
	public void setSelection(ISelection selection) {
		// TODO Auto-generated method stub

	}
}