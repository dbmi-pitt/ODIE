package edu.pitt.dbmi.odie.ui.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import edu.pitt.dbmi.odie.model.Datapoint;
import edu.pitt.dbmi.odie.model.Suggestion;
import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IOntology;

public class DetailsView extends ViewPart implements ISelectionListener {

	public static final String ID = "edu.pitt.dbmi.odie.ui.views.DetailsView";

	private static final String NAME_HEADER = "Name: ";
	private static final String SUGGESTED_NAME_HEADER = "Suggested Name: ";
	private static final String DESC_HEADER = "Description: ";
	private static final String SOURCE_HEADER = "Ontology: ";
	private static final String PARENT_HEADER = "Parents: ";
	private static final String CHILD_HEADER = "Children: ";
	
	private static final String NO_CLASS_SELECTED_MSG = "Select an item from the list above to see its details.";

	private StyledText detailsTextBox;
	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
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
		detailsTextBox = new StyledText(parent,SWT.V_SCROLL | SWT.WRAP);
		detailsTextBox.setText(NO_CLASS_SELECTED_MSG);
		detailsTextBox.setEnabled(false);
		detailsTextBox.setBackground(Aesthetics.getDisabledBackground());
		detailsTextBox.setEditable(false);
		
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		detailsTextBox.setLayoutData(gd);
	}


	private void hookListeners() {
		getSite().getPage().addSelectionListener(ConceptsView.ID, this);
		getSite().getPage().addSelectionListener(AnnotationsView.ID, this);
		getSite().getPage().addSelectionListener(SuggestionsView.ID, this);
	}

	private void unhookListeners() {
		getSite().getPage().removeSelectionListener(ConceptsView.ID, this);
		getSite().getPage().removeSelectionListener(AnnotationsView.ID, this);
		getSite().getPage().removeSelectionListener(SuggestionsView.ID, this);
	}
	
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		Object res = null;
		
		if(part instanceof ConceptsView){
			Datapoint dp = ((Datapoint) ((StructuredSelection) selection)
					.getFirstElement());
			
			if(dp==null || dp.getConceptClass()==null){
				res = null;
			}
			else
				res = dp.getConceptClass();
		}
		else if(part instanceof AnnotationsView){
			
			res = ((StructuredSelection) selection).getFirstElement();
		}
		if(part instanceof SuggestionsView){
			Suggestion sugg = ((Suggestion) ((StructuredSelection) selection)
					.getFirstElement());
			
			res = sugg;
		}
		
		
		if(res==null){
			disableTextbox();
			return;
		}
		
		detailsTextBox.setBackground(Aesthetics.getEnabledBackground());
		detailsTextBox.setEnabled(true);
		
		StringBuffer text;
		if(res instanceof IClass){
			this.setPartName("Concept Details");
			text = getClassDetails((IClass)res);
		}
		else if(res instanceof IOntology){
			this.setPartName("Ontology Details");
			text = getOntologyDetails((IOntology)res);
		}
		else if(res instanceof Suggestion){
			this.setPartName("Suggestion Details");
			text = getSuggestionDetails((Suggestion)res);
		}
		else{
			this.setPartName("Details");
			text = new StringBuffer("No information to display.");
		}
			
		detailsTextBox.setText(text.toString());
		
		applyStyles();
		
	}


	private StringBuffer getSuggestionDetails(Suggestion sugg) {
		StringBuffer text = new StringBuffer();
		text.append(SUGGESTED_NAME_HEADER + sugg.getText());
		text.append("\n");
		return text;
	}


	private StringBuffer getOntologyDetails(IOntology o) {
		StringBuffer text = new StringBuffer();
		text.append(NAME_HEADER + o.getName());
		text.append("\n");
		text.append(DESC_HEADER + o.getDescription());
		text.append("\n");
		return text;
	}


	private StringBuffer getClassDetails(IClass c) {
		StringBuffer text = new StringBuffer();
		text.append(NAME_HEADER + c.getName());
		text.append("\n");
		text.append(DESC_HEADER + c.getDescription());
		text.append("\n");
		text.append(SOURCE_HEADER + c.getOntology().getName());
		text.append("\n");
		
		text.append(PARENT_HEADER);
		text.append("\n");
		for(IClass parent:c.getDirectSuperClasses()){
			text.append("\t"+ "> " + parent.getName());
			text.append("\n");
		}
		
		text.append(CHILD_HEADER);
		text.append("\n");
		for(IClass child:c.getDirectSubClasses()){
			text.append("\t"+ "> " +child.getName());
			text.append("\n");
		}
		return text;
	}
	
	/**
	 * 
	 */
	private void disableTextbox() {
		detailsTextBox.setText(NO_CLASS_SELECTED_MSG);
		detailsTextBox.setEnabled(false);
		detailsTextBox.setBackground(Aesthetics.getDisabledBackground());
		
	}


	private void applyStyles() {
		String s = detailsTextBox.getText();
		
		int start = s.indexOf(NAME_HEADER);
		StyleRange sr;
		if(start >= 0){
			sr = new StyleRange();
			sr.start = start;
			sr.length = NAME_HEADER.length();
			sr.fontStyle = SWT.BOLD;
			detailsTextBox.setStyleRange(sr);
		}
		
		start = s.indexOf(SUGGESTED_NAME_HEADER);
		if(start >= 0){
			sr = new StyleRange();
			sr.start = start;
			sr.length = DESC_HEADER.length();
			sr.fontStyle = SWT.BOLD;
			detailsTextBox.setStyleRange(sr);
		}
		
		start = s.indexOf(DESC_HEADER);
		if(start >= 0){
			sr = new StyleRange();
			sr.start = start;
			sr.length = DESC_HEADER.length();
			sr.fontStyle = SWT.BOLD;
			detailsTextBox.setStyleRange(sr);
		}
		
		start = s.indexOf(PARENT_HEADER);
		if(start >= 0){
			sr = new StyleRange();
			sr.start = start;
			sr.length = PARENT_HEADER.length();
			sr.fontStyle = SWT.BOLD;
			detailsTextBox.setStyleRange(sr);
		}
		
		start = s.indexOf(CHILD_HEADER);
		if(start >= 0){
			sr = new StyleRange();
			sr.start = start;
			sr.length = CHILD_HEADER.length();
			sr.fontStyle = SWT.BOLD;
			detailsTextBox.setStyleRange(sr);
		}

		start = s.indexOf(SOURCE_HEADER);
		if(start >= 0){
			sr = new StyleRange();
			sr.start = start;
			sr.length = SOURCE_HEADER.length();
			sr.fontStyle = SWT.BOLD;
			detailsTextBox.setStyleRange(sr);
		}
		
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {

	}


	@Override
	public void dispose() {
		unhookListeners();
		super.dispose();
	}

}