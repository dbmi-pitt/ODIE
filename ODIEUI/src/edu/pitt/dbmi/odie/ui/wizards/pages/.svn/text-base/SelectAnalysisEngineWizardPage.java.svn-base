package edu.pitt.dbmi.odie.ui.wizards.pages;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ListViewer;
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

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.dbmi.odie.model.AnalysisEngineMetadata;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.dbmi.odie.ui.wizards.ImportAnalysisEnginesWizard;
import edu.pitt.dbmi.odie.ui.wizards.providers.AnalysisEngineMetadataContentProvider;
import edu.pitt.dbmi.odie.ui.wizards.providers.AnalysisEngineMetadataLabelProvider;
import edu.pitt.dbmi.odie.ui.workers.AnalysisEngineLoader;

public class SelectAnalysisEngineWizardPage extends WizardPage {

	private static final String DEFAULT_DETAILS_MSG = "Select an analysis engine above to see its details";
	public static final String PAGE_NAME = "SelectAnalysisEngineWizardPage";
	private static final String NAME_HEADER = "Name: ";
	private static final String TYPE_HEADER = "Type: ";
	protected static final String DESC_HEADER = "Description:";
	protected static final String AE_LOAD_ERROR = "Error loading analysis engine";

	private static HashMap<String, String> titleMap = new HashMap<String, String>();

	static {
		titleMap.put(ODIEConstants.AE_TYPE_NER,
				"Named Entity Recognizer Engines");
		titleMap.put(ODIEConstants.AE_TYPE_OE, "Ontology Enrichment Engines");
		titleMap.put(ODIEConstants.AE_TYPE_OTHER, "UIMA Analysis Engines");
	}
	private StyledText detailsTextBox;
	private ListViewer aeList;
	private Button importButton;
	private Button removeButton;

	public SelectAnalysisEngineWizardPage() {
		super("selectAnalysisEngine");
		setTitle("Analysis Engines");
		setDescription("Select an analysis engine");
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout gl = new GridLayout();
		gl.verticalSpacing = Aesthetics.INTERGROUP_SPACING;
		gl.numColumns = 1;
		container.setLayout(gl);

		// AE List Group
		Group aelistGrp = new Group(container, SWT.NONE);
		aelistGrp.setText("Analysis Engines");
		gl = new GridLayout();
		gl.verticalSpacing = Aesthetics.INTRAGROUP_SPACING;
		aelistGrp.setLayout(gl);

		GridData gd = new GridData();
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = true;
		gd.minimumHeight = 100;
		aelistGrp.setLayoutData(gd);

		gl = new GridLayout();
		gl.numColumns = 2;
		aelistGrp.setLayout(gl);

		// IOntology list

		ScrolledComposite scroll = new ScrolledComposite(aelistGrp,
				SWT.H_SCROLL | SWT.V_SCROLL);
		aeList = new ListViewer(scroll);

		aeList.setContentProvider(new AnalysisEngineMetadataContentProvider());
		aeList.setLabelProvider(new AnalysisEngineMetadataLabelProvider());

		aeList.setInput("");
		aeList.getList().select(0);
		gd = new GridData();
		gd.verticalSpan = 2;
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = true;
		scroll.setLayoutData(gd);
		scroll.setContent(aeList.getControl());
		scroll.setMinSize(100, 100);
		scroll.setExpandHorizontal(true);
		scroll.setExpandVertical(true);

		// add/remove buttons
		gd = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);
		importButton = new Button(aelistGrp, SWT.PUSH);
		importButton.setText("Import...");
		importButton.setLayoutData(gd);

