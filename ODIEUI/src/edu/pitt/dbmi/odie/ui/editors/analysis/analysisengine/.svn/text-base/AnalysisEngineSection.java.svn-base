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

package edu.pitt.dbmi.odie.ui.editors.analysis.analysisengine;

import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import edu.pitt.dbmi.odie.model.Analysis;
import edu.pitt.dbmi.odie.ui.editors.analysis.AnalysisEditorInput;
import edu.pitt.dbmi.odie.uima.utils.UIMAUtils;
import edu.pitt.dbmi.odie.utils.FormUtils;

public class AnalysisEngineSection extends SectionPart {

	protected FormEditor editor;

	Logger logger = Logger.getLogger(this.getClass());

	private AnalysisEngineMetadataTable aemt;

	public AnalysisEngineSection(FormEditor editor, Composite parent, int style) {
		super(parent, editor.getToolkit(), style);
		this.editor = editor;
	}

	@Override
	public void initialize(final IManagedForm form) {
		super.initialize(form);

		FormToolkit toolkit = form.getToolkit();
		Section section = getSection();
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.verticalIndent = getVerticalIndent();
		section.setLayoutData(gd);
		section.addExpansionListener(new ExpansionAdapter() {

			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
			}
		});
		section.setText("Analysis Engine");
		section
				.setDescription("Information about the UIMA analysis engine for this analysis");

		Composite sectionClient = FormUtils.newNColumnComposite(toolkit,
				section, 1, true, true);
		FormUtils.setMargins(sectionClient, 5, 0);

		gd = new GridData(GridData.FILL_BOTH);
		sectionClient.setLayoutData(gd);
		
		aemt = new AnalysisEngineMetadataTable(sectionClient);

		gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		aemt.getTable().setLayoutData(gd);
		
		Analysis a = ((AnalysisEditorInput) editor.getEditorInput())
		.getAnalysis();
		
		if(a.getAnalysisEngine() == null){
			section.setDescription("Loading engine metadata...");
			AnalysisEngine ae;
			try {
				ae = UIMAUtils.loadAnalysisEngine(a
						.getAnalysisEngineMetadata().getURL());
				a.getAnalysisEngineMetadata().setAnalysisEngine(ae);
			} catch (Exception e1) {
				e1.printStackTrace();
				section.setDescription("Error loading analysis engine:" + e1.getMessage());
				return;
			}
		}
		
		section.setText(a.getAnalysisEngineMetadata().getName());
		section.setDescription("Information about the analysis engine used.");
		aemt.setInput(a.getAnalysisEngine().getAnalysisEngineMetaData());
		

	}

	protected int getVerticalIndent() {
		return 10;
	}

	protected String getDescriptionTooltip() {
		return "Description for the Analysis Engine";
	}

	protected String getNameTooltip() {
		return "Analysis Engine Name";
	}

	ModifyListener modifyListener = new ModifyListener() {

		@Override
		public void modifyText(ModifyEvent e) {
			AnalysisEngineSection.this.markDirty();
		}

	};

	public FormEditor getEditor() {
		return editor;
	}

	@Override
	public void refresh() {
		Analysis a = ((AnalysisEditorInput) editor.getEditorInput())
				.getAnalysis();
		aemt.setInput(a.getAnalysisEngine().getAnalysisEngineMetaData());
		super.refresh();
	}

	public void callRefreshOnSectionPart() {
		super.refresh();
	}

}
