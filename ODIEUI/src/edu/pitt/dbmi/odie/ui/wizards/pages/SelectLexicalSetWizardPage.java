package edu.pitt.dbmi.odie.ui.wizards.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
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

import edu.pitt.dbmi.odie.lexicalizer.lucenefinder.LexicalSetBuilder;
import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.LanguageResource;
import edu.pitt.dbmi.odie.model.LexicalSet;
import edu.pitt.dbmi.odie.model.LexicalSetLanguageResource;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.dbmi.odie.ui.wizards.NewLexicalSetWizard;
import edu.pitt.dbmi.odie.ui.wizards.providers.LexicalSetContentProvider;
import edu.pitt.dbmi.odie.ui.wizards.providers.LexicalSetLabelProvider;
import edu.pitt.dbmi.odie.utils.GeneralUtils;
import edu.pitt.ontology.IClass;

public class SelectLexicalSetWizardPage extends WizardPage {

	private static final String DEFAULT_DETAILS_MSG = "Select from above to see more information";
	public static final String PAGE_NAME = "SelectLexicalSetWizardPage";
	private static final String NAME_HEADER = "Name: ";
	private static final String LOCATION_HEADER = "Lucene Directory: ";
	private static final String DESC_HEADER = "Description: ";
	private static final String ANALYSIS_HEADER = "Analyses using this set: ";
	protected static final String ONT_HEADER = "Ontologies in this set:";

	private StyledText detailsTextBox;
	private ListViewer lexicalSetList;
	private Button newButton;
	private Button removeButton;

	public SelectLexicalSetWizardPage() {
		super("selectLexicalSet");
		setTitle("Select Vocabulary");
		setDescription("Select a vocabulary to use in the analysis");
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout gl = new GridLayout();
		gl.verticalSpacing = Aesthetics.INTERGROUP_SPACING;
		gl.numColumns = 1;
		container.setLayout(gl);

		// AE List Group
		Group aelistGrp = new Group(container, SWT.NONE);
		aelistGrp.setText("Vocabularys");
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
		lexicalSetList = new ListViewer(scroll);

		lexicalSetList.setContentProvider(new LexicalSetContentProvider());
		lexicalSetList.setLabelProvider(new LexicalSetLabelProvider());
		lexicalSetList.setInput("root"); // pass a non-null that will be ignored
		gd = new GridData();
		gd.verticalSpan = 2;
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = true;
		scroll.setLayoutData(gd);
		scroll.setContent(lexicalSetList.getControl());
		scroll.setMinSize(100, 100);
		scroll.setExpandHorizontal(true);
		scroll.setExpandVertical(true);

		// add/remove buttons
		gd = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);
		newButton = new Button(aelistGrp, SWT.PUSH);
		newButton.setText("New...");
		newButton.setLayoutData(gd);

