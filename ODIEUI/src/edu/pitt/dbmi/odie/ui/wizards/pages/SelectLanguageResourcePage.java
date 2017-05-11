/**
 * 
 */
package edu.pitt.dbmi.odie.ui.wizards.pages;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import edu.pitt.dbmi.odie.model.LanguageResource;
import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.dbmi.odie.ui.wizards.providers.LanguageResourceContentProvider;
import edu.pitt.dbmi.odie.ui.wizards.providers.LanguageResourceTreeLabelProvider;
import edu.pitt.ontology.IOntology;

/**
 * @author Girish Chavan
 * 
 */
public class SelectLanguageResourcePage extends WizardPage {
	Logger logger = Logger.getLogger(this.getClass());
	public static final String PAGE_NAME = "SelectInstalledOntologyPage";
	private StyledText detailsTextBox;
	private CheckboxTableViewer ontologyTable;

	private static final String SIZE_HEADER = "Size: ";
	private static final String NAME_HEADER = "Name: ";
	private static final String DESC_HEADER = "Description: ";
	private static final String LANG_HEADER = "Language: ";
	private static final String VER_HEADER = "Version: ";
	private static final String INSTALLED_VER_HEADER = "Installed Version: ";
	private static final String RELEASE_HEADER = "Release Date: ";
	private static final String FORMAT_HEADER = "Format: ";
	private static final String URL_HEADER = "URL: ";

	/**
	 * @param pageName
	 */
	public SelectLanguageResourcePage() {
		super(PAGE_NAME);
		setTitle("Select ontology");
		setDescription("Select an ontology from the list");
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
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout gl = new GridLayout();
		gl.verticalSpacing = Aesthetics.INTERGROUP_SPACING;
		gl.numColumns = 1;
		container.setLayout(gl);

		// Ontology List Group
		Group olistGrp = new Group(container, SWT.NONE);
		olistGrp.setText("Ontologies");
		gl = new GridLayout();
		gl.verticalSpacing = Aesthetics.INTRAGROUP_SPACING;
		olistGrp.setLayout(gl);

		GridData gd = new GridData();
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = true;
		gd.minimumHeight = 100;
		olistGrp.setLayoutData(gd);

		// ontology list

		ScrolledComposite scroll = new ScrolledComposite(olistGrp, SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER);
		ontologyTable = CheckboxTableViewer.newCheckList(scroll, SWT.NULL);
		ontologyTable.setContentProvider(new LanguageResourceContentProvider());
		ontologyTable.setLabelProvider(new LanguageResourceTreeLabelProvider());
		ontologyTable.setInput("root");
		
		gd = new GridData();
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = true;
		scroll.setLayoutData(gd);
		scroll.setContent(ontologyTable.getControl());
		scroll.setMinSize(100, 100);
		scroll.setExpandHorizontal(true);
		scroll.setExpandVertical(true);

		// /////// Details group
		Label detailsGrp = new Label(olistGrp, SWT.NONE);
		detailsGrp.setText("Additional Info:");

		detailsTextBox = new StyledText(olistGrp, SWT.WRAP | SWT.READ_ONLY
				| SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		detailsTextBox.setText("Select an ontology above to see its details");

		gd = new GridData();
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalAlignment = GridData.FILL;
		gd.minimumHeight = 100;
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = true;
		detailsTextBox.setLayoutData(gd);

		hookListeners();
		setControl(container);

		setPageComplete(validatePage());

	}

	/**
	 * 
	 */
	private void hookListeners() {

		ontologyTable.getTable().addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if (e.item.getData() instanceof IOntology) {
					updateDetailsBox((IOntology) e.item.getData());
				}

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.detail == SWT.CHECK)
					return;
				if (e.item.getData() instanceof IOntology) {
					IOntology o = (IOntology) e.item.getData();
					updateDetailsBox(o);
				}
			}

