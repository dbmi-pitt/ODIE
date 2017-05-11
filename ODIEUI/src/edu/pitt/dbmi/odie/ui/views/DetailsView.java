package edu.pitt.dbmi.odie.ui.views;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

import edu.pitt.dbmi.odie.ui.views.details.DetailViewFactory;
import edu.pitt.dbmi.odie.ui.views.details.DetailsTreeTable;

public class DetailsView extends StackedViewPart implements ISelectionListener {

	Logger logger = Logger.getLogger(this.getClass());
	private DetailsTreeTable detailsTable;

	public static final String ID = "edu.pitt.dbmi.odie.ui.views.DetailsView";

	@Override
	public void createDelegatePartControl(Composite parent) {
		logger.debug("creating DetailsView");
		doParentLayoutAndAesthetics(parent);
		createDetailsWidget(parent);
		hookListeners();
	}

	private void doParentLayoutAndAesthetics(Composite parent) {
		parent.setLayout(new FillLayout());
		//		
		// parent.setBackground(new Color(parent.getDisplay(),255,255,255));
	}

	private void createDetailsWidget(Composite parent) {
		detailsTable = new DetailsTreeTable(parent);
	}

	private void hookListeners() {
		getSite().getPage().addSelectionListener(this);
	}

	private void unhookListeners() {
		getSite().getPage().removeSelectionListener(this);
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {

		DetailViewFactory dvf = DetailViewFactory.getInstance();
		if (!selection.isEmpty() && selection instanceof StructuredSelection) {
			Object element = ((StructuredSelection) selection)
					.getFirstElement();

			detailsTable.setupProvidersFor(element);
			detailsTable.setInput(element);
			detailsTable.expandAll();
		} else {
			detailsTable.setupProvidersFor("");
			detailsTable.setInput("");
			detailsTable.expandAll();
		}
	}

	@Override
	public void dispose() {
		unhookListeners();
		super.dispose();
	}

	@Override
	public void setFocus() {
		detailsTable.getControl().setFocus();
	}

	@Override
	protected void refreshView() {
		// TODO Auto-generated method stub

	}

}