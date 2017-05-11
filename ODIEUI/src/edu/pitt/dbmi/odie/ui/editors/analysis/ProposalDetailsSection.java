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

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.ui.Activator;
import edu.pitt.dbmi.odie.utils.FormUtils;
import edu.pitt.dbmi.odie.utils.GeneralUtils;
import edu.pitt.ontology.IClass;
import edu.pitt.ontology.IOntologyException;

public class ProposalDetailsSection extends SectionPart {

	protected FormEditor editor;

	Logger logger = Logger.getLogger(this.getClass());

	public ProposalDetailsSection(FormEditor editor, Composite parent, int style) {
		super(parent, editor.getToolkit(), style);
		this.editor = editor;
	}

	@Override
	public void initialize(final IManagedForm form) {
		super.initialize(form);

		FormToolkit toolkit = form.getToolkit();
		Section section = getSection();
		GridData gd = new GridData(GridData.FILL_BOTH);
		section.setLayoutData(gd);
		section.addExpansionListener(new ExpansionAdapter() {

			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
			}
		});
		section.setText("New Concept Details");
		section.setDescription("Information about the selected concept");

		Composite sectionClient = FormUtils.newNColumnComposite(toolkit,
				section, 1, true, false);
		FormUtils.setMargins(sectionClient, 5, 0);

		Analysis analysis = ((AnalysisEditorInput) editor.getEditorInput())
				.getAnalysis();

		proposalDetailsPanel = new ProposalDetailsPanel(sectionClient,
				GeneralUtils.getOntologiesInUse(analysis), true);

		hookMyListeners();
	}

	private void hookMyListeners() {
		proposalDetailsPanel.addDetailsChangedListener(new Listener() {

			@Override
			public void handleEvent(Event event) {
				isDirty = true;
				saveChanges();
			}

		});

		//for some reason addSelectionListener(AnalysisEditor.ID,new ISelectionListener
		//does not work, so for now registering it as selection listener for entire page
		//and only handling selection events from AnalysisEditor by checking part type
		getEditor().getSite().getPage().addSelectionListener(new ISelectionListener() {
					@Override
					public void selectionChanged(IWorkbenchPart part,
							ISelection selection) {

						if(!(part instanceof AnalysisEditor)){
							return;
						}
						
						if (selection.isEmpty()) {
							clear();
						}
						if (!(selection instanceof StructuredSelection))
							return;

						Object element = ((StructuredSelection) selection)
								.getFirstElement();

						if (element instanceof IClass) {
							if (isDirty)
								saveChanges();

							if(!GeneralUtils.isProxyClass((IClass) element)){
								loadClass((IClass) element);
							}
							else
								clear();
						}
					}
				});
	}

	protected void clear() {
		isDirty = false;
		currentClass = null;
		proposalDetailsPanel.clear();

	}

	private boolean isDirty;
	private IClass currentClass;

	private ProposalDetailsPanel proposalDetailsPanel;

	protected void loadClass(IClass proposal) {
		this.currentClass = proposal;
		proposalDetailsPanel.loadClass(currentClass);

	}

	private void saveChanges() {
		if (isDirty) {
			for (String label : currentClass.getLabels())
				currentClass.removeLabel(label);
			for (String label : proposalDetailsPanel.getSynonyms())
				currentClass.addLabel(label);

			Activator.getDefault().getMiddleTier().addParent(currentClass,
					proposalDetailsPanel.getParentClass());

			try {
				currentClass.getOntology().save();
				Analysis analysis = ((AnalysisEditorInput) editor
						.getEditorInput()).getAnalysis();
				GeneralUtils.refreshProposalLexicalSet(analysis);
				isDirty = false;

				((EnrichOntologiesPage) editor.getSelectedPage())
						.getProposalsSection().refresh();
			} catch (IOntologyException e) {
				e.printStackTrace();
				GeneralUtils.showErrorDialog("Error Saving Changes", e
						.getMessage());
			}
		}
	}

	protected String getNameTooltip() {
		return "Preferred Name";
	}

	public FormEditor getEditor() {
		return editor;
	}

	@Override
	public void refresh() {

		super.refresh();
	}

	public void callRefreshOnSectionPart() {
		super.refresh();
	}
}
