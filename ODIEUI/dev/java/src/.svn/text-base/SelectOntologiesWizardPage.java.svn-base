package edu.pitt.dbmi.odie.ui.wizards.pages;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import edu.pitt.dbmi.odie.model.LanguageResource;
import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.dbmi.odie.ui.wizards.ImportOntologiesWizard;
import edu.pitt.dbmi.odie.ui.wizards.providers.LanguageResourceTreeContentProvider;
import edu.pitt.dbmi.odie.ui.wizards.providers.LanguageResourceTreeLabelProvider;
import edu.pitt.dbmi.odie.ui.workers.OntologyLoader;
import edu.pitt.ontology.IOntology;

public class SelectOntologiesWizardPage extends WizardPage {

	public static final String PAGE_NAME = "SelectOntologiesWizardPage";
	private boolean forEnrichment = false;
	private static final String NAME_HEADER = "Name: ";
	private static final String DESC_HEADER = "Description: ";
	private static final String EOLIST_HEADER = "Enriched Ontologies (automatically added):";
	
	private StyledText detailsTextBox;
	private CheckboxTreeViewer ontologyTree;
	private Button addButton;
	private Button remButton;

	public SelectOntologiesWizardPage(boolean forEnrichment){
		super("selectOntologies");
		this.forEnrichment = forEnrichment;
		setTitle("Ontologies");
		if(forEnrichment)
			setDescription("Select the ontologies to enrich");
		else
			setDescription("Select the ontologies to code with");
	}
	
	public void createControl(Composite parent) {
		Composite container = new Composite(parent,SWT.NULL);
		GridLayout gl = new GridLayout();
		gl.verticalSpacing = Aesthetics.VERTICAL_INTERGROUP_SPACING;
		gl.numColumns = 1;
		container.setLayout(gl);

		//Ontology List Group
		Group olistGrp = new Group(container,SWT.NONE);
		olistGrp.setText("Ontologies");
		gl = new GridLayout();
		gl.verticalSpacing = Aesthetics.VERTICAL_INTRAGROUP_SPACING;
		olistGrp.setLayout(gl);

		GridData gd = new GridData();
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = true;
		gd.minimumHeight = 100;
		olistGrp.setLayoutData(gd);
		
		gl = new GridLayout();
		gl.numColumns = 2;
		olistGrp.setLayout(gl);
		
		//IOntology list

		ScrolledComposite scroll = new ScrolledComposite(olistGrp,SWT.H_SCROLL | SWT.V_SCROLL);
		ontologyTree = new CheckboxTreeViewer(scroll);
		

	    
	    ontologyTree.setContentProvider(new LanguageResourceTreeContentProvider());
	    ontologyTree.setLabelProvider(new LanguageResourceTreeLabelProvider());
	    ontologyTree.setInput("root"); // pass a non-null that will be ignored
		gd = new GridData();
		gd.verticalSpan = 2;
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = true;
		scroll.setLayoutData(gd);
	    scroll.setContent(ontologyTree.getControl());
	    scroll.setMinSize(100,100);
	    scroll.setExpandHorizontal(true);
	    scroll.setExpandVertical(true);
		
	    
	    //add/remove buttons
	    gd = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);
	    addButton = new Button(olistGrp,SWT.PUSH);
	    addButton.setText("Import...");
	    addButton.setLayoutData(gd);
	    
	    
	    
	    
	    