		gd = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);
		removeButton = new Button(aelistGrp, SWT.PUSH);
		removeButton.setText("Remove");
		removeButton.setLayoutData(gd);
		removeButton.setEnabled(false);

		// /////// Details group
		Label detailsGrp = new Label(aelistGrp, SWT.NONE);
		detailsGrp.setText("Information:");

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
		newButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				launchWizard();

			}

			private void launchWizard() {
				NewLexicalSetWizard wizard = new NewLexicalSetWizard();
				// Instantiates the wizard container with the wizard and opens
				// it
				WizardDialog dialog = new WizardDialog(getShell(), wizard);
				dialog.setBlockOnOpen(true);
				dialog.create();
				if (dialog.open() == IDialogConstants.OK_ID) {
					lexicalSetList.refresh();
					LexicalSet ls = wizard.getNewLexicalSet();
					MiddleTier mt = Activator.getDefault().getMiddleTier();
					ls = mt.getLexicalSetForName(ls.getName());
					ls.getName();
					lexicalSetList.setSelection(new StructuredSelection(ls));
				}
			}

		});

		removeButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				removeLexicalSet();

			}

			private void removeLexicalSet() {
				Object firstSelection = ((StructuredSelection) lexicalSetList
						.getSelection()).getFirstElement();

				if (firstSelection instanceof LexicalSet) {
					LexicalSet ls = (LexicalSet)firstSelection;
					String names = getAnalysesUsingList(ls);
					
					if(names.length()>0){
						GeneralUtils.showErrorDialog("Cannote remove selected lexical set", 
								"The following analyses are still using " +
								"'" + ls.getName() +"'" + names + "\n " +
										"Please remove the analyses before attempting to remove this lexical set"
								);
					}
					else{
						
						LexicalSetBuilder builder = new LexicalSetBuilder(Activator.getDefault().getMiddleTier());	
						builder.removeLexicalSet((LexicalSet) firstSelection);
						lexicalSetList.refresh();
						setPageComplete(false);
					}
				}
			}
		});

		lexicalSetList.addPostSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Object firstSelection = ((StructuredSelection) event
						.getSelection()).getFirstElement();

				if (firstSelection instanceof LexicalSet) {
					removeButton.setEnabled(true);
					updateDetailsBox((LexicalSet) firstSelection);
					setPageComplete(true);
				}
			}

			private void updateDetailsBox(LexicalSet ls) {

				StringBuffer text = buildDescriptionFrom(ls);
				detailsTextBox.setText(text.toString());
				applyStylesToText();
			}

			private StringBuffer buildDescriptionFrom(LexicalSet ls) {
				StringBuffer text = new StringBuffer();

				text.append(NAME_HEADER + ls.getName());
				text.append("\n");
				text.append(LOCATION_HEADER + ls.getLocation());
				text.append("\n");
//				text.append(DESC_HEADER + ls.getDescription());
//				text.append("\n");
				text.append(ONT_HEADER);
				text.append("\n");
				text.append(getOntologyDetails(ls));
				text.append(ANALYSIS_HEADER);
//				text.append("\n");
				text.append(getAnalysesUsingList(ls));

				return text;
			}

			

			private String getOntologyDetails(LexicalSet ls) {

				MiddleTier mt = Activator.getDefault().getMiddleTier();
				class LRRoots {

					LanguageResource lr;
					List<IClass> roots = new ArrayList<IClass>();
				}
				;

				HashMap<LanguageResource, LRRoots> rootMap = new HashMap<LanguageResource, LRRoots>();

				for (LexicalSetLanguageResource lslr : ls
						.getLexicalSetLanguageResources()) {
					LanguageResource lr = lslr.getLanguageResource();
					LRRoots lrr = rootMap.get(lr);

					if (lrr == null) {
						lrr = new LRRoots();
						lrr.lr = lr;
						rootMap.put(lr, lrr);
					}
					if (lslr.getSubsetParentURI() != null)
						lrr.roots.add((IClass) mt.getResourceForURI(lslr
								.getSubsetParentURI()));
				}

				StringBuffer sb = new StringBuffer();
				int i = 1;
				for (LRRoots lrr : rootMap.values()) {
					sb.append(i + "." + lrr.lr.getName() + "\n");
					for (IClass c : lrr.roots)
						sb.append("\t\t" + c.getName() + "\n");
					i++;
				}
				return sb.toString();
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

				start = s.indexOf(LOCATION_HEADER);
				if (start >= 0) {
					sr = new StyleRange();
					sr.start = start;
					sr.length = LOCATION_HEADER.length();
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

				start = s.indexOf(ONT_HEADER);
				if (start >= 0) {
					sr = new StyleRange();
					sr.start = start;
					sr.length = ONT_HEADER.length();
					sr.fontStyle = SWT.BOLD;
					detailsTextBox.setStyleRange(sr);
				}

				start = s.indexOf(ANALYSIS_HEADER);
				if (start >= 0) {
					sr = new StyleRange();
					sr.start = start;
					sr.length = ANALYSIS_HEADER.length();
					sr.fontStyle = SWT.BOLD;
					detailsTextBox.setStyleRange(sr);
				}

				
			}
		});

	}

	private String getAnalysesUsingList(LexicalSet ls) {
		MiddleTier mt = Activator.getDefault().getMiddleTier();
		List<Analysis> alist = mt.getAnalysesUsingLexicalSet(ls);
		
		String names = "";
		for(int i=0;i<alist.size();i++){
			names += "\n" + (i+1) + ". " + alist.get(i).getName();
		}
		return names;
	}
	
	public LexicalSet getLexicalSet() {
		Object firstSelection = ((StructuredSelection) lexicalSetList.getSelection())
				.getFirstElement();

		if (firstSelection instanceof LexicalSet) {
			return (LexicalSet) firstSelection;
		}
		return null;
	}

}
