package edu.pitt.dbmi.odie.ui.wizards.pages;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.metadata.AnalysisEngineMetaData;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.resource.ResourceInitializationException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PatternFilter;

import edu.pitt.dbmi.odie.model.AnalysisEngineMetadata;
import edu.pitt.dbmi.odie.ui.editors.analysis.TypeSystemTree;
import edu.pitt.dbmi.odie.ui.wizards.ShowChildrenPatternFilter;
import edu.pitt.dbmi.odie.ui.workers.AnalysisEngineLoader;
import edu.pitt.dbmi.odie.uima.utils.UIMAUtils;

public class SelectAEDescriptorPage extends FileSystemBrowsePage {

	private static final String NAME_HEADER = "Name: ";
	private static final String DESC_HEADER = "Description: ";
	private static final String SRC_URL_HEADER = "Source URL:";
	private static final String VENDOR_HEADER = "Vendor:";
	private static final String VERSION_HEADER = "Version:";

	public static final String PAGE_NAME = "SelectAEDescriptorPage";
	private static final String SourceLocationEmpty = "Select the Analysis Engine XML Descriptor";
	private static final String InvalidSourceLocation = "File does not exist.";

	public static final String DESC_NAME = "UIMA AE Descriptor(*.xml)";

	public static final String DESC_EXTENSION = "*.xml";
	private static final String AnalysisEngineLoadError = "Failed to load Analysis Engine";
	private static final String Success = "Analysis Engine Loaded";

	private AnalysisEngine analysisEngine;
	private StyledText detailsTextBox;
	private TypeSystemTree typeSystemTreeViewer;
	private TypeSystem typeSystem;
	private String aeFilePath;
	private AnalysisEngineLoader loader;

	public SelectAEDescriptorPage() {
		super(PAGE_NAME);
		setTitle("Select Analysis Engine Descriptor File");
		setDescription(SourceLocationEmpty);
	}

	public SelectAEDescriptorPage(String aeFilePath) {
		this();
		this.aeFilePath = aeFilePath;
	}

	protected IPath openBrowseDialog(IPath sourceLocation) {
		FileDialog dialog = new FileDialog(getShell());
		dialog.setFilterNames(new String[] { DESC_NAME });
		dialog.setFilterExtensions(new String[] { DESC_EXTENSION });

		if (sourceLocation != null)
			dialog.setFilterPath(sourceLocation.toOSString());

		String result = dialog.open();

		if (result == null)
			return null;

		return new Path(result);
	}

	protected boolean validatePage() {
		if (locationTextField == null)
			return false;
		if (locationTextField.getText().trim().length() == 0) {
			setErrorMessage(null);
			setMessage(SelectAEDescriptorPage.SourceLocationEmpty);
			return false;
		} else if (!isValidPath()) {
			setErrorMessage(SelectAEDescriptorPage.InvalidSourceLocation);
			return false;

		} else {
			boolean status;

			try {
				status = loadAE();
				updateDetailsBox();
				updateTypeSystemTree();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				setErrorMessage(e.getMessage());
				return false;
			} catch (InterruptedException e) {
				e.printStackTrace();
				// setMessage(SelectAEDescriptorPage.LoadingInterrupted,
				// DialogPage.INFORMATION);
				return false;
			}

			if (!status) {
				setErrorMessage(SelectAEDescriptorPage.AnalysisEngineLoadError);
			} else {
				setErrorMessage(null);
				setMessage(SelectAEDescriptorPage.Success,
						DialogPage.INFORMATION);
			}

			return status;
		}
	}

	public TypeSystem getTypeSystem() {
		return typeSystem;
	}

