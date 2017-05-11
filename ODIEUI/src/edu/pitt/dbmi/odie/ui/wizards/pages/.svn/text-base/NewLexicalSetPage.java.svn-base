package edu.pitt.dbmi.odie.ui.wizards.pages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.model.LexicalSet;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.dbmi.odie.ui.editors.providers.OntologyTreeLabelProvider;
import edu.pitt.dbmi.odie.ui.wizards.ImportOntologiesWizard;
import edu.pitt.dbmi.odie.ui.wizards.providers.AllOntologiesTreeContentProvider;
import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IOntology;
import edu.pitt.ontology.IResource;

public class NewLexicalSetPage extends WizardPage {

	public static final String PAGE_NAME = "NewLexicalSetPage";
	private static final String initDesc = "Create a new vocabulary from ontologies.";
	private static final String nameEmpty = "Enter a name for the new vocabulary";
	private static final String nameInUse = "A vocabulary with that name already exists. Please enter another name.";
	private static String incorrectOntologySelection;

	Text nameField;
//	Text descriptionField;
	private List<String> existingNames;
	private CheckboxTreeViewer ontologyTree;
	private Button importButton;

	public NewLexicalSetPage() {
		super(PAGE_NAME);
		setTitle("New Vocabulary");
		setDescription(initDesc);

		loadExistingOntologySetNames();

	}

	private void loadExistingOntologySetNames() {
		existingNames = Activator.getDefault().getMiddleTier()
				.getLexicalSetNames();

	}

	protected boolean validatePage() {
		if (nameField == null)
			return false;
		if (nameField.getText().trim().length() == 0) {
			setErrorMessage(null);
			setMessage(NewLexicalSetPage.nameEmpty);
			return false;
		}
		if (existingNames.contains(nameField.getText())) {
			setErrorMessage(NewLexicalSetPage.nameInUse);
			return false;
		} else
			setErrorMessage(null);

		if (ontologyTree.getCheckedElements().length <= 0) {
			setMessage("Select at least one ontology/class");
			return false;
		}

		if (hasInvalidOntologySelections()) {
			setErrorMessage(NewLexicalSetPage.incorrectOntologySelection);
			return false;
		}
		Object[] sel = ontologyTree.getCheckedElements();
		int count = 0;
		for (int i = 0; i < sel.length; i++) {
			if (!ontologyTree.getGrayed(sel[i]))
				count++;
		}
		setMessage(count + " elements selected ");
		return true;

	}

	private boolean hasInvalidOntologySelections() {
		List<IOntology> parentOlist = new ArrayList<IOntology>();
		List<IOntology> olist = new ArrayList<IOntology>();

		for (Object o : ontologyTree.getCheckedElements()) {
			if (o instanceof IClass)
				parentOlist.add(((IClass) o).getOntology());
			else if (o instanceof IOntology)
				olist.add((IOntology) o);
		}

		if (olist.isEmpty() || parentOlist.isEmpty())
			return false;
		else {
			for (IOntology o : olist) {
				if (parentOlist.contains(o))
					return false;
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);

		GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = Aesthetics.INTERGROUP_SPACING;
		gridLayout.numColumns = 2;
		gridLayout.verticalSpacing = 2;
		container.setLayout(gridLayout);

		Label label = new Label(container, SWT.NONE);
		label.setText("Name:");

		nameField = new Text(container, SWT.BORDER);
		nameField.setTextLimit(ODIEConstants.MAX_DB_NAME_LENGTH
				- ODIEConstants.LEXSET_PREFIX.length());
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		nameField.setLayoutData(gd);

//		label = new Label(container, SWT.NONE);
//		label.setText("Description:");
//		gd = new GridData();
//		gd.horizontalSpan = 2;
//		gd.horizontalAlignment = GridData.BEGINNING;
//		gd.verticalIndent = 2;
//		label.setLayoutData(gd);
//
//		descriptionField = new Text(container, SWT.MULTI | SWT.WRAP
//				| SWT.BORDER);
//		gd = new GridData();
//		gd.grabExcessHorizontalSpace = true;
//		gd.horizontalSpan = 2;
//		gd.horizontalAlignment = GridData.FILL;
//		gd.heightHint = 100;
//		descriptionField.setLayoutData(gd);

		// label = new Label(container, SWT.NONE);
		// label.setText("Ontologies:");
		// gd = new GridData();
		// gd.horizontalSpan=2;
		// gd.horizontalAlignment = GridData.BEGINNING;
		// gd.verticalIndent = 2;
		// label.setLayoutData(gd);

		// IOntology list

		// AE List Group
		Group aelistGrp = new Group(container, SWT.NONE);
		aelistGrp.setText("Ontologies");
		GridLayout gl = new GridLayout();
		gl.verticalSpacing = Aesthetics.INTRAGROUP_SPACING;
		aelistGrp.setLayout(gl);

		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = true;
		gd.minimumHeight = 100;
		aelistGrp.setLayoutData(gd);

		gl = new GridLayout();
		gl.numColumns = 2;
		aelistGrp.setLayout(gl);
		ontologyTree = new CheckboxTreeViewer(aelistGrp);

		ontologyTree.setContentProvider(new AllOntologiesTreeContentProvider());
		ontologyTree.setLabelProvider(new OntologyTreeLabelProvider());
		ontologyTree.setInput("root"); // pass a non-null that will be ignored

		gd = new GridData();
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = true;
		gd.verticalSpan = 2;
		ontologyTree.getTree().setLayoutData(gd);

		// add/remove buttons
		gd = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);
		importButton = new Button(aelistGrp, SWT.PUSH);
		importButton.setText("Import...");
		importButton.setLayoutData(gd);

		// gd = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);
		// removeButton = new Button(aelistGrp,SWT.PUSH);
		// removeButton.setText("Remove");
		// removeButton.setLayoutData(gd);
		// removeButton.setEnabled(false);
		//	    

		hookListeners();

		setPageComplete(validatePage());

		setControl(container);
	}

