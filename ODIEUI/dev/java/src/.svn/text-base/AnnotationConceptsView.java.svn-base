package edu.pitt.dbmi.odie.ui.views;

import org.apache.uima.cas.text.AnnotationFS;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.dbmi.odie.ui.editors.document.AnnotatedDocumentEditor;

public class AnnotationConceptsView extends StackedViewPart implements ISelectionListener {

	public static final String NamedEntityTypeName = "NamedEntity";
	public static final String ID = "edu.pitt.dbmi.odie.ui.views.AnnotationConceptsView";
	public AnnotationConceptsView() {
	}

	private AnnotationConceptsTable conceptsTable;
	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createDelegatePartControl(Composite parent) {
		doParentLayoutAndAesthetics(parent);
		createDetailsWidget(parent);
		hookListeners();
	}

	private void doParentLayoutAndAesthetics(Composite parent) {
		GridLayout gl = new GridLayout();
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = Aesthetics.VERTICAL_INTRAGROUP_SPACING;
		parent.setLayout(gl);
		
		parent.setBackground(new Color(parent.getDisplay(),255,255,255));
	}
	
	private void createDetailsWidget(Composite parent) {	
		conceptsTable = new AnnotationConceptsTable(parent, SWT.FULL_SELECTION);
		
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		conceptsTable.getTable().setLayoutData(gd);
		
	}


	private void hookListeners() {
		getSite().getPage().addSelectionListener(this);
	}

	private void unhookListeners() {
		getSite().getPage().removeSelectionListener(this);
	}
	
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		Object res = null;
		
		if(part instanceof AnnotatedDocumentEditor){
			res = ((StructuredSelection) selection).getFirstElement();
		}
		
		if(res==null){
			return;
		}
		
		if(res instanceof AnnotationFS){
			conceptsTable.setInput(res);
		}
		else{
			this.setPartName("No information to display");
		}
	}


	@Override
	public void dispose() {
		unhookListeners();
		super.dispose();
	}

	@Override
	public void setFocus() {
	}

	@Override
	protected void refreshView() {
		// TODO Auto-generated method stub
		
	}
}
