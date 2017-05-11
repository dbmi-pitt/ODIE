package edu.pitt.dbmi.odie.ui.wizards.pages;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.pitt.dbmi.odie.middletier.BioportalNote;
import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.dbmi.odie.ui.dialogs.OntologyClassPickerDialog;
import edu.pitt.dbmi.odie.ui.editors.analysis.AnalysisEditorInput;
import edu.pitt.dbmi.odie.ui.views.ODIEMessages;
import edu.pitt.dbmi.odie.utils.GeneralUtils;
import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IOntology;
import edu.pitt.ontology.IOntologyException;

public class BioportalNewProposalNotePage extends WizardPage {

	public static final String PAGE_NAME = "BioportalNewProposalNotePage";
	private static final String initDesc = "Provide some information for the new concept proposal";
	private static final String requiredMissing = "All required fields(shown in bold) must be filled";
	private static final String ALL_OK_DESC = "Click Next";
	
	Text nameField;
	Text definitionField;
	Text synonymsField;
	Text parentField;

	private BioportalNote note;
	private Button changeParentButton;
	private IOntology selectedOntology;
	
	public BioportalNewProposalNotePage(BioportalNote note) {
		super(PAGE_NAME);
		setTitle("New Concept Proposal for ontology");
		setDescription(initDesc);
		this.note = note;
		
	}

	public void setSelectedOntology(IOntology selectedOntology) {
		this.selectedOntology = selectedOntology;
	}

	protected boolean validatePage() {
		if (nameField == null)
			return false;
		if (nameField.getText().trim().length() == 0 || 
				definitionField.getText().trim().length() == 0 || parentField.getText().equals(ODIEMessages.NO_PARENT_SET)) {
			setErrorMessage(null);
			setMessage(BioportalNewProposalNotePage.requiredMissing);
			return false;
		}else {
			note.setNewTermDefinition(definitionField.getText());
			note.setNewTermPreferredName(nameField.getText());
			note.setNewTermSynonyms(synonymsField.getText());
			
			setErrorMessage(null);
			setMessage(ALL_OK_DESC, DialogPage.INFORMATION);

			return true;
		}

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
		GridLayout gridLayout = new GridLayout(2,false);
		gridLayout.verticalSpacing = 2;
		gridLayout.horizontalSpacing = Aesthetics.INTERGROUP_SPACING;
		container.setLayout(gridLayout);
		setControl(container);
		Label label = new Label(container, SWT.NONE);
		GeneralUtils.installBoldFont(label);
		label.setText("Preferred Name");
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.BEGINNING;
		label.setLayoutData(gd);

		label = new Label(container, SWT.NONE);
		GeneralUtils.installBoldFont(label);
		label.setText("Parent");
		gd = new GridData();
		gd.horizontalAlignment = GridData.BEGINNING;
		label.setLayoutData(gd);
		
		nameField = new Text(container, SWT.BORDER);
		nameField.setTextLimit(250);
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.verticalAlignment = GridData.FILL;
		nameField.setLayoutData(gd);

		Composite parentConceptComposite = new Composite(container, SWT.NONE);
		gridLayout = new GridLayout(2,false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 2;
		
		parentConceptComposite.setLayout(gridLayout);
		
		
		gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.BEGINNING;
		parentConceptComposite.setLayoutData(gd);

		parentField = new Text(parentConceptComposite, SWT.BORDER);
		parentField.setEditable(false);
		gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.FILL;
		parentField.setLayoutData(gd);
		
		changeParentButton = new Button(parentConceptComposite,SWT.PUSH);
		changeParentButton.setText("Change...");
		gd = new GridData();
		gd.verticalAlignment = GridData.BEGINNING;
		changeParentButton.setLayoutData(gd);
		
		label = new Label(container, SWT.NONE);
		GeneralUtils.installBoldFont(label);
		label.setText("Definition");
		gd = new GridData();
		gd.horizontalAlignment = GridData.BEGINNING;
		gd.verticalIndent = Aesthetics.INTRAGROUP_SPACING;
		label.setLayoutData(gd);

		label = new Label(container, SWT.NONE);
		label.setText("Synonyms (comma separated list)");
		gd = new GridData();
		gd.horizontalAlignment = GridData.BEGINNING;
		gd.verticalIndent = Aesthetics.INTRAGROUP_SPACING;
		label.setLayoutData(gd);
		
		definitionField = new Text(container, SWT.MULTI | SWT.WRAP
				| SWT.BORDER | SWT.V_SCROLL);
		
		gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		gd.heightHint = 100;
		gd.verticalIndent = 0;
		definitionField.setLayoutData(gd);

		synonymsField = new Text(container, SWT.MULTI | SWT.WRAP
				| SWT.BORDER | SWT.V_SCROLL);
		
		gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		gd.heightHint = 50;
		gd.verticalIndent = 0;
		gd.verticalAlignment = GridData.BEGINNING;
		synonymsField.setLayoutData(gd);
		
		hookListeners();
		loadNote();
		setPageComplete(validatePage());
	}

	private void loadNote() {
		nameField.setText(GeneralUtils.nullSafe(note.getNewTermPreferredName()));
		definitionField.setText(GeneralUtils.nullSafe(note.getNewTermDefinition()));
		synonymsField.setText(GeneralUtils.nullSafe(note.getNewTermSynonyms()));
		
		if(note.getNewTermParent()!=null)
			parentField.setText(note.getNewTermParent().getName());
		else
			parentField.setText(ODIEMessages.NO_PARENT_SET);
		
	}

	/**
	 * 
	 */
	private void hookListeners() {
		changeParentButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				OntologyClassPickerDialog d = new OntologyClassPickerDialog(
						Activator.getDefault().getWorkbench().getDisplay()
								.getActiveShell(), new IOntology[]{selectedOntology});
				d.setBlockOnOpen(true);

				// Open the main window
				if (d.open() == IDialogConstants.OK_ID) {
					IClass parentClass = d.getSelectedClass();
					
					Activator.getDefault().getMiddleTier().addParent(note.getNewConceptClass(),
							parentClass);
					try {
						note.getNewConceptClass().getOntology().save();
					} catch (IOntologyException e1) {
						e1.printStackTrace();
					}
					parentField.setText(parentClass.getName());
					note.setNewTermParent(parentClass);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

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

		definitionField.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});

		definitionField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				definitionField.selectAll();
			}

		});


	}

	protected String getClassIDFromBioportal() {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell parent = new Shell(display);
		parent.setLayout(new FillLayout());

		Wizard wiz = new Wizard() {

			@Override
			public void addPages() {
				setWindowTitle("New Document Coding Analysis");

				BioportalNewProposalNotePage propPage = new BioportalNewProposalNotePage(null);
				addPage(propPage);
			}

			@Override
			public boolean performFinish() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean performCancel() {
				this.dispose();
				return true;
			}

		};

		WizardDialog d = new WizardDialog(parent, wiz);


		d.open();
		
		while (!parent.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();

		display.dispose();
		
	}

	/**
	 * @return
	 */
	public String getName() {
		return nameField.getText();
	}

	public String getDescription() {
		return definitionField.getText();
	}

}
