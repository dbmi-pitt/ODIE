package edu.pitt.dbmi.odie.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;

import edu.mayo.bmi.uima.coref.type.Chain;
import edu.mayo.bmi.uima.coref.type.Markable;
import edu.pitt.dbmi.odie.model.AnalysisDocument;
import edu.pitt.dbmi.odie.ui.editors.document.AnnotatedDocumentEditor;
import edu.pitt.dbmi.odie.ui.editors.document.DocumentEditorInput;
import edu.pitt.dbmi.odie.ui.views.providers.CoreferenceContentProvider;
import edu.pitt.dbmi.odie.ui.views.providers.CoreferenceLabelProvider;

/**
 *
 */

public class CoreferenceView extends StackedViewPart implements
		ISelectionProvider {

	public static final String ID = "edu.pitt.dbmi.odie.ui.views.CoreferenceView";

	private TreeViewer viewer;
	Logger logger = Logger.getLogger(this.getClass());

	/**
	 * 
	 */
	public void refreshView() {
		viewer.refresh();
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
	 * @see
	 * org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse
	 * .jface.viewers.ISelection)
	 */
	@Override
	public void setSelection(ISelection selection) {
		// TODO Auto-generated method stub

	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createDelegatePartControl(Composite parent) {

		createParentLayout(parent);
		applyAesthetics(parent);

		createViewer(parent);

		hookListeners();

		getSite().setSelectionProvider(this);
	}

	ListenerList selectionChangedListeners = new ListenerList();

	/**
	 * @param parent
	 */
	private void createParentLayout(Composite parent) {
		parent.setLayout(new FillLayout());
	}

	private void hookTreeListener() {
		viewer.addPostSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				fireSelectionChanged();
			}
		});

	}

	private void hookListeners() {
		hookTreeListener();
		hookDocumentEditorListener();
	}

	/**
	 * @param parent
	 */
	private void applyAesthetics(Composite parent) {
		parent.setBackground(new Color(parent.getDisplay(), 255, 255, 255));
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
		List l = ((StructuredSelection) viewer.getSelection()).toList();

		ArrayList markableList = new ArrayList();

		for (Object o : l) {
			if (o instanceof Chain) {

				markableList.addAll(Arrays.asList(CoreferenceContentProvider
						.getMembers((Chain) o)));
			} else if (o instanceof Markable) {
				markableList.add(o);
			}
		}

		StructuredSelection ss = new StructuredSelection(markableList);
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

				viewer.setInput(doc);
			}
		});

	}

	private void createViewer(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new CoreferenceContentProvider());
		viewer.setLabelProvider(new CoreferenceLabelProvider());
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

}