			private void updateDetailsBox(IOntology ontology) {
				Map<String, String> map = getOntologyMetadata(ontology);
				if (map == null) {
					detailsTextBox
							.setText("Error getting description for this ontology.");
					return;
				}
				StringBuffer text = new StringBuffer();

				text.append(NAME_HEADER + "\n" + map.get(NAME_HEADER));
				text.append("\n\n");
				text.append(VER_HEADER + map.get(VER_HEADER));
				
				if(map.get(INSTALLED_VER_HEADER)!=null){
					text.append("\t" + INSTALLED_VER_HEADER + map.get(INSTALLED_VER_HEADER));
				}
				text.append("\n\n");
				text.append(DESC_HEADER + "\n" + map.get(DESC_HEADER));
				text.append("\n\n");

				detailsTextBox.setText(text.toString());

				applyStyles();
			}

			private void applyStyles() {
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

				start = s.indexOf(LANG_HEADER);
				if (start >= 0) {
					sr = new StyleRange();
					sr.start = start;
					sr.length = LANG_HEADER.length();
					sr.fontStyle = SWT.BOLD;
					detailsTextBox.setStyleRange(sr);
				}

				start = s.indexOf(VER_HEADER);
				if (start >= 0) {
					sr = new StyleRange();
					sr.start = start;
					sr.length = VER_HEADER.length();
					sr.fontStyle = SWT.BOLD;
					detailsTextBox.setStyleRange(sr);
				}

				start = s.indexOf(INSTALLED_VER_HEADER);
				if (start >= 0) {
					sr = new StyleRange();
					sr.start = start;
					sr.length = INSTALLED_VER_HEADER.length();
					sr.fontStyle = SWT.BOLD;
					detailsTextBox.setStyleRange(sr);
				}
				
				start = s.indexOf(RELEASE_HEADER);
				if (start >= 0) {
					sr = new StyleRange();
					sr.start = start;
					sr.length = RELEASE_HEADER.length();
					sr.fontStyle = SWT.BOLD;
					detailsTextBox.setStyleRange(sr);
				}

				start = s.indexOf(FORMAT_HEADER);
				if (start >= 0) {
					sr = new StyleRange();
					sr.start = start;
					sr.length = FORMAT_HEADER.length();
					sr.fontStyle = SWT.BOLD;
					detailsTextBox.setStyleRange(sr);
				}

				start = s.indexOf(URL_HEADER);
				if (start >= 0) {
					sr = new StyleRange();
					sr.start = start;
					sr.length = URL_HEADER.length();
					sr.fontStyle = SWT.BOLD;
					detailsTextBox.setStyleRange(sr);
				}

				start = s.indexOf(SIZE_HEADER);
				if (start >= 0) {
					sr = new StyleRange();
					sr.start = start;
					sr.length = SIZE_HEADER.length();
					sr.fontStyle = SWT.BOLD;
					detailsTextBox.setStyleRange(sr);
				}
			}
		});

		ontologyTable.addCheckStateListener(new ICheckStateListener() {

			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				setPageComplete(validatePage());
			}

		});

	}

	public LanguageResource[] getLanguageResources() {
		Object[] sel = ontologyTable.getCheckedElements();
		return Arrays.copyOf(sel, sel.length, LanguageResource[].class);
	}

	/**
	 * @return
	 */
	protected boolean validatePage() {
		if (ontologyTable.getCheckedElements().length <= 0) {
			setMessage("Select at least one ontology to import.");
			return false;
		} else {
			setMessage(ontologyTable.getCheckedElements().length
					+ " ontologies selected");
			return true;
		}

	}


	/**
	 * get selected ontology
	 * 
	 * @return null if none selected
	 */
	public Map<String, String> getOntologyMetadata(IOntology ont) {
		if (ont != null) {
			try {
				// setup meta map
				Map<String, String> map = new HashMap<String, String>();

				map.put(NAME_HEADER, ont.getName());
				map.put(DESC_HEADER, ont.getDescription());
				map.put(VER_HEADER, ont.getVersion());
				
				return map;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}
}
