package edu.pitt.dbmi.odie.ui.views;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ViewPart;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.ui.views.providers.OntologiesContentProvider;
import edu.pitt.dbmi.odie.ui.views.providers.OntologiesLabelProvider;
import edu.pitt.dbmi.odie.utils.GeneralUtils;
import edu.pitt.ontology.IOntology;

/**
 *
 */

public class OntologiesView extends ViewPart {

	public static final String ID = "edu.pitt.dbmi.odie.ui.views.OntologiesView";

	private TreeViewer viewer;
	private Action openAction;

	Logger logger = Logger.getLogger(this.getClass());

	private Action selectionChangedAction;

	/**
	 * The constructor.
	 */
	public OntologiesView() {

	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {

		doParentLayout(parent);
		createViewer(parent);
		getSite().setSelectionProvider(viewer);

		makeActions();
		hookSelectionChangedAction();
		hookDoubleClickAction();
		hookContextMenu();

		viewer.setInput(Activator.getDefault().getMiddleTier().getSortedOntologies());

	}

	private void hookContextMenu() {
		MenuManager mm = new MenuManager();
		getSite().registerContextMenu(mm, viewer);
		Control control = viewer.getControl();
		Menu menu = mm.createContextMenu(control);
		control.setMenu(menu);

	}

	private void doParentLayout(Composite parent) {
		// parent.setLayout(new FillLayout());

	}

	private void createViewer(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		viewer.setContentProvider(new OntologiesContentProvider());
		viewer.setLabelProvider(new OntologiesLabelProvider());
	}

	private void makeActions() {
		openAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				
				if (obj == null ||!(obj instanceof IOntology))
					return;
				GeneralUtils.openOntologyEditor((IOntology) obj, true);
			}
			
			
		};

		selectionChangedAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				if (obj == null ||!(obj instanceof IOntology))
					return;

				GeneralUtils.openOntologyEditor((IOntology) obj, false);
			}
		};
	}

	private void hookSelectionChangedAction() {
		viewer.addPostSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				selectionChangedAction.run();

			}

		});

	}

	private void hookDoubleClickAction() {
		viewer.addOpenListener(new IOpenListener() {

			@Override
			public void open(OpenEvent event) {
				openAction.run();

			}

		});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	/**
	 * 
	 */
	public void refresh() {
		viewer.setInput(Activator.getDefault().getMiddleTier().getRepository()
				.getOntologies());
		// viewer.setSelection(viewer.getSelection());
	}

	/**
	 * @param analysis
	 */
	public void setSelection(Analysis analysis) {
		viewer.setSelection(new StructuredSelection(analysis), true);

	}

	public TreeViewer getViewer() {
		return viewer;
	}

}