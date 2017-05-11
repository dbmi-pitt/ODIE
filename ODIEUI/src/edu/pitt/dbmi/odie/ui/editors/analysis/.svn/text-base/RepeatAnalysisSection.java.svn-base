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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.ui.workers.AnalysisReprocessor;
import edu.pitt.dbmi.odie.utils.FormUtils;
import edu.pitt.dbmi.odie.utils.GeneralUtils;

public class RepeatAnalysisSection extends SectionPart {

	protected FormEditor editor;
	private Analysis currentAnalysis;
	private StructuredViewer suggestionsTable;

	public RepeatAnalysisSection(FormEditor editor, Composite parent,
			int style, StructuredViewer viewer) {
		super(parent, editor.getToolkit(), style);
		this.editor = editor;
		this.suggestionsTable = viewer;
		currentAnalysis = ((AnalysisEditorInput) editor.getEditorInput())
				.getAnalysis();
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
		section.setText("Repeat Analysis");
		section.setDescription("Repeat the analysis with new proposals");

		Composite sectionClient = FormUtils.newNColumnComposite(toolkit,
				section, 1, true, true);
		FormUtils.setMargins(sectionClient, 5, 0);

		Button repeatAnalysisBtn = new Button(sectionClient, SWT.PUSH);
		repeatAnalysisBtn.setText("Repeat Analysis");
		repeatAnalysisBtn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				repeatAnalysis();

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				repeatAnalysis();

			}

		});
	}

	protected void repeatAnalysis() {
		ProgressMonitorDialog pd = GeneralUtils.getProgressMonitorDialog();

		try {
			pd.run(true, true, new AnalysisReprocessor(currentAnalysis));

			GeneralUtils.showPerformanceDialog(currentAnalysis);
			GeneralUtils.openEditor(AnalysisEditor.ID,new AnalysisEditorInput(
					currentAnalysis), true);
			GeneralUtils.refreshViews();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