	    gd = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);
	    remButton = new Button(olistGrp,SWT.PUSH);
	    remButton.setText("Remove");
	    remButton.setLayoutData(gd);
	    
	    ///////// Details group
	    Label detailsGrp = new Label(olistGrp,SWT.NONE);
	    detailsGrp.setText("Description:");

	    detailsTextBox = new StyledText(olistGrp, SWT.WRAP | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
	    gd = new GridData();
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalAlignment = GridData.FILL;
		gd.horizontalSpan = 2;
		gd.minimumHeight = 50;
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = true;
		detailsTextBox.setLayoutData(gd);
		detailsTextBox.setText("Select an ontology above to see its details");

		
		hookListeners();
		setControl(container);
		
		setPageComplete(validatePage());
	}
	
	private void hookListeners() {
		ontologyTree.addPostSelectionChangedListener(new ISelectionChangedListener(){
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Object firstSelection = ((StructuredSelection)event.getSelection()).getFirstElement();
				
				if(firstSelection instanceof LanguageResource){
					updateDetailsBox((LanguageResource) firstSelection);
				}
			}
			
			private void updateDetailsBox(LanguageResource o) {
				
				if(!(o.getResource() instanceof IOntology))
					return;
				
				if(!loadOntology((IOntology) o.getResource())){
					setErrorMessage("Error loading '" + o.getName() + "' ontology");
					detailsTextBox.setText(getErrorMessage());
					return;
				}
				
				StringBuffer text = buildDescriptionFrom(o);
				
				detailsTextBox.setText(text.toString());
				
				applyStylesToText();
			}

			private StringBuffer buildDescriptionFrom(LanguageResource o) {
				StringBuffer text = new StringBuffer();

				text.append(NAME_HEADER + o.getName());
				text.append("\n");
				text.append(DESC_HEADER + o.getResource().getDescription());
				
				if(((IOntology) o.getResource()).getImportedOntologies().length !=0){
					text.append("\n");
					text.append(EOLIST_HEADER + "\n");
					
					int i=0;
					for(;i<((IOntology) o.getResource()).getImportedOntologies().length-1;i++){
						text.append(((IOntology) o.getResource()).getImportedOntologies()[i].getName() + ", ");
					}
					text.append(((IOntology) o.getResource()).getImportedOntologies()[i].getName());
				}
				return text;
			}

			private boolean loadOntology(IOntology o) {
				if(o.isLoaded())
					return true;
				
				try {
					getWizard().getContainer().run(true, false, new OntologyLoader(o));
					return true;
				} catch (InvocationTargetException e) {
					e.printStackTrace();
					return false;
				} catch (InterruptedException e) {
					e.printStackTrace();
					return false;
				}
			}
			

			private void applyStylesToText() {
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
				
				start = s.indexOf(DESC_HEADER);
				if(start >= 0){
					sr = new StyleRange();
					sr.start = start;
					sr.length = DESC_HEADER.length();
					sr.fontStyle = SWT.BOLD;
					detailsTextBox.setStyleRange(sr);
				}
				
				start = s.indexOf(EOLIST_HEADER);
				if(start >= 0){
					sr = new StyleRange();
					sr.start = start;
					sr.length = EOLIST_HEADER.length();
					sr.fontStyle = SWT.BOLD;
					detailsTextBox.setStyleRange(sr);
				}
				
				
			}
		});
		
	    ontologyTree.addCheckStateListener(new ICheckStateListener(){

			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				setPageComplete(validatePage());
			}
	    	
	    });
	    
	    addButton.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				launchWizard();
				
			}
			
			private void launchWizard(){
				ImportOntologiesWizard wizard = new ImportOntologiesWizard(false);
			    // Instantiates the wizard container with the wizard and opens it
			    WizardDialog dialog = new WizardDialog(getShell(), wizard);
			    dialog.create();
			    dialog.open();
			}
	    	
	    });
		
	}

	protected boolean validatePage(){
		if(ontologyTree.getCheckedElements().length <= 0){
			setMessage("Select at least one ontology " + (forEnrichment?"for enrichment":"to process documents with"));
			return false;
		}
		else{
			setMessage(ontologyTree.getCheckedElements().length + " ontologies selected " + (forEnrichment?"for enrichment":"to process documents with"));
			return true;
		}
		
			
	}

	public List<LanguageResource> getSelectedOntologies() {
		if(ontologyTree==null)
			return new ArrayList<LanguageResource>();
		
		Object[] sel = ontologyTree.getCheckedElements();
		List<LanguageResource> olist = new ArrayList<LanguageResource>();
		
		for(int i=0;i<sel.length;i++)
			olist.add((LanguageResource)sel[i]);
		
		return olist;
	}

}