	/**
	 * 
	 */
	private void hookListeners() {
		importButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				launchWizard();

			}

			private void launchWizard() {
				ImportOntologiesWizard wizard = new ImportOntologiesWizard(
						false);
				// Instantiates the wizard container with the wizard and opens
				// it
				WizardDialog dialog = new WizardDialog(getShell(), wizard);
				dialog.create();
				dialog.open();
				ontologyTree.refresh();
			}

		});

		nameField.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});
		nameField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				nameField.selectAll();
			}

		});

		nameField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				setPageComplete(validatePage());
			}
		});

		ontologyTree.addTreeListener(new ITreeViewerListener() {
			
			@Override
			public void treeExpanded(TreeExpansionEvent event) {
				Object element = event.getElement();
				boolean checked = ontologyTree.getChecked(element);
				boolean grayed = ontologyTree.getGrayed(element);
				if(!grayed && checked){
					updateChildCheckStateIfExpanded(element,checked,true);
				}
			}
			
			@Override
			public void treeCollapsed(TreeExpansionEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
		
		ontologyTree.addCheckStateListener(new ICheckStateListener() {

			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				Object o = event.getElement();
				updateState(o,event.getChecked());
				setPageComplete(validatePage());
			}

			private void updateState(Object element, boolean checked) {
				ontologyTree.setGrayed(element, false);
				updateChildCheckState(element,checked);
				updateParentCheckState(element,checked);
			}

			private void updateParentCheckState(Object element, boolean checked) {
				if(element instanceof IOntology){
					return;
				}
				IClass cls = ((IClass)element);
				IOntology o = cls.getOntology();
				
				boolean oChecked = ontologyTree.getChecked(o);
				boolean oGrayed = ontologyTree.getGrayed(o);
				
				if(oChecked && !oGrayed && !checked)
					ontologyTree.setGrayed(o, true);
				else if(!oChecked && checked){
					ontologyTree.setChecked(o, true);
					ontologyTree.setGrayed(o, true);
				}
					
//				if(oChecked && !checked){
//					ontologyTree.setGrayed(o, true);
//				}
				for(IClass c:cls.getDirectSuperClasses()){
					boolean parentChecked = ontologyTree.getChecked(c);
					boolean parentGrayed = ontologyTree.getGrayed(c);
					if(parentChecked && !parentGrayed && !checked){
						ontologyTree.setGrayed(c, true);
					}
					else if(!parentChecked && checked){
						ontologyTree.setChecked(c, true);
						ontologyTree.setGrayed(c, true);
					}
					
					parentChecked = ontologyTree.getChecked(c);
					updateParentCheckState(c,parentChecked);
				}
			}

		});
	}

	private void updateChildCheckState(Object element,
			boolean checked) {
		boolean expanded = ontologyTree.getExpandedState(element);
		updateChildCheckStateIfExpanded(element, checked, expanded);
	}

	private void updateChildCheckStateIfExpanded(Object element, boolean checked,
			boolean expanded) {
		if(!expanded)
			return;
		
		IClass[] classes = null;
		if(element instanceof IOntology){
			classes = ((IOntology)element).getRootClasses();
		}
		else if(element instanceof IClass){
			classes = ((IClass)element).getDirectSubClasses();
		}
		else
			return;
		
		if(expanded){
			for(IClass cls:classes){
				ontologyTree.setChecked(cls, checked);
				updateChildCheckState(cls, checked);
			}
		}
	}
	public static void main(String[] args) {
		Display display = new Display();
		Shell parent = new Shell(display);
		parent.setLayout(new FillLayout());

		NewLexicalSetPage p = new NewLexicalSetPage();

		Wizard wiz = new Wizard() {

			@Override
			public void addPages() {
				setWindowTitle("Wizard Test");

				NewLexicalSetPage propPage = new NewLexicalSetPage();
				addPage(propPage);
			}

			@Override
			public boolean performFinish() {
				// TODO Auto-generated method stub
				return false;
			}

		};

		WizardDialog d = new WizardDialog(parent, wiz);
		d.open();
		while (!parent.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();

		display.dispose();
	}

	public List<IResource> getSelectedResources() {
		Object[] sel = ontologyTree.getCheckedElements();
		List<IResource> olist = new ArrayList<IResource>();

		for (int i = 0; i < sel.length; i++)
			if (!ontologyTree.getGrayed(sel[i]))
				olist.add((IResource) sel[i]);

		return olist;
	}

	public String getName() {
		return nameField.getText();
	}

	public String getDescription() {
		return null;
//		return descriptionField.getText();
	}
}
