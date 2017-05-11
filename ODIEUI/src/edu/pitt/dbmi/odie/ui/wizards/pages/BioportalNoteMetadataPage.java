package edu.pitt.dbmi.odie.ui.wizards.pages;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.pitt.dbmi.odie.middletier.BioportalNote;
import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.dbmi.odie.ui.UIConstants;
import edu.pitt.dbmi.odie.ui.views.ODIEMessages;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class BioportalNoteMetadataPage extends WizardPage {

	public static final String PAGE_NAME = "BioportalNoteMetadataPage";
	private static final String initDesc = "Provide reasons for new proposal, author information";
	private static final String requiredMissing = "All required fields(shown in bold) must be filled";
	private static final String ALL_OK_DESC = "Click Finish to submit the Bioportal note.";
	
	Text reasonsField;
	Text nameField;
	Text contactField;

	private BioportalNote note;
	private Text subjectField;

	
	public BioportalNoteMetadataPage(BioportalNote note) {
		super(PAGE_NAME);
		setTitle("New Concept Proposal for ontology");
		setDescription(initDesc);
		this.note = note;
		
	}

	protected boolean validatePage() {
		if (nameField == null)
			return false;
		if (nameField.getText().trim().length() == 0 || 
				reasonsField.getText().trim().length() == 0 || 
				contactField.getText().trim().length() == 0) {
			setErrorMessage(null);
			setMessage(BioportalNoteMetadataPage.requiredMissing);
			return false;
		}else {
			note.setContactInfo(contactField.getText());
			note.setReasonForChange(reasonsField.getText());
			note.setAuthorID(nameField.getText());
			note.setSubject(subjectField.getText());
			
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
		label.setText("Subject");
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.BEGINNING;
		label.setLayoutData(gd);

		label = new Label(container, SWT.NONE);
		GeneralUtils.installBoldFont(label);
		label.setText("Bioportal Userid");
		gd = new GridData();
		gd.horizontalAlignment = GridData.BEGINNING;
		label.setLayoutData(gd);
		
		subjectField = new Text(container, SWT.BORDER);
		subjectField.setTextLimit(250);
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.verticalAlignment = GridData.FILL;
		subjectField.setLayoutData(gd);
		
		nameField = new Text(container, SWT.BORDER);
		nameField.setTextLimit(250);
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.verticalAlignment = GridData.FILL;
		nameField.setLayoutData(gd);

		label = new Label(container, SWT.NONE);
		GeneralUtils.installBoldFont(label);
		label.setText("Reasons For Change");
		gd = new GridData();
		gd.horizontalAlignment = GridData.BEGINNING;
		label.setLayoutData(gd);
		
		label = new Label(container, SWT.NONE);
		GeneralUtils.installBoldFont(label);
		label.setText("Email Address");
		gd = new GridData();
		gd.horizontalAlignment = GridData.BEGINNING;
		gd.verticalIndent = Aesthetics.INTRAGROUP_SPACING;
		label.setLayoutData(gd);

		reasonsField = new Text(container, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.verticalAlignment = GridData.FILL;
		gd.heightHint = 100;
		reasonsField.setLayoutData(gd);
		
		contactField = new Text(container,SWT.BORDER);
		
		gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = SWT.BEGINNING;
		contactField.setLayoutData(gd);

		hookListeners();
		subjectField.setText(ODIEMessages.NEW_BIOPORTALNOTE_SUBJECT);
		loadNote();
		setPageComplete(validatePage());
	}

	private void loadNote() {
		nameField.setText(GeneralUtils.nullSafe(note.getAuthorID()));
		contactField.setText(GeneralUtils.nullSafe(note.getContactInfo()));
		reasonsField.setText(GeneralUtils.nullSafe(note.getReasonForChange()));
	}

	/**
	 * 
	 */
	private void hookListeners() {
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

		reasonsField.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});

		reasonsField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				reasonsField.selectAll();
			}

		});

		contactField.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});

		contactField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				contactField.selectAll();
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

				BioportalNoteMetadataPage propPage = new BioportalNoteMetadataPage(null);
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
}
