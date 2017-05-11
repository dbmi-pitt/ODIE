/**
 * 
 */
package edu.pitt.dbmi.odie.ui.wizards.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.dbmi.odie.ui.views.StickyNotePanel;

/**
 * @author Girish Chavan
 * 
 */
public class SelectOntologySourcePage extends WizardPage {

	private static final String STICKY_NOTE_CONTENT = "This feature is not implemented yet.\n"
			+ "In future versions of ODIE you will be able to import ontologies "
			+ "from files or from BioPortal.";
	public static final String PAGE_NAME = "SelectImportOntologySourcePage";
	public static final String FILE_IMPORT_OPTION = "File System";
	public static final String BIOPORTAL_IMPORT_OPTION = "BioPortal";

	private String selectedOption;

	public SelectOntologySourcePage() {
		super(PAGE_NAME);
		setTitle("Select");
		setDescription("Select from where to import");

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
		GridLayout gl = new GridLayout();
		gl.numColumns = 1;
		container.setLayout(gl);

		addImportSourcesGroup(container);
		// addStickyNote(container);
		setControl(container);
	}

	private void addStickyNote(Composite container) {
		StickyNotePanel stickyNotePanel = new StickyNotePanel(container,
				SWT.NONE);
		stickyNotePanel.setText(STICKY_NOTE_CONTENT);
		GridData gd = new GridData();
		gd.verticalAlignment = SWT.LEFT;
		gd.horizontalAlignment = SWT.TOP;
		gd.grabExcessVerticalSpace = false;
		gd.grabExcessHorizontalSpace = false;

		gd.widthHint = 425;

		stickyNotePanel.setLayoutData(gd);

	}

	private Group addImportSourcesGroup(Composite container) {
		Group importOptionsGrp = new Group(container, SWT.NONE);
		importOptionsGrp.setText("Import Sources");
		GridData gd = new GridData();
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessVerticalSpace = false;
		gd.grabExcessHorizontalSpace = true;
		importOptionsGrp.setLayoutData(gd);

		RowLayout rl = new RowLayout();
		rl.spacing = Aesthetics.INTRAGROUP_SPACING;
		rl.type = SWT.VERTICAL;
		importOptionsGrp.setLayout(rl);

		SelectionListener listener = new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				saveSelection((String) ((Button) e.getSource()).getData());

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				saveSelection((String) ((Button) e.getSource()).getData());

			}

			private void saveSelection(String s) {
				selectedOption = s;

			}
		};

		Button tButton = new Button(importOptionsGrp, SWT.RADIO);
		tButton.setText(FILE_IMPORT_OPTION);
		tButton.setData(FILE_IMPORT_OPTION);
		tButton.addSelectionListener(listener);

		tButton = new Button(importOptionsGrp, SWT.RADIO);
		tButton.addSelectionListener(listener);
		tButton.setText(BIOPORTAL_IMPORT_OPTION);
		tButton.setData(BIOPORTAL_IMPORT_OPTION);

		return importOptionsGrp;
	}

	public String getSelectedOption() {
		return selectedOption;
	}
	// public void setSelectedOption(String selectedOption) {
	// this.selectedOption = selectedOption;
	// }

}