		gd = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);
		removeButton = new Button(aelistGrp, SWT.PUSH);
		removeButton.setText("Remove");
		removeButton.setLayoutData(gd);
		removeButton.setEnabled(false);

		// /////// Details group
		Label detailsGrp = new Label(aelistGrp, SWT.NONE);
		detailsGrp.setText("Description:");

		detailsTextBox = new StyledText(aelistGrp, SWT.WRAP | SWT.READ_ONLY
				| SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		gd = new GridData();
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalAlignment = GridData.FILL;
		gd.horizontalSpan = 2;
		gd.minimumHeight = 50;
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = true;
		detailsTextBox.setLayoutData(gd);
		detailsTextBox.setText(DEFAULT_DETAILS_MSG);

		addListeners();

		setControl(container);

		setPageComplete(false);
	}

	private void addListeners() {
		importButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				launchWizard();

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				launchWizard();

			}

			private void launchWizard() {
				ImportAnalysisEnginesWizard wizard = new ImportAnalysisEnginesWizard();
				// Instantiates the wizard container with the wizard and opens
				// it
				WizardDialog dialog = new WizardDialog(getShell(), wizard);
				dialog.create();
				dialog.open();
			}

		});

		removeButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				doAction();

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				doAction();

			}

			private void doAction() {
				Object o = ((StructuredSelection) aeList.getSelection()).getFirstElement();
				if(o==null)
					return;
				
				AnalysisEngineMetadata aem = (AnalysisEngineMetadata)o;
				try {
					Activator.getDefault().getMiddleTier().delete(aem);
					aeList.refresh();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

		aeList.addPostSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Object firstSelection = ((StructuredSelection) event
						.getSelection()).getFirstElement();

				if (firstSelection instanceof AnalysisEngineMetadata) {
					removeButton.setEnabled(true);
					updateDetailsBox((AnalysisEngineMetadata) firstSelection);
					setPageComplete(true);
				}
			}

			private void updateDetailsBox(AnalysisEngineMetadata aem) {
				StringBuffer text = buildDescriptionFrom(aem);

				detailsTextBox.setText(text.toString());

				applyStylesToText();
			}

			private StringBuffer buildDescriptionFrom(AnalysisEngineMetadata aem) {
				StringBuffer text = new StringBuffer();

				// text.append(NAME_HEADER + aem.getName());
				// text.append("\n");
				// text.append(TYPE_HEADER + aem.getType());
				// text.append("\n");

				text.append(aem.getDescription());
				text.append("\n");

				return text;
			}

			private void applyStylesToText() {
				String s = detailsTextBox.getText();

				int start = s.indexOf(NAME_HEADER);
				StyleRange sr;
				if (start >= 0) {
					sr = new StyleRange();
					sr.start = start;
					sr.length = NAME_HEADER.length();
					sr.fontStyle = SWT.BOLD;
					detailsTextBox.setStyleRange(sr);
				}

				start = s.indexOf(TYPE_HEADER);
				if (start >= 0) {
					sr = new StyleRange();
					sr.start = start;
					sr.length = TYPE_HEADER.length();
					sr.fontStyle = SWT.BOLD;
					detailsTextBox.setStyleRange(sr);
				}

				start = s.indexOf(DESC_HEADER);
				if (start >= 0) {
					sr = new StyleRange();
					sr.start = start;
					sr.length = DESC_HEADER.length();
					sr.fontStyle = SWT.BOLD;
					detailsTextBox.setStyleRange(sr);
				}

			}
		});

	}

	protected AnalysisEngine loadAnalysisEngine(AnalysisEngineMetadata aem) {
		try {
			AnalysisEngineLoader aeloader = new AnalysisEngineLoader(aem
					.getURL());
			this.getWizard().getContainer().run(true, true, aeloader);
			return aeloader.getAnalysisEngine();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return null;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public AnalysisEngineMetadata getAnalysisEngineMetadata() {
		Object firstSelection = ((StructuredSelection) aeList.getSelection())
				.getFirstElement();

		if (firstSelection instanceof AnalysisEngineMetadata) {
			return (AnalysisEngineMetadata) firstSelection;
		}
		return null;
	}

	public void setAnalysisEngineType(String selectedOption) {
		aeList.setInput(selectedOption);
		setTitle(titleMap.get(selectedOption));
		detailsTextBox.setText(DEFAULT_DETAILS_MSG);
	}

	Logger logger = Logger.getLogger(this.getClass());

}
