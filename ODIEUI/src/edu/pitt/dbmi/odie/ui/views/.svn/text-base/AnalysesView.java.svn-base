package edu.pitt.dbmi.odie.ui.views;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ViewPart;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.ui.editors.analysis.AnalysisEditor;
import edu.pitt.dbmi.odie.ui.editors.analysis.AnalysisEditorInput;
import edu.pitt.dbmi.odie.ui.views.providers.AnalysisContentProvider;
import edu.pitt.dbmi.odie.ui.views.providers.AnalysisLabelProvider;
import edu.pitt.dbmi.odie.ui.workers.AnalysisLoader;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

/**
 *
 */

public class AnalysesView extends ViewPart {

	public static final String ID = "edu.pitt.dbmi.odie.ui.views.AnalysesView";

	private TreeViewer viewer;
	private Action openAction;
	private Action selectionChangedAction;

	Logger logger = Logger.getLogger(this.getClass());

	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public AnalysesView() {

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

	}

	private void hookContextMenu() {
		MenuManager mm = new MenuManager();
		getSite().registerContextMenu(mm, viewer);
		Control control = viewer.getControl();
		Menu menu = mm.createContextMenu(control);
		control.setMenu(menu);

	}

	private void doParentLayout(Composite parent) {
		parent.setLayout(new FillLayout());

	}

	private void createViewer(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new AnalysisContentProvider(viewer));
		viewer.setLabelProvider(new AnalysisLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());
	}

	private void makeActions() {
		openAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();

				loadAnalysis((Analysis) obj);
				GeneralUtils.openEditor(AnalysisEditor.ID,new AnalysisEditorInput(
						(Analysis) obj), true);

				GeneralUtils.refreshViews();
			}

			private void loadAnalysis(Analysis analysis) {
				ProgressMonitorDialog pd = GeneralUtils
						.getProgressMonitorDialog();
				try {
					pd.run(true, false, new AnalysisLoader(analysis));
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};

		selectionChangedAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				if (obj == null)
					return;

				GeneralUtils.openEditor(AnalysisEditor.ID,new AnalysisEditorInput(
						(Analysis) obj), false);
			}
		};

	}

	public TreeViewer getViewer() {
		return viewer;
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
		viewer.refresh();
		viewer.setSelection(viewer.getSelection());
	}

	/**
	 * @param analysis
	 */
	public void setSelection(Analysis analysis) {
		viewer.setSelection(new StructuredSelection(analysis), true);

	}

}