	private void updateTypeSystemTree() {
		if (analysisEngine == null)
			return;

		try {
			typeSystem = UIMAUtils.createCAS(
					analysisEngine.getAnalysisEngineMetaData().getTypeSystem())
					.getTypeSystem();
			typeSystemTreeViewer.getViewer().setInput(typeSystem);
		} catch (CASRuntimeException e) {
			e.printStackTrace();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
	}

	private void updateDetailsBox() {
		if (analysisEngine == null) {
			detailsTextBox.setText(AnalysisEngineLoadError);
			return;
		}

		AnalysisEngineMetaData aem = analysisEngine.getAnalysisEngineMetaData();

		StringBuffer text = new StringBuffer();

		text.append(NAME_HEADER + aem.getName());
		text.append("\n");
		text.append(DESC_HEADER + aem.getDescription());
		text.append("\n");
		text.append(SRC_URL_HEADER + aem.getSourceUrlString());
		text.append("\n");
		text.append(VENDOR_HEADER + aem.getVendor());
		text.append("\n");
		text.append(VERSION_HEADER + aem.getVersion());

		detailsTextBox.setText(text.toString());

		applyStylesToText();
	}

	private boolean loadAE() throws InvocationTargetException,
			InterruptedException {
		loader = new AnalysisEngineLoader(getSelectedFileURI());
		this.getWizard().getContainer().run(true, true, loader);
		this.analysisEngine = loader.getAnalysisEngine();

		return analysisEngine != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pitt.dbmi.odie.ui.wizards.FileSystemBrowsePage#getLabelText()
	 */
	@Override
	protected String getLabelText() {
		return "From file:";
	}

	/**
	 * @return
	 */
	public URL getSelectedFileURI() {
		try {
			return getLocation().toFile().toURI().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public AnalysisEngine getAnalysisEngine() {
		return analysisEngine;
	}

	@Override
	void createAdditionalControls(Composite container) {
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		container.setLayout(gridLayout);

		Label detailsGrp = new Label(container, SWT.NONE);
		detailsGrp.setText("Description:");

		detailsTextBox = new StyledText(container, SWT.WRAP | SWT.READ_ONLY
				| SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		GridData gd = new GridData();
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalAlignment = GridData.FILL;
		gd.minimumHeight = 75;
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = true;
		detailsTextBox.setLayoutData(gd);
		detailsTextBox.setText("No analysis engine descriptor selected.");

		Label typeSystemGrp = new Label(container, SWT.NONE);
		typeSystemGrp.setText("Type System:");

		// For Tree
		gd = new GridData();
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalAlignment = GridData.FILL;
		gd.minimumHeight = 150;
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = true;

		createTree(container);
		typeSystemTreeViewer.getViewer().getTree().setLayoutData(gd);

		if (aeFilePath != null)
			this.locationTextField.setText(aeFilePath);
	}

	private void createTree(Composite container) {
		final PatternFilter patternFilter = new ShowChildrenPatternFilter();
		typeSystemTreeViewer = new TypeSystemTree(container, patternFilter);
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

		start = s.indexOf(DESC_HEADER);
		if (start >= 0) {
			sr = new StyleRange();
			sr.start = start;
			sr.length = DESC_HEADER.length();
			sr.fontStyle = SWT.BOLD;
			detailsTextBox.setStyleRange(sr);
		}

		start = s.indexOf(VENDOR_HEADER);
		if (start >= 0) {
			sr = new StyleRange();
			sr.start = start;
			sr.length = VENDOR_HEADER.length();
			sr.fontStyle = SWT.BOLD;
			detailsTextBox.setStyleRange(sr);
		}

		start = s.indexOf(VERSION_HEADER);
		if (start >= 0) {
			sr = new StyleRange();
			sr.start = start;
			sr.length = VERSION_HEADER.length();
			sr.fontStyle = SWT.BOLD;
			detailsTextBox.setStyleRange(sr);
		}

		start = s.indexOf(SRC_URL_HEADER);
		if (start >= 0) {
			sr = new StyleRange();
			sr.start = start;
			sr.length = SRC_URL_HEADER.length();
			sr.fontStyle = SWT.BOLD;
			detailsTextBox.setStyleRange(sr);
		}

	}

	public AnalysisEngineMetadata getAnalysisEngineMetadata() {
		return loader.getAnalysisEngineMetadata();
	}

}
