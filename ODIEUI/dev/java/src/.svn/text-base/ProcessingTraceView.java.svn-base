package edu.pitt.dbmi.odie.ui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

import edu.pitt.dbmi.odie.ui.Aesthetics;

public class ProcessingTraceView extends ViewPart {

	public static final String ID = "edu.pitt.dbmi.odie.ui.views.ProcessingTraceView";
	public ProcessingTraceView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		doParentLayoutAndAesthetics(parent);
		Label l = new Label(parent,SWT.NONE);
		l.setText("This view will show a processing trace for the currently selected annotation");

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
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
