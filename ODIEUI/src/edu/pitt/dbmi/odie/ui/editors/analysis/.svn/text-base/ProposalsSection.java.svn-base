/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package edu.pitt.dbmi.odie.ui.editors.analysis;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import edu.pitt.dbmi.odie.ODIEConstants;
import edu.pitt.dbmi.odie.middletier.MiddleTier;
import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.model.LanguageResource;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.ui.Aesthetics;
import edu.pitt.dbmi.odie.ui.dnd.ProposalTreeDropAdapter;
import edu.pitt.dbmi.odie.ui.dnd.SuggestionTransfer;
import edu.pitt.dbmi.odie.ui.editors.ontology.OntologyFilteredTree;
import edu.pitt.dbmi.odie.ui.wizards.BioportalNewProposalNoteWizard;
import edu.pitt.dbmi.odie.utils.FormUtils;
import edu.pitt.dbmi.odie.utils.GeneralUtils;
import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IOntology;
import edu.pitt.ontology.IOntologyException;
import edu.pitt.ontology.IProperty;
import edu.pitt.ontology.bioportal.BioPortalRepository;

public class ProposalsSection extends SectionPart {

	protected FormEditor editor;
	OntologyFilteredTree ontologyTree;
	private Button remButton;
	private Button addButton;
	private Button shareButton;
	private Button exportButton;
	
	public ProposalsSection(FormEditor editor, Composite parent, int style) {
		super(parent, editor.getToolkit(), style);
		this.editor = editor;
	}

	@Override
	public void initialize(final IManagedForm form) {
		super.initialize(form);

		FormToolkit toolkit = form.getToolkit();
		Section section = getSection();
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
		section.addExpansionListener(new ExpansionAdapter() {

			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
			}
		});
		section.setText("Proposed New Concepts");
		section.setDescription("Proposals for new concepts.");

		Composite sectionClient = FormUtils.newNColumnComposite(toolkit,
				section, 2, true, true);
		FormUtils.setMargins(sectionClient, 5, 0);

		ontologyTree = new OntologyFilteredTree(sectionClient);
		GridData gd = (GridData) ontologyTree.getLayoutData();
		gd.verticalSpan = 4;

		addButton = new Button(sectionClient, SWT.PUSH);
		addButton.setText("Add...");
		addButton.setToolTipText("Add a new concept to the proposal ontology");
		gd = new GridData();
		gd.verticalIndent = 30;
		gd.verticalAlignment = SWT.TOP;
		gd.horizontalAlignment = SWT.FILL;
		addButton.setLayoutData(gd);

		remButton = new Button(sectionClient, SWT.PUSH);
		remButton.setText("Remove");
		remButton.setToolTipText("Remove the selected concept from the proposal ontology");
		gd = new GridData();
		gd.verticalAlignment = SWT.TOP;
		gd.horizontalAlignment = SWT.FILL;
		remButton.setLayoutData(gd);

		exportButton = new Button(sectionClient, SWT.PUSH);
		exportButton.setText("Export");
		exportButton.setToolTipText("Export new concepts to a Excel compatible CSV file");
		gd = new GridData();
		gd.verticalAlignment = SWT.TOP;
		gd.horizontalAlignment = SWT.FILL;
		exportButton.setLayoutData(gd);

		shareButton = new Button(sectionClient, SWT.PUSH);
		shareButton.setText("Share");
		shareButton.setToolTipText("Contribute the selected concept to Bioportal or NeuroLex Wiki");
		gd = new GridData();
		gd.verticalAlignment = SWT.TOP;
		gd.horizontalAlignment = SWT.FILL;
		shareButton.setLayoutData(gd);
		
