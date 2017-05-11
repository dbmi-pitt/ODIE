package edu.pitt.dbmi.odie.ui.editors;

import org.apache.uima.analysis_engine.metadata.AnalysisEngineMetaData;
import org.apache.uima.analysis_engine.metadata.impl.AnalysisEngineMetaData_impl;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

public class AnalysisEngineDelegatesMDB extends MasterDetailsBlock {
	private FormPage page;
	public AnalysisEngineDelegatesMDB(
			FormPage formPage) {
		this.page = formPage;
	}

	@Override
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		managedForm.addPart(new DelegateAnalysisEngineSection(page.getEditor(),parent,Section.DESCRIPTION|Section.TITLE_BAR));
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.registerPage(AnalysisEngineMetaData.class,new AnalysisEngineConfigParamsPage());

	}

}
