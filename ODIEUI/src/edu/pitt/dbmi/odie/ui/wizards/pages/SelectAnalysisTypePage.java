package edu.pitt.dbmi.odie.ui.wizards.pages;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.Bullet;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.dbmi.odie.ui.Aesthetics;

public class SelectAnalysisTypePage extends WizardPage {

	private static final String OTHER_LABEL = "Any other UIMA Pipeline";
	private static final String OE_LABEL = "Ontology Enrichment";
	private static final String NER_LABEL = "Named Entity Recognition";
	public static final String PAGE_NAME = "SelectAnalysisTypePage";
	private static final String initDesc = "Select the type of UIMA analysis engine you want to run";
	protected static final String NER_DESCRIPTION = "Annotate a document set with one or more ontologies.\n\n"
			+ "You can...\n"
			+ "Determine the best ontologies to annotate a document set.\n"
			+ "Search the document set using concepts from an ontology.\n"
			+ "Inspect the performance of the NER algorithm in use.";

	protected static final String OE_DESCRIPTION = "Enrich an ontology and improve annotation of a document set.\n\n"
			+ "You can...\n"
			+ "Create a list of suggestions for new concepts for an ontology.\n"
			+ "Create local extensions to the ontology using the suggestions.\n"
			+ "Use local extensions to annotate the document set.";

	protected static final String OTHER_DESCRIPTION = "Run any valid UIMA analysis engine.\n\n"
			+ "You can... \n"
			+ "View the annotated document and the list of annotations";

	public SelectAnalysisTypePage() {
		super(PAGE_NAME);
		setTitle("Select");
		setDescription(initDesc);
	}

	SelectionListener radioSelectionListener = new SelectionListener() {

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			handleEvent(e);
		}

		private void handleEvent(SelectionEvent e) {
			selectedType = (String) ((Button) e.getSource()).getData();
			updateDescription(selectedType);
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			handleEvent(e);
		}

	};
	private String selectedType = ODIEConstants.AE_TYPE_NER;
	private StyledText descriptionField;

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
		gridLayout.verticalSpacing = Aesthetics.INTRAGROUP_SPACING;
		gridLayout.numColumns = 1;
		container.setLayout(gridLayout);
		setControl(container);

		Button button = new Button(container, SWT.RADIO);
		button.setText(NER_LABEL);
		button.setData(ODIEConstants.AE_TYPE_NER);
		button.addSelectionListener(radioSelectionListener);

		// default select NER
		button.setSelection(true);
		setSelectedType(ODIEConstants.AE_TYPE_NER);

		button = new Button(container, SWT.RADIO);
		button.setData(ODIEConstants.AE_TYPE_OE);
		button.setText(OE_LABEL);
		button.addSelectionListener(radioSelectionListener);

		button = new Button(container, SWT.RADIO);
		button.setData(ODIEConstants.AE_TYPE_OTHER);
		button.setText(OTHER_LABEL);
		button.addSelectionListener(radioSelectionListener);

		Label descriptionLabel = new Label(container, SWT.NONE);
		descriptionLabel.setText("Description:");
		GridData gd = new GridData();
		gd.verticalIndent = Aesthetics.INTERGROUP_SPACING;
		descriptionLabel.setLayoutData(gd);

		descriptionField = new StyledText(container, SWT.WRAP | SWT.READ_ONLY
				| SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		gd.heightHint = 150;
		gd.verticalIndent = 0;
		descriptionField.setLayoutData(gd);

		// text for default selection(NER)
		updateDescription(ODIEConstants.AE_TYPE_NER);

	}

	protected void setSelectedType(String type) {
		selectedType = type;
	}

	private void updateDescription(String type) {
		StyleRange style0 = new StyleRange();
		style0.metrics = new GlyphMetrics(0, 0, 20);
		Bullet bullet = new Bullet(style0);

		if (type.equals(ODIEConstants.AE_TYPE_NER)) {
			descriptionField.setText(NER_DESCRIPTION);
			descriptionField.setLineBullet(3, 1, bullet);
			descriptionField.setLineBullet(4, 1, bullet);
			descriptionField.setLineBullet(5, 1, bullet);
		} else if (type.equals(ODIEConstants.AE_TYPE_OE)) {
			descriptionField.setText(OE_DESCRIPTION);
			descriptionField.setLineBullet(3, 1, bullet);
			descriptionField.setLineBullet(4, 1, bullet);
			descriptionField.setLineBullet(5, 1, bullet);
		} else if (type.equals(ODIEConstants.AE_TYPE_OTHER)) {
			descriptionField.setText(OTHER_DESCRIPTION);
			descriptionField.setLineBullet(3, 1, bullet);
		}
	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell parent = new Shell(display);
		parent.setLayout(new FillLayout());

		SelectAnalysisTypePage p = new SelectAnalysisTypePage();

		Wizard wiz = new Wizard() {

			@Override
			public void addPages() {
				setWindowTitle(PAGE_NAME);

				SelectAnalysisTypePage propPage = new SelectAnalysisTypePage();
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

	public String getSelectedType() {
		return selectedType;
	}

}