		hookMyListeners();
		addDropSupport();
	}

	private void hookMyListeners() {
		ontologyTree.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (ontologyTree.getViewer().getSelection().isEmpty()){
					remButton.setEnabled(false);
//					noteButton.setEnabled(false);
				}
				else{
					remButton.setEnabled(true);
					List<IClass> items = ((StructuredSelection) ontologyTree
							.getViewer().getSelection()).toList();
					if(items.size()==1){
//						bioportalButton.setEnabled(true);
						IClass cl = items.get(0);
						
//						noteButton.setEnabled(!GeneralUtils.isProxyClass(cl));
					}
					else{
//						noteButton.setEnabled(false);
					}
						
				}
				
			}
		});
		
		addButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				handleSelection();

			}

			private void handleSelection() {
				GeneralUtils
						.addToProposalOntologyWithDialog(((AnalysisEditorInput) editor
								.getEditorInput()).analysis);
				ontologyTree.refresh();
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				handleSelection();
			}
		});

		remButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				handleSelection();

			}

			private void handleSelection() {
				if (ontologyTree.getViewer().getSelection().isEmpty())
					return;

				List<IClass> items = ((StructuredSelection) ontologyTree
						.getViewer().getSelection()).toList();

				String itemNames = "";
				if (items.size() > 0)
					itemNames = "the selected concepts";
				else
					itemNames = "'" + (items.get(0)).getName() + "'";

				IOntology ontology = items.get(0).getOntology();
				if (MessageDialog.openQuestion(Display.getCurrent()
						.getActiveShell(), "Remove Concept",
						"Are you sure you want to remove " + itemNames)) {
					for (IClass item : items)
						item.delete();

					try {
						ontology.save();
						Analysis analysis = ((AnalysisEditorInput) editor
								.getEditorInput()).getAnalysis();
						GeneralUtils.refreshProposalLexicalSet(analysis);
						ontologyTree.refresh();
					} catch (IOntologyException e) {
						e.printStackTrace();
						GeneralUtils.showErrorDialog("Error", e.getMessage());
					}
				}
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				handleSelection();
			}

		});

		shareButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				final Shell dialog = new Shell(GeneralUtils.getShell(), SWT.APPLICATION_MODAL
				        | SWT.DIALOG_TRIM);
			    dialog.setText("Share");
			    dialog.setLayout(new GridLayout(3,true));
			    
			    final Label label = new Label(dialog, SWT.NONE);
			    label.setText("Select the community to contribute to");
			    GridData gd = new GridData();
			    gd.horizontalSpan = 3;
			    gd.horizontalAlignment = SWT.LEFT;
			    label.setLayoutData(gd);
			    
			    Button bioportalButton = new Button(dialog, SWT.PUSH);
			    bioportalButton.setText("Bioportal");
			    gd = new GridData();
			    gd.horizontalAlignment = SWT.CENTER;
			    bioportalButton.setLayoutData(gd);
			    
			    Button neuroLexButton = new Button(dialog, SWT.PUSH);
			    neuroLexButton.setText("NeuroLex Wiki");
			    gd = new GridData();
			    gd.horizontalAlignment = SWT.CENTER;
			    neuroLexButton.setLayoutData(gd);
			    
			    Button buttonCancel = new Button(dialog, SWT.PUSH);
			    buttonCancel.setText("Cancel");
			    gd = new GridData();
			    gd.horizontalAlignment = SWT.CENTER;
			    buttonCancel.setLayoutData(gd);

			    dialog.pack();
			    
			    Aesthetics.centerDialog(dialog);
			    
			    neuroLexButton.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						if(MessageDialog.openQuestion(GeneralUtils.getShell(), "Share on NeuroLex Wiki",
								"Do you want to create a new wiki page for the concept in the NeuroLex Wiki?")){
							
							List<IClass> items = ((StructuredSelection) ontologyTree
									.getViewer().getSelection()).toList();
							if (items.size() > 0) {
								IClass newc = items.get(0);
								String urlString = ODIEConstants.NEUROLEX_WIKI_CREATE_PAGE_PREFIX + newc.getName();
								try {
									GeneralUtils.openURL(new URL(urlString));
								} catch (MalformedURLException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
							dialog.close();
						}
						
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						
					}
				});
			    
			    buttonCancel.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						dialog.close();
						
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						
					}
				});
			    
			    bioportalButton.addSelectionListener(new SelectionListener() {

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						handleSelection();
						dialog.close();

					}

					private void handleSelection() {
						if (ontologyTree.getViewer().getSelection().isEmpty()){
							GeneralUtils.showErrorDialog("No Concept Selected", "Please select a new proposal concept to be suggested in the Bioportal Note");
							return;
						}
						

						List<IClass> items = ((StructuredSelection) ontologyTree
								.getViewer().getSelection()).toList();
						if (items.size() > 0) {
							IClass newc = items.get(0);
							String bpURIStr = (String)newc.getPropertyValue(newc.getOntology().getProperty(IProperty.RDFS_IS_DEFINED_BY));
							if(bpURIStr!=null){
								GeneralUtils.showErrorDialog("Concept already in Bioportal", 
										newc.getName() + " is already part of an existing Bioportal " +
												"ontology. Please select a newly created concept to be submitted as a Bioportal note");
								return;
							}
							IProperty p = newc.getOntology().getProperty(ODIEConstants.BIOPORTAL_NOTE_ID_PROPERTY);
							IProperty op = newc.getOntology().getProperty(ODIEConstants.BIOPORTAL_ONTOLOGY_ID_PROPERTY);
							if(p!=null){
								String noteid = (String) newc.getPropertyValue(p);
								String ontologyid = (String) newc.getPropertyValue(op);
								if(noteid!=null && ontologyid!=null){
									MessageDialog dialog = new MessageDialog(Display.getCurrent().getActiveShell(), 
											"Bioportal note already exists", null, 
											"A bioportal note was previously created for this concept.", 
											MessageDialog.INFORMATION, 
											new String[] { "View in Bioportal",IDialogConstants.OK_LABEL }, 0);
									int index = dialog.open();
									if(index == 0){
										String url = BioPortalRepository.DEFAULT_BIOPORTAL_URL + ontologyid + "/?noteid=" + noteid;
										try {
											GeneralUtils.openURL(new URL(url));
										} catch (MalformedURLException e) {
											e.printStackTrace();
										}
									}
									return;
								}
							}
								
							Analysis analysis = ((AnalysisEditorInput) editor
									.getEditorInput()).getAnalysis();

							MiddleTier mt = Activator.getDefault().getMiddleTier();
							
							BioportalNewProposalNoteWizard wizard = new BioportalNewProposalNoteWizard(analysis,newc);
							
							WizardDialog dialog = new WizardDialog(Display.getCurrent()
									.getActiveShell(), wizard);
							
							
							dialog.open();
						}
					}

					@Override
					public void widgetSelected(SelectionEvent e) {
						handleSelection();
						dialog.close();
					}

				});
			    
			    dialog.open();			
			}
			
		});
		
		
		
		exportButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				handleSelection();

			}

			private void handleSelection() {
				GeneralUtils.exportOntologyToCSVWithDialog((IOntology)ontologyTree.getViewer().getInput());
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				handleSelection();
			}
		});
	}

	private void addDropSupport() {
		int ops = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transfers = new Transfer[] { SuggestionTransfer
				.getInstance() };

		// JFace DND Does not work
		// ontologyTree.getViewer().addDropSupport(ops, transfers, new
		// ProposalTreeDropAdapter(ontologyTree.getViewer()));

		// SWT DND
		DropTarget target = new DropTarget(ontologyTree, ops);
		target.setTransfer(transfers);
		target.addDropListener(new ProposalTreeDropAdapter(ontologyTree
				.getViewer()));
	}

	Logger logger = Logger.getLogger(ProposalsSection.class);

	@Override
	public void refresh() {
		logger.debug("start refresh");
		super.refresh();
		Analysis a = ((AnalysisEditorInput) editor.getEditorInput())
				.getAnalysis();
		LanguageResource lr = a.getProposalLexicalSet()
				.getLexicalSetLanguageResources().get(0).getLanguageResource();

		ontologyTree.setInput((IOntology) Activator.getDefault()
				.getMiddleTier().getResourceForURI(lr.getURI()));
		logger.debug("end refresh");
	}

	public ISelectionProvider getSelectionProvider() {
		return ontologyTree.getViewer();
	}
}
